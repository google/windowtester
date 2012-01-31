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
package swing.samples;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.locator.JTreeItemLocator;
import com.windowtester.runtime.swing.locator.NamedWidgetLocator;

public class TreeDnDTest extends UITestCaseSwing {
	
	
	public TreeDnDTest(){
		super(TreeDnD.class);
	}
	
	
	public void testTreeDnD() throws WidgetSearchException{
	
		IUIContext ui = getUI();
		
		ui.click(new JTreeItemLocator("JTree/colors/blue", new NamedWidgetLocator("tree1")));
		ui.dragTo(new JTreeItemLocator("JTree/sports",new NamedWidgetLocator("tree2")));
		
		ui.click(new JTreeItemLocator("JTree/food/hot dogs", new NamedWidgetLocator("tree2")));
		ui.dragTo(new JTreeItemLocator("JTree/colors",new NamedWidgetLocator("tree1")));
		ui.pause(10000);
	
	}

}
