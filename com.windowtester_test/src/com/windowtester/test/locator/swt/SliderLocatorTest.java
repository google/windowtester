/*******************************************************************************
 *  Copyright (c) 2012 Phillip Jensen, Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Phillip Jensen - initial API and implementation
 *  Frederic Gurr - alignment to WindowTester code standards
 *******************************************************************************/
package com.windowtester.test.locator.swt;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.HasMaximumCondition;
import com.windowtester.runtime.condition.HasMinimumCondition;
import com.windowtester.runtime.swt.condition.HasSelectionCondition;
import com.windowtester.runtime.swt.locator.SliderLocator;
import com.windowtester.test.locator.swt.shells.SliderTestShell;

public class SliderLocatorTest extends AbstractLocatorTest {

	SliderTestShell window;
	
	@Override
	public void uiSetup() {
		window = new SliderTestShell();
		window.open();
	}

	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public void testSlider() throws Exception {
		IUIContext ui = getUI();
		SliderLocator sliderLocator = new SliderLocator();
		ui.assertThat(sliderLocator.hasMinimum(1));
		ui.assertThat(new HasMinimumCondition(sliderLocator, 1));
		ui.assertThat(sliderLocator.hasMaximum(50));
		ui.assertThat(new HasMaximumCondition(sliderLocator, 50));
		ui.assertThat(sliderLocator.hasSelection(30));
		ui.assertThat(new HasSelectionCondition(sliderLocator, 30));
		ui.assertThat(sliderLocator.isEnabled(true));
		ui.assertThat(sliderLocator.isVisible(true));

		ui.click(sliderLocator);
	}

}