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
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import abbot.i18n.Strings;
import abbot.tester.ComponentLocation;
import abbot.tester.JTreeLocation;
import abbot.tester.LocationUnavailableException;

/***
 *  Copy of abbot JTreeTester
 *  Added support for multiple selection for JTree
 */
public class JTreeTester extends abbot.tester.JTreeTester {
	
	
	/** Select the given path, expanding parent nodes if necessary. */
    public void actionSelectPath(int clickCount,Component c, TreePath path,int buttons) {
        actionSelectRow(clickCount,c, new JTreeLocation(path),buttons);
    }

    
    /** Select the given row.  If the row is already selected, does nothing. */
    public void actionSelectRow(int clickCount,Component c, ComponentLocation loc, int buttons) {
        JTree tree = (JTree)c;
        if (loc instanceof JTreeLocation) {
            TreePath path = ((JTreeLocation)loc).getPath((JTree)c);
            if (path == null) {
                String msg = Strings.get("tester.JTree.path_not_found",
                                         new Object[] { loc });
                throw new LocationUnavailableException(msg);
            }
            makeVisible(c, path);
        }
        Point where = loc.getPoint(c);
        int row = tree.getRowForLocation(where.x, where.y);
        if (tree.getLeadSelectionRow() != row
            || tree.getSelectionCount() != 1) {
            // NOTE: the row bounds *do not* include the expansion handle
            Rectangle rect = tree.getRowBounds(row);
            // NOTE: if there's no icon, this may start editing
            actionClick(tree, rect.x + 1, rect.y + rect.height/2,buttons,clickCount);
        }
    }
    
   
    
}
