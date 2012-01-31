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
package com.windowtester.runtime.internal.junit4.runner;

import com.windowtester.runtime.internal.junit4.mirror.runners.InitializationError;

/**
 * 
 * @author Phil Quitslund
 *
 */
public class RunnerFactory {

	public static ITestRunnerDelegate createRunner(Class<?> klass) throws InitializationError {		
		return new JUnit4RunnerDelegate(klass);
	}

}
