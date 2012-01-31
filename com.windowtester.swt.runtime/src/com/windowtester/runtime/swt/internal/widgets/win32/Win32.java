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
package com.windowtester.runtime.swt.internal.widgets.win32;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Control;

/**
 * A native win32 helper.
 */
public class Win32 {

	static int SendMessage (long hWnd, int Msg, int wParam, int [] lParam)
	{
		/*
		 * In the move to 3.5M4 we're seeing this error in the build:
		 * SendMessage(int,int,int,int[]) in SWTWorkarounds cannot be applied to (long,int,int,int[])
		 * This cast fixes the compilation.  NOTE: this method should not get called in 3.5.
		 */
		return SendMessage((int)hWnd, Msg, wParam, lParam);
	}
	
	static int SendMessage (int hWnd, int Msg, int wParam, int [] lParam)
	{
		int result = 0;
		try {
			Class<?> clazz = Class.forName ("org.eclipse.swt.internal.win32.OS");
			Class<?> [] params = new Class [] {
				Integer.TYPE,
				Integer.TYPE,
				Integer.TYPE,
				lParam.getClass (),
			};
			Method method = clazz.getMethod ("SendMessage", params);
			Object [] args = new Object [] {
				new Integer (hWnd),
				new Integer (Msg),
				new Integer (wParam),
				lParam,
			};
			result = ((Integer) method.invoke (clazz, args)).intValue ();
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
		return result;
	}

	static int SendMessage(Control target, int Msg, int wParam, int [] lParam) {
		Object handle;
		Exception exception;
		try {
			handle = getHandle(target);
			exception = null;
		}
		catch (Exception e) {
			handle = null;
			exception = e;
		}
		if (handle instanceof Integer)
			return SendMessage(((Integer)handle).intValue(), Msg, wParam, lParam);
		if (handle instanceof Long)
			return SendMessage(((Long)handle).longValue(), Msg, wParam, lParam);
		throw new UnsupportedOperationException("no handle found for: " + target, exception);	
	}

	private static Object getHandle(Control target) throws SecurityException, NoSuchFieldException,
		IllegalArgumentException, IllegalAccessException
	{
		Field handle = Control.class.getDeclaredField("handle");
		handle.setAccessible(true);
		return handle.get(target);
	}

}
