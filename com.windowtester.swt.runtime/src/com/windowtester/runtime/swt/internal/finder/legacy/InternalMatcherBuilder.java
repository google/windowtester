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
package com.windowtester.runtime.swt.internal.finder.legacy;

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.isVisible;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.withText;

import java.lang.reflect.Method;

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.finder.matchers.eclipse.SectionComponentMatcher;
import com.windowtester.runtime.swt.internal.matchers.WidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.SectionLocator;



/**
 * A matcher building factory.
 * <br>
 * <br>
 * <b>For Internal Use Only.</b>  This class requires package access to locators so it must be in the API 
 * locator package despite that fact that it is for internal use only.
 *
 */
@Deprecated
public class InternalMatcherBuilder {

//	public static IWidgetMatcher build(SWTWidgetLocator locator) {
//		
//		//note that labeled locators get special treatment
//		if (locator instanceof LabeledLocator) {
//			return locator.buildMatcher();
//		}
//		//as do named widget locators
//		if (locator instanceof NamedWidgetLocator) {
//			return locator.buildMatcher();
//		}
//
//		if (locator instanceof SWTWidgetReference)
//			return (IWidgetMatcher)locator;  //references do their own matching
//		
//		/*
//		 * First, query locator for identifying details.
//		 */
//		Class<?> cls                 = locator.getTargetClass();
//		String nameOrLabel           = locator.getNameOrLabel();
//		int index                    = locator.getIndex();
//		IWidgetMatcher<?> parentInfo = locator.getParentInfo();
//		
//		//a special case for sections which as parents perform component matching...
//		//TODO: if there are more cases, consider adding a new interface IComponentMatcher
//		if (parentInfo instanceof SectionLocator)
//			parentInfo = SectionComponentMatcher.forLocator((SectionLocator)parentInfo);
//		
//		
//		/* 
//		 * Next, create the matcher
//		 */
//		
//		IWidgetMatcher matcher = new ExactClassMatcher(cls);
//		if (nameOrLabel != null)
//			matcher = new CompoundMatcher(matcher, TextMatcher.create(nameOrLabel));
//		
//		//add visibility test:
//		matcher = new CompoundMatcher(matcher, VisibilityMatcher.create(true));
//				
//		//add hierarchy matching criteria
//		if (parentInfo != null)
//			matcher = new SWTHierarchyMatcher(matcher, index, parentInfo);
//
//		return matcher;
//		
//
//		
//	}

	@Deprecated
	public static ISWTWidgetMatcher build2(SWTWidgetLocator locator) {
		
		if (locator instanceof NamedWidgetLocator) {
			//we need to do this since buildMatcher is protected
			Method method;
			try {
				method = locator.getClass().getDeclaredMethod("buildMatcher", (Class<?>[])null);
				method.setAccessible(true);
				return (ISWTWidgetMatcher) method.invoke(locator, (Object[])null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		/*
		 * First, query locator for identifying details.
		 */
		Class<?> cls                 = locator.getTargetClass();
		String nameOrLabel           = locator.getNameOrLabel();
		int index                    = locator.getIndex();
		IWidgetMatcher<?> parentInfo = locator.getParentInfo();
		
		//a special case for sections which as parents perform component matching...
		//TODO: if there are more cases, consider adding a new interface IComponentMatcher
		if (parentInfo instanceof SectionLocator)
			parentInfo = SectionComponentMatcher.forLocator((SectionLocator)parentInfo);
		
		WidgetMatcher m = ofClass(cls).and(isVisible());
		if (nameOrLabel != null)
			m = m.and(withText(nameOrLabel));
		
		if (parentInfo != null)
			return m.in(index, adaptToMatcher(parentInfo));
		
		return m;
		
	}

	@SuppressWarnings("unchecked")
	public static ISWTWidgetMatcher adaptToMatcher(final IWidgetMatcher matcher) {
		if (matcher instanceof ISWTWidgetMatcher)
			return (ISWTWidgetMatcher) matcher;
		return new ISWTWidgetMatcher() {			
			public boolean matches(ISWTWidgetReference<?> widget) {
				return matcher.matches(widget);
			}
		};

	}

}
