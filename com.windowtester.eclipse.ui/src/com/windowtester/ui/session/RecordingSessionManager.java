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
package com.windowtester.ui.session;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.Session;
import com.windowtester.eclipse.ui.session.SessionMonitor;
import com.windowtester.eclipse.ui.views.RecorderConsoleView;
import com.windowtester.recorder.ISemanticEventProvider;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.recorder.ui.IRecorderConsoleActionHandler;
import com.windowtester.recorder.ui.remote.DashboardRemote;
import com.windowtester.recorder.ui.remote.IDashBoardRemote;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;
import com.windowtester.ui.util.Logger;

public class RecordingSessionManager implements IRecordingSessionManager, IRecorderConsoleActionHandler {

	private ISemanticEventProvider eventProvider;
	private int port;

	class EventRecorderGateway {		
		void notifyRecord() {
			EventRecorderPlugin.send(RecorderMetaEvent.START, getPort());
		}
		void notifyPause() {
			EventRecorderPlugin.send(RecorderMetaEvent.PAUSE, getPort());
		}
		void notifyRestart() {
			EventRecorderPlugin.send(RecorderMetaEvent.RESTART, getPort());
		}
		public void notifySpyModeToggled() {
			EventRecorderPlugin.send(RecorderMetaEvent.TOGGLE_SPY, getPort());
		}
	}
	

	
	private final EventRecorderGateway recorderGateway = new EventRecorderGateway();
	
	private ISession currentSession = SessionMonitor.NULL_SESSION;
	private final ExecutionProfile profile;
	private RecorderConsoleView console;
	private IDashBoardRemote dashRemote;
	
	
	public RecordingSessionManager(ISemanticEventProvider eventProvider, ExecutionProfile profile) {
		this.eventProvider = eventProvider;
		this.profile       = profile;
	}
	
	public ISemanticEventProvider getEventProvider() {
		return eventProvider;
	}
	
	public ExecutionProfile getProfile() {
		return profile;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.session.IRecordingSessionManager#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	public final int getPort() {
		return port;
	}
	
	protected EventRecorderGateway getRecorderGateway() {
		return recorderGateway;
	}
	
	protected ISession getCurrentSession() {
		return currentSession;
	}
	
	protected void setCurrentSession(ISession currentSession) {
		this.currentSession = currentSession;
	}
	
	public RecorderConsoleView getConsole() {
		return console;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.ui.session.IRecordingSessionManager#start()
	 */
	public void start() {
			//executed on the display thread since we access the Platform UI 
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try {
						setUpConsole();
						doStartSession();
					} catch (Throwable e) {
						Logger.log(e);
					}
				}
			});
	}

	
	private void setUpConsole() throws PartInitException {
		console = activateRecorderConsoleView();
	}

	private void doStartSession() {
		EventRecorderPlugin.inRecording = true; //TODO: yuck!
		setCurrentSession(Session.newRemote(getEventProvider(), getProfile()));
		UiPlugin.getDefault().sessionStarted(getCurrentSession());
		getConsole().addHandler(this);
		/*
		 * Testing here...
		 */
		openDashboard();
	}	
		
	private void openDashboard() {
		dashRemote = DashboardRemote.forRecorderActions(console.getActions()).withModel(console.getPresenter().getSequenceModel());
		dashRemote.addStatusSource(getConsole());
		dashRemote.open();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.session.IRecordingSessionManager#stop()
	 */
	public void end() {
		closeDashboard();
		UiPlugin.getDefault().sessionEnded(getCurrentSession());
		EventRecorderPlugin.inRecording = false; //TODO: yuck!
		RecorderConsoleView consoleView = getConsole();
		if (consoleView != null)
			consoleView.removeHandler(this);
	}

	private void closeDashboard() {
		//TODO: if recorder does not properly launch this will be null...
		if (dashRemote != null)
			dashRemote.close();
	}

	private RecorderConsoleView activateRecorderConsoleView() throws PartInitException {
		return (RecorderConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(UiPlugin.RECORDER_VIEW_ID);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActionHandler#clickDelete()
	 */
	public void clickDelete() {
		// ignored
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderConsoleActionHandler#clickAddHook()
	 */
	public void clickAddHook() {
		// ignored
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActionHandler#clickCodegen()
	 */
	public void clickCodegen() {
		//ignored
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.recorder.ui.IRecorderActionHandler#clickPause()
	 */
	public void clickPause() {
		getRecorderGateway().notifyPause();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActionHandler#clickRecord()
	 */
	public void clickRecord() {
		getRecorderGateway().notifyRecord();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActionHandler#clickRestart()
	 */
	public void clickRestart() {
		getRecorderGateway().notifyRestart();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderConsoleActionHandler#clickSpyMode()
	 */
	public void clickSpyMode() {
		getRecorderGateway().notifySpyModeToggled();
	}
	
}
