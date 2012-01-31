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
package com.windowtester.runtime.swt.internal.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Generic color manager.
 * (copied from org.eclipse.debug.internal.ui and modified)
 */
public class ColorManager {	
	
	private static ColorManager _colorManager;

	protected Map _colorTable= new HashMap(10);

	
	private ColorManager() {
	}
	
	public static ColorManager getDefault() {
		if (_colorManager == null) {
			_colorManager= new ColorManager();
		}
		return _colorManager;
	}
	

	public Color getColor(RGB rgb) {
		Color color= (Color) _colorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			_colorTable.put(rgb, color);
		}
		return color;
	}
	
	public void dispose() {
		Iterator e= _colorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}
}