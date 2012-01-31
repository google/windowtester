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
package com.windowtester.recorder.ui.remote.standalone;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.windowtester.codegen.CodegenControllerHandler;
import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.ICodegenControllerHandler;
import com.windowtester.codegen.debug.DebugRecordingDialog;
import com.windowtester.codegen.debug.DebugRecordingInfo;
import com.windowtester.recorder.event.meta.RecorderControllerStartEvent;
import com.windowtester.recorder.event.meta.RecorderDisplayNotFoundEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.recorder.event.user.SemanticEventHandler;
import com.windowtester.swt.codegen.wizards.NewTestTypeWizard;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;
import com.windowtester.swt.event.server.WorkbenchEventController;
import com.windowtester.ui.util.Logger;
import com.windowtester.ui.util.Tracer;

/**
 * This is a port of the (soon to be) legacy {@link CodegenControllerHandler}
 * fitted to drive our new remote.
 */
public class RemoteRecordingController extends SemanticEventHandler implements IDebugEventSetListener, ICodegenControllerHandler {
	
	private final ExecutionProfile profile;
	private final Display display;
	private final Shell workbenchShell;
	private final WorkbenchEventController server;
	
	private int port;
	private Remote remote;
	
	public RemoteRecordingController(WorkbenchEventController server, ExecutionProfile profile) {
		this.profile = profile;
		this.server = server;
		this.workbenchShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
		this.display = PlatformUI.getWorkbench().getDisplay();
	}
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.SemanticEventHandler#handleDisplayNotFound(com.windowtester.swt.event.model.RecorderDisplayNotFoundEvent)
	 */
	public void handleDisplayNotFound(RecorderDisplayNotFoundEvent event) {
		MessageDialog.openError(workbenchShell, "Application Initialization", "The application user interface is not yet initialized");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.SemanticEventHandler#handleControllerStart(com.windowtester.swt.event.model.RecorderControllerStartEvent)
	 */
	public void handleControllerStart(RecorderControllerStartEvent event) {
		port = event.getPort();
		if(port<=0){
			Logger.log("Got invalid port number from application under test");
			return;
		}
		
		display.syncExec(new Runnable(){
			public void run() {
				if (remote != null)
					remote.dispose();
				remote = Remote.forRecorder(RecorderGateway.forPort(port));
				remote.open();
			}
		});
		EventRecorderPlugin.inRecording = true;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.SemanticEventHandler#handleError(com.windowtester.swt.event.model.RecorderErrorEvent)
	 */
	public void handleError(RecorderErrorEvent event) {
		Logger.log(event.getMsg(), event.getThrowable());
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.SemanticEventHandler#handleTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
	 */
	public void handleTrace(RecorderTraceEvent event) {
		Tracer.trace(event.getTraceOption(), event.getMsg());
	}

	/**
	 * Call the codegen wizard.
	 */
	protected void codegen() {
    	display.syncExec(new Runnable() {
			public void run() {
		        final NewTestTypeWizard wizard = new NewTestTypeWizard(server.getEvents(), profile);
		        WizardDialog dialog = new WizardDialog(display.getActiveShell(), wizard);
		        dialog.open();
			}
    	});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		if (events == null || events.length == 0)
			return;
		DebugEvent event = events[0];
		if(event.getSource() instanceof RuntimeProcess){
			RuntimeProcess process = (RuntimeProcess)event.getSource();
			if(process.getLaunch().equals(profile.getLaunch())&&event.getKind()==DebugEvent.TERMINATE){
				// the recording flag
				EventRecorderPlugin.inRecording = false;
				// close Controller dialog window
				if (remote != null)
					remote.dispose();
				// stop the server from listening on socket if not in debug mode
				if(!EventRecorderPlugin.isInDebugMode())
					server.stopServer();				
				// now codegen is ready to go...`
				if (!server.getEvents().isEmpty()) //ignore case where no events have been recorded
					codegen();
				// display debug information as appropriate
				DebugRecordingInfo debugInfo = DebugRecordingInfo.getInfo();
				debugInfo.setSemanticEvents(server.getEvents());
				if (debugInfo.shouldShow()) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							new DebugRecordingDialog(null).open();
						}
					});
				}
				// in both cases, clear the cache
				server.getEvents().clear(); 
				// remove this class from debug listeners
				DebugPlugin.getDefault().removeDebugEventListener(this);
				// delete launch configuration if not in debug mode
				try {
					if(!EventRecorderPlugin.isInDebugMode()){
						ILaunchConfiguration lc = process.getLaunch().getLaunchConfiguration();
						if(lc.getName().equals(ExecutionProfile.TMP_APPLICATION_LAUNCH_CONFIG))
							lc.delete();
					}
				} catch (CoreException e) {
					Logger.log(e);
				}
			}
		}
	}
}
