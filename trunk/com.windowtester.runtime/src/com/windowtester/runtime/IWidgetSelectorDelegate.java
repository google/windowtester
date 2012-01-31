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
package com.windowtester.runtime;

/**
 * Interface for a widget selector that is contributed to the windowtester runtime.
 * <code>IWidgetSelectorDelegate</code>s are registered with the runtime via the runtime's
 * {@link com.windowtester.internal.runtime.selector.IWidgetSelectorService}. Once registered to a class,
 * a widget selector will receive all calls to effect widgets of the type to which it is registered.
 * For instance, if a MyButtonSelector widget selector is registered for the type MyButton, all
 * calls to click a MyButton will be dispatched to the click methods of the MyButtonSelector.  
 * <p>
 * Note that not all methods will be appropriate for all widget types.  For instance, buttons
 * are unlikely to be referenced by path.  In such cases, implementers are encouraged
 * to throw an {@link java.lang.UnsupportedOperationException}.
 * <p>
 */
public interface IWidgetSelectorDelegate {

	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Click actions
	//
	///////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Click in the given part of the component using the specified mouse mask.
	 * <p>
	 * By convention, the runtime uses mouse and key constants as defined in SWT.
	 * For example "SWT.BUTTON1 | SWT.SHIFT" specifies a shift-click of mouse button 1.
	 * @param w the widget to click
	 * @param x the x offset (from the top left) to click
	 * @param y the y offset (from the top left) to click
	 * @param mask the mouse mask
	 * @return the clicked widget
	 */
	Object click(Object w, int x, int y, int mask);
		
	/**
	 * Click this widget by path, such as clicking a tree item relative to the tree by
	 * specifying the path to the node to click (e.g., "path/to/node").
	 * <p>
	 * The convention is to use "\" as a path delimiter.
	 * <p>
	 * Note on the returned widget: Wherever possible, the most specific widget is returned.  For instance
	 * in the case of a tree, the selected tree item should be returned.  If there is no such thing as a
	 * tree item for the tree class in question, the tree itself should be returned.
	 * @param w the widget
	 * @param path the path identifying the item to click
	 * @return the clicked widget
	 * @throws WidgetNotFoundException if the path cannot be resolved
	 * @throws MultipleWidgetsFoundException if the path is ambiguous
	 * @throws UnsupportedOperationException  if path-based selections are not supported for this widget type
	 */
	Object click(Object w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/**
	 * Click this widget by path, such as clicking a tree item relative to the tree by
	 * specifying the path to the node to click (e.g., "path/to/node").
	 * <p>
	 * The convention is to use "\" as a path delimiter.
	 * <p>
	 * Note on the returned widget: Whereever possible, the most specific widget is returned.  For instance
	 * in the case of a tree, the selected tree item should be returned.  If there is no such thing as a
	 * tree item for the tree class in question, the tree itself should be returned.
	 * @param w the widget
	 * @param path the path identifying the item to click
	 * @param mask the mouse mask
	 * @return the clicked widget
	 * @throws WidgetNotFoundException if the path cannot be resolved
	 * @throws MultipleWidgetsFoundException if the path is ambiguous
	 * @throws UnsupportedOperationException  if path-based selections are not suported for this widget type
	 */
	Object click(Object w, String path, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	

	///////////////////////////////////////////////////////////////////////////////
	//
	// Double-click actions
	//
	///////////////////////////////////////////////////////////////////////////////

	/**
	 * Double-click this widget by path, such as clicking a tree item relative to the tree by
	 * specifying the path to the node to click (e.g., "path/to/node").
	 * <p>
	 * The convention is to use "\" as a path delimiter.
	 * <p>
	 * Note on the returned widget: Whereever possible, the most specific widget is returned.  For instance
	 * in the case of a tree, the selected tree item should be returned.  If there is no such thing as a
	 * tree item for the tree class in question, the tree itself should be returned.
	 * @param w the widget
	 * @param path the path identifying the item to click
	 * @return the clicked widget
	 * @throws WidgetNotFoundException if the path cannot be resolved
	 * @throws MultipleWidgetsFoundException if the path is ambiguous
	 * @throws UnsupportedOperationException  if path-based selections are not supported for this widget type
	 */
	Object doubleClick(Object w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** 
	 * Double-click in the given part of the component using the specified mouse mask.
	 * <p>
	 * By convention, the runtime uses mouse and key constants as defined in SWT.
	 * For example "SWT.BUTTON1 | SWT.SHIFT" specifies a shift-click of mouse button 1.
	 * @param w the widget to click
	 * @param x the x offset (from the top left) to click
	 * @param y the y offset (from the top left) to click
	 * @param mask the mouse mask
	 * @return the clicked widget
	 */
	Object doubleClick(Object w, int x, int y, int mask);

	/**
	 * Double-click this widget by path, such as clicking a tree item relative to the tree by
	 * specifying the path to the node to click (e.g., "path/to/node").
	 * <p>
	 * The convention is to use "\" as a path delimiter.
	 * <p>
	 * Note on the returned widget: Wherever possible, the most specific widget is returned.  For instance
	 * in the case of a tree, the selected tree item should be returned.  If there is no such thing as a
	 * tree item for the tree class in question, the tree itself should be returned.
	 * @param w the widget
	 * @param path the path identifying the item to click
	 * @param mask the mouse mask
	 * @return the clicked widget
	 * @throws WidgetNotFoundException if the path cannot be resolved
	 * @throws MultipleWidgetsFoundException if the path is ambiguous
	 * @throws UnsupportedOperationException  if path-based selections are not supported for this widget type
	 */
	Object doubleClick(Object w, String path, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException;

	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Context-click actions
	//
	///////////////////////////////////////////////////////////////////////////////

	Object contextClick(Object w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	Object contextClick(Object w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	Object contextClick(Object w, int x, int y, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Click internals
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Calculate the offset from the top left for clicking this widget.  This method is used to determine
	 * where on the widget to click when no x,y coordinates are specified.
	 */
	java.awt.Point getClickOffset(Object w);
	

}
