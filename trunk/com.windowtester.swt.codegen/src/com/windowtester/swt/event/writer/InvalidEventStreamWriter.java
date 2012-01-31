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
package com.windowtester.swt.event.writer;

import java.util.List;

import com.windowtester.codegen.ICodeGenPluginTraceOptions;
import com.windowtester.internal.debug.Tracer;


public class InvalidEventStreamWriter implements IEventStreamWriter {

    private String _msg;

    /**
     * Create an instance.
     * @param msg - a message string describing the nature of the stream writing error
     */
    public InvalidEventStreamWriter(String msg) {
        _msg = msg;
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.swt.event.writer.IEventStreamWriter#write(java.util.List)
     */
    public void write(List events) {
//        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//        MessageDialog.openError(shell, Messages.getString("InvalidEventStreamWriter.STREAM_ERROR_TITLE"), _msg); //$NON-NLS-1$
    	Tracer.trace(ICodeGenPluginTraceOptions.BASIC, "write called on invalid stream writer: " + _msg);
    }

    
    
    
}
