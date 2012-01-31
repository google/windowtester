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
package com.windowtester.runtime.swt.internal;

import com.windowtester.internal.runtime.ISelectionTarget;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A helper that coordinates the delegation of UI commands.
 *
 */
public class ActionDirector {

	
	private final UIContextSWT _ui;

	public ActionDirector(UIContextSWT ui) {
		_ui = ui;
	}
	
	protected UIContextSWT getUI() {
		return _ui;
	}

	public IWidgetLocator doMouseMove(ISelectionTarget target) throws WidgetSearchException {
		IUISelector2 selector = getSelector(target.getWidgetLocator());
		if (selector != null)
			return selector.mouseMove(getUI(), target);
		return getUI().doWidgetMouseMove(target);
	}
	
	
	protected IUISelector2 getSelector(ILocator locator) {
		if (locator instanceof IUISelector2)
			return (IUISelector2)locator;
		if (locator instanceof IAdaptable)
			return (IUISelector2) ((IAdaptable)locator).getAdapter(IUISelector2.class);
		return null;
	}
	
	
	
}
