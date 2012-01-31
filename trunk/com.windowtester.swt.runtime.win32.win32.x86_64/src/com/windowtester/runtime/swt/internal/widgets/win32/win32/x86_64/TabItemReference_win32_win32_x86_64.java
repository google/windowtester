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
package com.windowtester.runtime.swt.internal.widgets.win32.win32.x86_64;

import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;

public class TabItemReference_win32_win32_x86_64 extends TabItemReference
{
	public TabItemReference_win32_win32_x86_64(TabItem control) {
		super(control);
	}

	@Override
	public Rectangle getDisplayBounds() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				TabItem tabItem = getWidget();
				TabFolder parent = tabItem.getParent();
				int index = parent.indexOf(tabItem);
				if (index == -1)
					return new Rectangle(0, 0, 0, 0);
				int[] rect = new int[4];
				Win32_64.SendMessage(parent, /*TCM_GETITEMRECT*/0x130a, index, rect);
				int width = rect[2] - rect[0];
				int height = rect[3] - rect[1];
				Rectangle bounds = new Rectangle(rect[0], rect[1], width, height);
				return tabItem.getDisplay().map(tabItem.getParent(), null, bounds);
			}
		});
	}

}