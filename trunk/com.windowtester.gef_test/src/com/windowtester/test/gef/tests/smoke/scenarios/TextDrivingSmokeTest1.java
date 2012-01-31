package com.windowtester.test.gef.tests.smoke.scenarios;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.test.gef.tests.common.BaseTextDrivingTest;

/**
 * Test to verify basic actions in the context of a text diagram.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 * @author Phil Quitslund
 */
public class TextDrivingSmokeTest1 extends BaseTextDrivingTest {

	public void test1TextDrive() throws Exception {
		
		final IUIContext ui = getUI();
		
		ui.click(new XYLocator(new FigureCanvasLocator(), 12, 272));
		
		ui.enterText("This is some text entered by the GEF runtime");
		
		ui.mouseMove(new XYLocator(new FigureCanvasLocator(), 12, 272));
		ui.dragTo(new XYLocator(new FigureCanvasLocator(), 325, 272));
		
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.bold"));
		
		/*
		ui.click(new ComboItemLocator("Courier New Baltic",
				new SWTWidgetLocator(Combo.class, 0, new SWTWidgetLocator(
						ToolBar.class))));
		ui.click(new ComboItemLocator("10", new SWTWidgetLocator(Combo.class,
				1, new SWTWidgetLocator(ToolBar.class))));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.bold"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.italic"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.underline"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.alignLeft"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.alignCenter"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.alignRight"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.ltr"));
		ui.click(new ContributedToolItemLocator("org.eclipse.gef.text.rtl"));
		*/
		
		pauseForModel();
		
		pause(5000);
		
		save();
		
		assertNoLoggedExceptions();
	}

}
