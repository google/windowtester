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
package com.windowtester.runtime.swt.internal.matcher;


import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

/**
 * A matcher that checks visibility.
 * @deprecated
 */
public class VisibilityMatcher implements IWidgetMatcher {

	public static final String VISIBILITY_KEY       = "test.visibility";
	public static final Object VISIBILITY_SET_VALUE = Boolean.TRUE;
	
	
	
	/**
	 * Used to enable testing --- use only when you want to override the default
	 * behavior for the purposes of testing.
	 */
	public static boolean TEST_MODE;

	public static class TestFriendlyVisibilityMatcher extends VisibilityMatcher {

		public TestFriendlyVisibilityMatcher(boolean isVisible) {
			super(isVisible);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.matcher.VisibilityMatcher#matches(org.eclipse.swt.widgets.Widget)
		 */
		public boolean matches(Widget w) {
			if (super.matches(w))
				return true;
			return checkMetadata(w);
		}
		
		private boolean checkMetadata(final Widget widget) {
			Object data = DisplayExec.sync(new RunnableWithResult() {
				public Object runWithResult() {
					return widget.getData(VISIBILITY_KEY);
				}
			});
			return data == VISIBILITY_SET_VALUE;
		}
	}
	
	public static Widget setVisibleForTesting(Widget w) {
		w.setData(VISIBILITY_KEY, VISIBILITY_SET_VALUE);
		return w;
	}
	
	
	private final boolean _isVisible;

	private VisibilityMatcher(boolean isVisible) {
		_isVisible = isVisible;
	}

	public boolean matches(Widget w) {
		return SWTHierarchyHelper.isVisible(w) == _isVisible;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		if (widget instanceof Widget)
			return matches((Widget)widget);
		return false;
	}


	public static IWidgetMatcher create(boolean isVisible) {
		if (TEST_MODE)
			return new TestFriendlyVisibilityMatcher(isVisible);
		return new VisibilityMatcher(isVisible);
	}

	public static IWidgetMatcher visibleMatcher() {
		return create(true);
	}
		
}
