/*******************************************************************************
 *  Copyright (c) 2012 Phillip Jensen, Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Phillip Jensen - initial API and implementation
 *  Frederic Gurr - adaptation to HasMinimum locator 
 *******************************************************************************/
package com.windowtester.runtime.condition;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;

/**
 * Tests whether a locator identifies a widget that has the
 * specified minimum value. For example, this can be used to assert that a SWT slider widget
 * has a particular minimum value.
 * 
 * <pre>
 * 		IUIContext ui = [obtain IUIContext]
 * 		ui.assertThat(new HasMinimumCondition(new SliderLocator(), 0));
 * </pre>
 * 
 * Any locator that implements {@link HasMinimum} can be used with this condition. 
 */
public class HasMinimumCondition implements IUICondition, IDiagnosticParticipant {

	private final HasMinimum locator;
	private final int expected;
	private int actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance
	 * @param locator the locator for the widget to be tested
	 * @param expected the expected minimum value
	 */
	public HasMinimumCondition(HasMinimum locator, int expected) {
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
			actual = locator.getMinimum(ui);
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
