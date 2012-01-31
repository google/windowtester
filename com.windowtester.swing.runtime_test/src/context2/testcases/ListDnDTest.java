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

import swing.samples.DragListDemo;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.locator.JListLocator;
import com.windowtester.runtime.swing.locator.NamedWidgetLocator;

public class ListDnDTest extends UITestCaseSwing {

	public ListDnDTest(){
		super(DragListDemo.class);
	}
	
	
	public void testListDnD() throws WidgetSearchException{
		IUIContext ui = getUI();
		
		ui.click(new JListLocator("1 (list 1)",new NamedWidgetLocator("list1")));
		ui.dragTo(new JListLocator("1 (list 2)", new NamedWidgetLocator("list2")));
		ui.pause(10000);
	}
	
}
