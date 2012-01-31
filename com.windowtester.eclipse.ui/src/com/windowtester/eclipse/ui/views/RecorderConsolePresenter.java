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
package com.windowtester.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener;
import com.windowtester.recorder.event.user.ICompositeEvent;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceView;
import com.windowtester.recorder.ui.IRecorderConsoleActionHandler;
import com.windowtester.recorder.ui.IRecorderPanelModel;
import com.windowtester.recorder.ui.IRecorderPanelView;
import com.windowtester.recorder.ui.RecorderConsoleActionAdapter;
import com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener;
import com.windowtester.recorder.ui.IRecorderPanelModel.IChangeListener;
import com.windowtester.swt.codegen.wizards.NewTestTypeWizard;
import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.internal.corel.model.Event;

/**
 * Console presenter that wires together view and model.
 *
 */
public class RecorderConsolePresenter implements IRecorderConsoleActionHandler /*, IRecorderPanelModel*/, ISequenceListener, IChangeListener, ISessionListener {

	
	private final IRecorderPanelModel _panelModel;
	private final IRecorderPanelView _panelView;

	private final List _handlers = new ArrayList();
	private final IEventSequenceModel _sequenceModel;
	private final IEventSequenceView _sequenceView;	

	private ExecutionProfile _currentProfile = new DefaultExecutionProfile();
	
	private final IRecorderConsoleActionHandler _codegenHandler = new CodegenHandler();

	
	static class DefaultExecutionProfile extends ExecutionProfile {
		DefaultExecutionProfile() {
			setExecType(ExecutionProfile.RCP_EXEC_TYPE);
		}
	}
	
	
	/**
	 * This should probably move elsewhere...
	 */
	class CodegenHandler extends RecorderConsoleActionAdapter {
		public void clickCodegen() {
			if (!eventsToCodegen())
				return;
			doCodegen();			
		}

		private boolean eventsToCodegen() {
			return getSequenceModel().getEvents().length != 0;
		}

		private void doCodegen() {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
			        final NewTestTypeWizard wizard = new NewTestTypeWizard(asFlattenedList(getSequenceModel().getEvents()), getCurrentProfile());
			        WizardDialog dialog = new WizardDialog(display.getActiveShell(), wizard);
			        dialog.open();
				}
	    	});
		}
		
		/**
		 * Flattens out any composite events.
		 */
		private List asFlattenedList(ISemanticEvent[] events) {
			List l = new ArrayList();
			for (int i = 0; i < events.length; i++) {
				ISemanticEvent event = events[i];
				if (event instanceof Event) {
					Object semantic = ((Event) event).getUIEvent();
					if (semantic instanceof ICompositeEvent) {
						ICompositeEvent composite = (ICompositeEvent)semantic;
						com.windowtester.recorder.event.ISemanticEvent[] composedEvents = composite.getComponents();
						for (int j = 0; j < composedEvents.length; j++) {
							l.add(new Event(composedEvents[j]));
						}
					} else {
						l.add(semantic);
					}
				} else {
					l.add(event);
				}
			}
			return l;
		}
	}

	
	public RecorderConsolePresenter(IRecorderPanelModel recorderPanelModel, IRecorderPanelView view, IEventSequenceModel seqModel, IEventSequenceView seqView) {
		_panelModel = recorderPanelModel;
		_panelView = view;
		_sequenceModel = seqModel;
		_sequenceView = seqView;
		_panelModel.setEventProvider(_sequenceModel);
		hookListeners();
	}

	public IRecorderConsoleActionHandler getCodegenHandler() {
		return _codegenHandler;
	}
	
	public ExecutionProfile getCurrentProfile() {
		return _currentProfile;
	}
	
	public void setCurrentProfile(ExecutionProfile currentProfile) {
		_currentProfile = currentProfile;
	}
	
	
	/**
	 * Perform teardown.
	 */
	public void dispose() {
		unhookListeners();
	}
	
	private void hookListeners() {
		getSessionMonitor().addListener(this);
		getPanelModel().addListener(this);
		getSequenceModel().addListener(this);
	}

	private void unhookListeners() {
		getSequenceModel().removeListener(this);
		getPanelModel().removeListener(this);
		getSessionMonitor().removeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener#sequenceChanged()
	 */
	public void sequenceChanged() {
		update();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel.IChangeListener#panelChanged()
	 */
	public void panelChanged() {
		updatePanel();
	}


	protected ISessionMonitor getSessionMonitor() {
		return UiPlugin.getDefault().getSessionMonitor();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener#ended(com.windowtester.eclipse.ui.session.ISession)
	 */
	public void ended(ISession session) {
		sessionEnded();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener#started(com.windowtester.eclipse.ui.session.ISession)
	 */
	public void started(ISession session) {
		setCurrentProfile(session.getExecutionProfile());
		sessionStarted();
	}
	
	public void addHandler(IRecorderConsoleActionHandler handler) {
		getHandlers().add(handler);
	}

	public void removeHandler(IRecorderConsoleActionHandler handler) {
		getHandlers().remove(handler);
	}
	
	public IRecorderPanelModel getPanelModel() {
		return _panelModel;
	}
	
	public IEventSequenceModel getSequenceModel() {
		return _sequenceModel;
	}
	
	private IRecorderPanelView getPanelView() {
		return _panelView;
	}
	
	public IEventSequenceView getSequenceView() {
		return _sequenceView;
	}
	
	protected List getHandlers() {
		return _handlers;
	}
	
	
	public void clickDelete() {
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickDelete();
		}
		getSequenceModel().clickDelete();
	}

	public void clickPause() {
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickPause();
		}
		getPanelModel().clickPause();
	}

	public void clickRecord() {
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickRecord();
		}
		getPanelModel().clickRecord();
	}

	public void clickRestart() {
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickRestart();
		}
		getPanelModel().clickRestart();
	}

	public void clickAddHook() {
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickAddHook();
		}
		getPanelModel().clickAddHook();
	}
	
	
	public void clickCodegen() {		
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickCodegen();
		}
		//this could be a listener just as well
		getCodegenHandler().clickCodegen();
	}
	
	public void clickSpyMode() {
		for (Iterator iter = getHandlers().iterator(); iter.hasNext(); ) {
			((IRecorderConsoleActionHandler)iter.next()).clickSpyMode();
		}
		getPanelModel().clickSpyMode();
	}
	

	public void update() {
		updatePanel();
		updateSequenceViewer();
	}
	
	private void updateSequenceViewer() {
		getSequenceView().refresh();
	}

	private void updatePanel() {
		IRecorderPanelModel panelModel = getPanelModel();
		IRecorderPanelView panelView = getPanelView();
		panelView.setRecordEnabled(panelModel.isRecordEnabled());
		panelView.setDeleteEnabled(panelModel.isDeleteEnabled());
		panelView.setPauseEnabled(panelModel.isPauseEnabled());
		panelView.setCodegenEnabled(panelModel.isCodegenEnabled());
	}
	

	public boolean isDeleteEnabled() {
		return getPanelModel().isDeleteEnabled();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel#getPauseEnabled()
	 */
	public boolean isPauseEnabled() {
		return getPanelModel().isPauseEnabled();
	}


	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel#isRecordEnabled()
	 */
	public boolean isRecordEnabled() {
		return getPanelModel().isRecordEnabled();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel#isRestartEnabled()
	 */
	public boolean isRestartEnabled() {
		return getPanelModel().isRestartEnabled();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel#inSession()
	 */
	public boolean inSession() {
		return getPanelModel().inSession();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel#sesssionEnded()
	 */
	public void sessionEnded() {
		getPanelModel().sessionEnded();
		updatePanel(); //this refresh feels out of place: rub: need to refresh whenever a session starts
		/*
		 * The old behavior was to pop up the codegen dialog at session end;
		 * for now we want to stay consistent with that...
		 */
		getCodegenHandler().clickCodegen();
		
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelModel#sesssionStarted()
	 */
	public void sessionStarted() {
		getSequenceModel().sessionStarted();
		getPanelModel().sessionStarted();
		updatePanel(); //this refresh feels out of place: rub: need to refresh whenever a session starts
	}

	/**
	 * @param selected
	 * @return
	 * @since 3.9.1
	 */
	public IEventGroup group(IEvent[] selected) {
		return _sequenceModel.group(selected);
	}






	

	
	
}
