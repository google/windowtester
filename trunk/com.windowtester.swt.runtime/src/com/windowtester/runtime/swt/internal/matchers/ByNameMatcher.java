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
package com.windowtester.runtime.swt.internal.matchers;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Matches on widget name.
 */
public class ByNameMatcher extends WidgetMatcher {

	
	private final String expectedName;

	public ByNameMatcher(String name) {
		this.expectedName = name;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
		String actual = widget.getName();
		if (actual == null)
			return false;
		return StringComparator.matches(actual, expectedName);
	}
	
	
	
}
