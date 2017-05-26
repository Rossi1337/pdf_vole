package com.btr.pdfvole;

import javax.swing.tree.DefaultMutableTreeNode;

import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * Interface for a PDF analyzer.
 * Used to implement PDF stream analyzers that help to build up the tree model.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public interface ICommonAnalyzer {
  
	/*************************************************************************
	 * Analyze the given PdfObject and adds an appropriate node to the parent node
	 * for this object.
	 * @param pdfobj The PdfObject that is currently analyzed.
	 * @param name the name of the object in the PDF stream.
	 * @param pdfreader the PDF reader positioned at the current file index. 
	 * @param parentNode the parent node to fill.
	 ************************************************************************/
	
	void buildSubTree(PdfObject pdfobj, String name, PdfReader pdfreader, DefaultMutableTreeNode parentNode);
  
}
