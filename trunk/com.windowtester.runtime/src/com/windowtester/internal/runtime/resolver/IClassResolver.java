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
package com.windowtester.internal.runtime.resolver;

/**
 * Resolves classes.
 */
public interface IClassResolver {

	/**
	 * Resolve this class.
	 * @param className - the name of the class to resolve
	 * @return the class instance if found, otherwise <code>null</code>
	 */
	Class resolveClass(String className);
	
}
