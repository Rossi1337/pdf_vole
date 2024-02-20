package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.btr.pdfvole.ResourceManager;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfName;

/*****************************************************************************
 * An content stream viewer container. There are multiple viewers registered
 * to choose from.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class StreamViewer extends JPanel implements ActionListener {

	public static class DummyViewer extends JComponent implements IStreamViewer {
		@Override
		public void setData(PRStream stream) {/***/}
	}

	private static final Object[][] VIEWER = {
		{ResourceManager.getString("stream.viewer.mode1"), DummyViewer.class},  //$NON-NLS-1$
		{ResourceManager.getString("stream.viewer.mode2"), RawStreamViewer.class} ,  //$NON-NLS-1$
		{ResourceManager.getString("stream.viewer.mode3"), HexViewer.class},  //$NON-NLS-1$
		{ResourceManager.getString("stream.viewer.mode4"), PdfContentStreamViewer.class},  //$NON-NLS-1$
		{ResourceManager.getString("stream.viewer.mode5"), ImageStreamViewer.class},  //$NON-NLS-1$
		{ResourceManager.getString("stream.viewer.mode6"), TextStreamViewer.class}, //$NON-NLS-1$
		{ResourceManager.getString("stream.viewer.mode7"), FontStreamViewer.class} //$NON-NLS-1$
	};
	
	private PRStream data;

	private IStreamViewer currentViewer;
	
	private JComboBox cmbMode;
	private JTextPane headerArea;
	private JPanel settingsAreaPanel;
	private JPanel dataAreaPanel; 
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public StreamViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private JTextPane getHeaderArea() {
		if (this.headerArea == null) {
			this.headerArea = new JTextPane();
			this.headerArea.setContentType("text/html"); //$NON-NLS-1$
			this.headerArea.setEditable(false);
		}
		return this.headerArea;
	}

	/*************************************************************************
	 * Initializes the GUI.
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new BorderLayout());
		add(new JScrollPane(getHeaderArea()), BorderLayout.NORTH);

		this.currentViewer = new DummyViewer();

		// Build viewer combo box entries.
		Object[] cmbEntries = new Object[VIEWER.length];
		for (int i = 0; i < cmbEntries.length; i++) {
			cmbEntries[i] = VIEWER[i][0];
		}
		
		this.cmbMode = new JComboBox(cmbEntries);
		this.cmbMode.addActionListener(this);
		
		this.settingsAreaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.settingsAreaPanel.add( new JLabel(ResourceManager.getString("stream.viewer.mode"))); //$NON-NLS-1$
		this.settingsAreaPanel.add(this.cmbMode);

		this.dataAreaPanel = new JPanel(new BorderLayout());
		this.dataAreaPanel.add(this.settingsAreaPanel, BorderLayout.NORTH);
		this.dataAreaPanel.add((Component) this.currentViewer, BorderLayout.CENTER);

		JPanel p = new JPanel(new BorderLayout());
		p.add(this.settingsAreaPanel, BorderLayout.NORTH);
		p.add(this.dataAreaPanel, BorderLayout.CENTER);
		
		add(p, BorderLayout.CENTER);
		setSize(400, 300);
	}

	
	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param text the HTML text to set.
	 ************************************************************************/
	
	private void setHeader(String text) {
		getHeaderArea().setText(text);
	}
	
	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param data the binary data of the stream.
	 ************************************************************************/
	
	public void setData(PRStream data) {
		this.data = data;
		
		Set<?> set = data.getKeys();
		StringBuilder sb = new StringBuilder();
		for (Object key : set) {
			sb.append("<b>"+key.toString()+"</b>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("&nbsp;=&nbsp;" + data.get((PdfName)key)); //$NON-NLS-1$
			sb.append("<br>"); //$NON-NLS-1$
		}
		sb.append("</html>"); //$NON-NLS-1$
		
		setHeader(sb.toString());
		
		this.currentViewer.setData(data);		
	}

	/*************************************************************************
	 * actionPerformed
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 ************************************************************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		this.dataAreaPanel.removeAll();
		try {
			int index = this.cmbMode.getSelectedIndex();
			Class<?> viewerClass = (Class<?>) VIEWER[index][1];
		
			this.currentViewer = (IStreamViewer) viewerClass.newInstance();
			this.currentViewer.setData(this.data);
			
			this.dataAreaPanel.add((Component)this.currentViewer, BorderLayout.CENTER);
			this.dataAreaPanel.revalidate();
		} catch (InstantiationException e1) {
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e1) {
			throw new RuntimeException(e1);
		}
	}

}