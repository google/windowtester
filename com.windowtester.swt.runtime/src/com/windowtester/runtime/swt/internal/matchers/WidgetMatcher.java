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

import com.windowtester.internal.runtime.util.ReflectionUtils;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Base class for composing matchers.
 */
public abstract class WidgetMatcher implements ISWTWidgetMatcher {

	public static class And extends WidgetMatcher {
		final WidgetMatcher left, right;
		public And(WidgetMatcher left, WidgetMatcher right) {
			this.left = left;
			this.right = right;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.locator.IWidgetReference)
		 */
		public boolean matches(ISWTWidgetReference<?> widget) {
			return left.matches(widget) && right.matches(widget);
		}
		
		@Override
		public String toString() {
			return left.toString() + " and " + right.toString();
		}
		
	}
	
//	public static WidgetMatcher and(WidgetMatcher m1, WidgetMatcher m2, WidgetMatcher ... matchers){
//		And and = new And(m1, m2);
//		if (matchers != null) {
//			for (WidgetMatcher matcher : matchers) {
//				and = new And(and, matcher);
//			}		
//		}
//		return and;
//	}
	
			
	//should this be in the interface?
	/**
	 * This mechanism for composition should be replaced with {@link CriteriaMatcher}.
	 */
	@Deprecated
	public WidgetMatcher and(WidgetMatcher that){
		return new And(this, that);
	}
	
	public ISWTWidgetMatcher in(WidgetMatcher parent){
		return new ChildOfMatcher(this, parent);
	}
	
	public ISWTWidgetMatcher in(int index, ISWTWidgetMatcher parent){
		return new ChildOfMatcher(this, index, parent);
	}
	
	
	/**
	 * Casting helper.
	 */
	protected <T> T castTo(Object o, Class<T> cls) {
		return ReflectionUtils.castTo(o, cls);
	}

	
}
