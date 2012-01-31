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

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.swt.WidgetLocatorService;

/**
 * A matcher that matches widgets by way of their index in their parent's 
 * list of children.  
 * <p>
 * Note that indexes only matter in the case where there is at least one sibling
 * that matches the target widget exactly (by class and name/label).
 * 
 * @see com.windowtester.swt.WidgetLocatorService
 * 
 * @author Phil Quitslund
 * @deprecated - functionality folded into HierarchyMatcher
 *
 */
public class ParentIndexMatcher implements Matcher {

	private Matcher _matcher;
	private Matcher _parentMatcher;
	private int _index;
	
	public ParentIndexMatcher(Matcher matcher, int index, Matcher parentMatcher) {
		_index = index;
		_parentMatcher= parentMatcher;
		_matcher = matcher;
	}
		
	
	public boolean matches(Widget widget) {
//		boolean matches = false;
//		Widget parent = WidgetLocatorService.getParent(widget);
//		if (parent != null && _parentMatcher.matches(parent) && _matcher.matches(widget)) {
//			_current++;
//            Log.debug("Found match for matcher:\n"+_matcher+"\n Must check index:["+_current+"=="+_index+"]");
//            System.out.println("Found match for matcher:\n"+_matcher+"\n Must check index:["+_current+"=="+_index+"]");
//            
//			if(_current == _index) {
//				matches = true;
//			}  
//		}
		boolean matches = false;
		Widget parent   = new WidgetLocatorService().getParent(widget);
		if (parent != null && _parentMatcher.matches(parent) && _matcher.matches(widget)) {
			int indexRelativeToParent     = getIndex(widget, parent);
			matches = indexRelativeToParent == _index;
		}
		return matches;
	}
    
//    private boolean hasSameParentClass(Widget widget) {
//		Widget parent = WidgetLocatorService.getParent(widget);
//    	Class cls = parent.getClass();
//    	
//    	boolean result = cls.equals(_parentClass);
//    	return result;
//    	//== widget.getClass();
//	}

	public String toString() {
    	return "Index Matcher (" + _matcher + ", " + _index +")";
    }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// FIXME: copied from hiearchyinfoservice rather than chnaging behavior of getIndex, fix hiearchyinfoservice and use it instead
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get this widgets index relative to its parent widget.
	 * <p>Note that indexes only matter in the case where there is at least one sibling
	 * that matches the target widget exactly (by class and name/label).  Other cases
	 * return -1. 
	 * @param w - the widget
	 * @param parent - the parent widget
	 * @return an index, or -1 if is the only child
	 * FIXME: return 0 in only-child case
	 */
	public static int getIndex(Widget w, Widget parent) {
		
		List children = new WidgetLocatorService().getChildren(parent, w.getClass());
		int count = 0;    //the match counter
		int index = -1;   //the index of our target widget
		//only child case...
		if (children.size() == 1)
			return 0; //TODO: this is the RIGHT behavior, no?
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Widget child = (Widget)iter.next();
			
//			if (child.getClass().isAssignableFrom(w.getClass()))
			//using exact matches...
			if (child.getClass().isAssignableFrom(w.getClass()) && w.getClass().isAssignableFrom(child.getClass())) {
				//also check for nameOrLabelMatch (doing a self-check)
//				if ((w != child) && nameAndOrLabelDataMatch(w, child))
//					++count;	
				//also check for nameOrLabelMatch
				if (nameAndOrLabelDataMatch(w, child))
					++count;	
			}
			if (child == w)
				index = count-1; //indexes are zero-indexed
		}
		return (count > 1) ? index : -1;
		//throw new IllegalStateException("unfound child");
	}
	
	/**
	 * Checks to see that widget names/labels match.
	 * @param w1 - the first widget
	 * @param w2 - the second widget
	 * @return true if they match
	 */
	private static boolean nameAndOrLabelDataMatch(Widget w1, Widget w2) {
		String text1 = WidgetLocatorService.getWidgetText(w1);
		String text2 = WidgetLocatorService.getWidgetText(w2);
		if (text1 == null)
			return text2 == null;
		return text1.equals(text2);
	}
}

