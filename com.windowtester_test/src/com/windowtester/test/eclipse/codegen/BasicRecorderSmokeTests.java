package com.windowtester.test.eclipse.codegen;


import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openPreferences;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.TableItemTester;
import abbot.tester.swt.WidgetTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.eclipse.ViewShowingCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.runtime.swt.locator.TabItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.test.eclipse.EclipseUtil;

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
public class BasicRecorderSmokeTests extends AbstractRecorderSmokeTest {


	@Override
	protected void setUp() throws Exception {
		closeWelcomePageIfNecessary();
		super.setUp();
	}
	
	
	//bug in 3.3
	//	expected: import com.windowtester.runtime.swt.locator.TableItemLocator; but got: import com.windowtester.runtime.swt.locator.SWTWidgetLocator; at [10] expected:<...runtime.swt.locator.[TableItem]Locator;> but was:<...runtime.swt.locator.[SWTWidget]Locator;>
	//
	//	junit.framework.ComparisonFailure: expected: import com.windowtester.runtime.swt.locator.TableItemLocator; but got: import com.windowtester.runtime.swt.locator.SWTWidgetLocator; at [10] expected:<...runtime.swt.locator.[TableItem]Locator;> but was:<...runtime.swt.locator.[SWTWidget]Locator;>
	//		at com.windowtester.test.eclipse.codegen.AbstractRecorderSmokeTest.assertSameAsFileContents(AbstractRecorderSmokeTest.java:226)
	public void XtestTableDoubleClick() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new FilteredTreeItemLocator("WindowTester Support Sandbox/Table In A View"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
		ui.wait(new ViewShowingCondition("TableInAView"));
		
		/*
		 * Event generation using the TableItemLocator is finnicky under 3.3.
		 * To remedy, the generic locator is used instead. 
		 */
		if (EclipseUtil.isVersion_33()) {	
			ui.click(new SWTWidgetLocator(TableItem.class, "New item 1"));
			ui.click(2, new SWTWidgetLocator(TableItem.class, "New item 2"));
			ui.click(new SWTWidgetLocator(TableItem.class, "New item 1"));
		} else {
			ui.click(new TableItemLocator("New item 1", new ViewLocator("TableInAView")));
			ui.click(2, new TableItemLocator("New item 2", new ViewLocator("TableInAView")));
			ui.click(new TableItemLocator("New item 1", new ViewLocator("TableInAView")));			
		}

	}
	
	//In W2 on E3.5.1, we are seeing an extra selection event generated for the first item in the tree
	//https://fogbugz.instantiations.com/default.php?44689
	public void testSimpleJavaProjectCreation() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("JP2");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new TimeElapsedCondition(TimeUnit.MILLISECONDS, 500));
		if(new ShellLocator("Open Associated Perspective?").isVisible().testUI(ui)){
			ui.click(new ButtonLocator("&Yes"));
			ui.wait(new ShellDisposedCondition("Open Associated Perspective?"));
		}
		ui.wait(new ShellDisposedCondition("New Java Project"));
	}

	@Match(Type.INEXACT)
	public void testClickErrorLogCTabItem() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Error Log"));
		//this XY may be brittle...  Not convinced the click coordinates will be exact
		ui.click(new XYLocator(new CTabItemLocator("Error Log"), 59, 11));
	}
	
	//In W2 on E3.5.1, we are seeing an extra selection event generated for the first item in the tree
	//https://fogbugz.instantiations.com/default.php?44689
	public void testClickLabeledTextFieldInNewProjectWizard() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new LabeledTextLocator("&Project name:"));
		ui.enterText("DiddyWahDiddy");
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
	}
	

	@Match(Type.INEXACT)
	public void testClosePreferenceWindow() throws Exception {
		IUIContext ui = getUI();
		openPreferences(ui);
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.ensureThat(new ShellLocator("Preferences").isClosed());
		//cleanup: https://fogbugz.instantiations.com/default.php?44759
	}
	
	public void testOpenSearchToolItem() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Navigator"));
		String id = EclipseUtil.isAtLeastVersion_34() ? "org.eclipse.search.OpenSearchDialogPage" : "org.eclipse.search.ui.openSearchDialog";
		ui.click(new ContributedToolItemLocator(id));
		ui.click(new ButtonLocator("Cancel"));
	}
	
	
	public void testSelectPreferenceTreeItem() throws WidgetSearchException {
		IUIContext ui = getUI();
		openPreferences(ui);
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new FilteredTreeItemLocator("Help"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Inexact matches
	//
	//////////////////////////////////////////////////////////////////////////////
	
	/*
	 * The issue here is that playback does not faithfully produce the same actions that a user would
	 */
	
	@Match(Type.INEXACT)	
	public void testClickSyntaxTabItemInAntPrefPage() throws Exception {
		IUIContext ui = getUI();
		openPreferences(ui);
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("Ant/Editor"));
		/* 
		 * generates (if not already expanded):
		 * 
		 *		ui.click(new TreeItemLocator("Ant"));
		 *		ui.enterText("+");
		 *		ui.click(new TreeItemLocator("Ant/Editor"));
		 */
		ui.click(new TabItemLocator("Synta&x"));
		ui.wait(TimeElapsedCondition.milliseconds(2000));
		
//		ui.click(new TableItemLocator("Text"));
//		ui.click(new SWTWidgetLocator(TableItem.class, "Text"));
			
		//this also works
		TableItem ti = (TableItem) ((IWidgetReference)ui.find(new TableItemLocator("Text"))).getWidget();
		new TableItemTester().actionClick(ti);
				
		ScreenCapture.createScreenCapture("AntEditorPrefPage");
		
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));		
		//ugly pause here! -- trying to remedy occasional failures when run in suite 
		ui.wait(TimeElapsedCondition.milliseconds(2000));
		//conjecture: recorder hasn't had time to fully process events before moving on
		//???: what might a condition look like?

	}
	

	@Match(Type.INEXACT)
	public void XtestTreeItemClickInNamedAntRuntimeTree() throws Exception {
		IUIContext ui = getUI();
		openPreferences(ui);
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("Ant/Runtime"));
		
		/*
		 * 	generates (if not already expanded):
		 * 
		 * 		ui.click(new TreeItemLocator("Ant"));
		 * 		ui.enterText("+");
		 * 		ui.click(new TreeItemLocator("Ant/Runtime"));
		 */
		
		WidgetReference ref;
		if (EclipseUtil.isVersion_31() || EclipseUtil.isVersion_32())
			ref = (WidgetReference) ui.find(
				new SWTWidgetLocator(Tree.class, new SWTWidgetLocator(Composite.class)));
		else
			ref = (WidgetReference) ui.find(
				new SWTWidgetLocator(Tree.class, new SWTWidgetLocator(Composite.class, 0, new LabeledLocator(TabFolder.class, "Settings used when running Ant buildfiles:"))));
		name(ref.getWidget(), "named.tree");
		ui.click(new TreeItemLocator("Contributed Entries", new NamedWidgetLocator("named.tree")));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}
	
	//NOTE: TableItem Recording Issues
	@Match(Type.INEXACT)
	public void XtestCreateProjectAndOpenType() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("JP21");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.click(new MenuItemLocator("Navigate/Open Type..."));
//		ui.wait(new ShellDisposedCondition("Progress Information"));
		ui.wait(new ShellShowingCondition("Open Type"));
		ui.enterText("Object");
		ui.wait(new JobsCompleteCondition());
		
	
		//ui.click(new SWTWidgetLocator(TableItem.class, "Object - java.lan.*"));
		ui.click(new TableItemLocator("Object - java.lan.*"));	
		
		ui.wait(TimeElapsedCondition.milliseconds(2000));
		// Take a screenshot so that if recording comparison fails to match we can determine why
		ScreenCapture.createScreenCapture("OpenType");
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Open Type"));
	}
	
	
	@Match(Type.INEXACT)
	public void XtestCreateNewProjectAndDelete() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new TreeItemLocator("Java/Package Explorer"));
		/*
		 * generates (if not already expanded):
		 * 		ui.click(new TreeItemLocator("Java"));
		 *		ui.enterText("+");
		 * 		ui.click(new TreeItemLocator("Java/Package Explorer"));
		 */
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
		
		//<--- yikes! (when run solo this next click gets lost)
		ui.click(new MenuItemLocator("File/New/Other..."));
		//<----
		
		ui.wait(new ShellShowingCondition("New"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		/*
		 * generates (if not already expanded):
		 * 		ui.click(new TreeItemLocator("Java"));
		 * 		ui.enterText("+");
		 *		ui.click(new TreeItemLocator("Java/Java Project"));
		 */
		
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("JP");
		ui.click(new ButtonLocator("&*Create separate.*"));
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.contextClick(new TreeItemLocator("JP", new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "&Delete.*");
		ui.wait(new ShellShowingCondition("Confirm Project Delete"));
		ui.click(new ButtonLocator("&No"));
		ui.wait(new ShellDisposedCondition("Confirm Project Delete"));
	}

	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	//////////////////////////////////////////////////////////////////////////////

	private void name(Object widget, String name) {
		new WidgetTester().setData((Widget) widget, "name", name);	
	}
	
	


	


}
