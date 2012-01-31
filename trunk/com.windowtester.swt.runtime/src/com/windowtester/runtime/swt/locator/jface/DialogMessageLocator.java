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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.locator.jface.DialogFinder;
import com.windowtester.runtime.swt.internal.locator.jface.DialogInspector;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link Dialog} messages.
 * <p>
 * This locator is useful for accessing dialog message content.  For example,
 * asserting that the message line of the current active dialog says "Does not compute"
 * is as simple as this:
 * <pre>
 *   ui.assertThat(new DialogMessageLocator().hasText("Does not compute"));
 * </pre>
 * 
 *
 */
public class DialogMessageLocator extends SWTWidgetLocator implements HasText {

	private static final long serialVersionUID = 2824992059762515644L;

	public DialogMessageLocator() {
		super(Label.class); //may be Text... probably ignored
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		Dialog dialog = DialogFinder.findActiveDialog();
		if (dialog == null)
			return new IWidgetLocator[0];
		Control messageControl = DialogInspector.getMessageControl(dialog);
		return new IWidgetLocator[]{WidgetReference.create(messageControl)};
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		//note: although we implement find, we implement matching to play nice w/ the recorder
//		return new IWidgetMatcher() {
//			public boolean matches(Object widget) {
//				if (!(widget instanceof Control))
//					return false;
//				return widget == DialogFinder.findActiveDialogMessageControl();
//			}
//		};
		return new ISWTWidgetMatcher() {
			public boolean matches(ISWTWidgetReference<?> ref) {
				Object widget = ref.getWidget();
				if (!(widget instanceof Control))
					return false;
				return widget == DialogFinder.findActiveDialogMessageControl();
			}
		};
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getWidgetText(org.eclipse.swt.widgets.Control)
	 */
	protected String getWidgetText(Control control) throws WidgetSearchException {
		if (control instanceof Label)
			return ((Label)control).getText();
		if (control instanceof CLabel)
			return ((CLabel)control).getText();
		if (control instanceof Text)
			return ((Text)control).getText();
		return null;
	}
	
	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
}
