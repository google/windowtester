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
package com.windowtester.eclipse.ui.convert;

import org.eclipse.jdt.core.compiler.IProblem;

public class WTConvertAPIParseException extends IllegalArgumentException
{
	private static final long serialVersionUID = 293937469874520979L;
	private final IProblem problem;

	public WTConvertAPIParseException(IProblem problem) {
		super("Problem encountered when parsing source: \n   " + problem + "\n   line: "
			+ problem.getSourceLineNumber());
		this.problem = problem;
	}
	
	public IProblem getProblem() {
		return problem;
	}
}
