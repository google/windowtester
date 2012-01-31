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

import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

/**
 * A {@link CTabFolder} reference.
 */
public class TabFolderReference extends CompositeReference<TabFolder> {

	public TabFolderReference(TabFolder menu) {
		super(menu);
	}

	/**
	 * Gets the tab item count.
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


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<Composite> getParent() {
		return displayRef.execute(new Callable<CompositeReference<Composite>>() {
			public CompositeReference<Composite> call() throws Exception {
				Composite parent = widget.getParent();
				if (parent == null)
					return null;
				return new CompositeReference<Composite>(parent);
			}
		});
	}
	
	public TabItemReference[] getItems() {
		return displayRef.execute(new Callable<TabItemReference[]>() {
			public TabItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), TabItemReference.class);
			}
		});
	}
	
	/**
	 * Add elements for inclusion in the list of children returned by {@link SWTWidgetReference#getChildren()}.
	 * Note that {@link ChildSet} subscribes to the {@link Set} contract in that it ensures that there are no duplicates.
	 * @param children the collecting child set
	 */
	protected void setChildren(ChildSet children){
		children.add(getChildControls()).add(getItems());
	}
//	
//	@Override
//	public SWTWidgetReference<?>[] getChildren() {
//		ChildSet children = new ChildSet();
//		setChildren(children);
//		return children.toArray();
//	}
//	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#accept(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
//	 */
//	@Override
//	public void accept(SWTWidgetReference.Visitor visitor) {
//		visitor.visit(this);
//		visitor.visitEnter(this);
//		for (TabItemReference item: getItems())
//			item.accept(visitor);
//		visitor.visitLeave(this);
//	}
}
