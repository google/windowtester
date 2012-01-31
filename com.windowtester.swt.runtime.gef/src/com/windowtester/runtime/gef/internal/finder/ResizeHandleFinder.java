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
package com.windowtester.runtime.gef.internal.finder;

import java.lang.reflect.Method;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.ResizeHandle;

import com.windowtester.internal.debug.LogHandler;


public class ResizeHandleFinder {

	private static final Object[] NO_ARGS = new Object[0];
	
	public static IFigure getOwner(ResizeHandle handle) {
		// ick
		try {
			Method method = AbstractHandle.class.getDeclaredMethod("getOwnerFigure", null);
			method.setAccessible(true);
			return (IFigure) method.invoke(handle, NO_ARGS);
		} catch (Exception e) {
			LogHandler.log(e);
		}
		return null;
	}

	
	
}
