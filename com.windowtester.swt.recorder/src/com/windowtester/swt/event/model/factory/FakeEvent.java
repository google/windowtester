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
package com.windowtester.swt.event.model.factory;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/**
 * A synthesized event.
 */
public class FakeEvent extends Event {

	public static FakeEvent forWidget(Widget target) {
		FakeEvent event = new FakeEvent();
		event.widget = target;
		return event;
	}

	
	public FakeEvent atXY(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}


	public FakeEvent atCursorXY() {
		Point cursorLocation = (Point) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				Display display = Display.getDefault();
				Point screenLocation = display.getCursorLocation();
				Control cursorControl;
				if (widget instanceof Control)
					cursorControl = (Control)widget;
				else
					cursorControl = display.getCursorControl();
				return display.map(null, cursorControl, screenLocation);
			}
		});
		return atXY(cursorLocation.x, cursorLocation.y);
	}
	
}
