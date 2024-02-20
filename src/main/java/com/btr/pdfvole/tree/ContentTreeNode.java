package com.btr.pdfvole.tree;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.ResourceManager;
import com.btr.pdfvole.viewer.StreamViewer;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfObject;

/*****************************************************************************
 * Tree node for a content stream.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class ContentTreeNode extends AbstractPdfTreeNode {

	private PdfObject contents;

	/*************************************************************************
	 * Constructor
	 * @param name
	 * @param value
	 ************************************************************************/
	
	public ContentTreeNode(String name, PdfObject value) {
		super(name);
		this.contents = value;
		
		setIcon(ResourceManager.getIcon("tree.node.content.icon")); //$NON-NLS-1$
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@Override
	public void updateViewer(INodeViewer viewer) {
		if (this.contents.isStream()) {
			StreamViewer contentPanel = new StreamViewer();
			contentPanel.setData((PRStream) ContentTreeNode.this.contents);
			viewer.setViewer(contentPanel);
		}
	}
	
	/*************************************************************************
	 * calculateSize
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#calculateSize()
	 ************************************************************************/
	@Override
	protected int calculateSize() {
		if (this.contents.isStream()) {
			return PdfSizeCalculator.calculateSize((PRStream)this.contents);
		}
		return 0;
	}
	

}
