/*******************************************************************************
 *  Copyright (c) 2012 Phillip Jensen
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Phillip Jensen - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.condition;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.swt.locator.SliderLocator;

/**
 * Tests whether a locator identifies a widget that has the
 * specified selection value. For example, this can be used to assert that a SWT slider widget
 * has a particular selection value.
 * 
 * <pre>
 * 		IUIContext ui = [obtain IUIContext]
 * 		ui.assertThat(new HasSelectionCondition(new SliderLocator(), 30));
 * </pre>
 * 
 */
public class HasSelectionCondition implements IUICondition, IDiagnosticParticipant {

	private final SliderLocator locator;
	private final int expected;
	private int actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance
	 * @param locator the locator for the widget to be tested
	 * @param expected the expected selection value
	 */
	public HasSelectionCondition(SliderLocator locator, int expected) {
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
		actual = 0;
		exception = null;
		try {
			actual = locator.getSelection(ui);
			return actual == expected;
		} catch (WidgetSearchException e) {
			exception = e;
			return false;
		}
	}

	@Override
	public String toString() {
		return "expected: " + expected + " actual: " + actual;
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
