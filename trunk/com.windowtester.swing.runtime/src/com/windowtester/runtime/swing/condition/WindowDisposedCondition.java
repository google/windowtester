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
package com.windowtester.runtime.swing.condition;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;

import abbot.finder.AWTHierarchy;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.util.StringComparator;

/**
 * Tests whether a given {@link Window} has been disposed.
 */
public class WindowDisposedCondition implements ICondition {

private String _windowName;
	
	/**
	 * Creates an instance
	 * @param title the title of the Window that is to be checked 
	 */
	public WindowDisposedCondition(String title){
		_windowName = title;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.condition.ICondition#test()
	 */
	public boolean test() {
		return assertComponentNotShowing(_windowName);
	}
	
	boolean boolT;
	
	private synchronized boolean assertComponentNotShowing(final String title){
		boolT = true;

		ArrayList componentList = new ArrayList();
		AWTHierarchy h = new AWTHierarchy();
		final Iterator rootIter = h.getRoots().iterator();
		while (rootIter.hasNext()) {
			componentList.addAll(h.getComponents((Component)rootIter.next()));
		}
		componentList.addAll(h.getRoots());

		final Iterator compIter = componentList.iterator();
		while (compIter.hasNext()) {
			Component c = (Component)compIter.next();
			if (c instanceof Frame){
				Frame w = (Frame)c;
				//if ( w.isDisplayable() && (w.getTitle().equals(title))) {
				if ( w.isDisplayable() && StringComparator.matches(w.getTitle(),title)) {	
					boolT = !w.isVisible();
				}
			}
			if (c instanceof Dialog){
				Dialog d = (Dialog)c;
				//if ( d.isDisplayable() && (d.getTitle().equals(title))) {
				if ( d.isDisplayable() && StringComparator.matches(d.getTitle(),title)) {
					boolT = !d.isVisible();
				}
			}
		}
		return boolT;
	}
	
}
