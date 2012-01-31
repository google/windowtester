package com.windowtester.test.recorder.ui;

import static com.windowtester.test.codegen.CodeGenFixture.fakeCloseEvent;
import static com.windowtester.test.codegen.CodeGenFixture.fakeSelectEvent;
import static com.windowtester.test.codegen.CodeGenFixture.mockAssert;

import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.JTree;

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.JComboBoxLocator;
import com.windowtester.runtime.swing.locator.JListLocator;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.JTreeItemLocator;
import com.windowtester.runtime.swt.locator.jface.WizardPageLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.swt.event.recorder.jface.WizardProperty;
import com.windowtester.ui.internal.corel.model.Event;
import com.windowtester.ui.internal.corel.model.EventSequenceLabelProvider;

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
public class EventSequenceLabelProviderSmokeTest extends TestCase {

	private EventSequenceLabelProvider labelProvider = new EventSequenceLabelProvider();
	
	public void testBasicTreeClick() throws Exception {
		assertEquals("Tree Item: 'foo/bar' clicked", getText(fakeSelectEvent(TreeItem.class, new TreeItemLocator("foo/bar"))));	
	}
	
	public void testBasicButtonClick() throws Exception {
		assertEquals("Button: 'foo' clicked", getText(fakeSelectEvent(Button.class, new ButtonLocator("foo"))));	
	}
	
	public void testBasicComboClick() throws Exception {
		assertEquals("Combo Item: 'foo' clicked", getText(fakeSelectEvent(Combo.class, new ComboItemLocator("foo"))));	
	}
	
	public void testBasicMenuClick() throws Exception {
		assertEquals("Menu Item: 'foo/bar' clicked", getText(fakeSelectEvent(MenuItem.class, new MenuItemLocator("foo/bar"))));	
	}
	
	public void testCTabItemClose() throws Exception {
		assertEquals("CTabItem: 'Blah' closed", getText(fakeCloseEvent(CTabItem.class, new CTabItemLocator("Blah"))));	
	}
	
	public void testBasicButtonAssertion() throws Exception {
		assertEquals("Asserted Button: 'foo' isVisible=true", getText(new Event(mockAssert(Button.class, new ButtonLocator("foo")).withProperties(PropertySet.empty().withMapping(PropertyMapping.VISIBLE.withValue(true).flag())))));	
	}	
	
	public void testBasicTreeItemAssertion() throws Exception {
		assertEquals("Asserted Tree Item: 'foo/bar' isVisible=true", getText(new Event(mockAssert(Button.class, new TreeItemLocator("foo/bar")).withProperties(PropertySet.empty().withMapping(PropertyMapping.VISIBLE.withValue(true).flag())))));	
	}
	
	public void testWizardPageAssertion() throws Exception {
		assertEquals("Asserted Wizard Page: hasTitle=Title", getText(new Event(mockAssert(Dialog.class, new IdentifierAdapter(new WizardPageLocator())).withProperties(PropertySet.empty().withMapping(WizardProperty.HAS_TITLE.withValue("Title").flag())))));	
	}
	
	private String getText(Event e) {
		return labelProvider.getText(e);
	}

	// for Swing
	public void testBasicJTreeClick() throws Exception {
		assertEquals("Tree Item: 'foo/bar' clicked", getText(fakeSelectEvent(JTree.class, new JTreeItemLocator("foo/bar"))));
	}
	
	public void testBasicJTableClick() throws Exception {
		assertEquals("Table Item: row 1, column 1 clicked", getText(fakeSelectEvent(JTable.class, new JTableItemLocator(new Point(1,1)))));
	}
	
	public void testBasicJButtonClick() throws Exception {
		assertEquals("Button: 'foo' clicked", getText(fakeSelectEvent(JButton.class, new JButtonLocator("foo"))));	
	}
	
	public void testBasicJComboBoxClick() throws Exception {
		assertEquals("Combo Item: 'foo' clicked", getText(fakeSelectEvent(JComboBox.class, new JComboBoxLocator("foo"))));	
	}
	
	public void testBasicJListClick() throws Exception {
		assertEquals("List Item: 'foo' clicked", getText(fakeSelectEvent(JList.class, new JListLocator("foo"))));	
	}
	
	public void testBasicJMenuItemClick() throws Exception {
		assertEquals("Menu Item: 'foo/bar' clicked", getText(fakeSelectEvent(JMenuItem.class, new JMenuItemLocator("foo/bar"))));	
	}
	
	
}
