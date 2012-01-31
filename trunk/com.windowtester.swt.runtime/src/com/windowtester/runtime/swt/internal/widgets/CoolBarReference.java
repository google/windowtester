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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;

/**
 * A {@link CoolBar} reference.
 */
public class CoolBarReference extends CompositeReference<CoolBar> {

	public CoolBarReference(CoolBar coolbar) {
		super(coolbar);
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
	

	public CoolItemReference[] getItems() {
		return displayRef.execute(new Callable<CoolItemReference[]>() {
			public CoolItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), CoolItemReference.class);
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
//		for (CoolItemReference item: getItems())
//			item.accept(visitor);
//		visitor.visitLeave(this);
//	}
	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#visitChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
//	 */
//	@Override
//	protected void visitChildren(SWTWidgetReference.Visitor visitor) {
//		super.visitChildren(visitor);
//		for (CoolItemReference item: getItems())
//			item.accept(visitor);
//	}
//	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getMenu()).add(getChildControls()).add(getItems());
	}
	
}
