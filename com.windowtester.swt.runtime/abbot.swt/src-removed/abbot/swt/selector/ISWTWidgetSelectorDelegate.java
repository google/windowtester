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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;


public interface ISWTWidgetSelectorDelegate {

	Widget click(Widget w, int x, int y, int mask);
	Widget doubleClick(Widget w, int x, int y, int mask);

	
	/**
	 * Click this widget by label or path.  If the widget is a combo or list the 
	 * label identifies which item to select.  If the widget is a tree, the path string is
	 * used to locate the tree item to select.
	 * <p>
	 * Note on returned widget: in the case of combos and lists, the combo or list is returned;
	 * in the case of Trees and Menus, the TreeItem or MenuItem are returned. 
	 * @param w - the widget
	 * @param itemLabelOrPath - the label or the path identifying the item to click
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget click(Widget w, String itemLabelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/**
	 * Double-click this widget by label or path.  If the widget is a combo or list the 
	 * label identifies which item to select.  If the widget is a tree, the path string is
	 * used to locate the tree item to select.
	 * <p>
	 * Note on returned widget: in the case of combos and lists, the combo or list is returned;
	 * in the case of Trees and Menus, the TreeItem or MenuItem are returned. 
	 * @param w - the widget
	 * @param itemLabelOrPath - the label or the path identifying the item to click
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClickItem(Widget w, String itemLabel) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/**
	 * Click this widget by label or path.  If the widget is a combo or list the 
	 * label identifies which item to select.  If the widget is a tree, the path string is
	 * used to locate the tree item to select.
	 * <p>
	 * Note on returned widget: in the case of combos and lists, the combo or list is returned;
	 * in the case of Trees and Menus, the TreeItem or MenuItem are returned. 
	 * @param w - the widget
	 * @param itemLabelOrPath - the label or the path identifying the item to click
	 * @param mask - the mouse mask
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget click(Widget w, String itemLabelOrPath, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	Widget doubleClick(Widget w, String itemLabelOrPath, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	Widget contextClick(Widget w, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException;
	
	Widget contextClick(Widget w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	Widget contextClick(Widget w, int x, int y, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	

	///////////////////////////////////////////////////////////////////////////
	//
	// Click internals
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Calculate the offset from the top left for clicking this widget.
	 */
	Point getClickOffset(Widget w, int mask);
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selection actions
	//
	///////////////////////////////////////////////////////////////////////////
	
//	/** 
//	 * Select this range of items in the given widget. 
//	 * @param w - the widget in which to select
//	 * @param start - the starting index
//	 * @param stop - the stop index
//	 */
//	void select(Widget w, int start, int stop);
//	
//	/** 
//	 * Select all the items in the given widget. 
//	 * @param w - the widget in which to select
//	 */
//	void selectAll(Widget w);
//	
//	
//	
//	/**
//	 * Click to expand this widget.  In the case of a Tree Item, 
//	 * it expands the tree node.  In the case of a Tool Item, it expands
//	 * the pull down menu.
//	 * @param w - the widget to expand
//	 */
//	Widget clickExpand(Widget w);
	

}
