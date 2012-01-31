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
package com.windowtester.runtime.gef.internal.util;

public abstract class AbstractDescriber implements IDescriber {
	
	private static final int DEFAULT_INDENT_AMT = 3;
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	
	protected void separate(StringBuffer buffer) {
		buffer.append(' ');
	}
	
	protected void indent(int indent, StringBuffer buffer) {
		for (int i = 0; i < indent; i++) {
			buffer.append(" ");
		}
	}
	
	protected void newline(StringBuffer sb) {
		sb.append(NEW_LINE);
	}
	
	
	public int getIndent() {
		return DEFAULT_INDENT_AMT;
	}
	
	
}
