package com.btr.pdfvole.tasks;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.tree.TreeModel;

import com.btr.pdfvole.PdfTreeParser;
import com.btr.pdfvole.PdfVole;
import com.btr.pdfvole.ResourceManager;

/*****************************************************************************
 * No description for now
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class LoadFileTask extends SwingWorker<TreeModel, Void> {

		private PdfVole mainFrame;
		private String password;
		private File pdfFile;

		/*************************************************************************
		 * Constructor
		 * @param mainFrame the caller of the task
		 * @param pdfFile the file to load from.
		 * @param password the password for the file, null if none is needed. 
		 ************************************************************************/
		
		public LoadFileTask(PdfVole mainFrame, File pdfFile, String password) {
			super();
			this.mainFrame = mainFrame;
			this.pdfFile = pdfFile;
			this.password = password;
		}
	
		/*************************************************************************
		 * doInBackground
		 * @see javax.swing.SwingWorker#doInBackground()
		 ************************************************************************/
		
		@Override
		protected TreeModel doInBackground() throws ExecutionException {
			try {
				PdfTreeParser analyzer = new PdfTreeParser(this.pdfFile.getAbsolutePath(), this.password);
				return analyzer.buildTree();
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}

		/*************************************************************************
		 * done
		 * @see javax.swing.SwingWorker#done()
		 ************************************************************************/
		@Override
		protected void done() {
			TreeModel newModel = null;
			try {
				newModel = get();
			} catch (ExecutionException e) {

			// If password is needed ask user and try again.
			if (e.getMessage() != null && e.getMessage().toLowerCase().contains("bad user password")) { //$NON-NLS-1$
				PdfVole.busy.setLocked(false);
				String pw = JOptionPane.showInputDialog(ResourceManager.getString("pwDialog.msg")); //$NON-NLS-1$
				if (pw == null) {
					return;
				}

				PdfVole.busy.setLocked(true);
				new LoadFileTask(this.mainFrame, this.pdfFile, pw).execute();
				return;
			}

			PdfVole.busy.setLocked(false);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			PdfVole.busy.setLocked(false);
			throw new RuntimeException(e);
		} 
		
		this.mainFrame.setNewModel(newModel);
		
		PdfVole.busy.setLocked(false);
	}
	

}


