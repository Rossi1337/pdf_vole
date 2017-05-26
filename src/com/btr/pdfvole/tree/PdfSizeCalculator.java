package com.btr.pdfvole.tree;

import java.util.Set;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

/*****************************************************************************
 * Defines some helper methods to calculate PDF object sizes.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class PdfSizeCalculator {
	
	/*************************************************************************
	 * Generic calculate size method.
	 * @param obj the PDf object.
	 * @return the size in bytes.
	 ************************************************************************/
	
	public static int calculateSize(PdfObject obj) {
		if (obj == null) {
			return 0;
		}

		if (obj.isDictionary()) {
			return calculateSize((PdfDictionary)obj);
		}
		if (obj.isStream()) {
			return calculateSize((PRStream)obj);
		}
		
		// Generic calculation.
		if (obj.getBytes() == null) {
			return obj.length();
		}
		return obj.getBytes().length; 
	}
	
	/*************************************************************************
	 * Generic calculate size method.
	 * @param obj the PDf object.
	 * @return the size in bytes.
	 ************************************************************************/
	
	public static int calculateSize(PdfDictionary obj) {
		int size = 0;

		@SuppressWarnings("unchecked")
		Set<PdfName> keys = obj.getKeys();
		for (PdfName pdfName : keys) {
			PdfObject value = obj.get(pdfName);
			size += calculateSize(pdfName);
			size += calculateSize(value);
		}
		return size; 
	}
	
	/*************************************************************************
	 * Generic calculate size method.
	 * @param obj the PDf object.
	 * @return the size in bytes.
	 ************************************************************************/
	
	public static int calculateSize(PRStream obj) {
		int size = calculateSize((PdfDictionary)obj);
		size += obj.getLength();
		return size; 
	}

}


