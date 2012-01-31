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
package com.windowtester.runtime.swt.internal.matchers;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Matcher to locate a cell in a table
 *
 * For row index 0 is the column header
 * 
 *
 */
public class TableCellMatcher extends WidgetMatcher {
	
	private String rowText;
	private String colText;
	private int row;
	private int column;
	
	public static final int UNSPECIFIED = -1;
	
	public TableCellMatcher(int row, int col,String rowNameOrText,String colNameOrText){
		this.row = row;
		column = col;
		rowText = rowNameOrText;
		colText = colNameOrText;
	}

	public TableCellMatcher(int row, String colNameOrText){
		this.row = row;
		colText = colNameOrText;
	}
	
	public TableCellMatcher(String rowNameOrText,int col){
		rowText = rowNameOrText;
		column = col;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {
		System.out.println(ref);
		Object w = ref.getWidget();
		if (w instanceof TableItem){ // SWTHierarchyHelper.isVisible(w)
			if (row != UNSPECIFIED && row != 0){
				TableRowIndexMatcher matcher = new TableRowIndexMatcher(row);
				return matcher.matches(ref);
			}
			if (column != UNSPECIFIED){
				TableRowTextColumnIndexMatcher matcher = new TableRowTextColumnIndexMatcher(rowText,column);
				return matcher.matches(ref);
			}
			else {
				TableRowTextColumnTextMatcher matcher = new TableRowTextColumnTextMatcher(rowText,colText);
				return matcher.matches(ref);			
			}
		}		
		if (w instanceof TableColumn && row == 0){
			TableColumnMatcher matcher = new TableColumnMatcher(column);
			return matcher.matches(ref);
		}
		return false;
	}
	
	/**
	 * 
	 * Matches by row index
	 *
	 */
	private class TableRowIndexMatcher implements ISWTWidgetMatcher {

		private int row;
		private boolean result;
		
		public TableRowIndexMatcher(int row){
			this.row = row;
		}
		
		public boolean matches(ISWTWidgetReference<?> ref) {
			Object w = ref.getWidget();
			result = false;
			final TableItem item = (TableItem)w;
			DisplayReference.getDefault().execute(new VoidCallable(){
				public void call() throws Exception {
					Table table = item.getParent(); 
					if (row != 0 ){// special case 0 is column header
						int index = table.indexOf(item);
						if (index == (row -1)){
							result = true;
							return;
						}
					}
				}
			}); 
			return result;
		}		
	}

    /**
     * 
     * Matches by row text and column index
     * text and can by setData or actual text in item at that column
     */
	private class TableRowTextColumnIndexMatcher implements ISWTWidgetMatcher {
		
		private String rText;
		private int index;
		private boolean result;
		
		public TableRowTextColumnIndexMatcher(String rowText,int colIndex){
			rText = rowText;
			index = colIndex;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
		 */
		public boolean matches(ISWTWidgetReference<?> ref) {
			Object w = ref.getWidget();
			result = false;
			final TableItem item = (TableItem)w;
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() {
					// check for setData("name")
					String name = (String)item.getData("name");
					String text = item.getText(index); 
					if (name != null && name.equals(rText) || 
						text != null && text.equals(rText)) {
						
						result = true;
						return;
					}
				}
			});
			return result;
		}	
	}
	
	/**
	 * Matches by row text and column text
	 * Row text is matched against setData, text in the column 
	 * corresponding to column text, or it can be text in any column
	 * of the table item - unique text in the entire table. 
	 *
	 */
	private class TableRowTextColumnTextMatcher implements ISWTWidgetMatcher {
		
		private String rText;
		private String cText;
		private boolean result;
		
		public TableRowTextColumnTextMatcher(String rtext,String ctext){
			rText = rtext;
			cText = ctext;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
		 */
		public boolean matches(ISWTWidgetReference<?> ref) {
			Object w = ref.getWidget();
			result = false;
			final TableItem item = (TableItem)w;
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() {
					Table table = item.getParent(); 
					TableColumn[] columns = table.getColumns();
					int index= WidgetLocator.UNASSIGNED;
					// match against setData("name") of column
					// or column text to find column index
					for (int i = 0;i < columns.length;i++){
						String colName = (String)columns[i].getData("name");
						String colText = columns[i].getText();
						if (colName != null && colName.equals(cText) ||
								colText != null && colText.equals(cText)){
							index = i;
							break;
						}
					}					
					// we have found column index,
					// now match against the row text 
					if ((index != WidgetLocator.UNASSIGNED) && 
							(item.getText(index).equals(rText))){
						result =  true;
						return;
					}
					// now match against ALL the text in the tableItem
					for (int i = 0;i < columns.length;i++){
						if (item.getText(i).equals(rText)){
							result =  true;
							return;
						}
					}
					
				}
			});
			return result;
		}
		
	}
	
	/**
	 * Matches for the column of the table. row is specified as 0,
	 * so look in the column header.
	 *  
	 */
	private class TableColumnMatcher implements ISWTWidgetMatcher {

		private int col;
		private boolean result;
		
		public TableColumnMatcher(int column){
			col = column;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
		 */
		public boolean matches(ISWTWidgetReference<?> ref) {
			Object w = ref.getWidget();
			final TableColumn item = (TableColumn)w;
			result = false;
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() {
					Table table = item.getParent(); 
					if (table.indexOf(item) == col)
						result = true;
				}
			});
			return result;
		}
		
	}

	
}
