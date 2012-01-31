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
package com.windowtester.runtime.swt.condition.eclipse;

import org.eclipse.core.runtime.IPath;

import com.windowtester.runtime.condition.ICondition;

/**
 * Tests for the existence of a given absolute path.
 *
 */
public class PathExistsCondition implements ICondition {

	protected final IPath _pathToTest;
	protected final boolean _exists;

	/**
	 * Create an instance that tests a given path.
	 * @param pathToTest the absolute path to test
	 * @param exists whether the path should exist or not
	 */
	public PathExistsCondition(IPath pathToTest, boolean exists) {
		_exists = exists;
		_pathToTest = pathToTest;
	}
		
	/**
	 * Test whether the path to test exists.
	 * @return <code>true</code> if the existence of the path
	 * 		matches the expected value set in the constructor, <code>false</code> otherwise. 
	 */
	public boolean test() {
		return _pathToTest.toFile().exists() == _exists;
	}

	/**
	 * Return a String representation of this condition.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return " for path [" + _pathToTest + "] to exist: "
			+ _exists;
	}
	
	
}
