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
package com.windowtester.runtime.swt.internal.widgets.win32.win32.x86;

import java.lang.reflect.Field;

import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Control;

/**
 * A native win32 helper.
 * <p>
 * Note: this is taken from <code>SWTWorkarounds</code>
 */
public class Win32
{
	static int SendMessage(Control target, int Msg, int wParam, int[] lParam) {
		try {
			Field field = Control.class.getDeclaredField("handle");
			field.setAccessible(true);
			Object handle = field.get(target);
			return OS.SendMessage(((Integer) handle).intValue(), Msg, wParam, lParam);
		}
		catch (Exception e) {
			throw new UnsupportedOperationException("Failed to obtain handle for " + target, e);
		}
	}

}
