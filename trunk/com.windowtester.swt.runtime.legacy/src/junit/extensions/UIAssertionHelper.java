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
package junit.extensions;

import junit.framework.Assert;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.selector.UIProxy;

import abbot.tester.swt.ControlTester;
import abbot.tester.swt.MenuItemTester;
import abbot.tester.swt.MenuTester;
import abbot.tester.swt.ScrollBarTester;
import abbot.tester.swt.ToolItemTester;
import abbot.tester.swt.WidgetTester;


/**
 * A helper class that executes assertions in the context of the UI thread.
 *
 * This is a tentative service...  See assertIsEnabled for a sketch of how these assertions 
 * might be implemented
 * 
 * If we decide to go for it, we should be exhaustive with the assertions.
 *
 *
 * @author Phil Quitslund
 *
 */
public class UIAssertionHelper {

	
	/**
	 * Asserts that the given widget has been disposed.
	 * @param w - the widget in question
	 * @param isDisposed - whether it is expected to be disposed
	 */
	public static void assertIsDisposed(Widget w, boolean isDisposed) {
		boolean result = new WidgetTester().isDisposed(w);
		Assert.assertEquals(result, isDisposed);
	}
	
	/**
	 * Asserts that the given widget is enabled.
	 * @param w - the widget in question
	 * @param isEnabled - whether it is expected to be enabled
	 * @throws UnsupportedOperationException - if the "isEnabled" operation is not supported by the widget
	 */
	public static void assertIsEnabled(Widget w, boolean isEnabled) throws UnsupportedOperationException {
		//nullCheck(w); TODO: should we guard with null checks?
		boolean result = false;
		if (w instanceof Control) {
			result = new ControlTester().getEnabled((Control)w);
		} else if (w instanceof Menu) {
			result = new MenuTester().getEnabled((Menu)w);
		} else if (w instanceof MenuItem) {
			result = new MenuItemTester().getEnabled((MenuItem)w);
		} else if (w instanceof ToolItem) {
			result = new ToolItemTester().getEnabled((ToolItem)w);
		} else if (w instanceof ScrollBar) {
			result = new ScrollBarTester().getEnabled((ScrollBar)w);
		} else {
			unsupportedOp(w, "isEnabled");
		}
		Assert.assertEquals(result, isEnabled);
	}


	public static void assertIsVisible(Widget w, boolean isVisible) throws UnsupportedOperationException {
		if (!(w instanceof Control))
			unsupportedOp(w, "isVisible");
		Control c = (Control)w;
		boolean result = new ControlTester().getVisible(c);
		//TODO: fill in the rest (like assertIsEnabled)
		Assert.assertEquals(result, isVisible);
	}
	
	public static void assertSelectionEquals(Widget w, ISelection selection)  throws UnsupportedOperationException {

	}	
	public static void assertSelectionEquals(Widget w, boolean isSelected)  throws UnsupportedOperationException {

	}
	
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Utiltity functions
	//
	////////////////////////////////////////////////////////////////////////////////
	
	private static void unsupportedOp(Widget w, String op) {
		throw new UnsupportedOperationException("Widget : " + toString(w) + " does not support the <" + op  + "> operation");
	}

	
	public static String toString(Widget w) {
		return UIProxy.getToString(w);
	}
	
}
