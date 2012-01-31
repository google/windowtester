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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTTreeItemLocation;
import com.windowtester.runtime.swt.internal.operation.SWTTreeItemOperation;
import com.windowtester.runtime.swt.internal.util.TextUtils;

/**
 * A {@link TreeItem} reference.
 */
public class TreeItemReference extends ItemReference<TreeItem>
	implements ISWTWidgetReferenceWithContextMenu, TreeItemReferenceContainer, IUISelector
{

	/*
	 * NOTE: implementation of IUISelector is provisional.
	 * 
	 */
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		return show().click(click);		
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget,
			final IClickDescription click, String menuItemPath)
			throws WidgetSearchException {
		final TreeItemReference item = show();
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return item.showContextMenu(click);
			}
		}, menuItemPath);
	}
	

	public class ItemExpandedCondition implements ICondition {

		public boolean test() {
			return getExpanded();
		}
		
		@Override
		public String toString() {
			return TreeItemReference.this.toString() + " to be expanded";
		}
	}
	
	public class ItemCheckedCondition implements ICondition{

		public boolean test() {
			return getChecked();
		}
		
		@Override
		public String toString() {
			return TreeItemReference.this.toString() + " to be checked";
		}
	}
	
	
	public TreeItemReference(TreeItem item) {
		super(item);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public TreeReference getParent() {
		return displayRef.execute(new Callable<TreeReference>() {
			public TreeReference call() throws Exception {
				return new TreeReference(widget.getParent());
			}
		});
	}

	/**
	 * Proxy for {@link TreeItem#getText(int)}
	 */
	public String getText(final int column) {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return widget.getText(column);
			}
		});
	}

	/**
	 * Proxy for {@link TreeItem#getItems()}
	 */
	public TreeItemReference[] getItems() {
		return displayRef.execute(new Callable<TreeItemReference[]>() {
			public TreeItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), TreeItemReference.class);
			}
		});
	}

	/**
	 * Calculate the client area of the widget or a column within the widget and convert that from local coordinates to
	 * global coordinates (also known as display coordinates).
	 * 
	 * @param columnIndex the zero based column index or -1 for the bounds of the entire tree item 
	 * @return the area of the widget or widget column in display coordinates
	 */
	public Rectangle getDisplayBounds(final int columnIndex) {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				TreeItem treeItem = getWidget();
				Rectangle localBounds = columnIndex >= 0 ? treeItem.getBounds(columnIndex) : treeItem.getBounds();
				return treeItem.getDisplay().map(treeItem.getParent(), null, localBounds);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IControlReference#isVisible()
	 */
	public boolean isVisible() {
		// TODO[pq]: this is NOT right -- returns false -- the rub is we need a way to detect visibility (perhaps parent expansion state?)
		return super.isVisible();
	}
	
	@Override
	public boolean isEnabled() {
		return getParent().isEnabled();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getItems());
	}
//	
//	
//	@Override
//	public void accept(SWTWidgetReference.Visitor visitor) {
//		super.accept(visitor);
//		visitor.visitEnter(this);
//		visitAll(visitor, getItems());
//		visitor.visitLeave(this);
//	}

	/**
	 * Proxy for {@link TreeItem#setExpanded(boolean)}.
	 */
	public void setExpanded(final boolean expanded) {
		displayRef.execute(new VoidCallable() {
			public void call() throws Exception {
				widget.setExpanded(expanded);
			}
		});
	}

	/**
	 * Proxy for {@link TreeItem#getExpanded()}.
	 */
	public boolean getExpanded() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getExpanded();
			}
		});
	}
	
	/**
	 * Proxy for {@link TreeItem#setChecked(boolean)}.
	 */
	public void setChecked(final boolean checked) {
		displayRef.execute(new VoidCallable() {
			public void call() throws Exception {
				widget.setChecked(checked);
			}
		});
	}
	

	/**
	 * Proxy for {@link TreeItem#getChecked()}.
	 */
	public boolean getChecked(){
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getChecked();
			}
		});
	}
	
	
	public void expand() {
//		new SWTTreeItemExpandEvent(this).dispatch();
		new SWTTreeItemOperation(this).show().expand().execute();	
		// TODO[pq]: wait for expand?
//		new SWTOperation() {
//			protected boolean queueEventsInUI() throws Exception {
//				queueWidgetEvent(widget, SWT.Expand);
//				postEventsInUI();
//				widget.setExpanded(true);
//				queueWidgetEvent(widget, SWT.MouseMove);
//				queueWidgetEvent(widget, SWT.Activate);
//				queueWidgetEvent(widget, SWT.FocusIn);
//				queueWidgetEvent(widget, SWT.MouseDown);
//				queueWidgetEvent(widget, SWT.MeasureItem);
//				queueWidgetEvent(widget, SWT.Deactivate);
//				queueWidgetEvent(widget, SWT.FocusOut);
//				return true;
//			}
//		}.execute();
	}
	
	public ICondition isExpanded(){
		return new ItemExpandedCondition();
	}
	
	@SuppressWarnings("deprecation")
	public TreeItemReference check(){
//		new SWTTreeItemOperation(this).check().execute();
		new SWTTreeItemOperation(this).show().checkSWTBotStyle().execute();
		return this;
	}
	
	public TreeItemReference show(){
		new SWTTreeItemOperation(this).show().execute();
		return this;
	}
	
	public TreeItemReference click(IClickDescription click) {
		show();
		if ((click.modifierMask() & WT.CHECK) != 0) {
			check();
		}
		else {
			// new SWTTreeItemClickOperation(this).forClick(click).execute();
			SWTTreeItemLocation loc = SWTTreeItemLocation.withDefaultTopLeft33(this, click);
			// TODO process column base clicks
			loc.column(-1 /* column */);
			new SWTMouseOperation(click.modifierMask()).at(loc).count(click.clicks()).execute();
		}
		return this;
	}

	public ICondition isChecked(){
		return new ItemCheckedCondition();
	}

	public String getPathString() {
		//based on TreeItemPathMatcher implementation
		return displayRef.execute(new Callable<String>(){
			public String call() throws Exception {
				TreeItem item = widget;
				String path = TextUtils.escapeSlashes(item.getText());
				//handle dummy child case (approximate?)
				
				if (path == "") {
					TreeItem parentItem = item.getParentItem();
					if (parentItem != null)
						if (!parentItem.getExpanded()) {
							return path;
						}
				}
					
				for (TreeItem parent = item.getParentItem(); parent != null; parent = parent
						.getParentItem()) {
					//prepend 
					//(be sure to fix slashes here too!
					path = TextUtils.escapeSlashes(parent.getText()) + '/' + path;
				}
				return path;
			}
		});		
	}

	/* (non-Javadoc)
	 * @see ISWTWidgetReferenceWithContextMenu#showContextMenu()
	 */
	public MenuReference showContextMenu(IClickDescription click) {
		show();
		
		// Tree items default context click location is NOT in the center of the tree item
		SWTLocation location = SWTTreeItemLocation.withDefaultTopLeft33(this, click);
	
		// On Linux, tree menus work more reliably with pauseOnMouseDown = true
		// and it does not make a difference either way on Windows
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, true);
		op.execute();
		return op.getMenu();
	}
}
