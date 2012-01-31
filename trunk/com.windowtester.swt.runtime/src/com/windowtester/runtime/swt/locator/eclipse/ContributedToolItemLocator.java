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
package com.windowtester.runtime.swt.locator.eclipse;

import java.util.Map;

import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.internal.locator.IUnscopedLocator;
import com.windowtester.runtime.swt.internal.matchers.ContributedToolItemMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link ToolItem} widgets via the contributed tool item's action id.
 * <p>
 * For example, this opens the Eclipse Search Dialog by clicking the tool item associated with 
 * the "org.eclipse.search.ui.openSearchDialog" action id:
 * <pre>
 * IUIContext ui = ...;
 * ui.click(new ContributedToolItemLocator("org.eclipse.search.ui.openSearchDialog"));
 * </pre>
 */
public class ContributedToolItemLocator extends SWTWidgetLocator 
	implements IUnscopedLocator, IsEnabled
{
	
	private static final long serialVersionUID = -1978271528107136199L;
	
	
	/**
	 * Matches parameter maps associated with parameterized commands.
	 * <p>
	 * For example:
	 * <pre>new IParameterMatcher(){
	 *    public boolean matches(Map parameterMap) {				
	 *       Object value = parameterMap.get("my.key");
	 *       return "expected.value".equals(value);
	 *    }
	 * };</pre>
	 * 
	 * will match tool items whose associated command has a 
	 * <em>"my.key"->"expected.value"</em> parameter mapping.
	 * 
	 * <p/>
	 *
	 */
	public static interface IParameterMatcher {
		/**
		 * Test the given parameter map.
		 * @param parameterMap the map to test (note: cannot be <code>null</code>).
		 * @return <code>true</code> if the map matches, <code>false</code> otherwise
		 */
		@SuppressWarnings("unchecked")
		boolean matches(Map parameterMap);
	}
	
	
	private final String id;
	private final IParameterMatcher parameterMatcher;
	
	/**
	 * Create an instance.
	 * @param id the contributed tool item's action id
	 *   (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public ContributedToolItemLocator(String id) {
		this(id, null);
	}
	
	/**
	 * Create an instance.
	 * @param id the contributed tool item's action id
	 *   (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parameterMatcher a matcher for matching parameters (if specified in the contribution)
	 */
	public ContributedToolItemLocator(String id, IParameterMatcher parameterMatcher) {
		super(ToolItem.class, id); //putting id in label for use in codegen
		if (id == null)
			throw new IllegalArgumentException("id must not be null");
		this.id = id;
		this.parameterMatcher = parameterMatcher;
	}
	
	
	
	/**
	 * Get the contributed tool item's action id.
	 * @return the String id
	 */
	public String getID() {
		return id;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	@Override
	protected ISWTWidgetMatcher buildMatcher() {		
		return new ContributedToolItemMatcher(id, parameterMatcher);
	}
	
	/**
	 * Get the action id associated with this tool item.
	 */
	public static String getAssociatedContributionID(ToolItem item) {
		return new ToolItemReference(item).getActionDefinitionId();
	}
	
	
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
	
}
