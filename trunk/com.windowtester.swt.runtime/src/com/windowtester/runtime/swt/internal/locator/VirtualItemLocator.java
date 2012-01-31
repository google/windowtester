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
package com.windowtester.runtime.swt.internal.locator;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.swt.internal.finder.legacy.InternalMatcherBuilder;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * An SWT locator for locating items that are not actual objects.  (Such
 * as items in a combo, list.)  In contrast, {@link ItemLocator}s locate
 * actual item objects (such as Table and Tree items).
 * 
 * @noextend This class is not intended to be subclassed by clients.
 *
 */
public abstract class VirtualItemLocator extends SWTWidgetLocator implements IItemLocator, IPathLocator, IControlRelativeLocator {

	
	/*
	 * Parents are interepreted slightly differently in the virtual item case
	 * Here they are the actual control (e.g., combo, list, etc.)
	 * As a result we need to pull the immediate parent into the target matcher and pass
	 * it's parentinfo up.
	 */
	
	
	private static final long serialVersionUID = -6848540232007367241L;

	//the locator for the control (e.g., list, combo, etc) containing the virtual item
	protected /*transient*/ SWTWidgetLocator _controlLocator;
	//             ^---- this was breaking the recording story!
	
	//in some cases, the path is not known at construction time and we need to override it
	private String _overridenPath;

	public VirtualItemLocator(Class<?> cls, String text, int index, SWTWidgetLocator parent) {
		super(cls, text, index, getParent(parent));
		_controlLocator = parent;
	}

	private static SWTWidgetLocator getParent(SWTWidgetLocator locator) {
		if (locator == null)
			return null;
		return (SWTWidgetLocator) locator.getParentInfo();
	}

	public VirtualItemLocator(Class<?> cls, int index, SWTWidgetLocator parent) {
		this(cls, null, index, parent);
	}

	public VirtualItemLocator(Class<?> cls, String text, SWTWidgetLocator parent) {
		this(cls, text, UNASSIGNED, parent);
	}

	public VirtualItemLocator(Class<?> cls, String text) {
		this(cls, text, UNASSIGNED, null);
	}

	public VirtualItemLocator(Class<?> cls, SWTWidgetLocator parent) {
		this(cls, UNASSIGNED, parent);
	}

	public VirtualItemLocator(Class<?> cls) {
		this(cls, UNASSIGNED, null);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.locator.IPathLocator#getPath()
	 */
	public String getPath() {
		//path can be set after the fact -- if so, use it -- else default
		if (_overridenPath != null)
			return _overridenPath;
		//default is for the path to be the name text (set at construction time)
		return super.getNameOrLabel();
	}
	
	/**
	 * Override the path set at construction time.
	 */
	public void setPath(String path) {
		_overridenPath = path;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getNameOrLabel()
	 * 
	 * See getPath implementation above, it can be used to get the contents from
	 * the nameOrLabel field.
	 */
	public String getNameOrLabel() {
		//name is NOT set on virtual item locators
		return null;
	}

	protected ISWTWidgetMatcher buildMatcher() {
		/*
		 * 2 cases:
		 * 	    i.)  no parent locator and standard matcher can be used
		 * 		ii.) parent locator is defined to elaborate info (e.g., label) on parent control
		 */
		
		SWTWidgetLocator controlLocator = getControlLocator();
		
		if (controlLocator == null)
			return super.buildMatcher();
		
		//parent case
		
		//This is a bit different than a standard SWTLocator since the parent is not really the parent
		//instead it is the actual widget (e.g. the combo or ccombo or list...)
		//System.out.println("using control locator: " + controlLocator);
	
		return InternalMatcherBuilder.build2(controlLocator);
	}

	
	
	public boolean matches(Object widget) {

		//adaptation to new runtime expectations
		if (widget instanceof Widget)
			widget = WTRuntimeManager.asReference(widget);
		
		//TODO: as a workaround we are regenerating the matcher for each call (not pretty!)
//		return buildMatcher().matches(widget);
		ISWTWidgetMatcher matcher = buildMatcher();
		if (widget instanceof ISWTWidgetReference<?>)
			return matcher.matches((ISWTWidgetReference<?>) widget);
		return false;
	}
	
	
	public void setParentInfo(WidgetLocator parentInfo) {
		_controlLocator = (SWTWidgetLocator) parentInfo;
		super.setParentInfo(parentInfo);
	}
	
	/**
	 * Get the locator that identifies the control containing this virtual item.
	 */
	public SWTWidgetLocator getControlLocator() {
		return _controlLocator;
	}
	
	
//	protected IWidgetMatcher buildMatcher() {
//		
//		//This is a bit different than a standard SWTLocator since the parent is not really the parent
//		//instead it is the actual widget (e.g. the combo or ccombo or list...)
//		
//		/*
//		 * First, query locator for identifying details.
//		 */
//		Class cls                = getTargetClass();
//		String nameOrLabel       = getNameOrLabel();
//		int index                = getIndex();
//		WidgetLocator parentInfo = getParentInfo();
//		
//		
//		/* 
//		 * Next, create the matcher
//		 */
//		IWidgetMatcher matcher = new ExactClassMatcher(cls);
//		if (nameOrLabel != null)
//			matcher = new CompoundMatcher(matcher, TextMatcher.create(nameOrLabel));
//		
//		//add visiblity test:
//		matcher = new CompoundMatcher(matcher, VisibilityMatcher.create(true));
//		
//		if (index != WidgetLocator.UNASSIGNED)
//			matcher = IndexMatcher.create(matcher, index);
//	}
	
	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.WidgetLocator#toString()
//	 */
//	public String toString() {
//
//		String name = getWidgetLocatorStringName();
//		if (name == null)
//			return super.toString(); //for sanity
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append(name).append("(");
//		if (getNameOrLabel() != null)
//			sb.append('\"').append(getNameOrLabel()).append("\", ");
//		
//		if (getControlLocator() != null)
//			sb.append(getControlLocator());
//		
//		sb.append(")");
// 
//		return sb.toString();
//	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getToStringDetail()
	 */
	protected String getToStringDetail() {
		return "\"" + getPath() +"\"";
	}
	
	
}
