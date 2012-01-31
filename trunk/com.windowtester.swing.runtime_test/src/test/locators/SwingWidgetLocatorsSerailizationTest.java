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
package test.locators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;
import javax.swing.JTextField;

import junit.framework.TestCase;

import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.JComboBoxLocator;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;

public class SwingWidgetLocatorsSerailizationTest extends TestCase {
	
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	/**
	 *  verify that JButtonLocators can serialize out and back in without corruption.
	 */
	
	public void testJButtonLocaotorSerializes() throws Exception {
		// create locator
		JButtonLocator locator = new JButtonLocator("Button",0,new SwingWidgetLocator(JPanel.class));
		
		// send it and read it back
		JButtonLocator copy = (JButtonLocator)streamOutAndBackIn(locator);
		assertEquals(locator.getTargetClass(), copy.getTargetClass());
		assertEquals(locator.getNameOrLabel(), copy.getNameOrLabel());
		assertEquals(locator.getIndex(), copy.getIndex());
		
	}
	
	
	/**
	 *  verify that JComboBoxLocators can serialize out and back in without corruption.
	 */
	
	public void testJComboBoxLocaotorSerializes() throws Exception {
		// create locator
		JComboBoxLocator locator = new JComboBoxLocator("ValueNo1",0,new SwingWidgetLocator(JPanel.class));
		
		// send it and read it back
		JComboBoxLocator copy = (JComboBoxLocator)streamOutAndBackIn(locator);
		assertEquals(locator.getTargetClass(), copy.getTargetClass());
		assertEquals(locator.getNameOrLabel(), copy.getNameOrLabel());
		assertEquals(locator.getIndex(), copy.getIndex());
		
	}
	
/*	public void testJTextComponentLocatorSerializes() throws Exception{
		JTextComponentLocator locator = new JTextComponentLocator(JTextField.class, 23,
				new SwingWidgetLocator(DatePicker.class));
		JTextComponentLocator copy = (JTextComponentLocator)streamOutAndBackIn(locator);
		assertEquals(locator.getTargetClass(), copy.getTargetClass());
		assertEquals(locator.getNameOrLabel(), copy.getNameOrLabel());
		assertEquals(locator.getIndex(), copy.getIndex());
	}
	*/
	/**
	 * @param locator
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private SwingWidgetLocator streamOutAndBackIn(Object locator) throws IOException, ClassNotFoundException {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream ();
	    ObjectOutputStream out     = new ObjectOutputStream (bout);

	    out.writeObject(locator);
	    out.flush();

	    ByteArrayInputStream bin = new ByteArrayInputStream (bout.toByteArray ());
	    ObjectInputStream in     = new ObjectInputStream (bin);

	    return (SwingWidgetLocator)in.readObject();
	}
	
}
