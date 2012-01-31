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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.visible;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.condition.MenuItemStateAccessor;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.matchers.MenuItemByPathMatcher;
import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.ShellReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link MenuItem} widgets.
 * <p>
 * Example:
 * <pre>
 * ui.click(new MenuItemLocator("File/Save"));
 * </pre>
 */
public class MenuItemLocator extends SWTWidgetLocator
	implements IItemLocator, IMenuItemLocator, IPathLocator, IsSelected, IsEnabled
{
//	private static final int ENABLEMENT_WAIT_TIMEOUT = 3000;

	//TODO: push AbstractPathLocator up and extend it...
	private static final long serialVersionUID = -571020226870858459L;

	private final String fullPath;

	/**
	 * Create a locator instance.
	 * @param fullPath the path to the menu item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public MenuItemLocator(String fullPath) {
		this(fullPath, null);
	}

	/**
	 * Create a locator instance.
	 * @param fullPath the path to the menu item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public MenuItemLocator(String fullPath, SWTWidgetLocator parent) {
		super(MenuItem.class, parent);
		this.fullPath = fullPath;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		DisplayReference display = DisplayReference.getDefault();
		ShellReference activeShell = display.getActiveShell();
		if (activeShell == null)
			throw new WidgetNotFoundException("No active shell");
		final MenuReference menuBar = activeShell.getMenuBar();
		if (menuBar == null)
			throw new WidgetNotFoundException("No menubar found for active shell");
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return menuBar;
			}
		}, fullPath);
//		String[] items = PathStringTokenizerUtil.tokenize(fullPath);
//		String rootNode = items[0];
//		StringBuffer remainingNodes = new StringBuffer();
//		for (int i=1; i < items.length;) {
//			remainingNodes.append(TextUtils.escapeSlashes(items[i]));
//			if (++i < items.length)
//				remainingNodes.append('/');
//		}
//		WidgetReference root = (WidgetReference) ui.find(new SWTWidgetLocator(MenuItem.class, rootNode));
//		Widget clicked = getMenuSelector(ui).click((MenuItem)root.getWidget(), remainingNodes.toString());
//		return new WidgetReference(clicked);
	}

//	private MenuItemSelector getMenuSelector(final IUIContext ui) {
//		PlaybackSettings settings = getPlaybackSettings(ui);
//		/*
//		 * Overriding click to add a wait for item enablement.
//		 */
//		if (settings != null && settings.getDelayOn())
//			return new MenuItemHighlightingSelector(settings) {
//				public synchronized Widget click(Widget w) {
//					ui.wait(new IsEnabledCondition(new SWTWidgetReference2(w)), ENABLEMENT_WAIT_TIMEOUT);
//					return super.click(w);
//				}
//		};
//		return new MenuItemSelector() {
//			public synchronized Widget click(Widget w) {
//				ui.wait(new IsEnabledCondition(new SWTWidgetReference2(w)), ENABLEMENT_WAIT_TIMEOUT);
//				return super.click(w);
//			}
//		};
//	}
	
//	private PlaybackSettings getPlaybackSettings(IUIContext ui) {
//		return (PlaybackSettings) ui.getAdapter(PlaybackSettings.class);
//	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
//		IWidgetMatcher matcher = new AdapterFactory().adapt(new MenuItemByPathMatcher(fullPath));
//		return new CompoundMatcher(VisibilityMatcher.create(true), matcher);
		return SWTMatcherBuilder.buildMatcher(new MenuItemByPathMatcher(fullPath), visible());
//		return new MenuItemByPathMatcher(fullPath).and(IsVisibleMatcher.forValue(true));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return super.findAll(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IMenuItemLocator#getPath()
	 */
	public String getPath() {
		return fullPath;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		return new MenuItemStateAccessor(getPath()).isSelected(ui, true);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#isEnabled(com.windowtester.runtime.IUIContext)
	 */
	public boolean isEnabled(IUIContext ui) throws WidgetSearchException {
		return new MenuItemStateAccessor(getPath()).isEnabled(ui, true);
	}
	
	@Override
	protected String getToStringDetail() {
		return getPath();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create a condition that tests if the given menu item is selected.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isSelected() {
		return isSelected(true);
	}
	
	/**
	 * Create a condition that tests if the given menu item is selected.
	 * @param selected 
	 * @param expected <code>true</code> if the menu is expected to be selected, else
	 *            <code>false</code>
	 */            
	public IUICondition isSelected(boolean expected) {
		return new IsSelectedCondition(this, expected);
	}

	
	
	/**
	 * Create a condition that tests if the given menu item is enabled.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isEnabled(true)</code>
	 */
	public IUICondition isEnabled() {
		return isEnabled(true);
	}
	
	/**
	 * Create a condition that tests if the given menu item is enabled. 
	 * @param expected <code>true</code> if the menu is expected to be enabled, else
	 *            <code>false</code>
	 */            
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}
	
}
