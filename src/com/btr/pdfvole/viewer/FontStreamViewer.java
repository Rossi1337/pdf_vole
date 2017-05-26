package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * An PDF font stream parser and viewer.
 * Can be used to visualize embedded TTF font data.
 * 
 * This is currently broken because Java can parse a TTF font from the 
 * embedded stream but the returned Font seems to be broken. It does not 
 * paint any glyph and the FontMetrics are not correct.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class FontStreamViewer extends JPanel implements IStreamViewer {

	private FontViewer fontViewer;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public FontStreamViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * Initializes the GUI.
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new BorderLayout());
		setSize(400, 300);
		
		this.fontViewer  = new FontViewer();
		add(new JScrollPane(this.fontViewer), BorderLayout.CENTER);
	}

	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param data the binary data of the stream.
	 ************************************************************************/
	
	public void setData(PRStream data) {
		try {
			byte[] bArr = PdfReader.getStreamBytes(data);
			
			Font f = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(bArr));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
			this.fontViewer.setDemoFont(f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (FontFormatException e) {
			throw new RuntimeException(e);
		}
	}
	
}