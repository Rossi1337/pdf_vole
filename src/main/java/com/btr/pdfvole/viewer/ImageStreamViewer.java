package com.btr.pdfvole.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import com.btr.pdfvole.PdfVole;
import com.btr.pdfvole.ResourceManager;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;

/*****************************************************************************
 * An PDF Image stream parser and viewer.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class ImageStreamViewer extends JComponent implements IStreamViewer {

	// Zoom by 10 % per click
	private static final float ZOOM_STEP = 0.10f;
	
	private BufferedImage image;
	private JComponent imageComponent;
	
	private float zoom = 1.0f; // Initial zoom = 100%

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public ImageStreamViewer() {
		super();
		initComponent();
	}

	/*************************************************************************
	 * Initializes the GUI.
	 ************************************************************************/
	
	private void initComponent() {
		setLayout(new BorderLayout());
		setSize(400, 300);
		
		this.imageComponent = new JComponent() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if  (ImageStreamViewer.this.image == null) {
					return;
				}
				
				int imgWidth = (int) (ImageStreamViewer.this.image.getWidth()*ImageStreamViewer.this.zoom);
				int imgHeiht = (int) (ImageStreamViewer.this.image.getHeight()*ImageStreamViewer.this.zoom); 
				
				if (ImageStreamViewer.this.image != null) {
					g.drawImage(
							ImageStreamViewer.this.image, 
							(getWidth()-imgWidth) / 2, 
							(getHeight()-imgHeiht) / 2, 
							imgWidth, 
							imgHeiht, 
							null
						);
				}
			}
		};
		
		add(new JScrollPane(this.imageComponent), BorderLayout.CENTER);
		
		ApplicationActionMap actions = Application.getInstance().getContext().getActionMap(this);
		
		// Build toolbar.
		JToolBar pTop = new JToolBar();
		pTop.add(actions.get("saveImg")); //$NON-NLS-1$
		pTop.addSeparator();
		pTop.add(actions.get("zoomIn")); //$NON-NLS-1$
		pTop.add(actions.get("zoomOut")); //$NON-NLS-1$
		
		add(pTop, BorderLayout.SOUTH);
	}

	/*************************************************************************
	 * Save the image to disk.
	 ************************************************************************/
	
	@Action
	public void saveImg() {
		if (this.image == null) {
			return;
		}
		JFileChooser chooser = new JFileChooser();
		String[] suffixes = ImageIO.getWriterFileSuffixes();
		for (String suffix : suffixes) {
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(
					ResourceManager.getString("stream.img.filter",  //$NON-NLS-1$
							suffix.toUpperCase(), 
							suffix.toLowerCase()), 
					suffix)); 
		}
		chooser.setAcceptAllFileFilterUsed(true);
		
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
					String ext = f.getName().substring(f.getName().lastIndexOf('.')+1);
					Iterator<ImageWriter> writer = ImageIO.getImageWritersBySuffix(ext);
					if (writer.hasNext()) {
						try {
							ImageWriter w = writer.next();
							w.setOutput(new FileImageOutputStream(f));
							w.write(ImageStreamViewer.this.image);
							w.dispose();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} else {
						cancel(true);
					}
					return null;
				}

				@Override
				protected void done() {
					PdfVole.busy.setEnabled(false);
					if (isCancelled() == true) {
						JOptionPane.showMessageDialog(ImageStreamViewer.this, 
								ResourceManager.getString("stream.img.filter.error"), "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				
			}.execute();			
			
		}
	}

	/*************************************************************************
	 * Zoom in the image.
	 ************************************************************************/
	
	@Action
	public void zoomIn() {
		if (this.zoom < 100.0f) {
			this.zoom += ZOOM_STEP;
			updateSize();
		}
	}
	
	/*************************************************************************
	 * Zoom out the image.
	 ************************************************************************/
	
	@Action
	public void zoomOut() {
		if (this.zoom > 0.15f) {
			this.zoom -= ZOOM_STEP;
			updateSize();
		}
	}

	/*************************************************************************
	 * Sets the content for this viewer panel.
	 * @param data the binary data of the stream.
	 ************************************************************************/
	
	public void setData(PRStream data) {
		try {
			this.image = decodeRGBImage(data);

//			PdfObject colorSp = data.get(PdfName.COLORSPACE);
//			if (colorSp.isName() && colorSp.equals(PdfName.DEVICERGB)){
//				this.image = decodeRGBImage(data);
//			} else 
//			if (colorSp.isArray()) {
//				// TODO Bros 29.12.2008 indexed image processing goes here. 
//			}
			updateSize();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Image type not supported.",  //$NON-NLS-1$
					"Error",  //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/*************************************************************************
	 * Updates the size of the viewer component.
	 * This is called when the zoom changes.
	 ************************************************************************/
	
	private void updateSize() {
		if (this.image == null) {
			return;
		}
		int imgWidth = (int)(this.image.getWidth()*this.zoom);
		int imgHeight = (int) (this.image.getHeight()*this.zoom);
		
		this.imageComponent.setPreferredSize(
				new Dimension(imgWidth+20, imgHeight+20));
		
		this.imageComponent.revalidate();
		this.imageComponent.repaint();
	}

	/*************************************************************************
	 * Decode an RGB encoded image.
	 * @param data the stream to read from.
	 * @return the decoded image.
	 * @throws IOException on error.
	 ************************************************************************/
	
	private BufferedImage decodeRGBImage(PRStream data) throws IOException {
		// Check the stream and see if it is DCT or CCIT encoded 
		try {
			System.out.println("Try to load image."); //$NON-NLS-1$
			Image pdfImg = Image.getInstance(PdfReader.getStreamBytesRaw(data));
			int type = pdfImg.getOriginalType();
			System.out.println("Type: = "+ type); //$NON-NLS-1$
			ImageInputStream source = new MemoryCacheImageInputStream(new ByteArrayInputStream(PdfReader.getStreamBytesRaw(data)));
			Iterator<ImageReader> readers = ImageIO.getImageReaders(source);
			if (readers.hasNext()) {
				System.out.println("found reader"); //$NON-NLS-1$
				ImageReader reader = readers.next();
				reader.setInput(source);
				BufferedImage result = reader.read(0);
				reader.dispose();
				return result;
			}
		} catch (Exception e) {
			// Trial and error.	
			System.out.println("No reader found."); //$NON-NLS-1$
		}

		// Fall back to raw decoding.
		return decodeRawImage(data);
	}

	/*************************************************************************
	 * Tries to decode the image as raw image data.
	 * @param data the stream to read the pixel data from.
	 * @return an image.
	 * @throws IOException on read error.
	 ************************************************************************/
	
	private BufferedImage decodeRawImage(PRStream data) throws IOException {
		byte[] bArr = PdfReader.getStreamBytes(data);
		PdfNumber imgWidth = data.getAsNumber(PdfName.WIDTH);
		PdfNumber imgHeight = data.getAsNumber(PdfName.HEIGHT);
		// PdfNumber imgBits = data.getAsNumber(PdfName.BITSPERCOMPONENT);
		
		ComponentColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), 
				false, false, ComponentColorModel.OPAQUE, 
				DataBuffer.TYPE_BYTE);
		
		SampleModel sampleModel = colorModel.createCompatibleSampleModel(
				imgWidth.intValue(), imgHeight.intValue());
		
		WritableRaster raster = Raster.createWritableRaster(
				sampleModel, 
				new DataBufferByte(bArr, bArr.length), new Point(0, 0));
		
		BufferedImage img = new BufferedImage(
					colorModel, 
					raster, 
					true, 
					new Hashtable<String, Object>()
				);
		return img;
	}
	
}