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
package com.windowtester.runtime.swt.internal.reveal;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.TableItemTester;
import abbot.tester.swt.TableTester;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.selector.UIDriver;

/**
 * A custom revealer for revealing table items.
 */
public class TableItemRevealer implements IRevealStrategy {

	/** How long to wait for a reveal action to complete */
	private static final int REVEAL_WAIT = 100;
	
	private TableTester _tableTester = new TableTester();
	private TableItemTester _tableItemTester = new TableItemTester();
	

	/**
	 * @see com.windowtester.runtime.swt.internal.reveal.IRevealStrategy#reveal(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public Widget reveal(Widget w, int x, int y) {

		if (!(w instanceof TableItem))
			throw new IllegalArgumentException("Widget must be a TableItem");
		
		TableItem item = (TableItem)w;
		Table table = _tableItemTester.getParent(item);
		_tableTester.actionShowTableItem(table, item);
		
		waitForReveal();
		
		return w;
	}
	
	/**
	 * Wait for the reveal action to complete.
	 */
	private void waitForReveal() {
		/*
		 * This is a kludge.  Unfortunately, there is no good way to test for item visibility.
		 * As a stop gap, we just wait for a set period of time.
		 */
		UIDriver.pause(REVEAL_WAIT);
		
	}

	/**
	 * @see com.windowtester.runtime.swt.internal.reveal.IRevealStrategy#reveal(org.eclipse.swt.widgets.Widget, java.lang.String, int, int)
	 */
	public Widget reveal(Widget w, String path, int x, int y)
			throws WidgetNotFoundException, MultipleWidgetsFoundException {

		revealColumn(w, path);
		
		//reveal row
		reveal(w, x, y);

		return w;
	}

	private void revealColumn(Widget w, String path) {
		if (!(w instanceof TableItem))
			throw new IllegalArgumentException("Widget must be a TableItem");

		
		int columnIndex = 0;
		try {
			columnIndex = Integer.parseInt(path);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(path + " must be an integer value to select the item column");
		}
		
		TableItem item = (TableItem)w;
		Table table = _tableItemTester.getParent(item);
		
		//sanity check to prevent from trying to reveal a column that does not exist
		if (_tableTester.getColumnCount(table) == 0)
			return; //might this be made more general? 

		
		TableColumn column = _tableTester.getColumn(table, columnIndex);

		showColumn(table, column);
		
		waitForReveal();
	}

	//TableTester can't be trusted...
	private void showColumn(final Table table, final TableColumn column) {
		table.getDisplay().syncExec(new Runnable() {
			public void run() {
				table.showColumn(column);
			}
		});
	}



}
