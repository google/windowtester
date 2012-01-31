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
package com.windowtester.runtime.swt.internal.locator;

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.visible;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * A locator that works by class name.
 */
public class SWTWidgetByClassNameLocator extends SWTWidgetLocator {
	
	private static final long serialVersionUID = 1L;
	private final String className;
	
	public SWTWidgetByClassNameLocator(String className) {
		super(Widget.class);
		this.className = className;
	}
	
	protected String getClassName() {
		return className;
	}
	
	protected ISWTWidgetMatcher buildMatcher() {
		return SWTMatcherBuilder.buildMatcher(ofClass(className), visible());
//		return new CompoundMatcher(new ByNameClassMatcher(getClassName()), VisibilityMatcher.create(true));
//		return new ByClassMatcher(className).and(IsVisibleMatcher.forValue(true));
	}
}