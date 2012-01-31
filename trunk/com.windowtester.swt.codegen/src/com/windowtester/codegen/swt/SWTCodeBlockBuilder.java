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

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.generator.IWidgetSystemCodeBlockBuilder;

public class SWTCodeBlockBuilder implements IWidgetSystemCodeBlockBuilder {
	
	private static final IWidgetSystemCodeBlockBuilder _instance = new SWTCodeBlockBuilder();
	
	private SWTCodeBlockBuilder() {}
	
	public CodeBlock shellShowCondition(String shellTitle) {
		StringBuffer sb = new StringBuffer();
		sb.append("new ShellShowingCondition(\"").append(shellTitle).append("\")");
		return new CodeBlock(sb);
	}
	
	public CodeBlock shellDisposedCondition(String shellTitle) {
		StringBuffer sb = new StringBuffer();
		sb.append("new ShellDisposedCondition(\"").append(shellTitle).append("\")");
		return new CodeBlock(sb);
	}

	public static IWidgetSystemCodeBlockBuilder getInstance() {
		return _instance;
	}
}
