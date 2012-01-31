package com.windowtester.test.locator.swt;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.condition.WidgetShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.test.locator.swt.shells.ButtonTestShell;

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
public class NamedWidgetLocatorTest extends AbstractLocatorTest {

	
	ButtonTestShell _window;
	
	class ButtonCheckedCondition implements ICondition {
		public boolean test() {
			return _window.getButtonChecked();
		}
	}
	
	class ButtonClickedCondition implements ICondition {
		public boolean test() {
			return _window.getButtonClicked();
		}
	}
	
	class ButtonRadioedCondition implements ICondition {
		public boolean test() {
			return _window.getButtonRadioed();
		}
	}
	
	
	@Override
	public void uiSetup() {
		_window = new ButtonTestShell();
		_window.open();
		wait(new WidgetShowingCondition(getUI(), new ButtonLocator("button")));
	}
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	public void testNamedWidgetLocatorClick() throws WidgetSearchException {

		IUIContext ui = getUI();

		assertFalse(_window.getButtonChecked());
		ui.click(new ButtonLocator("check button"));
//		assertTrue(_window.getButtonChecked());
		ui.assertThat(new ButtonCheckedCondition());
		
		assertFalse(_window.getButtonClicked());
		ui.click(new NamedWidgetLocator("b1"));
//		assertTrue(_window.getButtonClicked());
		ui.assertThat(new ButtonClickedCondition());
		

		assertFalse(_window.getButtonRadioed());
		ui.click(new NamedWidgetLocator("rb1"));
//		assertTrue(_window.getButtonRadioed());
		ui.assertThat(new ButtonRadioedCondition());
		
		
		ui.assertThat(new IsEnabledCondition(new NamedWidgetLocator("rb1"), true));
		ui.assertThat(new HasTextCondition(new NamedWidgetLocator("rb1"), "radio button"));
		
		try {
			ui.assertThat(new IsEnabledCondition(new NamedWidgetLocator("rb1"), false));
			fail("toggle button should have been enabled");
		}
		catch (WaitTimedOutException e) {
			// success, so fall through
		}
		try {
			ui.assertThat(new HasTextCondition(new NamedWidgetLocator("rb1"), "boo"));
			fail("toggle button should not have text = boo");
		}
		catch (WaitTimedOutException e) {
			// success, so fall through
		}
	}

}
