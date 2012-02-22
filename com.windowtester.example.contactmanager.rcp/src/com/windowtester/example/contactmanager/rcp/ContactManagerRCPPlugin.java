/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *  
 *******************************************************************************/

package com.windowtester.example.contactmanager.rcp;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.windowtester.example.contactmanager.rcp.model.Contact;
import com.windowtester.example.contactmanager.rcp.model.ContactsManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class ContactManagerRCPPlugin extends AbstractUIPlugin
{
	/**
	 * The shared instance.
	 */
	private static ContactManagerRCPPlugin plugin;

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		ContactsManager.getManager().saveContacts();
		Contact.disposeColors();
		plugin = null;
		super.stop(context);
	}

	public static void logError(String errMsg, Exception exception) {
		getDefault().getLog().log(new Status(
			IStatus.ERROR, 
			getDefault().getBundle().getSymbolicName(), 
			errMsg,
			exception));
	}

	/**
	 * Returns the shared instance.
	 */
	public static ContactManagerRCPPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(getDefault().getBundle().getSymbolicName(), path);
	}
}
