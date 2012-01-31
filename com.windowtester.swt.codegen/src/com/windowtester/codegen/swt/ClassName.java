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
package com.windowtester.codegen.swt;


/**
 * Convenience class used to represent the fully qualified name of a Java class.
 */
public class ClassName {

	
	public static ClassName forQualifiedName(String qualifiedName) {
		return new ClassName(qualifiedName);
	}
	
	public static ClassName forClass(Class cls) {
		return new ClassName(cls.getName());
	}
	
	
	private String qualifiedName; // Fully qualified name of the Java class
	private String packageName; // Name of the package for this class
	private String className; // Name of the class without the package

	/**
	 * This constructor builds an object which represents the name of a Java
	 * class.
	 * 
	 * @param qualifiedName
	 *            String representing the fully qualified class name of the Java
	 *            class.
	 */
	private ClassName(String qualifiedName) {
		if (qualifiedName == null) {
			return;
		}

		this.qualifiedName = qualifiedName;

		int index = qualifiedName.lastIndexOf('.');
		if (index == -1) {
			className = qualifiedName;
			packageName = "";
		} else {
			packageName = qualifiedName.substring(0, index);
			className = qualifiedName.substring(index + 1);
		}
	}

	/**
	 * Gets the fully qualified name of the Java class.
	 * 
	 * @return String representing the fully qualified class name.
	 */
	public String getQualifiedClassName() {
		return qualifiedName;
	}

	/**
	 * Gets the package name for the Java class.
	 * 
	 * @return String representing the package name for the class.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Gets the Java class name without the package structure.
	 * 
	 * @return String representing the name for the class.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * String representation of this class name. It returns the fully qualified
	 * class name.
	 * 
	 * @return String representing the fully qualified class name.
	 */
	public String toString() {
		return getQualifiedClassName();
	}
} 
