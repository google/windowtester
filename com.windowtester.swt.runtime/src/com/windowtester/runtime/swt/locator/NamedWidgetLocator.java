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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.visible;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.withName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.InaccessableWidgetException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.internal.locator.IUnscopedLocator;
import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates named widgets. Widgets are matched based on the "name" data field value.
 * Names are set using the {@link Widget#setData(String, Object)} method (<code>widget.setData("name", "widget.name")</code>.
 */
public class NamedWidgetLocator extends SWTWidgetLocator
	implements IUnscopedLocator, IsEnabled, HasText
{

	private static final long serialVersionUID = -7192638436568956938L;

	/**
	 * Create a locator instance.
	 * @param name the name to match
	 */
	
	public NamedWidgetLocator(String name) {
		super(Widget.class, name); 
	}
	
	/**
	 * Create a locator instance.
	 * @param cls the class of the widget
	 * @param name the name to match
	 */
	// added class to NamedWidgetLocator : 5/2/07 :kp
	
	public NamedWidgetLocator(Class<?> cls,String name) {
		super(cls, name); 
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		
		
//		IWidgetMatcher matcher = new AdapterFactory().adapt(new NameMatcher(getNameOrLabel()));
//		IWidgetMatcher classMatcher = new AdapterFactory().adapt(new ExactClassMatcher(getTargetClass()));
//		// Test type (if specified --- note that Widget is a sentinel)
//		if (!getTargetClass().equals(Widget.class))
//			matcher = new CompoundMatcher(matcher, classMatcher);
//		
//		// Be sure and add visibility check!
//		return new CompoundMatcher(matcher, VisibilityMatcher.create(true));
		
//		WidgetMatcher matcher = withName(getNameOrLabel()).and(IsVisibleMatcher.forValue(true));
//		// Test type (if specified --- note that Widget is a sentinel)
//		if (!getTargetClass().equals(Widget.class))
//			matcher = matcher.and(ofClass(getTargetClass()));
//		return matcher;
		
		SWTMatcherBuilder matcherBuilder = new SWTMatcherBuilder();
		matcherBuilder.specify(withName(getNameOrLabel()), visible());
		// Test type (if specified --- note that Widget is a sentinel)
		if (!getTargetClass().equals(Widget.class))
			matcherBuilder.specify(ofClass(getTargetClass()));
		
		return matcherBuilder.build();
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#toString()
	 */
	public String toString() {
		return "NamedWidgetLocator(\"" + getNameOrLabel() +"\")";
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getWidgetText(org.eclipse.swt.widgets.Control)
	 */
	protected String getWidgetText(Control widget) throws WidgetSearchException {
		
		// Find the method
		
		Method method;
		try {
			method = widget.getClass().getMethod("getText", new Class[] {});
		}
		catch (SecurityException e) {
			throw new InaccessableWidgetException(e);
		}
		catch (NoSuchMethodException e) {
			throw new InaccessableWidgetException(e);
		}
		
		// Invoke the method and return the result
		
		try {
			return (String) method.invoke(widget, new Object[]{});
		}
		catch (IllegalArgumentException e) {
			throw new InaccessableWidgetException(e);
		}
		catch (IllegalAccessException e) {
			throw new InaccessableWidgetException(e);
		}
		catch (InvocationTargetException e) {
			throw new InaccessableWidgetException(e);
		}
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
	
	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
}
