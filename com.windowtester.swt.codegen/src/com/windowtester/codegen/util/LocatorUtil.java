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
package com.windowtester.codegen.util;

public class LocatorUtil {
	
	/**
	 * returns true if the class cannot be resolved - is internal or a nested class
	 * @param className
	 * @return
	 */
	public static boolean isInternalSwingClass(String className){
		// nested class case
		if (className.indexOf("$") != -1)
			return true;
		if ((className.indexOf("javax.swing")== -1) && (className.indexOf("java.awt")== -1))
			return true;
		return false;
	}

}
