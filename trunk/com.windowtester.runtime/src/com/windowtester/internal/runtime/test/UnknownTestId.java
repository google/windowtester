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
package com.windowtester.internal.runtime.test;

import com.windowtester.internal.runtime.junit.core.ITestIdentifier;

/**
 * An identifier that signifies an unknown test case.
 */
public class UnknownTestId implements ITestIdentifier {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.util.ITestIdentifier#getName()
	 */
	public String getName() {
		return "Unknown TestCase";
	}

}
