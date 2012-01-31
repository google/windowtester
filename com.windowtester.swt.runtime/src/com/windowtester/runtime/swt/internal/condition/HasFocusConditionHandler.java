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
package com.windowtester.runtime.swt.internal.condition;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.UICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

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

	public boolean hasFocus(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator found = ui.find(locator);
		if (!(found instanceof IWidgetReference))
			return false;
		final Object widget = ((IWidgetReference) found).getWidget();
		final boolean[] result = new boolean[1];
		final Exception[] exception = new Exception[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					result[0] = widget == Display.getDefault().getFocusControl();
				} catch (Exception e) {
					exception[0] = e;
				}
			}
		});
		if (exception[0] != null)
			throw new WidgetSearchException(exception[0]);
		return result[0];
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Moved from UIContextSWT
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void setFocus(IUIContext ui) throws WidgetSearchException {
		Widget widget = findWidget(ui, locator);
		setFocus(widget);
	}

	 private void focus(final Control c) {
		 c.getDisplay().syncExec(new Runnable(){
		 	public void run(){
		 		c.forceFocus();
		 	}		 
		 });
	 }

	/** Set the focus on to the given component. 
	/* TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS */
	private void setFocus(Widget widget) throws WidgetSearchException {
		//handleConditions();
		SWTHierarchyHelper helper = new SWTHierarchyHelper();
		while (!(widget instanceof Control))
			widget = helper.getParent(widget);
		if (widget == null)
			throw new WidgetSearchException("Target of setFocus is null or does not belong to a control");
		
		focus((Control)widget);
		//waitForIdle();
	}
	
	private Widget findWidget(IUIContext ui, IWidgetLocator locator) throws WidgetSearchException {
		IWidgetReference ref = (IWidgetReference) ui.find(locator);
		Object target = ref.getWidget();
		if (target == null)
			throw new IllegalArgumentException("widget reference must not be null");
		if (!(target instanceof Widget))
			return null; //NULL is now a sentinel
		return (Widget)target;
	}
	
}
