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
package com.windowtester.finder.matchers.swt;


/**
 * Matcher to locate a cell in a table
 *
 * For row index 0 is the column header
 * 
 * @author Keerti P
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 */
public class TableCellMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.TableCellMatcher {
	
	
	public TableCellMatcher(int row, int col,String rowNameOrText,String colNameOrText){
		super(row, col, rowNameOrText, colNameOrText);
	}

	public TableCellMatcher(int row, String colNameOrText){
		super(row, colNameOrText);
	}
	
	public TableCellMatcher(String rowNameOrText,int col){
		super(rowNameOrText, col);
	}
		
}
