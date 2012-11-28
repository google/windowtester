/*******************************************************************************
 *  Copyright (c) 2012 Phillip Jensen, Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Phillip Jensen - initial API and implementation
 *  Frederic Gurr - adaptation to HasMaximum and HasMinimum locators 
 *******************************************************************************/
package com.windowtester.runtime.swt.locator;

import org.eclipse.swt.widgets.Slider;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasMaximum;
import com.windowtester.runtime.condition.HasMaximumCondition;
import com.windowtester.runtime.condition.HasMinimum;
import com.windowtester.runtime.condition.HasMinimumCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.condition.HasSelectionCondition;
import com.windowtester.runtime.swt.internal.widgets.SliderReference;

public class SliderLocator extends SWTWidgetLocator implements IsEnabled, HasFocus, HasMinimum, HasMaximum {

	private static final long serialVersionUID = -6355700821071219211L;

	/**
	 * Create a locator instance.
	 * 
	 * @param parent the parent locator
	 */
	public SliderLocator() {
		super(Slider.class);
	}

	// child
	/**
	 * Create a locator instance.
	 * 
	 * @param parent the parent locator
	 */
	public SliderLocator(SWTWidgetLocator parent) {
		super(Slider.class, parent);
	}

	// indexed child
	/**
	 * Create a locator instance.
	 * 
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public SliderLocator(int index, SWTWidgetLocator parent) {
		super(Slider.class, index, parent);
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
	 * @param expected <code>true</code> if the widget is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}

	/**
	 * Create a condition that tests if the given widget has the expected selection value.
	 * <p>
	 * For example, the following asserts that a Slider has the Selection value 5.
	 * 
	 * <pre>
	 * ui.assertThat(new SliderLocator(parent).hasSelection(5));
	 * </pre>
	 * 
	 * @param expected the expected selection
	 */
	public IUICondition hasSelection(int expected) {
		return new HasSelectionCondition(this, expected);
	}

	/**
	 * Create a condition that tests if the given widget has the expected minimum value.
	 * <p>
	 * For example, the following asserts that a Slider has the minimum value 5.
	 * 
	 * <pre>
	 * ui.assertThat(new SliderLocator(parent).hasMinimum(5));
	 * </pre>
	 * 
	 * @param expected the expected minimum
	 */
	public IUICondition hasMinimum(int expected) {
		return new HasMinimumCondition(this, expected);
	}

	/**
	 * Create a condition that tests if the given widget has the expected maximum value.
	 * <p>
	 * For example, the following asserts that a Slider has the maximum value 5.
	 * 
	 * <pre>
	 * ui.assertThat(new SliderLocator(parent).hasMaximum(5));
	 * </pre>
	 * 
	 * @param expected the expected maximum
	 */
	public IUICondition hasMaximum(int expected) {
		return new HasMaximumCondition(this, expected);
	}

	
	
	
	
	/**
	 * Retrieves the minimum from the underlying slider reference.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the minimum value associated with that object (may be null)
	 * @throws WidgetSearchException
	 */
	public int getMinimum(IUIContext ui) throws WidgetSearchException {
		return getSliderReference(ui).getMinimum();
	}

	/**
	 * Retrieves the maximum from the underlying slider reference.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the maximum value associated with that object (may be null)
	 * @throws WidgetSearchException
	 */
	public int getMaximum(IUIContext ui) throws WidgetSearchException {
		return getSliderReference(ui).getMaximum();
	}

	/**
	 * Retrieves the selection from the underlying slider reference.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the selection associated with that object (may be null)
	 * @throws WidgetSearchException
	 */
	public int getSelection(IUIContext ui) throws WidgetSearchException {
		return getSliderReference(ui).getSelection();
	}

	private SliderReference getSliderReference(IUIContext ui) throws WidgetSearchException {
		return (SliderReference) ui.find(this);
	}
}