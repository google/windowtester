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
package com.windowtester.runtime.swing.locator;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JTable;

import com.windowtester.internal.runtime.matcher.CompoundMatcher;
import com.windowtester.internal.runtime.matcher.ExactClassMatcher;
import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.internal.swing.matcher.HierarchyMatcher;
import com.windowtester.internal.swing.matcher.IndexMatcher;
import com.windowtester.internal.swing.matcher.NameMatcher;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swing.SwingWidgetLocator;


/**
 * A locator for JTable items.
 */
public class JTableItemLocator extends SwingWidgetLocator implements IsSelected{

	private static final long serialVersionUID = 7291989565140551064L;
	
	private final Point _rowCol;

	/**
	 * Creates an instance of a locator to an item in a JTable
	 * @param rowCol the (row,col) of the item
	 */
	public JTableItemLocator(Point rowCol) {
		this(rowCol,null);
	}
	
	/**
	 * Creates an instance of a locator to an item in a JTable, relative
	 * to the parent of the JTable.
	 * @param rowCol the (row,col) of the item
	 * @param parent locator of the parent 
	 */
	public JTableItemLocator(Point rowCol, SwingWidgetLocator parent) {
		this( rowCol,UNASSIGNED,parent);
	}
	
	/**
	 * Creates an instance of a locator to an item in a JTable, relative
	 * to the index and parent of the JTable
	 * @param rowCol the (row,col) of the item
	 * @param index the index of the JTable relative to the parent
	 * @param parent locator of the parent
	 */
	public JTableItemLocator(Point rowCol, int index, SwingWidgetLocator parent) {
		this(JTable.class, rowCol,index, parent);
		
	}
	
	/**
	 * Creates an instance of a locator to an item in a JTable, relative
	 * to the index and parent of the JTable
	 * @param cls the exact Class of the table
	 * @param rowCol the (row,col) of the item
	 * @param index the index of the JTable relative to the parent
	 * @param parent locator of the parent
	 */
	public JTableItemLocator(Class cls,Point rowCol, int index, SwingWidgetLocator parent) {
		super(cls,null,index,parent);
		_rowCol = rowCol;
		
		// create the matcher
		_matcher = new ExactClassMatcher(cls);
		if (index != UNASSIGNED)
			_matcher = IndexMatcher.create(_matcher, index);
		if (parent != null){
			if (parent instanceof com.windowtester.runtime.swing.locator.NamedWidgetLocator){
				_matcher = new CompoundMatcher(_matcher,NameMatcher.create(parent.getNameOrLabel()));
			}
			else {
				if (index != UNASSIGNED)
					_matcher = HierarchyMatcher.create(_matcher, parent.getMatcher(), index);
				else
					_matcher = HierarchyMatcher.create(_matcher,parent.getMatcher());
			}
		}
	}
	

	public int getRow() {
		return _rowCol.x;
	}

	public int getColumn() {
		return _rowCol.y;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.swing.WidgetLocator#doClick(com.windowtester.runtime2.IUIContext2, int, java.awt.Component, java.awt.Point, int)
	 */
	protected Component doClick(IUIContext ui, int clicks, Component c, Point offset, int modifierMask) {	
		return ((UIContextSwing)ui).getDriver().clickTable(clicks,(JTable)c,getRow(),getColumn(),modifierMask);
	}
	
	
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click,String menuItemPath) throws WidgetSearchException {
		Component component = (Component)widget.getWidget();
		Component clicked = ((UIContextSwing)ui).getDriver().contextClickTable((JTable)component,getRow(),getColumn(), menuItemPath);
		return WidgetReference.create(clicked, this);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		JTable table = (JTable) ((IWidgetReference)ui.find(this)).getWidget();
		return table.isCellSelected(getRow(), getColumn());
	}
	
	
    ///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given button is selected.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isSelected() {
		return isSelected(true);
	}
	
	/**
	 * Create a condition that tests if the given button is selected.
	 * @param selected 
	 * @param expected <code>true</code> if the button is expected to be selected, else
	 *            <code>false</code>
	 */            
	public IUICondition isSelected(boolean expected) {
		return new IsSelectedCondition(this, expected);
	}
	
	
	
	
	
}
