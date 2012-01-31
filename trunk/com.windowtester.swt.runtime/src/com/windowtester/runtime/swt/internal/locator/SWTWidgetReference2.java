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
package com.windowtester.runtime.swt.internal.locator;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.InaccessableWidgetException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Eventually, this class will replace WidgetReference on the SWT side and there will be a
 * SwingWidgetReference on the Swing side.
 */
public class SWTWidgetReference2
	implements IWidgetReference, IsEnabled
{
	private final Widget widget;

	/**
	 * Construct a new instance wrappering this particular widget
	 * 
	 * @param widget
	 */
	public SWTWidgetReference2(Widget widget) {
		if (widget == null)
			throw new IllegalArgumentException();
		this.widget = widget;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
	 */
	public Object getWidget() {
		return widget;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return new IWidgetLocator[]{
			this
		};
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return this.widget == widget;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsEnabled#isEnabled(com.windowtester.runtime.IUIContext)
	 */
	public boolean isEnabled(IUIContext ui) throws WidgetSearchException {
		final boolean[] result = new boolean[1];
		final Exception[] exception = new Exception[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					if (widget.isDisposed())
						result[0] = false;
					else if (widget instanceof Control)
						result[0] = ((Control) widget).isEnabled();
					else if (widget instanceof MenuItem)
						result[0] = ((MenuItem) widget).isEnabled();
					else
						throw new InaccessableWidgetException("Cannot determine if widget is enabled: " + widget.getClass().getName());
				}
				catch (Exception e) {
					exception[0] = e;
				}
			}
		});
		if (exception[0] != null)
			throw new WidgetSearchException(exception[0]);
		return result[0];
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given widget is enabled.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isEnabled(true)</code>
	 */
	public IUICondition isEnabled() {
		return isEnabled(true);
	}
	
	/**
	 * Create a condition that tests if the given widget is enabled.
	 * @param selected 
	 * @param expected <code>true</code> if the menu is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */            
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}

}
