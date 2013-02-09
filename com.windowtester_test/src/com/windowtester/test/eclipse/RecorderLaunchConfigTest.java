package com.windowtester.test.eclipse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.WidgetPrinter;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.util.DebugHelper;

/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *  Frederic Gurr - fixed test for Eclipse 3.6+
 *******************************************************************************/
public class RecorderLaunchConfigTest extends BaseTest {

	public void testSWTLaunchConfigExists() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PullDownMenuItemLocator(getRecordDialogMenuPath(), new ContributedToolItemLocator("org.eclipse.ui.recordAction")));
		ui.wait(new ShellShowingCondition(getLaunchConfigShellName()));
		ui.click(new TreeItemLocator("SWT Application"));
		new WidgetPrinter().print();
		ui.click(new SWTWidgetLocator(ToolItem.class, "", 0, new SWTWidgetLocator(ToolBar.class)));
//		here --
		new WidgetPrinter().print();
		new DebugHelper().printWidgets();
		clickProjectText(ui);
		clickMainClassText(ui);
		ui.click(new ButtonLocator("Close"));
		ui.wait(new ShellDisposedCondition(getLaunchConfigShellName()));
		
	}



	private void clickMainClassText(IUIContext ui) throws WidgetSearchException {
		//another annoying Eclipse version difference.... (it was actually changed back again in Eclipse 3.6 (maybe also in Eclipse 3.5.1 or 3.5.2?!)
		IWidgetLocator textLocator = EclipseUtil.isVersion_32() || EclipseUtil.isAtLeastVersion_36() ? 
				new SWTWidgetLocator(Text.class, new SWTWidgetLocator(Group.class, "&Main class:")) : 
					new SWTWidgetLocator(Text.class, new SWTWidgetLocator(Composite.class, new SWTWidgetLocator(Group.class, "&Main class:")));
		ui.click(textLocator);
	}

	private void clickProjectText(IUIContext ui) throws WidgetSearchException {
		ui.click(new SWTWidgetLocator(Text.class, new SWTWidgetLocator(Group.class, "&Project:")));
	}

	private String getLaunchConfigShellName() {
		return "Record( Configurations)?"; //3.4M7+ added "Configurations...
	}
	
	private String getRecordDialogMenuPath() {
		//ick: notice that we need to escape the periods?  --- this is because the periods form a pattern that
		//overeagerly matches (namely "Record As" in addition to the target "Record...")
		//another option would be to use the key accelerator to disambiguate
		if (EclipseUtil.isVersion_32())
			return "Record\\.\\.\\.";
		if (EclipseUtil.isVersion_33())
			return "Open Record Dialog...";
		return "Record Configurations..."; //3.4M4+
	}
	
	
}
