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

import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link ToolItem} widgets.
 */
public class ToolItemLocator extends SWTWidgetLocator 
	implements IsEnabled
{
	
	private static final long serialVersionUID = -1978271528107136199L;
		
//	private SWTMatcherBuilder matcherBuilder = new SWTMatcherBuilder();
	
	/**
	 * Create an instance.
	 * @param text the tool item's text
	 *   (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public ToolItemLocator(String text) {
		super(ToolItem.class, text); 
	}
	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
//	 */
//	@Override
//	protected ISWTWidgetMatcher buildMatcher() {
//		matcherBuilder.addCriteria(ofClass(ToolItem.class), visible());
//		return matcherBuilder.build();
//	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#isWidgetEnabled(org.eclipse.swt.widgets.Widget)
	 */
	protected boolean isWidgetEnabled(Widget swtWidget) throws WidgetSearchException {
		if (swtWidget instanceof ToolItem) {
			ToolItem toolItem = (ToolItem) swtWidget;
			return !toolItem.isDisposed() && toolItem.isEnabled();
		}
		return super.isWidgetEnabled(swtWidget);
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

	
	///////////////////////////////////////////////////////////////////////////
	//
	// Match criteria filtering
	//
	///////////////////////////////////////////////////////////////////////////
	

//	public ToolItemLocator named(String name) {
//		matcherBuilder.addCriteria(WidgetMatchers.named(name));
//		return this;
//	}
//
//	public ToolItemLocator in(int index, SWTWidgetLocator parent) {
//		ToolItemLocator locator = new ToolItemLocator(getNameOrLabel());
//		locator.setParentInfo(parent);
//		locator.setIndex(index);
//		locator.matcherBuilder.addCriteria(matcherBuilder.criteria());
//		locator.matcherBuilder.setParent(index, parent.buildMatcher());
//		return locator;
//	}
//
//	public ToolItemLocator in(SWTWidgetLocator parent) {
//		return in(UNASSIGNED, parent);
//	}
//	
	
		
	@Override
	public ToolItemLocator in(SWTWidgetLocator parent) {
		return (ToolItemLocator) super.in(parent);
	}
	
	@Override
	public ToolItemLocator in(int index, SWTWidgetLocator parent) {
		return (ToolItemLocator) super.in(index, parent);
	}
	
	@Override
	public ToolItemLocator named(String name) {
		return (ToolItemLocator) super.named(name);
	}
	
	
}
