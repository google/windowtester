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
package com.windowtester.runtime.internal.junit4.runners;

import com.windowtester.runtime.internal.junit4.mirror.runners.InitializationError;
import com.windowtester.runtime.junit4.UITestRunner.Launch;

/**
 * @author Phil Quitslund
 *
 */
public class UITestIntrospector {

	/*
	 * A sentinel to indicate no main class is specified.
	 */
	public static final class NoMain { }
	
	public static Class<?> getLaunchClass(Class<?> klass) throws InitializationError {
		Launch annotation= klass.getAnnotation(Launch.class);
		if (annotation == null)
			return null;
		Class<?> main = annotation.main();
		if (main == NoMain.class)
			return null;
		return main;
	}
	
	public static String[] getLaunchArgs(Class<?> klass) throws InitializationError {
		Launch annotation= klass.getAnnotation(Launch.class);
		if (annotation == null)
			return null;
		return annotation.args();
	}
	
	
}
