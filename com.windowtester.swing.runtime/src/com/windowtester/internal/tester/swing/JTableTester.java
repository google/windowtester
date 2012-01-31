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
package com.windowtester.internal.tester.swing;

import java.awt.Component;

import javax.swing.JTable;

import abbot.tester.JTableLocation;

/** Provide user actions on a JTable.
    The JTable substructure is a "Cell", and JTableLocation provides different
    identifiers for a cell.
    <ul>
    <li>Select a cell by row, column index
    <li>Select a cell by value (its string representation)
    </ul>

    @see abbot.tester.JTableLocation
    
    Added multi select support
 */

public class JTableTester extends abbot.tester.JTableTester {

    
    /** Select the given cell, if not already. */
    public void actionSelectCell(Component c, JTableLocation loc,int mask) {
        JTable table = (JTable)c; 
        JTableLocation.Cell cell = loc.getCell(table);
        if (table.isRowSelected(cell.row)
            && table.isColumnSelected(cell.col)
            && table.getSelectedRowCount() == 1) {
            return;
        }
        actionClick(c, loc, mask);
        
    }
    
    /** double click the given cell, if not already. */
    public void actionSelectCell(int count,Component c, JTableLocation loc,int mask) {
        JTable table = (JTable)c; 
        JTableLocation.Cell cell = loc.getCell(table);
        if (table.isRowSelected(cell.row)
            && table.isColumnSelected(cell.col)
            && table.getSelectedRowCount() == 1) {
            return;
        }
        actionClick(c, loc, mask, count);
        
    }
    
    
   
    /** Select the given cell, if not already.
     	Equivalent to actionSelectCell(c, new JTableLocation(row, col)).
    */
    public void actionSelectCell(Component c, int row, int col, int mask) {
    	actionSelectCell(c, new JTableLocation(row, col), mask);
    }

    
   
}
