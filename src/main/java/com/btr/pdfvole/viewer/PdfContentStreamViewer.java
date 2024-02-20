package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.btr.pdfvole.OperatorDescription;
import com.btr.pdfvole.PdfVole;
import com.btr.pdfvole.ResourceManager;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * An PDF content stream parser and viewer.
 * Parses the PDF commands and displays them together with a description of 
 * the commands.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class PdfContentStreamViewer extends JPanel implements IStreamViewer {

	private static final String colID1 = ResourceManager.getString("stream.pdf.tbl.header1"); //$NON-NLS-1$
	private static final String colID2 = ResourceManager.getString("stream.pdf.tbl.header2"); //$NON-NLS-1$
	
	private CellRenderer cellR = new CellRenderer();
	private JTable table;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public PdfContentStreamViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * Initializes the GUI.
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new BorderLayout());
		add(new JScrollPane(getTable()), BorderLayout.CENTER);
		setSize(400, 300);
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private JTable getTable() {
		if (this.table == null) {
			this.table = new JTable();
			this.table.setModel(new DefaultTableModel(new Object[0][], new String[] { colID1, colID2 }));
			this.table.setColumnSelectionAllowed(false);
			this.table.setCellSelectionEnabled(false);
			this.table.setRowSelectionAllowed(true);
			this.table.setShowHorizontalLines(false);
			this.table.setShowVerticalLines(false);
		}
		return this.table;
	}

	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param data the binary data of the stream.
	 ************************************************************************/
	
	public void setData(PRStream data) {
		parsePageContent(data);
	}
	
	/*************************************************************************
	 * Sets the new table data.
	 * @param data the data to set on the table.
	 ************************************************************************/
	
	private void setTableContent(Object[][] data) {
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		model.setDataVector(data, new String[] {colID1, colID2});
		this.table.getColumn(colID1).setCellRenderer(this.cellR);
		this.table.getColumn(colID2).setCellRenderer(this.cellR);
	}
	

	/*************************************************************************
	 * Parse a page content stream and update the UI.
	 * @param stream 
	 ************************************************************************/
	
	private void parsePageContent(final PRStream stream) {
		final StringBuilder sb = new StringBuilder("<html>"); //$NON-NLS-1$
		final List<Object[]> data = new ArrayList<Object[]>();
		
		PdfVole.busy.setLocked(true);
		
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {

				Set<?> set = stream.getKeys();
				for (Object key : set) {
					sb.append("<b>"+key.toString()+"</b>"); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append("&nbsp;=&nbsp;" + stream.get((PdfName) key)); //$NON-NLS-1$
					sb.append("<br>"); //$NON-NLS-1$
				}
				sb.append("</html>"); //$NON-NLS-1$

				try {
					byte barr[] = PdfReader.getStreamBytes(stream);

					PdfContentParser contentParser = new PdfContentParser(new PRTokeniser(barr));
					
					ArrayList<PdfObject> cmdParts = new ArrayList<PdfObject>();
					contentParser.parse(cmdParts);
					
					int indent = 0;
					while (!cmdParts.isEmpty()) {
						Object[] row = new Object[2];
						
						// Last one is operator.
						String opName = cmdParts.get(cmdParts.size()-1).toString().trim();
						
						if ("Q".equals(opName)) { //$NON-NLS-1$
							indent--;
						}
						
						StringBuilder opLine = new StringBuilder(); 
						for (int j = 0; j < indent; j++) {
							opLine.append("    "); //$NON-NLS-1$
						}
						for (PdfObject op : cmdParts) {
							opLine.append(" "); //$NON-NLS-1$
							opLine.append(op.toString());
						}

						if ("q".equals(opName)) { //$NON-NLS-1$
							indent++;
						}
						row[0] = opLine.toString(); 
						
						OperatorDescription desc = OperatorDescription.get(opName);
						if (desc != null) {
							row[1] = desc.getDescription();
						}
						data.add(row);

						contentParser.parse(cmdParts);
					}
				} catch (IOException ex) {
					// Do nothing
				}
				return null;
			}

			@Override
			protected void done() {
				setTableContent(data.toArray(new Object[data.size()][]));
				PdfVole.busy.setLocked(false);
			}
		}.execute();
	}


}

/*****************************************************************************
 * Table cell renderer.
 ****************************************************************************/

class CellRenderer extends DefaultTableCellRenderer {
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	CellRenderer() {
		super();
	}

	/*************************************************************************
	 * getTableCellRendererComponent
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 ************************************************************************/
	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,	int row, int column) {
		
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column == 0) {
			c.setFont(getFont().deriveFont(Font.BOLD));
		}
		
		if (row %2 > 0) {
			c.setBackground(new Color(240, 240, 240));
		} else {
			c.setBackground(Color.WHITE);
		}
		if (value == null && column == 1) {
			c.setText(ResourceManager.getString("stream.pdf.cmd.unknown")); //$NON-NLS-1$
			c.setForeground(Color.RED);
		} else {
			c.setForeground(table.getForeground());
		}
		
		return c;
	}
	
}

