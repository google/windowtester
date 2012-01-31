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
package com.windowtester.runtime.internal.junit4.mirror.runners;

import java.util.Arrays;
import java.util.List;

public class InitializationError extends Exception {
	
	static String NEW_LINE = System.getProperty("line.separator", "\n");
	
	private static final long serialVersionUID= 1L;
	private final List<Throwable> fErrors;

	public InitializationError(List<Throwable> errors) {
		fErrors= errors;
	}

	public InitializationError(Throwable... errors) {
		this(Arrays.asList(errors));
	}
	
	public InitializationError(String string) {
		this(new Exception(string));
	}

	public List<Throwable> getCauses() {
		return fErrors;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return super.toString() +  ": " + NEW_LINE + "\t" + Arrays.toString(getCauses().toArray());
	}
}
