package com.windowtester.test.gef.tests.smoke;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Tree;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EclipseLocators;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;

/**
 * Repro for: http://fogbugz.instantiations.com//default.php?32726
 * 
 * NOTE [12/28/2009 pq]: Intermittent failures being tracked here: https://fogbugz.instantiations.com/default.php?44684
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class LogicComboMagnifyTest extends BaseTest {

	//NOTE: in his test this is: "GEF (Graphical Editing Framework)/Logic Diagram"
	private static final String LOGIC_DIAGRAM_EXAMPLE_PATH = "GEF Team Examples/Logic Diagram";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getUI().ensureThat(EclipseLocators.view("Package Explorer").isShowing());
//		getUI().wait(TimeElapsedCondition.seconds(3));
	}
	
	public void testBroken_Combo() throws Exception {
		IUIContext ui = getUI();
		
		ui.contextClick(new SWTWidgetLocator(Tree.class, new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")), "Ne&w/P&roject...");
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("General/Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("stuff");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("Progress Information"));
		ui.wait(new ShellDisposedCondition("New Project"));
		ui.click(new MenuItemLocator("File/New/Example..."));
		ui.wait(new ShellShowingCondition("New Example"));
		ui.click(new TreeItemLocator(LOGIC_DIAGRAM_EXAMPLE_PATH));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("Progress Information"));
			
		comboClick(ui, "75%");
		comboClick(ui, "150%");
		comboClick(ui, "200%");
		comboClick(ui, "300%");
		comboClick(ui, "50%");
			
	}


	private void comboClick(IUIContext ui, String item) throws WidgetSearchException {
//		ui.pause(2000);
//		new WidgetPrinter().print();
//		System.out.println("---------------------------------------");
//		new DebugHelper().printWidgets();
//		fail();
		IWidgetLocator clicked = ui.click(new ComboItemLocator(item));
		Combo combo = (Combo) ((IWidgetReference)clicked).getWidget();
		assertEquals(item, getText(combo));
	}


	private String getText(final Combo combo) {
		final String [] text = new String[1];
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				text[0] = combo.getText();
			}
		});
		return text[0];
	}
	
}
