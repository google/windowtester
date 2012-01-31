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
package com.windowtester.internal.swing.matcher;

import com.windowtester.internal.runtime.matcher.AdapterFactory;
import com.windowtester.runtime.locator.IWidgetMatcher;


/**
 * A factory for creating class by name matchers
 *
 */

public class ClassByNameMatcher {
	
	public static IWidgetMatcher create(String className) {
		return new AdapterFactory().adapt(new com.windowtester.internal.finder.matchers.swing.ClassByNameMatcher(className));
	}


}
