/*******************************************************************************
 *  Copyright (c) 2013 Frederic Gurr
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
import com.windowtester.runtime.util.StringComparator;

/**
 * Tests whether a locator identifies a widget that has the
 * specified line of text. For example, this can be used to assert that the 5th line in a StyledText widget is "Hello World".
 * 
 * <pre>
 * 		IUIContext ui = [obtain IUIContext]
 * 		ui.assertThat(new HasLineOfTextCondition(new StyledTextLocator(), 4, &quot;Hello World&quot;));
 * </pre>
 * 
 * Any locator that implements {@link HasLineOfText} can be used with this condition. 
 */
public class HasLineOfTextCondition implements IDiagnosticParticipant, IUICondition {

	private final HasLineOfText locator;
	private final String expected;
	private final int lineNumber;
	private String actual;
	private WidgetSearchException exception;

	/**
	 * Construct a new instance
	 * @param locator the locator for the widget to be tested
	 * @param lineNumber the line number (index 0 is the first line of the content)
	 * @param expected the expected line of text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public HasLineOfTextCondition(HasLineOfText locator, int lineNumber, String expected) {
		this.locator = locator;
		this.lineNumber = lineNumber;
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
			actual = locator.getLineOfText(lineNumber, ui);
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
		diagnostic.attribute("lineNumber", lineNumber);
		diagnostic.attribute("expected", expected);
		diagnostic.attribute("actual", actual);
		if (exception != null)
			diagnostic.diagnose("exception", exception);
	}
}
