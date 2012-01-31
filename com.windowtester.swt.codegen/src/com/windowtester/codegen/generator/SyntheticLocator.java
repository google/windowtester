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
package com.windowtester.codegen.generator;

import com.windowtester.codegen.ISourceTypeBuilder;
import com.windowtester.internal.runtime.IWidgetIdentifier;

/**
 * A locator that does not exist as a source file on disk.
 *
 */
public class SyntheticLocator implements IWidgetIdentifier, ISelfDescribingLocator {

	private final String locatorName;

	public SyntheticLocator(String locatorName) {
		this.locatorName = locatorName;
	}

	public String getNameOrLabel() {
		return locatorName;
	}

	public Class getTargetClass() {
		return null;
	}

	public String getTargetClassName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ISelfDescribingLocator#toJavaString()
	 */
	public String toJavaString() {
		return "new " + getNameOrLabel() +"()";
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ISelfDescribingLocator#addImports(com.windowtester.codegen.ISourceTypeBuilder)
	 */
	public void addImports(ISourceTypeBuilder builder) {
		//for now: none
	}
	
}
