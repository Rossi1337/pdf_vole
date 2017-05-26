package com.btr.pdfvole.tree;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.viewer.HtmlNodeViewer;

/*****************************************************************************
 * Null tree node for the PDF tree.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class NullTreeNode extends AbstractPdfTreeNode {

	/*************************************************************************
	 * Constructor
	 * @param name
	 ************************************************************************/
	
	public NullTreeNode(String name) {
		super(name);
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@Override
	public void updateViewer(INodeViewer viewer) {
		HtmlNodeViewer contentPanel = new HtmlNodeViewer();
		contentPanel.setContent(
				String.format("<html><p>null</p></html>")); //$NON-NLS-1$
		viewer.setViewer(contentPanel);
	}
	
	/*************************************************************************
	 * calculateSize
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#calculateSize()
	 ************************************************************************/
	@Override
	protected int calculateSize() {
		return 1;
	}
	
}
