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
package com.windowtester.runtime.swt.locator.jface;

import java.io.Serializable;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.jface.WizardPageElement;

/**
 * Locates the active {@link WizardPage} in a {@link Wizard}. 
 * <p/>
 * Example uses:
 * 
 * <pre>
 * ui.assertThat(new WizardPageLocator().hasText(&quot;My Wizard Page&quot;));
 * ui.assertThat(new WizardPageLocator().hasErrorMessage(&quot;Does not compute!&quot;));
 * </pre>
 * <p/>
 */
public class WizardPageLocator implements ILocator /* but cannot be clicked... */, Serializable {


	private static final long serialVersionUID = -5263699547739894094L;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "WizardPageLocator";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Condition factories
	//
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Build a condition that tests whether the active wizard page has an expected message text.
	 * @param messageText the expected message
	 * @return a condition that tests whether the active wizard page has an expected message text
	 * @since 3.9.1
	 */
	public IUICondition hasMessage(String messageText) {
		return new HasTextCondition(new WizardPageElement() {
			public String getText(IUIContext ui) throws WidgetSearchException {
				return getPage().getMessage();
			}
		}, messageText);
	}

	/**
	 * Build a condition that tests whether the active wizard page has an expected error message text.
	 * @param errorMessageText the expected error message
	 * @return a condition that tests whether the active wizard page has an expected error message text
	 * @since 3.9.1
	 */
	public IUICondition hasErrorMessage(String errorMessageText) {
		return new HasTextCondition(new WizardPageElement() {
			public String getText(IUIContext ui) throws WidgetSearchException {
				return getPage().getErrorMessage();
			}
		}, errorMessageText);
	}
	
	/**
	 * Build a condition that tests whether the active wizard page has an expected title text.
	 * @param titleText the expected title
	 * @return a condition that tests whether the active wizard page has an expected title text
	 * @since 3.9.1
	 */
	public IUICondition hasTitle(String titleText) {
		return new HasTextCondition(new WizardPageElement() {
			public String getText(IUIContext ui) throws WidgetSearchException {
				return getPage().getTitle();
			}
		}, titleText);
	}
	
	/**
	 * Build a condition that tests whether the active wizard page has an expected description text.
	 * @param descriptionText the expected description
	 * @return a condition that tests whether the active wizard page has an expected description text
	 * @since 3.9.1
	 */
	public IUICondition hasDescription(String descriptionText) {
		return new HasTextCondition(new WizardPageElement() {
			public String getText(IUIContext ui) throws WidgetSearchException {
				return getPage().getDescription();
			}
		}, descriptionText);
	}
	
}
