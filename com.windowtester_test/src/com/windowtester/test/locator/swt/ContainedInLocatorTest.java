/*******************************************************************************
 * Copyright (c) 2012 Softvision GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Max Hohenegger (windowtester@hohenegger.eu). - initial implementation
 *******************************************************************************/
package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TextLocator;
import com.windowtester.test.locator.swt.shells.DynamicCompositeStacksTestShell;

public class ContainedInLocatorTest extends AbstractLocatorTest {
	private static final String HELLO = "Hello";
	private static final String WINDOW_TESTER = "WindowTester";
	private static final String PRO = "Pro";

	DynamicCompositeStacksTestShell _window;

	@Override
	public void uiSetup() {
		_window = new DynamicCompositeStacksTestShell();
		_window.open();
//		wait(new WidgetShowingCondition(getUI(), new ButtonLocator("button")));
	}

	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}

	public void testNamedWidgetLocatorClick() throws WidgetSearchException {
		IUIContext ui = getUI();

		SWTWidgetLocator singleGroupLocator = new SWTWidgetLocator(Group.class, "Single");
		TextLocator singleText = new TextLocator().containedIn(singleGroupLocator);
		ui.click(singleText);
		ui.enterText(HELLO);

		SWTWidgetLocator doubleGroupLocator = new SWTWidgetLocator(Group.class, "Double");
		TextLocator doubleText0 = new TextLocator().containedIn(0, doubleGroupLocator);
		ui.click(doubleText0);
		ui.enterText(WINDOW_TESTER);
		TextLocator doubleText1 = new TextLocator().containedIn(1, doubleGroupLocator);
		ui.click(doubleText1);
		ui.enterText(PRO);

		ui.assertThat(new HasTextCondition(singleText, HELLO));
		ui.assertThat(new HasTextCondition(doubleText0, WINDOW_TESTER));
		ui.assertThat(new HasTextCondition(doubleText1, PRO));

		//Index test
		ui.assertThat(new ButtonLocator("button" , 0, new SWTWidgetLocator(Shell.class)).isEnabled());
		ui.assertThat(new ButtonLocator("button" , 1, new SWTWidgetLocator(Shell.class)).isEnabled(false));
	}
}
