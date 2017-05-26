package com.btr.pdfvole.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.ResourceManager;

/*****************************************************************************
 * Base class for all tree nodes.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public abstract class AbstractPdfTreeNode extends DefaultMutableTreeNode {

	private Icon icon = ResourceManager.getIcon("tree.node.unknown.icon"); //$NON-NLS-1$
	private int size = -1;

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public AbstractPdfTreeNode() {
		super();
	}

	/*************************************************************************
	 * Constructor
	 * @param userObject
	 ************************************************************************/
	
	public AbstractPdfTreeNode(Object userObject) {
		super(userObject);
	}

	/*************************************************************************
	 * Called to update the viewer.
	 * @param viewer
	 ************************************************************************/
	
	public abstract void updateViewer(INodeViewer viewer);

	/*************************************************************************
	 * Sets the icon for the tree node.
	 * @param icon an icon, null for no icon.
	 ************************************************************************/
	
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/*************************************************************************
	 * Gets the icon for this tree node.
	 * @return an Icon, null if none is set.
	 ************************************************************************/
	
	public Icon getIcon() {
		return this.icon;
	}
	
	/*************************************************************************
	 * Calculates the size of this PDF node. This will only be the flat size, 
	 * so referenced objects are not counted recursively.
	 * @return the calculated size.
	 ************************************************************************/
	
	protected abstract int calculateSize();
	
	/*************************************************************************
	 * Gets the size of the node. This will only be the flat size, so referenced
	 * objects are not counted recursively.
	 * @return the size of this PDF node.
	 ************************************************************************/
	
	public int getSize() {
		if (this.size == -1) {
			this.size = calculateSize();
		}
		return this.size;
	}
	
}
