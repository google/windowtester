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
package com.windowtester.internal.runtime;

public class DefaultCodeGenerator implements ICodeGenerator {

	private final StringBuffer buffer = new StringBuffer();
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.ICodeGenerator#getJavaVersion()
	 */
	public JavaVersion getJavaVersion() {
		return ICodeGenerator.JAVA4;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.ICodeGenerator#append(java.lang.String)
	 */
	public ICodeGenerator append(String body) {
		buffer.append(body);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.ICodeGenerator#addImport(java.lang.String)
	 */
	public ICodeGenerator addImport(String importString) {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.ICodeGenerator#toCodeString()
	 */
	public String toCodeString() {
		return buffer.toString();
	}
	
}
