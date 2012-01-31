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
package com.windowtester.runtime.swt.locator;


import java.awt.Point;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.finder.legacy.InternalMatcherBuilder;
import com.windowtester.runtime.swt.internal.matchers.TableCellMatcher;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.selector.TableColumnSelector;
import com.windowtester.runtime.swt.internal.selector.TableItemSelector2;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.TableItemReference;

/**
 * Locates {@link Table} cells.
 * <p>
 * 
 * 
 *  TableCellLocator(int row, int column)
 *  <br>
 *  TableCellLocator(int row, String columnName)
 *  <br>
 *  TableCellLocator(String rowText, int column)
 *  <br>
 *  TableCellLocator(String rowText, String columnName)
 *  
 *  <p>
 *
 *<dl>
 *  <dt>int row</dt>
 *  		<dd>the index of the row where 1 is the first row and 0 is a column header</dd>
 *  <br><dt>int column</dt> <dd>the index of the column where 1 is the first column </dd>
 *  <br><dt>String columnName </dt><dd>the setData("name",...) of the column </dd>
 *  					<dd>the text in the column header</dd>
 *  <br><dt>String rowName</dt><dd>the setData("name", ...) of the TableItem</dd>
 *                <dd>the unique text of the row in the specified column</dd>
 *                <dd>the unique text of that row in the entire table.</dd>
 * </dl>              
 * <p> 
 * Example<p>
 *<table>
 * <tr><td>Name</td><td>Age</td><td>Sex</td></tr>
 * <tr><td>Bob Smith</td><td>16</td><td>Male</td></tr>
 * <tr><td>John Doe</td><td>32</td><td>Male</td></tr>
 * <tr><td>Kate Baker</td><td>32</td><td>Male</td></tr>
 * </table>
 *
 *<dl>
 *<dt>Edit the age of "Bob Smith" ...</dt>
 *
 *      <dd>ui.click(new TableCellLocator("16", "Age"));</dd>
 *  <dd>ui.enterText("29");</dd>
 * 
 *<br>
 *  <dt>Edit the age of "John Doe" ...</dt>
 *
 *  <dd>ui.click(new TableCellLocator("John Doe", "Age"));</dd>
 *  <dd>ui.enterText("29");</dd>
 *  
 *<br>
 *  <dt>Change the sex field of "Kate Baker"'s record ...</dt>
 *
 *  <dd>ui.click(new TableCellLocator("Kate Baker", "Sex"));</dd>
 *  <dd>ui.click(new ComboItemLocator("Female"));</dd>
 *<br>
 *  <dt>Double click on the "Age" column header...</dt>
 *
 *  <dd>ui.click(2, new TableCellLocator(0, "Age")); </dd>    
 *</dl>             
 */
public class TableCellLocator extends TableItemLocator
	implements HasText
{
	private static final long serialVersionUID = -7312641272372817767L;
	
	public static final int UNSPECIFIED = -1;
	
	/** the row no, with 0 being column header, 1 being first row */
	private int _row = UNSPECIFIED;
	/** the name or text for the column */
	private String _colNameOrText = null;
	/** the name or text for row */
	private String _rowNameOrText = null;
	
	/**
	 * Constructor for the table cell locator
	 * @param row - the row index
	 * @param column - the column index
	 * @throws Exception 
	 */
	public TableCellLocator(int row, int column) throws WidgetSearchException{
		super();
		_row = row;
		if (column !=0)
			_column = column - 1;	
		else throw new WidgetSearchException("Column index starts from 1 for first column; " +
						"0 is an invalid index");
	}
	
	/**
	 * Constructor
	 * @param row - the row index
	 * @param columnName - the name or text for the column
	 */
	public TableCellLocator(int row, String columnName){
		super();
		_row = row;
		_colNameOrText = columnName;
	}

	/**
	 * Constructor
	 * @param rowText - the text to be matched
	 * @param col - the column index
	 * @throws Exception 
	 */
	public TableCellLocator(String rowText,int column) throws WidgetSearchException{
		super();
		_rowNameOrText = rowText;
		if (column != 0)
			_column = column - 1;
		else throw new WidgetSearchException("Column index starts from 1 for first column; " +
			"0 is an invalid index");
	}
	
	/**
	 * Constructor
	 * @param rowText - the text to be matched
	 * @param colName - the column header
	 */
	public TableCellLocator(String rowText,String colName){
		super();
		_rowNameOrText = rowText;
		_colNameOrText = colName;
	}
	
	protected ISWTWidgetMatcher buildMatcher() {
//		IWidgetMatcher matcher = 
//			new AdapterFactory()
//				.adapt(new TableCellMatcher(getRow(),getColumn(),getRowNameOrText(),getColNameOrText()));
//		if (getParentInfo() != null)
//			matcher = new SWTHierarchyMatcher(matcher, getIndex(), getParentInfo());
//		return matcher;
		
		TableCellMatcher matcher = new TableCellMatcher(getRow(),getColumn(),getRowNameOrText(),getColNameOrText());		
		if (getParentInfo() != null)
			return matcher.in(getIndex(), InternalMatcherBuilder.adaptToMatcher(getParentInfo()));
		return matcher;
		
		
	}
	
	/**
	 * * <strong>PROVISIONAL</strong>. This method has been added as
     * part of a work in progress. There is no guarantee that this API will
     * work or that it will remain the same. Please do not use this API for more than
     * experimental purpose without consulting with the WindowTester team.
     * </p>
     *
	 * Method used specify the parent widget locator 
	 * @param parent the target's parent
	 * @return the TableCellLocator
	 */
	public TableCellLocator in(SWTWidgetLocator parent){
		if (!(parent.getTargetClass().isAssignableFrom(Table.class)))
			parent = new SWTWidgetLocator(Table.class, parent);
		setParentInfo(parent);
		return this;
	}
	
	/**
	 * * <strong>PROVISIONAL</strong>. This method has been added as
     * part of a work in progress. There is no guarantee that this API will
     * work or that it will remain the same. Please do not use this API for more than
     * experimental purpose without consulting with the WindowTester team.
     * </p>
     *
	 * Method used to specify the parent locator, and also indicate the index of the table
	 * in the parent
	 * @param index the target's index relative to its parent
	 * @param parent the target's parent
	 * @return
	 */
	public TableCellLocator in(int index, SWTWidgetLocator parent){
		if (!(parent.getTargetClass().isAssignableFrom(Table.class))){
			parent = new SWTWidgetLocator(Table.class, parent);
			parent.setIndex(index);
		}
		else this.setIndex(index);
		setParentInfo(parent);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference reference, IClickDescription click) throws WidgetSearchException {
		Widget w = (Widget)reference.getWidget();
		if (w instanceof TableItem)
			super.click(ui, reference, click);
		// it is a table column
		else {
			TableColumn tableColumn = (TableColumn)w;
		
			Point offset = getXYOffset(reference, click);
			preClick(reference, offset, ui);
			doClick(tableColumn, click.clicks(),click.modifierMask(),offset);		
			postClick(reference, ui);
		}
		
		//here we add a slight pause -- note that a condition would be better
	
		UIDriver.pause(1500);
			
		return WidgetReference.create(w, this);
	}
	
	@Override
	protected void doClick(TableItemReference item, int clicks, int modifiers,
			Point offset) throws WidgetSearchException {
		if (_column == UNSPECIFIED)
			setColumnIndex(item.getWidget());
		new TableItemSelector2().click(clicks, item.getWidget(), getColumn(), convertPoint(offset), modifiers);
	}
		
	protected void doClick(TableColumn item, int clicks, int modifiers, Point offset) throws WidgetSearchException {
		new TableColumnSelector().click(clicks, item, convertPoint(offset), modifiers);
	//	throw new UnsupportedOperationException("Click on Column Header to be implemented");
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, final WidgetReference widget, final IClickDescription click, String menuItemPath) throws WidgetSearchException {
//		TableItem item = (TableItem) widget.getWidget();
//		if (_column == UNSPECIFIED)
//			setColumnIndex(item);
//		Point offset = getXYOffset(item, click);
//		preClick(item, offset, ui);
//		Widget clicked = new TableItemSelector2().contextClick(item, getColumn(), convertPoint(offset), menuItemPath);
//		postClick(clicked, ui);
		
	
		MenuItemReference clicked = new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return showTableCellContextMenu(widget, click);
			}
		}, menuItemPath);
		return WidgetReference.create(clicked, this);
	}

	// TODO move this into TabelCellReference#showContextMenu(...)
	private MenuReference showTableCellContextMenu(WidgetReference widget, IClickDescription click) {
		TableItem item = (TableItem) widget.getWidget();
		if (_column == UNSPECIFIED)
			setColumnIndex(item);
		org.eclipse.swt.graphics.Point offset = new TableItemSelector2().getTableCellClickOffset(item, _column);
		SWTLocation location = SWTWidgetLocation.withDefaultCenter((ISWTWidgetReference<?>) widget, click, offset);
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, false);
		op.execute();
		return op.getMenu();
	}
	
	
	
	
	private void setColumnIndex(final TableItem item){
		item.getDisplay().syncExec(new Runnable() {
			public void run() {
				Table table = item.getParent();
				TableColumn[] columns = table.getColumns();
				for (int i = 0;i < columns.length;i++)
					if (columns[i].getText().equals(_colNameOrText)){
						setColumn(i);
						break;
					}
			}
		});
		
	}
	
	public int getRow() {
		return _row;
	}

	public void setRow(int row) {
		this._row = row;
	}


	public String getColNameOrText() {
		return _colNameOrText;
	}

	public void setColNameOrText(String nameOrText) {
		_colNameOrText = nameOrText;
	}

	public String getRowNameOrText() {
		return _rowNameOrText;
	}

	public void setRowNameOrText(String nameOrText) {
		_rowNameOrText = nameOrText;
	}

	/**
	 * Create a condition that tests if the given table cell has the 
	 * expected text.
	 */
	public IUICondition hasText(String expectedText) {
		return new HasTextCondition(this, expectedText);
	}
	
	/**
	 * Resolve the locator to a single cell and answer the text associated with it.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the text associated with that object (may be null)
	 */
	public String getText(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator found = ui.find(this);
		if (found instanceof IWidgetReference) {
			final Object widget = ((IWidgetReference) found).getWidget();
			if (widget instanceof TableItem) {
				final TableItem item = (TableItem) widget;
				final String[] result = new String[1];
				final Exception[] exception = new Exception[1];
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						try {
							int column = getColumn();
							//This is a crude hack -- there is temporal coupling for the named column case
							//Essentially, column index is cached DURING A CLICK -- if no click is made, column is not resolved
							//To work-around, we set the column (as in a click) if it is unset
							if (column == UNSPECIFIED_COLUMN)
								setColumnIndex(item);
							
							result[0] = item.getText(getColumn());
						}
						catch (Exception e) {
							exception[0] = e;
						}
					}
				});
				if (exception[0] != null)
					throw new WidgetSearchException(exception[0]);
				return result[0];
			}
			if (widget instanceof TableColumn){
				final TableColumn item = (TableColumn) widget;
				final String[] result = new String[1];
				final Exception[] exception = new Exception[1];
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						try {
							result[0] = item.getText();
						}
						catch (Exception e) {
							exception[0] = e;
						}
					}
				});
				if (exception[0] != null)
					throw new WidgetSearchException(exception[0]);
				return result[0];
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#toString()
	 */
	public String toString() {
		if (_row != UNSPECIFIED && _column != UNSPECIFIED)
			return "TableCellLocator(\"" + getRow()+ ","+ (getColumn()+1) +"\")";
		if (_row != UNSPECIFIED && _colNameOrText != null)
			return "TableCellLocator(\"" + getRow() + ","+ getColNameOrText()+"\")";
			
		return "TableCellLocator";
	}
	
	
	

}
