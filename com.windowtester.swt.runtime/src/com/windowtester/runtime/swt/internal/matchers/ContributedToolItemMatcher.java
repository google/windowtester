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

import java.util.Map;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator.IParameterMatcher;
import com.windowtester.runtime.util.StringComparator;

/**
 * Matches contributed tool items.
 */
public class ContributedToolItemMatcher extends WidgetMatcher {

	private final String id;
	private final IParameterMatcher parameterMatcher;

	public ContributedToolItemMatcher(String id, IParameterMatcher parameterMatcher) {
		this.id = id;
		this.parameterMatcher = parameterMatcher;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	@SuppressWarnings("unchecked")
	public boolean matches(ISWTWidgetReference<?> ref) {
		if (!(ref instanceof ToolItemReference)) 
			return false;
		ToolItemReference item = (ToolItemReference)ref;
		if (!StringComparator.matches(item.getActionDefinitionId(), id))
			return false;
		if (parameterMatcher == null)
			return true;
		Map parameterMap = item.getCommandParameterMap();
		if (parameterMap == null)
			return true;
		return parameterMatcher.matches(parameterMap);
	}

}
