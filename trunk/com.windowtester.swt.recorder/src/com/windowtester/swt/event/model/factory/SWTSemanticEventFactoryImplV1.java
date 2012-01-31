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

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.CComboTester;
import abbot.tester.swt.ComboTester;
import abbot.tester.swt.MenuItemTester;
import abbot.tester.swt.MenuTester;
import abbot.tester.swt.ShellTester;
import abbot.tester.swt.TableItemTester;
import abbot.tester.swt.TreeItemTester;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticComboSelectionEvent;
import com.windowtester.recorder.event.user.SemanticDefaultSelectionEvent;
import com.windowtester.recorder.event.user.SemanticDragEvent;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticFocusEvent;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticListSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMoveEvent;
import com.windowtester.recorder.event.user.SemanticResizeEvent;
import com.windowtester.recorder.event.user.SemanticShellClosingEvent;
import com.windowtester.recorder.event.user.SemanticShellDisposedEvent;
import com.windowtester.recorder.event.user.SemanticShellShowingEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.finder.WidgetLocatorService;
import com.windowtester.runtime.swt.internal.identifier.ContributedIdentifierManager;
import com.windowtester.runtime.swt.internal.locator.NoOpLocator;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.util.TextUtils;
import com.windowtester.runtime.swt.locator.ListItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.swt.event.model.EventModelConstants;
import com.windowtester.swt.event.model.ISWTSemanticEventFactory;

/**
 * A factory for building SWTSemanticEvents
 * 
 * NOTE: this is a LEGACY class.  Ultimately {@link SWTSemanticEventFactoryImplV2} will be preferred.
 */
public class SWTSemanticEventFactoryImplV1 implements ISWTSemanticEventFactory {

	////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	////////////////////////////////////////////////////////////////////////////
	
	private  final MenuItemTester _menuItemTester   = new MenuItemTester();
	private  final MenuTester _menuTester           = new MenuTester();
	private  final ComboTester _comboTester         = new ComboTester();
	private  final CComboTester _cComboTester       = new CComboTester();
	private  final TreeItemTester _treeItemTester   = new TreeItemTester();
	private  final TableItemTester _tableItemTester = new TableItemTester();
	private  final ShellTester _shellTester         = new ShellTester();
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Cached references
	//
	////////////////////////////////////////////////////////////////////////////
	
	//the last seen widget --- whereby seen we mean has had info extracted...
	private Widget _lastWidget;
	//the last calculated widget locator
	private IWidgetIdentifier _lastWidgetLocator;
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Public factory methods
	//
	////////////////////////////////////////////////////////////////////////////
	
	public SemanticDropEvent createDragToEvent(Event event) {
		return null; //unsupported
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createKeyDownEvent(org.eclipse.swt.widgets.Event)
	 */
	public  SemanticKeyDownEvent createKeyDownEvent(Event event) {
		
		
		EventInfo info = extractInfo(event);
		SemanticKeyDownEvent keyDown = new SemanticKeyDownEvent(info);

        keyDown.setKey(event.character);
		keyDown.setKeyCode(event.keyCode);
        
		return keyDown;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createKeyDownEvent(org.eclipse.swt.events.TraverseEvent)
	 */
	public  SemanticKeyDownEvent createKeyDownEvent(TraverseEvent event) {
		/*
		 * extractInfo does not work on TypedEvents... so, we replicate
		 * here (for now) 
		 */
		EventInfo info     = new EventInfo();
		info.toString      = "Traverse Event widget=" + event.widget + "char= " + event.character; //a quick hack
		info.cls           = event.widget.getClass().getName();
//		info.parentShell   = getParentShellInfo(event.widget);
		info.button        = 0; //not a mouse event
		info.x             = 0; //no x, y info associated
		info.y             = 0; //...
		
		
		SemanticKeyDownEvent keyDown = new SemanticKeyDownEvent(info);

        keyDown.setKey(event.character);
        keyDown.setKeyCode(event.keyCode);
        
		return keyDown;		
		
	}


	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createKeyDownEvent(org.eclipse.swt.events.KeyEvent)
	 */
	public  SemanticKeyDownEvent createKeyDownEvent(KeyEvent event) {
		/*
		 * extractInfo does not work on TypedEvents... so, we replicate
		 * here (for now) 
		 */
		EventInfo info     = new EventInfo();
		info.toString      = "Traverse Event widget=" + event.widget + "char= " + event.character; //a quick hack
		info.cls           = event.widget.getClass().getName();
//		info.parentShell   = getParentShellInfo(event.widget);
		info.button        = 0; //not a mouse event
		info.x             = 0; //no x, y info associated
		info.y             = 0; //...
		
		
		SemanticKeyDownEvent keyDown = new SemanticKeyDownEvent(info);

        keyDown.setKey(event.character);
        keyDown.setKeyCode(event.keyCode);
        
		return keyDown;		
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createWidgetSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createWidgetSelectionEvent(Event event) {
		
		UISemanticEvent contributed = checkForContributedHandler(event);
		if (contributed != null)
			return contributed;
		
		return parseSelectionEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISWTSemanticEventFactory#createWidgetDisposedEvent(org.eclipse.swt.widgets.Event)
	 */
	public UISemanticEvent createWidgetClosedEvent(Widget widget) {
		// TODO not supported in V1
		return null;
	}
	
	private UISemanticEvent parseSelectionEvent(Event event) {
		
		
		EventInfo info = extractInfo(event);
		
		/*
		 * Some events are no-ops.  Check for one of these first.
		 */
		if (info.hierarchyInfo instanceof NoOpLocator)
			return null;
		
		/*
		 * Texts in CCombos require special treatment:
		 */
		if (event.widget instanceof Text) {
			WidgetLocatorService locator = new WidgetLocatorService();
			Widget parent = locator.getParent(event.widget);
			if (parent instanceof CCombo) {
				info.hierarchyInfo = locator.inferIdentifyingInfo(parent);
				info.toString = EventModelConstants.getEventName(event) + " widget=" + parent + " button= " + event.button;
				SemanticWidgetSelectionEvent widgetSelect = new SemanticWidgetSelectionEvent(info);
				widgetSelect.setItemLabel(parent.toString());
				return widgetSelect;
			}
		} //TODO: cleanup and refactor this conditional logic
		
		SemanticWidgetSelectionEvent widgetSelect = new SemanticWidgetSelectionEvent(info);
        
		String label = extractWidgetLabel(event.widget);
		widgetSelect.setItemLabel(label);
		
		return widgetSelect;

	}


	private UISemanticEvent checkForContributedHandler(Event event) {
		ILocator locator = getContributedLocator(event);
		if (locator == null)
			return null;
		
		EventInfo info = extractInfo(event);
		info.hierarchyInfo = adaptToIdentifier(locator);
		SemanticWidgetSelectionEvent widgetSelect = new SemanticWidgetSelectionEvent(info);
        
		String label = extractWidgetLabel(event.widget);
		widgetSelect.setItemLabel(label);
		
		return widgetSelect;
	}

	private ILocator getContributedLocator(Event target) {
		return ContributedIdentifierManager.identify(target);
	}


	public static IWidgetIdentifier adaptToIdentifier(ILocator locator) {
		IWidgetIdentifier identifier = null;
		if (locator instanceof IWidgetIdentifier){
			identifier = (IWidgetIdentifier)locator;
		} else  {
			identifier = new IdentifierAdapter(locator);
		}
		return identifier;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTabItemSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createTabItemSelectionEvent(Event event) {
		
		Widget w = event.widget;
		
		Widget item = null;
		
		if (w instanceof TabFolder) {
			//perhaps overly simplistic: taking first selection
			item = ((TabFolder)w).getSelection()[0];
		} else if (w instanceof CTabFolder) {
			item = ((CTabFolder)w).getSelection();
		}
		

	
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = item.getClass().getName();
//		info.parentShell   = getParentShellInfo(item);
		info.hierarchyInfo = inferIdentifyingInfo(item);
		
		/*
		 * Need to map x coords to coords relative to the widget
		 */
		Rectangle parentBounds = UIProxy.getBounds(event.widget);
		Rectangle itemBounds   = UIProxy.getBounds(item);
		
		info.x = event.x - (itemBounds.x - parentBounds.x);
		info.y = event.y;
		
		
		SemanticWidgetSelectionEvent widgetSelect = new SemanticWidgetSelectionEvent(info);
        
		String label = extractWidgetLabel(item);
		widgetSelect.setItemLabel(label);
		
		return widgetSelect;
	}

	private IWidgetIdentifier inferIdentifyingInfo(Widget widget) {
		return new WidgetLocatorService().inferIdentifyingInfo(widget);
	}
	
	private IWidgetIdentifier inferIdentifyingInfo(Widget widget, Event event) {
		return new WidgetLocatorService().inferIdentifyingInfo(widget, event);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createListItemSelectionEvent(org.eclipse.swt.widgets.List, java.lang.String, java.lang.String)
	 */
	public  UISemanticEvent createListItemSelectionEvent(List list, String item, String mask) {
	
		EventInfo info     = new EventInfo();
		info.toString      = "List item selection (" + item + ") widget=" + list;
		info.cls           = list.getClass().getName();
		info.hierarchyInfo = inferIdentifyingInfo(list);
		
		//add item info to the locator (this isn't done by the service)
		if (info.hierarchyInfo instanceof ListItemLocator) {
			ListItemLocator loc = (ListItemLocator)info.hierarchyInfo;
			loc.setPath(item);
		}
		
	
		SemanticListSelectionEvent listSelect = new SemanticListSelectionEvent(info);
		listSelect.setItem(item);
		listSelect.setMask(mask);

		return listSelect;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createComboSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createComboSelectionEvent(Event event) {
	
		Combo combo = (Combo)event.widget;
		
		EventInfo info     = new EventInfo();
		info.toString      = "Combo item selection (" + combo + ") widget=" + combo;
		info.cls           = combo.getClass().getName();
//		info.parentShell   = getParentShellInfo(combo);
		info.hierarchyInfo = inferIdentifyingInfo(combo);
		info.x             = event.x;
		info.y             = event.y;
		
		SemanticComboSelectionEvent comboSelect = new SemanticComboSelectionEvent(info);
		
		int index   = _comboTester.getSelectionIndex(combo);
		//TODO: the case where none is selected is an error and should be managed like one...
		String item = (index == -1) ? "" /* none selected */ : _comboTester.getItem(combo, index);
	
		comboSelect.setSelection(item);

		return comboSelect;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createCComboSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public UISemanticEvent createCComboSelectionEvent(Event event) {
		CCombo cCombo      = (CCombo)event.widget;
		
		EventInfo info     = new EventInfo();
		info.toString      = "CCombo item selection (" + cCombo + ") widget=" + cCombo;
		info.cls           = cCombo.getClass().getName();
//		info.parentShell   = getParentShellInfo(cCombo);
		info.hierarchyInfo = inferIdentifyingInfo(cCombo);
		info.x             = event.x;
		info.y             = event.y;
		
		//N.B. reusing ComboSelection in the hopes we don't need a CCombo-specific one...
		SemanticComboSelectionEvent comboSelect = new SemanticComboSelectionEvent(info);
		
		int index   = _cComboTester.getSelectionIndex(cCombo);
		//TODO: the case where none is selected is an error and should be managed like one...
		String item = (index == -1) ? "" /* none selected */ : _cComboTester.getItem(cCombo, index);
	
		comboSelect.setSelection(item);

		return comboSelect;
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTableItemSelectionEvent(org.eclipse.swt.widgets.Table, org.eclipse.swt.widgets.TableItem, int, java.lang.String)
	 */
	public UISemanticEvent createTableItemSelectionEvent(Table table, TableItem item, int columnIndex, String mask) {
		
		EventInfo info     = new EventInfo();
		info.toString      = "Table item selection (" + item + ") widget=" + item;
		info.cls           = item.getClass().getName();
//		info.parentShell   = getParentShellInfo(table);
		info.hierarchyInfo = inferIdentifyingInfo(item);
		
		SemanticWidgetSelectionEvent tableItemSelect = new SemanticWidgetSelectionEvent(info);
		// set the no of clicks
		tableItemSelect.setClicks(1);
//		tableItemSelect.setItem(item);
		
	    if (columnIndex != 0)
			tableItemSelect.setIndex(columnIndex);
		
		tableItemSelect.setMask(mask);
	
		return tableItemSelect;
	}

	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createMenuSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public  SemanticMenuSelectionEvent createMenuSelectionEvent(Event event) {

		MenuItem item = (MenuItem)event.widget;
				
		String pathString = _menuItemTester.getPathString(item);

		//non context case
		if (event.button != 3) {
			int index = pathString.indexOf("/");
			if (index != -1)
				pathString = pathString.substring(index+1);
		}
		//need to find "root menu item"
		MenuItem root = getRootMenuItem(item);
		
		
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = root.getClass().getName();
//		info.parentShell   = getParentShellInfo(event.widget);
		info.hierarchyInfo = inferIdentifyingInfo(root);
		info.button        = event.button;
		info.x             = event.x;
		info.y             = event.y;
		
		SemanticMenuSelectionEvent menuSelect = new SemanticMenuSelectionEvent(info);
		
		String label = extractMenuItemLabel(event);
		menuSelect.setItemLabel(label);
		menuSelect.setPath(pathString);
		
		return menuSelect;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createContextMenuSelectionEvent(org.eclipse.swt.widgets.Widget, org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createContextMenuSelectionEvent(Widget target, Event event) {

		/**
		 * there are pathological cases (where someone tries to get a context menu from within a 
		 * context menu for example) where the target is null.  In this case we just fast fail.
		 * TODO: handle this better!
		 */
		if (target == null)
			return null;
		
		MenuItem item = (MenuItem)event.widget;
		
		String pathString = _menuItemTester.getPathString(item);
		pathString = TextUtils.fixTabs(pathString);
		
		
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = target.getClass().getName();
		
		ILocator locator = getContributedLocator(FakeEvent.forWidget(target).atCursorXY());
		if (locator != null)
			info.hierarchyInfo = adaptToIdentifier(locator);
		else
			info.hierarchyInfo = inferIdentifyingInfo(target);
		
		
		info.button        = 3;
		info.x             = event.x;
		info.y             = event.y;
		
		SemanticMenuSelectionEvent menuSelect = new SemanticMenuSelectionEvent(info);
		
		String label = extractMenuItemLabel(event);
		menuSelect.setItemLabel(label);
		menuSelect.setPath(pathString);
		
		return menuSelect;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTreeItemContextMenuSelectionEvent(com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent, org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createTreeItemContextMenuSelectionEvent(SemanticTreeItemSelectionEvent treeItemSelectionEvent, Event event) {
		String menuPath = _menuItemTester.getPathString((MenuItem)event.widget);
		menuPath = TextUtils.fixTabs(menuPath);
		treeItemSelectionEvent.setContextMenuSelectionPath(menuPath);
		return treeItemSelectionEvent;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createContextMenuSelectionEvent(com.windowtester.recorder.event.user.UISemanticEvent, org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createContextMenuSelectionEvent(UISemanticEvent selection, Event event) {
		
		MenuItem item = (MenuItem)event.widget;
		
		String pathString = _menuItemTester.getPathString(item);
		pathString = TextUtils.fixTabs(pathString);

		IWidgetIdentifier loc = selection.getHierarchyInfo();
		
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = loc.getTargetClass().getName();
		//info.parentShell   = selection.getParentShellInfo();
		info.hierarchyInfo = loc;
		info.button        = 3;
		info.x             = event.x;
		info.y             = event.y;
		
		SemanticMenuSelectionEvent menuSelect = new SemanticMenuSelectionEvent(info);
		
		menuSelect.setIndex(selection.getIndex());
		
		String label = extractMenuItemLabel(event);
		menuSelect.setItemLabel(label);
		menuSelect.setPath(pathString);
		
		return menuSelect;

	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTreeItemSelectionEvent(org.eclipse.swt.widgets.Event, com.windowtester.recorder.event.user.TreeEventType, int)
	 */
	public  SemanticTreeItemSelectionEvent createTreeItemSelectionEvent(Event event, TreeEventType type, int button) {

		EventInfo info = extractInfo(event);
		info.button    = button; //event's button info is incorrect
				
		SemanticTreeItemSelectionEvent treeSelect = new SemanticTreeItemSelectionEvent(info, type);
		
		String label = extractTreeItemLabel(event);
		treeSelect.setItemLabel(label);
		
		String path  = extractPathString((TreeItem)event.item);
		treeSelect.setItemPath(path);
		
		return treeSelect;
	}
	
	

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createTreeItemSelectionEvent(org.eclipse.swt.widgets.TreeItem, com.windowtester.recorder.event.user.TreeEventType, int)
	 */
	public  UISemanticEvent createTreeItemSelectionEvent(TreeItem item, TreeEventType type, int button) {
		
		//fetch parent tree
		Tree tree = _treeItemTester.getParent(item);
		
		EventInfo info     = new EventInfo();
		info.toString      = "Tree Item Selection (" + type.getLabel() + ")";
		info.cls           = tree.getClass().getName();
//		info.parentShell   = getParentShellInfo(tree);
		info.hierarchyInfo = inferIdentifyingInfo(tree);
		info.button        = button;
		
		SemanticTreeItemSelectionEvent treeSelect = new SemanticTreeItemSelectionEvent(info, type);
		
		String label = extractTreeItemLabel(item);
		treeSelect.setItemLabel(label);
		
		String path  = extractPathString(item);
		treeSelect.setItemPath(path);
		
		return treeSelect;
		
	}
	

	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createRawEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createRawEvent(Event event) {
		EventInfo info = extractInfo(event);
		return new UISemanticEvent(info);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createDefaultSelectionEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createDefaultSelectionEvent(Event event) {
		EventInfo info = extractInfo(event);
		return new SemanticDefaultSelectionEvent(info);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createShellShowingEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createShellShowingEvent(Event event) {
		EventInfo info = extractInfo(event);
		String text = null;
		if (event.widget instanceof Shell)
			text = _shellTester.getText((Shell)event.widget);
		return new SemanticShellShowingEvent(info, text);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createShellClosingEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createShellClosingEvent(Event event) {
		EventInfo info = extractInfo(event);
		String text = null;
		if (event.widget instanceof Shell)
			text = _shellTester.getText((Shell)event.widget);
		return new SemanticShellClosingEvent(info, text);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createShellDisposedEvent(org.eclipse.swt.widgets.Event)
	 */
	public  UISemanticEvent createShellDisposedEvent(Event event) {
		
		/*
		 * Rather than extract the info we build it manually 
		 * (extraction can throw exceptions since widgets are getting disposed)
		 */
		
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = event.widget.getClass().getName();
//		info.parentShell   = getParentShellInfo(event.widget);
		info.button        = event.button;
		info.x             = event.x;
		info.y             = event.y;

		Shell shell        = (Shell)event.widget;
//		info.hierarchyInfo = new WidgetLocator(Shell.class, _shellTester.getText(shell));
		info.hierarchyInfo = new SWTWidgetLocator(Shell.class, _shellTester.getText(shell));
		
		return new SemanticShellDisposedEvent(info);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createMoveEvent(org.eclipse.swt.widgets.Event, org.eclipse.swt.widgets.Widget)
	 */
	public  UISemanticEvent createMoveEvent(Event event, Widget widget) {
		EventInfo info = extractInfo(event);
		Rectangle bounds = UIProxy.getBounds(widget);
		info.x = bounds.x;
		info.y = bounds.y;
		return new SemanticMoveEvent(info);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createResizeEvent(org.eclipse.swt.widgets.Event, org.eclipse.swt.widgets.Widget)
	 */
	public  UISemanticEvent createResizeEvent(Event event, Widget widget) {
		EventInfo info = extractInfo(event);
		Rectangle bounds = UIProxy.getBounds(widget);
		return new SemanticResizeEvent(info, bounds.width, bounds.height);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createFocusEvent(org.eclipse.swt.widgets.Event, org.eclipse.swt.widgets.Widget)
	 */
	public  UISemanticEvent createFocusEvent(Event event, Widget widget) {
		//System.out.println("creating focus event: " + widget);
		UISemanticEvent focusEvent = null;
		try {
			EventInfo info = extractInfo(event);
			focusEvent = new SemanticFocusEvent(info);
		} catch(RuntimeException e) {
			//the cause here is in a WidgetNotFoundException for the Text component of a CCombo
			//we boldly ignore these as the CCombo's focus is set elsewhere
		}
		return focusEvent;
		
	}
	
	

	////////////////////////////////////////////////////////////////////////////
	//
	// Construction helpers
	//
	////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Find the topmost parent menu item.
	 * @param child
	 */
	protected MenuItem getRootMenuItem(MenuItem child) {
		
		Menu parentMenu;
		MenuItem parentItem = child, root = null;
		
		do {
			root = parentItem;
			parentMenu = _menuItemTester.getParent(parentItem);
			if (parentMenu != null)
				parentItem = _menuTester.getParentItem(parentMenu);
		} while	(parentItem  != null);

		return root;
	}
	
	/**
	 * Create a path String that identifies this tree item with respect to its parent's (e.g. "Java/Project")
	 * @param item - the tree item
	 * @return a String representing its path
	 */
	protected  String extractPathString(TreeItem item) {
		//TODO[author=pq] use a StringBuffer or something better here (rub: *pre*pending) ...
		String path = TextUtils.escapeSlashes(item.getText());
		for (TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
			//prepend
			path = parent.getText() + '/' + path;
		}
		return path;
	}
	
	/**
     * Extract the tree item label string.
     * @param event - the underlying event
     * @return the menu item label string
     */
    protected  String extractTreeItemLabel(Event event) {
        Widget widget = event.item;
        return extractTreeItemLabel(widget);
    }

	protected  String extractTreeItemLabel(Widget widget) {
		String label = null;
        if (widget instanceof TreeItem) {
            TreeItem item = (TreeItem)widget;
            label = TextUtils.escapeSlashes(item.getText());
            label = TextUtils.fixTabs(label);
        } else //should really assert and fail if this is false
            label       = widget.toString();
        return label;
	}
	
    
    
    
    /**
     * Extract the menu item label string.
     * @param event - the underlying event
     * @return the menu item label string
     */
    protected  String extractMenuItemLabel(Event event) {
        Widget widget = event.widget;
        String label = null;
        if (widget instanceof MenuItem) {
            MenuItem item = (MenuItem)widget;
            label = TextUtils.escapeSlashes(item.getText());
            label = TextUtils.fixTabs(item.getText());
        } else //should really assert and fail if this is false
            label       = widget.toString();
        return label;
    }
	
    /**
     * Extract the widget label info.
     * @param event
     */
    private  String extractWidgetLabel(Widget widget) {
    	String label = null;
        if (widget instanceof Button) {
            Button button = (Button)widget;
            label = button.getText();
        } else if (widget instanceof Item) {
        	//TODO[author=pq] add ToolItem support here...
            Item item = (Item)widget;
            label = item.getText();
        } else { //fall through...
            label       = widget.toString();
        }
        //finally, fix tabs that have been converted to spaces
        label = TextUtils.fixTabs(label);
        return label; 
    }
		
	/**
	 * @return a short String description of this (raw) event
	 */
	protected String getTrimmedDescription(Event e) {
		return EventModelConstants.getEventName(e) + " widget=" + e.widget + " button= " + e.button;
	}
	
//	
//    /**
//     * @param widget
//     * @return
//     */
//    private  ParentShellInfo getParentShellInfo(Widget widget) {
//        Shell shell = getParentShell(widget);
//        return  createParentShellInfo(shell);
//    }

//    /**
//     * Find this widget's parent shell.  N.B. : Only valid on Control widgets.
//     * @param widget - the widget in question
//     * @return the parent shell (or null if there is none associated).
//     */
//    private  Shell getParentShell(Widget widget) {
//        if (widget == null || !(widget instanceof Control) || widget.isDisposed())
//            return null;
//        Control control = (Control)widget;
//        Composite parent = control.getParent();
//        while (parent != null && !(parent instanceof Shell)) {
//            parent = parent.getParent();
//        }
//        return (Shell)parent;
//    }
    
//    /** 
//     * A factory helper.
//     * @param shell
//     * @return
//     */
//    private  ParentShellInfo createParentShellInfo(Shell shell) {
//        return shell == null ? UNAVAILABLE : new ParentShellInfo(shell);
//    }
//
//    /** An "empty/null" object instance */
//    public  final ParentShellInfo UNAVAILABLE = new ParentShellInfo(-1,"");
	
	/**
	 * Extract relevant info from the given SWT event.
	 * @param event
	 * @return
	 */     
	protected EventInfo extractInfo(Event event) {
	
		EventInfo info     = new EventInfo();
		info.toString      = getTrimmedDescription(event);
		info.cls           = event.widget.getClass().getName();
//		info.parentShell   = getParentShellInfo(event.widget);
		info.button        = event.button;
		info.x             = event.x;
		info.y             = event.y;

		/*
		 * here we do a little caching to speed things up
		 */
		if (event.widget == _lastWidget && _lastWidgetLocator != null) {
			info.hierarchyInfo = _lastWidgetLocator;
		} else { //do it the hard way:
			info.hierarchyInfo = inferIdentifyingInfo(event.widget, event);
			//and update the cache
			_lastWidgetLocator = info.hierarchyInfo;
			_lastWidget        = event.widget;
		}
		return info;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createDragEvent(com.windowtester.recorder.event.IUISemanticEvent)
	 */
	public  SemanticDragEvent createDragEvent(IUISemanticEvent event) {
		return new SemanticDragEvent(event);
	}


	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.factory.ISWTSemanticEventFactory#createDropEvent(org.eclipse.swt.dnd.DropTargetEvent, org.eclipse.swt.widgets.Control)
	 */
	public  SemanticDropEvent createDropEvent(DropTargetEvent event, Control targetControl) {
		
		//optionally we can set the target by passing an specifying param
		Widget targetWidget = (targetControl != null) ? targetControl : event.widget;
		
		EventInfo info     = new EventInfo();
		info.cls           = targetWidget.getClass().getName();
//		info.parentShell   = getParentShellInfo(targetWidget);
		info.button        = 0;

		//map this point from display coords to coords relative to target
		Point mappedPoint = map(targetWidget, event.x, event.y);
		
		info.x             = mappedPoint.x;
		info.y             = mappedPoint.y;
		
		
		Widget item = event.item;
		UISemanticEvent target = null;
		
		//trees and tables get special treatment
		if (item instanceof TreeItem) {
			target = createTreeItemSelectionEvent((TreeItem)item, TreeEventType.SELECT, 1);
			//NOTE: we need to adjust x,y here
			Point point = mapCoordinates((Tree)targetWidget, mappedPoint, (TreeItem)item);
			target.setX(point.x);
			target.setY(point.y);
		}
			
		if (item instanceof TableItem) {
			TableItem tableItem = (TableItem)item;
			Table table = _tableItemTester.getParent(tableItem);
			//TODO: defaulting to 0 column index... will coordinates fix this?
			target = createTableItemSelectionEvent(table, tableItem, 0, "SWT.BUTTON1");
//			NOTE: we need to adjust x,y here
			Point point = mapCoordinates(table, mappedPoint, tableItem);
			target.setX(point.x);
			target.setY(point.y);
		}
		
		//if we don't have a tree or table target, default to raw target
		if (target == null) {
			//note: we call this creation methods since it accepts contributions from extenders
			target = createWidgetSelectionEvent(FakeEvent.forWidget(targetWidget).atXY(mappedPoint.x, mappedPoint.y));
		}
		
		return new SemanticDropEvent(target);
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Coordinate mapping helpers
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Map these tree-relative offsets to tree-item-relative offsets
	 */
	private  Point mapCoordinates(Tree tree, Point offset, TreeItem item) {
		Rectangle bounds = _treeItemTester.getBounds(item);
		int x = offset.x - bounds.x;
		int y = offset.y - bounds.y;
		
		return new Point(x,y);
		
	}

	/**
	 * Map these table offsets to table-item-relative offsets
	 */
	private Point mapCoordinates(Table table, Point offset, TableItem item) {
		Rectangle bounds = _tableItemTester.getBounds(item, 0); //FIXME: using 0 column as default
		int x = offset.x - bounds.x;
		int y = offset.y - bounds.y;
		
		return new Point(x,y);
	}
	

	/**
	 * Map this display relative coordinate to a point relative to this
	 * widget's control.
	 */
	private Point map(Widget target, int dx, int dy) {
		Control control = UIProxy.getControl(target);
		if (control == null)
			LogHandler.log("unabled to find control for mapping target: " + target);
		return target.getDisplay().map(null, control, dx, dy);
	}


	
}
