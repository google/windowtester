package com.windowtester.test.eclipse.locator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.eclipse.ActiveEditorCondition;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SectionLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.locator.swt.forms.FormDialog;

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
public class SectionLocatorSmokeTest extends BaseTest {

	
	private static final ConfirmPerspectiveSwitchShellHandler HANDLER = new ConfirmPerspectiveSwitchShellHandler();

	private String projectName = getClass().getName() + "Project";
	
	private ButtonFormDialog dialog;

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		addHandler(HANDLER);
	}
	
	@Override
	protected void tearDown() throws Exception {
		closeDialogIfNecessary();
		saveAllIfNecessary();
		removeHandler(HANDLER);
		super.tearDown();
	}

	private void closeDialogIfNecessary() {
		if (dialog != null){
			DisplayReference.getDefault().execute(new VoidCallable() {
				@Override
				public void call() throws Exception {
					try {
						dialog.close();
					} catch(NullPointerException e) {
						//Occasionally tests fail with this:
//						Caused by: java.lang.NullPointerException
//						at org.eclipse.ui.forms.widgets.FormToolkit.dispose(FormToolkit.java:711)
//						at com.windowtester.test.locator.swt.forms.FormDialog.close(FormDialog.java:59)
						//Ignoring it should be safe
					}
				}
			});
			
		}
	}
	
	private void addHandler(IShellConditionHandler handler) {
		((IShellMonitor)getUI().getAdapter(IShellMonitor.class)).add(handler);
	}

	private void removeHandler(IShellConditionHandler handler) {
		((IShellMonitor)getUI().getAdapter(IShellMonitor.class)).remove(handler);
	}

	
	public void testDriveManifestEditor() throws Exception {
		IUIContext ui = getUI();
		try{
			ui.click(new MenuItemLocator("File/New/Other..."));
			ui.wait(new ShellShowingCondition("New"));
			ui.click(new TreeItemLocator("General"));
			ui.click(new TreeItemLocator("Plug-in Development/Plug-in Project"));
			ui.click(new ButtonLocator("Next >"));
			ui.enterText(projectName);
			ui.click(new ButtonLocator("Next >"));
			ui.click(new ButtonLocator("Finish"));
			// Extra long timeout for Dan's slow Linux box
			ui.wait(new ShellDisposedCondition("New Plug-in Project"), 60000);
			
			ui.wait(ActiveEditorCondition.forName(projectName), 5000);
	
			// Click the "Overview" tab to make sure we are on that page
			ui.click(new CTabItemLocator("Overview"));
			// Sometimes, the button is partially scrolled off the page, 
			// so zoom the editor and click the top left corner of the button
			ui.click(2, new CTabItemLocator(projectName));
			ui.click(new ButtonLocator("Add...", new SectionLocator("Execution Environments")));
			
			
			ui.wait(new ShellShowingCondition("Execution Environments"));
			ui.click(new TableItemLocator("J2SE-1.3"));
			ui.click(new ButtonLocator("OK"));
			ui.wait(new ShellDisposedCondition("Execution Environments"));
	
			ui.click(new TableItemLocator("J2SE-1.3", new SectionLocator("Execution Environments")));
			ui.assertThat(new ButtonLocator("Remove", new SectionLocator("Execution Environments")).isEnabled());
			
			// Save the changes
			ui.click(new MenuItemLocator("File/Save"));
		}finally{
			// Unzoom the editor
			ui.click(2, new CTabItemLocator(projectName));
			ui.ensureThat(new CTabItemLocator(projectName).isClosed());
		}
		
	}

	
	
	private static class ButtonFormDialog extends FormDialog {

		public ButtonFormDialog() {
			super(new Shell(Display.getDefault()));
		}
		@Override
		protected void createFormContent(IManagedForm mform) {
			ScrolledForm form = mform.getForm();
			Composite body = form.getBody();
			TableWrapLayout layout = new TableWrapLayout();
			//layout.numColumns =2;
			body.setLayout(layout);
			FormToolkit toolkit = mform.getToolkit();
			
			
			FormText formText = mform.getToolkit().createFormText(
					form.getBody(), true);
			form.getBody().setLayout(layout);
			String text = "Some text.";
			formText.setText(text, false, false);
			
			

			final Section section = toolkit.createSection(body,
					Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
			TableWrapData td = new TableWrapData(TableWrapData.FILL);
			//td.colspan = 2;
			section.setLayoutData(td);
			section.setText("Section");
			toolkit.createCompositeSeparator(section);
			section.setDescription("This section holds some buttons.");

			final Composite sectionClient = toolkit.createComposite(section);
			sectionClient.setLayout(new TableWrapLayout());
//
			for (String name : testButtonLabels) {
				/*final Button b = */ toolkit.createButton(sectionClient, name,
						SWT.RADIO);

			}

			section.setClient(sectionClient);
//
			

			
		}
		
		protected void configureShell(Shell newShell) {
			newShell.setText("TestShell");
			super.configureShell(newShell);
		}

	}


	
	// http://fogbugz.instantiations.com/default.php?34731
	public void testFindAllButtonsInSection() throws Exception {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				dialog = new ButtonFormDialog();
				dialog.open();
			}
		});	
		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("TestShell"));
		ButtonLocator locator = new ButtonLocator(".*", new SectionLocator("Section"));
		
		IWidgetLocator[] buttons = ui.findAll(locator);
		assertEquals(testButtonLabels.length, buttons.length);
		
		ui.wait(TimeElapsedCondition.milliseconds(2000));
		ui.click(new ButtonLocator("OK"));

		
	}

	private static String[] testButtonLabels = new String[]{"One", "Two", "Three"};


	static Shell getActiveShell() {
		Shell activeShell = ShellFinder.getActiveShell(Display.getDefault());
		if (activeShell != null)
			return activeShell;
		return new Shell(Display.getDefault());
	}
	
	
	
}
