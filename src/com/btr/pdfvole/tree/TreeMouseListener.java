package com.btr.pdfvole.tree;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.btr.pdfvole.PdfVole;


/*****************************************************************************
 * Small helper class for a listener.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class TreeMouseListener extends MouseAdapter {

	private PdfVole parent;

	/*************************************************************************
	 * Constructor
	 * @param parent
	 ************************************************************************/
	
	public TreeMouseListener(PdfVole parent) {
		super();
		this.parent = parent;
	}
	
	/*************************************************************************
	 * mousePressed
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 ************************************************************************/
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (e.isPopupTrigger()) {
			this.parent.showPopupMenu((Component)e.getSource(), e.getX(), e.getY());
		}
	}

	/*************************************************************************
	 * mouseReleased
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 ************************************************************************/
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if (e.isPopupTrigger()) {
			this.parent.showPopupMenu((Component)e.getSource(), e.getX(), e.getY());
		}
	}
	
	
}