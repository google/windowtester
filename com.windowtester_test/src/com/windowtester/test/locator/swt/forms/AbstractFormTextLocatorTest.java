package com.windowtester.test.locator.swt.forms;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
import com.windowtester.test.util.TestCollection;

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
public class AbstractFormTextLocatorTest extends UITestCaseSWT {

	protected static final String INSTANTIATIONS_LINK = "http://www.instantiations.com";
	protected static final String ECLIPSE_ORG_LINK = "http://www.eclipse.org";
	protected static final String SHELL_TITLE = "TestShell";
	protected static final String SECTION_1_TITLE = "Section";
	protected Object lastClickedHref;

	
	static final String ACME_LINK_TEXT = "Acme";

	static final String ACME_HREF = "http://www.acme.org";
	
	private static final String TEST_IMAGE_NAME = "assertion.gif";

	final class HRefClickedCondition implements ICondition {
		private final String link;

		public HRefClickedCondition(String link) {
			this.link = link;
		}

		public boolean test() {
			if (lastClickedHref == null)
				return false;
			return lastClickedHref.equals(link);
		}
	}


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

	
	@Override
	protected void tearDown() throws Exception {
		dismiss();
	}

	private void dismiss() throws WidgetSearchException {		
//		if (new ShellShowingCondition(SHELL_TITLE).test())
//			getUI().click(new ButtonLocator("Cancel"));
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				dialog.close();
			}
		});
	}

	private TestFormDialog dialog;
	
	public void setUp() throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				dialog = new TestFormDialog();
				dialog.open();
			}
		});
//		TimeUnit.SECONDS.sleep(3);
		getUI().wait(new ShellShowingCondition(SHELL_TITLE));
		setLastClickedLinkToNull();
	}

	protected void setLastClickedLinkToNull() {
		lastClickedHref = null;
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
		formText.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				System.out.println(e);
				lastClickedHref = e.getHref();
			}
		});
		
		formText.setImage("image", getImage());
	}
	
//	public void assertLastLinkClickedEquals(Object actual) {
//		assertEquals(lastClickedHref, actual);
//	}
	
	private Image getImage() {
		ImageData data = new ImageData(getClass().getResourceAsStream(TEST_IMAGE_NAME));
		return new Image(Display.getDefault(), data);
	}

	protected void assertHRefClicked(String href) throws WaitTimedOutException {
		getUI().assertThat(new HRefClickedCondition(href));
	}

	protected void assertLinkHrefsEqual(IWidgetLocator[] links, String ... hrefs) {
		TestCollection.assertContainsOnly(hrefs, Arrays.asList(links), new Comparator<Object>() {
			public int compare(Object actual, Object expected) {
				return ((IHyperlinkReference)actual).getHref().compareTo((String)expected);
			}			
		});
	}
	
}
