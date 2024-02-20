package com.btr.pdfvole.tree;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.ResourceManager;
import com.btr.pdfvole.viewer.HtmlNodeViewer;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * File node for the PDF tree.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class DocumentTreeNode extends AbstractPdfTreeNode {

	private PdfReader reader;
	private Object source;

	/*************************************************************************&
	 * Constructor
	 * @param p0
	 * @param reader
	 ************************************************************************/
	
	public DocumentTreeNode(Object p0, PdfReader reader) {
		super(ResourceManager.getString("tree.node.root.text")); //$NON-NLS-1$
		this.source = p0;
		this.reader = reader;
		setIcon(ResourceManager.getIcon("tree.node.root.icon")); //$NON-NLS-1$
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@Override
	public void updateViewer(INodeViewer viewer) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><b>"); //$NON-NLS-1$
		sb.append(ResourceManager.getString("tree.node.root.source")); //$NON-NLS-1$
		sb.append("</b> = "); //$NON-NLS-1$
		sb.append(this.source);
		sb.append("<br><b>"); //$NON-NLS-1$
		sb.append(ResourceManager.getString("tree.node.root.version")); //$NON-NLS-1$
		sb.append("</b> = 1."); //$NON-NLS-1$
		sb.append(this.reader.getPdfVersion());
		sb.append("<br><b>"); //$NON-NLS-1$
		sb.append(ResourceManager.getString("tree.node.root.pages")); //$NON-NLS-1$
		sb.append("</b> = "); //$NON-NLS-1$
		sb.append(this.reader.getNumberOfPages());
		sb.append("<br></html>"); //$NON-NLS-1$
		
		HtmlNodeViewer contentPanel = new HtmlNodeViewer();
		contentPanel.setContent(sb.toString());
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
