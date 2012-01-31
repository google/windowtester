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
package com.windowtester.eclipse.ui;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.windowtester.codegen.ui.controller.RecorderUIImages;
import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.eclipse.ui.session.SessionMonitor;
import com.windowtester.ui.util.Logger;

/**
 * The main plugin class to be used in the desktop.
 */
public class UiPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "com.windowtester.eclipse.ui";
	/**
	 * The unique identifier for this product
	 */
	public static final String PRODUCT_ID = "com.google.windowtester";

	public static final String RECORDER_VIEW_ID = "com.windowtester.eclipse.ui.recorder.console";

	
	public static final String SWT_LAUNCH_WORKSPACE = "swt-launch-workspace";
	public static final String ID_RUN_LAUNCH_GROUP = "com.windowtester.ui.recorderLauchGroup";
	public static final String WORKBENCH_LAUNCH_CONFIGURATION_TYPE = "org.eclipse.pde.ui.RuntimeWorkbench";
	public static final String LAUNCH_MODE_RECORD = "record";
	public static final String SWT_BOOTSTRAP_APPLICATION = "com.windowtester.swt.recorder.SwtBootstrapApplication";
	public static final String WINDOWTESTER_RUNTIME_VAR = "WINDOWTESTER_COMMON_RUNTIME_JAR";
	public static final String WINDOWTESTER_SWT_RUNTIME_VAR = "WINDOWTESTER_SWT_RUNTIME_JAR";

	//The shared instance.
	private static UiPlugin plugin;
	
	public static boolean _inRecording = false; //FIXME: this is not accessed?
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	//effectively the singleton session monitor
	private ISessionMonitor _sessionMonitor = new SessionMonitor();

	private ILaunchConfiguration configuration;

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("com.windowtester.eclipse.ui.UiPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		initImageManager();
		startUsageProfiling();
	}

	private void startUsageProfiling() {
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static UiPlugin getDefault() {
		return plugin;
	}

	
	public Image getImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, imageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}
	
	public static ImageDescriptor imageDescriptor(String imageFilePath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = UiPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * @return Returns the inRecoring.
	 */
	public boolean isInRecoring() {
		return _inRecording; 
	}
	
	/**
	 * Initialize the Image Manager
	 */
	private void initImageManager() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					RecorderUIImages.INSTANCE.initialize(new URL(getBundle().getEntry("/"), "icons/full/"), new ImageRegistry());
				} catch (Throwable e) {
					Logger.log("Error initiliazing image manager.", e);
				}
			}
		};
		// Post the initialization to the main thread
		PlatformUI.getWorkbench().getDisplay().asyncExec(runnable);
	}
	
	public void showErrorDialog(final String title, final String message){
		final Display d = PlatformUI.getWorkbench().getDisplay();			
		d.asyncExec(new Runnable(){
			public void run() {
				Shell s = new Shell(d);
				MessageDialog.openError(s, title, message);
				s.dispose();
			}}
		);
	}

	public ISessionMonitor getSessionMonitor() {
		return _sessionMonitor;
	}

	public void sessionStarted(ISession session) {
		getSessionMonitor().sessionStarted(session);
	}

	public void sessionEnded(ISession session) {
		getSessionMonitor().sessionEnded(session);
	}

	public void cacheLaunchConfig(ILaunchConfiguration configuration) {
		this.configuration = configuration;		
	}
	
	public ILaunchConfiguration getCachedLaunchConfig() {
		return configuration;
	}
	
}
