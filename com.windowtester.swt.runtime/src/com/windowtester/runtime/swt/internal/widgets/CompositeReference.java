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

/** 
 * A {@link Composite} reference.
 * @param <T> the composite type
 */
public class CompositeReference<T extends Composite>  extends ScrollableReference<T> {

	public CompositeReference(T composite) {
		super(composite);
	}


	/**
	 * Proxy for {@link Composite#getChildren()}
	 */
	public ControlReference<?> [] getChildControls (){
		return displayRef.execute(new Callable<ControlReference<?> []>() {
			public ControlReference<?>[] call() throws Exception {
				return asControlReferences(widget.getChildren());
			}
		});
	}
	
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getMenu()).add(getChildControls());
	}
	
//	//used in finding
//	public ControlReference<?> [] getAllChildren (){
//		return syncExec(new ArrayRunnable<ControlReference<?>>() {
//			public ControlReference<?>[] run() {	
//				return asControlReferences(widget.getChildren());
//			}
//		});
//	}
	
//	@Override
//	public void accept(Visitor visitor) {
//		visitor.visit(this);
//		visitor.visitEnter(this);
//		visitChildren(visitor);
//		visitor.visitLeave(this);
//	}

	
//	protected void visitChildren(Visitor visitor) {
////		HERE: follow the cue of abbot.finder.swt.SWTHierarchy.WidgetGetter.getWidgets(Widget)
//		visitIfNotNull(visitor, getMenu());
//		visitAll(visitor, getChildren());
//	}

//	public boolean isMatchedBy(ISWTWidgetMatcher matcher){
//		return matcher.matchesComposite(this);
//	}

}
