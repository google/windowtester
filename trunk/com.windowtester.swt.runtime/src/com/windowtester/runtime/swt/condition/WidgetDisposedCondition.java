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
package com.windowtester.runtime.swt.condition;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Tests for the disposal of a given widget. To test for disposal of a
 * shell see {@link ShellDisposedCondition}.
 * 
 */
public class WidgetDisposedCondition implements ICondition {
	
	private final ISWTWidgetReference<?> widget;
	
	/**
	 * Create an instance that tests whether the given widget has been disposed.
	 * @param widget the widget in question (must not be <code>null</code>).
	 */
	public WidgetDisposedCondition(Widget widget) {
		Invariants.notNull(widget);
		this.widget = (ISWTWidgetReference<?>) WTRuntimeManager.asReference(widget);
	}

	/**
	 * Test whether the widget is disposed.
	 * @see ICondition#test()
	 */
	public boolean test() {
		return widget.isDisposed();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return widget.toString() + " to be disposed";
	}
	
}