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
package com.windowtester.test.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

public class WidgetCollector {
	
	private final IUIContext ui;
	
	public WidgetCollector(IUIContext ui) {
		this.ui = ui;
	}
	
	/**
	 * Gets all widgets of a certain type.
	 * @param <T> - the type of the suspect widget
	 * @param cls - the class of the suspect widget
	 * @return a list of widgets of a given type
	 * @since 3.8.1
	 */
	@SuppressWarnings("unchecked")
	public <T extends Widget> List<T> all(Class<T> cls) {
		List<T> widgets = new ArrayList<T>();
		IWidgetLocator[] refs = ui.findAll(new SWTWidgetLocator(cls));
		for (IWidgetLocator ref : refs) {
			widgets.add((T)((IWidgetReference)ref).getWidget());
		}
		return widgets;
	}
}
