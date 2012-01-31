/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.eclipse.ui.convert.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extract key/value pairs from a META-INF/MANIFEST.MF file.
 */
public class BundleManifestReader extends LineBasedPreprocessor
{
	public BundleManifestReader() {
		lineProcessors.add(new BundleManifestLineParser());
	}

	private Map<String, String> values = new HashMap<String, String>();

	/**
	 * Answer the values associated with the specified key
	 * 
	 * @param key the key (e.g. "Bundle-Name")
	 * @return the trimmed value or null if the key is not found
	 */
	public String get(String key) {
		return get(key, null);
	}

	/**
	 * Answer the values associated with the specified key
	 * 
	 * @param key the key (e.g. "Bundle-Name")
	 * @return the trimmed value or the default value if the key is not found
	 */
	public String get(String key, String defaultValue) {
		String result = values.get(key);
		if (result == null)
			return defaultValue;
		return result.trim();
	}

	/**
	 * Answer the values associated with the specified key.
	 * 
	 * @param key the key (e.g. "Bundle-Name")
	 * @return the values with whitespace trimmed or null if the key is not found.
	 */
	public List<String> getList(String key) {
		return getList(key, null);
	}

	/**
	 * Answer the values associated with the specified key.
	 * 
	 * @param key the key (e.g. "Bundle-Name")
	 * @return the values with whitespace trimmed or the default value if the key is not found.
	 */
	public List<String> getList(String key, List<String> defaultValue) {
		String value = get(key);
		if (value == null)
			return defaultValue;
		ArrayList<String> result = new ArrayList<String>();
		if (value.length() > 0) {
			
			// A comma may be embedded in between double quotes
			// e.g. org.eclipse.swt;visibility:=reexport;bundle-version="[3.2.0,4.0.0)"
			// so we cannot use String[] values = value.split(",");
			
			int start = 0;
			int end = 0;
			int len = value.length();
			while (end < len) {
				char ch = value.charAt(end);
				if (ch == ',') {
					result.add(value.substring(start, end).trim());
					start = end + 1;
				}
				
				// Skip from leading quote to trailing quote
				else if (ch == '"') {
					end++;
					while (value.charAt(end) != '"')
						end++;
				}
				
				end++;
			}
			result.add(value.substring(start, end).trim());
		}
		return result;
	}

	/**
	 * Answer the plug-in identifier
	 */
	public String getId() {
		String id = get("Bundle-SymbolicName");
		int index = id.indexOf(';');
		if (index > 0)
			id = id.substring(0, index);
		return id.trim();
	}
	
	/**
	 * Answer the plugin version
	 */
	public String getVersion() {
		return get("Bundle-Version");
	}
	
	/**
	 * Answer the plugin activator
	 */
	public String getActivator() {
		return get("Bundle-Activator");
	}
	
	/**
	 * Answer the identifiers of the required plug-ins
	 */
	public List<String> getRequiredPlugins() {
		List<String> result = getList("Require-Bundle");
		// Also handle fragments
		if (result == null)
			result = getList("Fragment-Host");
		if (result == null)
			return new ArrayList<String>();
		for (int i = 0; i < result.size(); i++) {
			String id = result.get(i);
			int index = id.indexOf(';');
			if (index > 0)
				result.set(i, id.substring(0, index).trim());
		}
		return result;
	}

	/**
	 * Answer <code>true</code> if the manifest specifies a fragment
	 */
	public boolean isFragment() {
		return get("Fragment-Host") != null;
	}

	/**
	 * Replace a source pattern with the replacement text
	 */
	private class BundleManifestLineParser
		implements LineProcessor
	{
		private String tag;

		public void reset() {
		}

		public String process(String line) {
			int index = line.indexOf(':');
			if (index > 0 && (index + 1 == line.length() || line.charAt(index + 1) != '=')) {
				tag = line.substring(0, index).trim();
				values.put(tag, line.substring(index + 1).trim());
			}
			else {
				values.put(tag, values.get(tag) + line.trim());
			}
			return line;
		}
	}
}
