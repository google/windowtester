package com.windowtester.test.locator.swt;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
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
public class ButtonLocatorTest extends AbstractLocatorTest {

	
	ButtonTestShell window;
	
	
	@Override
	public void uiSetup() {
		window = new ButtonTestShell();
		window.open();
		wait(new ButtonLocator("button").isVisible());
	}
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public void testClick() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		assertFalse(window.getButtonClicked());
		
		ui.click(new ButtonLocator("button"));
		//NOTICE How verbose this is...
		//ui.click(new XYLocator(new ButtonLocator("button"), 60,20, WT.TOP | WT.LEFT));
		//ui.pause(3000);
		
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.getButtonClicked();
			}
		});
		
//		assertFalse(window.getButtonChecked());
		ui.assertThat(new ICondition() {
			public boolean test() {
				return !window.getButtonChecked();
			}
		});
		
		
		ui.click(new ButtonLocator("check button"));
		//assertTrue(window.getButtonClicked());
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.getButtonClicked();
			}
		});
		
		ui.assertThat("Check button should be enabled", new ButtonLocator("check button").isEnabled());
		ui.assertThat(new ButtonLocator("check button").hasText("check button"));

//		assertFalse(window.getButtonRadioed());
		ui.assertThat(new ICondition() {
			public boolean test() {
				return !window.getButtonRadioed();
			}
		});
		ui.click(new ButtonLocator("radio button"));
//		assertTrue(window.getButtonRadioed());
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.getButtonRadioed();
			}
		});
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("radio button")));
		ui.assertThat(new HasTextCondition(new ButtonLocator("radio button"), "radio button"));
		
//		assertFalse(window.getButtonToggled());	
		ui.assertThat(new ICondition() {
			public boolean test() {
				return !window.getButtonToggled();
			}
		});
		
		ui.click(new ButtonLocator("toggle button"));
//		assertTrue(window.getButtonToggled());
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.getButtonToggled();
			}
		});
		
		
		ui.assertThat(new ButtonLocator("toggle button").isEnabled());
		ui.assertThat(new ButtonLocator("toggle button").hasText("toggle button"));

		String failMsg = "Check button should not be enabled";
		try {
			ui.assertThat(failMsg, new ButtonLocator("toggle button").isEnabled(false));
			fail("toggle button should have been enabled");
		}
		catch (WaitTimedOutException e) {
			assertTrue(e.getMessage().contains(failMsg));
			// success, so fall through
		}
		try {
			ui.assertThat(new ButtonLocator("toggle button").hasText("boo"));
			fail("toggle button should not have text = boo");
		}
		catch (WaitTimedOutException e) {
			// success, so fall through
		}
	}

	
	public void testWidgetRefClick() throws WidgetSearchException {
		//WRefs should be selectable
		IUIContext ui = getUI();
		
		IWidgetReference widget = (IWidgetReference)ui.find(new ButtonLocator("button"));
		
		final boolean state = window.getButtonClicked();
		ui.click(widget);
//		assertTrue(window.getButtonClicked() != state);
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.getButtonClicked() != state;
			}
		});
		
	}
	
	public void testSelection() throws Exception {
		IUIContext ui = getUI();
		ui.assertThat(new ButtonLocator("toggle button").isSelected(false));
		ui.click(new ButtonLocator("toggle button"));
		ui.assertThat(new ButtonLocator("toggle button").isSelected(true));
	}
	
	public void testWidgetEnabled() throws WidgetSearchException {
		// all of the buttons should be enabled
		IUIContext ui = getUI();
		
		ui.assertThat((new ButtonLocator("button")).isEnabled());
		ui.assertThat((new ButtonLocator("toggle button")).isEnabled());
		ui.assertThat((new ButtonLocator("radio button")).isEnabled());
		ui.assertThat((new ButtonLocator("check button")).isEnabled());
		
		ui.assertThat((new ButtonLocator("button")).isEnabled(true));
		ui.assertThat((new ButtonLocator("toggle button")).isEnabled(true));
		ui.assertThat((new ButtonLocator("radio button")).isEnabled(true));
		ui.assertThat((new ButtonLocator("check button")).isEnabled(true));
	}
	
	public void testFocus() throws Exception {
		
		IUIContext ui = getUI();
		
		ui.ensureThat((new ButtonLocator("button")).hasFocus());
		ui.assertThat((new ButtonLocator("button")).hasFocus());
		
		ui.ensureThat((new ButtonLocator("toggle button")).hasFocus());
		ui.assertThat((new ButtonLocator("toggle button")).hasFocus());
		
		ui.ensureThat((new ButtonLocator("radio button")).hasFocus());
		ui.assertThat((new ButtonLocator("radio button")).hasFocus());
		
	}
	
}
