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

import org.eclipse.swt.widgets.ToolBar;

/**
 * A {@link ToolBar} reference.
 */
public class ToolBarReference extends CompositeReference<ToolBar>{

	public ToolBarReference(ToolBar control) {
		super(control);
	}

	/**
	 * Proxy for {@link ToolBar#getItemCount()}
	 */
	public int getItemCount() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link ToolBar#getItems()}
	 */
	public ToolItemReference[] getItems() {
		return displayRef.execute(new Callable<ToolItemReference[]>() {
			public ToolItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), ToolItemReference.class);
			}
		});
	}

//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#getChildren()
//	 */
//	@Override
//	public SWTWidgetReference<?>[] getChildren() {
//		// TODO[pq]: review ALL implementations of getChildren()
//		List<SWTWidgetReference<?>> children = new ArrayList<SWTWidgetReference<?>>(Arrays.asList(super.getChildren()));
//		children.addAll(Arrays.asList(getItems()));
//		return children.toArray(emptyArray());
////		return getItems();
//	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getItems());
		children.add(getChildControls());
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
//		//visitAll(visitor, getItems());
//		visitAll(visitor, getChildren());
//	}
//		
	
	
	
}
