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
package com.windowtester.recorder.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.windowtester.codegen.CodegenControllerHandler;
import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.ICodegenControllerHandler;
import com.windowtester.recorder.ISemanticEventProvider;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderControllerStartEvent;
import com.windowtester.recorder.event.meta.RecorderDisplayNotFoundEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.recorder.event.user.SemanticEventHandler;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;
import com.windowtester.swt.event.server.WorkbenchEventController;
import com.windowtester.ui.session.IRecordingSessionManager;
import com.windowtester.ui.session.RecordingSessionManager;
import com.windowtester.ui.util.Logger;
import com.windowtester.ui.util.Tracer;

/**
 * 
 * NOTE: this is an in progress refactoring of {@link CodegenControllerHandler}
 *
 */
public class RecordingSessionController extends SemanticEventHandler implements IDebugEventSetListener, ICodegenControllerHandler {
	
	
	/** Corresponding to this event listener recording session execution profile */ 
	private ExecutionProfile _profile;
		
	/** The workbench shell instance */
	private final Shell _workbenchShell;
	
	/** Application under recording controller listening port*/
	private int _port;
	
	/** Workbench event controller reference */
	private final WorkbenchEventController _server;
	
	private RecordingSessionManager _sessionManager;
	
	private RecorderEventGateway _eventGateway = new RecorderEventGateway();

	private InspectionEventHandler inspectionEventHandler;
	
	protected static class RecorderEventGateway implements ISemanticEventProvider {

		private List /*<ISemanticEventListener>*/ _listeners = new ArrayList();
		
	    public void addListener(ISemanticEventListener listener) {
	        List listeners = getListeners();
	        if (!listeners.contains(listener)) //multiple adds simply ignored
	            listeners.add(listener);
	    }

	    /* (non-Javadoc)
	     * @see com.windowtester.event.model.IEventRecorder#removeListener(com.windowtester.swt.event.model.ISemanticEventListener)
	     */
	    public void removeListener(ISemanticEventListener listener) {
	    	getListeners().remove(listener);
	    }
	    
	    List getListeners() {
			return _listeners;
		}
		
	    /**
	     * This needs to be thread-safe as events might be produced on the UI thread or the recording thread.
	     */
		public synchronized void notify(IUISemanticEvent semanticEvent) {
		    for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notify(semanticEvent);
		}
	    	    
	}
	
	
	public RecordingSessionController(WorkbenchEventController server, ExecutionProfile profile) {
		super();
		setProfile(profile);
		_server = server;
		_workbenchShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
		_sessionManager = new RecordingSessionManager(getEventGateway(), profile);

		inspectionEventHandler = new InspectionEventHandler(getEventGateway()) {
			/* (non-Javadoc)
			 * @see com.windowtester.recorder.ui.InspectionEventHandler#assertionMade()
			 */
			protected void assertionMade() {
				stopInspecting();
			}
			/* (non-Javadoc)
			 * @see com.windowtester.recorder.ui.InspectionEventHandler#expertDismissed()
			 */
			protected void expertDismissed() {
				stopInspecting();
			}
			
			private void stopInspecting() {
				//do a toggle: note this is not the cleanest way to do this... (demeter says...)
				_sessionManager.getConsole().clickSpyMode();
			}
			
		};
		
//disabled experiment w/ event notification
//		_eventGateway.addListener(new SemanticEventAdapter() {
//			public void notify(IUISemanticEvent event) {
//				if (event instanceof SemanticWidgetInspectionEvent)
//					return;
//				EventNotification.popupForEvent(event);
//			}
//		});
	}
	

	public IRecordingSessionManager getSessionManager() {
		return _sessionManager;
	}
	
	protected RecorderEventGateway getEventGateway() {
		return _eventGateway;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.SemanticEventHandler#handleDisplayNotFound(com.windowtester.swt.event.model.RecorderDisplayNotFoundEvent)
	 */
	public void handleDisplayNotFound(RecorderDisplayNotFoundEvent event) {
		MessageDialog.openError(getWorkbenchShell(), "Application Initialization", "The application user interface is not yet initialized");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.SemanticEventHandler#handleControllerStart(com.windowtester.swt.event.model.RecorderControllerStartEvent)
	 */
	public void handleControllerStart(RecorderControllerStartEvent event) {
		setPort(event.getPort());
		if (isInvalidPortNumber()){
			Logger.log("Got invalid port number from application under test");
			return;
		}
		startSession();
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Semantic event relay
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticKeyDownEvent)
	 */
	public void handle(SemanticKeyDownEvent event) {
		getEventGateway().notify(event);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticMenuSelectionEvent)
	 */
	public void handle(SemanticMenuSelectionEvent event) {
		getEventGateway().notify(event);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent)
	 */
	public void handle(SemanticWidgetSelectionEvent event) {
		getEventGateway().notify(event);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventHandler#handle(com.windowtester.recorder.event.user.UISemanticEvent)
	 */
	public void handle(UISemanticEvent event) {
		getEventGateway().notify(event);
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent)
	 */
	public void handle(SemanticTreeItemSelectionEvent event) {		
		getEventGateway().notify(event);
	}
	
		
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventHandler#handleInspectionEvent(com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent)
	 */
	public void handleInspectionEvent(SemanticWidgetInspectionEvent inspectionEvent) {
		inspectionEventHandler.handleInspectionEvent(inspectionEvent);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Meta event handling
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ...
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void startSession() {
		getSessionManager().setPort(getPort());
		getSessionManager().start();
		EventRecorderPlugin.inRecording = true;
		//TODO: disabled for now -- retool and re-enable
		//UsageHints.openStartRecordingHintDialog();
	}



	private boolean isInvalidPortNumber() {
		return getPort() <= 0;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		if (events == null || events.length == 0)
			return;
		DebugEvent event = events[0];
		if (event.getSource() instanceof RuntimeProcess){
			RuntimeProcess process = (RuntimeProcess)event.getSource();
			if (isTerminateEvent(event, process)){
				stopSession();
				// stop the server from listening on socket if not in debug mode
				if(!EventRecorderPlugin.isInDebugMode())
					getServer().stopServer();
				
				getServer().getEvents().clear(); 
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

	private void stopSession() {
		// the recording flag
		EventRecorderPlugin.inRecording = false;
		getSessionManager().end();
		
	}



	private boolean isTerminateEvent(DebugEvent event, RuntimeProcess process) {
		return process.getLaunch().equals(getProfile().getLaunch())&&event.getKind()==DebugEvent.TERMINATE;
	}

	private void setPort(int port) {
		_port = port;
	}

	private int getPort() {
		return _port;
	}

	private WorkbenchEventController getServer() {
		return _server;
	}

	private void setProfile(ExecutionProfile profile) {
		_profile = profile;
	}

	private ExecutionProfile getProfile() {
		return _profile;
	}

	private Shell getWorkbenchShell() {
		return _workbenchShell;
	}
}
