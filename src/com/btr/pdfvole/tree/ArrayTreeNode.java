package com.btr.pdfvole.tree;

import java.util.ArrayList;

import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.ResourceManager;
import com.btr.pdfvole.viewer.HtmlNodeViewer;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfObject;


/*****************************************************************************
 * A array tree node.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class ArrayTreeNode extends AbstractPdfTreeNode {
	
	 private PdfArray value;

	/*************************************************************************
	 * Constructor
	 * @param userObject
	 * @param arr
	 ************************************************************************/
	
	public ArrayTreeNode(Object userObject, PdfArray arr) {
		super(userObject);
		this.value = arr;
		setIcon(ResourceManager.getIcon("tree.node.array.icon")); //$NON-NLS-1$
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateViewer(INodeViewer viewer) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>[<br>"); //$NON-NLS-1$
		ArrayList<PdfObject> arl = this.value.getArrayList();
		for (int i = 0; i < arl.size(); i++) {
			if (i != 0) {
				sb.append(",<br>"); //$NON-NLS-1$
			}
			sb.append(arl.get(i).toString());
		}
		sb.append("<br>]</html>"); //$NON-NLS-1$
		
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
		return PdfSizeCalculator.calculateSize(this.value);
	}
	
	
	
}
