package com.btr.pdfvole.tree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.btr.pdfvole.PdfVole;

/*****************************************************************************
 * Small listener adapter class.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class TreeSelectionAdapter implements TreeSelectionListener {
	private PdfVole adaptee;

	public TreeSelectionAdapter(PdfVole adaptee) {
		this.adaptee = adaptee;
	}

	public void valueChanged(TreeSelectionEvent e) {
		this.adaptee.treeSelectionChanged(e);
	}
}
