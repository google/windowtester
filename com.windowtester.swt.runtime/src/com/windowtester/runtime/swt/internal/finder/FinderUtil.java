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
package com.windowtester.runtime.swt.internal.finder;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class FinderUtil {

	private static Control widgetT;

	/**
	 * Given this widget, find its control.
	 * 
	 * @param w
	 * @return
	 */
	public static Control getControl(final Widget c) {

		if (c instanceof Control)
			return (Control)c;
		
		widgetT = null;
		c.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (c instanceof Caret)
					widgetT = ((Caret)c).getParent();
				if (c instanceof Menu)
					widgetT = ((Menu) c).getParent();
				if (c instanceof ScrollBar)
					widgetT = ((ScrollBar) c).getParent();
				if (c instanceof CoolItem)
					widgetT = ((CoolItem) c).getParent();
				if (c instanceof CTabItem)
					widgetT = ((CTabItem) c).getParent();
				if (c instanceof TabItem)
					widgetT = ((TabItem) c).getParent();
				if (c instanceof TableColumn)
					widgetT = ((TableColumn) c).getParent();
				if (c instanceof TableTreeItem)
					widgetT = ((TableTreeItem) c).getParent();
				if (c instanceof MenuItem) {
					widgetT = ((MenuItem) c).getParent().getParent();
				}
//				if (c instanceof TrayItem)
//					widgetT = ((TrayItem) c) ???
				if (c instanceof TabItem)
					widgetT = ((TabItem) c).getParent();
				if (c instanceof TableColumn)
					widgetT = ((TableColumn) c).getParent();
				if (c instanceof TableItem)
					widgetT = ((TableItem) c).getParent();
				if (c instanceof ToolItem)
					widgetT = ((ToolItem) c).getParent();
				if (c instanceof TreeItem)
					widgetT = ((TreeItem) c).getParent();
				if (c instanceof DragSource)
					widgetT = ((DragSource) c).getControl().getParent();
				if (c instanceof DropTarget)
					widgetT = ((DropTarget) c).getControl().getParent();
			}
		});
		return widgetT;
	}

}
