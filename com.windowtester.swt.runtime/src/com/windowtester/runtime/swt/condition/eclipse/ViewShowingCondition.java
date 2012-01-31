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
package com.windowtester.runtime.swt.condition.eclipse;

import org.eclipse.swt.widgets.Control;

import abbot.tester.swt.ControlTester;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;

/**
 * Tests to see if a given view is showing.
 *
 */
public class ViewShowingCondition implements ICondition {

	
	private final boolean _isShowing;
	private final String _viewId;
	
	
	/**
	 * Create a condition that tests if the view associated with the given id
	 * is showing.
	 * <p>
	 * Equivalent to: <code>ViewShowingCondition(viewId, true);</code>
	 * @param viewId the id of the view to test
	 */
	public ViewShowingCondition(String viewId) {
		this(viewId, true);
	}
	
	
	/**
	 * Create a condition that tests if the view associated with the given id
	 * is showing.
	 * <p>
	 * @param viewId the id of the view to test
	 * @param isShowing whether or not the view should be showing
	 */
	public ViewShowingCondition(String viewId, boolean isShowing) {
		Invariants.notNull(viewId);
		_viewId = viewId;
		_isShowing = isShowing;
	}

	protected String getViewId() {
		return _viewId;
	}
	
	protected boolean isShowing() {
		return _isShowing;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		Control control = ViewFinder.getViewControl(getViewId());
		if (control == null)
			return test(false);
		return test(new ControlTester().isVisible(control));
	}

	
	public ViewShowingCondition not() {
		return new ViewShowingCondition(_viewId, false);
	}
	
	protected boolean test(boolean isShowing) {
		return isShowing == isShowing();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ViewShowingCondition(" + getViewId() + ", " + isShowing() + ")";
	}
	
}
