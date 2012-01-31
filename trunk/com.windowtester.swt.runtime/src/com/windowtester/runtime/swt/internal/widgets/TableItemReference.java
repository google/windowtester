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

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;

/**
 * A {@link TableItem} reference.
 */
public class TableItemReference extends ItemReference<TableItem> {

	public TableItemReference(TableItem item) {
		super(item);
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
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#isVisible()
	 */
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return super.isVisible();
	}

	public MenuReference showContextMenu(IClickDescription click) {
		
		// Table items default context click location is NOT in the center of the table item
		SWTLocation location = SWTWidgetLocation.withDefaultTopLeft33(this, click);
			
		// For TableItem context menus on both Windows and Linux, 
		// need to process the mouse down events before the mouse up events are posted... so pass true
		// This can be seen in the TableDoubleClickTest
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, true);
		op.execute();
		return op.getMenu();
	}
}
