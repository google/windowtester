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

import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.widgets.ISWTWidgetReference;

/**
 * RENAME ME!  
 *
 * @author Phil Quitslund
 *
 */
public class WidgetSelectorAdapterFactory {

	public static IWidgetLocator create(IWidgetLocator locator) {
		return new WidgetSelectorAdapter(locator);
	}

	
	static class WidgetSelectorAdapter extends WidgetReference implements IUISelector, IWidgetLocator {
		BasicWidgetSelector _selector = new BasicWidgetSelector();

		private final IWidgetLocator _locator;

		public WidgetSelectorAdapter(IWidgetLocator locator) {
			super(((WidgetReference)locator).getWidget());
			_locator = locator;
		}

		public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
			Widget clicked = _selector.click((Widget)widget.getWidget(), click.x(), click.y(), click.modifierMask());
			return new WidgetReference(clicked);
		}

		public IWidgetLocator contextClick(IUIContext ui, WidgetReference widget, String menuItemPath) throws WidgetSearchException {
			Widget clicked = _selector.contextClick((Widget)widget.getWidget(), menuItemPath);
			return new WidgetReference(clicked);
		}

		public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) throws WidgetSearchException {
//			Widget clicked = _selector.contextClick((Widget)widget.getWidget(), click.x(), click.y(), menuItemPath);
//			return new WidgetReference(clicked);
			SWTLocation location = new SWTWidgetLocation((ISWTWidgetReference<?>) widget, WTInternal.TOPLEFT).offset(click.x(), click.y());
			return new SWTMenuSelector().contextClick(location, false, menuItemPath);
		}
		
		
		public IWidgetLocator[] findAll(IUIContext ui) {
			return _locator.findAll(ui);
		}

		public boolean matches(Object widget) {
			return _locator.matches(widget);
		}
	}
	
	
}
