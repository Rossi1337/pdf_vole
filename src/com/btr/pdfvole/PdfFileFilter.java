package com.btr.pdfvole;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/*****************************************************************************
 * File filter for PDF files. Used in the file open dialog.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class PdfFileFilter extends FileFilter implements java.io.FileFilter {
	
	public static final String PDF_EXTENSION = ".pdf"; //$NON-NLS-1$
	
	private boolean allowDirectories = true;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public PdfFileFilter() {
		super();
	}
	
	/*************************************************************************
	 * Constructor
	 * @param allowDirectories mode to allow only files or folders too.
	 ************************************************************************/
	
	public PdfFileFilter(boolean allowDirectories) {
		super();
		this.allowDirectories = allowDirectories;
	}
	
	/*************************************************************************
	 * getDescription
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 ************************************************************************/
	@Override
	public String getDescription() {
		return ResourceManager.getString("fileChooserFilter.text"); //$NON-NLS-1$
	}

	/*************************************************************************
	 * accept
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 ************************************************************************/
	@Override
	public boolean accept(File f) {
		return (f.isDirectory() && this.allowDirectories) 
			|| f.getName().toLowerCase().endsWith(PDF_EXTENSION);
	}
}