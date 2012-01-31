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
import com.windowtester.runtime.util.StringComparator;

/**
 * Tests whether a locator identifies a widget that has the
 * specified text. For example, this can be used to assert that a wizard error message
 * appears after a particular sequence of user input.
 * 
 * <pre>
 * 		IUIContext ui = [obtain IUIContext]
 * 		ui.assertThat(new HasTextCondition(new WizardErrorMessageLocator(), null));
 * 		ui.enterText(&quot;myFile.&quot;);
 * 		ui.assertThat(new HasTextCondition(new WizardErrorMessageLocator(), &quot;File extension is missing&quot;));
 * </pre>
 * 
 * Any locator that implements {@link HasText} can be used with this condition. 
 */
public class HasTextCondition
	implements IDiagnosticParticipant, IUICondition
{
	private final HasText locator;
	private final String expected;
	private String actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance
	 * @param locator the locator for the widget to be tested
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public HasTextCondition(HasText locator, String expected) {
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
		actual = null;
		exception = null;
		try {
			actual = locator.getText(ui);
			if (expected == null)
				return actual == null;
			if (actual == null)
				return false;
			return StringComparator.matches(actual, expected);
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
