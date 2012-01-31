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
package com.windowtester.runtime.gef.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * GEF plugin.
 */
public class WTGEFPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.windowtester.swt.runtime.gef";
	
	private static WTGEFPlugin instance;
	
	
	/**
	 * Returns the singleton instance of the console plug-in.
	 */
	public static WTGEFPlugin getDefault() {
		return instance;
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
	
	public void start(org.osgi.framework.BundleContext context) throws Exception {
        super.start(context);
		instance = this;
	}
}
