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

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.SWTWidgetReference2;
import com.windowtester.runtime.swt.internal.locator.VirtualItemLocator;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.ComboReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link Combo} widget items.
 */
public class ComboItemLocator extends VirtualItemLocator {
	
	private static final long serialVersionUID = -3460455003831853082L;

	
	/**
	 * Constant to specify how long to wait for the button to be enabled before
	 * attempting to click it.  The default is 3 seconds.
	 * <p>
	 * Note: this is <b>provisional</b> API.
	 */
	public static final long ENABLEMENT_TIMEOUT = 3000;
	
	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new ComboItemLocator(itemText, new SWTWidgetLocator(Combo.class));
	 * </pre>
	 * 
	 * @param itemText the combo item text to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public ComboItemLocator(String itemText) {
		super(Combo.class, itemText);
	}

	//child
	/** 
	 * Create a locator instance.
	 * @param text the combo item text to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */

	public ComboItemLocator(String text, IWidgetLocator parent) {
		super(Combo.class, text, SWTWidgetLocator.adapt(parent));
	}

	//indexed child
	/** 
	 * Create a locator instance.
	 * @param text the combo item text to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public ComboItemLocator(String text, int index, IWidgetLocator parent) {
		super(Combo.class, text, index, SWTWidgetLocator.adapt(parent));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		ComboReference combo = (ComboReference)ui.find(this);
		//Combo combo = (Combo) comboLocator.getWidget();
		//preClick(combo, null, ui);
		//Widget clicked = new ComboSelector(ui).click(combo, getPath());
		combo.click(getPath());
		//postClick(clicked, ui);
		//return new WidgetReference(clicked);	
		return combo;
	}

	@Override
	protected void preClick(IWidgetReference reference, Point offset, IUIContext ui) {
		Widget w = (Widget) reference.getWidget();
		//TODO: should this be user over-rideable? -- should this be in menus too?
		//TODO: should this be pushed up and a a general case?
		ui.wait(new IsEnabledCondition(new SWTWidgetReference2(w)), ENABLEMENT_TIMEOUT);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// IsVisibleLocator
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Return <code>true</code> if the inquiry string is some element in this
	 * combo.
	 * <p>
	 * This override of <code>isVisible</code> is a fix for 39540.
	 */
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator[] locators = ui.findAll(this);
		IWidgetReference reference;
		Combo combo;
		String[] comboStrItems;
		
		if(locators.length == 1 && locators[0] != null && locators[0] instanceof IWidgetReference) {
			reference = (IWidgetReference) locators[0];
			if(reference.getWidget() != null && reference.getWidget() instanceof Combo) {
				combo = (Combo) reference.getWidget();
				comboStrItems = UIProxy.getItems(combo);
				for (int i = 0; comboStrItems != null && i < comboStrItems.length; i++) {
					if(comboStrItems[i].equals(getPath())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ComboItemLocator(\"").append(getPath()).append('\"');
		WidgetLocator controlLocator = getControlLocator();
		//TODO: why might this be set?
		if (controlLocator == null)
			controlLocator = getParentInfo();
		if (controlLocator != null)
			sb.append(", ").append(controlLocator);
		sb.append(')');
		return sb.toString();
	}
	
}
