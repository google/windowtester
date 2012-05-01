/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *  Frederic Gurr - added checked condition
 *******************************************************************************/
package com.windowtester.runtime.swt.locator;

import java.awt.Point;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import abbot.tester.swt.TableItemTester;
import abbot.tester.swt.TableTester;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsChecked;
import com.windowtester.runtime.condition.IsCheckedCondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.locator.IModifiable;
import com.windowtester.runtime.swt.internal.selector.TableItemSelector2;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.TableItemReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link TableItem} widgets. Columns can be specified using {@link ColumnLocator}s.
 * @see ColumnLocator
 */
public class TableItemLocator extends SWTWidgetLocator implements IModifiable, IsSelected, IsChecked /* NOTICE: does not implement! implements IItemLocator -- see ClickHelper for why */{
	
	private static final long serialVersionUID = 7952190473575351080L;

	public static final int UNSPECIFIED_COLUMN = -1;
	
	//sentinel
	protected int _column = UNSPECIFIED_COLUMN;
	
	protected int _selectionModifiers = WT.NONE;

	/**
	 * Create a locator instance.
	 * @param text the text of the table item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */		
	public TableItemLocator(String label) {
		super(TableItem.class, label);
	}

	/*default */ 
	public TableItemLocator() {
		super(TableItem.class);
	}
	
	/**
	 * Create a locator instance.
	 * @param selectionMods the selection modifiers (e.g., WT.CHECK)
	 * @param text the text of the table item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public TableItemLocator(int selectionMods, String itemText) {
		super(TableItem.class, itemText);
		setSelectionModifiers(selectionMods);
	}

	/**
	 * Create a locator instance.
	 * @param text the text of the table item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public TableItemLocator(String text, int index, SWTWidgetLocator parent) {
		super(TableItem.class, text, index, parent);
	}

	/**
	 * Create a locator instance.
	 * @param selectionMods the selection modifiers (e.g., WT.CHECK)
	 * @param text the text of the table item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public TableItemLocator(int selectionMods, String text, int index, SWTWidgetLocator parent) {
		super(TableItem.class, text, index, parent);
		setSelectionModifiers(selectionMods);
	}	
	
	/**
	 * Create a locator instance.
	 * @param text the text of the table item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TableItemLocator(String text, SWTWidgetLocator parent) {
		super(TableItem.class, text, parent);
	}

	/**
	 * Create a locator instance.
	 * @param selectionMods the selection modifiers (e.g., WT.CHECK)
	 * @param text the text of the table item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TableItemLocator(int selectionMods, String text, SWTWidgetLocator parent) {
		super(TableItem.class, text, parent);
		setSelectionModifiers(selectionMods);
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		//TableItem item = (TableItem) widget.getWidget();
		
		TableItemReference ref = (TableItemReference)widget;
		
		int clicks = click.clicks();
		int modifiers = click.modifierMask();
		
		modifiers = modifiers | getSelectionModifiers();
		
		Point offset = getXYOffset(ref, click);
		
		preClick(ref, offset, ui);
		doClick(ref, clicks, modifiers, offset);
		postClick(ref, ui);
		
		return ref;
//		return WidgetReference.create(item, this);
	}

	protected void doClick(TableItemReference item, int clicks, int modifiers, Point offset) throws WidgetSearchException {
		new TableItemSelector2().click(clicks, item.getWidget(), getColumn(), convertPoint(offset), modifiers);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getXYOffset(com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	@Override
	public Point getXYOffset(IWidgetReference reference,
			IClickDescription click) {
		if (unspecifiedXY(click)) {
			return null; //sentinel
		}
		return new Point(click.x(), click.y());
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, final IWidgetReference widget, final IClickDescription click, String menuItemPath) throws WidgetSearchException {
//		TableItem item = (TableItem) widget.getWidget();
//		Point offset = getXYOffset(item, click);
//		preClick(item, offset, ui);
//		Widget clicked = new TableItemSelector2().contextClick(item, getColumn(), convertPoint(offset), menuItemPath);
//		postClick(clicked, ui);
		
		MenuItemReference clicked = new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return ((TableItemReference) widget).showContextMenu(click);
			}
		}, menuItemPath);
		return WidgetReference.create(clicked, this);
	}

	protected org.eclipse.swt.graphics.Point convertPoint(Point offset) {
		if (offset == null)
			return null;
		return new org.eclipse.swt.graphics.Point(offset.x, offset.y);
	}
		
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.IModifiable#setSelectionModifiers(int)
	 */
	public void setSelectionModifiers(int modifiers) {
		_selectionModifiers = modifiers;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.IModifiable#getSelectionModifiers()
	 */
	public int getSelectionModifiers() {
		return _selectionModifiers;
	}
	
	
	public int getColumn() {
		return _column;
	}
	
	void /* default */ setColumn(int column) {
		_column = column;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		TableItem item = (TableItem) ((IWidgetReference)ui.find(this)).getWidget();
		Table table = UIProxy.getParent(item);
		return new TableTester().isSelected(table, item);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsChecked#isChecked(com.windowtester.runtime.IUIContext)
	 */
	public boolean isChecked(IUIContext ui) throws WidgetSearchException {
		TableItem item = (TableItem) ((IWidgetReference) ui.find(this)).getWidget();
		return new TableItemTester().getChecked(item);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given table item is selected.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isSelected() {
		return isSelected(true);
	}
	
	/**
	 * Create a condition that tests if the given table item is selected.
	 * @param selected 
	 * @param expected <code>true</code> if the table item is expected to be selected, else
	 *            <code>false</code>
	 */            
	public IUICondition isSelected(boolean expected) {
		return new IsSelectedCondition(this, expected);
	}

	/**
	 * Create a condition that tests if the given table item is checked.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isChecked(true)</code>
	 */
	public IUICondition isChecked() {
		return isChecked(true);
	}
	
	/**
	 * Create a condition that tests if the given table item is checked.
	 * @param expected <code>true</code> if the table item is expected to be checked, else
	 *            <code>false</code>
	 */            
	public IUICondition isChecked(boolean expected) {
		return new IsCheckedCondition(this, expected);
	}
}
