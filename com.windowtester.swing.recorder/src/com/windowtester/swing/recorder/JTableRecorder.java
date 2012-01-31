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
package com.windowtester.swing.recorder;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.UISemanticEventFactory;

import abbot.script.Action;
import abbot.script.ComponentReference;
import abbot.script.Resolver;
import abbot.script.Step;

/**
 * Record basic semantic events you might find on an JTable. <p>
 * <ul>
 * <li>Click one or more times in a cell
 * </ul>
 * 
 * Added WindowTester semantic event generation
 */
public class JTableRecorder extends JComponentRecorder {

	  public JTableRecorder(Resolver resolver) {
	         super(resolver);
	   }

	 
	    /**
	     * Override
	     */
		protected Step createPopupMenuSelection(Component invoker, int x, int y, Component menuItem) {
	    	// create windowtester semantic event
			
			if (menuItem == null)
				return null;
	    	IUISemanticEvent semanticEvent = 
				UISemanticEventFactory.createTableContextMenuSelectionEvent((JTable)invoker,x,y,(JMenuItem)menuItem);
	    	notify(semanticEvent);
	    	doneEventGeneration = true;	
			return super.createPopupMenuSelection(invoker, x, y, menuItem);
		}


		/** Normally, a click in a table results in selection of a given cell. */
	    protected Step createClick(Component target, int x, int y,
	                               int mods, int count) {
	        JTable table = (JTable)target;
	        Point where = new Point(x, y);
	        int row = table.rowAtPoint(where);
	        int col = table.columnAtPoint(where);
	        
	        String mask = null;
	       
	        
	        ComponentReference cr = getResolver().addComponent(target);
	        String methodName = "actionSelectCell";
	        ArrayList args = new ArrayList();
	        args.add(cr.getID());
	        args.add(getLocationArgument(table, x, y));
	        if (row == -1 || col == -1) {
	            methodName = "actionClick";
	        }
	        if ((mods != 0 && mods != MouseEvent.BUTTON1_MASK)
	            || count > 1) {
	            methodName = "actionClick";
	            mask = abbot.util.AWT.getMouseModifiers(mods);
	            args.add(mask);
	            if (count > 1) {
	                args.add(String.valueOf(count));
	            }
	        }
	       
	        // windowtester semantic event generation
	        IUISemanticEvent semanticEvent = 
				UISemanticEventFactory.createTableSelectionEvent((JTable)target,x,y,mask,count,getButton());
	    	notify(semanticEvent);
	        
	        
	        return new Action(getResolver(), null, methodName,
	                          (String[])args.toArray(new String[args.size()]),
	                          javax.swing.JTable.class);
	    }
	    
}
