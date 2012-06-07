package com.windowtester.test.locator.swt;

import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS_2;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS_2_REGEXP;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS_CHILD_2;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS_ESCAPED;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS_ESCAPED_2;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.ITEM_LABEL_WITH_DELIMS_REGEXP;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.PATH_WITH_DELIMS;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.PATH_WITH_DELIMS_ESCAPED;
import static com.windowtester.test.locator.swt.shells.TreeTestShell.SUB_MENU_ITEM;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.tester.swt.TreeItemTester;
import abbot.tester.swt.TreeTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.test.locator.swt.shells.TreeTestShell;
import com.windowtester.test.util.junit.OS;
import com.windowtester.test.util.junit.RunOn;


/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *  Frederic Gurr - added tests for isSelected and isChecked condition
 *******************************************************************************/

public class TreeItemLocatorTest extends AbstractLocatorTest {

	
	public class ContextMenuSelectionCondition implements ICondition {
	
		private final String expectedText;

		public ContextMenuSelectionCondition(String expectedText) {
			this.expectedText = expectedText;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.condition.ICondition#test()
		 */
		public boolean test() {
			return expectedText.equals(window.getSelectedMenuText());
		}
		
	}
	
	
	TreeTestShell window;
	
	 
	@Override
	public void uiSetup() {
		window = new TreeTestShell();
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public TreeTestShell getWindow() {
		return window;
	}
	
	
	public void testTreeSelections() throws WidgetSearchException  {
		
		IUIContext ui = getUI();
				
		ui.click(new TreeItemLocator("TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0", new WidgetReference(getWindow().tree)));
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("TreeItem (2) -0", new TreeItemTester().getText(items[0]));
		
		ui.click(new TreeItemLocator("TreeItem (0) -1/TreeItem (1) -1/TreeItem (2) -1", new WidgetReference(getWindow().tree)));
		items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("TreeItem (2) -1", new TreeItemTester().getText(items[0]));

	}

	public void testTreeSelectionWithRegexps() throws Exception {
		
		IUIContext ui = getUI();
		
		ui.click(new TreeItemLocator(ITEM_LABEL_WITH_DELIMS_REGEXP, new WidgetReference(getWindow().tree)));
		
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals(ITEM_LABEL_WITH_DELIMS, new TreeItemTester().getText(items[0]));
		
		
		ui.click(new TreeItemLocator(ITEM_LABEL_WITH_DELIMS_REGEXP + "/" + ITEM_LABEL_WITH_DELIMS_2_REGEXP, new WidgetReference(getWindow().tree)));
		
		items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals(ITEM_LABEL_WITH_DELIMS_2, new TreeItemTester().getText(items[0]));
		
	}
	
	public void testTreeSelectionFailure() {
		try {
			clickTree(getWindow().tree, "TreeItem (0) -0/TreeItem (1) -0/BOGUS");
			fail();
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			//pass
		} catch (WidgetSearchException e) {
			fail();
		}
	}
	
	//convenience helper
	private TreeItemReference clickTreeItem(IUIContext ui, int clicks, String path, Tree tree, int mods) throws WidgetSearchException {
		return (TreeItemReference) ui.click(clicks, new TreeItemLocator(path, new WidgetReference(tree)), mods);
	}

	public void testTreeShiftSelections() throws WidgetSearchException {
		//fail("unimplemented");
		
		
		clickTree(getWindow().tree, "TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0");
		clickTree(getWindow().tree, 1, "TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -1", SWT.BUTTON1 | SWT.SHIFT);
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		
		assertEquals(2, items.length);
		
		//fail("need to confirm items match too");
	}
	

	
	
	
	private TreeItemReference clickTree(Tree tree, int clicks, String path, int mods) throws WidgetSearchException {
		return clickTreeItem(getUI(), clicks, path, tree, mods);
	}

	private void clickTree(Tree tree, String path) throws WidgetSearchException {
		clickTree(tree, 1, path, SWT.BUTTON1);
	}

//	public void testLazyTreeSelections() throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		
//		fail("unimplemented");
//		
//		ui.click(window.lazyTree, "root 1/node 1");
//		
//		//fail("lazy selections fail");
//		//this will hang playback
//		ui.click(window.lazyTree, "root 2/node 0/node 1");
//	}
	

	@SuppressWarnings("unchecked")
	public void testCheckTreeSelections() throws WidgetSearchException {
		
		
		//ui.pause(2000);
		TreeItemReference checkedItem = clickTree(getWindow().checkTree, 1, "parent 1", SWT.BUTTON1 | SWT.CHECK);
		TreeItem[] items = new TreeTester().getSelection(getWindow().checkTree);
		// Check is not select, so should not be any items selected
		assertEquals(0, items.length);
		assertTrue(new TreeItemTester().getChecked(checkedItem.getWidget()));

		//uncheck
		clickTree(getWindow().checkTree, 1, "parent 1", SWT.BUTTON1 | SWT.CHECK);
		items = new TreeTester().getSelection(getWindow().checkTree);

		// Check is not select, so should not be any items selected
		assertEquals(0, items.length);
		assertFalse(new TreeItemTester().getChecked(checkedItem.getWidget()));
		
		//check again, using the locator with check modifier constructor
		
		IWidgetReference ref = (IWidgetReference) getUI().click(new TreeItemLocator(WT.CHECK, "parent 1", new WidgetReference(getWindow().checkTree)));
		assertTrue(new TreeItemTester().getChecked((TreeItem) ref.getWidget()));
		
	}
	
	
	public void testCheckTreeSelectionsWithEscapes()
			throws WidgetSearchException {
		TreeItem[] items;
		//check another (NOTICE we are using escaped delims!!!)
		TreeItemReference checkedItem = clickTree(getWindow().checkTree, 1, "parent 2/child\\/0", SWT.BUTTON1 | SWT.CHECK);
		items = new TreeTester().getSelection(getWindow().checkTree);
		
		// On non-Linux OSes, Check is not select, so should not be any items selected
		int numSelects = com.windowtester.runtime.internal.OS.isLinux() ? 1 : 0;
		assertEquals(numSelects, items.length);
		
		assertEquals("child/0", new TreeItemTester().getText(checkedItem.getWidget()));
		assertTrue(new TreeItemTester().getChecked(checkedItem.getWidget()));
	}
	
	
	public void testContextMenuSelection() throws WidgetSearchException {
		window.clearSelectedMenuText();
		final String menuText = "TreeItem (0) -0";
		contextClick(getWindow().tree, menuText, menuText);
		wait(new ContextMenuSelectionCondition(menuText));
	}
	
	private Widget contextClick(Tree tree, String itemPath, String menuPath) throws WidgetSearchException {
		IWidgetReference ref = (IWidgetReference)getUI().contextClick(new TreeItemLocator(itemPath, new WidgetReference(tree)), new MenuItemLocator(menuPath));
		return (Widget)ref.getWidget();
	}

	public void testContextClickBadMenuFailure() throws Exception {
		window.clearSelectedMenuText();
		try {
			contextClick(getWindow().tree, "TreeItem (0) -0", "BOGUS");
			fail();
		} catch(WidgetNotFoundException e) {
			//pass
			assertNull(window.getSelectedMenuText());
		}
	}
	
	public void testContextClickBadItemFailure() throws Exception {
		window.clearSelectedMenuText();
		try {
			contextClick(getWindow().tree, "BOGUS", "BOGUS");
			fail();
		} catch(WidgetNotFoundException e) {
			//pass
			assertNull(window.getSelectedMenuText());
		}
	}
	
	public void testContextMenuWithReveal() throws Exception {
		window.clearSelectedMenuText();
		String menuText = "TreeItem (1) -0";
		contextClick(getWindow().tree, "TreeItem (0) -2/TreeItem (1) -0", menuText);
		wait(new ContextMenuSelectionCondition(menuText));
	}
	
	//https://fogbugz.instantiations.com/fogbugz/default.asp?45900
	public void testReferenceContextMenuWithScrollingReveal() throws Exception {
		
		new TreeTester().setSize(getWindow().tree, 150, 50);
		
		window.clearSelectedMenuText();
		String menuText = "TreeItem (0) -2";
		
		IUIContext ui = getUI();
		
		IWidgetLocator[] found = ui.findAll(new TreeItemLocator("TreeItem (0) -2"));
		ui.contextClick(found[0], menuText);
		wait(new ContextMenuSelectionCondition(menuText));
	}
	
	//https://fogbugz.instantiations.com/fogbugz/default.asp?45900
	public void testReferenceClickWithScrollingReveal() throws Exception {
		
		new TreeTester().setSize(getWindow().tree, 150, 50);
		
		IUIContext ui = getUI();
		
		IWidgetLocator[] found = ui.findAll(new TreeItemLocator("TreeItem (0) -3"));
		ui.click(found[0]);
		
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals("TreeItem (0) -3", new TreeItemTester().getText(items[0]));	
	}
	
	
	
	
	//http://developer.instantiations.com/fogbugz/default.php?9429
	public void testContextWithEscapedPathText() throws Exception {	
		//1
		window.clearSelectedMenuText();
		contextClick(getWindow().checkTree, "parent 0", PATH_WITH_DELIMS_ESCAPED);
		wait(new ContextMenuSelectionCondition(PATH_WITH_DELIMS));
		// Give the system lots of time to settle... this fixes this test when run as part
		// of Linux WTRuntimeScenario2
//		getUI().pause(10000);
//		new WaitForIdle2().waitForIdle();
//		new WaitForIdle2().waitForIdle();
//		getUI().pause(200);
//		ScreenCapture.createScreenCapture();
		//2 -submenu
		window.clearSelectedMenuText();
		contextClick(getWindow().checkTree, "parent 0", SUB_MENU_ITEM + "/" + PATH_WITH_DELIMS_ESCAPED);
		wait(new ContextMenuSelectionCondition(SUB_MENU_ITEM + "/" + PATH_WITH_DELIMS));
	}

	public void testItemWithEscapedPathText() throws Exception {
		
		
		clickTree(getWindow().tree, ITEM_LABEL_WITH_DELIMS_ESCAPED);
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals(ITEM_LABEL_WITH_DELIMS, new TreeItemTester().getText(items[0]));
		
		
		clickTree(getWindow().tree, ITEM_LABEL_WITH_DELIMS_ESCAPED+ '/' + ITEM_LABEL_WITH_DELIMS_ESCAPED_2);	
		items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals(ITEM_LABEL_WITH_DELIMS_2, new TreeItemTester().getText(items[0]));	
	

		clickTree(getWindow().tree, ITEM_LABEL_WITH_DELIMS_ESCAPED+ '/' + ITEM_LABEL_WITH_DELIMS_CHILD_2);	
		items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals(ITEM_LABEL_WITH_DELIMS_CHILD_2, new TreeItemTester().getText(items[0]));	
	
	}

	//http://fogbugz.instantiations.com//default.php?13961
	public void testItemWithEscapedPathText2() throws Exception {
		
		
		
		//add some new nodes
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				TreeItem escaped = new TreeItem(getWindow().tree, 0);
				escaped.setText("ProjectName [svn/annotation]");
				new TreeItem(escaped, 0).setText("src");
			}
		});
		

		
		clickTree(getWindow().tree, "ProjectName [svn\\/annotation]/src");	
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("src", new TreeItemTester().getText(items[0]));	
		
		
	}
	
	/**
	 * Pre-W2, <code>ui.contextClick(new TreeItemLocator(""), "path/to/item")</code> was valid on an empty tree.
	 * In W2, this is no longer supported; this should be done instead:
	 * <code>ui.contextClick(new TreeLocator(), "path/to/item")</code>
	 */
	public void testContextMenuWithoutParent() throws Exception {
		window.clearSelectedMenuText();

		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				getWindow().emptyTree.setData("name", "empty.tree");
			}
		});
		
		IWidgetReference ref = (IWidgetReference) getUI().contextClick(new NamedWidgetLocator("empty.tree"), "item2/subitem1");
		Widget widget = (Widget) ref.getWidget();
		
		assertTrue(widget instanceof MenuItem);
		assertEquals("subitem1", window.getSelectedMenuText());
		
		//widget = ui.contextClick(window.emptyTree, "item2/subitem1");
		
		IWidgetReference item = (IWidgetReference)getUI().contextClick(new WidgetReference(getWindow().emptyTree), new MenuItemLocator("item2/subitem1"));
		
		assertEquals("MenuItem {subitem1}", UIProxy.getToString((Widget)item.getWidget()));
	}

	public void testContextMenuSelectionClippedCase() throws WidgetSearchException {
		new TreeTester().setSize(getWindow().tree, 50, 100);
		
		//using paths here
		window.clearSelectedMenuText();
		contextClick(getWindow().tree, "TreeItem (0) -0", "TreeItem (0) -0");
		wait(new ContextMenuSelectionCondition("TreeItem (0) -0"));
	}

	@RunOn(OS.WIN)
	public void testTreeSelectionClippedCase() throws WidgetSearchException {

		if (Platform.isOSX())
			fail("Mac skip: Tree.showItem() does not scroll horizontally");
		
		new TreeTester().setSize(getWindow().tree, 50, 150);

		clickTree(getWindow().tree, "TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0");
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("TreeItem (2) -0", new TreeItemTester().getText(items[0]));
		
		clickTree(getWindow().tree, "TreeItem (0) -3/TreeItem (1) -2/TreeItem (2) -2");
		items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("TreeItem (2) -2", new TreeItemTester().getText(items[0]));
	}

	@RunOn(OS.WIN)
	public void testTreeExpandClippedCase() throws WidgetSearchException {

		if (Platform.isOSX())
			fail("Mac skip: Tree.showItem() does not scroll horizontally");
		
		new TreeTester().setSize(getWindow().tree, 50, 150);

		clickTree(getWindow().tree, 2, "TreeItem (0) -0/TreeItem (1) -0", SWT.BUTTON1);
		TreeItem[] items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("TreeItem (1) -0", new TreeItemTester().getText(items[0]));
		// [Dan] parent tree item is expanded but not selected, tree item is selected but not expanded
//		if (SWT.getPlatform().equals("win32"))
//			assertTrue(new TreeItemTester().getExpanded(items[0]));
		
		clickTree(getWindow().tree, 2, "TreeItem (0) -3/TreeItem (1) -2/TreeItem (2) -2", SWT.BUTTON1);
		items = new TreeTester().getSelection(getWindow().tree);
		assertEquals(1, items.length);
		assertEquals("TreeItem (2) -2", new TreeItemTester().getText(items[0]));
		// [Dan] parent tree item is expanded but not selected, tree item is selected but not expanded
//		if (SWT.getPlatform().equals("win32"))
//			assertTrue(new TreeItemTester().getExpanded(items[0]));
	}
	
	public void testTreeItemSelection() throws Exception {
		IUIContext ui = getUI();
		
		WidgetReference treeWidgetReference = new WidgetReference(getWindow().tree);
		
		ui.click(new TreeItemLocator("TreeItem (0) -0", treeWidgetReference));
		assertTrue(new TreeItemLocator("TreeItem (0) -0").isSelected(ui));
		ui.assertThat(new TreeItemLocator("TreeItem (0) -0").isSelected());
		
		ui.click(new TreeItemLocator("TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0", treeWidgetReference));
		assertTrue(new TreeItemLocator("TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0").isSelected(ui));
		ui.assertThat(new TreeItemLocator("TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0").isSelected());
		
		assertFalse(new TreeItemLocator("TreeItem (0) -0").isSelected(ui));
		ui.assertThat(new TreeItemLocator("TreeItem (0) -0").isSelected(false));	
	}
	
	public void testTreeItemIsChecked() throws Exception {
		IUIContext ui = getUI();
		
		WidgetReference checkTreeWidgetReference = new WidgetReference(getWindow().checkTree);
		ui.click(1, new TreeItemLocator("parent 0", checkTreeWidgetReference), WT.CHECK);
		
		assertTrue(new TreeItemLocator("parent 0").isChecked(ui));
		ui.assertThat(new TreeItemLocator("parent 0").isChecked());
		
		ui.click(1, new TreeItemLocator("parent 1/child\\/1", checkTreeWidgetReference), WT.CHECK);
		assertTrue(new TreeItemLocator("parent 1/child\\\\/1").isChecked(ui));
		ui.assertThat(new TreeItemLocator("parent 1/child\\\\/1").isChecked());

		assertFalse(new TreeItemLocator("parent 2").isChecked(ui));
		ui.assertThat(new TreeItemLocator("parent 2").isChecked(false));	
	}

	
}
