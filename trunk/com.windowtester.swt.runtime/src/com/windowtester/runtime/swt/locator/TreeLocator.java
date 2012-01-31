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
package com.windowtester.runtime.swt.locator;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Tree;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;

/**
 * Locates {@link Tree} widgets.  
 */
public class TreeLocator extends SWTWidgetLocator
{
	private static final long serialVersionUID = -6047904520533271027L;

	public TreeLocator() {
		super(Tree.class);
	}

	public TreeLocator(SWTWidgetLocator parent) {
		super(Tree.class, parent);
	}

	@Override
	public IWidgetLocator contextClick(IUIContext ui, final IWidgetReference widget, final IClickDescription click, String menuItemPath)
		throws WidgetSearchException
	{
		// Override {@link SWTWidgetLocator} because it does not properly delegate showContextMenu(click) to the reference
		// and the default showContextMenu(...) does not properly handle context clicks in a tree on Linux
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return ((TreeReference) widget).showContextMenu(click);
			}
		}, menuItemPath);
	}
}
