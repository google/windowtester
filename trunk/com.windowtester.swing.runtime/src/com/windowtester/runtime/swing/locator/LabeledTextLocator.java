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

import javax.swing.JTextField;

import com.windowtester.internal.swing.matcher.HierarchyMatcher;
import com.windowtester.internal.swing.matcher.IndexMatcher;
import com.windowtester.internal.swing.matcher.LabeledWidgetMatcher;
import com.windowtester.runtime.swing.SwingWidgetLocator;

/**
 * Locates a text component that is immediately adjacent to (e.g., following) 
 * a Label component with the given label text.
 * <p>
 * For instance, this locator:
 * <pre>
 *    new LabeledTextLocator("File:");
 * </pre>
 * identifies a Text component that is preceded by the "File:" label.
 * <p>
 * (A widget w1 is considered to be preceding another widget w2 if they are siblings with the same
 *  parent c1 and the index of w1 is just before the index of w2 in c1's list of children.)
 */
public class LabeledTextLocator extends JTextComponentLocator {

	
	private static final long serialVersionUID = -4186840479034195183L;
	
	/**
	 * Create an instance that locates a Text component of class JTextField preceded by a
	 * Label component with the given text. 
	 * @param label the text of the label preceding it
	 */
	public LabeledTextLocator(String label) {
		this(label,null);
	}
	
	/**
	 * Create an instance that locates a Text component of class JTextField preceded by a
	 * Label component with the given text, relative to a given parent.
	 * @param label the text of the label preceding it
	 * @param parent the parent locator
	 */
	public LabeledTextLocator(String label, SwingWidgetLocator parent) {
		this(label,UNASSIGNED,parent);
		
		
	}
	
	public LabeledTextLocator(int caret,String label){
		this(label,null);
		setCaretPosition(caret);
	}

	public LabeledTextLocator(String label,int index,SwingWidgetLocator parent){
		super(JTextField.class,label,index,parent);
		
		_matcher = LabeledWidgetMatcher.create(JTextField.class,label);
		if (index != UNASSIGNED)
			_matcher = IndexMatcher.create(_matcher, index);
		if (parent != null)
			_matcher = HierarchyMatcher.create(_matcher,parent.getMatcher());
		
	}
	
	protected String getWidgetLocatorStringName() {
		return "LabeledTextLocator" ;
	}
	
	
	
}
