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

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.util.StringComparator;

public class ToolItemActionIdMatcher implements IWidgetMatcher<ToolItemReference>{

	private String idToMatch;
	public ToolItemActionIdMatcher(String idToMatch) {
		this.idToMatch = idToMatch;
	}

	public boolean matches(ToolItemReference itemRef) {
		String id = itemRef.getActionDefinitionId();
		if (id == null)
			return false;
		return StringComparator.matches(id, idToMatch);
	}
}
