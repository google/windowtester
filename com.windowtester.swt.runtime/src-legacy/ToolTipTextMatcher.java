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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.matchers.swt.AbstractMatcher;
import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;

/**
 * A matcher for identifying widgets by their tooltip text.
 * 
 * @author Phil Quitslund
 */
public class ToolTipTextMatcher extends AbstractMatcher {

	/** The tooltip to match on */
	private final String _toolTip;

	/**
	 * @param toolTip
	 */
	public ToolTipTextMatcher(String toolTip) {
		_toolTip = toolTip;
	}
	
	/* (non-Javadoc)
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(Widget w) {		
		String toolTipText = "";
		if (w instanceof ToolItem) {
			ToolItem item = (ToolItem)w;
			toolTipText = getToolTipText(item);
		} else if (w instanceof Control) {
			Control c = (Control)w;
			toolTipText = getToolTipText(c);
		} else {
			return false; 		//TODO: there are other types that have tooltips...
								//this test should be expanded to include them.
		}
		//System.err.println(w.getClass() + " -> " + toolTipText);
		return stringsMatch(_toolTip, toolTipText);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Proxy methods
	//
	////////////////////////////////////////////////////////////////////////////

    /**
     * Proxy for {@link ToolItem#getToolTipText()}. <p/>
     * 
     * @param item - the tool item under test.
     * @return the item's tool tip text
     */
    public String getToolTipText(final ToolItem item) {
        String result = (String) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getToolTipText();
                    }
                });
        return result;
    }
	
    
    /**
     * Proxy for {@link Control#getToolTipText()}. <p/>
     * 
     * @param control - the control under test.
     * @return the control's tool tip text
     */
    public String getToolTipText(final Control control) {
        String result = (String) Robot.syncExec(control.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return control.getToolTipText();
                    }
                });
        return result;
    }
	
    public String toString() {
        return "ToolTipText matcher (" + _toolTip + ")";
    }
    
}
