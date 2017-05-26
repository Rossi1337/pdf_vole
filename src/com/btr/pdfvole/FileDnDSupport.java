package com.btr.pdfvole;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*****************************************************************************
 * Generic drag and drop support for files.
 * Implements a drop target for files. You can install a file filter to allow
 * only some special files to be dropped. You can install this DnD support onto
 * multiple components to enable file DnD on them. By registering a 
 * FileDropListener you can react on file drops.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class FileDnDSupport extends DropTargetAdapter {
	
	private static DataFlavor URI_LIST = null;
	
	private Map<Component, DropTarget> dropTargets;
	private List<FileDropListener> listeners;
	private List<FileFilter> fileFilter;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public FileDnDSupport() {
		super();
		this.dropTargets = new HashMap<Component, DropTarget>(); 
		this.listeners = new ArrayList<FileDropListener>();
		this.fileFilter = new ArrayList<FileFilter>();
	}

	/*************************************************************************
	 * drop
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 ************************************************************************/
	@Override
	public void drop(DropTargetDropEvent dtde) {
		int action = dtde.getDropAction();
		dtde.acceptDrop(action);
		
		try {
			Transferable trans = dtde.getTransferable();

			List<File> fileList = getFileList(trans);
			if (fileList != null && fileList.size() > 0) {

				// Filter the file list.
				List<File> filteredList = new ArrayList<File>(fileList.size());
				for (File file : fileList) {
					for (FileFilter filter : this.fileFilter) {
						if (filter.accept(file)) {
							filteredList.add(file);
						}
					}
				}
				
				// Notify listener.
				if (filteredList.size() > 0) {
					for (FileDropListener lis : this.listeners) {
						lis.filesDropped(filteredList);
					}
				}
			}
			
			dtde.dropComplete(true);
			
		} catch (IOException e) {
			dtde.dropComplete(false);
		} catch (UnsupportedFlavorException e) {
			dtde.dropComplete(false);
		}
	}

	/************************************************************************
	 * Helper method to extract the file list from an transferable object.
	 * @param trans the Transferable
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 ************************************************************************/
	@SuppressWarnings("unchecked")
	private List<File> getFileList(Transferable trans)
			throws UnsupportedFlavorException, IOException {
		List<File> fileList = null;
		if (trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ) { // Windows
			fileList = (List<File>) trans.getTransferData(DataFlavor.javaFileListFlavor);
		} else 
		if (trans.isDataFlavorSupported(getUriListFlavor()) ) { // GNOME and KDE do not use javaFileListFlavor	
			fileList = textURIListToFileList((String) trans.getTransferData(getUriListFlavor()));
		}
		return fileList;
	}

	/*************************************************************************
	 * Adds a listener that is notified if a file is dropped.
	 * @param listener a FileDropListener.
	 ************************************************************************/
	
	public void addDropListener(FileDropListener listener) {
		this.listeners.add(listener);
	}
	
	/*************************************************************************
	 * Removes a file drop listener.
	 * @param listener the FileDropListener to remove.
	 * @return true if it was removed successfully else false.
	 ************************************************************************/
	
	public boolean removeDropListener(FileDropListener listener) {
		return this.listeners.remove(listener);
	}

	/*************************************************************************
	 * Adds a drop target component.
	 * @param c the component to use as drop target.
	 ************************************************************************/
	
	public void addDropTarget(Component c) {
		this.dropTargets.put(c, new DropTarget(c, this)) ;
	}
	
	/*************************************************************************
	 * removes a drop target from this DnD support.
	 * @param c the component to remove.
	 * @return true if it was removed successfully else false.
	 ************************************************************************/
	
	public boolean removeDropTarget(Component c) {
		return this.dropTargets.remove(c) != null;
	}

	/*************************************************************************
	 * Adds a file filter that will filter out unwanted files.
	 * @param filter a FileFilter
	 ************************************************************************/
	
	public void addFileFilter(FileFilter filter) {
		this.fileFilter.add(filter);
	}
	
	/*************************************************************************
	 * Removes a file filter.
	 * @param filter a FileFilter to remove.
	 * @return true on success else false.
	 ************************************************************************/
	
	public boolean removeFileFilter(FileFilter filter) {
		return this.fileFilter.remove(filter);
	}
	
	/*************************************************************************
	 * @return Returns the URI_LIST data flavor.
	 ************************************************************************/
	
	private static DataFlavor getUriListFlavor() {
		if (URI_LIST == null) {
			try {
				URI_LIST = new DataFlavor("text/uri-list;class=java.lang.String"); //$NON-NLS-1$
			} catch (ClassNotFoundException e) {
				// may never happen. 
			} 
		}
		return URI_LIST;
	}
	
	/*************************************************************************
	 * Convert a list of uri s to File objects. 
	 * @param data the  RFC 2483 formatted uri list.
	 * @return a list containing file objects.
	 ************************************************************************/
	
	private static List<File> textURIListToFileList(String data) {
		List<File> list = new ArrayList<File>(1);
		for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) { //$NON-NLS-1$
			String s = st.nextToken();
			if (s.startsWith("#")) { //$NON-NLS-1$
				// the line is a comment (as per the RFC 2483)
				continue;
			}
			try {
				URI uri = new URI(s);
				File file = new File(uri);
				list.add(file);
			} catch (URISyntaxException e) {
				// invalid URI
			} catch (IllegalArgumentException e) {
				// the URI is not a valid 'file:' URI
			}
		}
		return list;
	}
	
	/*****************************************************************************
	 * This listener interface is used to notify a watcher when a list of files
	 * was dropped.
	 ****************************************************************************/
	
	public static interface FileDropListener {
		
		/*************************************************************************
		 * This method is invoked when files are dropped.
		 * @param fileList the list containing the files that were dropped.
		 ************************************************************************/
		
		public void filesDropped(List<File> fileList);
		
		
	}
	
}
