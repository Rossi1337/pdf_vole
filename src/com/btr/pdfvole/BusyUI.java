package com.btr.pdfvole;

import org.jdesktop.jxlayer.plaf.ext.LockableUI;

/*****************************************************************************
 * Busy UI for the JXLayer.
 * Used to lock the UI during long term operations and to display the wait cursor.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class BusyUI extends LockableUI {

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public BusyUI() {
		super();
	}
	
}
