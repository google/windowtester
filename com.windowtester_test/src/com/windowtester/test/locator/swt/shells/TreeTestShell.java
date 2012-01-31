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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeTestShell
{

	public static final String PATH_WITH_DELIMS = "delimed/path";
	public static final String PATH_WITH_DELIMS_ESCAPED = "delimed\\/path";


	/*
	 * PackageTest [TEST\\/tags\\/packageTest\\/V1.0]"
	 */
	public static final String ITEM_LABEL_WITH_DELIMS = "PackageTest [TEST/tags/packageTest/V1.0]";
	public static final String ITEM_LABEL_WITH_DELIMS_REGEXP = "PackageTest .*";
	
	public static final String ITEM_LABEL_WITH_DELIMS_ESCAPED = "PackageTest [TEST\\/tags\\/packageTest\\/V1.0]";
	public static final String ITEM_LABEL_WITH_DELIMS_2 = "ChildTest [TEST/tags/childTest/V1.1]";
	public static final String ITEM_LABEL_WITH_DELIMS_2_REGEXP = "ChildTest .*";
	public static final String ITEM_LABEL_WITH_DELIMS_ESCAPED_2 = "ChildTest [TEST\\/tags\\/childTest\\/V1.1]";
	
	
	public static final String SUB_MENU_ITEM = "sub";
	public static final String ITEM_LABEL_WITH_DELIMS_CHILD_2 = "child";
	
	public Tree checkTree;
	private Tree lazyTree;
	public Tree tree;
	public Tree emptyTree;
	public Shell shell;
	private String selectedMenuText;

	public Shell getShell() {
		return shell;
	}

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TreeTestShell window = new TreeTestShell();
			window.open();
			
			//new EventRecordingWatcher(window.getShell()).watch();
			
	
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
		shell.open();

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData());
		composite.setLayout(new GridLayout());

		tree = new Tree(composite, SWT.BORDER | SWT.MULTI);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.widthHint = 142;
		gridData.heightHint = 177;
		tree.setLayoutData(gridData);

	
		/*
		 * escaped tree item
		 */
		TreeItem escaped = new TreeItem(tree, 0);
		escaped.setText(ITEM_LABEL_WITH_DELIMS);
		new TreeItem(escaped, 0).setText(ITEM_LABEL_WITH_DELIMS_2);
		new TreeItem(escaped, 0).setText(ITEM_LABEL_WITH_DELIMS_CHILD_2);
		
		
		
		for (int i=0; i<4; i++) {
			TreeItem iItem = new TreeItem (tree, 0);
			iItem.setText ("TreeItem (0) -" + i);
			for (int j=0; j<4; j++) {
				TreeItem jItem = new TreeItem (iItem, 0);
				jItem.setText ("TreeItem (1) -" + j);
				for (int k=0; k<4; k++) {
					TreeItem kItem = new TreeItem (jItem, 0);
					kItem.setText ("TreeItem (2) -" + k);
					for (int l=0; l<4; l++) {
						TreeItem lItem = new TreeItem (kItem, 0);
						lItem.setText ("TreeItem (3) -" + l);
					}
				}
			}
		}
		
		
		final Menu menu = new Menu (shell, SWT.POP_UP);
		tree.setMenu (menu);

		menu.addListener (SWT.Show, new Listener () {
			public void handleEvent (Event event) {
				MenuItem [] menuItems = menu.getItems ();
				for (int i=0; i<menuItems.length; i++) {
					menuItems [i].dispose ();
				}
				TreeItem [] treeItems = tree.getSelection ();
				for (int i=0; i<treeItems.length; i++) {
					MenuItem menuItem = new MenuItem (menu, SWT.PUSH);
					final String menuText = treeItems [i].getText ();
					menuItem.setText (menuText);
					menuItem.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							setSelectedMenuText(menuText);
						}
						public void widgetDefaultSelected(SelectionEvent e) {
							widgetSelected(e);
						}
					});
				}
			}
		});

		
		final Composite composite_1 = new Composite(shell, SWT.NONE);
		final GridData gridData_2 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1);
		gridData_2.widthHint = 185;
		composite_1.setLayoutData(gridData_2);
		composite_1.setLayout(new GridLayout());
		
		lazyTree = new Tree(composite_1, SWT.BORDER);
		for (int i=0; i < 4; i++) {
			TreeItem root = new TreeItem (lazyTree, 0);
			root.setText ("root " + i);
			root.setData ("root" + i);
			new TreeItem (root, 0);
		}
		lazyTree.addListener (SWT.Expand, new Listener () {
			public void handleEvent (final Event event) {
				final TreeItem root = (TreeItem) event.item;
				TreeItem [] items = root.getItems ();
				for (int i= 0; i<items.length; i++) {
					if (items [i].getData () != null) return;
					items [i].dispose ();
				}
				for (int i= 0; i<2; i++) {
					TreeItem item = new TreeItem (root, 0);
					item.setText ("node " + i);
					item.setData ("node " + i);
					if (i ==0) {
						new TreeItem (item, 0);
					}
				}
			}
		});
		
		final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData_1.heightHint = 175;
		lazyTree.setLayoutData(gridData_1);

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(new GridData(170, 172));
		composite_2.setLayout(new GridLayout());

		checkTree = new Tree(composite_2, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		checkTree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		for (int i=0; i<5; i++) {
			TreeItem item = new TreeItem (checkTree, SWT.NONE);
			item.setText ("parent " + i);
			for (int j=0; j < 3; ++j) {
				TreeItem item2 = new TreeItem (item, SWT.NONE);
				item2.setText ("child/" + j);
			}
				
		}
		
		/*
		 * A menu with an escaped path delim
		 */
		
		Menu menu2 = new Menu (shell, SWT.POP_UP);
		MenuItem item1 = new MenuItem (menu2, SWT.PUSH);
		item1.setText (PATH_WITH_DELIMS);
		item1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setSelectedMenuText(PATH_WITH_DELIMS);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		MenuItem item2 = new MenuItem (menu2, SWT.CASCADE);
		item2.setText (SUB_MENU_ITEM);
		Menu subMenu = new Menu (menu2);
		item2.setMenu (subMenu);
		MenuItem subItem1 = new MenuItem (subMenu, SWT.PUSH);
		subItem1.setText (PATH_WITH_DELIMS);
		subItem1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setSelectedMenuText(SUB_MENU_ITEM + "/" + PATH_WITH_DELIMS);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		checkTree.setMenu(menu2);
		
		
		final Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayoutData(new GridData(185, 172));
		composite_3.setLayout(new GridLayout());

		emptyTree = new Tree(composite_3, SWT.MULTI | SWT.BORDER);
		emptyTree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		Menu menu3 = new Menu (shell, SWT.POP_UP);
		MenuItem mitem1 = new MenuItem (menu3, SWT.PUSH);
		mitem1.setText ("item1");
		mitem1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setSelectedMenuText("item1");
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		MenuItem mitem2 = new MenuItem (menu3, SWT.CASCADE);
		mitem2.setText ("item2");
		mitem2.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setSelectedMenuText("item2");
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		Menu msubMenu = new Menu (menu3);
		mitem2.setMenu (msubMenu);
		MenuItem msubItem1 = new MenuItem (msubMenu, SWT.PUSH);
		msubItem1.setText ("subitem1");
		msubItem1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setSelectedMenuText("subitem1");
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		emptyTree.setMenu (menu3);
		
		shell.layout();
	}

	void createContents() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		shell.setLayout(gridLayout);
		shell.setSize(380, 425);
		shell.setText("Tree Test");
	}

	/**
	 * Called by the menu handler for purposes of test assertion
	 * @param menuText the text of the menu selected
	 */
	private void setSelectedMenuText(String menuText) {
		selectedMenuText = menuText;
	}
	public String getSelectedMenuText() {
		return selectedMenuText;
	}
	public void clearSelectedMenuText() {
		selectedMenuText = null;
	}
}