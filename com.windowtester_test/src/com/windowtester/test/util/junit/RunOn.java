package com.windowtester.test.util.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
@Retention(RetentionPolicy.RUNTIME)
public @interface RunOn {
	OS value() default OS.ALL;
	OS[] but() default OS.NONE;
}
