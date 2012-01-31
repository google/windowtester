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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableCellParentIndexTestShell {
	
	 private Shell shell;



	public static void main(String[] args) {
		 
		 try {
				
				TableCellParentIndexTestShell window = new TableCellParentIndexTestShell();
				window.open();
				
				final Display display = Display.getDefault();
				while (!window.getShell().isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 
		 
	 public void open(){
	   
	    shell = new Shell();
		shell.setSize(280, 500);
	    shell.setText("TableCell Example");
	    shell.setLayout(new GridLayout());
	  
	    createTable(shell);

	    createTable2(shell);
	    
	    shell.open();
	    
	  }

	private void createTable(Composite parent) {
			Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		    table.setHeaderVisible(true);
		    String[] titles = { "Col 1", "Col 2", "Col 3", "Col 4" };
		
		    for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
		      TableColumn column = new TableColumn(table, SWT.NULL);
		      column.setText(titles[loopIndex]);
		    }
		
		    for (int loopIndex = 0; loopIndex < 10; loopIndex++) {
		      TableItem item = new TableItem(table, SWT.NULL);
		      item.setText("Item " + loopIndex);
		      item.setText(0, "Item " + loopIndex);
		      item.setText(1, "Yes");
		      item.setText(2, "No");
		      item.setText(3, "A table item");
		    }
		
		    for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
		      table.getColumn(loopIndex).pack();
		    }
		
		    table.setBounds(25, 25, 220, 200);
		    
	}
		
		
		
	private void createTable2(Composite parent) {
			Table table2 = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		    table2.setHeaderVisible(true);
		    String[] titles = { "Col 1", "Col 2", "Col 3", "Col 4" };
		
		    for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
		      TableColumn column = new TableColumn(table2, SWT.NULL);
		      column.setText(titles[loopIndex]);
		    }
		
		    for (int loopIndex = 0; loopIndex < 5; loopIndex++) {
		      TableItem item = new TableItem(table2, SWT.NULL);
		      item.setText("Item " + loopIndex);
		      item.setText(0, "Item " + loopIndex);
		      item.setText(1, "Yes");
		      item.setText(2, "No");
		      item.setText(3, "A table item");
		    }
		
		    for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
		      table2.getColumn(loopIndex).pack();
		    }
		
		    table2.setBounds(25, 25, 220, 200);
		    
	}


	public Shell getShell() {
		return shell;
	}

}
