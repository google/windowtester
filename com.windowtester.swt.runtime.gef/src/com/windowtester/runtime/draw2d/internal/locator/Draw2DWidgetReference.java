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
package com.windowtester.runtime.draw2d.internal.locator;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.internal.selectors.FigureSelectorDelegate;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;

public class Draw2DWidgetReference  {

	//create a smart com.windowtester.runtime.gef.locator wrappering this figure
	public static IWidgetLocator create(IFigure figure) {
		return WidgetReference.create(figure, new FigureSelectorDelegate());
	}
	
}
