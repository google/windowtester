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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.pde.core.plugin.IPluginModelBase;

import com.windowtester.eclipse.ui.UiPlugin;

/**
 * Validator for recorder launch configs.
 */
public class RecorderLaunchValidator {

	
	public IStatus validate(ILaunchConfiguration configuration) {
		try {
			String[] missingRequirements = collectMissingRequirements(configuration);
			if (missingRequirements.length > 0)
				return status(IStatus.ERROR, UiPlugin.PLUGIN_ID, unsatisfiedRequirementsString(missingRequirements));
		} catch (CoreException e) {
			return status(IStatus.WARNING, UiPlugin.PLUGIN_ID, "unable to validate requirements", e);
		}
		return Status.OK_STATUS;
	}


	private static String unsatisfiedRequirementsString(String[] missingRequirements) {
		String msg = "Missing required bundles: [";
		for (int i = 0; i < missingRequirements.length; i++) {
			msg += " " + missingRequirements[i];
		}
		msg +=" ]";
		return msg;
	}


	private static String[] collectMissingRequirements(ILaunchConfiguration configuration) throws CoreException {
		IPluginModelBase[] plugins = getPluginList(configuration);
		//TODO: consider cooking up a more performant lookup
		//whoah -- this is expensive!!!  (wrap in runnable and do progress monitoring)
		//a la org.eclipse.pde.internal.core.BundleValidationOperation
		return RecorderBundleRequirements.getUnsatisfied(plugins); 
	}
	
	private static IPluginModelBase[] getPluginList(ILaunchConfiguration configuration) throws CoreException {
		/* $if eclipse.version < 3.5 $
		return org.eclipse.pde.internal.ui.launcher.LaunchPluginValidator.getPluginList(configuration);
		$elseif eclipse.version == 3.5 $ 
		java.util.Map mergedBundleMap = org.eclipse.pde.internal.ui.launcher.BundleLauncherHelper.getMergedBundleMap(configuration, false);
		return (IPluginModelBase[]) mergedBundleMap.keySet().toArray(new IPluginModelBase[mergedBundleMap.size()]);
		/* $else $ */
		java.util.Map mergedBundleMap = org.eclipse.pde.internal.launching.launcher.BundleLauncherHelper.getMergedBundleMap(configuration, false);
		return (IPluginModelBase[]) mergedBundleMap.keySet().toArray(new IPluginModelBase[mergedBundleMap.size()]);
	/*	$endif$ */
	}
	

	//these convenience methods essentially back-port convenience constructors introduced in 3.3
	private static IStatus status(int severity, String pluginId, String msg) {
		return status(severity, pluginId, msg, null);
	}

	private static IStatus status(int severity, String pluginId, String msg, Throwable throwable) {
		return new Status(severity, pluginId, IStatus.OK, msg, throwable);
	}

}
