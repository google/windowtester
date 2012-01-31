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

import org.eclipse.swt.widgets.Decorations;

/** 
 * A {@link Decorations} reference.
 * @param <T> the decorations type
 */
public class DecorationsReference<T extends Decorations>  extends CanvasReference<T> {

	public DecorationsReference(T decorations) {
		super(decorations);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getText()
	 */
	public String getText() {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return widget.getText();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getMenuBar()}.
	 */
	public MenuReference getMenuBar() {
		return displayRef.execute(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return asReferenceOfType(widget.getMenuBar(), MenuReference.class);
			}
		});
	}
	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#accept(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
//	 */
//	@Override
//	public void accept(SWTWidgetReference.Visitor visitor) {
//		visitor.visit(this);
//		visitor.visitEnter(this);
//		visitChildren(visitor);
//		visitor.visitLeave(this);	
//	}
//
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#visitChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
//	 */
//	@Override
//	protected void visitChildren(Visitor visitor) {
//		visitIfNotNull(visitor, getMenuBar());
//		super.visitChildren(visitor);
//	}
//	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		super.setChildren(children);
		children.add(getMenuBar());
	}
	
}
