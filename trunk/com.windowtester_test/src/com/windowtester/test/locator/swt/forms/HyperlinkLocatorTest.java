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
package com.windowtester.test.locator.swt.forms;


import static com.windowtester.test.codegen.CodeGenFixture.*;
import static com.windowtester.test.util.Serializer.serializeOutAndIn;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.eclipse.ui.inspector.WidgetDescriptionLabelProvider;
import com.windowtester.internal.runtime.DefaultCodeGenerator;
import com.windowtester.internal.runtime.ICodeGenerator;
import com.windowtester.internal.runtime.ICodegenParticipant;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.IPropertyProvider;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.locator.WidgetIdentifier;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkMatcher;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator;
import com.windowtester.test.codegen.CodeGenFixture;
import com.windowtester.ui.internal.corel.model.EventSequenceLabelProvider;

public class HyperlinkLocatorTest extends AbstractFormTextLocatorTest {

	private static final String SECTION_2_TITLE = "Section 2";
	private static final String SECTION_3_TITLE = "Section 3";
	private static final String GOOGLE_HREF = "http://www.google.com";
	private static final String GOOGLE_LINK_TEXT = "g00gle";
	private static final String SLASHDOT_LINK = "http://slashdot.org/";

	private static final String DIGG_LINK_TEXT = "Digg";
	private static final String DIGG_HREF = "http://digg.com/";

	private static final String AMAZON_LINK_TEXT = "Amazon";
	private static final String AMAZON_HREF = "http://www.amazon.com/";
	private Hyperlink diggLink;
	private Hyperlink amazonLink;
	private FormText section2FormText;
	
	
	@Override
	protected void doCreateSections(Composite body, FormToolkit toolkit,
			TableWrapLayout layout) {
		super.doCreateSections(body, toolkit, layout);
		createSection2(body, toolkit, layout);
		createSection3(body, toolkit, layout);
	}

	private void createSection2(Composite body, FormToolkit toolkit,
			TableWrapLayout layout) {
		Section section2 = toolkit.createSection(body, Section.TITLE_BAR);
		section2.setLayout(layout);
		section2.setText(SECTION_2_TITLE);
		
		section2FormText = toolkit.createFormText(
				section2, true);
		body.setLayout(layout);
		section2FormText.setText(getSection2FormText(), true, true);
		section2.setClient(section2FormText);
		section2FormText.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				System.out.println(e);
				lastClickedHref = e.getHref();
			}
		});
	}
	
	private void createSection3(Composite body, FormToolkit toolkit,
			TableWrapLayout layout) {
		Section section3 = toolkit.createSection(body, Section.TITLE_BAR);
		section3.setLayout(layout);
		section3.setText(SECTION_3_TITLE);
		body.setLayout(layout);
		
		Composite composite = toolkit.createComposite(section3);
		composite.setLayout(new GridLayout());
		
		diggLink = toolkit.createHyperlink(composite, DIGG_LINK_TEXT, SWT.NONE);
		diggLink.setHref(DIGG_HREF);		
		amazonLink = toolkit.createHyperlink(composite, AMAZON_LINK_TEXT, SWT.NONE);
		amazonLink.setHref(AMAZON_HREF);		
		Menu menu = new Menu(amazonLink);
		new MenuItem(menu, SWT.NONE).setText("Foo");
		amazonLink.setMenu(menu);
		
		addLinkListener(diggLink);
		addLinkListener(amazonLink);
		
		section3.setClient(composite);
	}

	private void addLinkListener(Hyperlink link) {
		link.addHyperlinkListener(new HyperlinkAdapter() {
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

//for now we won't worry about these assertions --- they can be supported if users request...
//	public void testUnsupportedAssertionThrowsException() {
//		getUI().assertThat(new HyperlinkLocator().withText(ACME_LINK_TEXT).isUnderlined();		
//	}
	
	public void testAssertHasHyperlinkSegmentWithText() throws Exception {		
		getUI().assertThat(new HyperlinkLocator(ACME_LINK_TEXT).isVisible());
	}

	public void testAssertHasHyperlinkSegmentWithHref() throws Exception {		
		getUI().assertThat(new HyperlinkLocator().withHRef(ACME_HREF).isVisible());
	}
	
	public void testAssertHyperlinkSegmentHasHref() throws Exception {		
		getUI().assertThat(new HyperlinkLocator(ACME_LINK_TEXT).hasHRef(ACME_HREF));
		//regression test for explicit text specification
		getUI().assertThat(new HyperlinkLocator().withText(ACME_LINK_TEXT).hasHRef(ACME_HREF));
	}
	
	public void testClickSegmentLinks() throws Exception {
		IUIContext ui = getUI();
		ui.click(new HyperlinkLocator(ECLIPSE_ORG_LINK));
		assertHRefClicked(ECLIPSE_ORG_LINK);
		ui.click(new HyperlinkLocator().withHRef(ACME_HREF));
		assertHRefClicked(ACME_HREF);
		ui.click(new HyperlinkLocator(INSTANTIATIONS_LINK));
		assertHRefClicked(INSTANTIATIONS_LINK);
	}


	public void testClickControlLinks() throws Exception {
		IUIContext ui = getUI();
		ui.click(new HyperlinkLocator(DIGG_LINK_TEXT));
		assertHRefClicked(DIGG_HREF);
		ui.click(new HyperlinkLocator().withHRef(AMAZON_HREF));
		assertHRefClicked(AMAZON_HREF);
	}
	
	public void testAssertHasHyperlinkControlWithText() throws Exception {		
		getUI().assertThat(new HyperlinkLocator(AMAZON_LINK_TEXT).isVisible());
	}

	public void testAssertHasHyperlinkControlWithHref() throws Exception {		
		getUI().assertThat(new HyperlinkLocator().withHRef(AMAZON_HREF).isVisible());
	}
	
	public void testAssertHyperlinkControlHasHref() throws Exception {		
		getUI().assertThat(new HyperlinkLocator(DIGG_LINK_TEXT).hasHRef(DIGG_HREF));
	}
	
	
	public void testCollectSegmentsInSection() throws Exception {
		IWidgetLocator[] links = getUI().findAll(new HyperlinkLocator().inSection(SECTION_2_TITLE));
		assertLinkHrefsEqual(links, GOOGLE_HREF, SLASHDOT_LINK);
	}
	
	public void testCollectControlsInSection() throws Exception {
		IWidgetLocator[] links = getUI().findAll(new HyperlinkLocator().inSection(SECTION_3_TITLE));
		assertLinkHrefsEqual(links, AMAZON_HREF, DIGG_HREF);
	}
	

	//note we are piggy-backing on codegen to verify contents...
	public void testIdentifyHyperlinkControls() throws Exception {
		HyperlinkLocator diggLinkLocator = (HyperlinkLocator) identify(diggLink);
		assertNotNull(diggLinkLocator);
		//System.out.println(diggLinkLocator);
		assertCodegensJava5("new HyperlinkLocator(\"Digg\")", diggLinkLocator);
		
		HyperlinkLocator amazonLinkLocator = (HyperlinkLocator) identify(amazonLink);
		//System.out.println(amazonLinkLocator);
		assertCodegensJava5("new HyperlinkLocator(\"Amazon\")", amazonLinkLocator);
	}

	private IWidgetIdentifier identify(Hyperlink link) {
		return WidgetIdentifier.getInstance().identify(link);
	}
	
	public void testHyperlinkLocatorCodegen() throws Exception {
		assertCodegensJava5("new HyperlinkLocator(\"foo\")", new HyperlinkLocator("foo"));
		assertCodegensJava5("new HyperlinkLocator(\"foo\").inView(\"my.view\")", new HyperlinkLocator("foo").inView("my.view"));
		assertCodegensJava5("new HyperlinkLocator(\"foo\").inSection(\"my.section\")", new HyperlinkLocator("foo").inSection("my.section"));
		assertCodegensJava5("new HyperlinkLocator(\"foo\").inSection(\"my.section\").inView(\"my.view\")", new HyperlinkLocator("foo").inSection("my.section").inView("my.view"));
	}

	public void testHyperlinkConditionCodegen() throws Exception {
		assertCodegensJava5("new HyperlinkLocator(\"foo\").hasHRef(\"ref\")", new HyperlinkLocator("foo").hasHRef("ref"));
		assertCodegensJava5("new HyperlinkLocator(\"foo\").inView(\"my.view\").hasHRef(\"ref\")", new HyperlinkLocator("foo").inView("my.view").hasHRef("ref"));
		assertCodegensJava5("new HyperlinkLocator(\"foo\").inSection(\"my.section\").inView(\"my.view\").hasHRef(\"ref\")", new HyperlinkLocator("foo").inSection("my.section").inView("my.view").hasHRef("ref"));
	}

	

	private void assertCodegensJava5(final String expected, IAdaptable hl) {
		ICodegenParticipant cp = (ICodegenParticipant) hl.getAdapter(ICodegenParticipant.class);
		
		ICodeGenerator cg = new DefaultCodeGenerator() {
			public JavaVersion getJavaVersion() {
				return JAVA5;
			}
		};
		cp.describeTo(cg);
		assertEquals(expected, cg.toCodeString());
	}

	public void testIdentifyHyperlinkSegments() throws Exception {
		final Event[] event = new Event[1];
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() {
				section2FormText.addListener(SWT.MouseDown, new Listener() {	
					public void handleEvent(Event e) {
						event[0] = e;
					}
				});
			}
		});
		IUIContext ui = getUI();
		ui.click(new HyperlinkLocator(GOOGLE_LINK_TEXT));
		ui.wait(new ICondition() {
			public boolean test() {
				return event[0] != null;
			}
		});
		IHyperlinkLocator googleLink = (IHyperlinkLocator) WidgetIdentifier.getInstance().identify(section2FormText, event[0]);
		System.out.println(googleLink);
		assertCodegensJava5("new HyperlinkLocator(\"" + GOOGLE_LINK_TEXT + "\").inSection(\"" + SECTION_2_TITLE +"\")", googleLink);
	}
	
	
	public void testLocatorSerialization() throws Exception {
		serializeOutAndIn(new HyperlinkLocator("foo"));
		serializeOutAndIn(new HyperlinkLocator("foo").withHRef("bar"));
		serializeOutAndIn(new HyperlinkLocator("foo").inSection("baz").inView("zoo"));
	}
	
	
	public void testContributesHREFProperty() throws Exception {
		IPropertyProvider pp = (IPropertyProvider) new HyperlinkLocator(DIGG_LINK_TEXT).getAdapter(IPropertyProvider.class);
		assertNotNull(pp);
		PropertyMapping[] mappings = pp.getProperties(getUI());		
		assertEquals(HyperlinkMatcher.HAS_HREF.getKey(), mappings[0].getKey());
		assertEquals(DIGG_HREF, mappings[0].getValue());
	}
	
	public void testHREFAssertableSurfaced() throws Exception {
		PropertyMapping[] mappings = PropertySet.forLocatorInContext(new HyperlinkLocator(DIGG_LINK_TEXT), getUI()).toArray();
		assertEquals(HyperlinkMatcher.HAS_HREF.getKey(), mappings[0].getKey());
		assertEquals(DIGG_HREF, mappings[0].getValue());
	}
		
	public void testHREFAssertionCodegen() throws Exception {
		CodeBlock block = new NewAPICodeBlockBuilder(CodeGenFixture.builder()).buildAssertion(new HyperlinkLocator(DIGG_LINK_TEXT), HyperlinkMatcher.HAS_HREF.withValue(DIGG_HREF));
		assertEquals("ui.assertThat(new HyperlinkLocator(\"" + DIGG_LINK_TEXT + "\").hasHRef(\"" +  DIGG_HREF + "\"));", block.toString().trim());
	}
	
	public void testEventSequenceLabelForClick() throws Exception {
		assertEquals("Hyperlink: 'foo' clicked", new EventSequenceLabelProvider().getText(fakeSelectEvent(FormText.class, new HyperlinkLocator("foo"))));
		assertEquals("Hyperlink: 'foo' in Section 'section' clicked", new EventSequenceLabelProvider().getText(fakeSelectEvent(FormText.class, new IdentifierAdapter(new HyperlinkLocator("foo").inSection("section")))));
		assertEquals("Hyperlink: 'foo' in Section 'section' in View 'view' clicked", new EventSequenceLabelProvider().getText(fakeSelectEvent(FormText.class, new IdentifierAdapter(new HyperlinkLocator("foo").inSection("section").inView("view")))));
	}
	
	public void testEventSequenceLabelForAssert() throws Exception {
		com.windowtester.ui.internal.corel.model.Event event = fakeAssertEvent(FormText.class, new HyperlinkLocator("foo"), PropertySet.empty().withMapping(HyperlinkMatcher.HAS_HREF.withValue("bar").flag()));
		assertEquals("Asserted Hyperlink: 'foo' hasHRef=bar", new EventSequenceLabelProvider().getText(event));
	}

	public void testAssertionExpertLabel() throws Exception {
		com.windowtester.ui.internal.corel.model.Event event = fakeAssertEvent(FormText.class, new HyperlinkLocator("foo"), PropertySet.empty().withMapping(HyperlinkMatcher.HAS_HREF.withValue("bar").flag()));
		String description = WidgetDescriptionLabelProvider.getDescription(((SemanticWidgetInspectionEvent)event.getUIEvent()));
		assertEquals("Hyperlink", description);
	}
	
	public void testAssertionExpertLabelForDelegate() throws Exception {
		com.windowtester.ui.internal.corel.model.Event event = fakeAssertEvent(FormText.class, new IdentifierAdapter(new HyperlinkLocator("foo").inSection("foo")), PropertySet.empty().withMapping(HyperlinkMatcher.HAS_HREF.withValue("bar").flag()));
		String description = WidgetDescriptionLabelProvider.getDescription(((SemanticWidgetInspectionEvent)event.getUIEvent()));
		assertEquals("Hyperlink", description);
	}
	
	public void testHyperlinkSegmentContextMenu() throws Exception {
		try {
			getUI().contextClick(new HyperlinkLocator(ACME_LINK_TEXT), "Copy");
			fail("Copy menu item is not enabled, so exception should be thrown");
		} catch (WaitTimedOutException e){
			Throwable cause = e.getCause();
			assertTrue(cause.getMessage().startsWith("Waiting for item to become enabled"));
		}
	}
	
	public void testHyperlinkControlContextMenu() throws Exception {
		getUI().contextClick(new HyperlinkLocator(AMAZON_LINK_TEXT), "Foo");
	}
	
	
	
//	public void testDebug() throws Exception {
//		getUI().pause(5000);
//	}
	
	
	
}
