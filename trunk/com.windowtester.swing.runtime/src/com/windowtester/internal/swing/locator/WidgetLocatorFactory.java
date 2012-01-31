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
package com.windowtester.internal.swing.locator;

import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import com.windowtester.internal.swing.util.ComponentAccessor;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.JCheckBoxLocator;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JRadioButtonLocator;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;
import com.windowtester.runtime.swing.locator.JToggleButtonLocator;
import com.windowtester.runtime.swing.locator.LabeledTextLocator;
import com.windowtester.runtime.swing.locator.NamedWidgetLocator;

/**
 * A factory for creating locators from concrete widgets.
 */
public class WidgetLocatorFactory {

	public static WidgetLocatorFactory _instance = new WidgetLocatorFactory();
	
	private WidgetLocatorFactory() {}

	public static WidgetLocatorFactory getInstance() {
		return _instance;
	}

	public SwingWidgetLocator create(Component w) {
		
		// create named widget locator
		Class cls = w.getClass();
		String name = w.getName();
		
		if (name != null && (name.indexOf("OptionPane")== -1))
			return new NamedWidgetLocator(cls,name);
		
		
		///////////////////////////////////////////////////////////////
		//
		// Handle special cases
		//
		///////////////////////////////////////////////////////////////
	
		if (w instanceof JButton) {
			return new JButtonLocator(((JButton)w).getText());
		}
		if (w instanceof JRadioButton) {
			return new JRadioButtonLocator(((JRadioButton)w).getText());
		}
		if (w instanceof JCheckBox){
			return new JCheckBoxLocator(((JCheckBox)w).getText());
		}
		if (w instanceof JToggleButton){
			return new JToggleButtonLocator(((JToggleButton)w).getText());
		}
			
		if (w instanceof JMenuItem) {
			JMenuItem item = (JMenuItem)w;
			String string = ComponentAccessor.extractMenuPath(item);
			String itemLabel = ComponentAccessor.extractMenuItemLabel(item);
			String path = string + "/" + itemLabel;
		
			return new JMenuItemLocator(w.getClass(),path);
		}
		
		if (w instanceof JTextField){ // create LabeledTextLocator
			Component parent = w.getParent();
			boolean found = false;
			String labelText = null;
			int labelIndex = 0;
			
			if (parent instanceof Container){	
				
				Component[] children = ((Container)parent).getComponents();
				Component child;
				for (int i = 0; i < children.length; i++) {
					child = children[i];
					//look for next widget of target class
					if (labelText != null) {
						if (child.getClass().equals(cls)) {
							found = (child == w) && (labelIndex == (i-1));
							if (found) break;
						}
					}
					//set up for next iteration
					if (child instanceof JLabel){
						labelText = ((JLabel)child).getText();
						labelIndex = i;
					}	
				}
			}
			if (found)
				return new LabeledTextLocator(labelText);
		
			
		}
		// widget locators for all other text components
		if (w instanceof JTextComponent){
			return new JTextComponentLocator(w.getClass());
		}
		
		//N.B. tree item locators are built up at codegen time
		
		///////////////////////////////////////////////////////////////
	
		
		///////////////////////////////////////////////////////////////
		//
		// Fall through case
		//
		///////////////////////////////////////////////////////////////
	
		return defaultLocator(w);

	}

	private SwingWidgetLocator defaultLocator(Component w) {
		Class cls = w.getClass();
		String name = w.getName();
		/**
		 * Check if the component has a text , set by the setText method
		 * TODO!pq: is this the only component with a button?
		 */
		if (w instanceof AbstractButton){
			String text = ((AbstractButton)w).getText();
			if (text != null){
				return  new SwingWidgetLocator(cls, text);
			}
		}	
		
		if (name != null)
			return new SwingWidgetLocator(cls, name);
		else return new SwingWidgetLocator(cls); 
	}
	

	
	
	
	
	
	
}
