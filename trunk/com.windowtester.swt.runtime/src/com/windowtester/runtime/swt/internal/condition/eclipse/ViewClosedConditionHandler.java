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
package com.windowtester.runtime.swt.internal.condition.eclipse;

import com.windowtester.runtime.swt.internal.condition.LocatorClosingHandler;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A condition handler for ensuring that views are closed.
 */
public class ViewClosedConditionHandler extends LocatorClosingHandler {

	public static ViewClosedConditionHandler forView(ViewLocator view) {
		return new ViewClosedConditionHandler(view);
	}


	private ViewClosedConditionHandler(ViewLocator view) {
		super(view);
	}


}
