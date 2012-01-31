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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;

/**
 * Adapts a core runtime {@link com.windowtester.runtime.IWidgetSelectorDelegate} 
 * to an SWT-specific {@link com.windowtester.event.swt.ISWTWidgetSelectorDelegate}.
 * <p>It is the clients responsibility to ensure that the adaptation is compatible.
 * 
 * 
 * @author Phil Quitslund
 *
 */
public class SWTWidgetSelectorAdapter implements ISWTWidgetSelectorDelegate {

	private com.windowtester.runtime.IWidgetSelectorDelegate _delegate;

	public SWTWidgetSelectorAdapter(com.windowtester.runtime.IWidgetSelectorDelegate delegate) {
		_delegate = delegate;
	}

	public Widget click(Widget w, int x, int y, int mask) {
		return (Widget)_delegate.click(w, x, y, mask);
	}

	public Widget doubleClick(Widget w, int x, int y, int mask) {
		return (Widget)_delegate.doubleClick(w, x, y, mask);
	}

	public Widget click(Widget w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		try {
			return (Widget)_delegate.click(w, path);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	public Widget doubleClickItem(Widget w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		try {
			return (Widget)_delegate.doubleClick(w, path);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	public Widget click(Widget w, String path, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		try {
			return (Widget)_delegate.click(w, path, mask);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	public Widget doubleClick(Widget w, String path, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		try {
			return (Widget)_delegate.doubleClick(w, path, mask);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	public Widget contextClick(Widget w, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException {
		try {
			return (Widget)_delegate.contextClick(w, path);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	public Widget contextClick(Widget w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		try {
			return (Widget)_delegate.contextClick(w, itemPath, menuPath);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	public Widget contextClick(Widget w, int x, int y, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		try {
			return (Widget)_delegate.contextClick(w, x, y, path);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	public Point getClickOffset(Widget w, int mask) {
		java.awt.Point offset = _delegate.getClickOffset(w);
		if (offset != null) 
			return new Point(offset.x, offset.y);
		return null;
	}
	
	
}
