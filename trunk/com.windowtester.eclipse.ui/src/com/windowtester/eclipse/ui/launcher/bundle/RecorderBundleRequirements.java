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
package com.windowtester.eclipse.ui.launcher.bundle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.pde.core.plugin.IPluginModelBase;

import com.windowtester.eclipse.ui.target.RequiredPlugins;

/**
 * A specification of bundles required for recording.
 */
public class RecorderBundleRequirements {


	public static String[] getUnsatisfied(IPluginModelBase[] plugins) {
		Set<String> requiredPlugins = getRequiredPlugins();
		for (int i = 0; i < plugins.length; i++) {
			requiredPlugins.remove(getId(plugins[i]));
		}
		return requiredPlugins.toArray(new String[]{});
	}


	private static String getId(IPluginModelBase plugin) {
		return plugin.getPluginBase().getId();
	}

	
	private static Set<String> getRequiredPlugins() {
		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList(RequiredPlugins.RECORDING));
		set.addAll(Arrays.asList(RequiredPlugins.RUNTIME));
		set.addAll(Arrays.asList(RequiredPlugins.RUNTIME_DEPENDENCIES));
		return set;
	}


	public static String[] getRequiredPluginIds() {
		return getRequiredPlugins().toArray(new String[]{});
	}

	
//	public static ILaunchConfiguration addTo(ILaunchConfiguration configuration) {		
//		return BundleRequirementUpdater.updateToInclude(configuration, getRequiredPluginIds());
//	}

}
