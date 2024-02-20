package com.btr.pdfvole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.btr.pdfvole.tree.ArrayTreeNode;
import com.btr.pdfvole.tree.BooleanTreeNode;
import com.btr.pdfvole.tree.ContentTreeNode;
import com.btr.pdfvole.tree.DictionaryTreeNode;
import com.btr.pdfvole.tree.DocumentTreeNode;
import com.btr.pdfvole.tree.NameTreeNode;
import com.btr.pdfvole.tree.NullTreeNode;
import com.btr.pdfvole.tree.NumberTreeNode;
import com.btr.pdfvole.tree.RefTableTreeNode;
import com.btr.pdfvole.tree.ReferenceTreeNode;
import com.btr.pdfvole.tree.SimpleTextTreeNode;
import com.btr.pdfvole.tree.StringTreeNode;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;

/*****************************************************************************
 * Analyzes the PDF file and builds the tree model used in the left hand side
 * PDF structure tree.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class PdfTreeParser implements ICommonAnalyzer {

	private String fileName; 
	private PdfReader reader;
	private String password;

	/*************************************************************************
	 * Constructor
	 * @param sourceFile the PDF file to parse.
	 * @param password the password for the pdf file, null if none is needed.
	 ************************************************************************/
	
	public PdfTreeParser(String sourceFile, String password) {
		super();
		this.fileName = sourceFile;
		this.password = password;
	}
	
	/*************************************************************************
	 * iterateObjects
	 * @see com.btr.pdfvole.ICommonAnalyzer#buildSubTree(com.lowagie.text.pdf.PdfObject, java.lang.String, com.lowagie.text.pdf.PdfReader, javax.swing.tree.DefaultMutableTreeNode)
	 ************************************************************************/
	
	@SuppressWarnings("unchecked")
	public void buildSubTree(PdfObject pdfobj, String name, PdfReader pdfreader, DefaultMutableTreeNode parentNode) {
		if (pdfobj == null) {
			return;
		}
		
		// Strip away PDF slash in names
		if (name.startsWith("/")) { //$NON-NLS-1$
			name = name.substring(1);
		}
		
		if (pdfobj.isDictionary() || pdfobj.isStream()) {
			
			PdfDictionary dict = (PdfDictionary) pdfobj;
			
			DefaultMutableTreeNode leaf = pdfobj.isStream()? 
					new ContentTreeNode(name, pdfobj) : 
					new DictionaryTreeNode(name, dict);
					
			parentNode.add(leaf);
			Set<PdfObject> s = dict.getKeys();
			for (PdfObject obj : s) {
				PdfObject value = dict.get((PdfName) obj);
				buildSubTree(value, String.valueOf(obj), pdfreader, leaf);			
			}
		} else if (pdfobj.isArray()) {
			PdfArray array = (PdfArray) pdfobj;
			DefaultMutableTreeNode leaf = new ArrayTreeNode(
					ResourceManager.getString("tree.node.array.text", //$NON-NLS-1$ 
							name, array.size()), array);  
			parentNode.add(leaf);
			int i = 0;
			ArrayList<PdfObject> kids = array.getArrayList();
			for (PdfObject curkid : kids) {
				buildSubTree(curkid, 
						ResourceManager.getString("tree.node.arrayItem.text", i++), //$NON-NLS-1$ 
						pdfreader, leaf); 
			}
		} else if (pdfobj.isIndirect()) {
			parentNode.add(new ReferenceTreeNode(name, (PRIndirectReference)pdfobj, this));
		} else if (pdfobj.isBoolean()) {
			parentNode.add(new BooleanTreeNode(name, (PdfBoolean)pdfobj));
		} else if (pdfobj.isName()) {
			parentNode.add(new NameTreeNode(name, (PdfName)pdfobj));
		} else if (pdfobj.isNull()) {
			parentNode.add(new NullTreeNode(name));
		} else if (pdfobj.isNumber()) {
			parentNode.add(new NumberTreeNode(name, (PdfNumber)pdfobj));
		} else if (pdfobj.isString()) {
			parentNode.add(new StringTreeNode(name, (PdfString)pdfobj));
		} else {
			parentNode.add(new SimpleTextTreeNode(
					ResourceManager.getString("tree.node.unknown.text", //$NON-NLS-1$ 
							name, pdfobj))); 
		}

	}


	/*************************************************************************
	 * Parses the file and build the tree. 
	 * @return a tree model.
	 * @throws IOException on read error.
	 ************************************************************************/
	
	public TreeModel buildTree() throws IOException {

		// Setup reader and root nodes.
		this.reader = new PdfReader(this.fileName, this.password == null? null : this.password.getBytes());
		
		DefaultMutableTreeNode root = new SimpleTextTreeNode(null);
		DefaultMutableTreeNode docNode = new DocumentTreeNode(this.fileName, this.reader);
		root.add(docNode);

		// Add PDF trailer
		buildSubTree(this.reader.getTrailer(), 
				ResourceManager.getString("tree.node.trailer.text"),  //$NON-NLS-1$
				this.reader, docNode);

		// Build reference table.
		int xrefCount = this.reader.getXrefSize();
		SimpleTextTreeNode xRefNode = new RefTableTreeNode(
				ResourceManager.getString("tree.node.refTable.text", xrefCount-1)); //$NON-NLS-1$
		xRefNode.setIcon(ResourceManager.getIcon("tree.node.refTable.icon")); //$NON-NLS-1$
		for (int i = 1; i <= xrefCount; i++) {
			PdfObject obj = this.reader.getPdfObject(i);
			buildSubTree(obj, 
					ResourceManager.getString("tree.node.refObject.text", i),  //$NON-NLS-1$
					this.reader, xRefNode);
		}
		docNode.add(xRefNode);
		
		return new DefaultTreeModel(root);
	}

}
