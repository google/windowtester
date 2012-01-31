package com.windowtester.swt.condition.eclipse;

import org.eclipse.core.runtime.IPath;

import com.windowtester.swt.condition.ICondition;

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
public class PathExistsCondition extends com.windowtester.runtime.swt.condition.eclipse.PathExistsCondition
	implements ICondition
{

	/**
	 * Create an instance that tests a given path.
	 * @param pathToTest the absolute path to test
	 * @param exists whether the path should exist or not
	 */
	public PathExistsCondition(IPath pathToTest, boolean exists) {
		super(pathToTest, exists);
	}
	
}
