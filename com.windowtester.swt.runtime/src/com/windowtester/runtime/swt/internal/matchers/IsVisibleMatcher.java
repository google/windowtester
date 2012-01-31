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

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

public class IsVisibleMatcher extends WidgetMatcher {

	public static final String VISIBILITY_KEY       = "test.visibility";
	public static final Object VISIBILITY_SET_VALUE = Boolean.TRUE;
	
	/**
	 * Used to enable testing --- use only when you want to override the default
	 * behavior for the purposes of testing.
	 */
	public static boolean TEST_MODE;

	public static class TestFriendlyVisibilityMatcher extends IsVisibleMatcher {

		public TestFriendlyVisibilityMatcher(boolean isVisible) {
			super(isVisible);
		}

		@Override
		public boolean matches(ISWTWidgetReference<?> widget) {
			if (super.matches(widget))
				return true;
			return matches(widget.getWidget());
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.matcher.VisibilityMatcher#matches(org.eclipse.swt.widgets.Widget)
		 */
		public boolean matches(Widget w) {
			return checkMetadata(w);
		}
		
		private boolean checkMetadata(final Widget widget) {
			Object data = DisplayReference.getDefault().execute(new Callable<Object>() {
				public Object call() {
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
	
	
	public static IsVisibleMatcher forValue(boolean isVisible){
		if (TEST_MODE)
			return new TestFriendlyVisibilityMatcher(isVisible);
		return new IsVisibleMatcher(isVisible);
	}
	
	private final boolean isVisible;

	private IsVisibleMatcher() {
		this(true);
	}
	
	private IsVisibleMatcher(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
		return widget.isVisible() == isVisible;
	}
	
//	public boolean matches(Widget w) {
//		return SWTHierarchyHelper.isVisible(w) == isVisible;
//	}


	@Override
	public String toString() {
		return "matches visible = " + isVisible;
	}
	
	
	
}
