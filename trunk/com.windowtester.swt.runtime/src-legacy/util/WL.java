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
package com.windowtester.runtime.swt.legacy.util;

import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.legacy.util.LegacyLocatorAdapter;
import com.windowtester.swt.WidgetLocator;


/**
 * Utilities for migrating from the (old) {@link com.windowtester.swt.IUIContext}} to the new 
 * {@link com.windowtester.runtime.IUIContext} API. 
 * 
 * @author Phil Quitslund
 * @deprecated Use the new WindowTester API instead
 */
public class WL {

	
	/**
	 * Take the given legacy {@link WidgetLocator} instance and adapt it to an {@link IWidgetLocator}.
	 * For example, given a legacy locator for a <code>Button</code> you could adapt it and use it in a
	 * selection like so:
	 * <pre>
	 *  ui.click(WL.adapt(new WidgetLocator(Button.class, "button")));
	 * </pre>
	 * A more interesting legacy locator identify a <code>Tree</code> in a <code>Composite</code> could be adapted like this:
	 * <pre>
	 *  ui.click(new TreeItemLocator("path/to/item", WL.adapt(new WidgetLocator(Tree.class, new WidgetLocator(Composite.class)))));
	 * </pre>
	 * Note: this utility is meant to be used as a means towards migration.  In all cases a {@link IWidgetLocator} should be
	 * easy to derive from the legacy {@link WidgetLocator} and this is prefered.
	 * <br>
	 * @param legacyLocator the locator to adapt
	 * @return an adapted locator
	 */
	public static IWidgetLocator adapt(WidgetLocator legacyLocator) {
		return new LegacyLocatorAdapter(legacyLocator);
	}
	
	
}
