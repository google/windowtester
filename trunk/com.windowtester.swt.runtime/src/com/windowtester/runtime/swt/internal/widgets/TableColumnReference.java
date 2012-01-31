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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.windowtester.runtime.internal.concurrent.SafeCallable;

/** 
 * A {@link TableColumn} reference.
 */
public class TableColumnReference extends ItemReference<TableColumn> {

	/**
	 * Constructs a new instance with the given control.
	 * 
	 * @param control the control.
	 */
	public TableColumnReference(TableColumn control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<Table> getParent() {
		return displayRef.execute(new Callable<CompositeReference<Table>>() {
			public CompositeReference<Table> call() throws Exception {
				return new TableReference(widget.getParent());
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getBounds()
	 */
	public Rectangle getDisplayBounds() {
		return displayRef.execute(new SafeCallable<Rectangle>() {
			public Rectangle call() throws Exception {
				Control parent = (Control) invoke(widget, "getParent");
				Rectangle bounds;
				Table table = (Table) parent;
				TableColumn column = (TableColumn) widget;
				// Not sure this will work if table has no rows...
				Rectangle cellBounds = table.getItem(0).getBounds(table.indexOf(column));
				Rectangle tableBounds = table.getBounds();
				bounds = new Rectangle(cellBounds.x, 0, cellBounds.width, tableBounds.height);
				return widget.getDisplay().map(parent, null, bounds);
			}
			public Rectangle handleException(Throwable e) throws Throwable {
				throw new RuntimeException("Failed to calculate bounds", e);
			}
		});
	}
	
		
}
