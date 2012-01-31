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

public class RecorderPanelModel implements IRecorderPanelModel {

	
	private static interface RecordingState extends IRecorderPanelModel {}
	
	private static interface SessionState extends RecordingState {
		void setRecording(RecordingState state);
	}
	
	class StateAdapter implements RecordingState {

		public boolean isDeleteEnabled() {
			return false;
		}
		public boolean isPauseEnabled() {
			return false;
		}
		public boolean isRecordEnabled() {
			return false;
		}
		public boolean isRestartEnabled() {
			return false;
		}
		public boolean isCodegenEnabled() {
			return false;
		}
		public boolean isHookEnabled() {
			return false;
		}
		
		public boolean inSession() {
			return false;
		}
		
		public void sessionEnded() {}
		public void sessionStarted() {}

		public void clickDelete() {}
		public void clickPause() {}
		public void clickRecord() {}
		public void clickRestart() {}
		public void clickCodegen() {}
		public void clickAddHook() {}
		public void clickSpyMode() {}
		public void addListener(IChangeListener listener) {}
		public void removeListener(IChangeListener listener) {}
		
		public void setEventProvider(IEventSequenceModel provider) {}
		
	}
	
	
	class InSession extends StateAdapter implements SessionState {
		
		private RecordingState _recordingState = IDLE; //initial state
		
		public void setRecording(RecordingState state) {
			_recordingState = state;
		}
		
		public boolean isDeleteEnabled() {
			return getRecording().isDeleteEnabled();
		}
		public boolean isPauseEnabled() {
			return getRecording().isPauseEnabled();
		}
		public boolean isRecordEnabled() {
			return getRecording().isRecordEnabled();
		}
		public boolean isRestartEnabled() {
			return getRecording().isRestartEnabled();
		}
		public void clickDelete() {
			getRecording().clickDelete();
		}
		public void clickPause() {
			getRecording().clickPause();
		}
		public void clickRecord() {
			getRecording().clickRecord();
		}
		public void clickRestart() {
			getRecording().clickRestart();
		}
		public boolean inSession() {
			return true;
		}
		
		public boolean isHookEnabled() {
			return true;
		}

		RecordingState getRecording() {
			return _recordingState;
		}

	}
	
	class OutOfSession extends StateAdapter implements SessionState {
		public void setRecording(RecordingState state) {}
	}
	
	
	class Recording extends StateAdapter {

		public void clickPause() {
			setRecording(IDLE);
		}
		public boolean isRecordEnabled() {
			return false;
		}
		public boolean isPauseEnabled() {
			return true;
		}
		public boolean isRestartEnabled() {
			return true;
		}
		public boolean isDeleteEnabled() {
			return false;
		}
	}
	
	class Idle extends StateAdapter implements RecordingState {

		public void clickRecord() {
			setRecording(RECORDING);
		}		
		public boolean isDeleteEnabled() {
			return false;
		}		
		public boolean isRecordEnabled() {
			return true;
		}
		public boolean isPauseEnabled() {
			return false;
		}
		public boolean isRestartEnabled() {
			return hasEventsToProcess();
		}		
	}	
	
	private RecordingState RECORDING    = new Recording();
	private RecordingState IDLE         = new Idle();
	private SessionState OUT_OF_SESSION = new OutOfSession();
	
	private IEventSequenceModel _provider;
	
	private final List _listeners = new ArrayList();

	public RecorderPanelModel(IEventSequenceModel provider) {
		setEventProvider(provider);
		setSession(OUT_OF_SESSION);
	}
	
	
	public void setEventProvider(IEventSequenceModel provider) {
		_provider = provider;
	}
	
	
	public RecorderPanelModel() {
		this(new EventSequenceModel());
	}

	public IEventSequenceModel getProvider() {
		return _provider;
	}
	
	public boolean hasEventsToProcess() {
		return getProvider().getEvents().length != 0;
	}

	private SessionState _sessionState;
	
	private SessionState getSession() {
		return _sessionState;
	}
	
	private void setRecording(RecordingState state) {
		getSession().setRecording(state);
		notifyChanged();
	}
	
	private void setSession(SessionState state) {
		_sessionState = state;
		notifyChanged();
	}

	public void clickPause() {
		getSession().clickPause();
	}

	public void clickRecord() {
		getSession().clickRecord();
	}

	public void clickRestart() {
		getSession().clickRestart();
	}

	public void clickDelete() {
		getSession().clickDelete();
	}
	
	public void clickAddHook() {
		getSession().clickAddHook();
	}
	
	//ignored by session?
	public void clickCodegen() {
		// TODO Auto-generated method stub	
	}
	public void clickSpyMode() {
		// TODO Auto-generated method stub	
	}
	
	public boolean isRecordEnabled() {
		return getSession().isRecordEnabled();
	}
	
	public boolean isPauseEnabled() {
		return getSession().isPauseEnabled();
	}

	public boolean isRestartEnabled() {
		return getSession().isRestartEnabled();
	}

	public boolean isDeleteEnabled() {
		return getProvider().hasSelection();
	}
	
	public boolean isCodegenEnabled() {
		return hasEventsToProcess();
	}
	
	public boolean isHookEnabled() {
		return getSession().isHookEnabled();
	}
	
	
	
	public void sessionStarted() {
		setSession(new InSession()); //since in-sessions are stateful we don't use a constant
	}
	
	public void sessionEnded() {
		setSession(OUT_OF_SESSION);
	}
	
	public boolean inSession() {
		return getSession().inSession();
	}
	
	public void addListener(IChangeListener listener) {
		getListeners().add(listener);
	}
	
	public void removeListener(IChangeListener listener) {
		getListeners().remove(listener);
	}
	
	
	private void notifyChanged() {
		for (Iterator iterator = getListeners().iterator(); iterator.hasNext();) {
			IChangeListener listener = (IChangeListener) iterator.next();
			listener.panelChanged();
		}
	}
		
	public List getListeners() {
		return _listeners;
	}



	
}
