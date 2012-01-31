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

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Simple class based matcher.
 */
public class ByClassMatcher extends WidgetMatcher {

	
	private final ClassReference classReference;

	public ByClassMatcher(String className) {
		classReference = ClassReference.forName(className);
	}
	
	public ByClassMatcher(Class<?> cls) {
		classReference = ClassReference.forClass(cls);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
		Object w = widget.getWidget();
		if (w == null)
			return false;
		return classReference.refersTo(w.getClass());
	}
	
	@Override
	public String toString() {
		return "matches class: " + classReference.getName();
	}
	
	
}
