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
package com.windowtester.runtime.swt.internal.widgets.cocoa64;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;

/**
 * A {@link MenuItem} reference that uses native cocoa_x86_64-specific code to access bounds.
 */
public class MenuItemReference_cocoa_x86_64 extends MenuItemReference {

	public MenuItemReference_cocoa_x86_64(MenuItem item) {
		super(item);
	}

	@Override
	public Rectangle getDisplayBounds() {
		return Cocoa64.INSTANCE.getMenuItemBounds(getWidget());
	}
	
}
