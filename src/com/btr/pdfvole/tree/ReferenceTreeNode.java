package com.btr.pdfvole.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.btr.pdfvole.ICommonAnalyzer;
import com.btr.pdfvole.INodeViewer;
import com.btr.pdfvole.ResourceManager;
import com.btr.pdfvole.viewer.HtmlNodeViewer;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * Reference tree node for the PDF tree.
 * 
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class ReferenceTreeNode extends AbstractPdfTreeNode {

	private PRIndirectReference value;
	private ICommonAnalyzer analyzer;
	
	/*************************************************************************
	 * Constructor
	 * @param p0
	 ************************************************************************/
	
	public ReferenceTreeNode(String name, PRIndirectReference value, ICommonAnalyzer analyzer) {
		super(name);
		this.value = value;
		this.analyzer = analyzer;
		
		setIcon(ResourceManager.getIcon("tree.node.ref.icon")); //$NON-NLS-1$
		add(new DefaultMutableTreeNode());
	}

	/*************************************************************************
	 * @see com.btr.pdfvole.tree.AbstractPdfTreeNode#updateViewer(com.btr.pdfvole.INodeViewer)
	 ************************************************************************/
	
	@Override
	public void updateViewer(INodeViewer viewer) {
		HtmlNodeViewer contentPanel = new HtmlNodeViewer();
		contentPanel.setContent(
				String.format("<html><p>Reference to object {%s}</p></html>", this.value)); //$NON-NLS-1$
		viewer.setViewer(contentPanel);
	}

	/*************************************************************************
	 * Expands this node and resolves the reference to the real object.
	 ************************************************************************/
	
	public void expand() {
		this.removeAllChildren();
		this.analyzer.buildSubTree(PdfReader.getPdfObject(this.value), 
				ResourceManager.getString("tree.node.ref.text", String.valueOf(this.value)),  //$NON-NLS-1$
				this.value.getReader(), this);
	}
	
	/*************************************************************************
	 * Gets the reference the node is used for.
	 * @return a PRIndirectReference.
	 ************************************************************************/
	
	public PRIndirectReference getReference() {
		return this.value;
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
