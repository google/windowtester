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

import static com.windowtester.internal.runtime.util.ReflectionUtils.invoke;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.operation.SWTMenuItemOperation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.util.TextUtils;

/**
 * A {@link MenuItem} reference.
 */
public class MenuItemReference extends ItemReference<MenuItem>
{

	public static final String DEFAULT_MENUITEM_PATH_DELIMITER = "/";

	public MenuItemReference(MenuItem item) {
		super(item);
	}

	public boolean isEnabled() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.isEnabled();
			}
		});
	}

	public boolean getEnabled() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getEnabled();
			}
		});
	}

	public boolean getSelection() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getSelection();
			}
		});
	}


	public int getIndex() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				Menu parent = widget.getParent();
				MenuItem[] items = parent.getItems();
				for (int i=0; i < items.length; ++i){
					if (items[i] == widget)
						return i;
				}
				return -1; //indicates failure (should an exception be thrown here or further up?
			}
		});
	}
	
	
	/**
	 * Proxy for {@link MenuItem#setSelection(boolean)}.
	 */
	public void setSelection(final boolean selected) {
		displayRef.execute(new VoidCallable() {
			public void call() throws Exception {
				widget.setSelection(selected);
			}
		});
	}
	
	/**
	 * Proxy for {@link MenuItem#getMenu()}.
	 */
	public MenuReference getMenu() {
		return displayRef.execute(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return asReferenceOfType(widget.getMenu(), MenuReference.class);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				Menu menu = widget.getParent();
				return (menu.getStyle() & SWT.BAR) != 0 || menu.isVisible();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public MenuReference getParent() {
		return displayRef.execute(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return new MenuReference(widget.getParent());
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getBounds()
	 */
	public Rectangle getDisplayBounds() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				try {
					Rectangle bounds = (Rectangle) invoke(widget, "getBounds");
					Menu parent = (Menu) invoke(widget, "getParent");
					Rectangle parentBounds = (Rectangle) invoke(parent, "getBounds");
					bounds.x += parentBounds.x;
					bounds.y += parentBounds.y;
					return bounds;
				}
				catch (Exception e) {
					throw new RuntimeException("Failed to calculate bounds", e);
				}
			}
		});
	}

	//	/* (non-Javadoc)
	//	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#accept(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
	//	 */
	//	@Override
	//	public void accept(SWTWidgetReference.Visitor visitor) {
	//		visitor.visit(this);
	//		MenuReference menu = getMenu();
	//		if (menu == null)
	//			return;
	//		visitor.visitEnter(menu);
	//		menu.accept(visitor);
	//		visitor.visitLeave(menu);
	//	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getMenu());
	}

	public String getPathString() {
		String path = "";
		MenuItemReference item = this;
		MenuReference parent;
		while (item != null) {
			//!pq: adding fix to escape use of delimiter in menu items
			path = TextUtils.escapeSlashes(item.getText()) + DEFAULT_MENUITEM_PATH_DELIMITER + path;
			parent = item.getParent();
			//item = ((MenuTester)getTester(Menu.class)).getParentItem(parent);
			item = parent == null ? null : parent.getParentItem();
		}
		path = path.substring(0, path.length() - 1);
		return path;
	}

	/**
	 * Click the menu item
	 */
	public void click() {
		SWTWidgetLocation<MenuItemReference> location = new SWTWidgetLocation<MenuItemReference>(this,
			WTInternal.CENTER);
		new SWTMenuItemOperation(this).waitForEnabled(this).click(WT.BUTTON1, location, false).execute();
	}

	/**
	 * Very similar to {@link #click()} but waits for a submenu to become visible
	 * 
	 * @return the menu that became visible
	 */
	public MenuReference showMenu() {
		SWTWidgetLocation<MenuItemReference> location = new SWTWidgetLocation<MenuItemReference>(this,
			WTInternal.CENTER);
		SWTMenuOperation op = new SWTShowMenuOperation(this).waitForEnabled(this).click(WT.BUTTON1,
			location, false);
		op.execute();
		return op.getMenu();
	}



}