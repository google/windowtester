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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.isVisible;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.withText;

import org.eclipse.swt.widgets.Label;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Matches a widget that is immediately adjacent to (e.g., following) a Label
 * widget with the given label text.
 */
public class HasLabelMatcher extends WidgetMatcher {

	// TODO[pq]: move this and similar constants into centralized place
	private static final int NOT_FOUND = -1;
	
	private final ISWTWidgetMatcher targetMatcher;
	private final WidgetMatcher labelMatcher;


	public HasLabelMatcher(ISWTWidgetMatcher target, String labelText) {
		this.targetMatcher = target;
		this.labelMatcher  = ofClass(Label.class).and(isVisible()).and(withText(labelText));
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {	
		if (!targetMatcher.matches(ref))
			return false;
		
		ISWTWidgetReference<?> parent = ref.getParent();
		
		int index = getLabelIndex(parent);
		if (index == NOT_FOUND)
			return false; 
		
		// get the next widget that matches the target class in the list of children
		// if it matches our target widget, success!
		ISWTWidgetReference<?>[] children = parent.getChildren();
		// fixed: increment index by one to get NEXT
		
		for (int i = index + 1; i < children.length; ++i) {
			ISWTWidgetReference<?> child = children[i];
			if (targetMatcher.matches(child))
				return child.equals(ref);
		}
		return false;
	}

	/**
	 * Get the index of the target label in the list of our parent's children.
	 * 
	 * @return - the label's index (-1 indicates no label found)
	 */
	private int getLabelIndex(ISWTWidgetReference<?> parent){
		ISWTWidgetReference<?>[] children = parent.getChildren();
		for (int i = 0; i < children.length; ++i) {
			ISWTWidgetReference<?> child = children[i];
			if (labelMatcher.matches(child))
				return i;
		}
		return NOT_FOUND;
	}


}
