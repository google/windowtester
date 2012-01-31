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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.UIPlugin;

import com.windowtester.eclipse.ui.UiPlugin;

/**
 * factory for the actions behind the remote.
 */
class RemoteActionFactory  {

	
	private final RemotePresenter presenter;
	
	RemoteActionFactory(RemotePresenter presenter) {
		this.presenter = presenter;
	}
	
	static abstract class RemoteAction extends Action {
		void enable() {
			setEnabled(true);
		}
		void disable() {
			setEnabled(false);
		}
		
	}
	
	RemoteAction RECORD = new Record() {
		public void run() {
			presenter.record();
		}
	};
	
	RemoteAction PAUSE = new Pause() {
		public void run() {
			presenter.pause();
		}
	};
	
	
	RemoteAction HOOK = new Hook() {
		public void run() {			
			presenter.hook();
		}
	};
	
	RemoteAction SPY = new Spy() {
		public void run() {
			presenter.spy();
		}	
	};
	
	
	
	
	public static final String ACTION_TAG_PREFIX = "com.windowtester.recorder.ui.recorder.actions.";
	
	private static class Record extends RemoteAction {
		public Record () {
			setText("Record");
			setImageDescriptor(imageDescriptor("start_recording.gif"));
			setDisabledImageDescriptor(imageDescriptor("start_recording_dis.gif"));
			setId(actionTag("record"));
		}
	}

	private static class Pause extends RemoteAction {
		public Pause () {
			setText("Pause");
			setImageDescriptor(imageDescriptor("pause.gif"));
			setDisabledImageDescriptor(imageDescriptor("pause_dis.gif"));
			setId(actionTag("pause"));
		}
	}
	
	private static class Hook extends RemoteAction {
		public Hook () {
			setText("Add Assertion Hook");
			setImageDescriptor(imageDescriptor("assertion_hook.gif"));
			setDisabledImageDescriptor(imageDescriptor("assertion_hook_dis.gif"));
			setId(actionTag("hook"));
		}
	}
	
	private static class Spy extends RemoteAction {
		public Spy () {
			setText("Toggle Spy Mode");
			setImageDescriptor(imageDescriptor("spy.gif"));
			setDisabledImageDescriptor(imageDescriptor("spy_dis.gif"));
			setId(actionTag("spy"));
		}
	}	
	
	
	
	protected static String actionTag(String id) {
		return ACTION_TAG_PREFIX + id;
	}
	
	protected static ImageDescriptor imageDescriptor(String imageFilePath) {
		return UIPlugin.imageDescriptorFromPlugin(UiPlugin.PLUGIN_ID, "icons/full/obj16/" + imageFilePath);
	}
	

	
	
	
}
