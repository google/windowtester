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
package com.windowtester.finder.matchers.swt;

import abbot.finder.swt.Matcher;

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
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * 
 * @author Phil Quitslund
 *
 */
public class HierarchyMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.HierarchyMatcher {

	
	/**
	 * Create an instance.
	 * @param targetMatcher - a matcher to check target criteria.
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public HierarchyMatcher(Matcher targetMatcher, Matcher parentMatcher) {
		super(targetMatcher, parentMatcher);
	}

	/**
	 * Create an instance.
	 * @param targetMatcher - a matcher to check target criteria.
	 * @param index - index that locates child with respect to its parent (in the parent's
	 *                list of children).
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public HierarchyMatcher(Matcher targetMatcher, int index, Matcher parentMatcher) {
		super(targetMatcher, index, parentMatcher);
	}
	
	
	//convenience constructors
	
	/**
	 * Create an instance.
	 * @param cls - the class of the widget in question.
	 * @param nameOrLabel - the widget's name or label.
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public HierarchyMatcher(Class cls, String nameOrLabel, Matcher parentMatcher) {
		super(cls, nameOrLabel, parentMatcher);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the class of the widget in question.
	 * @param nameOrLabel - the widget's name or label.
	 * @param index - the index of the widget in question.
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public HierarchyMatcher(Class cls, String nameOrLabel, int index, Matcher parentMatcher) {
		super(cls, nameOrLabel, index, parentMatcher);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the class of the widget in question.
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public HierarchyMatcher(Class cls, Matcher parentMatcher) {
		super(cls, parentMatcher);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the class of the widget in question.
	 * @param index - the index of the widget in question.
	 * @param parentMatcher - a matcher to check parent criteria.
	 */
	public HierarchyMatcher(Class cls, int index, Matcher parentMatcher) {
		super(cls, index, parentMatcher);
	}    
	

}
