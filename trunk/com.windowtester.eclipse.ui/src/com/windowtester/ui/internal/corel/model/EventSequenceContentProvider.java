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
package com.windowtester.ui.internal.corel.model;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener;
import com.windowtester.eclipse.ui.views.RecorderConsoleView;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.recorder.ISemanticEventProvider;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticEventAdapter;
import com.windowtester.recorder.event.user.SemanticShellEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;
import com.windowtester.ui.core.model.IEventSequence;

public class EventSequenceContentProvider extends LabelProvider implements ITreeContentProvider, ISessionListener {

	

	private final ISessionMonitor sessionMonitor;
	
	private TreeViewer treeViewer;

	SemanticEventHandler eventHandler;
	private final IEventSequence sequence;
	
	//TODO: consider state object
	boolean disposed;

	private static final EventSequenceLabelProvider LABEL_PROVIDER = new EventSequenceLabelProvider();

	private static final boolean DEBUGGING_LOCALLY = false;

	
	class SemanticEventHandler extends SemanticEventAdapter {

		/* (non-Javadoc)
		 * @see com.windowtester.recorder.event.user.SemanticEventAdapter#notify(com.windowtester.recorder.event.IUISemanticEvent)
		 */
		public void notify(IUISemanticEvent event) {
			
			/*
			 * TEMPORARY -- when this is not in the same Display as the app under test, this can go away!
			 */
			if (DEBUGGING_LOCALLY) {
				if (isInRecordViewScope(event))
					return; // ignore
				if (isEmptyShellEvent(event))
					return; // ignore
				if (isInspectionRequest(event))
					return; // ignore
			}
			if (isInspectionRequest(event)) {
				SemanticWidgetInspectionEvent inspection = (SemanticWidgetInspectionEvent)event;
				//ignore if there are no flagged properties
				if (inspection.getProperties().flagged().isEmpty())
					return;
			}
			
			getCachedSequence().add(new Event(event));
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					TreeViewer viewer = getViewer();
					if (viewer == null)
						return;
					viewer.refresh();
				}


			});
		}

		private boolean isInspectionRequest(IUISemanticEvent event) {
			return event instanceof SemanticWidgetInspectionEvent;
		}

		private boolean isDisposed() {
			return disposed || Display.getDefault().isDisposed();
		}
		
		private boolean isEmptyShellEvent(IUISemanticEvent event) {
			if (event instanceof SemanticShellEvent) {
				SemanticShellEvent shellEvent = (SemanticShellEvent)event;
				String name = shellEvent.getName();
				if (name == null)
					return true;
				name = name.trim();
				return name.length() == 0 || name.equals("");	
			}
			return false;
		}

		private boolean isInRecordViewScope(IUISemanticEvent event) {
			IWidgetIdentifier hierarchyInfo = event.getHierarchyInfo();
			if (hierarchyInfo instanceof WidgetLocator) {
				if (hierarchyInfo instanceof ContributedToolItemLocator) {
					return ((ContributedToolItemLocator)hierarchyInfo).getID().startsWith(RecorderConsoleView.ACTION_TAG_PREFIX);
				}
			
				WidgetLocator loc = (WidgetLocator)hierarchyInfo;
				//find top-most
				while (loc.getParentInfo() != null) {
					loc = loc.getParentInfo();
				}
				if (loc instanceof ViewLocator) {
					return ((ViewLocator)loc).getViewId().equals(UiPlugin.RECORDER_VIEW_ID);
				}
			}
			return false;
		}
	}
	
	public IEventSequence getCachedSequence() {
		return sequence;
	}
	
	
	
	public EventSequenceContentProvider(ISessionMonitor sessionMonitor, IEventSequence sequence) {
	 	this.sessionMonitor = sessionMonitor;
		this.sequence = sequence;
		sessionMonitor.addListener(this);
	}


	public ISessionMonitor getSessionMonitor() {
		return sessionMonitor;
	}
	
	protected ISemanticEventProvider getRecorder() {
		return getSessionMonitor().getCurrent().getRecorder();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IEventGroup)
			return ((IEventGroup)parentElement).getEvents();
		if (parentElement instanceof IEventSequence)
			return ((IEventSequence)parentElement).getEvents();
		if (parentElement instanceof IEvent) {
//skipping widget locators for now...			
//			IWidgetIdentifier hierarchyInfo = ((IEvent)parentElement).getUIEvent().getHierarchyInfo();
//			if (hierarchyInfo != null)
//				return new Object[]{hierarchyInfo};
		}	
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof IEvent)
			return ((IEvent)element).getGroup();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element) != null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IEventSequence)
			return ((IEventSequence)inputElement).getEvents();
		return new Object[]{inputElement};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#dispose()
	 */
	public void dispose() {
		disposed = true;
		getRecorder().removeListener(getEventHandler());
		//EventRecorderController.getInstance().removeListener(getEventHandler());
	}

	private ISemanticEventListener getEventHandler() {
		if (eventHandler == null)
			eventHandler = new SemanticEventHandler();
		return eventHandler;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		treeViewer = (TreeViewer)viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object e) {
		return LABEL_PROVIDER.getText(e);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object e) {
		return LABEL_PROVIDER.getImage(e);
	}


	protected final TreeViewer getViewer() {
		return treeViewer;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener#started(com.windowtester.eclipse.ui.session.ISession)
	 */
	public void started(ISession session) {
		session.getRecorder().addListener(getEventHandler());
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener#ended(com.windowtester.eclipse.ui.session.ISession)
	 */
	public void ended(ISession session) {
		session.getRecorder().removeListener(getEventHandler());
	}
	
	
}
