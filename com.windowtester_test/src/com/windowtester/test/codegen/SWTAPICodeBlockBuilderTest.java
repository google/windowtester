package com.windowtester.test.codegen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormText;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.jface.WizardPageLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.ColumnLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.StyledTextLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TextLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.swt.event.recorder.jface.WizardProperty;
import com.windowtester.test.locator.swt.forms.HyperlinkLocatorTest;

/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *  Frederic Gurr - added tests for isEnabled, isSelected and isChecked condition
 *******************************************************************************/
public class SWTAPICodeBlockBuilderTest extends BaseSWTAPICodeBlockCodegenTest {

	public void testAddClass() throws Exception {
		
		
	}
	
	public void testAssertionHook() throws Exception {
		String method = "test_assertion";
    	MethodUnit assertMethod = new MethodUnit(method);
    	assertMethod.addModifier(Modifier.PROTECTED);
    	assertMethod.addThrows("Exception");
    	CodeBlock block = getBuilder().buildMethodInvocation(method);
		assertEquals("test_assertion();", block);
	}

	/**
	 * @see HyperlinkLocatorTest for more...
	 */
	public void testHyperlinkSelect() throws Exception {
		//TODO: this cast sucks...
		EventInfo info = CodeGenFixture.mockEvent(FormText.class, (WidgetLocator)new HyperlinkLocator("link").inSection("Foo"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new HyperlinkLocator(\"link\").inSection(\"Foo\"));", block);
		assertImportsContain(HyperlinkLocator.class);
	}
	
	public void testBasicSelect() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new TableItemLocator(\"item\"));", block);
	}

	public void testContribToolItemSelect() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(ToolItem.class, new ContributedToolItemLocator("actionId"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new ContributedToolItemLocator(\"actionId\"));", block);
	}

	public void testPullDownItemSelect() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(MenuItem.class, new PullDownMenuItemLocator("path/to/item", new ContributedToolItemLocator("actionId")));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new PullDownMenuItemLocator(\"path/to/item\", new ContributedToolItemLocator(\"actionId\")));", block);	
	}
	

	public void testBasicContextClick() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		info.button = 3; //CClick
		SemanticMenuSelectionEvent select = new SemanticMenuSelectionEvent(info);
		select.setPath("foo/bar");
		CodeBlock block = getBuilder().buildMenuSelect(select);
		assertEquals("ui.contextClick(new TableItemLocator(\"item\"), \"foo/bar\");", block);
		assertImportsContain(TableItemLocator.class);
	}	
	
	public void testBasicContextClick2() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item", new ViewLocator("my.view")));
		info.button = 3; //CClick
		SemanticMenuSelectionEvent select = new SemanticMenuSelectionEvent(info);
		select.setPath("foo/bar");
		CodeBlock block = getBuilder().buildMenuSelect(select);
		assertEquals("ui.contextClick(new TableItemLocator(\"item\", new ViewLocator(\"my.view\")), \"foo/bar\");", block);
		assertImportsContain(TableItemLocator.class);
		assertImportsContain(ViewLocator.class);
	}
	
	public void testShiftMod() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setMask("SWT.BUTTON1 | SWT.SHIFT"); //ugh legacy!
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(1, new TableItemLocator(\"item\"), WT.SHIFT);", block);
		assertImportsContain(WT.class);
	}
	
	public void testCTRLMod() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setMask("SWT.BUTTON1 | SWT.CTRL"); //ugh legacy!
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(1, new TableItemLocator(\"item\"), WT.CTRL);", block);
		assertImportsContain(WT.class);
	}
	
	public void testTreeItemCTRLMod() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TreeItem.class, new TreeItemLocator("item"));
		SemanticTreeItemSelectionEvent select  = new SemanticTreeItemSelectionEvent(info, TreeEventType.SINGLE_CLICK);
		select.setMask("SWT.BUTTON1 | SWT.CTRL"); //ugh legacy!
		CodeBlock block = getBuilder().buildTreeSelect(select);
		assertEquals("ui.click(1, new TreeItemLocator(\"item\"), WT.CTRL);", block);
		assertImportsContain(WT.class);
	}
	
	public void testTreeItemShiftMod() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TreeItem.class, new TreeItemLocator("item"));
		SemanticTreeItemSelectionEvent select  = new SemanticTreeItemSelectionEvent(info, TreeEventType.SINGLE_CLICK);
		select.setMask("SWT.BUTTON1 | SWT.SHIFT"); //ugh legacy!
		CodeBlock block = getBuilder().buildTreeSelect(select);
		assertEquals("ui.click(1, new TreeItemLocator(\"item\"), WT.SHIFT);", block);
		assertImportsContain(WT.class);
	}
	
	public void testTreeItemCheck() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TreeItem.class, new TreeItemLocator("item"));
		SemanticTreeItemSelectionEvent select  = new SemanticTreeItemSelectionEvent(info, TreeEventType.SINGLE_CLICK);
		select.setChecked(true);
		CodeBlock block = getBuilder().buildTreeSelect(select);
		assertEquals("ui.click(new TreeItemLocator(WT.CHECK, \"item\"));", block);
		assertImportsContain(WT.class);
	}
	
	//should only have locator (not parent too!)
	public void testGenericTreeItemImports() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TreeItem.class, new TreeItemLocator("item"));
		SemanticTreeItemSelectionEvent select  = new SemanticTreeItemSelectionEvent(info, TreeEventType.SINGLE_CLICK);
		CodeBlock block = getBuilder().buildTreeSelect(select);
		assertEquals("ui.click(new TreeItemLocator(\"item\"));", block);
		assertImportsContain(TreeItemLocator.class);
		assertImportsDoNotContain(SWTWidgetLocator.class);
	}
	
	public void testTableItemCheck() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setChecked(true);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new TableItemLocator(WT.CHECK, \"item\"));", block);
		assertImportsContain(WT.class);
	}
	
	
	
	public void testTableItemColumnSelect() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setIndex(3);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new ColumnLocator(3, new TableItemLocator(\"item\")));", block);
		assertImportsContain(ColumnLocator.class);	
		assertImportsContain(TableItemLocator.class);	
	}
	
	public void testTableItemDoubleClick() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setClicks(2);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(2, new TableItemLocator(\"item\"));", block);
		assertImportsContain(TableItemLocator.class);	
	}
	
	public void testTableItemSingleClick() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TableItem.class, new TableItemLocator("item"));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setClicks(1);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new TableItemLocator(\"item\"));", block);
		assertImportsContain(TableItemLocator.class);	
	}
	
	
	public void testGenericWidgetSelect() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(Canvas.class, new SWTWidgetLocator(Canvas.class));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new SWTWidgetLocator(Canvas.class));", block);
		assertImportsContain(Canvas.class);
	}
	
	public void testTreeItemWithParentTree() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TreeItem.class, new TreeItemLocator("item", new SWTWidgetLocator(Tree.class, new SWTWidgetLocator(Composite.class))));
		SemanticTreeItemSelectionEvent select  = new SemanticTreeItemSelectionEvent(info, TreeEventType.SINGLE_CLICK);
		CodeBlock block = getBuilder().buildTreeSelect(select);
		assertEquals("ui.click(new TreeItemLocator(\"item\", new SWTWidgetLocator(Tree.class, new SWTWidgetLocator(Composite.class))));", block);
		assertImportsContain(Tree.class);
		assertImportsContain(Composite.class);
	}
	
	public void testFilteredTreeItemWithoutParentTree() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(TreeItem.class, new FilteredTreeItemLocator("item"));
		SemanticTreeItemSelectionEvent select  = new SemanticTreeItemSelectionEvent(info, TreeEventType.SINGLE_CLICK);
		CodeBlock block = getBuilder().buildTreeSelect(select);
		assertEquals("ui.click(new FilteredTreeItemLocator(\"item\"));", block);
		assertImportsContain(FilteredTreeItemLocator.class);
		assertImportsDoNotContain(FilteredTreeLocator.class);
	}
	
	public void testStyledTextSingleClick() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(StyledText.class, new StyledTextLocator());
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setClicks(1);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new StyledTextLocator());", block);
		assertImportsContain(StyledTextLocator.class);
	}

	public void testStyledTextSingleClick2() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(StyledText.class, new StyledTextLocator(new SWTWidgetLocator(Composite.class)));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setClicks(1);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new StyledTextLocator(new SWTWidgetLocator(Composite.class)));", block);
		assertImportsContain(StyledTextLocator.class);
	}

	public void testStyledTextSingleClick3() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(StyledText.class, new StyledTextLocator(1, new SWTWidgetLocator(Composite.class)));
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setClicks(1);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new StyledTextLocator(1, new SWTWidgetLocator(Composite.class)));", block);
		assertImportsContain(StyledTextLocator.class);
	}
	
	
	public void testGenericXYWidgetSelect() throws Exception {
		EventInfo info = CodeGenFixture.mockEvent(Canvas.class, new SWTWidgetLocator(Canvas.class));
		info.x = 5;
		info.y = 7;
		SemanticWidgetSelectionEvent select = new SemanticWidgetSelectionEvent(info);
		select.setRequiresLocationInfo(true);
		CodeBlock block = getBuilder().buildSelect(select);
		assertEquals("ui.click(new XYLocator(new SWTWidgetLocator(Canvas.class), 5, 7));", block);
		assertImportsContain(Canvas.class);
		assertImportsContain(XYLocator.class);
	}

	public void testSWTCtrlKeyEntry2() throws Exception {
		CodeBlock block = getBuilder().buildKeyEntry("SWT.CTRL", "A");
		assertEquals("ui.keyClick(WT.CTRL, \'A\');", block);
		assertImportsContain(WT.class);
	}
	
	public void testSWTCtrlKeyEntry() throws Exception {
		CodeBlock block = getBuilder().buildKeyEntry("SWT.TAB");
		assertEquals("ui.keyClick(WT.TAB);", block);
		assertImportsContain(WT.class);
		assertImportsDoNotContain(SWT.class);
	}
	
	public void testWTCtrlKeyEntry() throws Exception {
		CodeBlock block = getBuilder().buildKeyEntry("WT.ALT", "S");
		assertEquals("ui.keyClick(WT.ALT, \'S\');", block);
		assertImportsContain(WT.class);
	}
	
	public void testHasDescriptionAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new WizardPageLocator(), WizardProperty.HAS_DESCRIPTION.withValue("Foo"));
		assertEquals("ui.assertThat(new WizardPageLocator().hasDescription(\"Foo\"));", block);
	}
	
	public void testIsVisibleAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new ButtonLocator("OK"), PropertyMapping.VISIBLE.withValue(true));
		assertEquals("ui.assertThat(new ButtonLocator(\"OK\").isVisible());", block);
	}
	
	public void testIsEnabledAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new ButtonLocator("OK"), PropertyMapping.ENABLED.withValue(true));
		assertEquals("ui.assertThat(new ButtonLocator(\"OK\").isEnabled());", block);
	}
	
	public void testIsEnabledFalseAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new ButtonLocator("OK"), PropertyMapping.ENABLED.withValue(false));
		assertEquals("ui.assertThat(new ButtonLocator(\"OK\").isEnabled(false));", block);
	}
	
	public void testIsSelectedAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new TreeItemLocator("foo"), PropertyMapping.SELECTED.withValue(true));
		assertEquals("ui.assertThat(new TreeItemLocator(\"foo\").isSelected());", block);
	}

	public void testIsSelectedFalseAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new TreeItemLocator("foo"), PropertyMapping.SELECTED.withValue(false));
		assertEquals("ui.assertThat(new TreeItemLocator(\"foo\").isSelected(false));", block);
	}
	
	public void testIsCheckedAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new TreeItemLocator("foo"), PropertyMapping.CHECKED.withValue(true));
		assertEquals("ui.assertThat(new TreeItemLocator(\"foo\").isChecked());", block);
	}

	public void testIsCheckedFalseAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new TableItemLocator("foo"), PropertyMapping.CHECKED.withValue(false));
		assertEquals("ui.assertThat(new TableItemLocator(\"foo\").isChecked(false));", block);
	}
	
	public void testPerspectiveActiveAssertion() throws Exception {
		/*
		 * NOTE this test must be run as a PDE test.  This is because PerspectiveLocators
		 * aggressively resolve ids into descriptors.
		 */
		final String id = "org.eclipse.debug.ui.DebugPerspective";
		PerspectiveLocator perspective = PerspectiveLocator.forId(id);
		CodeBlock block = getBuilder().buildAssertion(perspective, PropertyMapping.ACTIVE.withValue(true));
		assertEquals("ui.assertThat(new PerspectiveLocator(\"" + id + "\").isActive());", block);
	}
	
	public void testHasTextAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new TextLocator(), PropertyMapping.TEXT.withValue("foo"));
		assertEquals("ui.assertThat(new TextLocator().hasText(\"foo\"));", block);
	}
	
	public void testHasFocusTrueAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new ButtonLocator("OK"), PropertyMapping.FOCUS.withValue(true));
		assertEquals("ui.assertThat(new ButtonLocator(\"OK\").hasFocus());", block);
	}

	public void testHasFocusFalseAssertion() throws Exception {
		CodeBlock block = getBuilder().buildAssertion(new ButtonLocator("OK"), PropertyMapping.FOCUS.withValue(false));
		assertEquals("ui.assertThat(new ButtonLocator(\"OK\").hasFocus(false));", block);
	}

	
	
}
