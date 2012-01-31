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
package com.windowtester.runtime.swt.internal.widgets.linux.motif.x86;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;

/**
 * A {@link TabItem} reference that uses native linux_x86-specific code to access bounds.
 * This is only necessary for Eclipse 3.3 and earlier which does not have a getBounds
 * method.
 */
public class TabItemReference_linux_motif_x86 extends TabItemReference
{
	public TabItemReference_linux_motif_x86(TabItem control) {
		super(control);
	}

	@Override
	public Rectangle getDisplayBounds() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				TabItem tabItem = getWidget();
				Rectangle bounds = new Rectangle(0, 0, 0, 0);
				try {
					Class<?> c = tabItem.getClass();
					Method m = c.getDeclaredMethod("getBounds", (Class<?>[]) null);
					m.setAccessible(true);
					bounds = (Rectangle) m.invoke(tabItem, (Object[]) null);
					int margin = 2;
					bounds.x += margin;
					bounds.y += margin;
					bounds.width -= 2 * margin;
					bounds.height -= margin;
				}
				catch (Throwable e) {
					throw new UnsupportedOperationException(e);
				}
				return tabItem.getDisplay().map(tabItem.getParent(), null, bounds);
			}
		});
	}
}
