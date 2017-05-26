package com.btr.pdfvole.tree;

import javax.swing.Icon;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.viewer.HtmlNodeViewer;

/*****************************************************************************
 * Simple text tree node.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class SimpleTextTreeNode extends AbstractPdfTreeNode {

	/*************************************************************************
	 * Constructor
	 * @param p0
	 ************************************************************************/
	
	public SimpleTextTreeNode(Object p0) {
		super(p0);
	}

	/*************************************************************************
	 * Constructor
	 * @param title
	 * @param icon
	 ************************************************************************/
	
	public SimpleTextTreeNode(Object title, Icon icon) {
		super(title);
		setIcon(icon);
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@Override
	public void updateViewer(INodeViewer viewer) {
		HtmlNodeViewer contentPanel = new HtmlNodeViewer();
		contentPanel.setContent(
				String.format("<html><p>%s</p></html>", //$NON-NLS-1$ 
						this.userObject));
		viewer.setViewer(contentPanel);
	}
	
	/*************************************************************************
	 * calculateSize
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#calculateSize()
	 ************************************************************************/
	@Override
	protected int calculateSize() {
		return 0;
	}
}
