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

import java.io.File;

import com.windowtester.codegen.util.FileNameProposer;


/**
 * A service for codegen-related constants and settings.
 */
public class CodeGenSettings {
    
    //the default location
    private static String _outputDirPath = System.getProperty("user.home")
            + System.getProperty("file.separator") + "WindowTester";
    public static final String STREAM_EXTENSION = ".xml";
    
    private static String _streamDataPath = _outputDirPath;
    
    //FIXME: put this some place proper
    static {
        File defaultDir = new File(_outputDirPath);
        if (!defaultDir.exists())
            defaultDir.mkdir();
    }
    
    /**
     * Get the output directory path, defaulting to "user.home/WindowTester" if
     * none is set.
     * 
     * @return the output directory path
     */
    public static String getOutputDirPath() {
        return _outputDirPath;
    }
    
    /**
     * Get the path to stored/storing stream data.
     * @return the cached stream data path
     */
    public static String getStreamMetadataPath() {
        return _streamDataPath;
    }
    
    /**
     * Set the path to stored/storing stream data.
     * @param path - the new path
     */
    public static void setStreamMetadataPath(String path) {
        _streamDataPath = path;
    }
    
    /**
     * Set the output directory path
     * @param outputDirPath
     */
    public static void setOutputDirPath(String outputDirPath) {
        _outputDirPath = outputDirPath;
    }

    /**
     * Generates a file name proposal based on the most recently edited file in the
     * current output directory.  In case of an IO error, "null" is returned.
     * @return an output filename proposal
     */
    public static String getFreshStreamFilenameProposal() {
        return new FileNameProposer("recording1.xml", new File(getStreamMetadataPath())).propose();
    }

    public static File getMostRecentlyModifiedFile(String extension, File dir) {
        return FileNameProposer.getMostRecentltyModifiedFile(dir, extension);
    }
    
}
