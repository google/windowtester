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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import abbot.finder.AWTHierarchy;
import abbot.finder.ComponentFinder;
import abbot.finder.Hierarchy;
import abbot.finder.Matcher;

import com.windowtester.runtime.swing.SwingWidgetLocator;

/***
 *  To build the WidgetLocator based on the hierarchy of the widget.
 *  First, try a simple WidgetLocator, like WidgetLocator(cls). If there
 *  is more than one match with this locator, then elaborate by adding  
 *  parent info. Do this till we get a unique WidgetLocator, or else 
 *  return null, to indicate failure.
 *  
 *  Only the active window is considered when building the WidgetLocator.
 *  
 *  based on com.windowtester.swt.locator.ScopedWidgetIdentifierBuilder
 * 
 */

public class ScopedComponentIdentifierBuilder implements
		IWidgetIdentifierStrategy {
	
	/** For use in checking for unique matches */
	private final ComponentFinder _finder = BasicFinder2.getDefault();

	/** For use in elaboration (created once per call it identify) */
	
	private Hierarchy _hierarchy = AWTHierarchy.getDefault();
	
	/**
	 * Generates a <code>WidgetLocator</code> that uniquely identifies this widget
	 * relative to the current widget hierarchy.  If no uniquely identifying locator is found
	 * <code>null</code> is returned.
	 *  
	 */
	public SwingWidgetLocator identify(Component w) {
		
		// get locator describing the target widget itself
		SwingWidgetLocator locator = getLocator(w);
		// get the top level frame/dailog for the component
//		WidgetLocator scope = findTopLevelScope(w);
//		locator.setParentInfo(scope); //note: it can be null
		
		
		Matcher matcher = MatcherFactory.getMatcher(locator);
		
		//	elaborate until done (notice: null locator indicates a failure)
		// Note: not going to look only in active shell, since the find has not
		// been implemented this way.
	//	while(!isUniquelyIdentifying(matcher, _activeWindow) && locator != null) {
		while(!isUniquelyIdentifying(matcher) && locator != null) {
			locator = elaborate(locator, w);
			if (locator != null)
				matcher = MatcherFactory.getMatcher(locator);

		}
		
		return locator;
	}
	
	/**
	 * Create an (unelaborated) info object for this widget. 
	 * @param w - the widget to describe.
	 * @return an info object that describes the widget.
	 */
	private SwingWidgetLocator getLocator(Component w) {
		
		if (w == null) {
			return null;
		}
		
		
		/**
		 * CCombos require special treatment as the chevron is a button and receives the click event.
		 * Instead of that button, we want to be identifying the combo itself (the button's parent).
		 * TODO!pq: is this true for JCombos?
		 */
		if (w instanceof JButton) {
			Component parent = _hierarchy.getParent(w);
			if (parent instanceof JComboBox)
				w = parent;
		}
		
		// TODO : implement this functionality
		//WidgetLocator locator = checkForLabeledLocatorCase(w);
		
		return WidgetLocatorFactory.getInstance().create(w);
		
	}

	
	/**
	 * Find top-level scope (Frame) -- might be <code>null</code>.
	 */
/*	private WidgetLocator findTopLevelScope(Component w) {
		WidgetLocator rootLocator = null;
		
		Collection roots = _hierarchy.getRoots();
		boolean found = false;
		Iterator it = roots.iterator();
		while (it.hasNext() && !found){
			Object o = it.next();
		//	if (o instanceof java.awt.Container){
			if (((Container)o).isAncestorOf(w)){
				found = true;
				if (o instanceof Frame)
					rootLocator = new WidgetLocator(Frame.class,((Frame)o).getTitle());
				else if (o instanceof Dialog)
					rootLocator = new WidgetLocator(Dialog.class,((Dialog)o).getTitle());
			}
		}
		return rootLocator;
	}
	
*/	
	/**
	 * Does this macther uniquely identify a widget in this Hierarchy
	 * TODO: limit search to active window 
	 */
//	private boolean isUniquelyIdentifying(Matcher matcher, Window window) {
	private boolean isUniquelyIdentifying(Matcher matcher) {
	/*	 try {
			_finder.find(matcher);
			return true;
		} catch (ComponentNotFoundException e) {
			System.out.println("Component not found exception");
			e.printStackTrace();
			// do nothing, return false
		} catch (MultipleComponentsFoundException e) {
			//	do nothing, return false
			System.out.println("multiple Components found exception");
			e.printStackTrace();
		}*/
		int result = ((BasicFinder2)_finder).findAll(matcher);
		if ( result == -1) 
			return false;
		else
			return true;
	}
	
	/**
	 * Takes a WidgetLocator object and elaborates on it until is uniquely identifying.
	 * If no uniquely identifying locator can be inferred, a <code>null</code> value 
	 * is returned.
	 * 
	 */
	private SwingWidgetLocator elaborate(SwingWidgetLocator info, Component w) {
		
		//a pointer to the original locator for returning (in the success case)
		SwingWidgetLocator root = info;

		boolean elaborated = false;
		SwingWidgetLocator parentInfo = null;
		
		while(!elaborated) {

			//get parent info of the current (top-most) locator 
			//note[!pq]: we need this cast but it's safe since swing locators 
			//can only contain other swing locators
			parentInfo = (SwingWidgetLocator) info.getParentInfo();
			//get the parent of the current (top-most) widget in the target's hierarchy
			Component parent = _hierarchy.getParent(w);
			/*
			 * if the parent is null at this point, we've failed to elaborate and we
			 * need to just return
			 */
			if (parent == null) {
				System.out.println("Failed, returning null");
				return null;
			}

			//if the parent is a scope locator, connect to it
			//if (isScopeLocator(parentInfo)) {
			//	handleScopeLocatorCase(info, parentInfo, w, parent);
			//	elaborated = true;
			//if the parentinfo is null, create a new parent and attach it	
			//} else if (parentInfo == null) {
			if (parentInfo == null) {
				info.setParentInfo(getLocator(parent));
				setIndex(info, w, parent);
				elaborated = true;
			}
			
			/*
			 * setup for next iteration
			 */
			w    = parent;
			info = parentInfo;
		} 
		
		return root;
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
	 * Get the children (of a particular class) of a given parent widget.
	 * @param parent - the parent widget
	 * @param cls - the class of child widgets of interest
	 * @return a list of children
	 */
	public List getChildren(Component parent, Class cls) {
		Collection children = _hierarchy.getComponents(parent);
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
	
	private boolean nameAndOrLabelDataMatch(Component c1, Component c2){
		String name1 = c1.getName();
		String name2 = c2.getName();
		
		if ((name1 != null) || (name2 != null)){
			if (name1 == null)
				return name2 == null;
			return name1.equals(name2);
		}
		String text1 = getWidgetText(c1);
		String text2 = getWidgetText(c2);
		if (text1 == null)
			return text2 == null;
		return text1.equals(text2);
		
	}
	
	/**
	 * Set the index for this locator that describes the given widget relative to the given parent.
	 */
	private void setIndex(SwingWidgetLocator locator, Component currentWidget, Component widgetParent) {
		int index = getIndex(currentWidget,widgetParent);
		if (index != SwingWidgetLocator.UNASSIGNED)
			locator.setIndex(index);
	}
	
	/**
	 * Check to see if the given locator is a scope locator.
	 */
/*	private boolean isScopeLocator(WidgetLocator locator) {
		return (locator.getTargetClass()== Frame.class) || 
				(locator.getTargetClass()== Dialog.class);
	}
*/
	/**
	 * Handle case where parent locator is a scoping locator.
	 */
/*	private void handleScopeLocatorCase(WidgetLocator currentTopLocator, WidgetLocator scopeLocator, Component currentWidget, Component widgetParent) {

			//1. create a new parent
			WidgetLocator newParent = getLocator(widgetParent);
			//attatch it to our old top locator
			currentTopLocator.setParentInfo(newParent);
			setIndex(currentTopLocator, currentWidget, widgetParent);
			
			int scopeRelativeIndex = getIndex(currentWidget, scopeLocator);
			if (scopeRelativeIndex != WidgetLocator.UNASSIGNED)
				newParent.setIndex(scopeRelativeIndex);

			newParent.setParentInfo(scopeLocator);	
	}
	
*/
	public int getIndex(Component w, SwingWidgetLocator scopeLocator) {
		//TODO: decide whether we want frame/dialog relative indexes
		return SwingWidgetLocator.UNASSIGNED;
	}

	/**
	 * Extract the text from the given widget.
	 * @param w - the widget in question
	 * @return the widget's text
	 */
	public String getWidgetText(Component w) {

         	
	    	if (w instanceof AbstractButton) {
	    		return (((AbstractButton)w).getText());       	    		
	    	}
		    	
		    if (w instanceof JLabel) {
		    	return (((JLabel)w).getText());       	    		
		    }
		   	   	 
		   	
		    	//fall through ....
		    	return null;
    		
	}
	
}
