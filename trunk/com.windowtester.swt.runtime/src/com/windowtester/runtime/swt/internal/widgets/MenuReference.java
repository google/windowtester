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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

/**
 * A {@link Menu} reference.
 */
public class MenuReference extends SWTWidgetReference<Menu> {

	public MenuReference(Menu menu) {
		super(menu);
	}

	/**
	 * Gets the menu item count.
	 * 
	 * @return the number of items in the menu.
	 */
	public int getItemCount() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getItemCount();
			}
		});
	}

	public boolean isEnabled() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.isEnabled();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<? extends Composite> getParent() {
		// TODO[pq]: this could actually return a Decorations
		return displayRef.execute(new Callable<CompositeReference<Composite>>() {
			public CompositeReference<Composite> call() throws Exception {
				// TODO[pq]: move this logic out of SWTHierarchyHelper
				Widget parent = new SWTHierarchyHelper(displayRef.getDisplay()).getParent(widget);
				if (parent == null)
					return null;
				return new CompositeReference<Composite>((Composite) parent);
			}
		});
	}
	
	/**
	 * Proxy for {@link Menu#getItems()}.
	 */
	public MenuItemReference[] getItems() {
		return displayRef.execute(new Callable<MenuItemReference[]>() {
			public MenuItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), MenuItemReference.class);
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getParentItem()}.
	 */
	public MenuItemReference getParentItem() {
		return displayRef.execute(new Callable<MenuItemReference>() {
			public MenuItemReference call() throws Exception {
				MenuItem parentItem = widget.getParentItem();
				if (parentItem == null)
					return null;
//				return new MenuItemReference(parentItem);
				return (MenuItemReference) WTRuntimeManager.asReference(parentItem);
			}
		});
	}
	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#accept(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
//	 */
//	@Override
//	public void accept(SWTWidgetReference.Visitor visitor) {
//		visitor.visit(this);
//		visitor.visitEnter(this);
//		for (MenuItemReference item: getItems())
//			item.accept(visitor);
//		visitor.visitLeave(this);
//	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getItems());
	}
}
