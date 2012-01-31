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


import swing.samples.UseTheSampleDialog;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JButtonLocator;

public class TestDialog extends UITestCaseSwing {
			
			
			
		 private IUIContext ui;
			
			
			public TestDialog(){
				//super(SampleDialog.class);
				super(UseTheSampleDialog.class);
				System.out.println("back to test");
			}
			
			
		public void testMain() throws Exception {
			System.out.println("start test");
			ui = getUI();	
			ui.click(new JButtonLocator("Test the dialog!"));
			ui.wait(new WindowShowingCondition("Question"));
			ui.click(new JButtonLocator("Yes"));
			
		}
			

}
