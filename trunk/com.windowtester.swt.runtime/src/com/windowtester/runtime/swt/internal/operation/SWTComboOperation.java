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
import org.eclipse.swt.widgets.Combo;

import com.windowtester.runtime.swt.internal.widgets.ComboReference;

/**
 * Select an item in a {@link Combo} widget programmatically with SWTBot style events
 * because the doing the same thing using OS based mouse events is too complicated.
 */
public class SWTComboOperation extends SWTWidgetOperation<ComboReference>
{
	public SWTComboOperation(ComboReference widgetRef) {
		super(widgetRef);
	}

	/**
	 * Select the item at the specified index in the specified widget
	 * @param itemIndex the index of the item to be selected
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTSelectComboItemOperation().select(widget, 5).execute();</code>
	 */
	public SWTComboOperation select(final int itemIndex) {

		// TODO [Dan] Can combo content be dynamically calculated?
		// Do we need to simulate the combo drop down to populate the combo?
		// Or perhaps trigger it using a mouse click before the step below?

		queueStep(new Step() {
			public void executeInUI() throws Exception {
				getWidgetRef().getWidget().select(itemIndex);
			}
		});
		queueWidgetEvent(SWT.Selection);
		return this;
	}
}