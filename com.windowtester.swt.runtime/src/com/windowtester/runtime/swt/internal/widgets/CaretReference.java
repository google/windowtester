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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;

/** 
 * A {@link Caret} reference.
 * @param <T> the control type
 */
public class CaretReference extends SWTWidgetReference<Caret> {

	/**
	 * Constructs a new instance with the given control.
	 * 
	 * @param control the control.
	 */
	public CaretReference(Caret control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CanvasReference<Canvas> getParent() {
		return displayRef.execute(new Callable<CanvasReference<Canvas>>() {
			public CanvasReference<Canvas> call() throws Exception {
				return new CanvasReference<Canvas>(widget.getParent());
			}
		});
	}
		
}
