package com.btr.pdfvole;

import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/*****************************************************************************
 * A little helper class to catch all exceptions and show them in a dialog.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class ExceptionDialog implements UncaughtExceptionHandler {

	/*************************************************************************
	 * uncaughtException
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 ************************************************************************/
	@Override
	public void uncaughtException(Thread t, final Throwable e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				StringWriter errorMsg = new StringWriter(); 
				e.printStackTrace(new PrintWriter(errorMsg));
				
				JScrollPane sp = new JScrollPane(new JTextArea(errorMsg.toString()));
				sp.setPreferredSize(new Dimension(500, 300));
				
				JOptionPane.showMessageDialog(null, sp, 
						"Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
		});
	}

}


