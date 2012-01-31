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
package com.windowtester.runtime.swt.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;

public class SWTEventTypes {

	private static final Map<String, Integer> EVENTS	= new HashMap<String, Integer>();
	
	static {
		EVENTS.put("Activate", new Integer(SWT.Activate));
		EVENTS.put("Arm", new Integer(SWT.Arm));
		EVENTS.put("Close", new Integer(SWT.Close));
		EVENTS.put("Collapse", new Integer(SWT.Collapse));
		EVENTS.put("Deactivate", new Integer(SWT.Deactivate));
		EVENTS.put("DefaultSelection", new Integer(SWT.DefaultSelection));
		EVENTS.put("Deiconify", new Integer(SWT.Deiconify));
		EVENTS.put("Dispose", new Integer(SWT.Dispose));
		EVENTS.put("DragDetect", new Integer(SWT.DragDetect));
		EVENTS.put("EraseItem", new Integer(SWT.EraseItem));
		EVENTS.put("Expand", new Integer(SWT.Expand));
		EVENTS.put("FocusIn", new Integer(SWT.FocusIn));
		EVENTS.put("FocusOut", new Integer(SWT.FocusOut));
		EVENTS.put("HardKeyDown", new Integer(SWT.HardKeyDown));
		EVENTS.put("HardKeyUp", new Integer(SWT.HardKeyUp));
		EVENTS.put("Help", new Integer(SWT.Help));
		EVENTS.put("Hide", new Integer(SWT.Hide));
		EVENTS.put("Iconify", new Integer(SWT.Iconify));
		EVENTS.put("KeyDown", new Integer(SWT.KeyDown));
		EVENTS.put("KeyUp", new Integer(SWT.KeyUp));
		EVENTS.put("MeasureItem", new Integer(SWT.MeasureItem));
		EVENTS.put("MenuDetect", new Integer(SWT.MenuDetect));
		EVENTS.put("Modify", new Integer(SWT.Modify));
		EVENTS.put("MouseDoubleClick", new Integer(SWT.MouseDoubleClick));
		EVENTS.put("MouseDown", new Integer(SWT.MouseDown));
		EVENTS.put("MouseEnter", new Integer(SWT.MouseEnter));
		EVENTS.put("MouseExit", new Integer(SWT.MouseExit));
		EVENTS.put("MouseHover", new Integer(SWT.MouseHover));
		EVENTS.put("MouseMove", new Integer(SWT.MouseMove));
		EVENTS.put("MouseUp", new Integer(SWT.MouseUp));
		EVENTS.put("MouseWheel", new Integer(SWT.MouseWheel));
		EVENTS.put("Move", new Integer(SWT.Move));
		EVENTS.put("Paint", new Integer(SWT.Paint));
		EVENTS.put("PaintItem", new Integer(SWT.PaintItem));
		EVENTS.put("Resize", new Integer(SWT.Resize));
		EVENTS.put("Selection", new Integer(SWT.Selection));
		EVENTS.put("SetData", new Integer(SWT.SetData));
		EVENTS.put("Settings", new Integer(SWT.Settings)); // note: this event only goes to Display
		EVENTS.put("Show", new Integer(SWT.Show));
		EVENTS.put("Traverse", new Integer(SWT.Traverse));
		EVENTS.put("Verify", new Integer(SWT.Verify));
	}

	/**
	 * Converts the event to a string for display.
	 * 
	 * @param event the event.
	 * @return the string representation of the event.
	 */
	public static String toString(int event) {
		for (Entry<String, Integer> entry : EVENTS.entrySet()) {
			if (event == entry.getValue().intValue())
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Lists all the events.
	 * 
	 * @return all the events.
	 */
	public static int[] events() {
		int[] events = new int[EVENTS.size()];
		int i = 0;
		for (Entry<String, Integer> entry : EVENTS.entrySet()) {
			events[i++] = (entry.getValue()).intValue();
		}
		return events;
	}

	
	public static void main(String[] args) {
		int[] events = events();
		for (int i = 0; i < events.length; i++) {
			System.out.println(toString(events[i]));
		}
	}
	
}
