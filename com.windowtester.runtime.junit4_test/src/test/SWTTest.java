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
package test;

import static com.windowtester.runtime.junit4.UIFactory.getUI;
import static org.junit.Assert.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.junit4.UITestRunner.Launch;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.junit4.TestRunnerSWT;
import com.windowtester.runtime.swt.locator.ButtonLocator;

/**
 * Sample JUnit4 SWT test.
  *
 * @author Phil Quitslund
 *
 */
@RunWith(TestRunnerSWT.class)
@Launch(main=SWTTest.SimpleShell.class) //should we be able to specify if we are to run on the UI thread?
public class SWTTest {

	private static final String SHELL_TEXT = "Multi Button Test";
	private static String BUTTON_TEXT = "button";
	
	public static class SimpleShell {

		public static void main(String[] args) {
			//TODO: should there be an option to run main on the UI thread?
			DisplayExec.sync(new Runnable() {
				public void run() {
					Shell shell = createButtonShell();
					final Display display = Display.getDefault();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}
				}
			});
		}

		private static Shell createButtonShell() {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 4;
		 	Shell shell = new Shell();
			shell.setLayout(gridLayout);
			shell.setSize(400, 67);
			shell.setText(SHELL_TEXT);

			for (int i=0; i < 4; ++i) {
				Button button1 = new Button(shell, SWT.NONE);
				button1.setText(BUTTON_TEXT);			
			}
			shell.open();
			shell.layout();
			return shell;
		}
		
	}

	@Test
	public void verifyClicks() throws WidgetSearchException {
		IUIContext ui = getUI();
		
		IWidgetLocator[] items = ui.findAll(new ButtonLocator(BUTTON_TEXT));
		assertEquals(4, items.length);
		for (IWidgetLocator button : items) {
			ui.click(button); //failure would throw an exception
		}
	}
	
	
}
	
