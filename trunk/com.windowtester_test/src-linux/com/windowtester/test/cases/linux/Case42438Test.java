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
package com.windowtester.test.cases.linux;

import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector2;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

/**
 * Test copied from TableDoubleClickTest.
 * <p>
 * Test for proper dismissal of dialog opened by context click on a table items.
 * @see {@link PopupMenuSelector}
 * @see {@link PopupMenuSelector2}
 * 
 * @author Keerti P
 * @author Jaime Wren
 */
public class Case42438Test extends AbstractLocatorTest {

	Case42438Shell _window;
	
	@Override
	public void uiSetup() {
		_window = new Case42438Shell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	/**
	 * See Case 42438.
	 * My attempt so far to get a click at (0,0). This doesn't do it.
	 */
	public void testTableItemContextClick() throws WidgetSearchException{
		IUIContext ui = getUI();
		ui
				.contextClick(new TableItemLocator("", 0, new SWTWidgetLocator(
						Table.class)), "Action 1");
		ui.wait(new ShellShowingCondition("Message Dialog"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Message Dialog"));
		ui
				.click(new TableItemLocator("", 1, new SWTWidgetLocator(
						Table.class)));
		ui
				.click(new TableItemLocator("", 2, new SWTWidgetLocator(
						Table.class)));
		ui
				.click(new TableItemLocator("", 3, new SWTWidgetLocator(
						Table.class)));
		ui
				.click(new TableItemLocator("", 0, new SWTWidgetLocator(
						Table.class)));
		ui.click(2, new TableItemLocator("", 1, new SWTWidgetLocator(
				Table.class)));
		ui.pause(12000);
	}
}



/**
 * Initial source code base taken from dev.eclipse.org.
 * <p>
 * A minimal TableViewer demo
 * 
 * @author michael
 */
class Case42438Shell {

	private TableViewer viewer;
	private Shell shell;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	
	/**
         *
         */
	public Case42438Shell() {
		super();

		// Create the view
		shell = new Shell(Display.getDefault(), SWT.SHELL_TRIM);
		shell.setText("Case 42438 Test");
		shell.setLayout(new FillLayout());

		Table table = new Table(shell, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("");
		column.setWidth(20);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Package");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT);
		column.setText("Class");
		column.setWidth(200);

		// Create the viewer and connect it to the view
		viewer = new TableViewer(table);

		// To get anything displayed in the table you must provide two things
		// ~ the content
		// This is done by an
		// org.eclipse.jface.viewers.IStructuredContentProvider
		// ~ the labels to be displayed in the table cells
		// This is delegated to an ITableLabelProvider

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
	}

	public Shell getShell() {
		return shell;
	}

	public void open() {
		shell.pack();
		shell.setSize(800, 800);
		shell.open();

		// At last you have to connect the viewer to your model. Because we use
		// an ArrayContentProvider
		// the model must be an array or a java.util.Collection
		viewer.setInput(new Object[] { new Integer(1), Boolean.TRUE,
				new Date(), viewer });
		
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
	}
	
	public void run() {
		Display display = Display.getCurrent();
		open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}

	public static void main(String[] args) {
		new Case42438Shell().run();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}
	
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Message Dialog",
			message);
	}
	/**
	 * A very simple private LabelProvider. Does not support images.
	 */
	private class MyLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		/**
		 * We return null, because we don't support images yet.
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 1:
				return element.getClass().getPackage().getName();
			case 2:
				return element.getClass().getName();
			}
			return "";
		}
	}

}

