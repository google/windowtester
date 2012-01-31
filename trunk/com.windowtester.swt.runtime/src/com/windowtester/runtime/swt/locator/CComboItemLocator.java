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

import org.eclipse.swt.custom.CCombo;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.VirtualItemLocator;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.CComboReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link CCombo} items.
 */
public class CComboItemLocator extends VirtualItemLocator {
	
	private static final long serialVersionUID = -3460455003831853082L;
	
	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new CComboItemLocator(itemText, new SWTWidgetLocator(CCombo.class));
	 * </pre>
	 * 
	 * @param itemText the ccombo item text to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public CComboItemLocator(String itemText) {
		super(CCombo.class, itemText);
	}

	//child
	/** 
	 * Create a locator instance.
	 * @param itemText the ccombo item text to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public CComboItemLocator(String text, IWidgetLocator parent) {
		super(CCombo.class, text, SWTWidgetLocator.adapt(parent));
	}

	//indexed child
	/** 
	 * Create a locator instance.
	 * @param itemText the ccombo item text to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public CComboItemLocator(String text, int index, IWidgetLocator parent) {
		super(CCombo.class, text, index, SWTWidgetLocator.adapt(parent));
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		CComboReference combo = (CComboReference)ui.find(this);
		//CCombo combo = (CoCmbo) comboLocator.getWidget();
		preClick(combo, null, ui);
		//Widget clicked = new CComboSelector(ui).click(combo, getPath());
		combo.click(getPath());
		postClick(combo, ui);
		//return new WidgetReference(clicked);	
		return combo;
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
	 * See Case 39540.
	 */
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator[] locators = ui.findAll(this);
		IWidgetReference reference;
		CCombo ccombo;
		String[] ccomboStrItems;
		
		if(locators.length == 1 && locators[0] != null && locators[0] instanceof IWidgetReference) {
			reference = (IWidgetReference) locators[0];
			if(reference.getWidget() != null && reference.getWidget() instanceof CCombo) {
				ccombo = (CCombo) reference.getWidget();
				ccomboStrItems = UIProxy.getItems(ccombo);
				for (int i = 0; ccomboStrItems != null && i < ccomboStrItems.length; i++) {
					if(ccomboStrItems[i].equals(getPath())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
