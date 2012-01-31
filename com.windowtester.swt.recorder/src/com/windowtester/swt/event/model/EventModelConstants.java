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
package com.windowtester.swt.event.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public class EventModelConstants {

	public static final int[] EVENT_TYPES = {	
			SWT.None,		SWT.KeyDown, 		SWT.KeyUp,
			SWT.MouseDown,	SWT.MouseUp,		SWT.MouseMove,
			SWT.MouseEnter,	SWT.MouseExit,		SWT.MouseDoubleClick,
			SWT.Paint,		SWT.Move,			SWT.Resize,
			SWT.Dispose,	SWT.Selection,		SWT.DefaultSelection,
			SWT.FocusIn,	SWT.FocusOut,		SWT.Expand,
			SWT.Collapse,	SWT.Iconify,		SWT.Deiconify,
			SWT.Close,		SWT.Show,			SWT.Hide,
			SWT.Modify,		SWT.Verify,			SWT.Activate,
			SWT.Deactivate,	SWT.Help,			SWT.DragDetect,
			SWT.Arm,		SWT.Traverse,		SWT.MouseHover,
			SWT.HardKeyDown,SWT.HardKeyUp,		SWT.MenuDetect,
			SWT.DRAG
	};

	public static final String[] EVENT_NAMES = {
			"None",			"KeyDown", 		"KeyUp",
			"MouseDown",	"MouseUp",		"MouseMove",
			"MouseEnter",	"MouseExit",	"MouseDoubleClick",
			"Paint",		"Move",			"Resize",
			"Dispose",		"Selection",	"DefaultSelection",
			"FocusIn",		"FocusOut",		"Expand",
			"Collapse",		"Iconify",		"Deiconify",
			"Close",		"Show",			"Hide",
			"Modify",		"Verify",		"Activate",
			"Deactivate",	"Help",			"DragDetect",
			"Arm",			"Traverse",		"MouseHover",
			"HardKeyDown",	"HardKeyUp",	"MenuDetect", 
			"Drag"
	};	



	public static String getEventName(Event e) {
		String res = EVENT_NAMES[e.type];
		final int NAME_LENGTH = 12; // EVENT_NAMES.length()?
		for (int i = EVENT_NAMES[e.type].length(); i < NAME_LENGTH; i++) {
			res += " ";
		}
		return res;
	}

	public static int getAccelerator(Event e){
		int accel = 0;
		
		if(e.keyCode!=0){
			accel |= SWT.KEYCODE_BIT;
			accel |= e.keyCode;	
			accel |= e.stateMask;
		}		
		else	
			accel |= e.character;
				
		return accel;
	}

	
	
}
