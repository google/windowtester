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
package com.windowtester.runtime.swt.locator;

import java.awt.Point;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ButtonReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link Button} widgets.
 */
public class ButtonLocator extends SWTWidgetLocator
	implements IsEnabled, HasText, IsSelected, HasFocus
{

	private static final long serialVersionUID = 621335055837801982L;
	
	/**
	 * Constant to specify how long to wait for the button to be enabled before
	 * attempting to click it.  The default is 3 seconds.
	 * <p>
	 * Note: this is <b>provisional</b> API.
	 */
	public static final long ENABLEMENT_TIMEOUT = 10000;

	/**
	 * Create a locator instance.
	 * @param buttonText the text of the button 
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public ButtonLocator(String buttonText) {
		super(Button.class, buttonText);
	}

	//child
	/**
	 * Create a locator instance.
	 * @param text the text of the button (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the button's parent locator
	 */
	public ButtonLocator(String text, SWTWidgetLocator parent) {
		super(Button.class, text, parent);
	}

	//indexed child
	/**
	 * Create a locator instance.
	 * @param text the text of the button (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index the button's index relative to its parent
	 * @param parent the button's parent locator
	 */
	public ButtonLocator(String text, int index, SWTWidgetLocator parent) {
		super(Button.class, text, index, parent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#preClick(com.windowtester.runtime.locator.IWidgetReference, java.awt.Point, com.windowtester.runtime.IUIContext)
	 */
	@Override
	protected void preClick(IWidgetReference reference,
			Point offset, IUIContext ui) {
//		Button w = (Button) reference.getWidget();
		//TODO: should this be user over-rideable? -- should this be in menus too?
		//TODO: should this be pushed up and a a general case?
//		ui.wait(new IsEnabledCondition(new SWTWidgetReference2(w)), ENABLEMENT_TIMEOUT);
//		ui.wait(new ButtonReference(w).isEnabled(true), ENABLEMENT_TIMEOUT);	
		ui.wait(((ButtonReference) reference).isEnabled(true), ENABLEMENT_TIMEOUT);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getWidgetText(org.eclipse.swt.widgets.Control)
	 */
	protected String getWidgetText(Control widget) {
		return ((Button) widget).getText();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		ButtonReference button = (ButtonReference) ui.find(this);		
		return button.getSelection();
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
	 * @param selected 
	 * @param expected <code>true</code> if the menu is expected to be enabled, else
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
	 * Create a condition that tests if the given button is selected.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isSelected() {
		return isSelected(true);
	}
	
	/**
	 * Create a condition that tests if the given button is selected.
	 * @param selected 
	 * @param expected <code>true</code> if the button is expected to be selected, else
	 *            <code>false</code>
	 */            
	public IUICondition isSelected(boolean expected) {
		return new IsSelectedCondition(this, expected);
	}
	

}
