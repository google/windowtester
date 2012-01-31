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
package com.windowtester.runtime.swt.internal.widgets.carbon;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;

/**
 * A {@link MenuItem} reference that uses native carbon_x86-specific code to access bounds.
 */
public class MenuItemReference_carbon_x86 extends MenuItemReference {

	public MenuItemReference_carbon_x86(MenuItem item) {
		super(item);
	}

	@Override
	public Rectangle getDisplayBounds() {
		return Carbon.EXTENSIONS.getMenuItemBounds(getWidget());
	}
	
}
