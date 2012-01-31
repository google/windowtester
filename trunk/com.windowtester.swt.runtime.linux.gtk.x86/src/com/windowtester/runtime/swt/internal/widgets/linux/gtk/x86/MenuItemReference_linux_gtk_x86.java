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
package com.windowtester.runtime.swt.internal.widgets.linux.gtk.x86;

import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;

/**
 * A {@link MenuItem} reference that uses native cocoa_x86_64-specific code to access bounds.
 */
public class MenuItemReference_linux_gtk_x86 extends MenuItemReference {

	private static final int PRE_CLICK_PAUSE = 1000;

	public MenuItemReference_linux_gtk_x86(MenuItem item) {
		super(item);
	}

	
	@Override
	public void click() {
		//a slight pause seems to greatly improve chances of clicks succeeding on slow boxes
//		try {
//			Thread.sleep(PRE_CLICK_PAUSE);
//		} catch (InterruptedException e) {
//			//ignore interruptions
//		}		
		super.click();
	}
	
	
}