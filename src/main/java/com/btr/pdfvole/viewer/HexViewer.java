package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import com.btr.pdfvole.PdfVole;
import com.btr.pdfvole.ResourceManager;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * Hex viewer class that is used to display raw stream content.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class HexViewer extends JPanel implements IStreamViewer {
	
	private static SimpleAttributeSet COL_1 = new SimpleAttributeSet();
	private static SimpleAttributeSet COL_2 = new SimpleAttributeSet();
	
	static {
		StyleConstants.setForeground(COL_1, Color.BLACK);
		StyleConstants.setBackground(COL_1, Color.WHITE);
	    StyleConstants.setFontFamily(COL_1, Font.MONOSPACED);
	    StyleConstants.setFontSize(COL_1, 12);
		
		StyleConstants.setForeground(COL_2, Color.BLACK);
		StyleConstants.setBackground(COL_2, new Color(240, 240, 240));
	    StyleConstants.setFontFamily(COL_2, Font.MONOSPACED);
	    StyleConstants.setFontSize(COL_2, 12);
	}
	
	private static final Font TEXT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	private static final Font HEX_FONT = TEXT_FONT;
	private static final Font ADDRESS_FONT = TEXT_FONT.deriveFont(Font.BOLD);
	
	private boolean showRawData = false;
	private int lineSize = 32;
	private int colSize = 4;
	private byte[] data;
	
	private JTextPane addressArea;
	private JTextPane textArea;
	private JTextPane hexArea;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public HexViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private JTextPane getAddressArea() {
		if (this.addressArea == null) {
			this.addressArea = new JTextPane();
			this.addressArea.setBackground(new Color(220, 220, 220));
			this.addressArea.setEditable(false);
			this.addressArea.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), 
						BorderFactory.createEmptyBorder(0, 5, 0, 5)
					)
				);			this.addressArea.setFont(ADDRESS_FONT);
		}
		return this.addressArea;
	}

	/*************************************************************************
	 * Initializes the GUI
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new BorderLayout());
		
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(getHexArea(), BorderLayout.WEST);
		p2.add(getTextArea(), BorderLayout.CENTER);
		
		JScrollPane sp = new JScrollPane(p2);
		sp.getVerticalScrollBar().setUnitIncrement(5);
		sp.getHorizontalScrollBar().setUnitIncrement(5);
		sp.setRowHeaderView(getAddressArea());
		
		add(sp, BorderLayout.CENTER);

		// Build toolbar
		ApplicationActionMap actions = Application.getContext().getActionMap(this);

		JToolBar pTop = new JToolBar();
		pTop.add(actions.get("export")); //$NON-NLS-1$
		add(pTop, BorderLayout.SOUTH);
		
		setSize(400, 300);
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private JTextPane getHexArea() {
		if (this.hexArea == null) {
			this.hexArea = new JTextPane();
			this.hexArea.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), 
						BorderFactory.createEmptyBorder(0, 5, 0, 5)
					)
				);
			this.hexArea.setEditable(true);
			this.hexArea.setFocusable(true);
			this.hexArea.setFont(HEX_FONT);
		}
		return this.hexArea;
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private JTextPane getTextArea() {
		if (this.textArea == null) {
			this.textArea = new JTextPane();
			this.textArea.setEditable(false);
			this.textArea.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), 
						BorderFactory.createEmptyBorder(0, 5, 0, 5)
					)
				);
			this.textArea.setFont(TEXT_FONT);
		}
		return this.textArea;
	}
	
	/*************************************************************************
	 * Sets the data to display in the editor. 
	 * @param data the content to display.
	 ************************************************************************/
	
	public void setData(PRStream stream) {
		try {
			if (this.showRawData) {
				this.data = PdfReader.getStreamBytesRaw(stream);
			} else {
				this.data = PdfReader.getStreamBytes(stream);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		updateView();
	}

	/*************************************************************************
	 * Update the view and layout the content.
	 ************************************************************************/
	
	private void updateView() {
		if (this.data == null) {
			return;
		}
		
		PdfVole.busy.setLocked(true);
		
		final int lines = (int) Math.ceil(this.data.length / (double)this.lineSize);

		final StringBuilder sb = new StringBuilder(lines*9);
		final Document hexDoc = new DefaultStyledDocument();
		final StringBuilder sb3 = new StringBuilder(this.data.length+lines);
		
		new SwingWorker<Void, Void>() {
		
			@Override
			protected Void doInBackground() throws Exception {
			    SimpleAttributeSet textAttribs = COL_1;

			    for (int line = 0; line < lines; line++) {
					
					// Build address line
					String address = Integer.toHexString(line*HexViewer.this.lineSize).toUpperCase();
					sb.append(("00000000"+address).substring(address.length())); //$NON-NLS-1$
					sb.append("\n"); //$NON-NLS-1$
					
					// Build hex line
					for (int lineIndex = 0; lineIndex < HexViewer.this.lineSize; lineIndex++) {
						int dataIndex = line*HexViewer.this.lineSize+lineIndex;

						if (lineIndex%(HexViewer.this.colSize) == 0 && (lineIndex/(HexViewer.this.colSize)) > 0) {
							textAttribs = (textAttribs == COL_1)? COL_2:COL_1; 
							appendHex(hexDoc, " ", textAttribs); //$NON-NLS-1$
						}
						
						String nibble; 
						if (dataIndex >= HexViewer.this.data.length) {
							nibble = "XX"; //$NON-NLS-1$
						} else {
							int b = (HexViewer.this.data[dataIndex] << 24) >>> 24;
							nibble = Integer.toHexString(b).toUpperCase();
							if (nibble.length() == 1) {
								nibble = "0" + nibble;  //$NON-NLS-1$
							}
						}
						
						appendHex(hexDoc, nibble, textAttribs);
					}
					appendHex(hexDoc, "\n", textAttribs); //$NON-NLS-1$
					textAttribs = COL_1;
					
					// Append ASCII text line.
					appendText(sb3, line);
				}
				
				return null;
			}

			@Override
			protected void done() {
				HexViewer.this.addressArea.setText(sb.toString());
				HexViewer.this.hexArea.setDocument(hexDoc);
				HexViewer.this.textArea.setText(sb3.toString());
				
				PdfVole.busy.setLocked(false);
			}
			
		}.execute();
		
	}

	/*************************************************************************
	 * Helper method to append a text line.
	 * @param sb
	 * @param line
	 ************************************************************************/
	
	private void appendText(StringBuilder sb, int line) {
		int startIndex = line*this.lineSize;
		int endIndex = startIndex+this.lineSize;
		if (endIndex > this.data.length) {
			endIndex = this.data.length;
		}
		byte[] lineData = Arrays.copyOfRange(this.data, startIndex, endIndex);
		
		for (int i = 0; i < lineData.length; i++) {
			if (lineData[i] < 32) {
			lineData[i] = 46;
			}
 		}
		
		try {
			sb.append(new String(lineData, "US-ASCII")); //$NON-NLS-1$
			sb.append("\n"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// May never happen.
			throw new RuntimeException(e);
		}
	}

	/*************************************************************************
	 * Helper method to append a hex string to a document.
	 * @param doc
	 * @param text
	 * @param textAttribs 
	 ************************************************************************/
	
	private void appendHex(Document doc, String text, SimpleAttributeSet textAttribs) {
		try {
			doc.insertString(doc.getLength(), text, textAttribs);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*************************************************************************
	 * Set the viewer into raw mode. 
	 * In this mode the data is displayed with no decode filters applied.
	 * @param raw true to enable raw mode.
	 ************************************************************************/
	
	public void setShowRawData(boolean raw) {
		this.showRawData = raw;
	}
	
	/*************************************************************************
	 * Export the data to a file.
	 ************************************************************************/
	@Action
	public void export() {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File f = chooser.getSelectedFile();
			if (f.exists()) {
				if (JOptionPane.showConfirmDialog(this, 
						ResourceManager.getString("stream.export.confirm.msg", f),  //$NON-NLS-1$
						ResourceManager.getString("stream.export.confirm.title"),  //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
					return;
				}
			}
			
			PdfVole.busy.setEnabled(true);
			new SwingWorker<Void, Void>() {
				
				@Override
				protected Void doInBackground() throws Exception {
					FileOutputStream out = new FileOutputStream(f);
					out.write(HexViewer.this.data);
					out.close();
					return null;
				}

				@Override
				protected void done() {
					PdfVole.busy.setEnabled(false);
				}
				
			}.execute();			
			
		}
		
		
	}
	

}
