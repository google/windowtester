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

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.ViewPart;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasIndex;
import com.windowtester.runtime.condition.HasIndexCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.condition.PullDownMenuItemStateAccessor;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.locator.IControlRelativeLocator;
import com.windowtester.runtime.swt.internal.widgets.CTabFolderReference;
import com.windowtester.runtime.swt.internal.widgets.CTabItemReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.swt.internal.widgets.ViewReference;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.util.ScreenCapture;

/**
 * Locates {@link MenuItem} widgets in {@link ToolItem} and view pull-downs.
 * <p>
 * Example tool item use:
 * <pre>   ui.click(new PullDownMenuItemLocator("Project...", new ContributedToolItemLocator("newWizardDropDown")));</pre>
 * Selects the "Project..." menu item in the "New Wizard" action tool item contribution.
 * <p>
 * Example view use:
 * <pre>   		ui.click(new PullDownMenuItemLocator("&Filters...", new ViewLocator("org.eclipse.ui.views.ResourceNavigator")));</pre>
 * Selects the "Filters..." menu item in the Navigator view.
 * <p>
 * <b>Note:</b> View support is provisional and requires some fancy and dangerous (read: internal eclipse API) footwork.
 * If the internal eclipse API changes this functionality MAY break.
 * 
 */
public class PullDownMenuItemLocator extends SWTWidgetLocator implements IPathLocator, IControlRelativeLocator, IsSelected, IsEnabled, HasIndex {

	private static final long serialVersionUID = -5770390916590164722L;
	
	private final String _menuItemPath;
	private final SWTWidgetLocator _controlLocator;
	
	/**
	 * Create a locator that selects the given menupath in the pulldown menu for the given host locator
	 * @param menuItemPath the path of the menu item to select
	 * @param pullDownHost the pull down control host locator (note: this MUST resolve to a ToolItem or a View)
	 */
	public PullDownMenuItemLocator(String menuItemPath, SWTWidgetLocator pullDownHost) {
		super(MenuItem.class); //ignored
		_menuItemPath   = menuItemPath;
		_controlLocator = pullDownHost;
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	@Override
	public IWidgetLocator[] findAll(IUIContext ui) {
		SWTWidgetLocator controlLocator = getControlLocator();
		//in the view locator case we can just return the associated control
		if (controlLocator instanceof ViewLocator) {
			IWidgetLocator ref = WidgetReference.create(ViewFinder.getViewControl(((ViewLocator)controlLocator).getViewId()));
			return new IWidgetLocator[]{ref};
		}
		return controlLocator.findAll(ui);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		//visibility check first and foremost
//		if (!isVisible(widget))
//			return false;
		//matching to find the tool item
		return getControlLocator().matches(widget);
	}


	private boolean isVisible(Object widget) {
		return SWTHierarchyHelper.isVisible((Widget)widget);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, final IClickDescription click) throws WidgetSearchException {
		final IWidgetReference widgetToSelect;
		if (getControlLocator() instanceof ViewLocator) {
			widgetToSelect = getToolItemReference(ui);
		}else {
			widgetToSelect = getMenuHost(widget);
		}
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return ((ISWTWidgetReference<?>) widgetToSelect).showPulldownMenu(click);
			}
		}, getPath());
		
//		openMenu(widget);
//		//N.B. the passed in ref is ignored in the MenuItemLocator impl.
//		return new MenuItemLocator(getPath()).click(ui, widget /* ignored */, click);
		
//		SWTLocation location = getClickLocation(ref);
//		Widget clicked = new MenuDriver().select(location, WT.BUTTON1, getPath());
//		return new WidgetReference(clicked);
		
//		throw new RuntimeException("Not implemented");
	}


	private IWidgetReference getMenuHost(IWidgetReference widget)
			throws WidgetNotFoundException {
		final Object ref = widget.getWidget();
		if (!isVisible(ref)) {
			ScreenCapture.createScreenCapture();
			throw new WidgetNotFoundException("Menu host not visible");
		}

		/*
		 * TODO It seems like we should have a first class ViewReference instance here that provides
		 * atomic operations on a view and implements ISWTWidgetReferenceWithPullDownMenu
		 * Also, it seems like this method should be called with widget = this new ViewReference
		 * and thus our finder story should translate ViewLocator into ViewReference.
		 * Below is a hack to see/show how this new ViewReference concept would work for this method
		 * If we don't have a first class ViewReference, then this code needs to be moved somewhere else
		 * because it really does not belong here
		 */
		if (_controlLocator instanceof ViewLocator)
			widget = new ViewReference(((ViewLocator) _controlLocator).getViewId());
		
		final IWidgetReference widgetToSelect = widget;
		return widgetToSelect;
	}

	private IWidgetReference getMenuHost(IUIContext ui)
			throws WidgetSearchException, WidgetNotFoundException {
		IWidgetReference widget = (IWidgetReference) ui.find(this);
		final IWidgetReference menuHost;
		if (getControlLocator() instanceof ViewLocator) {
			menuHost = getToolItemReference(ui);
		}else {
			menuHost = getMenuHost(widget);
		}
		return menuHost;
	}
	
	
//	private SWTLocation getClickLocation(final Object ref) {
//		if (ref instanceof ToolItem) {
//			return new SWTWidgetLocation((ToolItem) ref, WTInternal.RIGHT).offset(-3, 0);
//		}
//		if (_controlLocator instanceof ViewLocator) {
////			ViewPullDownSelector vpdSelector = new ViewPullDownSelector((ViewLocator)_controlLocator);
////			IViewReference viewRef = vpdSelector.getViewRef(vpdSelector.getViewId());
////			IViewPart viewPart = viewRef.getView(true);
////			IMenuManager menuManager = viewPart.getViewSite().getActionBars().getMenuManager();
////			final Control[] parent = new Control[1];
////			Display.getDefault().syncExec(new Runnable() {
////				public void run() {
////					parent[0] = ((Composite) ref).getParent();
////				}
////			});
////			return new SWTWidgetLocation(parent[0], WTInternal.TOPRIGHT);
//			return new SWTWidgetLocation((Widget) ref, WTInternal.TOPRIGHT).offset(-5, -5);
//		}
//		throw new RuntimeException("Not implemented");
//	}

//	private void openMenu(IWidgetReference widget) {
//		SWTWidgetLocator controlLocator = getControlLocator();
//		if (controlLocator instanceof ViewLocator) {
//			new ViewPullDownSelector((ViewLocator)controlLocator).openMenu();
//		} else {
//			ToolItem item = getToolItem(widget); 
//			new ToolItemSelector().clickExpand(item);
//		}
//	}

	/**
	 * Returns "ViewMenu" ToolItemReference <br/>
	 * Works only with Eclipse 4.x!
	 *  
	 * @return
	 */
	private ToolItemReference getToolItemReference(IUIContext ui) throws WidgetSearchException {
		SWTWidgetLocator controlLocator = getControlLocator();
		if (controlLocator instanceof ViewLocator) {
			ViewLocator viewLocator = (ViewLocator) controlLocator;
			
			String viewName = viewLocator.getViewId();
			ViewPart viewPart = (ViewPart) ViewFinder.getViewPart(viewName);
			String partName = viewPart.getPartName();
			
			CTabItemLocator cTabItemLocator = new CTabItemLocator(partName);
			final CTabItemReference tabItemRef = (CTabItemReference) ui.find(cTabItemLocator);
			
			CTabFolderReference parent = tabItemRef.getParent();
			ToolItemReference viewMenu = parent.getViewMenu();
			
			return viewMenu;
		} else {
			System.out.println("No viewlocator found");
			return null;
		}
	}
	
	
//	private ToolItem getToolItem(IWidgetReference ref) {
//		Object widget = ref.getWidget();
//		if (!(widget instanceof ToolItem))
//			throw new IllegalArgumentException("target widget must be a toolitem, got: " + widget);
//		return (ToolItem)widget;
//	}

	/**
	 * Get the associated host control locator.
	 */
	public SWTWidgetLocator getControlLocator() {
		return _controlLocator;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IPathLocator#getPath()
	 */
	public String getPath() {
		return _menuItemPath;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Create a condition that tests if the given menu item has an expected index 
	 * in its parent menu.
	 * @param expectedIndex the expected index of the item
	 */
	public IUICondition hasIndex(int expectedIndex){
		return new HasIndexCondition(this, expectedIndex);
	}
		
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
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		IWidgetReference menuHost = getMenuHost(ui);
		return new PullDownMenuItemStateAccessor(menuHost, getPath()).isSelected(ui, true);
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#isEnabled(com.windowtester.runtime.IUIContext)
	 */
	public boolean isEnabled(IUIContext ui) throws WidgetSearchException {
		IWidgetReference menuHost = getMenuHost(ui);
		return new PullDownMenuItemStateAccessor(menuHost, getPath()).isEnabled(ui, true);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.HasIndex#getIndex(com.windowtester.runtime.IUIContext)
	 */
	public int getIndex(IUIContext ui) throws WidgetSearchException {
		IWidgetReference menuHost = getMenuHost(ui);
		return new PullDownMenuItemStateAccessor(menuHost, getPath()).getIndex(ui);
	}
	
	
}
