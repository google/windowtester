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
package com.windowtester.swt.event.model.dnd;

import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Control;

import abbot.tester.swt.ControlTester;

/**
 * A helper for locating {@link org.eclipse.swt.dnd.DropTarget}s given a
 * {@link org.eclipse.swt.widgets.Control}.
 *
 */
public class DropTargetHelper {

	/** Control tester helper for accessing control parent */
	private static final ControlTester _controlTester = new ControlTester();

	/** 
	 * Find the drop target associated with this control.
	 */
	public static DropTarget findDropTarget(Control control) {
		
		DropTarget dropTarget = (DropTarget) control.getData("DropTarget"); // note
		//if control isn't a drop target, look to its parents
		if (dropTarget == null) {	
			Control parent = _controlTester.getParent(control);
			while (parent != null && dropTarget == null) {
				dropTarget = (DropTarget) parent.getData("DropTarget"); 
				parent = _controlTester.getParent(parent);
			}
		}
		return dropTarget;
	}	
	
}
