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
package com.windowtester.eclipse.ui.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.ui.core.model.IEventGroup;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.internal.corel.model.Event;
import com.windowtester.ui.internal.corel.model.EventSequenceContentProvider;

/**
 * A tree viewer for displaying sequences of semantic events.
 */
public class EventSequenceTreeViewer {

	private class EventSequenceTreeCellModifier implements ICellModifier {
		
		private boolean _enabled;
		
		public void setEnabled(boolean enabled) {
			_enabled = enabled;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
		 *      java.lang.String)
		 */
		public boolean canModify(Object element, String property) {
			return _enabled;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
		 *      java.lang.String)
		 */
		public Object getValue(Object element, String property) {
			return ((IEventGroup)element).getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
		 *      java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) {
			TreeItem item = (TreeItem) element;
			((IEventGroup) item.getData()).setName((String)value);
			getTreeViewer().update(item.getData(), null);
		}
	}

	
	
	
	private static final int DEFAULT_STYLE_BITS = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
	private TreeViewer treeViewer;
	private EventSequenceContentProvider contentProvider;
	private IEventSequenceModel sequenceModel;
	private final int treeStyleBits;
	private com.windowtester.eclipse.ui.viewers.EventSequenceTreeViewer.EventSequenceTreeCellModifier cellModifier;


	public EventSequenceTreeViewer(ISessionMonitor sessionMonitor, Composite parent, IEventSequenceModel sequenceModel, int treeStyleBits) {
		this.treeStyleBits = treeStyleBits;
		initializeContentProvider(sessionMonitor, sequenceModel);
		initializeTree(parent);
	}
	
	
	public EventSequenceTreeViewer(ISessionMonitor sessionMonitor, Composite parent, IEventSequenceModel sequenceModel) {
		this(sessionMonitor, parent, sequenceModel, DEFAULT_STYLE_BITS);
	}
	
	public EventSequenceTreeViewer(Composite parent, IEventSequenceModel sequenceModel) {
		this(UiPlugin.getDefault().getSessionMonitor(), parent, sequenceModel);
	}

	private void initializeContentProvider(ISessionMonitor sessionMonitor, IEventSequenceModel sequenceModel) {
		this.sequenceModel   = sequenceModel;
		this.contentProvider = new EventSequenceContentProvider(sessionMonitor, sequenceModel);
	}
	
	private void initializeTree(Composite parent) {
		
		Tree tree = new Tree(parent, getTreeStyle());
		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(getContentProvider());
		treeViewer.setLabelProvider(getLabelProvider());
		treeViewer.setInput(getInitalInput());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getSequenceModel().select(getEvents(event));
			}
		});
		EventSequenceDNDSupport.addTo(this);
		setUpCellEditors(treeViewer);
	}
	
	private void setUpCellEditors(TreeViewer treeViewer) {
		cellModifier = new EventSequenceTreeCellModifier();
		treeViewer.setCellModifier(cellModifier);
		treeViewer.setColumnProperties(new String[] { "column" });
		treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(treeViewer.getTree()) });
	}

	private int getTreeStyle() {
		return treeStyleBits;
	}

	protected ISemanticEvent[] getEvents(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		return adapt(selection.toArray());
	}

	private ISemanticEvent[] adapt(Object[] elems) {
		ISemanticEvent[] events = new ISemanticEvent[elems.length];
		for (int i = 0; i < elems.length; i++) {
			events[i] = adaptToSemanticEvent(elems[i]);
		}
		return events;
	}

	private ISemanticEvent adaptToSemanticEvent(Object object) {
		if (object instanceof ISemanticEvent)
        	return (ISemanticEvent)object;
        if (object instanceof IAdaptable) {
        	IAdaptable adapted = (IAdaptable)object;
        	return (ISemanticEvent) adapted.getAdapter(ISemanticEvent.class);
        }
        return null; //shouldn't get here!
	}
	
	
	protected final IEventSequenceModel getSequenceModel() {
		return sequenceModel;
	}
	
	public final ISemanticEvent[] getSelection() {
		return getSequenceModel().getSelection();
	}
	
	private ILabelProvider getLabelProvider() {
		return getContentProvider();
	}

	private Object getInitalInput() {
		return getContentProvider().getCachedSequence();
	}
	
	private EventSequenceContentProvider getContentProvider() {
		return contentProvider;
	}

	public final TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public IEventSequence getSequence() {
		return getContentProvider().getCachedSequence();
	}

//	private boolean _updating;
	
	public void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
//				if (_updating) //avoid infinite regress!
//					return;
//				_updating = true;
//				updateSelection();
				
				getTreeViewer().refresh();
				updateSelection();
				
				
//				_updating = false;
			}
		});
	}



	public void setExpandedState(IEventGroup group, boolean b) {
		getTreeViewer().setExpandedState(group, true);
	}


	public void setGroupedState(IEventGroup group) {
		
		refresh();
		setExpandedState(group, true);
		getCellModifier().setEnabled(true);
		getTreeViewer().editElement(group, 0);
		getCellModifier().setEnabled(false);
	}

	private EventSequenceTreeCellModifier getCellModifier() {
		return cellModifier;
	}

	public void updateSelection() {

		ISemanticEvent[] modelSelection = getSelection();
		List<TreeItem> toSelect = new ArrayList<TreeItem>();
		
		Tree tree = getTreeViewer().getTree();
		TreeItem[] items = tree.getItems();
		if (items.length == 0)
			return;
		
		for (int i=0; i < items.length; ++i) {
			TreeItem item = items[i];
			Event event = (Event) item.getData();
			for (int j = 0; j < modelSelection.length; j++) {
				if (modelSelection[j] == event)
					toSelect.add(item);
			}
		}
		if (toSelect.isEmpty())
			return;
		tree.setSelection(toSelect.toArray(new TreeItem[]{}));
	}

	
}
