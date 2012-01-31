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
import java.awt.event.InputEvent;

import javax.swing.JList;

import abbot.i18n.Strings;
import abbot.tester.ActionFailedException;
import abbot.tester.JListLocation;

/***
 *  Extend the Abbot JListTester to add mutliple selection capability.
 *  Implemented double click funcionality
 */
public class JListTester extends abbot.tester.JListTester {
	
	/** Select the first item in the list matching the given String
    representation of the item.<p>
    Equivalent to actionSelectRow(c, new JListLocation(item),buttons).
	*/
	public void actionSelectItem(Component c, String item,int buttons) {
	    actionSelectRow(c, new JListLocation(item),buttons);
	}
		
	
	/** Select the first value in the list matching the given String
	representation of the value.<p>
	Equivalent to actionSelectRow(c, new JListLocation(value),buttons).
	*/
	public void actionSelectValue(Component c, String value, int buttons) {
	actionSelectRow(c, new JListLocation(value),buttons);
	}

	
	/** Select the given row.  Does nothing if the index is already
     * selected.
     */
    public void actionSelectRow(Component c, JListLocation location,int buttons) {
        JList list = (JList)c;
        int index = location.getIndex(list);
        if (index < 0 || index >= list.getModel().getSize()) {
            String msg = Strings.get("tester.JList.invalid_index",
                                     new Object[] { new Integer(index) });
            throw new ActionFailedException(msg);
        }
        if (list.getSelectedIndex() != index) {
        				
        	super.actionClick(c,location,buttons);
        }
    }
    
    
    /** Double click on the first item  matching the given String
    representation of the item.<p>
    Equivalent to doubel click on actionSelectRow(c, new JListLocation(item)).
	*/
	public void actionMultipleClick(Component c, int clickCount, String item) {
	    actionSelectRow(c, clickCount, new JListLocation(item),InputEvent.BUTTON1_MASK);
	}
	
	
	/**
	 * click with mask specified
	 */
	public void actionMultipleClick(Component c,int clickCount,String item,int mask){
		actionSelectRow(c, clickCount, new JListLocation(item),mask);
	}
	/** click on the given row, with the given clickCount
     * 
     */
    public void actionSelectRow(Component c, int clickCount,JListLocation location,int mask) {
        JList list = (JList)c;
        int index = location.getIndex(list);
        if (index < 0 || index >= list.getModel().getSize()) {
            String msg = Strings.get("tester.JList.invalid_index",
                                     new Object[] { new Integer(index) });
            throw new ActionFailedException(msg);
        }
   //     if (list.getSelectedIndex() != index) {
        				
        	super.actionClick(c,location,mask,clickCount);
    //    }
    }
	

}
