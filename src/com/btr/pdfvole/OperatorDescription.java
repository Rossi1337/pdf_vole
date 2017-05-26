package com.btr.pdfvole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

/*****************************************************************************
 * PDF operator description. 
 * Loads the PDF operator description from a file and builds a lookup table
 * for this. An OperatorDescription consists of a operator, a PS name and a 
 * description string explaining the operators purpose.
 *
 * @author  Bernd Rosstauscher (pdfvole@rosstauscher.de)
 ****************************************************************************/

public class OperatorDescription {
	
	private static final HashMap<String, OperatorDescription> cache = new HashMap<String, OperatorDescription>();
	private static boolean loaded = false;
	
	private String operator;
	private String psName;
	private String description;
	
	/*************************************************************************
	 * Constructor
	 * @param op
	 * @param ps
	 * @param desc
	 ************************************************************************/
	
	private OperatorDescription(String op, String ps, String desc) {
		super();
		this.operator = op;
		this.psName = ps;
		this.description = desc;
	}

	/*************************************************************************
	 * hashCode
	 * @see java.lang.Object#hashCode()
	 ************************************************************************/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result
				+ ((this.operator == null) ? 0 : this.operator.hashCode());
		result = prime * result + ((this.psName == null) ? 0 : this.psName.hashCode());
		return result;
	}

	/*************************************************************************
	 * equals
	 * @see java.lang.Object#equals(java.lang.Object)
	 ************************************************************************/
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		OperatorDescription other = (OperatorDescription) obj;
		if (this.operator == null) {
			if (other.operator != null)
				return false;
		} else if (!this.operator.equals(other.operator))
			return false;

		return true;
	}

	/*************************************************************************
	 * @return Returns the operator.
	 ************************************************************************/
	
	public String getOperator() {
		return this.operator;
	}

	/*************************************************************************
	 * @return Returns the psName.
	 ************************************************************************/
	
	public String getPsName() {
		return this.psName;
	}

	/*************************************************************************
	 * @return Returns the description.
	 ************************************************************************/
	
	public String getDescription() {
		return this.description;
	}
	
	/*************************************************************************
	 * Loads the cache.
	 ************************************************************************/
	
	public static void loadCache() {
		loaded = true;
		cache.clear();
		try {
			URL file = ResourceManager.getOperatorFile();
			BufferedReader fin = new BufferedReader(new InputStreamReader(file.openStream()));
			try {
				String line = fin.readLine();
				while (line != null) {
					if (line.trim().length() > 0) {
						String[] parts = line.split("\t"); //$NON-NLS-1$
						OperatorDescription entry = new OperatorDescription(parts[0], parts[1], parts[2]);
						cache.put(entry.getOperator(), entry);
					}
					line = fin.readLine();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				fin.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*************************************************************************
	 * Gets an operator description for the given operator name. 
	 * @param op the operator.
	 * @return the description object, null if not found.
	 ************************************************************************/
	
	public static OperatorDescription get(String op) {
		if (!loaded) {
			loadCache();
		}
		return cache.get(op);
	}
	
	
	
	
	

}
