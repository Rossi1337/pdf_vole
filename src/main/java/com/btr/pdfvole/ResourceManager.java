package com.btr.pdfvole;

import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import org.jdesktop.application.Application;


/*****************************************************************************
 * Central class to manage all kind of resource loading.
 * Has helper methods to load icons, externalized strings, ...
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class ResourceManager {
	
	// Some constants
	private static final String HOMEPAGE_URL = "https://github.com/Rossi1337/pdf_vole"; //$NON-NLS-1$
	private static final String PDF_OP_DESC = "/com/btr/pdfvole/resources/operator.txt";  //$NON-NLS-1$ 

	private static File lastFile;
		
	/*************************************************************************
	 * Load an icon for the given name.
	 * @param name the name of the icon to load.
	 * @return an Icon.
	 ************************************************************************/
	
	public static Icon getIcon(String name) {
		return Application.getInstance().getContext().getResourceMap().getIcon(name);
	}
	
	/*************************************************************************
	 * Gets a string from the resources file.
	 * @param key the key to identify the string resource.
	 * @param vars the variables for the message formatting.
	 * @return a string.
	 ************************************************************************/
	
	public static String getString(String key, Object ...vars) {
		return 	Application.getInstance().getContext().getResourceMap().getString(key, vars);
	}
	
	public static URL getOperatorFile() {
		URL path = ResourceManager.class.getResource(PDF_OP_DESC);
		return path;
	}

	/*************************************************************************
	 * Sets the last file that was opened.
	 * This will be remembered for the next time the user goes to the open dialog.
	 * @param pdfFile the file that was opened.
	 ************************************************************************/
	
	public static void setLastOpenedFile(File pdfFile) {
		if (pdfFile != null) {
			ResourceManager.lastFile = pdfFile.getParentFile();
		}
	}
	
	/*************************************************************************
	 * Gets the file of the last open action of the user. 
	 * @return a File, null if not set.
	 ************************************************************************/
	
	public static File getLastOpenedFile() {
		return ResourceManager.lastFile;
	}
	
	/*************************************************************************
	 * Gets the URL of the project's home page.
	 * @return a URL as String.
	 ************************************************************************/
	
	public static String getHomepageURL() {
		return HOMEPAGE_URL;
	}
	

}
