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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor;

public class WidgetPrinter implements Visitor {

	StringBuilder sb = new StringBuilder();
	
	int indent = 0;
	
	public <W extends Widget> void visit(SWTWidgetReference<W> visitor) {
		append(visitor);
	}

	private void append(SWTWidgetReference<?> visitor) {
		sb.append(indent()).append(getString(visitor)).append(StringUtils.NEW_LINE);
	}

	public String getString(SWTWidgetReference<?> widget) {
		return widget.toString() + detailString(widget);
	}

	private String detailString(SWTWidgetReference<?> widget) {
		String desc = " is visible: " + widget.isVisible();
		// TODO[pq]: push this into a WidgetDescription map object
		if (widget instanceof ToolItemReference){
			String id = ((ToolItemReference)widget).getActionDefinitionId();
			if (id != null)
				desc += " action id: " + id;
		}
		
		return desc;
//		IWidgetLocator locator = Resolver.resolve(widget);
//		return " - locator: " + locator;
	}

	private String indent() {
		int spaces = indent*3;
		StringBuilder space = new StringBuilder();
		for (int i = 0; i < spaces; i++) {
			space.append(' ');
		}
		return space.toString();
	}

	
	public <T extends com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference<?>> void visitEnter(T composite) {
		++indent;
	}
	
	public <T extends com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference<?>> void visitLeave(T composite) {
		--indent;
	}
	
	public <W extends Composite> void visit(CompositeReference<W> visitor) {
		append(visitor);
	}

	public String asString(){
		return sb.toString();
	}
	
	
	/**
	 * Convenience method.
	 */
	public void print(){
		final DisplayReference display = DisplayReference.getDefault();
		display.execute(new VoidCallable() {
			@Override
			public void call() throws Exception {
				display.getActiveShell().accept(WidgetPrinter.this);
			}
		});
		System.out.println(asString());		
	}
	
	
}
