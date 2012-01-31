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
package com.windowtester.runtime.swt.internal.finder.matchers;

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.NameOrLabelMatcher;

/**
 * A factory for creating text matchers.
 *
 */
public class TextMatcher {

	public static IWidgetMatcher create(String txt) {
		//fixed to use regexp handling text matcher
		
		return new AdapterFactory().adapt(NameOrLabelMatcher.buildTextMatcher(txt));
	}
	
	
}
