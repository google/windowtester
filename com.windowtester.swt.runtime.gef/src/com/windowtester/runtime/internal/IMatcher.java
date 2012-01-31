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
package com.windowtester.runtime.internal;

/**
 * A matcher is a predicate that provides an indication whether a given object 
 * matches some criteria. 
 */
public interface IMatcher {

	
	//TODO: consider extending ISelfDescribing as JMock does...
	//http://jmock.org/javadoc/2.1.0-RC1/org/hamcrest/Matcher.html
	
	/**
	 * Evaluates the matcher for the given object to test.
	 * @param toTest teh object to test
	 * @return <code>true</code> if the matching criteria are met, <code>false</code> otherwise
	 */
	boolean matches(Object toTest);
	
}
