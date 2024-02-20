package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/*****************************************************************************
 * An HTML content viewer.
 * Used by some tree nodes to display the node info as formatted text.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class HtmlNodeViewer extends JPanel {

	private JTextPane textPane;
	private JScrollPane jScrollPane;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public HtmlNodeViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private JTextPane getTextPane() {
		if (this.textPane == null) {
			this.textPane = new JTextPane();
			this.textPane.setContentType("text/html"); //$NON-NLS-1$
			this.textPane.setEditable(false);
			this.textPane.setText("<html>\n  <head>\n    \n  </head>\n  <body>\n  </body>\n</html>"); //$NON-NLS-1$
		}
		return this.textPane;
	}

	private void initComponent() {
		setAlignmentY(0.5f);
		setLayout(new BorderLayout());
		add(getJScrollPane(), BorderLayout.CENTER);
		setSize(400, 300);
	}

	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.add(getTextPane());
			this.jScrollPane.setViewportView(getTextPane());
		}
		return this.jScrollPane;
	}

	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param text the HTML text to set.
	 ************************************************************************/
	
	public void setContent(String text) {
		getTextPane().setText(text);
	}

}
