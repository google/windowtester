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

/**
 * Matches widgets that are contained in a given composite.
 */
public class ContainedInMatcher extends WidgetMatcher {

	private final ISWTWidgetReference<?> container;

	public ContainedInMatcher(ISWTWidgetReference<?> containerMatcher){
		this.container = containerMatcher;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
		if (widget.equals(container))
			return true;

	     while (widget != null) {
	         widget = widget.getParent();
	         if (widget == null)
	        	 return false;
	         if (widget.equals(container))
	           return true;
	     }
	     return false;
	}
	
	
}
