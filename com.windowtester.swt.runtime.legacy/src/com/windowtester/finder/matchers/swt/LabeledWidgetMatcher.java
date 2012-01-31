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
package com.windowtester.finder.matchers.swt;


/**
 * Matches a widget that is immediately adjacent to (e.g., following) 
 * a Label widget with the given label text.
 * <p>
 * For instance, this matcher:
 * <pre>
 *    new LabeledWidgetMatcher(Text.class, "File:");
 * </pre>
 * matches a Text widget that is preceded by the "File:" label.
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * @author Phil Quitslund
 *
 */
public class LabeledWidgetMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.LabeledWidgetMatcher {


	
	/**
	 * Create an instance that matches an instance of a given class and preceded by a
	 * Label with the given text. 
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 */
	public LabeledWidgetMatcher(Class cls, String labelText) {
		super(cls, labelText);
	}	
	
}
