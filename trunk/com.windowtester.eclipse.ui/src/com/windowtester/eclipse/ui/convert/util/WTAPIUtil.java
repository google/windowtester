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
package com.windowtester.eclipse.ui.convert.util;

public class WTAPIUtil
{

	/**
	 * Answer the package name for the specified type
	 * 
	 * @param typeName the fully qualified type name (not <code>null</code>, not empty)
	 * @return the package name (not <code>null</code> but may be empty representing the
	 *         default package)
	 */
	public static String packageNameForType(String typeName) {
		int index = typeName.lastIndexOf('.');
		if (index == -1)
			return "";
		return typeName.substring(0, index);
	}

	/**
	 * Answer the simple type name for the specified type name
	 * 
	 * @param typeName the simple type name or fully qualified type name (not
	 *            <code>null</code>, not empty)
	 * @return the simple type name (not <code>null</code>, not empty)
	 */
	public static String simpleTypeName(String typeName) {
		int index = typeName.lastIndexOf('.');
		if (index == -1)
			return typeName;
		return typeName.substring(index + 1);
	}
}
