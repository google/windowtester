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
package com.windowtester.runtime.swt.util;

import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

public class DebugHelper {

	/*
	 * TODO: this is ultimately slated for rework
	 * 
	 */
	
	/**
	 * Print to System.out a String that represents the current widget hierarchy starting at the 
     * active shell as root. 
	 */
	public void printWidgets() {
		System.out.println(new SWTHierarchyHelper().dumpWidgets());
	}

//	/**
//	 * Print to System.out a String that represents the current widget hierarchy starting at the 
//     * given widget root. 
//	 */	
//	public void printWidgets(Widget root) {
//		System.out.println(new SWTHierarchyHelper().dumpWidgets(root));
//	}
//	
//	public Widget[] collectAll(Class/*<T extends Widget>*/ widgetType, IUIContext ui) {
//		IWidgetLocator[] locators = ui.findAll(new SWTWidgetLocator(widgetType));
//		List found = new ArrayList();
//		for (int i = 0; i < locators.length; i++) {
//			found.add(((WidgetReference) locators[i]).getWidget());
//		}
//		return (Widget[]) found.toArray(new Widget[]{});
//	}
//
//	public SWTWidgetLocator id(Widget w) {
//		IWidgetIdentifier identifier = new WidgetIdentifier().identify(w);
//		if (identifier instanceof SWTWidgetLocator)
//			return (SWTWidgetLocator)identifier;
//		return null;
//	}
	
	
}
