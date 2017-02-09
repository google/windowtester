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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;


import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.debug.DebugRecordingInfo;
import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

@SuppressWarnings("restriction")
public class RecorderSWTLaunchConfDelegate extends RecorderWorkbenchLaunchConfDelegate {
	
	protected ILaunchConfigurationWorkingCopy wcSwt;
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void launch0(ILaunchConfiguration configuration, String mode,	ILaunch launch, IProgressMonitor monitor) throws CoreException {
		

		// getting SWT launch config working copy
		wcSwt = configuration.getWorkingCopy();
		// instantiating default RCP launch configuration working copy
		ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(UiPlugin.WORKBENCH_LAUNCH_CONFIGURATION_TYPE);
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, ExecutionProfile.TMP_APPLICATION_LAUNCH_CONFIG);
		if(wc.exists())
			wc.delete();
		// map swt launch config to rcp launch config
		prepareSwtDummyBundle(wc);
		setWorkspaceDataSection(wc);
		setApplicationSection(wc);
		setJRESection(wc);
		setVMArgsSection(wc);
		setEnvironment(wc);
		setWorkingDir(wc);
		mapSources(wc);
		super.launch0(wc, mode, launch, monitor);
	}
	
	/**
	 * Maps source locations between SWT application launch configuration and default launch configuration
	 * @param wc
	 * @param wcSwt
	 * @throws CoreException
	 */
	protected void mapSources(ILaunchConfigurationWorkingCopy wc) throws CoreException {
		String locatorMememnto = wcSwt.getAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String) null);
		if(locatorMememnto!=null)
			wc.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, locatorMememnto);
		String locatorID = wcSwt.getAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, (String) null);
		if(locatorID!=null)
			wc.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, locatorID);
		String provider = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER, (String) null);
		if (provider != null) 
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER, provider);
	}

	/**
	 * @param wc
	 * @param wcSwt
	 * @throws CoreException
	 */
	private void setEnvironment(ILaunchConfigurationWorkingCopy wc) throws CoreException {
		wc.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map<String, String>)wcSwt.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map<String, String>)null));
		wc.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, wcSwt.getAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, false));
	}

	/**
	 * @param wcSwt
	 * @throws CoreException
	 */
	private void prepareSwtDummyBundle(ILaunchConfigurationWorkingCopy wc) throws CoreException {
		IPath stateDirPath = UiPlugin.getDefault().getStateLocation();
		//String projName = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "dummy");
		//stateDirPath = stateDirPath.append(new Path(projName));
		DummyBundleBuilder db = new DummyBundleBuilder();
		IPath bundlePath = db.buildDummyBundle(wcSwt, stateDirPath);
		// set required for SWT application type arguments
		String vmargs = getVmAttributes(wc);
		vmargs += " \"-D"+EventRecorderPlugin.INSTALL_BUNDLES_SYS_PROPERTY+"=reference:file:"+bundlePath.toString()+"\"";
		setVmAttributes(wc, vmargs);
		String progrargs = wc.getAttribute("progargs", "");
		progrargs+=" -noSplash";
		wc.setAttribute("progargs", progrargs);
	}

	/**
	 * Set VM arguments section 
	 * 
	 * @param wc
	 * @param wcSwt
	 * @throws CoreException
	 */
	protected void setVMArgsSection(ILaunchConfigurationWorkingCopy wc) throws CoreException {
		String mainClass = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "");
		// get the VM Args specified by the user from the working copy of launch config
		String vmargs = getVmAttributes(wcSwt);
		// get the VM Args for WindowTester
		vmargs += getVmAttributes(wc);
		// add the name of class main
		vmargs += " -D"+EventRecorderPlugin.LAUNCH_CLASS_NAME_PROP+"="+mainClass;
		setVmAttributes(wc, vmargs);
	}

	/**
	 * Map JRE settings
	 * 
	 * @param wc
	 * @param wcSwt
	 * @throws CoreException
	 */
	@SuppressWarnings("deprecation")
	private void setJRESection(ILaunchConfigurationWorkingCopy wc) throws CoreException {
//		String projectName = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
//		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//		IJavaProject javaProject = JavaCore.create(project);
//		IVMInstall vmInstall = JavaRuntime.getVMInstall(javaProject);
//		String vmName = vmInstall.getName();
		
		String vmName = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, (String)null);
		wc.setAttribute("vminstall", vmName);

	}

	/**
	 * Set an application to SWT bootstrap application 
	 * 
	 * @param wc
	 */
	private void setApplicationSection(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute("application", UiPlugin.SWT_BOOTSTRAP_APPLICATION);
		wc.setAttribute("useProduct", false);
	}

	/**
	 * Set workspace data section to UiPlugin's workspace location. This workspace 
	 * will never be used so it will be always cleared before startup. The only 
	 * reason why we would need it - is to inspect (or ask clients to provide) 
	 * development logs. 
	 * 
	 * @param wc
	 */
	private void setWorkspaceDataSection(ILaunchConfigurationWorkingCopy wc) {
		IPath stateDirPath = UiPlugin.getDefault().getStateLocation();
		IPath swtLaunchWorkspace = stateDirPath.append(UiPlugin.SWT_LAUNCH_WORKSPACE);
		DebugRecordingInfo.getInfo().setWorkspaceLocation(swtLaunchWorkspace);
		wc.setAttribute("location" + String.valueOf(0), swtLaunchWorkspace.toString());
		wc.setAttribute("clearws", true);
		wc.setAttribute("askclear", false);
	}
	
	/**
	 * Overrides default execution profile to provide SWT execution parameters
	 */
	protected ExecutionProfile createExecutionProfile(ILaunch launch) throws CoreException {
		// create execution profile instance
		ExecutionProfile profile = new ExecutionProfile();
		profile.setLaunch(launch);
		profile.setExecType(ExecutionProfile.SWT_EXEC_TYPE);
		profile.setMainSwtClassName(wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, ""));
		String args = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "");
		if(!"".equals(args))	
			profile.setProgramArgs(args.split("[ \t]"));
		else
			profile.setProgramArgs(new String[] {});
		profile.setProjectName(wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));
		return profile;
	}
	
	/**
	 * get the classpath of the launch configuration.
	 * Override AbstractPDELaunchConfiguration.getClasspath
	 * @param wc
	 */
	
	@SuppressWarnings("unchecked")
	public String[] getClasspath(ILaunchConfiguration configuration)
			throws CoreException {
		
		String[] classpath = constructClassPath(configuration);
		
		// get the boot path entries
		List entries = null;
		entries = getBootpath(wcSwt);
		// add user classpath entries
		entries.addAll(getUserClasspathEntries(wcSwt));
		// add the classpath entries from the AbstractPDELaunchConfiguration 
		for (int i= 0;i < classpath.length;i++)
			entries.add(0+i,classpath[i]);
		
		String[] result = (String[]) entries.toArray(new String[entries.size()]);
		DebugRecordingInfo.getInfo().setRecorderClasspath(result);
		return result;
	}

	private String[] constructClassPath(ILaunchConfiguration configuration) throws CoreException {
		/* $if eclipse.version < 3.6 $ */
//		return org.eclipse.pde.internal.ui.launcher.LaunchArgumentsHelper.constructClasspath(configuration);
		/* $else$ */
		return org.eclipse.pde.internal.launching.launcher.LaunchArgumentsHelper.constructClasspath(configuration);
		/* $endif$ */
	}

	/**
	 *  Set the working directory : has to be set to point to project location so that
	 *  the application is launched correctly. 
	 *  5/31/07 : kp
	 */
	private void setWorkingDir(ILaunchConfigurationWorkingCopy wc){
		try {
			
			String projectName = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			// get the absolute path
			IPath path = project.getLocation();
			String workingDir = path.toOSString();
			// set the working dir to absolute path of project
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, workingDir);		
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Returns the entries that should appear on the user portion of the
	 * classpath as specified by the given launch configuration, as an array of
	 * resolved strings. The returned array is empty if no classpath is
	 * specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the classpath specified by the given launch configuration,
	 *         possibly an empty array
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 *  from AbstractJavaLaunchConfigurationDelegate and modified
	 */
	@SuppressWarnings("unchecked")
	public List getUserClasspathEntries(ILaunchConfiguration configuration)
			throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime
				.computeUnresolvedRuntimeClasspath(configuration);
		entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
		List userEntries = new ArrayList(entries.length);
		Set set = new HashSet(entries.length);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				String location = entries[i].getLocation();
				if (location != null) {
					if (!set.contains(location)) {
						userEntries.add(location);
						set.add(location);
					}
				}
			}
		}
		return userEntries;
	}
	


	/**
	 * Returns entries that should appear on the bootstrap portion of the
	 * classpath as specified by the given launch configuration, as an array of
	 * resolved strings. The returned array is <code>null</code> if all
	 * entries are standard (i.e. appear by default), or empty to represent an
	 * empty bootpath.
	 * 
	 * from AbstractJavaLaunchConfigurationDelegate and modified
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the bootpath specified by the given launch configuration. An
	 *         empty bootpath is specified by an empty array, and
	 *         <code>null</code> represents a default bootpath.
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	@SuppressWarnings("unchecked")
	public List getBootpath(ILaunchConfiguration configuration)
			throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime
				.computeUnresolvedRuntimeClasspath(configuration);
		entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
		List bootEntries = new ArrayList(entries.length);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getClasspathProperty() != IRuntimeClasspathEntry.USER_CLASSES) {
				String location = entries[i].getLocation();
				if (!entries[i].getPath().lastSegment().equals("rt.jar"))
					if (location != null) {
						bootEntries.add(location);
				}
			}
		}
		return bootEntries;				
		
	}	
	
	
}