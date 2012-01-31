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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.matchers.swt.AbstractMatcher;

import com.windowtester.swt.locator.SWTHierarchyHelper;

/**
 * Matches a widget that is immediately adjacent to (e.g., following) 
 * a Label widget with the given label text.
 * <p>
 * For instance, this matcher:
 * <pre>
 *    new LabeledWidgetMatcher(Text.class, "File:");
 * </pre>
 * matches a Text widget that is preceded by the "File:" label.
 * 
 * <p>
 * @author Phil Quitslund
 *
 */
public class LabeledWidgetMatcher extends AbstractMatcher {

	private final String _labelText;
	private final Class _cls;

	private SWTHierarchyHelper _helper;
	
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
	
	
	/* (non-Javadoc)
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(final Widget w) {
		Display d = w.getDisplay();
		Widget parent = getHelper(d).getParent(w);
		if (!(parent instanceof Composite))
			return false;
		
		final Composite comp = (Composite)parent;
		final boolean result[]= new boolean[1];
		
		d.syncExec(new Runnable() {
			public void run() {
				final int index = getLabelIndex(comp);
				if (index == -1)
					return; //short-circuit
						
				//get the next widget that matches the target class in the list of children
				//if it matches our target widget, success!
				Control[] children = comp.getChildren();
				//fixed: increment index by one to get NEXT 
				for (int i=index+1; i < children.length; ++i) {
					if (children[i].getClass().equals(_cls)) {
						if (children[i] == w)
							result[0] = true;	
						return; //either way return
					}
				}					
			}
		});		
		return result[0];
	}

	
	/**
	 * Get the index of the target label in the list of our parent's children.
	 * @return - the label's index (-1 indicates an error)
	 */
	private int getLabelIndex(Composite parent) {
		Control[] children = parent.getChildren();
		for (int i=0; i < children.length; ++i) {
			if (children[i] instanceof Label) {
				Label label = (Label)children[i];
				if (stringsMatch(_labelText, label.getText()))
					return i;
			}
		}	
		return -1;
	}
	

	private SWTHierarchyHelper getHelper(Display display) {
		if (_helper == null)
			_helper = new SWTHierarchyHelper(display);
		return _helper;
	}

	
	
	
}
