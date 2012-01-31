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
package com.windowtester.runtime.condition;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;

/**
 * Tests whether a locator identifies a widget that is
 * visible. 
 * <p>
 * Any locator that implements {@link IsVisible} can be used with this condition.
 * <p>
 */
public class IsVisibleCondition
	implements IDiagnosticParticipant, IUICondition
{
	private final IsVisible locator;
	private final boolean expected;
	private boolean actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance that will test for the visibility of the widget specified by
	 * the locator. This is a convenience constructor that is fully equivalent to
	 * 
	 * <pre>
	 * new IsVisibleCondition(locator, true)
	 * </pre>
	 * 
	 * @param locator the locator for the widget to be tested
	 */
	public IsVisibleCondition(IsVisible locator) {
		this(locator, true);
	}

	/**
	 * Construct a new instance that will test for the visibility of the widget specified by
	 * the locator.
	 * 
	 * @param locator the locator for the widget to be tested
	 * @param expected <code>true</code> if the widget is expected to be visible, else
	 *            <code>false</code>
	 */
	public IsVisibleCondition(IsVisible locator, boolean expected) {
		if (locator == null)
			throw new IllegalArgumentException("locator cannot be null");
		this.locator = locator;
		this.expected = expected;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		throw new RuntimeException("unsupported method - should call testUI(IUIContext) instead");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		try {
			actual = locator.isVisible(ui);
			return actual == expected;
		}
		catch (WidgetSearchException e) {
			exception = e;
			return false;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// IDiagnosticParticipant
	//
	////////////////////////////////////////////////////////////////////////////

	public void diagnose(IDiagnostic diagnostic) {
		diagnostic.attribute("class", getClass().getName());
		diagnostic.attribute("expected", expected);
		diagnostic.attribute("actual", actual);
		if (exception != null)
			diagnostic.diagnose("exception", exception);
	}
}
