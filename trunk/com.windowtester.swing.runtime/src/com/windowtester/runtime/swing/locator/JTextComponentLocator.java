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
package com.windowtester.runtime.swing.locator;

import java.awt.Component;
import java.awt.Point;

import javax.swing.text.JTextComponent;

import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasFocusCondition;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.util.StringComparator;
/**
 *   A locator for text components - JTextField, JTextArea, JEditorPane, JTextPane.
 */
public class JTextComponentLocator extends SwingWidgetLocator 
	implements HasText, IsEnabled ,HasFocus{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4186840479034195183L;
	
	/** 
	 * the position index in case of a text pane
	 */
	private int caretPosition = UNASSIGNED;
	
	/**
	 * Creates an instance of a locator for a JTextComponent
	 * @param cls the exact Class of the component
	 */
	public JTextComponentLocator(Class cls) {
		this(cls,null);
		
	}
	/**
	 * Creates an instance of a locator for a JTextComponent
	 * @param cls the exact Class of the component
	 * @param caret the caret position
	 */
	public JTextComponentLocator(int caret,Class cls){
		this(caret,cls,null);
	}
	
	
	public JTextComponentLocator(int caret,Class cls,String nameOrLabel){
		super(cls,nameOrLabel,UNASSIGNED,null);
		setCaretPosition(caret);
	}
	/**
	 * Creates an instance of a locator for a JTextComponent
	 * @param cls the exact Class of the component
	 * @param parent the locator for the parent of the JTextComponent
	 */
	public JTextComponentLocator(Class cls, SwingWidgetLocator parentInfo) {
		this(cls, UNASSIGNED,parentInfo);
		
	}

	/**
	 * Creates an instance of a locator for a JTextComponent
	 * @param cls the exact Class of the component
	 * @param index the index relative to the parent
	 * @param parentInfo the locator for the parent of the JTextComponent
	 */
	public JTextComponentLocator(Class cls, int index, SwingWidgetLocator parentInfo) {
		this(cls, null,index, parentInfo);
		
	}	
	/**
	 * Creates an instance of a locator for a JTextComponent
	 * @param cls the exact Class of the component
	 * @param nameOrLabel the name or label for the component
	 * @param index the index relative to the parent
	 * @param parentInfo the locator for the parent of the JTextComponent
	 */
	public JTextComponentLocator(Class cls,String nameOrLabel,int index,SwingWidgetLocator parentInfo){
		super(cls,nameOrLabel,index,parentInfo);
	}
	
	protected String getWidgetLocatorStringName() {
		return "JTextComponentLocator";
	}
	/**
	 * Set the caret position for the locator
	 * @param pos the position of the caret
	 */
	public void setCaretPosition(int pos){
		caretPosition = pos; 
	}
	
	/**
	 * Get the caret position
	 * @return int caret position
	 */
	public  int getCaretPosition(){
		return caretPosition;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swing.SWingWidgetLocator#getWidgetText(java.awt.Component)
	 */
	protected String getWidgetText(Component widget) {
		return ((JTextComponent)widget).getText();
	}
	
	
	
	protected Component doClick(IUIContext ui, int clicks, Component c, Point offset, int modifierMask) {
		if (caretPosition == UNASSIGNED)
			return super.doClick(ui, clicks, c, offset, modifierMask);
		return ((UIContextSwing)ui).getDriver().clickTextComponent((JTextComponent)c, getCaretPosition());
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	
	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
	
	/**
	 * Create a condition that tests if the given widget is enabled.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isEnabled(true)</code>
	 */
	public IUICondition isEnabled() {
		return isEnabled(true);
	}
	
	/**
	 * Create a condition that tests if the given widget is enabled.
	 * @param selected 
	 * @param expected <code>true</code> if the menu is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */            
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}
	
	

}
