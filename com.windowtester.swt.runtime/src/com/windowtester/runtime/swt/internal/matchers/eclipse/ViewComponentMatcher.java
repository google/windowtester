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
package com.windowtester.runtime.swt.internal.matchers.eclipse;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Matcher that matches widgets that are components of a {@link ViewPart}.
 * 
 */
public class ViewComponentMatcher extends PartComponentMatcher {


	/** Cached view control */
	private Control viewControl;

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
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.matchers.eclipse.PartComponentMatcher#getPartControl()
	 */
	protected Control getPartControl() throws com.windowtester.runtime.WidgetSearchException {
		if (viewControl == null || viewControl.isDisposed())
			viewControl = doGetViewControl();
		return viewControl;
	}

	@Override
	public boolean matches(ISWTWidgetReference<?> ref) {
		return super.matches(ref);
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
