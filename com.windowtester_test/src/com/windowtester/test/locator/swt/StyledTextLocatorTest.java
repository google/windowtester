/*******************************************************************************
 *  Copyright (c) 2013 Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Frederic Gurr - initial API and implementation
 *******************************************************************************/
package com.windowtester.test.locator.swt;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.StyledTextLocator;
import com.windowtester.test.locator.swt.shells.StyledTextTestShell;

public class StyledTextLocatorTest extends AbstractLocatorTest {

	StyledTextTestShell window;
	
	@Override
	public void uiSetup() {
		window = new StyledTextTestShell();
		window.open();
	}

	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public void testStyledText() throws Exception {
		IUIContext ui = getUI();
		StyledTextLocator styledTextLocator = new StyledTextLocator();
		ui.click(styledTextLocator);
		ui.assertThat(styledTextLocator.hasLineOfText(2, "Third line"));
		ui.assertThat(styledTextLocator.hasLineOfText(4, "Fifth line"));
	}

}