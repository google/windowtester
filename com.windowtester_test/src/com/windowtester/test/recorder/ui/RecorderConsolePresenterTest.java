package com.windowtester.test.recorder.ui;

import junit.framework.TestCase;

import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.eclipse.ui.views.RecorderConsolePresenter;
import com.windowtester.recorder.ui.EventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceView;
import com.windowtester.recorder.ui.IRecorderConsoleActionHandler;
import com.windowtester.recorder.ui.IRecorderPanelModel;
import com.windowtester.recorder.ui.IRecorderPanelView;
import com.windowtester.recorder.ui.RecorderConsoleActionAdapter;
import com.windowtester.recorder.ui.RecorderPanelModel;
import com.windowtester.ui.core.model.ISemanticEvent;

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
public class RecorderConsolePresenterTest extends TestCase {

	
	static class StubPanelView implements IRecorderPanelView {
		
		public void addHandler(IRecorderConsoleActionHandler listener) {}

		public void setDeleteEnabled(boolean isEnabled) {}

		public void setPauseEnabled(boolean isEnabled) {}

		public void setRecordEnabled(boolean isEnabled) {}
		
		public void setCodegenEnabled(boolean isEnabled) {}

		public void clickDelete() {}

		public void clickPause() {}

		public void clickRecord() {}

		public void clickRestart() {}
		
		public void clickCodegen() {}
		
		public void clickAddHook() {}
		
		public void clearSessionState() {}

		public void clickSpyMode() {}

		public void removeHandler(IRecorderConsoleActionHandler handler) {}
	}
	
	static class StubSequenceView implements IEventSequenceView {
		public void clickCodegen() {}

		public void setCodegenEnabled(boolean enabled) {}
		
		public void refresh() {}
	}
	
	
	
	
	public void testRecordClickCausesJustOneViewUpdate() {
		IRecorderPanelModel model = new RecorderPanelModel();
		final int[] setCount = new int[1];
		IRecorderPanelView view = new StubPanelView() {
			@Override
			public void setPauseEnabled(boolean isEnabled) {
				setCount[0]++;
			}
		};
		presenter(model, view);
		model.sessionStarted();
		assertEquals(1, setCount[0]);
		model.clickRecord();
		assertEquals(2, setCount[0]);
	}


	public void testSessionStartCausesViewUpdate() {
		IRecorderPanelModel model = new RecorderPanelModel();
		final int[] setCount = new int[1];
		IRecorderPanelView view = new StubPanelView() {
			@Override
			public void setPauseEnabled(boolean isEnabled) {
				setCount[0]++;
			}
		};
		presenter(model, view);
		model.sessionStarted();
		assertEquals(1, setCount[0]);
	}
	
	public void testSessionEndCausesViewUpdate() {
		IRecorderPanelModel model = new RecorderPanelModel();
		final int[] setCount = new int[1];
		IRecorderPanelView view = new StubPanelView() {
			@Override
			public void setPauseEnabled(boolean isEnabled) {
				setCount[0]++;
			}
		};
		presenter(model, view);
		model.sessionStarted();
		int current = setCount[0];
		model.sessionEnded();
		assertEquals(current+1, setCount[0]);
	}

	
	public void testSessionStartCausesSequenceClear() {
		IRecorderPanelModel panelModel = new RecorderPanelModel();
		IRecorderPanelView panelView   = new StubPanelView();
		IEventSequenceModel eventModel = new EventSequenceModel();
		IEventSequenceView eventView   = new StubSequenceView();
		RecorderConsolePresenter presenter = presenter(panelModel, panelView, eventModel, eventView);
		presenter.sessionStarted();
		eventModel.add(new ISemanticEvent(){});
		presenter.sessionEnded();
		presenter.sessionStarted();
		assertEquals(0, eventModel.getEvents().length);
	}
	
	
	
	//tested here because presenter sets up listeners...
	public void testCodegenDisabledIfSeqEmpty() {
		IRecorderPanelModel panelModel = new RecorderPanelModel();
		IRecorderPanelView panelView   = new StubPanelView();
		IEventSequenceModel eventModel = new EventSequenceModel();
		IEventSequenceView eventView   = new StubSequenceView();
		presenter(panelModel, panelView, eventModel, eventView);
		assertFalse(panelModel.isCodegenEnabled());
	}
	
	public void testCodegenEnabledIfSeqNotEmpty() {
		IRecorderPanelModel panelModel = new RecorderPanelModel();
		IRecorderPanelView panelView   = new StubPanelView();
		IEventSequenceModel eventModel = new EventSequenceModel();
		IEventSequenceView eventView   = new StubSequenceView();
		presenter(panelModel, panelView, eventModel, eventView);
		eventModel.add(new ISemanticEvent(){});
		assertTrue(panelModel.isCodegenEnabled());
	}
		
	
	public void testDeleteDisabledIfSelectionEmpty() {
		IRecorderPanelModel panelModel = new RecorderPanelModel();
		IRecorderPanelView panelView   = new StubPanelView();
		IEventSequenceModel eventModel = new EventSequenceModel();
		IEventSequenceView eventView   = new StubSequenceView();
		presenter(panelModel, panelView, eventModel, eventView);
		//ISemanticEvent event = new ISemanticEvent(){};
		//eventModel.add(event);
		assertFalse(panelModel.isDeleteEnabled());
	}
	
	public void testDeleteEnabledIfSelectionNotEmpty() {
		IRecorderPanelModel panelModel = new RecorderPanelModel();
		IRecorderPanelView panelView   = new StubPanelView();
		IEventSequenceModel eventModel = new EventSequenceModel();
		IEventSequenceView eventView   = new StubSequenceView();
		presenter(panelModel, panelView, eventModel, eventView);
		ISemanticEvent event = new ISemanticEvent(){};
		eventModel.add(event);
		eventModel.select(new ISemanticEvent[]{event});
		assertTrue(panelModel.isDeleteEnabled());
	}	
	
	private RecorderConsolePresenter presenter(IRecorderPanelModel model,
			IRecorderPanelView view) {
		return presenter(model, view, new EventSequenceModel(), new StubSequenceView());
	}
	
	
	private RecorderConsolePresenter presenter(IRecorderPanelModel model,
			IRecorderPanelView view, IEventSequenceModel eventSequenceModel,
			IEventSequenceView eventSequenceView) {
		return new RecorderConsolePresenter(model, view, eventSequenceModel, eventSequenceView) {
			@Override
			protected ISessionMonitor getSessionMonitor() {
				return new ISessionMonitor() {
					public void addListener(ISessionListener listener) {
						//no-op
					}
					public ISession getCurrent() {
						return null;
					}
					public boolean inSession() {
						return false;
					}
					public void removeListener(ISessionListener listener) {
						//no-op
					}
					public void sessionEnded(ISession session) {
						//no-op
					}
					public void sessionStarted(ISession session) {
						//no-op
					}
				};
			}
			@Override
			public IRecorderConsoleActionHandler getCodegenHandler() {
				return new RecorderConsoleActionAdapter();
			}
		};
	}

	
}
