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
package com.windowtester.swt.event.model.factory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.MenuItemTester;

import com.windowtester.internal.runtime.event.StyleBits;
import com.windowtester.internal.runtime.locator.LocatorPathUtil;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.swt.internal.finder.WidgetLocatorService;
import com.windowtester.runtime.swt.internal.util.TextUtils;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;

/**
 * A factory for building SWTSemanticEvents
 */
public class SWTSemanticEventFactoryImplV2 extends SWTSemanticEventFactoryImplV1 {

	////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	////////////////////////////////////////////////////////////////////////////
	
	private  final MenuItemTester _menuItemTester   = new MenuItemTester();
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Customized actions
	//
	////////////////////////////////////////////////////////////////////////////

	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV1#createDragToEvent(org.eclipse.swt.widgets.Event)
	 */
	public SemanticDropEvent createDragToEvent(Event event) {
		UISemanticEvent selection = createWidgetSelectionEvent(event);
		return new SemanticDropEvent(selection);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV1#createMenuSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public SemanticMenuSelectionEvent createMenuSelectionEvent(Event event) {
		
		MenuItem item = (MenuItem)event.widget;
		
		String pathString = _menuItemTester.getPathString(item);
		pathString = LocatorPathUtil.stripAmpersands(pathString);
		pathString = LocatorPathUtil.stripAccelerators(pathString);
				
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = item.getClass().getName();
		info.hierarchyInfo = new WidgetLocatorService().inferIdentifyingInfo(item);
		info.button        = event.button;
		info.x             = event.x;
		info.y             = event.y;
		
		// TODO [author=Dan] I would have called stripAmpersands and stripAccelerators
		// from WidgetIdentifier$LocatorMapper.map(Widget) to construct a MenuItemLocator
		// with a properly normalized path, but WidgetIdentifier.identify(Widget)
		// has a loop that calls isUniquelyIdentifying(...) which does not perform the
		// same runtime matching as happens during playback. So I placed the hack here.
		
		if (info.hierarchyInfo instanceof com.windowtester.runtime.swt.locator.MenuItemLocator) {
			com.windowtester.runtime.swt.locator.MenuItemLocator oldLocator 
				= (com.windowtester.runtime.swt.locator.MenuItemLocator) info.hierarchyInfo;
			com.windowtester.runtime.swt.locator.MenuItemLocator newLocator 
				= new com.windowtester.runtime.swt.locator.MenuItemLocator(pathString, (SWTWidgetLocator) oldLocator.getParentInfo());
			oldLocator.copyDataTo(newLocator);
			info.hierarchyInfo = newLocator;
		}
		
		SemanticMenuSelectionEvent menuSelect = new SemanticMenuSelectionEvent(info);
		
		String label = extractMenuItemLabel(event);
		label = LocatorPathUtil.stripAmpersands(label);
		menuSelect.setItemLabel(label);
		menuSelect.setPath(pathString);
		
		/*
		 * Test for pulldown menu case:
		 */
		
		Menu menu = item.getParent();
		boolean isPullDown = isPullDown(menu);
		if (isPullDown)
			menuSelect.setStyle(StyleBits.PULL_DOWN);
				
		return menuSelect;
	}

	private boolean isPullDown(Menu menu) {
		
		Menu parentMenu = menu.getParentMenu();
		if (parentMenu != null)
			return isPullDown(parentMenu);
		int style = menu.getStyle();
		return !isMenuBar(style);
	}
	

	private boolean isMenuBar(int style) {
		return (style & SWT.BAR) == SWT.BAR;
	}
	
	

	public PullDownMenuItemLocator createPullDownMenuSelection(SWTWidgetLocator locator, SemanticMenuSelectionEvent menuSelect) {
		return new PullDownMenuItemLocator(menuSelect.getPathString(), locator);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTreeItemContextMenuSelectionEvent(com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent, org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createTreeItemContextMenuSelectionEvent(SemanticTreeItemSelectionEvent treeItemSelectionEvent, Event event) {
		String menuPath = _menuItemTester.getPathString((MenuItem)event.widget);
		menuPath = LocatorPathUtil.stripAmpersands(menuPath);
		menuPath = LocatorPathUtil.stripAccelerators(menuPath);
		menuPath = TextUtils.fixTabs(menuPath);
		treeItemSelectionEvent.setContextMenuSelectionPath(menuPath);
		return treeItemSelectionEvent;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTreeItemSelectionEvent(org.eclipse.swt.widgets.Event, com.windowtester.recorder.event.user.TreeEventType, int)
	 */
	public  SemanticTreeItemSelectionEvent createTreeItemSelectionEvent(Event event, TreeEventType type, int button) {
		return (SemanticTreeItemSelectionEvent) createTreeItemSelectionEvent((TreeItem)event.item, type, button);
	}
	
	

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTreeItemSelectionEvent(org.eclipse.swt.widgets.TreeItem, com.windowtester.recorder.event.user.TreeEventType, int)
	 */
	public  UISemanticEvent createTreeItemSelectionEvent(TreeItem item, TreeEventType type, int button) {
		
		
		EventInfo info     = new EventInfo();
		info.toString      = "Tree Item Selection (" + type.getLabel() + ")";
		info.cls           = item.getClass().getName();
//		info.parentShell   = getParentShellInfo(tree);
		info.hierarchyInfo = new WidgetLocatorService().inferIdentifyingInfo(item);
		info.button        = button;
		
		SemanticTreeItemSelectionEvent treeSelect = new SemanticTreeItemSelectionEvent(info, type);
		
		String label = extractTreeItemLabel(item);
		treeSelect.setItemLabel(label);
		
		String path  = extractPathString(item);
		treeSelect.setItemPath(path);
		
		return treeSelect;
		
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV1#createWidgetDisposedEvent(org.eclipse.swt.widgets.Widget)
	 */
	public UISemanticEvent createWidgetClosedEvent(Widget widget) {
		EventInfo info     = new EventInfo();
		info.toString      = "widget disposal";
		info.cls           = widget.getClass().getName();
		//NOTE: no hierarchy info...  this will get filled in from the previous selection by the parser
		
		return new SemanticWidgetClosedEvent(info);
	}
	
	
//	@Override
//	public UISemanticEvent createComboSelectionEvent(Event event) {
//		// TODO Auto-generated method stub
//		return super.createComboSelectionEvent(event);
//	}
//	
//	@Override
//	public UISemanticEvent createCComboSelectionEvent(Event event) {
//		// TODO Auto-generated method stub
//		return super.createCComboSelectionEvent(event);
//	}
//	
	

	
}
