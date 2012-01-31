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
/**
 * 
 */
package test;

import junit.framework.TestSuite;

public class JUnit3WTSuite {
	public static junit.framework.Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(NewProjectJUnit3Test.class);
		return suite;
	}
}