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

import java.awt.AWTEvent;

import abbot.script.Resolver;

/**
 * Override AbstractButton behavior, since we expect to grab a menu selection
 * instead of a click.
 */

public class JMenuItemRecorder extends AbstractButtonRecorder {

	public JMenuItemRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Regular clicks get treated as a menu event. */
    protected boolean isMenuEvent(AWTEvent e) {
        return isClick(e);
    }
}
