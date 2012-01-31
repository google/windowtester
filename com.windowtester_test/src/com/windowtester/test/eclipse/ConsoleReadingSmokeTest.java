package com.windowtester.test.eclipse;


import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.perspective;
import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

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
import com.windowtester.runtime.condition.WidgetShowingCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
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
public class ConsoleReadingSmokeTest extends UITestCaseSWT {

	

	private static final String CONSOLE_TEXT = "Hello console!";

	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		IUIContext ui = getUI();
		ui.ensureThat(view("Welcome").isClosed());
		ui.ensureThat(perspective("Java").isActive());
		ui.ensureThat(view("Console").isShowing());	
	}

	public void testConsole() throws Exception {
		
		doWriteToConsole();
		
		IUIContext ui = getUI();
	
		IWidgetLocator consoleLocator = new SWTWidgetLocator(StyledText.class, new ViewLocator("org.eclipse.ui.console.ConsoleView"));	
		ui.wait(new WidgetShowingCondition(ui, consoleLocator));
		
	
		IWidgetReference consoleRef = (IWidgetReference) ui.find(consoleLocator);
		String text = getText((StyledText)consoleRef.getWidget());
		assertEquals(CONSOLE_TEXT, text.trim());
		
	}


	private String getText(final StyledText widget) {
		final String[] text = new String[1];
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				text[0] = widget.getText();
			}
		});
		return text[0];
	}

	private void doWriteToConsole() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
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
		out.println(CONSOLE_TEXT);
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
