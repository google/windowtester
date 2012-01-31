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
package com.windowtester.internal.runtime.selector;

import com.windowtester.runtime.IWidgetSelectorDelegate;

/**
 * The widget selector service maps widget classes to selectors (implementers of
 * {@link com.windowtester.runtime.IWidgetSelectorDelegate}).
 * The widget selector service can be retrieved from the runtime via
 * an adapter defined on {@link com.windowtester.swt.IUIContext}:
 * <pre>
 *  IWidgetSelectorService wss = getUIContext().getAdapter(IWidgetSelectorService.class);
 *  wss.add(MyWidget.class, new MyWidgetSelector());   //add a selector for custom MyWidget class
 *  wss.add(List.class, new CustomizedListSelector()); //override default list selector
 * </pre>
 * 
 * @see com.windowtester.runtime.IWidgetSelectorDelegate
 * 
 */

public interface IWidgetSelectorService {

	/**
	 * Set this class, selector association.
	 * @param widgetClass the widget class
	 * @param selector the associated selector delegate
	 */
	void set(Class widgetClass, IWidgetSelectorDelegate selector);
	
	/**
	 * Get the widget selector associated with the given type.  If none has 
	 * been registered, return <code>null</code>.
	 * @param widgetClass the widget class in question
	 * @return the associated widget selector (or <code>null</code>)
	 */
	IWidgetSelectorDelegate get(Class widgetClass);
	
}
