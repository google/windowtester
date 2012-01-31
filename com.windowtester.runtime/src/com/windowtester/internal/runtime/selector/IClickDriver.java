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

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Provides basic UIContext Click services.
 */
public interface IClickDriver {

	interface Listener {
		void contextClicked(IClickDescription click, IWidgetLocator clicked);
		void clicked(IClickDescription click, IWidgetLocator clicked);
	}
	
	IWidgetLocator click(int clickCount, ILocator locator, int buttonMask) throws WidgetSearchException;

	IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem) throws WidgetSearchException;
	
	void addClickListener(Listener listener);

}
