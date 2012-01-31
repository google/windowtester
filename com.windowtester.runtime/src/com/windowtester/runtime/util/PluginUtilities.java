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
package com.windowtester.runtime.util;

import org.eclipse.core.runtime.*;
/* $codepro.preprocessor.if version >= 3.0 $ */
import org.osgi.framework.*;
/* $codepro.preprocessor.endif $ */
import java.net.*;

/**
 * The class <code>PluginUtilities</code> defines utility methods for working
 * with plug-ins.
 * <p>
 * 
 * @author Brian Wilkerson
 * @version $Revision$
 */
public class PluginUtilities
{
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevent the creation of instances of this class.
	 */
	private PluginUtilities()
	{
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// General Accessing
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Return the unique identifier of the given plug-in.
	 *
	 * @return the unique identifier of the given plug-in
	 */
	public static String getId(Plugin plugin)
	{
		/* $codepro.preprocessor.if version >= 3.0 $ */
		return plugin.getBundle().getSymbolicName();
		/* $codepro.preprocessor.elseif version < 3.0 $
		return plugin.getDescriptor().getUniqueIdentifier();
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return the name of the given plug-in. If the plug-in does not have a
	 * name, return the unique identifier for the plug-in instead.
	 *
	 * @return the name of the given plug-in
	 */
	public static String getName(Plugin plugin)
	{
		String label;
		Object bundleName;

		label = null;
		/* $codepro.preprocessor.if version >= 3.0 $ */
		bundleName = plugin.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_NAME);
		if (bundleName instanceof String) {
			label = (String) bundleName;
		}
		/* $codepro.preprocessor.elseif version < 3.0 $
		label = plugin.getDescriptor().getLabel();
		$codepro.preprocessor.endif $ */
		if (label == null || label.trim().length() == 0) {
			return getId(plugin);
		}
		return label;
	}

	/**
	 * Return the version identifier associated with the plug-in with the given
	 * identifier, or <code>null</code> if there is no such plug-in.
	 *
	 * @param pluginId the identifier of the plug-in
	 *
	 * @return the version identifier for the specified plug-in
	 */
	public static PluginVersionIdentifier getVersion(String pluginId)
	{
		/* $codepro.preprocessor.if version >= 3.0 $ */
		Bundle bundle;
		String version;

		bundle = Platform.getBundle(pluginId);
		if (bundle == null) {
			return null;
		}
		version = (String) bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		return new PluginVersionIdentifier(version); 
		/* $codepro.preprocessor.elseif version < 3.0 $
		Plugin plugin;

		plugin = Platform.getPlugin(pluginId);
		if (plugin == null) {
			return null;
		}
		return plugin.getDescriptor().getVersionIdentifier();
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return the version identifier associated with the plug-in with the given
	 * identifier, or <code>null</code> if there is no such plug-in.
	 *
	 * @param pluginId the identifier of the plug-in
	 *
	 * @return the version identifier for the specified plug-in
	 */
	public static PluginVersionIdentifier getVersion(Plugin plugin)
	{
		/* $codepro.preprocessor.if version >= 3.0 $ */
		String version;

		if (plugin == null) {
			return null;
		}
		version = (String) plugin.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		return new PluginVersionIdentifier(version); 
		/* $codepro.preprocessor.elseif version < 3.0 $
		if (plugin == null) {
			return null;
		}
		return plugin.getDescriptor().getVersionIdentifier();
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return the version identifier associated with the plug-in with the given
	 * identifier, or <code>null</code> if there is no such plug-in.
	 *
	 * @param pluginId the identifier of the plug-in
	 *
	 * @return the version identifier for the specified plug-in
	 */
	public static String getVersionString(String pluginId)
	{
		/* $codepro.preprocessor.if version >= 3.0 $ */
		Bundle bundle;

		bundle = Platform.getBundle(pluginId);
		if (bundle == null) {
			return null;
		}
		return (String) bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		/* $codepro.preprocessor.elseif version < 3.0 $
		Plugin plugin;

		plugin = Platform.getPlugin(pluginId);
		if (plugin == null) {
			return null;
		}
		return plugin.getDescriptor().getVersionIdentifier().toString();
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return the version identifier associated with the plug-in with the given
	 * identifier, or <code>null</code> if there is no such plug-in.
	 *
	 * @param pluginId the identifier of the plug-in
	 *
	 * @return the version identifier for the specified plug-in
	 */
	public static String getVersionString(Plugin plugin)
	{
		if (plugin == null) {
			return null;
		}
		/* $codepro.preprocessor.if version >= 3.0 $ */
		return (String) plugin.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		/* $codepro.preprocessor.elseif version < 3.0 $
		return plugin.getDescriptor().getVersionIdentifier().toString();
		$codepro.preprocessor.endif $ */
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// File Accessing
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Return an URL representing the installation directory of the plug-in with
	 * the given identifier, or <code>null</code> if there is no plug-in with
	 * the given identifier.
	 *
	 * @param pluginId the identifier of the plug-in
	 *
	 * @return the specified plug-in's installation directory
	 */
	public static URL getInstallUrl(String pluginId)
	{
		/* $codepro.preprocessor.if version >= 3.0 $ */
		Bundle bundle;

		bundle = Platform.getBundle(pluginId);
		if (bundle == null) {
			return null;
		}
		return bundle.getEntry("/");
		/* $codepro.preprocessor.elseif version < 3.0 $
		IPluginDescriptor descriptor;

		descriptor = Platform.getPluginRegistry().getPluginDescriptor(pluginId);
		if (descriptor == null) {
			return null;
		}
		return descriptor.getInstallURL();
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return an URL representing the given plug-in's installation directory.
	 *
	 * @param plugin the plug-in
	 *
	 * @return the given plug-in's installation directory
	 */
	public static URL getInstallUrl(Plugin plugin)
	{
		if (plugin == null) {
			return null;
		}
		/* $codepro.preprocessor.if version >= 3.0 $ */
		return plugin.getBundle().getEntry("/");
		/* $codepro.preprocessor.elseif version < 3.0 $
		return plugin.getDescriptor().getInstallURL();
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return an URL for the file located within the installation directory of
	 * the plug-in that has the given identifier that has the given relative path.
	 *
	 * @param pluginId the identifier for the plug-in
	 * @param relativePath the relative path of the file within the installation
	 *        directory
	 *
	 * @return the URL for the specified file
	 */
	public static URL getUrl(String pluginId, String relativePath)
	{
		/* $codepro.preprocessor.if version >= 3.0 $ */
		Bundle bundle;

		if (pluginId == null || relativePath == null) {
			return null;
		}
		bundle = Platform.getBundle(pluginId);
		if (bundle != null) {
			return bundle.getEntry(relativePath);
		}
		return null;
		/* $codepro.preprocessor.elseif version < 3.0 $
		IPluginDescriptor descriptor;

		if (pluginId == null || relativePath == null) {
			return null;
		}
		descriptor = Platform.getPluginRegistry().getPluginDescriptor(pluginId);
		if (descriptor != null) {
			return descriptor.find(new Path(relativePath));
		}
		return null;
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Return an URL for the file located within the installation directory of
	 * the given plug-in that has the given relative path.
	 *
	 * @param pluginId the identifier for the plug-in
	 * @param relativePath the relative path of the file within the installation
	 *        directory
	 *
	 * @return the URL for the specified file
	 */
	public static URL getUrl(Plugin plugin, String relativePath)
	{
		if (plugin == null || relativePath == null) {
			return null;
		}
		/* $codepro.preprocessor.if version >= 3.0 $ */
		return plugin.getBundle().getEntry(relativePath);
		/* $codepro.preprocessor.elseif version < 3.0 $
		return plugin.find(new Path(relativePath));
		$codepro.preprocessor.endif $ */
	}
}