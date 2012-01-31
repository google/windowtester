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
package com.windowtester.runtime.junit4;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.internal.junit4.ExecutionMonitor;

/**
 * An access point for the current {@link IUIContext} for use in driving JUnit4-style
 * UI tests.  Commonly this factory will be imported statically.  
 * <p>
 * A simple example use is as follows:
 * <p>
 * 
 * <pre>
 * ...
 * import static com.windowtester.runtime.junit4.UIFactory.getUI;
 * 
 * &#064;RunWith(TestRunnerSWT.class)
 * public class NewProjectTest {
 *    &#064;Test
 *    public void verifyNewProjectCreation() throws Exception {
 *       IUIContext ui = getUI();
 *       ...
 *    }
 * }
 * </pre>
 * 
 * <p>
 *
 * @author Phil Quitslund
 *
 */
public class UIFactory {

	
	
	/**
	 * Get the UI Context for the currently executing test.
	 */
	public static IUIContext getUI() {
		return ExecutionMonitor.getUI();
	}


}
