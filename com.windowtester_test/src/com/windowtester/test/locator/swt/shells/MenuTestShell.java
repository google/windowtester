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

import org.eclipse.jface.resource.DeviceResourceException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class MenuTestShell {

	private static final String RUN_ITEM_TEXT = "Ru&n...";
	private static final String RUN_AS_ITEM_TEXT = "R&un as";
	private static final String RUN_IT_ITEM_TEXT = "Run it";
	private static final String RUN_MENU_TEXT = "Run";
	private static final String MIX_MENU_TEXT = "Mix";
	private static final String MIX_PUSH_TEXT = "Push";
	private static final String MIX_RADIO_TEXT = "Radio";
	private static final String MIX_IMAGE_TEXT = "Image";
	private static final String MIX_NORMAL_TEXT = "Normal";

	protected Shell shell;

	// the last selected menu item
	public Widget selectedMenuItem;
	MenuTestShell window;

	public MenuItem topMenuItem;

	public MenuItem parentMenuItem_1;

	public MenuItem child1MenuItem;

	public MenuItem child2MenuItem;

	public MenuItem grandchildMenuItem;

	public MenuItem grandchildrenMenuItem;

	protected Menu menu;

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MenuTestShell window = new MenuTestShell();
			window.open();
			// new EventRecordingWatcher(window.getShell()).watch();

			final Display display = Display.getDefault();
			while (!window.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		createMenuAndContents();
		shell.open();
		shell.layout();
	}

	private void createMenuAndContents() {
		Listener selectionListener = new Listener() {
			public void handleEvent(Event event) {
				selectedMenuItem = event.widget;
				System.out.println("CLICK");// DEBUG
			}
		};

		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		topMenuItem = new MenuItem(menu, SWT.NONE);
		topMenuItem.setText("top");
		topMenuItem.addListener(SWT.Selection, selectionListener);

		parentMenuItem_1 = new MenuItem(menu, SWT.CASCADE);
		parentMenuItem_1.setText("parent");
		parentMenuItem_1.addListener(SWT.Selection, selectionListener);

		final Menu menu_1 = new Menu(parentMenuItem_1);
		parentMenuItem_1.setMenu(menu_1);

		child1MenuItem = new MenuItem(menu_1, SWT.NONE);
		child1MenuItem.setText("child 1");
		child1MenuItem.addListener(SWT.Selection, selectionListener);

		child2MenuItem = new MenuItem(menu_1, SWT.CASCADE);
		child2MenuItem.setText("child 2");
		child2MenuItem.addListener(SWT.Selection, selectionListener);

		final Menu menu_2 = new Menu(child2MenuItem);
		child2MenuItem.setMenu(menu_2);

		grandchildMenuItem = new MenuItem(menu_2, SWT.NONE);
		grandchildMenuItem.setText("grand/child");
		grandchildMenuItem.addListener(SWT.Selection, selectionListener);

		grandchildrenMenuItem = new MenuItem(menu_2, SWT.NONE);
		grandchildrenMenuItem.setText("grand/&children...\t\tCtrl+F"); // ...\t\tCtrl+F
		grandchildrenMenuItem.addListener(SWT.Selection, selectionListener);

		MenuItem runMenuItem = new MenuItem(menu, SWT.CASCADE);
		runMenuItem.setText(RUN_MENU_TEXT);
		Menu runMenu = new Menu(menu);
		runMenuItem.setMenu(runMenu);

		MenuItem runItem = new MenuItem(runMenu, SWT.NONE);
		runItem.setText(RUN_ITEM_TEXT);
		MenuItem runAsItem = new MenuItem(runMenu, SWT.NONE);
		runAsItem.setText(RUN_AS_ITEM_TEXT);
		MenuItem runItItem = new MenuItem(runMenu, SWT.NONE);
		runItItem.setText(RUN_IT_ITEM_TEXT);

		MenuItem mixMenuItem = new MenuItem(menu, SWT.CASCADE);
		mixMenuItem.setText(MIX_MENU_TEXT);
		Menu mixMenu = new Menu(menu);
		mixMenuItem.setMenu(mixMenu);
		MenuItem mixPushItem = new MenuItem(mixMenu, SWT.PUSH);
		mixPushItem.setText(MIX_PUSH_TEXT);
		MenuItem mixRadioItem = new MenuItem(mixMenu, SWT.RADIO);
		mixRadioItem.setText(MIX_RADIO_TEXT);
		MenuItem mixImageItem = new MenuItem(mixMenu, SWT.NONE);
		mixImageItem.setImage(null);
		mixImageItem.setText(MIX_IMAGE_TEXT);
		MenuItem mixNormalItem = new MenuItem(mixMenu, SWT.NONE);
		mixNormalItem.setText(MIX_NORMAL_TEXT);

		LocalResourceManager m = new LocalResourceManager(JFaceResources.getResources());
		ImageDescriptor icon = ImageDescriptor.createFromFile(getClass(), "new_wiz.png");
		try {
			mixImageItem.setImage(icon == null ? null : m.createImage(icon));
		} catch (DeviceResourceException e) {
			icon = ImageDescriptor.getMissingImageDescriptor();
			try {
				mixImageItem.setImage(m.createImage(icon));
				/*
				 * the 3.2 build complained of an unhandled exception here
				 * -- this attempts to make it happy...
				 */
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(316, 67);
		shell.setText("Menu Test");
	}

	public Shell getShell() {
		return shell;
	}

}
