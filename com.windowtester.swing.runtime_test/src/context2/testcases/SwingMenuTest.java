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

import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import swing.samples.SwingMenus;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JCheckBoxMenuItemLocator;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JMenuLocator;
import com.windowtester.runtime.swing.locator.JRadioButtonMenuItemLocator;

public class SwingMenuTest extends UITestCaseSwing {


	
	private IUIContext ui;
	
	public SwingMenuTest(){
		super(SwingMenus.class);
	}
	

	public void testSelection() throws Exception {
		IWidgetLocator locator;
		JMenuItem menuItem;
		JRadioButtonMenuItem radioButtonMenuItem;
		
		ui = getUI();
		
		ui.wait(new WindowShowingCondition("Swing Menus Example"));
		System.out.println("Verify using isEnabled condition");
		ui.assertThat(new JMenuItemLocator("parent/submenu/grandchild").isEnabled());
		locator = ui.click(new JMenuItemLocator("parent/submenu/grandchild"));
		
		locator = ui.click(new JMenuItemLocator("parent/child 1"));
		menuItem = (JMenuItem)((IWidgetReference)locator).getWidget();
		assertEquals("child 1", menuItem.getText());
		menuItem.setEnabled(false);
		ui.assertThat(new JMenuItemLocator("parent/child 1").isEnabled(false));
		menuItem.setEnabled(true);
		ui.assertThat(new JMenuItemLocator("parent/child 1").isEnabled());
		
		JRadioButtonMenuItemLocator radioButtonMenuItemLocator = new JRadioButtonMenuItemLocator("parent/child 2");
		ui.assertThat(radioButtonMenuItemLocator.isEnabled(false));
		locator = ui.find(radioButtonMenuItemLocator);
		radioButtonMenuItem = (JRadioButtonMenuItem) ((IWidgetReference)locator).getWidget();
		radioButtonMenuItem.setEnabled(true);
		ui.assertThat(radioButtonMenuItemLocator.isEnabled());
		ui.click(radioButtonMenuItemLocator);
		ui.assertThat(radioButtonMenuItemLocator.isSelected());
		
		JCheckBoxMenuItemLocator checkBoxMenuItemLocator = new JCheckBoxMenuItemLocator("parent/child 3");
		ui.assertThat(checkBoxMenuItemLocator.isEnabled());
		locator = ui.click(checkBoxMenuItemLocator);
		ui.assertThat(checkBoxMenuItemLocator.isSelected());
		menuItem = (JMenuItem)((IWidgetReference)locator).getWidget();
		assertEquals("child 3",menuItem.getText());
		
		
		
//	}
	
//	public void testSelection2() throws Exception {
//		IWidgetLocator locator;
//		JMenuItem menuItem;
		
//		ui = getUI();

		locator = ui.click(new JMenuItemLocator("parent/submenu/grandchild"));
		menuItem = (JMenuItem)((IWidgetReference)locator).getWidget();
		assertEquals("grandchild",menuItem.getText());

		JMenuLocator menuLocator = new JMenuLocator("top");
		ui.assertThat(menuLocator.isEnabled());
		locator = ui.click(menuLocator);
		menuItem = (JMenuItem)((IWidgetReference)locator).getWidget();
		assertEquals("top",menuItem.getText());
		
		// three menu levels
		locator = ui.click(new JMenuItemLocator("top/submenu1/submenu2/item1"));
		menuItem = (JMenuItem)((IWidgetReference)locator).getWidget();
		assertEquals("item1",menuItem.getText());
		
		locator = ui.click(new JMenuItemLocator("top/submenu1/submenu2/item2"));
		menuItem = (JMenuItem)((IWidgetReference)locator).getWidget();
		assertEquals("item2",menuItem.getText());
//	}
	
		
//	public void testFailedSelections() throws MultipleComponentsFoundException {
//		ui = getUI();
		//1
		try {
			ui.click(new JMenuItemLocator("child 2/nonexistent"));
			fail("should have thrown CME ex");
		} catch (WidgetSearchException e) {
			//pass
		
		}
		//2
		try {
			ui.click(new JMenuItemLocator( "bogus"));
			fail("should have thrown CME ex");
		} catch (WidgetSearchException e) {
			//pass
		}
		
		//3
		try {
			ui.click(new JMenuItemLocator( "bogus/really"));
			fail("should have thrown CME ex");
		} catch (WidgetSearchException e) {
			//pass
		}
		
	}
		
		
}
	
	
	
	
	
	
	

