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
package com.windowtester.runtime.swt.internal.widgets;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Wrappers an SWT widget providing thread safe access to the widget's methods.
 */
public interface ISWTWidgetReference<T extends Widget>
	extends IWidgetReference
{
	
	public T getWidget();
	
	/**
	 * Gets the text associated with the underlying widget. The text is retrieved using
	 * the widget's <code>getText()</code> method if one is defined. If there is no such
	 * method a <code>null</code> value is returned instead. If a client wants to know if
	 * the underlying widget supports the <code>getText()</code> protocol, they are
	 * encouraged to call {@link #hasText()} first.
	 * 
	 * @return the widget's text
	 */
	String getText();

	/**
	 * Get the text associated with the underlying widget in a form suitable for matching.
	 * 
	 * @return the widget's text processed for matching
	 */
	String getTextForMatching();

	/**
	 * Gets the name associated with this widget.
	 * 
	 * @return the widget's name
	 */
	String getName();

	/**
	 * Test if this widget has associated text (as returned by a <code>getText()</code>
	 * method).
	 * 
	 * @return true if the widget has text, false otherwise
	 */
	boolean hasText();

	/**
	 * Proxy for {@link Widget#getData(String))}.
	 */
	Object getData(final String key);

	/**
	 * Proxy for {@link Widget#getData()}.
	 */
	Object getData();

	/**
	 * Returns the receiver's parent.
	 * 
	 * @return the receiver's parent
	 */
	ISWTWidgetReference<?> getParent();

	/**
	 * Returns the receiver's children.
	 * 
	 * @return the receiver's children
	 */	
	ISWTWidgetReference<?>[] getChildren();

	
	/**
	 * Calculate the client area of the widget and convert that from local coordinates to
	 * global coordinates (also known as display coordinates).
	 * 
	 * @return the client area of the widget in display coordinates
	 * @throws RuntimeException if the bounds cannot be calculated
	 */
	Rectangle getDisplayBounds();

	/**
	 * Answer <code>true</code> if the widget is enabled
	 */
	boolean isEnabled();

	/**
	 * Answer <code>true</code> if the widget is disposed
	 */
	boolean isDisposed();

	/**
	 * Answer the widget's 
	 */
	int getStyle();
	
	/**
	 * Answer <code>true</code> if the widget has the specified style
	 * 
	 * @param style the style to match such as {@link WT#CHECK}
	 * @return <code>true</code> if has style, else <code>false</code>
	 */
	boolean hasStyle(int style);
	

	boolean isVisible();

	/**
	 * Show the pulldown menu associated with the receiver
	 * 
	 * @param click the click description
	 * @return the pulldown menu
	 */
	MenuReference showPulldownMenu(IClickDescription click);
}
