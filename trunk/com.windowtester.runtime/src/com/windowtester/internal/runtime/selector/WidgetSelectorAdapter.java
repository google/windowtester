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
package com.windowtester.internal.runtime.selector;

import java.awt.Point;

import com.windowtester.runtime.IWidgetSelectorDelegate;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;



/**
 * A base <code>IWidgetSelectorDelegate</code> implementation that defaults
 * to throwing an UnsupportedOperationException for all methods.  This
 * class is meant to be subclassed and the appropriate methods overriden.
 */
public class WidgetSelectorAdapter implements IWidgetSelectorDelegate {

	public Object click(Object w, int x, int y, int mask) {
		throw new UnsupportedOperationException();
	}

	public Object click(Object w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();
	}

	public Object click(Object w, String itemLabelOrPath, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();
	}

	public Object doubleClick(Object w, String itemLabel) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();
	}

	public Object doubleClick(Object w, int x, int y, int mask) {
		throw new UnsupportedOperationException();
	}

	public Object doubleClick(Object w, String itemLabelOrPath, int mask) {
		throw new UnsupportedOperationException();
	}

	public Object contextClick(Object w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();
	}

	public Object contextClick(Object w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();
	}

	public Object contextClick(Object w, int x, int y, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();
	}

	public Point getClickOffset(Object w) {
		throw new UnsupportedOperationException();
	}
	
}
