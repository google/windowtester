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
package com.windowtester.internal.finder.matchers.swing;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;

import abbot.finder.AWTHierarchy;
import abbot.finder.matchers.AbstractMatcher;

/**
 * Matches a widget that is immediately adjacent to (e.g., following) 
 * a Label widget with the given label text.
 * <p>
 * For instance, this matcher:
 * <pre>
 *    new LabeledWidgetMatcher(Text.class, "File:");
 * </pre>
 * matches a Text widget that is preceded by the "File:" label.
 */

public class LabeledWidgetMatcher extends AbstractMatcher {
	
	private final String _labelText;
	private final Class _cls;

	
	/**
	 * Create an instance that matches an instance of a given class and preceded by a
	 * Label with the given text. 
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 */
	public LabeledWidgetMatcher(Class cls, String labelText) {
		_cls = cls;
		_labelText = labelText;
	}
	
	
	public boolean matches(final Component w) {
		Component parent = w.getParent();
		if (parent == null)
			return false;
		if (!parent.isShowing())
			return false;
		int index = getLabelIndex(parent);
		if (index == -1)
			return false;
		
		//	get the next widget that matches the target class in the list of children
		//if it matches our target widget, success!
		Collection c = AWTHierarchy.getDefault().getComponents(parent);
		Component[] children = getArray(c);
		
		for (int i=index; i < children.length; ++i) {
			// changed from exact class match
			
			//if (children[i].getClass().equals(_cls)) {
			if (_cls.isAssignableFrom(children[i].getClass())) {	
				if (children[i] == w)
					return true;	
				return false; //either way return
			}					
		}		
		return false;
		
	}
	
	/**
	 * Get the index of the target label in the list of our parent's children.
	 * @return - the label's index (-1 indicates an error)
	 */
	private int getLabelIndex(Component parent) {
		Collection c = AWTHierarchy.getDefault().getComponents(parent);
		Component[] children = getArray(c);
		
		for (int i=0; i < children.length; ++i) {
			if (children[i] instanceof JLabel) {
				JLabel label = (JLabel)children[i];
				if (stringsMatch(_labelText, label.getText()))
					return i;
			}
		}	
		return -1;
	}
	
	private Component[] getArray(Collection c){
		Component[] components = new Component[c.size()];
        int i = 0;
        Iterator iter = c.iterator(); 
        while (iter.hasNext()){
        	components[i] = (Component)(iter.next());
        	i++;
        }
        return components;
	}

}
