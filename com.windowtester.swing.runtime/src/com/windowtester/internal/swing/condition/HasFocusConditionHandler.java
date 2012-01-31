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
package com.windowtester.internal.swing.condition;

import java.awt.Component;

import abbot.tester.ComponentTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.UICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;


/**
 * Has Focus condition handler.
 */
public class HasFocusConditionHandler extends UICondition implements IUIConditionHandler {

	private final IWidgetLocator locator;

	public HasFocusConditionHandler(IWidgetLocator locator){
		this.locator = locator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		setFocus(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		try {
			return hasFocus(ui);
		} catch(WidgetSearchException e){
			return false;
		}
	}

	/**
	 * @param ui
	 * @return
	 * @throws WidgetSearchException
	 */
	public boolean hasFocus(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator found = ui.find(locator);
		if (!(found instanceof IWidgetReference))
			return false;
		final Object widget = ((IWidgetReference) found).getWidget();
		boolean result = ((Component)widget).isFocusOwner();
		return result;
	}
	
	private void setFocus(IUIContext ui) throws WidgetSearchException {
		Component widget = findWidget(ui, locator);
		setFocus(widget);
	}
	
	
	/* TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS */
	private void setFocus(Component widget) throws WidgetSearchException {
				
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.actionFocus(widget);
	}
		
		
	
	private Component findWidget(IUIContext ui, IWidgetLocator locator) throws WidgetSearchException {
		IWidgetReference ref = (IWidgetReference) ui.find(locator);
		Object target = ref.getWidget();
		if (target == null)
			throw new IllegalArgumentException("widget reference must not be null");
		if (!(target instanceof Component))
			return null; //NULL is now a sentinel
		return (Component)target;
	}

	
}