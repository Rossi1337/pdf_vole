package com.btr.pdfvole.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JComponent;

/*****************************************************************************
 * Renders an example of a font. Set the font with setDemoFont to display on 
 * the component and then an example of that font is displayed in various 
 * font sizes. 
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class FontViewer extends JComponent {

	private static final String HEADER_LINE1 = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$
	private static final String HEADER_LINE2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$
	private static final String HEADER_LINE3 = "0123456789.:,;(*!?'/\\\")€$%^&-+@~#<>{}[]"; //$NON-NLS-1$
	
	private static final String DEFAULT_TEXT = "The quick brown fox jumps over the lazy dog.";  //$NON-NLS-1$
	
	private String exampleText = DEFAULT_TEXT;
	
	private Stroke lineStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	private float headerFontSize = 22.0f;
	private Font demoFont;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public FontViewer() {
		this(null);
	}
	
	/*************************************************************************
	 * Constructor
	 * @param demoFont the font to display.
	 ************************************************************************/
	
	public FontViewer(Font demoFont) {
		super();
		setOpaque(true);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		
		this.demoFont = demoFont;
		if (this.demoFont == null) {
			this.demoFont = getFont();
		}
	}

	/*************************************************************************
	 * paintComponent
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 ************************************************************************/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setColor(getForeground());
		g2.setStroke(this.lineStroke);

		Font fX = getDemoFont(); 
		int y = 5;
		
		// Print font name and glyphcount always in standard font.
		g2.setFont(getFont());
		y += g2.getFontMetrics().getHeight();
		g2.drawString(fX.getName()+" ("+fX.getNumGlyphs()+")", 5, y); //$NON-NLS-1$ //$NON-NLS-2$

		y += 5; 
		g2.drawLine(5, y, getWidth()-5, y);
		
		// Print header with most common characters.
		g2.setFont(fX.deriveFont(getHeaderFontSize()));
		
		y += g2.getFontMetrics().getHeight();
		g2.drawString(HEADER_LINE1, 5, y);
		
		y += g2.getFontMetrics().getHeight();
		g2.drawString(HEADER_LINE2, 5, y);
		
		y += g2.getFontMetrics().getHeight();
		g2.drawString(HEADER_LINE3, 5, y);

		y += 10;
		
		g2.drawLine(5, y, getWidth()-5, y);
		
		// Print example text line in various sizes.
		g2.setFont(fX.deriveFont(10.0f));
		while (y < getHeight()) {
			y += g2.getFontMetrics().getHeight();
			g2.drawString(this.exampleText, 5, y);
			fX = fX.deriveFont(fX.getSize2D()*1.3f);
			g2.setFont(fX);
		}
	}

	/*************************************************************************
	 * Sets the font size for the demo font header section.
	 * @return a font size in point.
	 ************************************************************************/
	
	public float getHeaderFontSize() {
		return this.headerFontSize;
	}
	
	/*************************************************************************
	 * Gets the font size for the demo font header section.
	 * @param size the font size in point.
	 ************************************************************************/
	
	public void setHeaderFontSize(float size) {
		float oldValue = getHeaderFontSize();
		this.headerFontSize = size;
		firePropertyChange("headerFontSize", oldValue, this.headerFontSize); //$NON-NLS-1$
	}

	/*************************************************************************
	 * Gets the example text line that is printed in various sizes.
	 * @return Returns the exampleText.
	 ************************************************************************/
	
	public String getExampleText() {
		return this.exampleText;
	}

	/*************************************************************************
	 * Sets the example text line that is printed in various sizes.
	 * If you set it null it will revert to the default text line.
	 * @param exampleText The exampleText to set.
	 ************************************************************************/
	
	public void setExampleText(String exampleText) {
		if (exampleText == null) {
			exampleText = DEFAULT_TEXT;
		}
		
		String oldValue = getExampleText();
		this.exampleText = exampleText;
		firePropertyChange("exampleText", oldValue, this.exampleText); //$NON-NLS-1$
	}

	/*************************************************************************
	 * Gets the font that is displayed in various sizes.
	 * @return Returns the demoFont.
	 ************************************************************************/
	
	public Font getDemoFont() {
		return this.demoFont;
	}

	/*************************************************************************
	 * Sets the font that is displayed in various sizes.
	 * If set to null the JComponent default font will be used.
	 * @param demoFont The demoFont to set.
	 ************************************************************************/
	
	public void setDemoFont(Font demoFont) {
		if (demoFont == null) {
			demoFont = getFont(); 
		}
		Font oldValue = getDemoFont(); 
		this.demoFont = demoFont;
		firePropertyChange("demoFont", oldValue, this.demoFont); //$NON-NLS-1$
	}

}