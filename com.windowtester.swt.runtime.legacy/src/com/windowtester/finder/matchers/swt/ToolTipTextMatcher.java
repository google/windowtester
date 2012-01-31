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
 * A matcher for identifying widgets by their tooltip text.
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * @author Phil Quitslund
 */
public class ToolTipTextMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.ToolTipTextMatcher {

	public ToolTipTextMatcher(String toolTip) {
		super(toolTip);
	}    
}
