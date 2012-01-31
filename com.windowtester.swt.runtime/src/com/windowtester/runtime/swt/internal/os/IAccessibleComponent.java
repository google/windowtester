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
package com.windowtester.runtime.swt.internal.os;


/**
 * Components are contained by {@link IAccessibleWindow}s.
 *
 */
public interface IAccessibleComponent {

	/**
	 * Get this component's displayable name.
	 */
	String getAccessibleName() throws InvalidComponentException;
	
	/**
	 * Get this component's role as a string value.
	 */
	String getAccessibleRole() throws InvalidComponentException;

	IAccessibleComponent[] getAccessibleChildren() throws InvalidComponentException;
	
}
