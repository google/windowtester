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
package com.windowtester.codegen;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.swtdesigner.ResourceManager;
import com.windowtester.internal.debug.Tracer;

/**
 * The main codegen plugin.
 */
public class CodeGenPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static CodeGenPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private Clipboard clipboard;
	
	/** A string identifying this plugin */
	static public final String PLUGIN_ID = "com.windowtester.swt.codegen";
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Tracer.trace(ICodeGenPluginTraceOptions.BASIC, "creating codegen plugin");
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("com.windowtester.codegen.CodeGenPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}		
		//initImageManager();
		Tracer.trace(ICodeGenPluginTraceOptions.BASIC, "starting codegen plugin");
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		disposeClipboard();
		ResourceManager.dispose();
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static CodeGenPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CodeGenPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Get the String identifier for this plugin.
	 * @return the plugin id
	 */
	public static String getPluginId() {
		return PLUGIN_ID;
	}

	/**
	 * Answer the cached clipboard
	 * @return the clipboard (not <code>null</code>)
	 */
	public Clipboard getClipboard() {
		if (clipboard == null)
			clipboard = new Clipboard(Display.getDefault());
		return clipboard;
	}
	
	/**
	 * Dispose of the cached clipboard if necessary
	 */
	public void disposeClipboard() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
	}
}
