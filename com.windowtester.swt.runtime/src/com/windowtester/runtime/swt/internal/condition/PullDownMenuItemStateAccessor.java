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
package com.windowtester.runtime.swt.internal.condition;

import java.util.concurrent.Callable;

import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.Timer;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;

/**
 * Tests enablement and selection of pulldown menu items.
 *
 */
public class PullDownMenuItemStateAccessor {

	private abstract class MenuAccessor<T> extends MenuDriver {
		
		T state;
		
		@Override
		protected void select(MenuItemReference itemRef) {
			access(itemRef);
			waitForPreviousItemSelection();
			dismissMenu();
		}

		private void access(MenuItemReference itemRef) {
			state = getState(itemRef);
		}

		protected abstract T getState(MenuItemReference itemRef);

		public final T drive() throws WidgetSearchException {
			resolveAndSelect(new Callable<MenuReference>() {
				public MenuReference call() throws Exception {
					return ((ISWTWidgetReference<?>) menuHost).showPulldownMenu(ClickDescription.singleClick());
				}		
			}, path);
			return state;
		}
		
		private void waitForPreviousItemSelection() {
			/*
			 * TODO: refactor to a condition
			 * What we really want here is to wait for the MenuFilter to pick up the previous item selection before we
			 * dismiss the menu.  Unfortunately there is no easy way to get at the underlying Menu operation to get access 
			 * to the filter.  As an interim solution we resort to a pause.
			 */
			new Timer().pause(1000);
		}
		
		private void dismissMenu() {
			DisplayReference.getDefault().closeAllMenus();
		}	
	};
	
	
	
	private final IWidgetReference menuHost;
	private final String path;

	public PullDownMenuItemStateAccessor(IWidgetReference menuHost, String path) {
			this.menuHost = menuHost;
			this.path = path;
	}

	public boolean isSelected(IUIContext ui, final boolean isSelected) throws WidgetSearchException {
		return new MenuAccessor<Boolean>() {
			@Override
			protected Boolean getState(MenuItemReference itemRef) {
				return itemRef.getSelection();
			}
		}.drive().booleanValue() == isSelected;
	}

	public boolean isEnabled(IUIContext ui, boolean isEnabled) throws WidgetSearchException {
		return new MenuAccessor<Boolean>() {
			@Override
			protected Boolean getState(MenuItemReference itemRef) {
				return itemRef.isEnabled();
			}
		}.drive().booleanValue() == isEnabled;
	}

	public int getIndex(IUIContext ui) throws WidgetSearchException {
		return new MenuAccessor<Integer>() {
			@Override
			protected Integer getState(MenuItemReference itemRef) {
				return itemRef.getIndex();
			}
		}.drive().intValue();
	}

	
	
	
}
