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
package com.windowtester.internal.runtime.junit.core;


/**
 * A <code>TestIdentifier</code> describes a test which is to be run, is running
 * or has been run.
 *
 */
public class TestIdentifier implements ITestIdentifier {

	private final String _id;

	/**
	 * Create an instance with the given id.
	 */
	public TestIdentifier(String id) {
		if (id == null) 
			throw new AssertionError("id must not be null");
		_id = id;
	}

	/**
	 * Get this test's id string.
	 */
	public String getName() {
		return getId();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getId().toString();
	}

	private String getId() {
		return _id;
	}
	
}
