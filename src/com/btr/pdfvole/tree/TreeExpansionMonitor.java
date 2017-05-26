package com.btr.pdfvole.tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;

/*****************************************************************************
 * Helper class to handle reference nodes.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class TreeExpansionMonitor implements TreeWillExpandListener {

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public TreeExpansionMonitor() {
		super();
	}
	
	/*************************************************************************
	 * treeWillCollapse
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 ************************************************************************/
	
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		// Not used
	}

	/*************************************************************************
	 * treeWillExpand
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 ************************************************************************/
	
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		if (event.getPath().getLastPathComponent() instanceof ReferenceTreeNode) {
			ReferenceTreeNode node = (ReferenceTreeNode) event.getPath().getLastPathComponent();
			node.expand();
		}
	}
}
