package com.btr.pdfvole.tree;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/*****************************************************************************
 * Tree renderer class.
 * Helper class for the tree.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class TreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static DecimalFormat sizeFormat = new DecimalFormat("#.##"); //$NON-NLS-1$
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public TreeCellRenderer() {
		super();
	}
	
	/*************************************************************************
	 * getTreeCellRendererComponent
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 ************************************************************************/
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		JLabel cmp = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node instanceof AbstractPdfTreeNode) {
			AbstractPdfTreeNode utn = (AbstractPdfTreeNode) node;
			cmp.setIcon(utn.getIcon());
			
			appendSize(cmp, utn);
		}
		return this;
	}

	/*************************************************************************
	 * Append size information to the node descriptions.
	 * @param cmp the label.
	 * @param utn the node.
	 ************************************************************************/
	
	private void appendSize(JLabel cmp, AbstractPdfTreeNode utn) {
		int size = utn.getSize();
		if (size > 0) {
			if (size / (1024*1000) > 0) {
				cmp.setText(cmp.getText()+" ("+sizeFormat.format(size / (1024*1000.0))+" MB)"); //$NON-NLS-1$ //$NON-NLS-2$
			} else 
			if (size / (1024) > 0) {
				cmp.setText(cmp.getText()+" ("+sizeFormat.format(size / (1024.0))+" KB)"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				cmp.setText(cmp.getText()+" ("+size+" B)");  //$NON-NLS-1$//$NON-NLS-2$
			}
		}
	}
}

