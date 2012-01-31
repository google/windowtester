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
package com.windowtester.test.locator.swt.shells;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Phil Quitslund
 *
 */
public class CoolBarTestShell {

	static Display display;

	static CoolBar coolBar;

	static Menu chevronMenu = null;
	protected Shell shell;

	CoolBarTestShell window;
	
	private Widget lastSelection;

	private ToolItem[] items = new ToolItem[5];
	
	
	
	public Shell getShell() {
		return shell;
	}
	
	public static void main(String[] args) {
		CoolBarTestShell window = new CoolBarTestShell();
		window.open();
		final Display display = Display.getDefault();
		while (!window.getShell().isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void open() {
//		display = new Display();
		shell = new Shell();
		shell.setLayout(new GridLayout());
		coolBar = new CoolBar(shell, SWT.FLAT | SWT.BORDER);
		coolBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT | SWT.WRAP);
		int minWidth = 0;
		for (int j = 0; j < 5; j++) {
			int width = 0;
			ToolItem item = new ToolItem(toolBar, SWT.PUSH);
			getItems()[j] = item;
			item.setText("B" + j);
			item.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					setLastSelection(e.widget);
				}
				public void widgetDefaultSelected(SelectionEvent e) {	
				}
			});
			width = item.getWidth();
			/* find the width of the widest tool */
			if (width > minWidth)
				minWidth = width;
		}
		CoolItem coolItem = new CoolItem(coolBar, SWT.DROP_DOWN);
		coolItem.setControl(toolBar);
		Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point coolSize = coolItem.computeSize(size.x, size.y);
		coolItem.setMinimumSize(minWidth, coolSize.y);
		coolItem.setPreferredSize(coolSize);
		coolItem.setSize(coolSize);
		coolItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					CoolItem item = (CoolItem) event.widget;
					Rectangle itemBounds = item.getBounds();
					Point pt = coolBar.toDisplay(new Point(itemBounds.x,
							itemBounds.y));
					itemBounds.x = pt.x;
					itemBounds.y = pt.y;
					ToolBar bar = (ToolBar) item.getControl();
					ToolItem[] tools = bar.getItems();

					int i = 0;
					while (i < tools.length) {
						Rectangle toolBounds = tools[i].getBounds();
						pt = bar
								.toDisplay(new Point(toolBounds.x, toolBounds.y));
						toolBounds.x = pt.x;
						toolBounds.y = pt.y;

						/*
						 * Figure out the visible portion of the tool by looking
						 * at the intersection of the tool bounds with the cool
						 * item bounds.
						 */
						Rectangle intersection = itemBounds
								.intersection(toolBounds);

						/*
						 * If the tool is not completely within the cool item
						 * bounds, then it is partially hidden, and all
						 * remaining tools are completely hidden.
						 */
						if (!intersection.equals(toolBounds))
							break;
						i++;
					}

					/*
					 * Create a menu with items for each of the completely
					 * hidden buttons.
					 */
					if (chevronMenu != null)
						chevronMenu.dispose();
					chevronMenu = new Menu(coolBar);
					for (int j = i; j < tools.length; j++) {
						MenuItem menuItem = new MenuItem(chevronMenu, SWT.PUSH);
						menuItem.setText(tools[j].getText());
					}

					/*
					 * Drop down the menu below the chevron, with the left edges
					 * aligned.
					 */
					pt = coolBar.toDisplay(new Point(event.x, event.y));
					chevronMenu.setLocation(pt.x, pt.y);
					chevronMenu.setVisible(true);
				}
			}
		});

		shell.pack();
		shell.open();
	}

	public void setLastSelection(Widget lastSelection) {
		this.lastSelection = lastSelection;
	}

	public Widget getLastSelection() {
		return lastSelection;
	}

	public void setItems(ToolItem[] items) {
		this.items = items;
	}

	public ToolItem[] getItems() {
		return items;
	}

}
