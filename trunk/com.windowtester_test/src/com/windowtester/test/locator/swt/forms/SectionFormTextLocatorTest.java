package com.windowtester.test.locator.swt.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.locator.forms.SectionFormTextLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
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
 *******************************************************************************/
public class SectionFormTextLocatorTest extends AbstractFormTextLocatorTest {


	private static final String SECTION_2_TITLE = "Section 2";
	private static final String GOOGLE_HREF = "http://www.google.com";
	private static final String GOOGLE_LINK_TEXT = "g00gle";
	private static final String SLASHDOT_LINK = "http://slashdot.org/";

	@Override
	protected void doCreateSections(Composite body, FormToolkit toolkit,
			TableWrapLayout layout) {
		super.doCreateSections(body, toolkit, layout);
		createSection2(body, toolkit, layout);
	}

	private void createSection2(Composite body, FormToolkit toolkit,
			TableWrapLayout layout) {
		Section section2 = toolkit.createSection(body, Section.TITLE_BAR);
		section2.setLayout(layout);
		section2.setText(SECTION_2_TITLE);
		
		FormText formText = toolkit.createFormText(
				section2, true);
		body.setLayout(layout);
		formText.setText(getSection2FormText(), true, true);
		section2.setClient(formText);
		formText.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				System.out.println(e);
				lastClickedHref = e.getHref();
			}
		});
	}
	
	private String getSection2FormText() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>"); //$NON-NLS-1$
		buffer.append("<p>");
		buffer.append("A link to <a href=\"" + GOOGLE_HREF + "\" nowrap=\"true\">" + GOOGLE_LINK_TEXT + "</a>.");
		buffer.append("</p>");
		
		buffer.append("<p>And slashdot: " + SLASHDOT_LINK + "</p>");
		buffer.append("</form>"); //$NON-NLS-1$
		return buffer.toString();
	}

	public void testDebug() throws Exception {
		new DebugHelper().printWidgets();		
//		IUIContext ui = getUI();
//		ui.pause(3000);
	
	}
	
	public void testAssertHasHyperlinkWithText() throws Exception {		
		getUI().assertThat(new SectionFormTextLocator(SECTION_1_TITLE).hasHyperlink().withText(ECLIPSE_ORG_LINK));
	}
	
	public void testAssertHyperlinkHasTextAndHref() throws Exception {		
		getUI().assertThat(new SectionFormTextLocator(SECTION_1_TITLE).hasHyperlink().withText(ACME_LINK_TEXT).withHRef(ACME_HREF));
	}
	
	public void testAssertHyperlinkHasHref() throws Exception {		
		getUI().assertThat(new SectionFormTextLocator(SECTION_1_TITLE).hasHyperlink().withHRef(ACME_HREF));
	}

	public void testClickLinkByHRef() throws Exception {		
		getUI().click(new SectionFormTextLocator(SECTION_1_TITLE).hyperlink().withHRef(ACME_HREF));
		assertHRefClicked(ACME_HREF);
	}
	
	public void testClickLinkByText() throws Exception {		
		getUI().click(new SectionFormTextLocator(SECTION_2_TITLE).hyperlink().withText(SLASHDOT_LINK));
		assertHRefClicked(SLASHDOT_LINK);
	}
	
	public void testCollectAllHyperlinks() throws Exception {	
		IWidgetLocator[] all = getUI().findAll(new SectionFormTextLocator(SECTION_2_TITLE).hyperlink().withText(".*"));
		assertEquals(GOOGLE_LINK_TEXT, ((IHyperlinkReference)all[0]).getText());
		assertEquals(SLASHDOT_LINK, ((IHyperlinkReference)all[1]).getHref());
	}
	
	
}
