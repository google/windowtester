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
import java.awt.event.InputEvent;

import javax.swing.JTree;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.UISemanticEventFactory;

import abbot.script.Action;
import abbot.script.ComponentReference;
import abbot.script.Resolver;
import abbot.script.Step;


/**
 * abbot.editor.recorder.AbstractButtonRecorder
 * Record basic semantic events you might find on an AbstractButton.  This
 * class handles a click on the button.
 * 
 * added windowtester semantic event generation
 */
public class AbstractButtonRecorder extends JComponentRecorder {

	public AbstractButtonRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Usually don't bother tracking drags/drops on buttons. */
    protected boolean canDrag() {
        return false;
    }

    /** Usually aren't interested in multiple clicks on a button. */
    protected boolean canMultipleClick() {
        return false;
    }

    /** Create a button-specific click action. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        // No need to store the coordinates, the center of the button is just
        // fine.   Only care about button 1, though.
    	
    	// generate windowtester semantic event
    	if (mods == 0 || mods == InputEvent.BUTTON1_MASK){
    		IUISemanticEvent semanticEvent = 
    			UISemanticEventFactory.createWidgetSelectionEvent(target,x,y,count,getButton());
    		notify(semanticEvent);
    	 }
    	
    	
        ComponentReference cr = getResolver().addComponent(target);
        if (mods == 0 || mods == InputEvent.BUTTON1_MASK)
            return new Action(getResolver(), 
                              null, "actionClick",
                              new String[] { cr.getID() },
                              javax.swing.AbstractButton.class);
        return null;
    }
}
