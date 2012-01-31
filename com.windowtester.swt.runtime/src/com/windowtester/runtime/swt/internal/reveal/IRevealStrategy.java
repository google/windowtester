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
package com.windowtester.runtime.swt.internal.reveal;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;

/**
 * Implementers of <code>IRevealStrategy</code> know how to reveal
 * widgets that may not be visible in the UI.  For example, custom
 * revealers might be used to reveal tree items in a collapsed tree
 * or images on a canvas that are scrolled off the display.
 */
public interface IRevealStrategy {

	/**
	 * Ensure that this widget-relative item is visible.
	 * @param w the "parent" widget
	 * @param path the path identifying an item relative to the widget (e.g. "path/to/node")
	 * @param x the x offset within the item to reveal
	 * @param y the y offset within the item to reveal
	 * @return the revealed widget
	 * @throws WidgetNotFoundException
	 * @throws MultipleWidgetsFoundException
	 */
	Widget reveal(Widget w, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;

	/**
	 * Ensure that this widget-relative coordinate is visible.
	 * @param target the "parent" widget
	 * @param x the x offset relative to the widget to reveal
	 * @param y the y offset relative to the widget to reveal
	 * @return the revealed widget
	 */
	Widget reveal(Widget target, int x, int y);
	
	
}
