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
package com.windowtester.runtime.swt.internal.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.finder.WidgetLocatorService;

/**
 * 
 * A matcher that matches widgets first against a target matcher (which might match
 * a given class and possibly name or label) and then by checking that parent criteria are met.
 * <p>
 * HierarchyMatchers are handy in making matches based on a widget's location in the 
 * widget hierarchy.
 * 
 * For instance, to match a Text widget contained in the Group labeled "guests"
 * in a Shell called "Party Planner", we might write a HierarchyMatcher like this:<p>
 * 
 * <pre>
 * new HierarchyMatcher(Text.class,
 *			new HierarchyMatcher(Group.class, "guests",
 *				new NameMatcher("Party Planner", Shell.class)));
 * 
 * </pre>
 * 
 * Where containment is not enough, we can augment with indexes.  For example,
 * suppose we want the second Text widget (indexes are zero-indexed):
 *
 * <pre>
 *    - group: -------------------
 *   |     Text   Text     Text   |
 *    ----------------------------
 *
 *   new HierarchyMatcher(Text.class, 1, 
 *           new HierarchyMatcher(Group.class, "group"));
 * </pre>
 * 
 * <b>Note:</b> using the 0-index for an "only child" will not have the desired effect.  In fact, it will fail.
 * Only-child matches should NOT specify an index value (though they could use -1).
 * <p>
 * In other words, <code>new HierarchyMatcher(Text.class, 0, new HierarchyMatcher(Group.class))</code>, will match the first Text 
 * child of a Group but only in the event that that Text has siblings.  To match a Text only-child of a group,
 * use a matcher constructs like this <code>new HierarchyMatcher(Text.class, Group.class)</code> (or possibly
 * like this <code>new HierarchyMatcher(Text.class, -1, Group.class)</code>).
 *
 */
public class SWTHierarchyMatcher implements IWidgetMatcher {

	/** A matcher composed from target class and name info */
	private final IWidgetMatcher _matcher; 
	/** A matcher to check parent criteria. */
	private final IWidgetMatcher _parentMatcher;
	/** The index that identifies the target with respect to its siblings in 
	 *  the parent's list of children.
	 */
	private int _index;
	
	/** The default index value for an unspecified index */
	private static final int DEFAULT_INDEX = -1;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create an instance.
	 * @param targetMatcher - a matcher to check target criteria.
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public SWTHierarchyMatcher(IWidgetMatcher targetMatcher, IWidgetMatcher parentMatcher) {
		this(targetMatcher, DEFAULT_INDEX, parentMatcher);
	}

	/**
	 * Create an instance.
	 * @param targetMatcher - a matcher to check target criteria.
	 * @param index - index that locates child with respect to its parent (in the parent's
	 *                list of children).
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public SWTHierarchyMatcher(IWidgetMatcher targetMatcher, int index, IWidgetMatcher parentMatcher) {
		_matcher       = targetMatcher;
		_index         = index;
		_parentMatcher = parentMatcher;
	}
	
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Matching
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		if (!(widget instanceof Widget))
			return false;
		return matches((Widget)widget);
	}
	
	/**
	 * Check for a match by first checking if the class (and possibly name or label)
	 * match and then checking that parent criteria are met.
	 * @see com.windowtester.runtime.swt.widgets.IWidgetMatcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(Widget widget) {		
		
		//is this log-worthy?
		if (widget == null)
			return false;
		
		/* * various fast-fail optimizations
		 */
		
		//check target matcher
		if (!_matcher.matches(widget))
			return false; //fast fail
		
		//if target matches, turn to parent
		
		//first, short-circuit if there is no parent matcher
		if (_parentMatcher == null)
			return true;
		
		//next, check parent matcher
		
		WidgetLocatorService infoService = new WidgetLocatorService();
		Widget parent   = infoService.getParent(widget);
		
		if (parent == null)
			return false;    //if there is no parent, but there is a matcher, return false
		
		if (!_parentMatcher.matches(parent))
			return false;   //fail if parent does not match
		
		//lastly, check index
		return testIndex(widget, infoService, parent);
	}

	private boolean testIndex(Widget widget, WidgetLocatorService infoService, Widget parent) {
		//NOTE: some matchers override the infoService indexer...
		if (_parentMatcher instanceof IComponentIndexer) {
			//in case no index is assigned, any index match will do
			//this handles the case where a user wants ALL children of a widget
			
			//TODO: this logic might apply to the general case as well...
			if (_index == DEFAULT_INDEX)
				return true;
			return ((IComponentIndexer)_parentMatcher).getIndex(widget, _matcher) == _index;
		}
		// check if there is an index , only then do a match
		if (_index == DEFAULT_INDEX)
			return true;
		return infoService.getIndex(widget, parent) == _index;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get the matcher identifying the target of this match.
	 */
	public IWidgetMatcher getTargetMatcher() {
		return _matcher;
	}
	
	/**
	 * Get the matcher identifying the parent of the target of this match.
	 */
	public IWidgetMatcher getParentMatcher() {
		return _parentMatcher;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Hierarchy matcher (" + _matcher + ", " +  _parentMatcher + ")";
    }

}
