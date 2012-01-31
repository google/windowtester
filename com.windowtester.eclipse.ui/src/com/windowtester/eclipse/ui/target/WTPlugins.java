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

import java.util.ArrayList;
import java.util.List;

/**
 * Manages required bundle info for target provisioning.
 */
public class WTPlugins {

	
	private final List<String> pluginIds = new ArrayList<String>();

	public static WTPlugins forRuntime() {
		return new WTPlugins().addAllPluginIds(RequiredPlugins.RUNTIME);
	}
	
	
	public WTPlugins addAllPluginIds(String[] runtime) {
		WTPlugins wtPlugins = new WTPlugins();
		String[] idsToAdd = RequiredPlugins.RUNTIME;
		for (int i = 0; i < idsToAdd.length; i++) {
			wtPlugins.addPluginId(idsToAdd[i]);
		}
		return wtPlugins;
	}


	public WTPlugins addPluginId(String pluginId) {
		if (!containsPluginId(pluginId))
			doAddPluginId(pluginId);
		return this;
	}


	private boolean doAddPluginId(String pluginId) {
		return pluginIds.add(pluginId);
	}


	public boolean containsPluginId(String pluginId) {
		return pluginIds.contains(pluginId);
	}


	public String[] bundleIds() {
		return pluginIds.toArray(new String[]{});	
	}
	
	
}
