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
package com.windowtester.swt.codegen.dialogs;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;

/**
 * Type selection extension that filters out all but a given set of types.
 */
public class FilteredTypeSelectionExtension extends TypeSelectionExtension {

	private class TypeFilter implements ITypeInfoFilterExtension {
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension#select(org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor)
		 */
		public boolean select(ITypeInfoRequestor typeInfoRequestor) {
			return isIncluded(getFullyQualifiedName(typeInfoRequestor));
		}
	}
	
	public static TypeSelectionExtension forTypes(IType[] types) {
		return new FilteredTypeSelectionExtension(types);
	}
	
	private String getFullyQualifiedName(ITypeInfoRequestor typeInfo) {
		String packageName = typeInfo.getPackageName();
		if (packageName.length() > 0)
			packageName += ".";
		return packageName + typeInfo.getTypeName();
	}
	
	TypeFilter filter = new TypeFilter();
	private final IType[] types;
	

	public FilteredTypeSelectionExtension(IType[] types) {
		this.types = types;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.dialogs.TypeSelectionExtension#getFilterExtension()
	 */
	public ITypeInfoFilterExtension getFilterExtension() {
		return filter;
	}

	public boolean isIncluded(String typeName) {
		for (int i = 0; i < types.length; i++) {
			IType type = types[i];
			String fullyQualifiedName = type.getFullyQualifiedName();
			//System.out.println(fullyQualifiedName);
			//System.out.println(" vs. " + typeName);
			if (fullyQualifiedName.equals(typeName)) {
				//System.out.println("match!: " + fullyQualifiedName);
				return true;
			}
		}
		return false;
	}

	
	
}
