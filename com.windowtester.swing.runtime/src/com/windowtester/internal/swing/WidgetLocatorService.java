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
package com.windowtester.internal.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import abbot.finder.Matcher;

import com.windowtester.internal.swing.locator.IWidgetIdentifierStrategy;
import com.windowtester.internal.swing.locator.MatcherFactory;
import com.windowtester.internal.swing.locator.ScopedComponentIdentifierBuilder;
import com.windowtester.runtime.swing.SwingWidgetLocator;


/**
* A service/factory class that performs various widget querying services and
* performs identifying widget info inference.
* <br>
* Note: instances cache results of calculations.  If the hierarchy changes between uses, results may be 
* invalid.  In cases where the hierarchy is changing, a new instance must be created.
* 
*/

public class WidgetLocatorService {

	/**
	 * To properly connect menus with their owners we need to do an exhaustive search or widgets looking at their
	 * children.  To disable this, set this flag to false.
	 * TODO: a future approach might exploit caching...
	 */
	private static final boolean FIND_OWNER_ENABLED = true;
	
//	a list of keys which we want to propagate to locators
	private static final String[] INTERESTING_KEYS = {};
	
	private IWidgetIdentifierStrategy _widgetIdentifier = new ScopedComponentIdentifierBuilder();
		
	/**
	 * Generate a Matcher that can be used to identify the widget described
	 * by this WidgetLocator object.
	 * @return a Matcher that matches this object.
	 * @see Matcher
	 */
	public static Matcher getMatcher(SwingWidgetLocator wl) {
		
		return MatcherFactory.getMatcher(wl);
	}

	/**
	 * Get this widget's index relative to its parent widget.
	 * <p>Note that indexes only matter in the case where there is at least one sibling
	 * that matches the target widget exactly (by class and name/label).  Other cases
	 * return -1. 
	 * @param w - the widget
	 * @param parent - the parent widget
	 * @return an index, or -1 if is the only child
	 * FIXME: return 0 in only-child case
	 */
	public int getIndex(Component w, Component parent) {
		
		List children = getChildren(parent, w.getClass());
		int count =  0;   //the match counter
		int index = -1;   //the index of our target widget
		//only child case...
		if (children.size() == 1)
			return index;
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Component child = (Component)iter.next();
			
			//using exact matches...
			if (child.getClass().isAssignableFrom(w.getClass()) && w.getClass().isAssignableFrom(child.getClass())) {
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
	private boolean nameAndOrLabelDataMatch(Component w1, Component w2) {
		String text1 = getWidgetText(w1);
		String text2 = getWidgetText(w2);
		if (text1 == null)
			return text2 == null;
		return text1.equals(text2);
	}

	/**
	 * Get the children (of a particular class) of a given parent widget.
	 * @param parent - the parent widget
	 * @param cls - the class of child widgets of interest
	 * @return a list of children
	 */
	public List getChildren(Component parent, Class cls) {
		
		List children = new ArrayList();
		if (parent instanceof Container){
				Component[] components = ((Container)parent).getComponents();
				addCheck(children,Arrays.asList(components));
		}
		
		 
		//prune non-exact class matches
		List pruned = new ArrayList();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Object child = iter.next();
			Class childClass = child.getClass();
			if (cls.isAssignableFrom(childClass) && childClass.isAssignableFrom(cls))
				pruned.add(child);
		}
		return pruned;
	}
	

	/**
	 * Add the contents of this collection to this other collection only if 
	 * it is non-empty.
	 * @param dest - the destination collection
	 * @param src - the source collection
	 */
	private void addCheck(Collection dest, Collection src) {
		/* add object to collection if non-null */
		if (src.size() > 0) {
			// Iterator iter = src.iterator();
			// while (iter.hasNext()) {
			// dest.addAll(getWidgets((Widget)iter.next()));
			// }
			dest.addAll(src);
		}
	}
	
	/**
	 * Extract the text from the given widget.
	 * @param w - the widget in question
	 * @return the widget's text
	 */
	public String getWidgetText(Component w) {

         	
	    	if ((w instanceof AbstractButton) && !(w instanceof JMenuItem)) {
	    		return (((AbstractButton)w).getText());       	    		
	    	}
		    	
		    if (w instanceof JLabel) {
		    	return (((JLabel)w).getText());       	    		
		    }
		   	   	 
		   	
		    	//fall through ....
		    	return null;
    		
	}
	
	/**
	 * Create an (unelaborated) info object for this widget. 
	 * @param w - the widget to describe.
	 * @return an info object that describes the widget.
	 */
	private SwingWidgetLocator getInfo(Component w) {
		if (w == null) {
			//return new WidgetLocator(NullParent.class);//TODO: handle NullParent case...
			return null;
		}
		/**
		 * CCombos require special treatment as the chevron is a button and receives the click event.
		 * Instead of that button, we want to be identifying the combo itself (the button's parent).
		 */
//		if (w instanceof Button) {
//			Widget parent = new ButtonTester().getParent((Button)w);
//			if (parent instanceof CCombo)
//				w = parent;
//		}
		
		Class cls   = w.getClass();
		/**
		 * We don't want the combo text to be part of the identifying information since it
		 * is only set to the value AFTER it is selected...
		 * Text values are also too volatile to use as identifiers.
		 * 
		 */
//		String text = (w instanceof Combo || w instanceof CCombo || w instanceof Text || w instanceof StyledText)? null : getWidgetText(w);
		String text = getWidgetText(w);
		SwingWidgetLocator locator = (text != null) ? new SwingWidgetLocator(cls, text)
				: new SwingWidgetLocator(cls);
		
		setDataValues(locator, w);
		
		return locator;
	}

	
//	propagate values of interest from the widget to the locator
	private void setDataValues(SwingWidgetLocator locator, Component w) {
		String key;
		Object value = null;

		for (int i= 0; i < INTERESTING_KEYS.length; ++i) {
			key = INTERESTING_KEYS[i];
			if (w instanceof JComponent)
				value = ((JComponent)w).getClientProperty(key);
			if (value != null)
				locator.setData(key, value.toString());
		}
	}
	
	
	/**
	 * Given a widget, infers the (minimal) WidgetLocator that uniquely
	 * identifies the widget.
	 * @param w - the target widget
	 * @return the identifying WidgetLocator or null if there was an error in identification
	 */
	public SwingWidgetLocator inferIdentifyingInfo(Component w) {
		
		/*
		 * pulling inference into separate strategy
		 */
		return _widgetIdentifier.identify(w);
	}

		

}
