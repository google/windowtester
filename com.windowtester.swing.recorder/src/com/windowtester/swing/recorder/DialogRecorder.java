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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;

import abbot.script.Action;
import abbot.script.ComponentReference;
import abbot.script.Resolver;
import abbot.script.Step;

/**
 * Record basic semantic events you might find on an Window. <p>
 */
public class DialogRecorder extends ComponentRecorder {

	public DialogRecorder(Resolver resolver) {
        super(resolver);
    }

    protected Step createResize(Window window, Dimension size) {
    	// TODO: add windowtester semantic event generation
        Step step = null;
        if (((Dialog)window).isResizable()) {
            ComponentReference ref = getResolver().addComponent(window);
            step = new Action(getResolver(), 
                              null, "actionResize",
                              new String[] { ref.getID(),
                                             String.valueOf(size.width),
                                             String.valueOf(size.height),
                              }, Dialog.class);
        }
        return step;
    }
}
