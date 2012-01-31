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
 * Tests whether a locator identifies a widget that has a particular index in
 * its parent.
 * 
 * Any locator that implements {@link HasIndex} can be used with this condition.
 */
public class HasIndexCondition implements IDiagnosticParticipant, IUICondition {
	
	private final HasIndex locator;
	private final int expected;
	private int actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance that will test for the index of the widget specified by
	 * the locator.
	 * 
	 * @param locator the locator for the widget to be tested
	 * @param expectedIndex the expected index
	 */
	public HasIndexCondition(HasIndex locator, int expectedIndex) {
		if (locator == null)
			throw new IllegalArgumentException("locator cannot be null");
		this.locator = locator;
		this.expected = expectedIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		throw new RuntimeException(
				"unsupported method - should call testUI(IUIContext) instead");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester
	 * .runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		try {
			actual = locator.getIndex(ui);
			return actual == expected;
		} catch (WidgetSearchException e) {
			exception = e;
			return false;
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// IDiagnosticParticipant
	//
	// //////////////////////////////////////////////////////////////////////////

	public void diagnose(IDiagnostic diagnostic) {
		diagnostic.attribute("class", getClass().getName());
		diagnostic.attribute("expected", expected);
		diagnostic.attribute("actual", actual);
		if (exception != null)
			diagnostic.diagnose("exception", exception);
	}
}
