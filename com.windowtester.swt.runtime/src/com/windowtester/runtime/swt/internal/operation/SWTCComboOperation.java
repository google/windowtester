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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.widgets.CComboReference;

/**
 * Select an item in a {@link CCombo} widget programmatically with SWTBot style events
 * because the doing the same thing using OS based mouse events is too complicated.
 */
public class SWTCComboOperation extends SWTWidgetOperation<CComboReference>
{
	public SWTCComboOperation(CComboReference widgetRef) {
		super(widgetRef);
	}

	/**
	 * Select the item at the specified index in the specified widget
	 * 
	 * @param itemIndex the index of the item to be selected
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTSelectCComboItemOperation().select(widget, 5).execute();</code>
	 */
	public SWTCComboOperation select(final int itemIndex) {
		if (itemIndex < 0)
			throw new IllegalArgumentException();

		// TODO [Dan] Can ccombo content be dynamically calculated?
		// Do we need to simulate the ccombo drop down to populate the combo?
		// Or perhaps trigger it using a mouse click before the step below?

		queueStep(new Step() {
			public void executeInUI() throws Exception {
				getWidgetRef().getWidget().select(itemIndex);
			}
		});
		queueWidgetEvent(SWT.Selection);
		return this;
	}

	//=======================================================================
	// Alternate methods
	
	// TODO [Dan] remove these unused methods or switch them with the methods above
	// once we determine which approach is better

	/**
	 * Select the item at the specified index in the specified widget
	 * 
	 * @param itemIndex the index of the item to be selected
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTSelectCComboItemOperation().select(widget, 5).execute();</code>
	 * @deprecated Alternate approach to selecting an item in a combo box using keyboard
	 *             events
	 */
	public SWTCComboOperation selectUsingKeys(final int itemIndex) {
		if (itemIndex < 0)
			throw new IllegalArgumentException();
		
		queueStep(new Step() {
			public void executeInUI() throws Exception {

				// Ensure that the combo has keyboard focus
				if (!getWidgetRef().getWidget().forceFocus())
					throw new SWTOperationStepException("Failed to force keyboard focus");

				// This is only implemented for drop down style
				if ((getWidgetRef().getWidget().getStyle() & SWT.DROP_DOWN) != 0)
					throw new RuntimeException("Not implemented for style " + getWidgetRef().getWidget().getStyle());

				// Ensure that an item can indeed be selected
				int itemCount = getWidgetRef().getWidget().getItemCount();
				if (itemIndex >= itemCount)
					throw new RuntimeException("Cannot select index " + itemIndex + " in " + itemCount + " items");

				// Drop down the combo box
				queueMouseMoveAndClick(getButton(WT.BUTTON1), new SWTControlLocation(getWidgetRef().getWidget(), WTInternal.RIGHT).offset(
					-8, 0).location());

				// Use arrow keys to move up or down the list
				// from the current selection to the desired selection
				int delta = itemIndex - getWidgetRef().getWidget().getSelectionIndex();
				while (delta > 0) {
					queueKeyCodeDown(WT.ARROW_DOWN);
					queueKeyCodeUp(WT.ARROW_DOWN);
					delta--;
				}
				while (delta < 0) {
					queueKeyCodeDown(WT.ARROW_UP);
					queueKeyCodeUp(WT.ARROW_UP);
					delta++;
				}
				queueKeyCodeDown(WT.CR);
				queueKeyCodeUp(WT.CR);
			}
		});
		return this;
	}
}