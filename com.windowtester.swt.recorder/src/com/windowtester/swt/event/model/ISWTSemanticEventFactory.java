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
package com.windowtester.swt.event.model;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticDragEvent;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.recorder.event.user.UISemanticEvent;

/**
 * A factory for creating semantic events.
 */
public interface ISWTSemanticEventFactory {

	/**
	 * Construct a SemanticKeyDownEvent based on this SWT event.
	 * @param event - the SWT event
	 * @return a semantic event
	 */
	public abstract SemanticKeyDownEvent createKeyDownEvent(Event event);

	public abstract SemanticKeyDownEvent createKeyDownEvent(TraverseEvent event);

	public abstract SemanticKeyDownEvent createKeyDownEvent(KeyEvent event);

	/**
	 * Construct a UISemanticEvent based on this SWT event.
	 * @param event - the SWT event
	 * @return a semantic event
	 */
	public abstract UISemanticEvent createWidgetSelectionEvent(Event event);

	/**
	 * Construct a UISemanticEvent based on this SWT event.
	 * @param e - the SWT event
	 * @return a semantic event
	 */
	public abstract UISemanticEvent createTabItemSelectionEvent(Event event);

	/**
	 * Construct a UISemanticEvent based on this SWT event.
	 * @param e - the SWT event
	 * @return a semantic event
	 */
	public abstract UISemanticEvent createListItemSelectionEvent(List list,
			String item, String mask);

	public abstract UISemanticEvent createComboSelectionEvent(Event event);

	public abstract UISemanticEvent createCComboSelectionEvent(Event event);

	/**
	 * Construct a UISemanticEvent based on this SWT event.
	 * @param columnIndex 
	 * @param e - the SWT event
	 * @return a semantic event
	 */
	public abstract UISemanticEvent createTableItemSelectionEvent(Table table,
			TableItem item, int columnIndex, String mask);

	/**
	 * Construct a SemanticMenuSelectionEvent based on this SWT event.
	 * @param event - the SWT event
	 * @return a semantic event
	 */
	public abstract SemanticMenuSelectionEvent createMenuSelectionEvent(
			Event event);

	/**
	 * Construct a UISemanticEvent based on this SWT event.
	 * @param e - the SWT event
	 * @return a semantic event
	 */
	public abstract UISemanticEvent createContextMenuSelectionEvent(
			Widget target, Event event);

	public abstract UISemanticEvent createTreeItemContextMenuSelectionEvent(
			SemanticTreeItemSelectionEvent treeItemSelectionEvent, Event event);

	public abstract UISemanticEvent createContextMenuSelectionEvent(
			UISemanticEvent selection, Event event);

	public abstract SemanticTreeItemSelectionEvent createTreeItemSelectionEvent(
			Event event, TreeEventType type, int button);

	public abstract UISemanticEvent createTreeItemSelectionEvent(TreeItem item,
			TreeEventType type, int button);

	public abstract UISemanticEvent createRawEvent(Event event);

	public abstract UISemanticEvent createDefaultSelectionEvent(Event event);

	public abstract UISemanticEvent createShellShowingEvent(Event event);

	public abstract UISemanticEvent createShellClosingEvent(Event event);

	public abstract UISemanticEvent createShellDisposedEvent(Event event);

	public abstract UISemanticEvent createMoveEvent(Event event, Widget widget);

	public abstract UISemanticEvent createResizeEvent(Event event, Widget widget);

	public abstract UISemanticEvent createFocusEvent(Event event, Widget widget);

	public abstract SemanticDragEvent createDragEvent(IUISemanticEvent event);

	public abstract SemanticDropEvent createDropEvent(DropTargetEvent event,
			Control targetControl);

	public abstract SemanticDropEvent createDragToEvent(Event event);

	public abstract UISemanticEvent createWidgetClosedEvent(Widget widget);

}