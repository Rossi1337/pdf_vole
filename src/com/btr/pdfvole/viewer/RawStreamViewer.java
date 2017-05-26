package com.btr.pdfvole.viewer;

import org.jdesktop.application.Action;

/*****************************************************************************
 * Show the raw stream data in a hex viewer component.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class RawStreamViewer extends HexViewer {
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public RawStreamViewer() {
		super();
		setShowRawData(true);
	}

	/*************************************************************************
	 * export
	 * @see com.btr.pdfvole.viewer.HexViewer#export()
	 ************************************************************************/
	@Override
	@Action
	public void export() {
		super.export();
	}
	
	
}
