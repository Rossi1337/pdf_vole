package com.btr.pdfvole.tree;

import java.util.Set;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.ResourceManager;
import com.btr.pdfvole.viewer.HtmlNodeViewer;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;


/*****************************************************************************
 * Dictionary tree node.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class DictionaryTreeNode extends AbstractPdfTreeNode {
	
	private PdfDictionary dictionary;

	/*************************************************************************
	 * Constructor
	 * @param name
	 * @param dictionary
	 ************************************************************************/
	
	public DictionaryTreeNode(String name, PdfDictionary dictionary) {
		super(ResourceManager.getString("tree.node.dict.text", name,  dictionary.isStream() ? " <Stream>" : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.dictionary = dictionary;

		setIcon(ResourceManager.getIcon("tree.node.dict.icon")); //$NON-NLS-1$
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateViewer(INodeViewer viewer) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>"); //$NON-NLS-1$
		Set<PdfName> set = this.dictionary.getKeys();
		for (PdfName key : set) {
			sb.append("<b>"+key.toString()+"</b>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("&nbsp;=&nbsp;" + this.dictionary.get(key)); //$NON-NLS-1$
			sb.append("<br>"); //$NON-NLS-1$
		}
		sb.append("</html>"); //$NON-NLS-1$
		
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
		return PdfSizeCalculator.calculateSize(this.dictionary);
	}

}
