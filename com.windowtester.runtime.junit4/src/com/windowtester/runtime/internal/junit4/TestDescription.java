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
package com.windowtester.runtime.internal.junit4;

import java.lang.reflect.Method;

import org.junit.runner.Description;

/**
 * Test {@link Description} factory.
 *
 * @author Phil Quitslund
 *
 */
public class TestDescription {

	public static Description fromMethod(Method method) {		
		return Description.createTestDescription(method.getDeclaringClass(), method.getName());
	}
	
}
