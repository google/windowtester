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
package com.windowtester.runtime.swt.internal.condition.eclipse;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;

import com.windowtester.internal.runtime.condition.NotCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.finder.eclipse.WorkbenchFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher;

/**
 * A factory for common view conditions.
 *
 */
public abstract class ViewCondition implements ICondition {

	
	protected final IViewMatcher matcher;

	public static class Active extends ViewCondition {
		public Active(IViewMatcher matcher) {
			super(matcher);
		}
		public boolean test() {
			IViewPart part = ViewFinder.getActiveViewPartNoRetries();
			if (part == null)
				return false;
			return matcher.matches(part);
		}	
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	public static class Dirty extends ViewCondition {
		public Dirty(IViewMatcher matcher) {
			super(matcher);
		}
		public boolean test() {
			IViewReference view = ViewFinder.findMatch(matcher);
			if (view == null)
				return false;
			return view.isDirty();
		}		
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	public static class Zoomed extends ViewCondition {
		public Zoomed(IViewMatcher matcher) {
			super(matcher);
		}
		public boolean test() {
			IViewReference view = ViewFinder.findMatch(matcher);
			if (view == null)
				return false;
			IWorkbenchPage activePage = WorkbenchFinder.getActivePage();
			if (activePage == null)
				return false;
			return activePage.getPartState(view) == IWorkbenchPage.STATE_MAXIMIZED;
		}		
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	
	
	public static class Visible extends ViewCondition {
		
		public Visible(IViewMatcher matcher) {
			super(matcher);
		}
		public boolean test() {
			Control viewControl = ViewFinder.getViewControl(matcher);
			if (viewControl == null)
				return false;
			return SWTHierarchyHelper.isVisible(viewControl);
		}		
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	
	protected ViewCondition(IViewMatcher matcher) {
		this.matcher = matcher;
	}
	
	
	
	
	public static Active isActive(IViewMatcher matcher) {
		return new Active(matcher);
	}
	
	public static Dirty isDirty(IViewMatcher matcher) {
		return new Dirty(matcher);
	}
	
	public static Visible isVisible(IViewMatcher matcher) {
		return new Visible(matcher);
	}
	
	public static Zoomed isZoomed(IViewMatcher matcher) {
		return new Zoomed(matcher);
	}
	
	
}
