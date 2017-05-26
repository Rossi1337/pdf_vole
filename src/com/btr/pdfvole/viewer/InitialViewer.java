package com.btr.pdfvole.viewer;

import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.lowagie.text.pdf.PRStream;

/*****************************************************************************
 * A Dummy viewer that is used initially when the application is launched.
 * This is used to display the small tip at the start panel.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class InitialViewer extends JPanel implements IStreamViewer {

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public InitialViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * Initializes the GUI.
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new GridBagLayout());
		setSize(400, 300);
		
		JLabel helpMsg = new JLabel();
		helpMsg.setName("helpMsg"); //$NON-NLS-1$
		helpMsg.setFont(helpMsg.getFont().deriveFont(Font.ITALIC));
		helpMsg.setHorizontalTextPosition(SwingConstants.LEADING);
		helpMsg.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(helpMsg);
	}

	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param data the binary data of the stream.
	 ************************************************************************/
	
	public void setData(PRStream data) {
		// Do nothing
	}
	
}