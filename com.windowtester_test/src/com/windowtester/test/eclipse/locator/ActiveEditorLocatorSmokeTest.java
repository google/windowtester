package com.windowtester.test.eclipse.locator;

import static com.windowtester.test.eclipse.helpers.SimpleProjectHelper.createSimpleFile;
import static com.windowtester.test.eclipse.helpers.SimpleProjectHelper.createSimpleProject;

import java.awt.Composite;

import org.eclipse.core.runtime.Path;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ShellReference;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;
import com.windowtester.test.eclipse.BaseTest;

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
public class ActiveEditorLocatorSmokeTest extends BaseTest {

	@Override
	protected void oneTimeSetup() throws Exception {
		closeWelcomePageIfNecessary();
		createSimpleProject(getUI(), getProjectName());
	}
	
	//simulates the case where there are no active editors
	class NoActiveEditor extends ActiveEditorLocator {
		private static final long serialVersionUID = 1L;
		@Override
		public String getPartName() {
			return null;
		}
	}
	
	public void testNone() throws WidgetSearchException {
		IUIContext ui = getUI();
		closeAllEditors(ui);
		assertNull(new ActiveEditorLocator().getPartName());
	}

	
	public void testOne() throws WidgetSearchException {
		IUIContext ui = getUI();
		createSimpleFile(ui, new Path(getProjectName()), "foo.txt");		
		assertEquals("foo.txt", new ActiveEditorLocator().getPartName());
	}
	
	
	public void testUseInLocatorWhenNonExistent() {
		ShellReference shell = DisplayReference.getDefault().getActiveShell();
		String title = shell.getText();
		try {
			getUI().click(new SWTWidgetLocator(Composite.class, new NoActiveEditor()));
			fail("should have thrown a WNFE");
		} catch (WidgetSearchException e) {
			//pass -- other exceptions (e.g., NPEs are failures)
		}
		// ensure shell has focus after a WNFE is thrown
		try {
			getUI().wait(new ShellShowingCondition(title));
		}
		catch (WaitTimedOutException e) {
			try {
				shell.setActive();
			}
			catch (Exception e1) {
				System.err.println(getClass().getSimpleName() + "#testUseInLocatorWhenNonExistent - failed to set shell active");
				e1.printStackTrace();
			}
			throw e;
		}
	}
	
	
	private void closeAllEditors(IUIContext ui) throws WidgetSearchException {
		// this is a bit cheesy...
		String projectName = getProjectName();
		createSimpleFile(ui, new Path(projectName), "myFile.txt");
		//In Eclipse 4.x "Close All" is only shown, when more than one editor is open
		createSimpleFile(ui, new Path(projectName), "myFile2.txt");
		ui.contextClick(new CTabItemLocator("myFile.txt"), "Close All");
		ui.wait(new JobsCompleteCondition());		
	}

	private String getProjectName() {
		return getClass().getSimpleName();
	}
	
	
}
