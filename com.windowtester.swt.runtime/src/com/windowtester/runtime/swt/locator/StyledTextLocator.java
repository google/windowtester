/*******************************************************************************
 *  Copyright (c) 2013 Testing Technologies IST GmbH
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Falk Zilm (zilm@testingtech.com) - initial implementation
 *  Frederic Gurr - adaptation to HasLineOfText locators 
 *******************************************************************************/
package com.windowtester.runtime.swt.locator;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasLineOfText;
import com.windowtester.runtime.condition.HasLineOfTextCondition;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.internal.widgets.StyledTextReference;

/**
 * Locates {@link StyledText} widgets.
 * 
 */

public class StyledTextLocator extends SWTWidgetLocator implements IsEnabled, HasText, HasFocus, HasLineOfText {

	private static final long serialVersionUID = 7386247660026276055L;

	/**
	 * Create a locator instance.
	 */
	public StyledTextLocator() {
		super(StyledText.class);
	}

	// child
	/**
	 * Create a locator instance.
	 * @param parent the parent locator
	 */
	public StyledTextLocator(SWTWidgetLocator parent) {
		super(StyledText.class, parent);
	}

	// indexed child
	/**
	 * Create a locator instance.
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator	 
	 */
	public StyledTextLocator(int index, SWTWidgetLocator parent) {
		super(StyledText.class, index, parent);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getWidgetText(org.eclipse.swt.widgets.Control)
	 */
	protected String getWidgetText(Control widget) throws WidgetSearchException {
		return ((StyledText) widget).getText();
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given widget is enabled.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isEnabled(true)</code>
	 */
	public IUICondition isEnabled() {
		return isEnabled(true);
	}

	/**
	 * Create a condition that tests if the given widget is enabled.
	 * 
	 * @param expected <code>true</code> if the text is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}

	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}

	/**
	 * Create a condition that tests if the given widget has the expected line of text.
	 * <p>
	 * For example, the following asserts that a StyledText has the text "foobar" in line 3.
	 * 
	 * <pre>
	 * ui.assertThat(new StyledTextLocator(parent).hasLineOfText(2, "foobar"));
	 * </pre>
	 * </p>
	 * @param lineNumber the line number of the text (index 0 is the first line of the content)
	 * @param expected the expected line of text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasLineOfText(int lineNumber, String expected) {
		return new HasLineOfTextCondition(this, lineNumber, expected);
	}

	/**
	 * Retrieves the line of text from the underlying StyledText reference.
	 * 
	 * @param lineNumber the line number of the text (index 0 is the first line of the content)
	 * @param ui the UI context in which to find the widgets
	 * @return the line of text associated with that object (may be null)
	 * @throws WidgetSearchException
	 */
	public String getLineOfText(int lineNumber, IUIContext ui) throws WidgetSearchException {
		return getStyledTextReference(ui).getLine(lineNumber);
	}
	
	public StyledTextReference getStyledTextReference(IUIContext ui) throws WidgetSearchException {
		return (StyledTextReference) ui.find(this);
	}
}
