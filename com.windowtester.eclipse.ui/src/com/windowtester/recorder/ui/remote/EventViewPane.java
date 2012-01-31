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
package com.windowtester.recorder.ui.remote;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.windowtester.eclipse.ui.session.SessionMonitor;
import com.windowtester.eclipse.ui.viewers.EventSequenceTreeViewer;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.recorder.ui.EventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.SequenceCommandLabelProvider;
import com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.ui.internal.corel.model.Event;
import com.windowtester.ui.util.ICommand;

public class EventViewPane implements ISequenceListener {

	private static final int BORDER_STYLE = SWT.NULL;

	private static final int TREE_STYLE_BITS = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | BORDER_STYLE;
	
	private EventSequenceTreeViewer eventTree;
	private final Composite viewPane;
	
	private final IEventSequenceModel sequenceModel;

	public EventViewPane(Composite viewPane, IEventSequenceModel sequenceModel) {
		this.viewPane = viewPane;
		this.sequenceModel = sequenceModel != null ? sequenceModel : stubEventModel();
		createTree();
	}


	private void createTree() {
		this.eventTree = new EventSequenceTreeViewer(getSessionMonitor(), viewPane, sequenceModel, TREE_STYLE_BITS) {
			public void refresh() {
				super.refresh();
				viewPane.pack(true);
			}
		};
		if (sequenceModel != null)
			sequenceModel.addListener(this);
		fixLayout();
	}


	private SessionMonitor getSessionMonitor() {
		return new SessionMonitor();
	}


	private void fixLayout() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.verticalIndent = 0;
		eventTree.getTreeViewer().getTree().setLayoutData(data);
		eventTree.getTreeViewer().getTree().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	}
	
	
	private EventSequenceModel stubEventModel() {
		return new EventSequenceModel() {
			{
				EventInfo info = new EventInfo();
				info.hierarchyInfo = new ButtonLocator("myButton");
				add(new Event(new SemanticWidgetSelectionEvent(info)));
			}
			protected SequenceCommandLabelProvider createSequenceLabelProvider() {
				return new SequenceCommandLabelProvider() {
					public ImageDescriptor getImage(ICommand command) {
						return null;
					}
				};
			}
		};
	}


	public boolean isHidden() {
		return eventTree == null;
	}
	
	public void hide() {
		eventTree.getTreeViewer().getTree().dispose(); //yuck
		if (sequenceModel != null)
			sequenceModel.removeListener(this);
		eventTree = null;
	}
	
	public void show() {
		createTree();
	}


	public EventViewPane hidden() {
		hide();
		return this;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener#sequenceChanged()
	 */
	public void sequenceChanged() {
		if (eventTree != null)
			eventTree.refresh();
	}
	

}
