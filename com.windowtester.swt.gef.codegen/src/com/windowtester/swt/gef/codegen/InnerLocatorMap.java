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
package com.windowtester.swt.gef.codegen;

import java.util.HashMap;
import java.util.Map;

import com.windowtester.codegen.assembly.unit.ClassUnit;
import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.gef.locator.FigureClassLocator;

/**
 * A service for managing mappings of locators to inner classes.
 *
 */
public class InnerLocatorMap {

	
	private static final String NEW_LINE = StringUtils.NEW_LINE;
	
	private final Map classUnitMap = new HashMap();
	private final Map classNameMap = new HashMap();
	{
		classNameMap.put("Tree", "TreeFigureLocator");
		classNameMap.put("MenuItem", "MenuItemFigureLocator");
		classNameMap.put("TreeItem", "TreeItemFigureLocator");
		classNameMap.put("Figure", "MyFigureLocator");
		classNameMap.put("Palette", "MyPaletteLocator");
		classNameMap.put("PaletteItem", "MyPaletteItemLocator");
		// etc....
	}
	
	
	
	public ClassUnit get(FigureClassLocator locator) {
		ClassUnit unit = (ClassUnit) classUnitMap.get(getClassName(locator));
		if (unit == null) {
			unit = createUnit(locator);
		}
		return unit;
	}

	private ClassUnit createUnit(FigureClassLocator locator) {
		String fqName    = locator.getClassName();
		String shortName = deriveClassName(locator);
		String contents = serializationId() + constructorBody(fqName, shortName);
		ClassUnit unit = new ClassUnit(shortName, ClassUnit.PRIVATE, ClassUnit.STATIC, contents);
		unit.setExtends("FigureClassLocator");
		return unit;
	}

	private String serializationId() {
		return NEW_LINE + "private static final long serialVersionUID = 1L;" + NEW_LINE;
	}
	
	private String constructorBody(String fqName, String shortName) {
		return NEW_LINE + 
			"public " + shortName + "(){" + NEW_LINE + 
			"super(\"" + fqName + "\");" + NEW_LINE + "}";
	}

	private String deriveClassName(FigureClassLocator locator) {
		String className = getClassName(locator);
		String derivedName = checkForDefaultMapping(className);
		if (derivedName == null) {
			derivedName = createClassName(className);
		}
		return derivedName;
	}

	private String getClassName(FigureClassLocator locator) {
		String name = locator.getClassName();
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1)
			return name;
		return name.substring(lastDot+1);
	}

	private String createClassName(String className) {
		return className + "Locator";
	}

	private String checkForDefaultMapping(String className) {
		return (String) classNameMap.get(className);
	}

	
}

