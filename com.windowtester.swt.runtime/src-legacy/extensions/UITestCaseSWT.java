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
package junit.extensions;


/**
 * A UI TestCase tailored for executing SWT-based application tests. 
 * <p>
 * <strong>DISCOURAGED</strong>.  This class has been replaced by 
 * {@link com.windowtester.runtime.swt.UITestCaseSWT} which is now
 * preferred.  This class remains purely for legacy compatability.
 * </p> 
 * 
 *
 * @author Phil Quitslund
 * @deprecated prefer {@link com.windowtester.runtime.swt.UITestCaseSWT}
 */
public class UITestCaseSWT extends com.windowtester.runtime.swt.UITestCaseSWT {
		
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Instance Creation
	//
	/////////////////////////////////////////////////////////////////////////////////

	public UITestCaseSWT() {
		super();
	}

	public UITestCaseSWT(String testName) {
		super(testName);
	}
	
	public UITestCaseSWT(String testName, Class launchClass) {
		super(testName, launchClass);
	}
	
	public UITestCaseSWT(String testName, Class launchClass, String[] launchArgs) {
		super(testName, launchClass, launchArgs);
	}
	
	public UITestCaseSWT(Class launchClass) {
		super(launchClass);
	}
	
	public UITestCaseSWT(Class launchClass, String[] launchArgs) {
		super(launchClass, launchArgs);
	}
	
}
