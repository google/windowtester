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
package com.windowtester.test.eclipse;



import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.NativeShellDisposedCondition;
import com.windowtester.runtime.swt.condition.NativeShellShowingCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.os.IWindowService;
import com.windowtester.runtime.swt.internal.os.InvalidOSDelegate;
import com.windowtester.runtime.swt.internal.os.OSDelegate;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.MessageBoxLocator;
import com.windowtester.test.util.junit.OS;
import com.windowtester.test.util.junit.RunOn;

@SuppressWarnings("restriction")
public class NativeDialogTest extends UITestCaseSWT {


	//OPEN issues: screenshots not being properly taken...
	
	String dialogValue;
	
	@Override
	protected void setUp() throws Exception {
		verifyTestPreconditions();
	}

	private void verifyTestPreconditions() {
		assertTrue(Platform.isRunning());
		assertTrue("native shell handling only implemented for win32", abbot.Platform.isWindows());
		assertFalse("native shell handling tests require a valid OS delegate, but we got " + OSDelegate.getCurrent(), OSDelegate.getCurrent() instanceof InvalidOSDelegate);
		
		IWindowService windowService = OSDelegate.getCurrent().getWindowService();
		assertNotNull("native shell handling requires a valid window service but got: " + windowService, windowService);	
	}
	
	@RunOn(OS.WIN)
	public void testDriveFileDialogFileDoesNotExist() throws Exception {
		
		
		IUIContext ui = getUI();
		Thread thread = new Thread() {
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				try {
					getUI().click(new MenuItemLocator("File/Open File..."));
				} catch (WidgetSearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		thread.start();
		
//		getUI().click(new MenuItemLocator("File/Open File..."));
		
		ui.wait(new NativeShellShowingCondition("Open File"));
		ui.enterText("FooBar");
		ui.keyClick(WT.CR);
		ui.wait(new NativeShellDisposedCondition("Open File"), 1000);
		

		//popped by SWT to inform us it does not exist...
		ui.wait(new ShellShowingCondition("Open File"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Open File"));

	}
	
	@RunOn(OS.WIN)
	public void testDriveFileDialog() throws Exception {
		
		IUIContext ui = getUI();
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				FileDialog fd = new FileDialog(getShell());
				fd.open();
				dialogValue = fd.getFileName();
				}
		});
		
		ui.wait(new NativeShellShowingCondition("Open"));		
		ui.enterText("FooBar");
		ui.keyClick(WT.CR);
		ui.wait(new NativeShellDisposedCondition("Open"));
		
		ui.assertThat(new ICondition() {
			public boolean test() {
				return "FooBar".equals(dialogValue);
			}
		});
	}
	
	
	public void XtestInterruptedFileDialogCreatesScreenShotFails() {
		fail("unimplemented");
	}
	
	@RunOn(OS.WIN)
	public void testInterruptedFileDialogDoesNotHang() throws Exception {
		IUIContext ui = getUI();
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				FileDialog fd = new FileDialog(getShell());
				fd.open();
				dialogValue = fd.getFileName();
				}
		});
		
		ui.wait(new NativeShellShowingCondition("Open"));
		try {
			fail();
		} catch(AssertionFailedError e) {
			//passing is just not hanging...
		}

	}
	
	@RunOn(OS.WIN)
	public void testAssertMessageBoxMsgText() throws Exception {
		final String MSG = "War is over, if you want it.";
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox mb = new MessageBox(getShell());
				mb.setText("Message");
				mb.setMessage(MSG);
				mb.open();
			}
		});
		IUIContext ui = getUI();
		ui.wait(TimeElapsedCondition.milliseconds(3000));
		ui.wait(new NativeShellShowingCondition("Message"));
		ui.assertThat(new MessageBoxLocator().hasMessage(MSG));
		ui.keyClick(WT.CR);
		ui.wait(new NativeShellDisposedCondition("Message"));
	}

	private Shell getShell() {
		return Display.getDefault().getActiveShell();
	}
	
	

	
}
