package com.btr.pdfvole.viewer;

import com.lowagie.text.pdf.PRStream;

/*****************************************************************************
 * Simple interface for stream viewer components.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public interface IStreamViewer {
	
	/*************************************************************************
	 * The stream data to set.
	 * @param stream a PRStream.
	 ************************************************************************/
	
	public void setData(PRStream stream);

}
