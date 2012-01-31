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
package com.windowtester.finder.matchers.swt;


/**
 * Matcher that matches widgets that are components of a view part.
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * @author Phil Quitslund
 */
public class ViewComponentMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.ViewComponentMatcher {

	/**
	 * Create a matcher for components of the given view (identified by view identifier).
	 * @param viewId  the view identifier of the view
	 */
	public ViewComponentMatcher(String viewId) {
		super(viewId);
	}
	
		
	
}
