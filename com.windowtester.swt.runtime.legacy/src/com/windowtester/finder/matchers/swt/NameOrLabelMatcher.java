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
 * 
 * A matcher that matches on names or labels.
 * 
 * Names are set using the <code>setData(..)</code> (<code>widget.setData("name", "someWidgetName");</code> 
 * method; labels are found by inspecting the widget's text attribute (this corresponds, 
 * for example, to the human readable text in a button).
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p> 
 * @author Phil Quitslund
 *
 */
public class NameOrLabelMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.NameOrLabelMatcher {

	public NameOrLabelMatcher(String nameOrLabel) {
		super(nameOrLabel);
	}
    
}
