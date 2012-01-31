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
package com.windowtester.swing.event.spy;


import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JTree;

import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.swing.UIContextSwingFactory;
import com.windowtester.internal.swing.WidgetLocatorService;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.swing.locator.JListLocator;

public class SpyEventHandler {
	
	
public static boolean FORCE_ENABLE = false; //FOR TESTING
	
	private static boolean inSpyMode = FORCE_ENABLE;

	public UISemanticEvent interepretHover(AWTEvent event) {		
		if (!inSpyMode)
			return null;
		return createInspectionEvent(event);
	}

	public static void spyModeToggled() {
		inSpyMode = !inSpyMode;
//		Tracer.trace(IEventRecorderPluginTraceOptions.SWT_EVENTS, "spy mode toggled to: " + inSpyMode);
	}

	private UISemanticEvent createInspectionEvent(AWTEvent event) {
		 MouseEvent me = (MouseEvent)event;
         
		 Component component = me.getComponent();
	
		if (component == null)
			return null;
		
		component = getMostSpecificWidgetForEvent(component, event);

		EventInfo info = extractInfo(event, component);
		SemanticWidgetInspectionEvent wEvent =
			new SemanticWidgetInspectionEvent(info, UIContextSwingFactory.createContext()).withWidgetHash(component.hashCode()).atHoverPoint(getCursorPosition());
		return wEvent;
	}


	private org.eclipse.swt.graphics.Point getCursorPosition() {
		return Display.getDefault().getCursorLocation();
	}


	
	private EventInfo extractInfo(AWTEvent event, Component w) {
		EventInfo info     = new EventInfo();
		info.toString      = "inspection request for: " + w;
		info.cls           = w.getClass().getName();
		info.hierarchyInfo = identifyWidget(w, event);
		if (event instanceof MouseEvent){
			MouseEvent me = (MouseEvent)event;
			info.x = me.getX();
			info.y = me.getY();
		}
		return info;
	}

	private IWidgetIdentifier identifyWidget(Component w, AWTEvent event) {
		
		IWidgetIdentifier id = new WidgetLocatorService().inferIdentifyingInfo(w);
		return sanityCheck(id, event);
	}
	
	private IWidgetIdentifier sanityCheck(IWidgetIdentifier id, AWTEvent event) {
		if (id instanceof JListLocator) {
			JListLocator listItem = (JListLocator)id;
			return listItem;
		}
		return id;
	}

	static Component getMostSpecificWidgetForEvent(Component w, AWTEvent event) {
		if (w instanceof JTree) {
			JTree tree = (JTree)w;
			return tree;
		}
		if (w instanceof JTable) {
			JTable table = (JTable)w;
			return table;
		}
		
		
		//TODO: are there more cases?
		return w;
	}
	
	private static Point pointFor(AWTEvent event) {
		int id = event.getID();
		if (id== MouseEvent.MOUSE_ENTERED || id == MouseEvent.MOUSE_MOVED){
			MouseEvent me = (MouseEvent)event;
			return new Point(me.getX(), me.getY());
		}
		return null;
	}
	

	
}