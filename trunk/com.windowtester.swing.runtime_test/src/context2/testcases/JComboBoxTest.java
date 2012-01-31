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

import javax.swing.JComboBox;

import swing.samples.ComboBoxes;
import abbot.finder.ComponentNotFoundException;
import abbot.finder.MultipleComponentsFoundException;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JComboBoxLocator;
import com.windowtester.runtime.swing.locator.NamedWidgetLocator;



public class JComboBoxTest extends UITestCaseSwing {
	

	JComboBox cBox;
	
	
	private IUIContext ui;
	
	
	public JComboBoxTest(){
		super(ComboBoxes.class);
	}
	
	protected void setUp() throws Exception {
		ui = getUI();	
	}
	
	
	public void testComboClicks() throws ComponentNotFoundException, MultipleComponentsFoundException, WidgetSearchException {

		IWidgetLocator locator;
		ui.wait(new WindowShowingCondition("Swing Combo Boxes"));
		locator = ui.click(new JComboBoxLocator("Cat",new com.windowtester.runtime.swing.SwingWidgetLocator(javax.swing.Box.class, 0, new com.windowtester.runtime.swing.SwingWidgetLocator(ComboBoxes.class))));
		cBox = (JComboBox)((IWidgetReference)locator).getWidget();
		assertEquals("Cat",cBox.getSelectedItem());
		JComboBoxLocator comboBoxLocator = new JComboBoxLocator("Rabbit",new NamedWidgetLocator("pets"));
		ui.assertThat(comboBoxLocator.isEnabled());
		ui.click(comboBoxLocator);
		assertEquals("Rabbit",cBox.getSelectedItem());
		
		locator = ui.click(new JComboBoxLocator("yellow",new com.windowtester.runtime.swing.SwingWidgetLocator(javax.swing.Box.class, 1, new com.windowtester.runtime.swing.SwingWidgetLocator(ComboBoxes.class))));
		cBox = (JComboBox)((IWidgetReference)locator).getWidget();
		assertEquals("yellow",cBox.getSelectedItem());
		
	}
	
	
/*	public void testComboEnterTextFails() throws ComponentNotFoundException, MultipleComponentsFoundException, WidgetSearchException {

		IWidgetLocator locator;
		ui.click(2,new JComboBoxLocator(new com.windowtester.runtime.swing.SwingWidgetLocator(javax.swing.Box.class, 1, new com.windowtester.runtime.swing.SwingWidgetLocator(ComboBoxes.class))));
		ui.enterText("pink\n");		
		ui.pause(300);
		locator = ui.click(new JComboBoxLocator("pink",new com.windowtester.runtime.swing.SwingWidgetLocator(javax.swing.Box.class, 1, new com.windowtester.runtime.swing.SwingWidgetLocator(ComboBoxes.class))));
		cBox = (JComboBox)((IWidgetReference)locator).getWidget();
		assertEquals("pink",cBox.getSelectedItem());
		
	}
	
	*/
	
	
	

}
