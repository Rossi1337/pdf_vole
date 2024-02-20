package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.btr.pdfvole.ResourceManager;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * An PDF text stream parser and viewer.
 * Can be used for embedded XML meta data or text.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class TextStreamViewer extends JPanel implements IStreamViewer, ActionListener {

	private JTextPane textPane; 
	private JComboBox cmbCharset;
	private byte[] data;

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public TextStreamViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * Initializes the GUI.
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new BorderLayout());
		setSize(400, 300);
		
		this.textPane = new JTextPane();
		this.textPane.setEditable(false);
		add(new JScrollPane(this.textPane), BorderLayout.CENTER);
		
		Vector<Charset> v = new Vector<Charset>();
		v.addAll(Charset.availableCharsets().values());
		this.cmbCharset = new JComboBox(v);
		this.cmbCharset.setSelectedItem(Charset.defaultCharset());
		this.cmbCharset.addActionListener(this);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(new JLabel(ResourceManager.getString("stream.text.charset"))); //$NON-NLS-1$
		p.add(this.cmbCharset);
		add(p, BorderLayout.SOUTH);
		
	}

	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param data the binary data of the stream.
	 ************************************************************************/
	
	public void setData(PRStream data) {
		try {
			this.data = PdfReader.getStreamBytes(data);
			this.textPane.setText(new String(this.data, (Charset)this.cmbCharset.getSelectedItem()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*************************************************************************
	 * actionPerformed
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 ************************************************************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		this.textPane.setText(new String(this.data, (Charset)this.cmbCharset.getSelectedItem()));
	}
	
}