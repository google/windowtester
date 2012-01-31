package com.windowtester.test.eclipse;


import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.WidgetShowingCondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;



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
public class ConsoleViewFindingSmokeTest extends BaseTest {

	
	public void testConsole() throws Exception {
		IUIContext ui = getUI();
		
		openJavaPerspective(ui);
		openConsoleView(ui);
		doWriteToConsole();
		
		IWidgetLocator consoleLocator = new SWTWidgetLocator(StyledText.class, new ViewLocator("org.eclipse.ui.console.ConsoleView"));
		
		/*
		 * this is failing intermittently because (I think) the view is not quite ready yet 
		 * (e.g., the styled test has not been rendered yet).
		 */
		ui.wait(new WidgetShowingCondition(ui, consoleLocator));
		
		ui.click(new XYLocator(consoleLocator, 210, 22));
		
	}


	private void openConsoleView(IUIContext ui) throws WidgetSearchException {
		ui.click(new MenuItemLocator("Window/Show View/Console"));
	}


	private void openJavaPerspective(IUIContext ui)
			throws WidgetSearchException, WaitTimedOutException {
		ui.click(new MenuItemLocator("Window/Open Perspective/Other..."));
		ui.wait(new ShellShowingCondition("Open Perspective"));
		ui.click(new TableItemLocator("Java \\(default\\)|Java"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Open Perspective"));
	}


	private void doWriteToConsole() {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				try {
					writeToConsole();
				} catch (PartInitException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		});
	}


	private void writeToConsole() throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		String id = "org.eclipse.ui.console.ConsoleView"; // IConsoleConstants.ID_CONSOLE_VIEW;
		page.showView(id);

		MessageConsole myConsole = findConsole(id);
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println("Hello console!");
	}
	
	
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
