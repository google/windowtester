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
package com.windowtester.runtime.draw2d.internal.helpers;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

public class ParentCanvasNotFoundException extends WidgetSearchException {


	private static final long serialVersionUID = 1L;

	
	public ParentCanvasNotFoundException(String msg) {
		super(msg);
	}


	public static ParentCanvasNotFoundException forFigure(IFigure target) {
		return new ParentCanvasNotFoundException("No Canvas containing the figure: " + describe(target) + " was found");
	}


	private static String describe(final IFigure figure) {
		return (String)DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return figure.toString();
			}
		});
	}

}
