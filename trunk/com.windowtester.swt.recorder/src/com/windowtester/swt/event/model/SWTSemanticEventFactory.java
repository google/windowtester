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
import com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV1;

/**
 * A factory for building SWTSemanticEvents
 */
public class SWTSemanticEventFactory {

	/*
	 * The backing event generation strategy.
	 */
	static ISWTSemanticEventFactory _eventFactory = new SWTSemanticEventFactoryImplV1();
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Strategy Selection
	//
	////////////////////////////////////////////////////////////////////////////
	
	public static void setStrategy(ISWTSemanticEventFactory strategy) {
		_eventFactory = strategy;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Public factory methods
	//
	////////////////////////////////////////////////////////////////////////////
	
	public static SemanticKeyDownEvent createKeyDownEvent(Event event) {
		return _eventFactory.createKeyDownEvent(event);
	}

	public static SemanticKeyDownEvent createKeyDownEvent(TraverseEvent event) {
		return _eventFactory.createKeyDownEvent(event);
	}

	public static SemanticKeyDownEvent createKeyDownEvent(KeyEvent event) {
		return _eventFactory.createKeyDownEvent(event);
	}
	
	public static UISemanticEvent createWidgetSelectionEvent(Event event) {
		return _eventFactory.createWidgetSelectionEvent(event);
	}
	
	public static UISemanticEvent createTabItemSelectionEvent(Event event) {
		return _eventFactory.createTabItemSelectionEvent(event);
	}
	
	public static UISemanticEvent createListItemSelectionEvent(List list, String item, String mask) {
		return _eventFactory.createListItemSelectionEvent(list, item, mask);
	}
	
	public static UISemanticEvent createComboSelectionEvent(Event event) {
		return _eventFactory.createComboSelectionEvent(event);
	}
	
	public static UISemanticEvent createCComboSelectionEvent(Event event) {
		return _eventFactory.createCComboSelectionEvent(event);
	}
	
	public static UISemanticEvent createTableItemSelectionEvent(Table table, TableItem item, int columnIndex, String mask) {
		return _eventFactory.createTableItemSelectionEvent(table, item, columnIndex, mask);
	}

	public static SemanticMenuSelectionEvent createMenuSelectionEvent(Event event) {
		return _eventFactory.createMenuSelectionEvent(event);
	}
	
	public static UISemanticEvent createContextMenuSelectionEvent(Widget target, Event event) {
		return _eventFactory.createContextMenuSelectionEvent(target, event);
	}
	
	public static UISemanticEvent createTreeItemContextMenuSelectionEvent(SemanticTreeItemSelectionEvent treeItemSelectionEvent, Event event) {
		return _eventFactory.createTreeItemContextMenuSelectionEvent(treeItemSelectionEvent, event);
	}
	
	public static UISemanticEvent createContextMenuSelectionEvent(UISemanticEvent selection, Event event) {
		return _eventFactory.createContextMenuSelectionEvent(selection, event);
	}
	
	public static SemanticTreeItemSelectionEvent createTreeItemSelectionEvent(Event event, TreeEventType type, int button) {
		return _eventFactory.createTreeItemSelectionEvent(event, type, button);
	}
	
	public static UISemanticEvent createTreeItemSelectionEvent(TreeItem item, TreeEventType type, int button) {
		return _eventFactory.createTreeItemSelectionEvent(item, type, button);
	}
		
	public static UISemanticEvent createRawEvent(Event event) {
		return _eventFactory.createRawEvent(event);
	}

	public static UISemanticEvent createDefaultSelectionEvent(Event event) {
		return _eventFactory.createDefaultSelectionEvent(event);
	}
	
	public static UISemanticEvent createShellShowingEvent(Event event) {
		return _eventFactory.createShellShowingEvent(event);
	}
	
	public static UISemanticEvent createShellClosingEvent(Event event) {
		return _eventFactory.createShellClosingEvent(event);
	}
	
	public static UISemanticEvent createShellDisposedEvent(Event event) {
		return _eventFactory.createShellDisposedEvent(event);
	}
	
	public static UISemanticEvent createMoveEvent(Event event, Widget widget) {
		return _eventFactory.createMoveEvent(event, widget);
	}
	
	public static UISemanticEvent createResizeEvent(Event event, Widget widget) {
		return _eventFactory.createResizeEvent(event, widget);
	}

	public static UISemanticEvent createFocusEvent(Event event, Widget widget) {
		return _eventFactory.createFocusEvent(event, widget);
	}
	
	public static SemanticDragEvent createDragEvent(IUISemanticEvent event) {
		return _eventFactory.createDragEvent(event);
	}

	public static SemanticDropEvent createDropEvent(DropTargetEvent event, Control targetControl) {
		return _eventFactory.createDropEvent(event, targetControl);
	}

	public static SemanticDropEvent createDragToEvent(Event event) {
		return _eventFactory.createDragToEvent(event);
	}

	public static UISemanticEvent createWidgetClosedEvent(Widget widget) {
		return _eventFactory.createWidgetClosedEvent(widget);
	}
	
}
