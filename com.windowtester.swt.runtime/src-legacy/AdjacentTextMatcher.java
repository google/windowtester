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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.matchers.swt.AbstractMatcher;
import abbot.finder.swt.MultiMatcher;
import abbot.finder.swt.MultipleWidgetsFoundException;

/**
 * A matcher that matches a Text widget that is immediately adjacent to (e.g., following) 
 * the widget with the given label.
 * 
 * @author Phil Quitslund
 */
public class AdjacentTextMatcher extends AbstractMatcher implements MultiMatcher {

	/** The parent */
	private final Composite _parent;
	/** The label name */
	private final String _labelName;

	/**
	 * Create an instance.
	 * @param parent    - the parent control
	 * @param labelName - the name of the label
	 */
	public AdjacentTextMatcher(Composite parent, String labelName) {
		_parent = parent;
		_labelName = labelName;
	}

	/* (non-Javadoc)
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(final Widget w) {
		final boolean result[] = new boolean[1];
		w.getDisplay().syncExec( new Runnable() { 
			public void run() {
				result[0] = containedInParent(w);
			}
		});
		return result[0];
 	}

	/**
	 * Check whether the given widget is contained in our parent composite.
	 * @param w - the widget to check
	 * @return  - true if the widget is contained in our parent
	 */
	private boolean containedInParent(Widget w) {
		Control[] children = _parent.getChildren();
		for (int i=0; i < children.length; ++i) {
			if (children[i] == w)
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see abbot.finder.swt.MultiMatcher#bestMatch(org.eclipse.swt.widgets.Widget[])
	 */
	public Widget bestMatch(Widget[] candidates) throws MultipleWidgetsFoundException {
		final Widget[] result = new Widget[1];
		_parent.getDisplay().syncExec(new Runnable() {
			public void run() {
				int index       = getLabelIndex();
				if (index == -1) {
					result[0] = null; //signal error
					return;
				}
				//get the next Text widget in the list of children
				Control[] children = _parent.getChildren();
				for (int i=index; i < children.length; ++i) {
					if (children[i] instanceof Text) {
						result[0] = children[i];
						return;
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
	private int getLabelIndex() {
		Control[] children = _parent.getChildren();
		for (int i=0; i < children.length; ++i) {
			if (children[i] instanceof Label) {
				Label label = (Label)children[i];
				if (stringsMatch(_labelName, label.getText()))
					return i;
			}
		}	
		return -1;
	}

}
