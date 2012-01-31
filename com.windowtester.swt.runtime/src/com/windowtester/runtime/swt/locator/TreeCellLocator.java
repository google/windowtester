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
package com.windowtester.runtime.swt.locator;

import java.awt.Point;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.selector.TreeCellSelector;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.util.StringComparator;

/**
 * 
 * Locates {@link Tree} cells.
 * <p>
 * Example use:
 * <p>
 * <code>
 * ui.click(new TreeCellLocator("Project/resource").in(new ViewLocator("project.view")));
 * </code>
 * <p/>
 * 
 * selects the "Project/resource" node in the view with the id "project.view".
 * 
 */
public class TreeCellLocator extends TreeItemLocator implements HasText {

	
	//TODO: context click column awareness
	
	private static final long serialVersionUID = 400564213270539073L;
	
	/**
	 * A locator for columns with a tree cell.
	 * <p>
	 * Note: direct reference to this class is discouraged.  Prefer using
	 * {@link SWTLocators#column(int)}.
	 */
	public static class Column implements ILocator {
		private final int column;

		public Column(int column) {
			this.column = column;
		}		
		public int getIndex() {
			return column;
		}
	}
	
	private int columnIndex = -1;
	
	public TreeCellLocator(String fullPath) {
		super(fullPath);
	}

	//used for cloning
	private TreeCellLocator(int modifiers, String fullPath, int index, IWidgetLocator parentLocator) {
		super(modifiers, fullPath, index, parentLocator);
	}
	
	
//NOTE: in defined in super	
//	public TreeCellLocator in(SWTWidgetLocator parent) {
//		TreeCellLocator locator = doClone();
//		locator.setParentInfo(parent);
//		return locator;
//	}

//	private TreeCellLocator doClone() {
//		return new TreeCellLocator(getPath());
//	}
	


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.TreeItemLocator#in(com.windowtester.runtime.swt.locator.SWTWidgetLocator)
	 */
	public TreeItemLocator in(SWTWidgetLocator parent) {
		return qualifiedClone(UNASSIGNED, parent);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.TreeItemLocator#in(int, com.windowtester.runtime.swt.locator.SWTWidgetLocator)
	 */
	public TreeItemLocator in(int index, SWTWidgetLocator parent) {
		return qualifiedClone(index, parent);
	}

	private TreeItemLocator qualifiedClone(int index, SWTWidgetLocator parent) {
		TreeCellLocator locator = new TreeCellLocator(getSelectionModifiers(), getPath(), index, parent);
		locator.columnIndex = columnIndex;
		return locator;
	}
	
	
	//TODO: fill out
	public TreeCellLocator at(ILocator locator) {
		if (locator instanceof Column) {
			columnIndex = ((Column)locator).getIndex();
		}
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		
		Tree tree = (Tree)getControl(ui);
		Widget clicked = null;
		int clicks    = click.clicks();
		int modifiers = click.modifierMask();
		/*
		 * add selection modifiers 
		 * For now, only permit checks
		 */
		if (getSelectionModifiers() == WT.CHECK)
			modifiers = modifiers | getSelectionModifiers();
		
		
		Point offset = getOffset(click);
			
		preClick(tree, ui);
		
		clicked = new TreeCellSelector().click(clicks, tree, getPath(), columnIndex, offset, modifiers);
	
		IWidgetReference clickedRef = WidgetReference.create(clicked, this);	
		postClick(clickedRef, ui);
		
		return clickedRef;	
	}
	
	//TODO: context click!
	
	
	/**
	 * Create a condition that tests if the given tree cell has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getText(com.windowtester.runtime.IUIContext)
	 */
	public String getText(IUIContext ui) throws WidgetSearchException {
		IWidgetReference ref = (IWidgetReference) ui.find(this);
		TreeItem item = (TreeItem) ref.getWidget();
		return UIProxy.getText(item, columnIndex);
	}
}
