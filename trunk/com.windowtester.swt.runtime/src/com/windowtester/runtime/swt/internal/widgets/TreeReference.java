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

import org.eclipse.swt.widgets.Tree;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;

/**
 * A {@link Tree} reference.
 */
public class TreeReference extends CompositeReference<Tree>
	implements ISWTWidgetReferenceWithContextMenu, TreeItemReferenceContainer
{

	public TreeReference(Tree control) {
		super(control);
	}

	/**
	 * Proxy for {@link Tree#getColumnCount()}
	 */
	public int getColumnCount() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getColumnCount();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getItemCount()}
	 */
	public int getItemCount() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getItemCount();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.TreeItemReferenceContainer#getItems()
	 */
	public TreeItemReference[] getItems() {
		return displayRef.execute(new Callable<TreeItemReference[]>() {
			public TreeItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), TreeItemReference.class);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getMenu()).add(getItems()).add(getChildControls());
	}
	
//	@Override
//	public void accept(Visitor visitor) {
//		visitor.visit(this);
//		visitor.visitEnter(this);
//		visitChildren(visitor);
//		visitor.visitLeave(this);
//	}
//
//	
//	protected void visitChildren(Visitor visitor) {
////		HERE: follow the cue of abbot.finder.swt.SWTHierarchy.WidgetGetter.getWidgets(Widget)
//		visitIfNotNull(visitor, getMenu());
//		visitAll(visitor, getItems());
//		visitAll(visitor, getChildren());
//	}

	/**
	 * Proxy for {@link Tree#showItem(org.eclipse.swt.widgets.TreeItem)}. 	 
	 */
	public void showItem(final TreeItemReference item) {
		displayRef.execute(new VoidCallable() {
			public void call() throws Exception {
				widget.showItem(item.widget);
			}
		});
	}

	/**
	 * Top level tree items are always visible, so nothing to do.
	 */
	public void expand() {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see ISWTWidgetReferenceWithContextMenu#showContextMenu()
	 */
	public MenuReference showContextMenu(IClickDescription click) {
		
		// Tree default context click location is NOT in the center of the tree item
		SWTLocation location = SWTWidgetLocation.withDefaultTopLeft33(this, click);
	
		// On Linux, tree menus work more reliably with pauseOnMouseDown = true
		// and it does not make a difference either way on Windows
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, true);
		op.execute();
		return op.getMenu();
	}
}
