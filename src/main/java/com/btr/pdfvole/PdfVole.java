package com.btr.pdfvole;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.jxlayer.JXLayer;

import com.btr.pdfvole.FileDnDSupport.FileDropListener;
import com.btr.pdfvole.tasks.LoadFileTask;
import com.btr.pdfvole.tree.AbstractPdfTreeNode;
import com.btr.pdfvole.tree.RefTableTreeNode;
import com.btr.pdfvole.tree.ReferenceTreeNode;
import com.btr.pdfvole.tree.TreeCellRenderer;
import com.btr.pdfvole.tree.TreeExpansionMonitor;
import com.btr.pdfvole.tree.TreeMouseListener;
import com.btr.pdfvole.tree.TreeSelectionAdapter;
import com.btr.pdfvole.viewer.InitialViewer;
import com.lowagie.text.pdf.PRIndirectReference;


/*****************************************************************************
 * PDF Debugger main class.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class PdfVole extends SingleFrameApplication implements
		INodeViewer, ActionListener, FileDropListener {

	// Some action commands
	private static final String ACTION_LNF_PREFIX 	= "LNF "; //$NON-NLS-1$

	public static BusyUI busy;
	
	private JPanel nodeContentPanel;
	private JTree pdfTree;
	
	private File pdfFile;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/

	public PdfVole() {
		super();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionDialog());
	}

	@Override
	protected void startup() {
		var topLevel = getMainTopLevel();
		configureRootPane(topLevel.getRootPane());
		configureTopLevel(topLevel);
		topLevel.setVisible(true);
	}

	private void configureRootPane(JRootPane rootPane) {
		JMenuBar menuBar = createJMenuBar();
		rootPane.setJMenuBar(menuBar);

		Component mainComponent = createMainComponent();
		rootPane.getContentPane().add(mainComponent);
	}

	/*************************************************************************
	 * Main entry point for the application.
	 * @param args command line arguments.
	 ************************************************************************/
	
	public static void main(String[] args) {
		launch(PdfVole.class, args);
	}
	

	/*************************************************************************
	 * createJMenuBar
	 ************************************************************************/
	protected JMenuBar createJMenuBar() {
		ApplicationActionMap actions = getContext().getActionMap();

		
		// --- Add File Menu ----

		JMenu fileMenu = new JMenu("File");
		fileMenu.setName("fileMenu"); //$NON-NLS-1$

		fileMenu.add(actions.get("open")); //$NON-NLS-1$
		fileMenu.addSeparator();
		fileMenu.add(actions.get("quit")); //$NON-NLS-1$

		// --- Add LnF Menu ----
		JMenu lnfMenu = new JMenu("Look & Feel");
		lnfMenu.setName("lnfMenu"); //$NON-NLS-1$
		
		LookAndFeelInfo[] lnf = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo lookAndFeelInfo : lnf) {
			JMenuItem lnfItem = new JMenuItem(lookAndFeelInfo.getName());
			lnfItem.setActionCommand(ACTION_LNF_PREFIX+lookAndFeelInfo.getClassName());
			lnfItem.addActionListener(this);
			lnfMenu.add(lnfItem);
		}

		// --- About Menu ----
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setName("helpMenu"); //$NON-NLS-1$
		helpMenu.add(actions.get("about")); //$NON-NLS-1$
		helpMenu.add(actions.get("visitHomepage")); //$NON-NLS-1$
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(lnfMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	/*************************************************************************
	 * createMainComponent
	 ************************************************************************/
	protected Component createMainComponent() {
		
		this.pdfTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		this.pdfTree.addTreeSelectionListener(new TreeSelectionAdapter(this));
		this.pdfTree.setCellRenderer(new TreeCellRenderer());
		this.pdfTree.addTreeWillExpandListener(new TreeExpansionMonitor());
		this.pdfTree.addMouseListener(new TreeMouseListener(this));
		this.pdfTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.pdfTree.setRootVisible(false);

		this.nodeContentPanel = new JPanel();
		this.nodeContentPanel.setLayout(new BorderLayout());
		this.nodeContentPanel.add(new InitialViewer(), BorderLayout.CENTER);

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				new JScrollPane(this.pdfTree), this.nodeContentPanel);
		mainSplitPane.setMinimumSize(new Dimension(150, 100));
		mainSplitPane.setDividerLocation(200);
		
		
		// Build tabbed pane
		JTabbedPane mainTab = new JTabbedPane();
		mainTab.addTab(ResourceManager.getString("mainTab.tab1"), mainSplitPane); //$NON-NLS-1$
		
		// Install busy layer 
		PdfVole.busy = new BusyUI();
		JXLayer<JComponent> layer = new JXLayer<JComponent>(mainTab, busy);   
		
		// Enable drag and drop for PDF files.
		FileDnDSupport dndSupport = new FileDnDSupport();
		dndSupport.addFileFilter(new PdfFileFilter(false));
		dndSupport.addDropTarget(layer);
		dndSupport.addDropListener(this);
		
		return layer;
	}
	
	/*************************************************************************
	 * Shows the tree popup menu.
	 * @param source the trigger component. 
	 * @param x the x position of the trigger point
	 * @param y the y position of the trigger point
	 ************************************************************************/
	
	public void showPopupMenu(Component source, int x, int y) {
		ApplicationActionMap actions = getContext().getActionMap();

		JPopupMenu popup = new JPopupMenu();
		popup.add(actions.get("collapseAll")); //$NON-NLS-1$
		popup.add(actions.get("expandAll")); //$NON-NLS-1$
		
		// Some special entries for special tree nodes.
		if (this.pdfTree.getSelectionPath() != null && 
			this.pdfTree.getSelectionPath().getLastPathComponent() instanceof ReferenceTreeNode) {
			popup.addSeparator();
			popup.add(actions.get("jumpToObject")); //$NON-NLS-1$
		}
		popup.show(source, x, y);
	}

	/*************************************************************************
	 * configureTopLevel
	 ************************************************************************/
	protected void configureTopLevel(JFrame mainFrame) {
		var dim = new Dimension(900, 640);
		mainFrame.setPreferredSize(dim);
		mainFrame.setSize(dim);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/*************************************************************************
	 * Changes the L&F
	 ************************************************************************/
	
	private void changeLnF(String className) {
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(getMainTopLevel());

			// Fix for tree need to create new renderer to flush cached UI values. 
			this.pdfTree.setCellRenderer(new TreeCellRenderer());
			
			this.getMainTopLevel().pack();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*************************************************************************
	 * Load a PDF file into the debugger.
	 ************************************************************************/

	private void loadPdfFile() {
		PdfVole.busy.setLocked(true);
		
		LoadFileTask task = new LoadFileTask(this, this.pdfFile, null);
		task.execute();
	}
	
	/*************************************************************************
	 * Sets a new tree model for the PDF tree.
	 * @param newModel a new TreeModel.
	 ************************************************************************/
	
	public void setNewModel(TreeModel newModel) {
		PdfVole.this.pdfTree.setModel(newModel);
		PdfVole.this.pdfTree.expandRow(0);
		PdfVole.this.setViewer(new InitialViewer());
	}

	/*************************************************************************
	 * Called when the tree selection changes.
	 * @param e the change event.
	 ************************************************************************/
	
	public void treeSelectionChanged(TreeSelectionEvent e) {
		AbstractPdfTreeNode selectednode = (AbstractPdfTreeNode) this.pdfTree
				.getLastSelectedPathComponent();
		if (selectednode != null) {
			selectednode.updateViewer(this);
		}
	}

	/*************************************************************************
	 * setViewer
	 * @see com.btr.pdfvole.INodeViewer#setViewer(javax.swing.JComponent)
	 ************************************************************************/
	@Override
	public void setViewer(JComponent content) {
		this.nodeContentPanel.removeAll();
		this.nodeContentPanel.add(content, BorderLayout.CENTER);
		this.nodeContentPanel.revalidate();
	}

	/*************************************************************************
	 * actionPerformed
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 ************************************************************************/
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith(ACTION_LNF_PREFIX)) {
			changeLnF(e.getActionCommand().substring(4));
		}
	}

	/*************************************************************************
	 * Shows the file dialog and opens a selected PDF file.
	 ************************************************************************/
	@Action
	public void open() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setName("fileChooser"); //$NON-NLS-1$
		chooser.setFileFilter(new PdfFileFilter());
		chooser.setCurrentDirectory(ResourceManager.getLastOpenedFile());

		getContext().getResourceMap().injectComponents(chooser);
		
		if (chooser.showOpenDialog(getMainTopLevel()) == JFileChooser.APPROVE_OPTION) {
			this.pdfFile = chooser.getSelectedFile();
			ResourceManager.setLastOpenedFile(this.pdfFile);
			loadPdfFile();
		}
	}
	
	/*************************************************************************
	 * Shows an about dialog.
	 ************************************************************************/
	@Action
	public void about() {
		JOptionPane.showMessageDialog(getMainTopLevel(), 
				ResourceManager.getString("about.msg"),  //$NON-NLS-1$
				ResourceManager.getString("mainFrame.title"),  //$NON-NLS-1$ 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/*************************************************************************
	 * Shows an about dialog.
	 ************************************************************************/
	@Action
	public void visitHomepage() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(ResourceManager.getHomepageURL()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/*************************************************************************
	 * Jump to a referenced object in the global object list.
	 ************************************************************************/
	@Action
	public void jumpToObject() {
		if (this.pdfTree.getSelectionPath() != null && 
				this.pdfTree.getSelectionPath().getLastPathComponent() instanceof ReferenceTreeNode) {
			
			ReferenceTreeNode refNode =	
				(ReferenceTreeNode) this.pdfTree.getSelectionPath().getLastPathComponent();
			
			PRIndirectReference ref = refNode.getReference();
			int no = ref.getNumber();
			
			DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) this.pdfTree.getModel().getRoot();
			Enumeration<?> enume = treeRoot.breadthFirstEnumeration();
			while (enume.hasMoreElements()) {
				DefaultMutableTreeNode node  = (DefaultMutableTreeNode) enume.nextElement();
				if (node instanceof RefTableTreeNode) {
					Enumeration<?> all = node.children();
					while (all.hasMoreElements()) {
						DefaultMutableTreeNode n = (DefaultMutableTreeNode) all.nextElement();
						if (String.valueOf(n.getUserObject()).startsWith(no+".")) { //$NON-NLS-1$
							TreePath nodePath = new TreePath(n.getPath());
							this.pdfTree.expandPath(new TreePath(node.getPath()));
							this.pdfTree.setSelectionPath(nodePath);
							this.pdfTree.scrollPathToVisible(nodePath);
							break;
						}
					}
				}
			}
		}
	}
	
	/*************************************************************************
	 * Collapse all child nodes of the currently selected node.
	 ************************************************************************/
	@Action
	public void collapseAll() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
				this.pdfTree.getSelectionPath().getLastPathComponent();
		
		if (node != null) {
			Enumeration<?> en = node.children();
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
				this.pdfTree.collapsePath(new TreePath(child.getPath()));
			}
		}
	}
	
	/*************************************************************************
	 * Expand all child nodes of the currently selected node.
	 * Only the next level is expanded. This is not a "deep expand". 
	 ************************************************************************/
	@Action
	public void expandAll() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
		this.pdfTree.getSelectionPath().getLastPathComponent();
		
		if (node != null) {
			Enumeration<?> en = node.children();
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
				this.pdfTree.expandPath(new TreePath(child.getPath()));
			}
		}
	}

	/*************************************************************************
	 * Files are dropped onto the GUI.
	 * @param fileList the list containing the dropped files.
	 ************************************************************************/
	@Override
	public void filesDropped(List<File> fileList) {
		if (fileList.size() > 0) {
			this.pdfFile = fileList.get(0);
			loadPdfFile();
		}
		
	}

	// Helpers
	public JFrame getMainTopLevel() {
		return super.getMainFrame();
	}

}
