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
package com.windowtester.runtime.swt.locator;

import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.internal.matchers.ByClassMatcher;
import com.windowtester.runtime.swt.internal.matchers.HasLabelMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;

/**
 * Locates a widget that is immediately adjacent to (e.g., following) 
 * a Label widget with the given label text.
 * <p>
 * For instance, this locator:
 * <pre>
 *    new LabeledLocator(Text.class, "File:");
 * </pre>
 * identifies a Text widget that is preceded by the "File:" label.
 * <p>
 * (A widget w1 is considered to be preceding another widget w2 if they are siblings with the same
 * Composite parent c1 and the index of w1 is just before the index of w2 in c1's list of children.)
 */
public class LabeledLocator extends SWTWidgetLocator
	implements IsEnabled
{
	
	private static final long serialVersionUID = 80238627627779416L;
	
	/**
	 * Create an instance that locates a widget of a given class preceded by a
	 * Label widget with the given text. 
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 */
	public LabeledLocator(Class cls, String labelText) {
		super(cls,labelText);
	}
	
	/**
	 * Create an instance that locates a widget of a given class preceded by a
	 * Label widget with the given text, relative to a given parent.
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 * @param parentLocator the parent locator
	 */
	public LabeledLocator(Class cls, String labelText, SWTWidgetLocator parentLocator) {
		super(cls, labelText, parentLocator);
	}
	
	/**
	 * Create an instance that locates a widget of a given class preceded by a
	 * Label widget with the given text, relative to a given parent.
	 * @param cls class of the widget to match
	 * @param labelText the text of the label preceding it
	 * @param index the index relative to the parent
	 * @param parentLocator the parent locator
	 */
	public LabeledLocator(Class cls, String labelText, int index, SWTWidgetLocator parentLocator) {
		super(cls, labelText, index, parentLocator);
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getToStringDetail()
	 */
	protected String getToStringDetail() {
		//this is a bit silly but we want the target class to display before the label...
		return getTargetClassName() + ", \"" + getNameOrLabel() +"\"";
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
//		 IWidgetMatcher matcher = new AdapterFactory().adapt(new LabeledWidgetMatcher(getTargetClass(), getNameOrLabel()));
//		 //Be sure and add visibility check!

//		 return new CompoundMatcher(matcher, VisibilityMatcher.create(true));
		return new HasLabelMatcher(new ByClassMatcher(getTargetClass()), getNameOrLabel());
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		//sb.append("LabeledLocator(").append(getTargetClass().getName()).append(", \"").append(getNameOrLabel()).append("\"");
		sb.append("LabeledLocator(").append(getTargetClassName()).append(", \"").append(getNameOrLabel()).append("\"");
		WidgetLocator parent = getParentInfo();
		if (parent != null)
			sb.append(", ").append(parent);
		sb.append(")");
		return sb.toString();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given widget is enabled.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isEnabled(true)</code>
	 */
	public IUICondition isEnabled() {
		return isEnabled(true);
	}
	
	/**
	 * Create a condition that tests if the given widget is enabled.
	 * @param selected 
	 * @param expected <code>true</code> if the menu is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */            
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}
	
}