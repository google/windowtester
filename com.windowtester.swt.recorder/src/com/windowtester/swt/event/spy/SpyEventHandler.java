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
package com.windowtester.swt.event.spy;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.debug.Tracer;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.Context;
import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.finder.WidgetLocatorService;
import com.windowtester.runtime.swt.internal.identifier.ContributedIdentifierManager;
import com.windowtester.runtime.swt.locator.ListItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV1;
import com.windowtester.swt.event.recorder.IEventRecorderPluginTraceOptions;

public class SpyEventHandler {

	public static boolean FORCE_ENABLE = false; //FOR TESTING
	
	private boolean inSpyMode = FORCE_ENABLE;

	public UISemanticEvent interepretHover(Event event) {		
		if (!inSpyMode)
			return null;
		return createInspectionEvent(event);
	}

	public void spyModeToggled() {
		this.inSpyMode = !inSpyMode;
		Tracer.trace(IEventRecorderPluginTraceOptions.SWT_EVENTS, "spy mode toggled to: " + inSpyMode);
	}

	private UISemanticEvent createInspectionEvent(Event event) {
		
		Widget w = event.widget;
	
		if (w == null)
			return null;
		
//		int stateMask = event.stateMask;
//		if ((stateMask & SWT.CTRL) != 0)
//			return null;
		
//		System.out.println("--->spy handling: " + event);
		
		
		Shell currentShell = getShell(w);
		
		/*
		 * UGLY, UGLY, UGLY
		 * Seed to ensure scope is properly set (rub: need to 
		 * override active shell which may be null)
		 */
		
		ShellFinder.CURRENT_SHELL_HINT = currentShell;
		
		w = getMostSpecificWidgetForEvent(w, event);
		
		if (w == null)
			return null;
		
		SemanticWidgetInspectionEvent containerInspection = SemanticContainerDescription.forWidgetInShell(w, currentShell);
		if (containerInspection != null)
			return containerInspection;
		
		EventInfo info = extractInfo(event, w);
		return new SemanticWidgetInspectionEvent(info, Context.GLOBAL.getUI()).withWidgetHash(w.hashCode()).atHoverPoint(getCursorPosition());
	}


	private Shell getShell(Widget w) {
		try {
			SWTHierarchyHelper helper = new SWTHierarchyHelper(w.getDisplay());
			do {
				w = helper.getParent(w);
				if (w instanceof Shell) {
					return (Shell) w;
				}
			} while (w != null);
		} catch (Throwable th) {
			// being safe
		}
		return null;
	}

	private Point getCursorPosition() {
		return Display.getDefault().getCursorLocation();
	}


	private EventInfo extractInfo(Event event, Widget w) {
		EventInfo info     = new EventInfo();
		info.toString      = "inspection request for: " + w;
		info.cls           = w.getClass().getName();
		info.hierarchyInfo = identifyWidget(w, event);
		info.x = event.x;
		info.y = event.y;
		return info;
	}

	private IWidgetIdentifier identifyWidget(Widget w, Event event) {
		ILocator contributed = ContributedIdentifierManager.identify(event);
		if (contributed != null)
			return SWTSemanticEventFactoryImplV1.adaptToIdentifier(contributed);
		IWidgetIdentifier id = new WidgetLocatorService().inferIdentifyingInfo(w, event);
		return sanityCheck(id, event);
	}
	
	private IWidgetIdentifier sanityCheck(IWidgetIdentifier id, Event event) {
		if (id instanceof ListItemLocator) {
			ListItemLocator listItem = (ListItemLocator)id;
			/*
			 * Since there is no way to map hover location to an associated list item, we pop up and only surface details on the List
			 */
			SWTWidgetLocator list = new SWTWidgetLocator(List.class);
			list.setParentInfo(listItem.getParentInfo());
			return list;
		}
		return id;
	}

	static Widget getMostSpecificWidgetForEvent(Widget w, Event event) {
		if (w instanceof Tree) {
			Tree tree = (Tree)w;
			return tree.getItem(pointFor(event));
		}
		if (w instanceof Table) {
			Table table = (Table)w;
			return table.getItem(pointFor(event));
		}
		if (w instanceof ToolBar) {
			ToolBar bar = (ToolBar)w;
			return bar.getItem(pointFor(event));
		}
		if (w instanceof CoolBar) {
			CoolBar bar = (CoolBar)w;
			CoolItem[] items = bar.getItems();
			for (int i = 0; i < items.length; i++) {
				CoolItem item = items[i];
				Rectangle bounds = SWTWorkarounds.getBounds(item);
				if (bounds != null && bounds.contains(pointFor(event)))
					return item;
			}
		}
		if (w instanceof TabFolder) {
			TabFolder folder = (TabFolder)w;
			TabItem[] items = folder.getItems();
			for (int i = 0; i < items.length; i++) {
				TabItem item = items[i];
				Rectangle bounds = SWTWorkarounds.getBounds(item);
				if (bounds != null && bounds.contains(pointFor(event)))
					return item;
			}
		}
		if (w instanceof CTabFolder) {
			CTabFolder folder = (CTabFolder)w;
			return folder.getItem(pointFor(event));
		}
		if (w instanceof Composite) {
			Composite composite = (Composite)w;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control control = children[i];
				Rectangle bounds = SWTWorkarounds.getBounds(control);
				if (bounds != null && bounds.contains(pointFor(event)))
					return control;
			}
		}
		//TODO: are there more cases?
		return w;
	}
	
	private static Point pointFor(Event event) {
		return new Point(event.x, event.y);
	}
	

	
}
