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
package com.windowtester.swt.condition;

import org.eclipse.swt.widgets.Widget;

/**
 * Condition that tests for the disposal of a given widget. To test for disposal of a
 * shell see {@link com.windowtester.swt.condition.shell.ShellDisposedCondition}.
 * <p>
 *
 * @author Phil Quitslund
 * @deprecated Use {@link com.windowtester.runtime.swt.condition.WidgetDisposedCondition} instead.
 */
public class WidgetDisposedCondition extends com.windowtester.runtime.swt.condition.WidgetDisposedCondition 
	implements ICondition
{
	/**
	 * Create an instance that tests whether the given widget has been disposed.
	 * @param widget the widget in question (must not be <code>null</code>).
	 */
	public WidgetDisposedCondition(Widget widget) {
		super(widget);
	}
}