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
package context2.testcases;



import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.UITestCaseSwing;

public class SWTSwingApplTest extends UITestCaseSwing{
	
	public SWTSwingApplTest(){
		super(SWTSwingApplication.class);
	}
	
	
	public void testMain() throws Exception {
		IUIContext ui = getUI();
		com.windowtester.runtime.IUIContext uiSwing = (com.windowtester.runtime.IUIContext)ui.getAdapter(UIContextSwing.class);
		
	//	uiSwing.click("Button");
	}
	
	
	
	

}
