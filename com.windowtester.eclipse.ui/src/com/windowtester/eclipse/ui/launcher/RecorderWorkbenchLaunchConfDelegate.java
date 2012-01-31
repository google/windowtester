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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.windowtester.codegen.CodegenControllerHandler;
import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.ICodegenControllerHandler;
import com.windowtester.codegen.debug.DebugRecordingInfo;
import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.WTUI;
import com.windowtester.eclipse.ui.launcher.bundle.RecorderBundleRequirements;
import com.windowtester.eclipse.ui.launcher.bundle.RecorderLaunchValidator;
import com.windowtester.internal.debug.LogHandler;
import com.windowtester.net.ICommunicationProtocolConstants;
import com.windowtester.recorder.ui.RecordingSessionController;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;
import com.windowtester.swt.event.server.WorkbenchEventController;
import com.windowtester.ui.util.Logger;

public class RecorderWorkbenchLaunchConfDelegate extends
/* $codepro.preprocessor.if version < 3.2.0 $
org.eclipse.pde.internal.ui.launcher.WorkbenchLaunchConfigurationDelegate
$codepro.preprocessor.elseif version >= 3.2.0 $ */
org.eclipse.pde.ui.launcher.EclipseApplicationLaunchConfiguration
/* $codepro.preprocessor.endif $ */
{
	/** Workbench event controller server */
	private WorkbenchEventController server;

	protected boolean injectBundles = false;

	public final static String INJECT_BUNDLES_KEY = "com.windowtester.eclipse.ui.launcher.INJECT_BUNDLES_KEY"; //$NON-NLS-1$

	protected static final boolean VERBOSE_INJECTION = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.
	 * eclipse.debug.core.ILaunchConfiguration, java.lang.String,
	 * org.eclipse.debug.core.ILaunch,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final void launch(final ILaunchConfiguration configuration, final String mode,
			final ILaunch launch, final IProgressMonitor monitor) throws CoreException {

		optionallyInjectBundles(configuration);
		
		cacheConfiguration(configuration);
		
		// first test if we there is no recording in progress
		if (UiPlugin.getDefault().isInRecoring()) {
			UiPlugin
					.getDefault()
					.showErrorDialog(
							Messages.getString("RecorderWorkbenchLaunchConfDelegate.RECORDING_TITLE"), //$NON-NLS-1$
							Messages.getString("RecorderWorkbenchLaunchConfDelegate.RECORDING_QUESTION")); //$NON-NLS-1$
			return;
		}
		// init bootstrap plugin and semantic event listener
		DebugRecordingInfo.newRecording();
		final CoreException[] ex = new CoreException[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					launch0(configuration, mode, launch, monitor);
				} catch (CoreException e) {
					ex[0] = e;
				}
			}
		});
		if (ex[0] != null)
			throw ex[0];
	}

	private void cacheConfiguration(ILaunchConfiguration configuration) {
		UiPlugin.getDefault().cacheLaunchConfig(configuration);
	}

	private void optionallyInjectBundles(
			final ILaunchConfiguration configuration) throws CoreException {
		if (BundleInjection.isDisabled())
			return;
		if (!validConfig(configuration)) {
			injectBundles = configuration.getAttribute(INJECT_BUNDLES_KEY,
					true);
			if (!injectBundles) {
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						boolean openQuestion = MessageDialog
								.openQuestion(
										PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow()
												.getShell(),
										Messages.getString("RecorderWorkbenchLaunchConfDelegate.MISSED_BUNDLES_TITLE"), //$NON-NLS-1$
										Messages.getString("RecorderWorkbenchLaunchConfDelegate.MISSED_BUNDLES_QUESTION")); //$NON-NLS-1$
						injectBundles = openQuestion;
						try{
							ILaunchConfigurationWorkingCopy copy = configuration.getWorkingCopy();
							copy.setAttribute(INJECT_BUNDLES_KEY, injectBundles);
							copy.doSave();
						}catch (CoreException e) {
							LogHandler.log(e);
						}
					}

				});

			}
		}
	}

	public String[] getProgramArguments(ILaunchConfiguration configuration)
			throws CoreException {
		if (injectBundles) {
			String[] requiredPluginIds = RecorderBundleRequirements
					.getRequiredPluginIds();
			return new ProgramArgumentBuilder().getProgramArguments(
					configuration, getConfigDir(configuration),
					requiredPluginIds);
		} else {
			return super.getProgramArguments(configuration);
		}
	}

	protected boolean validConfig(ILaunchConfiguration configuration) {

		RecorderLaunchValidator validator = new RecorderLaunchValidator();

		IStatus status = validator.validate(configuration);
		if (status.isOK())
			return true;

		// LaunchConfigurationStatusReporter.forStatus(status);

		return false;
	}

	protected void launch0(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
		initSemanticEventListener(wc, launch);

		if (EventRecorderPlugin.isInDebugMode()) {
			if (ExecutionProfile.TMP_APPLICATION_LAUNCH_CONFIG.equals(wc
					.getName())) {
				wc.doSave(); // this name is used for SWT launch types
			} else {
				ILaunchConfigurationType type = DebugPlugin.getDefault()
						.getLaunchManager().getLaunchConfigurationType(
								UiPlugin.WORKBENCH_LAUNCH_CONFIGURATION_TYPE);
				ILaunchConfigurationWorkingCopy localCopy = type.newInstance(
						null, ExecutionProfile.TMP_APPLICATION_LAUNCH_CONFIG);
				localCopy.setAttributes(wc.getAttributes());
				localCopy.doSave();
			}
		}
		// provide actual run mode if the record mode was issued
		if (UiPlugin.LAUNCH_MODE_RECORD.equals(mode))
			mode = ILaunchManager.RUN_MODE;
		super.launch((ILaunchConfiguration) wc, mode, launch, monitor);
		// cache recording information
		String workspaceLoc = wc.getAttribute("location", (String) null); //$NON-NLS-1$
		if (workspaceLoc == null)
			workspaceLoc = wc.getAttribute("location" + String.valueOf(0), //$NON-NLS-1$
					(String) null);
		if (workspaceLoc != null)
			DebugRecordingInfo.getInfo().setWorkspaceLocation(
					new Path(workspaceLoc));
	}

	public String[] getClasspath(ILaunchConfiguration configuration)
			throws CoreException {
		String[] result = null;

		/* $codepro.preprocessor.if version > 3.1 $ */
		result = super.getClasspath(configuration);
		/* $codepro.preprocessor.endif $ */
		
		DebugRecordingInfo.getInfo().setRecorderClasspath(result);
		return result;
	}

	/**
	 * @param wc
	 * @throws CoreException
	 */
	protected void initSemanticEventListener(
			ILaunchConfigurationWorkingCopy wc, ILaunch launch)
			throws CoreException {
		if (EventRecorderPlugin.isInDebugMode()) {
			// reuse the same instance of the server
			server = EventRecorderPlugin.getDefault().getWorkbechController();
		} else {
			// create a new instance for each LC launch
			server = new WorkbenchEventController();
		}
		ICodegenControllerHandler handler = createControllerHandler(server,
				launch);
		server.setHandler(handler);
		int port = server.getPort();
		if (port == -1) {
			IStatus s = Logger.createLogStatus(
					Messages.getString("RecorderWorkbenchLaunchConfDelegate.SERVER_SOCKET_NOT_INITED"), null, null); //$NON-NLS-1$
			throw new CoreException(s);
		}
		// save the port number in configuration scoped preferences
		String vmargs = getVmAttributes(wc);
		vmargs += " -D" //$NON-NLS-1$
				+ ICommunicationProtocolConstants.RECORDER_PORT_SYSTEM_PROPERTY
				+ "=" + port; //$NON-NLS-1$
		setVmAttributes(wc, vmargs);
		// register to Debug event for termination tests
		DebugPlugin.getDefault().addDebugEventListener(handler);
		// start server
		if (!server.isAlive())
			server.start();
	}

	// Hook to plugin in another handler
	protected ICodegenControllerHandler createControllerHandler(
			WorkbenchEventController wbController, ILaunch launch)
			throws CoreException {
		ExecutionProfile execProfile = createExecutionProfile(launch);
		if (WTUI.isRecorderConsoleViewEnabled())
			return new RecordingSessionController(wbController, execProfile);
		return new CodegenControllerHandler(wbController, execProfile);
	}

	protected ExecutionProfile createExecutionProfile(ILaunch launch)
			throws CoreException {
		// create execution profile
		ExecutionProfile profile = new ExecutionProfile();
		profile.setLaunch(launch);
		profile.setExecType(ExecutionProfile.RCP_EXEC_TYPE);
		return profile;
	}

	protected void setVmAttributes(ILaunchConfigurationWorkingCopy wc,
			String attr) {
		/* $codepro.preprocessor.if version < 3.2.0 $ 
		 wc.setAttribute("vmargs", attr);
		 wc.setAttribute(org.eclipse.jdt.launching.
		 IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, attr);
		$codepro.preprocessor.elseif version >= 3.2.0 $ */
		wc.setAttribute("vmargs", (String) null); //$NON-NLS-1$
		wc
				.setAttribute(
						org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
						attr);
		/* $codepro.preprocessor.endif $ */
	}

	protected String getVmAttributes(ILaunchConfiguration wc)
			throws CoreException {
		/* $codepro.preprocessor.if version < 3.2.0 $ 
		return wc.getAttribute("vmargs", ""); 
		$codepro.preprocessor.elseif version >= 3.2.0 $ */
		return wc
				.getAttribute(
						org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
						""); //$NON-NLS-1$
		/* $codepro.preprocessor.endif $ */
	}
}
