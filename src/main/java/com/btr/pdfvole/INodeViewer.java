package com.btr.pdfvole;

import javax.swing.JComponent;

/*****************************************************************************
 * Interface for a viewer component that can be set from a tree node. 
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public interface INodeViewer {
  
	/*************************************************************************
	 * Set the viewer for the given node type.
	 * @param component a component to displays the node's content.
	 ************************************************************************/
	
	public void setViewer(JComponent content);

}
