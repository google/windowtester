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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;

/**
 * A {@link TabItem} reference that uses native linux_x86-specific code to access bounds.
 */
public class TabItemReference_linux_gtk_x86 extends TabItemReference
{
	public TabItemReference_linux_gtk_x86(TabItem control) {
		super(control);
	}

	@Override
	public Rectangle getDisplayBounds() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				return gtk_getBounds(getWidget());
			}
		});
	}

	static Rectangle gtk_getBounds(TabItem tabItem) {
		Rectangle bounds = new Rectangle(0, 0, 0, 0);
		try {
			Class<?> c = Class.forName("org.eclipse.swt.widgets.Widget");
			Field f = c.getDeclaredField("handle");
			f.setAccessible(true);
			int handle = f.getInt(tabItem);
			gtk_getBounds(handle, bounds);
		}
		catch (Throwable e) {
			throw new UnsupportedOperationException(e);
		}
		return tabItem.getDisplay().map(tabItem.getParent(), null, bounds);
	}

	static void gtk_getBounds(int handle, Rectangle bounds) {
		try {
			Class<?> clazz = Class.forName("org.eclipse.swt.internal.gtk.OS");
			Class<?>[] params = new Class[]{
				Integer.TYPE
			};
			Object[] args = new Object[]{
				new Integer(handle)
			};
			Method method = clazz.getMethod("GTK_WIDGET_X", params);
			bounds.x = ((Integer) method.invoke(clazz, args)).intValue();
			method = clazz.getMethod("GTK_WIDGET_Y", params);
			bounds.y = ((Integer) method.invoke(clazz, args)).intValue();
			method = clazz.getMethod("GTK_WIDGET_WIDTH", params);
			bounds.width = ((Integer) method.invoke(clazz, args)).intValue();
			method = clazz.getMethod("GTK_WIDGET_HEIGHT", params);
			bounds.height = ((Integer) method.invoke(clazz, args)).intValue();
		}
		catch (Throwable e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
