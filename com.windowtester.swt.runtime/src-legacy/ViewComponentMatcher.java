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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.swt.locator.SWTHierarchyHelper;

/**
 * Matcher that matches widgets that are components of a view part.
 * 
 * @author Phil Quitslund
 */
public class ViewComponentMatcher implements Matcher {

//	/** The id (or name) of the target view */
//	private final String _viewId;
	
	/** Cached helper for finding widget parents */
	private SWTHierarchyHelper _hierarchyHelper;

	/** Cached view control */
	private Control _viewControl;

	private final IViewControlProvider viewControlProvider;
	
	public static interface IViewControlProvider {
		Control getViewControl();
		String getViewLabel();
	}
	
	private static abstract class ViewControlProvider implements IViewControlProvider {
		private final String identifier;
		ViewControlProvider(String identifier) {
			this.identifier = identifier;
		}
		/* (non-Javadoc)
		 * @see com.windowtester.finder.matchers.swt.ViewComponentMatcher.IViewControlProvider#getViewLabel()
		 */
		public String getViewLabel() {
			return identifier;
		}
	}
	
	private static final class ByNameProvider extends ViewControlProvider {
		ByNameProvider(String name) {
			super(name);
		}
		/* (non-Javadoc)
		 * @see com.windowtester.finder.matchers.swt.ViewComponentMatcher.IViewControlProvider#getViewControl(java.lang.String)
		 */
		public Control getViewControl() {
			return ViewFinder.getViewControlForName(getViewLabel());
		}
	}

	private static final class ByIdProvider extends ViewControlProvider {
		ByIdProvider(String id) {
			super(id);
		}
		/* (non-Javadoc)
		 * @see com.windowtester.finder.matchers.swt.ViewComponentMatcher.IViewControlProvider#getViewControl(java.lang.String)
		 */
		public Control getViewControl() {
			return ViewFinder.getViewControl(getViewLabel());
		}
	}
	
	
	public static ViewComponentMatcher forId(String id) {
		return new ViewComponentMatcher(new ByIdProvider(id));
	}

	public static ViewComponentMatcher forName(String name) {
		return new ViewComponentMatcher(new ByNameProvider(name));
	}
	
	/**
	 * Create a matcher for components of the given view (identified by view identifier).
	 * @param viewId  the view identifier of the view
	 */
	public ViewComponentMatcher(String viewId) {
		this(new ByIdProvider(viewId));
	}
	
	public ViewComponentMatcher(IViewControlProvider viewControlProvider) {
		this.viewControlProvider = viewControlProvider;
	}
	
	/**
	 * Returns true if the given widget is contained in the widget hierarchy of the control
	 * associated with the viewpart defined by this matcher.
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(Widget w) {
		Control viewControl = getViewControl();
		if (viewControl == null)
			throw new AssertionError("control for view: " + getViewLabel() + " not found");
		return isChildOf(w, viewControl);		
	}

	private String getViewLabel() {
		return viewControlProvider.getViewLabel();
	}

	/**
	 * Test whether the given widget is a child of the given control. 
	 */
	private boolean isChildOf(Widget w, Control control) {

		/*
		 * Create a new instance matcher and check to see if the instance is in
		 * the hierarchy rooted at this control.
		 */
		//return new WidgetFinder().find(control, new InstanceMatcher(w), 0 /* no retries */).getType() == WidgetFinder.MATCH;
	
		/*
		 * Optimized. 
		 */
		
	     //special case: if widget _is_ control, we say it is a child
	     if (w == control)
	           return true; 
	     
	     while (w != null) {
	         //advance to next parent
	         w = getParent(w);
	         if (w == control)
	           return true;
	     }
	     return false;
	}

	
	/**
	 * Get the parent of the givent widget.
	 */
	private Widget getParent(Widget w) {
		return getHelper(w).getParent(w);
	}

	/**
	 * Get a (possibly cached) hierarchy helper.  
	 * @param w - a widget whose display roots the helper
	 */
	private SWTHierarchyHelper getHelper(Widget w) {
		if (_hierarchyHelper == null)
			_hierarchyHelper = new SWTHierarchyHelper(w.getDisplay());
		return _hierarchyHelper;
	}
	
	
	/**
	 * Get the underlying view control.
	 */
	private Control getViewControl() {
		if (_viewControl == null || _viewControl.isDisposed())
			_viewControl = doGetViewControl();
		return _viewControl;
	}

	//hook for override
	protected Control doGetViewControl() {
		return viewControlProvider.getViewControl();
	}
	
	/**
	 * Return a String representation of this view component matcher.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        return "View Component matcher (" + viewControlProvider.getViewLabel() + ")";
    }
	
	
}
