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
package com.windowtester.eclipse.ui.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.pde.core.plugin.IPluginModelBase;

/* $if eclipse.version == 3.3 $
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.internal.core.ClasspathHelper;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.TargetPlatformHelper;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.eclipse.pde.internal.core.util.VersionUtil;
import org.eclipse.pde.internal.ui.IPDEUIConstants;

import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.launcher.BundleLauncherHelper;
import org.eclipse.pde.internal.ui.launcher.LaunchArgumentsHelper;
import org.eclipse.pde.internal.ui.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.internal.ui.launcher.LaunchPluginValidator;
import org.eclipse.pde.internal.ui.launcher.LauncherUtils;
$endif$ */
import org.eclipse.pde.internal.ui.PDEPlugin;

import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.osgi.framework.Version;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.ui.util.PreprocessorInvariant;

@SuppressWarnings({ "restriction", "unused", "unchecked" })
public class ProgramArgumentBuilder33 {
	
	private HashSet locations;
	
	public ProgramArgumentBuilder33(HashSet locations){
		this.locations=locations;
	}
	
	String[] getProgramArguments0(ILaunchConfiguration configuration,
			File configDir) throws CoreException {
		
		/* $if eclipse.version == 3.3 $
		
		ArrayList programArgs = new ArrayList();

		// add tracing, if turned on
		if (configuration.getAttribute(IPDELauncherConstants.TRACING, false)
				&& !IPDELauncherConstants.TRACING_NONE.equals(configuration
						.getAttribute(IPDELauncherConstants.TRACING_CHECKED,
								(String) null))) {
			programArgs.add("-debug"); //$NON-NLS-1$
			programArgs.add(LaunchArgumentsHelper.getTracingFileArgument(
					configuration, configDir.toString() + IPath.SEPARATOR
							+ ".options")); //$NON-NLS-1$
		}

		// add the program args specified by the user
		String[] userArgs = LaunchArgumentsHelper
				.getUserProgramArgumentArray(configuration);
		ArrayList userDefined = new ArrayList();
		for (int i = 0; i < userArgs.length; i++) {
			// be forgiving if people have tracing turned on and forgot
			// to remove the -debug from the program args field.
			if (userArgs[i].equals("-debug") && programArgs.contains("-debug")) //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			userDefined.add(userArgs[i]);
		}

		if (!configuration.getAttribute(IPDEUIConstants.APPEND_ARGS_EXPLICITLY,
				false)) {

			if (!userDefined.contains("-os")) { //$NON-NLS-1$
				programArgs.add("-os"); //$NON-NLS-1$
				programArgs.add(TargetPlatform.getOS());
			}
			if (!userDefined.contains("-ws")) { //$NON-NLS-1$
				programArgs.add("-ws"); //$NON-NLS-1$
				programArgs.add(TargetPlatform.getWS());
			}
			if (!userDefined.contains("-arch")) { //$NON-NLS-1$
				programArgs.add("-arch"); //$NON-NLS-1$
				programArgs.add(TargetPlatform.getOSArch());
			}
		}

		if (userDefined.size() > 0) {
			programArgs.addAll(userDefined);
		}

		return (String[]) programArgs.toArray(new String[programArgs.size()]);
		$else$ */
		return (String []) PreprocessorInvariant.violated();		
		/* $endif$ */
	}
	
	

	
	public String[] getProgramArguments(ILaunchConfiguration configuration,
			File configDir, String[] requiredPluginsIds) throws CoreException {
				
		/* $if eclipse.version == 3.3 $
		
		ArrayList programArgs = new ArrayList();

		String[] args = getProgramArguments0(configuration, configDir);
		// If a product is specified, then add it to the program args
		if (configuration
				.getAttribute(IPDELauncherConstants.USE_PRODUCT, false)) {
			programArgs.add("-product"); //$NON-NLS-1$
			programArgs.add(configuration.getAttribute(
					IPDELauncherConstants.PRODUCT, "")); //$NON-NLS-1$
		} else {
			// specify the application to launch
			programArgs.add("-application"); //$NON-NLS-1$
			programArgs.add(configuration.getAttribute(
					IPDELauncherConstants.APPLICATION, TargetPlatform
							.getDefaultApplication()));
		}

		// specify the workspace location for the runtime workbench
		String targetWorkspace = LaunchArgumentsHelper
				.getWorkspaceLocation(configuration);
		if (targetWorkspace.length() > 0) {
			programArgs.add("-data"); //$NON-NLS-1$
			programArgs.add(targetWorkspace);
		}

		boolean showSplash = true;		
		
 
		Map pluginMap =LaunchPluginValidator.getPluginsToRun(configuration);
//		pluginMap=BundleLauncherHelper.getMergedBundleMap(configuration, false);
//		Map pluginMap1=BundleLauncherHelper.getMergedBundleMap(configuration, false);
		updatePluginMap(pluginMap, requiredPluginsIds);
		if (configuration
				.getAttribute(IPDELauncherConstants.USEFEATURES, false)) {
			validateFeatures();
			IPath installPath = PDEPlugin.getWorkspace().getRoot()
					.getLocation();
			programArgs.add("-install"); //$NON-NLS-1$
			programArgs
					.add("file:" + installPath.removeLastSegments(1).addTrailingSeparator().toString()); //$NON-NLS-1$
			if (!configuration.getAttribute(
					IPDELauncherConstants.CONFIG_USE_DEFAULT_AREA, true)) {
				programArgs.add("-configuration"); //$NON-NLS-1$
				programArgs
						.add("file:" + new Path(configDir.getPath()).addTrailingSeparator().toString()); //$NON-NLS-1$
			}
			programArgs.add("-update"); //$NON-NLS-1$
			// add the output folder names
			programArgs.add("-dev"); //$NON-NLS-1$
			programArgs.add(ClasspathHelper.getDevEntriesProperties(configDir
					.toString()
					+ "/dev.properties", true)); //$NON-NLS-1$
		} else {
			String productID = LaunchConfigurationHelper
					.getProductID(configuration);
			
			Properties prop = LaunchConfigurationHelper.createConfigIniFile(
					configuration, productID, pluginMap, configDir);
			showSplash = prop.containsKey("osgi.splashPath") || prop.containsKey("splashLocation"); //$NON-NLS-1$ //$NON-NLS-2$
			String brandingId = LaunchConfigurationHelper
					.getContributingPlugin(productID);
			TargetPlatform.createPlatformConfiguration(configDir,
					(IPluginModelBase[]) pluginMap.values().toArray(
							new IPluginModelBase[pluginMap.size()]),
					brandingId != null ? (IPluginModelBase) pluginMap
							.get(brandingId) : null);
			TargetPlatformHelper.checkPluginPropertiesConsistency(pluginMap,
					configDir);
			programArgs.add("-configuration"); //$NON-NLS-1$
			programArgs
					.add("file:" + new Path(configDir.getPath()).addTrailingSeparator().toString()); //$NON-NLS-1$

			// add the output folder names
			programArgs.add("-dev"); //$NON-NLS-1$
			programArgs.add(ClasspathHelper.getDevEntriesProperties(configDir
					.toString()
					+ "/dev.properties", pluginMap)); //$NON-NLS-1$
		}
		// necessary for PDE to know how to load plugins when target platform =
		// host platform
		// see PluginPathFinder.getPluginPaths() and
		// PluginPathFinder.isDevLaunchMode()
		IPluginModelBase base = (IPluginModelBase) pluginMap
				.get(PDECore.PLUGIN_ID);
		if (base != null
				&& VersionUtil.compareMacroMinorMicro(base
						.getBundleDescription().getVersion(), new Version(
						"3.3.1")) < 0) //$NON-NLS-1$
			programArgs.add("-pdelaunch"); //$NON-NLS-1$

		for (int i = 0; i < args.length; i++) {
			programArgs.add(args[i]);
		}

		if (!programArgs.contains("-nosplash") && showSplash) { //$NON-NLS-1$
			if (TargetPlatformHelper.getTargetVersion() >= 3.1) {
				programArgs.add(0, "-launcher"); //$NON-NLS-1$

				IPath path = null;
				if (TargetPlatform.getOS().equals("macosx")) { //$NON-NLS-1$
					path = new Path(TargetPlatform.getLocation())
							.append("Eclipse.app/Contents/Resources/Splash.app/Contents/MacOS/eclipse"); //$NON-NLS-1$
				} else {
					path = new Path(TargetPlatform.getLocation())
							.append("eclipse"); //$NON-NLS-1$
					if (TargetPlatform.getOS().equals("win32")) { //$NON-NLS-1$
						path = path.addFileExtension("exe"); //$NON-NLS-1$
					}
				}

				programArgs.add(1, path.toOSString()); // This could be the
														// branded launcher if
														// we want (also this
														// does not bring much)
				programArgs.add(2, "-name"); //$NON-NLS-1$
				programArgs.add(3, "Eclipse"); //This should be the name of the product //$NON-NLS-1$
				programArgs.add(4, "-showsplash"); //$NON-NLS-1$
				programArgs.add(5, "600"); //$NON-NLS-1$
			} else {
				programArgs.add(0, "-showsplash"); //$NON-NLS-1$
				programArgs.add(1, computeShowsplashArgument());
			}
		}
		return (String[]) programArgs.toArray(new String[programArgs.size()]);
		$else$ */
		return (String[]) PreprocessorInvariant.violated();		
		/* $endif$ */
	}

	private void updatePluginMap(Map pluginMap, String[] requiredPluginsIds) {
		for (int a=0;a<requiredPluginsIds.length;a++){
			String id=requiredPluginsIds[a];		
			if (RecorderWorkbenchLaunchConfDelegate.VERBOSE_INJECTION) {
				LogHandler.log("Starting attempt to add plugin with id: '" + id
						+ "' to plugin map");
			}
			addToMap(pluginMap, id);
		}
	}

	private void addToMap(Map pluginMap, String id) {
		/* $if eclipse.version == 3.3 $
		ModelEntry entry = PluginRegistry.findEntry(id);
		if (entry != null) {
			IPluginModelBase models[] = entry.getExternalModels();
			if (models == null || models.length == 0) {
				LogHandler.log("Error: No plugin models for plugin with id: '"
						+ id + "' was found in target platform ");
				
			}
			else{
				for (int i = 0; i < models.length; i++) 
				{			
					addToMap(pluginMap, models[i]);
				}
			}
		}
		else{
			locations.add(id);
		}
		$else$ */
		PreprocessorInvariant.violated();		
		/* $endif$ */
	}

	private void validateFeatures() throws CoreException {
		/* $if eclipse.version == 3.3 $
		
		IPath installPath = PDEPlugin.getWorkspace().getRoot().getLocation();
		String lastSegment = installPath.lastSegment();
		boolean badStructure = lastSegment == null;
		if (!badStructure) {
			IPath featuresPath = installPath.removeLastSegments(1).append(
					"features"); //$NON-NLS-1$
			badStructure = !lastSegment.equalsIgnoreCase("plugins") //$NON-NLS-1$
					|| !featuresPath.toFile().exists();
		}
		if (badStructure) {
			throw new CoreException(
					LauncherUtils
							.createErrorStatus(PDEUIMessages.WorkbenchLauncherConfigurationDelegate_badFeatureSetup));
		}
		// Ensure important files are present
		ensureProductFilesExist(getProductPath());
		$else$ */
		PreprocessorInvariant.violated();		
		/* $endif$ */
	}

	private void ensureProductFilesExist(IPath productArea) {
		/* $if eclipse.version == 3.3 $
		
		File productDir = productArea.toFile();
		File marker = new File(productDir, ".eclipseproduct"); //$NON-NLS-1$
		IPath eclipsePath = new Path(TargetPlatform.getLocation());
		if (!marker.exists())
			CoreUtility.copyFile(eclipsePath, ".eclipseproduct", marker); //$NON-NLS-1$
		File configDir = new File(productDir, "configuration"); //$NON-NLS-1$
		if (!configDir.exists())
			configDir.mkdirs();
		File ini = new File(configDir, "config.ini"); //$NON-NLS-1$
		if (!ini.exists())
			CoreUtility.copyFile(
					eclipsePath.append("configuration"), "config.ini", ini); //$NON-NLS-1$ //$NON-NLS-2$
		$else$ */
		PreprocessorInvariant.violated();		
		/* $endif$ */
	}

	private IPath getProductPath() {
		return PDEPlugin.getWorkspace().getRoot().getLocation()
				.removeLastSegments(1);
	}

	private void addToMap(Map map, IPluginModelBase model) {
		BundleDescription desc = model.getBundleDescription();
		BundleSpecification[] requiredBundles = desc.getRequiredBundles();
		for (int a=0;a<requiredBundles.length;a++){
			String name = requiredBundles[a].getName();
			if (!map.containsKey(name)){
				addToMap(map,name);
			}
		}
		if (desc != null) {
			String id = desc.getSymbolicName();
			// the reason that we are using a map is to easily check
			// if a plug-in with a certain id is among the plug-ins we are
			// launching with.
			// Therefore, now that we support multiple plug-ins by the same ID,
			// once a particular ID is used up as a key, the rest can be entered
			// with key == id_version, for easy retrieval of values later on,
			// and without the need to create complicated data structures for
			// values.
			if (!map.containsKey(id)) {
				map.put(id, model);
			} else {
				// since other code grabs only the model matching the "id", we
				// want to make
				// sure the model matching the "id" has the highest version
				// (because for singletons
				// the runtime will only resolve the highest version). Bug
				// 218393
				IPluginModelBase oldModel = (IPluginModelBase) map.get(id);
				String oldVersion = oldModel.getPluginBase().getVersion();
				String newVersion = model.getPluginBase().getVersion();
				if (oldVersion.compareTo(newVersion) < 0) {
					map
							.put(
									id
											+ "_" + oldModel.getBundleDescription().getBundleId(), oldModel); //$NON-NLS-1$
					map.put(id, model);
				} else {
					map.put(id + "_" + desc.getBundleId(), model); //$NON-NLS-1$
				}
			}
			if (RecorderWorkbenchLaunchConfDelegate.VERBOSE_INJECTION){
				LogHandler.log("Bundle added successfully");
			}
		}
		else{
			LogHandler.log("Error: Bundle description was not found for: '"
					+ model.getPluginBase().getId() );
			
		}
	}

	private String computeShowsplashArgument() {
		/* $if eclipse.version == 3.3 $
		IPath eclipseHome = new Path(TargetPlatform.getLocation());
		IPath fullPath = eclipseHome.append("eclipse"); //$NON-NLS-1$
		return fullPath.toOSString() + " -showsplash 600"; //$NON-NLS-1$
		$else$ */
		return (String)PreprocessorInvariant.violated();		
		/* $endif$ */
	}
}
