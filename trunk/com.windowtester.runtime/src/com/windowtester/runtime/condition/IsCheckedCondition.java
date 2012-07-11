/*******************************************************************************
 *  Copyright (c) 2012 Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Frederic Gurr - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.condition;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;

/**
 * Tests whether a locator identifies a widget that is
 * checked. For example, this can be used to assert that a {@link TableItem} is checked after a
 * particular sequence of user inputs.
 * 
 * <pre>
 * 		IUIContext ui = [obtain IUIContext]
 * 		ui.assertThat(new IsCheckedCondition(new TableItemLocator(&quot;foobar&quot;), false));
 * 		ui.click(1, new TableItemLocator(&quot;foobar&quot;), WT.CHECK);
 * 		ui.assertThat(new IsCheckedCondition(new TableItemLocator(&quot;foobar&quot;), true));
 * </pre>
 * 
 * Any locator that implements {@link IsChecked} can be used with this condition.
 */
public class IsCheckedCondition implements IDiagnosticParticipant, IUICondition {

	private final IsChecked locator;
	private final boolean expected;
	private boolean actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance that will test if the widget which is specified by
	 * the locator is checked. This is a convenience constructor that is fully equivalent to
	 * 
	 * <pre>
	 * new IsCheckedCondition(locator, true)
	 * </pre>
	 * 
	 * @param locator the locator for the widget to be tested
	 */
	public IsCheckedCondition(IsChecked locator) {
		this(locator, true);
	}

	/**
	 * Construct a new instance that will test if the widget which is specified by
	 * the locator is checked.
	 * 
	 * @param locator the locator for the widget to be tested
	 * @param expected <code>true</code> if the widget is expected to be checked, else
	 *            <code>false</code>
	 */
	public IsCheckedCondition(IsChecked locator, boolean expected) {
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
			actual = locator.isChecked(ui);
			return (actual == expected);
		} catch (WidgetSearchException e) {
			exception = e;
			return false;
		}
	}

	public boolean testCheckStyleBit(IUIContext ui) {
		try {
			return locator.isCheckStyleBitSet(ui);
		} catch (WidgetSearchException e) {
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