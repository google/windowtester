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
package com.windowtester.runtime.swt.internal.matchers;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;


/**
 * A factory for {@link ISWTWidgetMatcher}s.
 */
public class WidgetMatchers {

	public static WidgetMatcher ofClass(Class<?> cls){
		return new ByClassMatcher(cls);
	}
	
	public static WidgetMatcher ofClass(String clsName){
		return new ByClassMatcher(clsName);
	}
	
	public static WidgetMatcher named(String name){
		return new HasNameMatcher(name);
	}

	public static WidgetMatcher withText(String text){
		return new ByTextMatcher(text);
	}
	
	public static WidgetMatcher withName(String name){
		return new ByNameMatcher(name);
	}
	
	public static WidgetMatcher isVisible(){
		return IsVisibleMatcher.forValue(true);
	}
	
	//isVisble() conflicts with SWTWidgetLocator method of same name
	public static WidgetMatcher visible(){
		return IsVisibleMatcher.forValue(true);
	}
	

	
	
}
