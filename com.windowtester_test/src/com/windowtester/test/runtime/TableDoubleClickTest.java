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
package com.windowtester.test.runtime;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.test.locator.swt.AbstractLocatorTest;
import com.windowtester.test.runtime.shells.TableDoubleClickTestShell;

/**
 * Test for proper dismissal of dialog opened by double click on a
 * table item.
 * <p>
 * See Cases 31882, 42438, 42439.
 * 
 * @author Keerti P
 * @author Jaime Wren
 */
public class TableDoubleClickTest extends AbstractLocatorTest {

	TableDoubleClickTestShell _window;
	
	@Override
	public void uiSetup() {
		_window = new TableDoubleClickTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	// See Case 31882 (closed).
	public void testTableItemDoubleClick() throws WidgetSearchException{
		IUIContext ui = getUI();
		// double click, dialog pops up
		ui.click(2, new TableItemLocator("Three"));
		ui.wait(new ShellShowingCondition("Message Dialog"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Message Dialog"));
		ui.click(2, new TableItemLocator(""));
		ui.wait(new ShellShowingCondition("Message Dialog"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Message Dialog"));
	}
	
	// See Case 42438.
	public void testTableItemContextClick() throws WidgetSearchException{
		IUIContext ui = getUI();
		// context click, dialog pops up
		ui.contextClick(new TableItemLocator("Five"), "Action 1");
		ui.wait(new ShellShowingCondition("Message Dialog"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Message Dialog"));
		ui.contextClick(new TableItemLocator("Four"), "Action 2");
		ui.wait(new ShellShowingCondition("Message Dialog"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Message Dialog"));
		ui.contextClick(new TableItemLocator(""), "Action 1");
		ui.wait(new ShellShowingCondition("Message Dialog"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Message Dialog"));
	}
	
	// See Case 42439.
	public void testTableItemAssertions() throws WidgetSearchException{
		IUIContext ui = getUI();

		// table item assertion tests
		ui.click(new TableItemLocator("Five"));
		ui.assertThat(new TableItemLocator("Five").isVisible());
		ui.assertThat(new TableItemLocator("Five").isSelected(true));
		ui.assertThat(new TableItemLocator("Four").isSelected(false));

		ui.click(new TableItemLocator("Four"));
		ui.assertThat(new TableItemLocator("Four").isVisible());
		ui.assertThat(new TableItemLocator("Four").isSelected(true));
		ui.assertThat(new TableItemLocator("Five").isSelected(false));
		
		ui.assertThat(new TableItemLocator("bogus").isVisible(false));
	}
	
	
}
