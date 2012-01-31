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

import org.eclipse.ui.dialogs.FilteredTree;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;

/**
 * Matches tree widgets that are part of a {@link FilteredTree}.
 */
public class FilteredTreeMatcher extends WidgetMatcher {
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {
		return isInFilteredTree(ref);
	}

	private boolean isInFilteredTree(ISWTWidgetReference<?> ref) {
		if (!isTree(ref))
			return false;
		return isChildOfFilteredTree(ref);
	}

	private boolean isTree(ISWTWidgetReference<?> ref) {
		return ref instanceof TreeReference;
	}

	private boolean isChildOfFilteredTree(ISWTWidgetReference<?> ref) {
		ISWTWidgetReference<?> parent = ref.getParent();
		while (parent != null) {
			if (parent.getWidget() instanceof FilteredTree)
				return true;
			parent = parent.getParent();
		}
		return false;
	}

	
}
