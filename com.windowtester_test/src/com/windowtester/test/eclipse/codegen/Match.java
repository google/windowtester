package com.windowtester.test.eclipse.codegen;


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
public @interface Match {
	/**
	 * Specifies the type of match the test performs. {@link Type#INEXACT}
	 * suggests that the generated code is not exactly what was used to generate it.
	 */
	Type value() default Type.EXACT;
}
