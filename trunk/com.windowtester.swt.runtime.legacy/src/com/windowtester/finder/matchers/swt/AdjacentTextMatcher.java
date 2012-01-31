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

import org.eclipse.swt.widgets.Composite;

/**
 * A matcher that matches a Text widget that is immediately adjacent to (e.g., following) 
 * the widget with the given label.
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * 
 * @author Phil Quitslund
 * 
 */
public class AdjacentTextMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.AdjacentTextMatcher {

	public AdjacentTextMatcher(Composite parent, String labelName) {
		super(parent, labelName);
	}
}
