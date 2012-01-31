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
package com.windowtester.runtime.swt.internal.finder.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.windowtester.runtime.swt.internal.debug.LogHandler;

/**
 * A basic {@link com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewRegistry} implementation.
 */
public class ViewRegistry implements IViewRegistry {

	/* Backing store */
	private final List<IViewHandle> views = new ArrayList<IViewHandle>();
	
	/* Singleton instance */
	private static final ViewRegistry INSTANCE = new ViewRegistry();
	
	/*
	 * It's not clear who should do this registering (and where).
	 * For now, keeping it simple and inline.
	 */
	{
		registerEclipseViews();
	}
	
	/**
	 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewRegistry#register(com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewHandle)
	 */
	public void register(IViewHandle view) {
		views.add(view);
	}

	/**
	 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewRegistry#get()
	 */
	public IViewHandle[] get() {
		return views.toArray(new IViewHandle[]{});
	}

	/**
	 * Get the default view registry instance.
	 */
	public static IViewRegistry getDefault() {
		//TODO: singleton or not?  who should own the reference?
		return INSTANCE;
	}


	/**
	 * Find and register all views contributed to the platform.
	 */
	private void registerEclipseViews() {
		
		//if we're not running in the platform, don't try and visit the extension registry
		if (!com.windowtester.internal.runtime.Platform.isRunning()) {
			return;
		}
		
		//TODO should we prune out "internal" views?
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("org.eclipse.ui.views");
		if (point == null)  {
			LogHandler.log("unable to retrieve org.eclipse.ui.views extension point for view registration");
			return;
		}
		
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
				IExtension extension = extensions[i];
				IConfigurationElement[] allElements = extension.getConfigurationElements();
				for (int j = 0; j < allElements.length; j++) {
					IConfigurationElement element = allElements[j];
					if (element.getName().equals("view")) {
						String id = element.getAttribute("id");
						if (id != null)
							register(new ViewHandle(id));
					}
				}
		}
	}
	
}
