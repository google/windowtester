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
package com.windowtester.swt.event.recorder;


/**
 * A service that provides access to recorder configuration information.
 */
public class RecorderSettings {

//    /**
//     * For debugging purposes we have a program argument flag that allows one to
//     * trigger recording of an entire workspace session.  If this flag is set,
//     * a thread will be spawned which will trigger recording as
//     * soon as the UI is up.
//     */
//    private static final String RECORD_FLAG = "-record"; //$NON-NLS-1$
    
    
    /**
     * Check to see whether a flag has been set indicating that the recorder
     * should be started as soon as the UI is up.
     * @return true if the record flag has been set
     */
    public boolean isRecordFlagIsSet() {
//        String[] args = Platform.getApplicationArgs();
//        for (int i = 0; i < args.length; i++) {
//            if (args[i].equals(RECORD_FLAG))
//                return true;
//        }
        return false;
    }

//    /**
//     * Get an event stream writer associated with the current settings.
//     * @return an event stream writer
//     */
//    public static IEventStreamWriter getStreamWriter() {
//        // TODO will first see if a headless option has been set
//        // if !set -> 
////        File output = promptUserForFile();
////        if (output == null) 
//            return new InvalidEventStreamWriter(Messages.getString("RecorderSettings.INVALID_NAME_MSG")); //$NON-NLS-1$
////        return new EventStreamWriter(output);
//    }


//    /**
//     * Prompt the user for a file name
//     * @return the file selected
//     */
//    private static File promptUserForFile() {
//        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//        FileDialog dialog = new FileDialog(shell, SWT.SINGLE);
//        dialog.setFilterPath(CodeGenSettings.getStreamMetadataPath());
//        dialog.setFileName(CodeGenSettings.getFreshStreamFilenameProposal());
//        dialog.setFilterExtensions(new String[]{"*" + CodeGenSettings.STREAM_EXTENSION});
//        dialog.setText(Messages.getString("WriteRecordingAction.OUTPUT_FILE")); //$NON-NLS-1$
//        String res = dialog.open();
//        return res == null ? null : new File(res);
//    }
}
