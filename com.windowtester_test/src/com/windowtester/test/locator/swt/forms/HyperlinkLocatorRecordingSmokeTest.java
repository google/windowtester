/**
 * 
 */
package com.windowtester.test.locator.swt.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.test.eclipse.codegen.AbstractRecorderSmokeTest;

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
public class HyperlinkLocatorRecordingSmokeTest extends AbstractRecorderSmokeTest {

	protected static final String SHELL_TITLE = "TestShell";
	protected static final String SECTION_1_TITLE = "Section";
	
	
	protected static final String INSTANTIATIONS_LINK = "http://www.instantiations.com";
	protected static final String ECLIPSE_ORG_LINK = "http://www.eclipse.org";
	
	static final String ACME_LINK_TEXT = "Acme";
	static final String ACME_HREF = "http://www.acme.org";
	
	
	class TestFormDialog extends FormDialog {

		public TestFormDialog() {
			super(new Shell(Display.getDefault()));
		}
		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(SHELL_TITLE);
		}
		
		@Override
		protected void createFormContent(IManagedForm mform) {
			doCreateFormContent(mform);
		}	
	}
	

	protected void doCreateFormContent(IManagedForm mform) {
		ScrolledForm form = mform.getForm();
		Composite body = form.getBody();
		FormToolkit toolkit = mform.getToolkit();
		TableWrapLayout layout = new TableWrapLayout();
		doCreateSections(body, toolkit, layout);
	}
	
	protected void doCreateSections(Composite body, FormToolkit toolkit,
			TableWrapLayout layout) {
		Section section1 = toolkit.createSection(body, Section.TITLE_BAR);
		section1.setLayout(layout);
		section1.setText(SECTION_1_TITLE);
		
		FormText formText = toolkit.createFormText(
				section1, true);
		body.setLayout(layout);
		formText.setText(getSection1FormText(), true, true);
		section1.setClient(formText);
	}
	
	
	public String getSection1FormText() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>"); //$NON-NLS-1$
		buffer.append("<p>");
		buffer.append("A link to the <a href=\"" + ACME_HREF + "\" nowrap=\"true\">" + ACME_LINK_TEXT + "</a> web site.");
		buffer.append(" (With image: <img href=\"image\"/>)");
		buffer.append("</p>");
		buffer.append("<p>An auto-converted link: " + ECLIPSE_ORG_LINK + "</p>");
		buffer.append("<p>And another: " + INSTANTIATIONS_LINK + "</p>");
		buffer.append("</form>"); //$NON-NLS-1$
		return buffer.toString();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		dismiss();
	}

	private void dismiss() throws WidgetSearchException {		
		if (new ShellShowingCondition(SHELL_TITLE).test())
			getUI().click(new ButtonLocator("Cancel"));
	}

	public void setUp() throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				TestFormDialog dialog = new TestFormDialog();
				dialog.open();
			}
		});
		getUI().wait(new ShellShowingCondition(SHELL_TITLE));	
		super.setUp();
	}
	

	
	public void testHyperlinkClicks() throws Exception {
		IUIContext ui = getUI();
		ui.click(new HyperlinkLocator(INSTANTIATIONS_LINK));
	}
	
	
	
	
	
}
