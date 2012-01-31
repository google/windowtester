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
package com.windowtester.eclipse.ui.target;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * An eclipse "target".
 *
 */
public class Target {

    private static final String PLUGINS_GROUP_LABEL = "<plugins>";
	private static final String PLUGIN_ID_LABEL = "plugin id=";
	static String NEW_LINE = System.getProperty("line.separator", "\n"); // $codepro.audit.disable platformSpecificLineSeparator
	
	public static Target fromStream(InputStream stream) {
		return new Target(stream);
	}
	
	private final InputStream stream;

	private String parsedString;
	private String idSpacing;
	
	
	Target(InputStream stream) {
		this.stream = stream;
	}

	public String asString() throws IOException {
		if (parsedString == null)
			parsedString = parseString();
		return parsedString;
	}

	private String parseString() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null) {
			sb.append(line);
			sb.append(NEW_LINE);
		}
		return sb.toString();
	}

	public String[] getPlugins() throws IOException {
		ArrayList<String> plugins = new ArrayList<String>();
		String str = asString();
		int current = 0;
		for ( ; ; ) {
			current = str.indexOf(PLUGIN_ID_LABEL, current)+1; //notice this will make '0' the sentinel
			if (current == 0)
				break;
			//System.out.println(str.indexOf("plugin id=", current));	
			int start = str.indexOf('"', current)+1;
			int stop  = str.indexOf('"', start);
			String plugin = str.substring(start, stop);
			//System.out.println(plugin);
			plugins.add(plugin);
		}
		return plugins.toArray(new String[]{});
	}

	public Target addRecordingPlugins() throws IOException {
		String[] plugins = RequiredPlugins.RECORDING;
		for (int i = 0; i < plugins.length; i++) {
			addPlugin(plugins[i]);
		}
		return this;
	}
	
	public Target addPlugin(String pluginId) throws IOException {
		if (!containsPlugin(pluginId))
			doAddPlugin(pluginId);
		return this;
	}

	private void doAddPlugin(String pluginId) throws IOException {
		String str = asString();
		int lastPluginStart = str.lastIndexOf(PLUGIN_ID_LABEL);		
		int lastPluginEnd   = str.indexOf('>', lastPluginStart) +1;
		//if there are no plugins in this target...
		if (lastPluginStart == -1) {
			lastPluginEnd = str.indexOf(PLUGINS_GROUP_LABEL)+ PLUGINS_GROUP_LABEL.length();
		}
		String firstHalf    = str.substring(0, lastPluginEnd);
		String secondHalf   = str.substring(lastPluginEnd+1, str.length());
		String pluginString = getPluginString(pluginId);
		parsedString        = firstHalf + pluginString + secondHalf; 
	}

	public boolean containsPlugin(String pluginId) throws IOException {
		String[] plugins = getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			if (plugins[i].trim().equals(pluginId))
				return true;
		}
		return false;
	}

	private String getPluginString(String pluginId) throws IOException {
		return NEW_LINE + getIdSpacing() + "<plugin id=\"" + pluginId +"\"/>";
	}

	private String getIdSpacing() throws IOException {
		if (idSpacing == null)
			idSpacing = parseIdSpacing();
		return idSpacing;
	}

	private String parseIdSpacing() throws IOException {
		// TODO we could parse this...
		return "         ";
	}

//	public Target writeToFile(IPath path) throws CoreException, IOException {
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		IWorkspaceRoot root  = workspace.getRoot();
//		IFile file = root.getFileForLocation(path);
//		if (!file.exists()) {
//			byte[] bytes = asString().getBytes();
//			InputStream source = new ByteArrayInputStream(bytes);
//			file.create(source, IResource.NONE, null);
//		}
//		return this;
//	}

	public InputStream toStream() throws IOException {
		byte[] bytes = asString().getBytes();
		return new ByteArrayInputStream(bytes);
	}
	
	
	
}
