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

import abbot.script.Resolver;

/**
 * Record basic semantic events you might find on an Container. <p>
 * copy of abbot.editor.recorder.ContainerRecorder
 * extends windowtester.ComponentRecorder
 */
public class ContainerRecorder extends ComponentRecorder {
	 public ContainerRecorder(Resolver resolver) {
	        super(resolver);
	    }
}
