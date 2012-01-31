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
package com.windowtester.internal.runtime.matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;


/**
 * A matcher that matches instances by class name.  This has the advantage of
 * not requiring that classes be on the classpath...
 */
public class ByNameClassMatcher implements IWidgetMatcher {

	private final String _className;
	
	public ByNameClassMatcher(String fullyQualifiedName) {
		_className = fullyQualifiedName;
	}

	protected final String getClassName() {
		return _className;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return widget.getClass().getName().equals(getClassName());
	}

}
