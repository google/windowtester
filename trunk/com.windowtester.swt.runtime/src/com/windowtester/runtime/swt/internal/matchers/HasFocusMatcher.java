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

import com.windowtester.runtime.swt.internal.widgets.ControlReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

public class HasFocusMatcher extends WidgetMatcher {
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
		ControlReference<?> control = castTo(widget, ControlReference.class);
		if (control == null)
			return false;
		return control.hasFocus();
	}
	
	
	
}