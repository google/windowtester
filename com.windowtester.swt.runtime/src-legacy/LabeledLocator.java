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
package com.windowtester.swt.locator;

import com.windowtester.swt.WidgetLocator;

/**
 * Locates a widget that is immediately adjacent to (e.g., following) 
 * a Label widget with the given label text.
 * <p>
 * For instance, this locator:
 * <pre>
 *    new LabeledLocator(Text.class, "File:");
 * </pre>
 * identifies a Text widget that is preceded by the "File:" label.
 * <p>
 * (A widget w1 is considered to be preceding another widget w2 if they are siblings with the same
 * Composite parent c1 and the index of w1 is just before the index of w2 in c1's list of children.)
 *
 * @deprecated Use {@link com.windowtester.runtime.swt.locator.LabeledLocator} instead
 */
public class LabeledLocator extends WidgetLocator {

	private static final long serialVersionUID = 80238627627779416L;

	/**
	 * Create an instance that locates a widget of a given class preceded by a
	 * Label widget with the given text. 
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 */
	public LabeledLocator(Class cls, String labelText) {
		super(cls,labelText);
	}
	
	/**
	 * Create an instance that locates a widget of a given class preceded by a
	 * Label widget with the given text, relative to a given parent.
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 * @param parentLocator the parent locator
	 */
	public LabeledLocator(Class cls, String labelText, WidgetLocator parentLocator) {
		super(cls, labelText, parentLocator);
	}
	
	/**
	 * Create an instance that locates a widget of a given class preceded by a
	 * Label widget with the given text, relative to a given parent.
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 * @param index the index relative to the parent
	 * @param parentLocator the parent locator
	 */
	public LabeledLocator(Class cls, String labelText, int index, WidgetLocator parentLocator) {
		super(cls, labelText, index, parentLocator);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LabeledLocator ["+ getTargetClass() + " labeled: " + getNameOrLabel() +"]";
	}
}
