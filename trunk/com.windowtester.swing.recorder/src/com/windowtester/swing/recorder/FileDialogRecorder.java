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

import java.awt.*;
import java.awt.event.WindowEvent;

import abbot.script.*;

/**
 * Recorder for the java.awt.FileDialog.  Since this is a native component and
 * no java events are generated other than window open/close, the only things
 * to take note of are the following:<br>
 * <ul>
 * <li>Changes to the directory
 * <li>Changes to the file
 * <li>Whether the user hits OK or Cancel
 * </ul>
 * @author Vrata Venet, European Space Agency, Madrid-Spain (av@iso.vilspa.esa.es)
 */
public class FileDialogRecorder extends DialogRecorder{

    private FileDialog dialog = null;
    private String originalFile = null;
    private String originalDir = null;

    /** Create a FileDialogRecorder for use in capturing the semantics of a GUI
     * action.
     */
    public FileDialogRecorder(Resolver resolver) {
        super(resolver);
    }
  
    protected void init(int type) {
        super.init(type);
        dialog = null;
        originalFile = null;
        originalDir = null;
    }

    /** Override the default window parsing to consume everything between
        dialog open and close.
    */
    protected boolean parseWindowEvent(AWTEvent event) {
        boolean consumed = true;
        if (event.getSource() instanceof FileDialog) {
            if (isOpen(event)) {
                dialog = (FileDialog)event.getSource();
                originalFile = dialog.getFile();
                originalDir = dialog.getDirectory();
            }
            // The FileDialog robot uses some event listener hacks to set the
            // correct state on dialog close; make sure we record after that
            // listener is finished.
            if (event instanceof FileDialogTerminator)
                setFinished(true);
            else if (event.getSource() == dialog && isClose(event)) {
                AWTEvent terminator = 
                    new FileDialogTerminator(dialog, event.getID());
                dialog.getToolkit().getSystemEventQueue().
                    postEvent(terminator);
            }
        }
        return consumed;
    }
  
    /** Create one or more steps corresponding to what was done to the file
        dialog.  If the directory is non-null, the directory was changed.  If
        the file is non-null, the file was accepted.
    */
    protected Step createFileDialogEvents(FileDialog d,
                                          String oldDir, String oldFile) {
        ComponentReference ref = getResolver().addComponent(d);
        String file = d.getFile();
        boolean accepted = file != null;
        boolean fileChanged = accepted && !file.equals(oldFile);
        String dir = d.getDirectory();
        boolean dirChanged = dir != oldDir
            && (dir == null || !dir.equals(oldDir));
        
        String desc = d.getMode() == FileDialog.SAVE
            ? "Save File" : "Load File";
        if (accepted)
            desc += " (" + file + ")";
        else
            desc += " (canceled)";
        Sequence seq = new Sequence(getResolver(), desc);
        if (dirChanged) {
            seq.addStep(new Action(getResolver(),
                                   null, "actionSetDirectory",
                                   new String[] { ref.getID(), dir },
                                   FileDialog.class));
        }
        if (accepted) {
            Step accept = new Action(getResolver(),
                                     null, "actionAccept",
                                     new String[] { ref.getID() },
                                     FileDialog.class);
            if (fileChanged) {
                seq.addStep(new Action(getResolver(),
                                       null, "actionSetFile",
                                       new String[] { ref.getID(), file },
                                       FileDialog.class));
                seq.addStep(accept);
            }
            else {
                return accept;
            }
        }
        else {
            Step cancel = new Action(getResolver(),
                                     null, "actionCancel",
                                     new String[] { ref.getID() },
                                     FileDialog.class);
            if (dirChanged)
                seq.addStep(cancel);
            else
                return cancel;
        }
        return seq;
    }

    protected Step createStep() {
        if (getRecordingType() == SE_WINDOW) {
            return createFileDialogEvents(dialog, originalDir, originalFile);
        }
        else {
            return super.createStep();
        }
    }

    private class FileDialogTerminator extends WindowEvent {
        public FileDialogTerminator(FileDialog fd, int id) {
            super(fd, id);
        }
    }
}
