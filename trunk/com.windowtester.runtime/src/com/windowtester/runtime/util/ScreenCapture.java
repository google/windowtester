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
package com.windowtester.runtime.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


import com.windowtester.internal.debug.LogHandler;


/**
 * Dumps a <em>png</em> of the current screen in the standard "wintest" output directory (as defined by {@link ScreenCapture#getOutputLocation()}).  
 * Since taking screen shots consumes considerable resources, heap limits may be exceeded during a capture.  To address
 * this, {@link OutOfMemoryError}s are caught in the default handler implementation and the capture retried after a short interval 
 * ({@link ScreenCapture#CAPTURE_RETRY_INTERVAL}).  The number of retries is bounded by {@link ScreenCapture#MAX_CAPTURE_RETRIES}.
 * <p>
 * Screen capture behavior can be specialized:
 * 
 * <p>
 * <b>1. Output Location.</b> Capture output can be redirected by calling {@link ScreenCapture#setOutputLocation(String)}.
 * <p>
 * <b>2. Screen Capture Handling.</b> The default screen capture implementation can be overridden by calling {@link ScreenCapture#setHandler(IScreenCaptureHandler)}.  For example, the
 * following user-defined handler will override the default behavior by doing nothing, essentially disabling screenshots.
 * 
 * <pre>
 * ScreenCapture.setHandler(new IScreenCaptureHandler() {
 *    public File createScreenCapture(String name) {
 *        //no-op
 *        return null;
 *    }
 *  });
 * </pre>
 * 
 *
 */
public class ScreenCapture {
	
	
	public static final int MAX_CAPTURE_RETRIES    = 5;
	public static final int CAPTURE_RETRY_INTERVAL = 3000;
	
	
    private static final String WINDOW_TESTER_SCREEN_CAPTURE_PATHS_KEY = "WindowTesterScreenCapturePaths";
	private static final String BASE_IMAGE_NAME = "screenshot";
    private static final String IMAGE_EXT = "png"; // best format for ui graphics
	
    private static final String PATH_DELIM = System.getProperty("file.separator");
    
    //TODO: make this use configurable
    private static String OUTPUT_DIR = "wintest";

    // increment counter for unique screenshot file names
    // on a per run basis
    private static int _counter = 0;

    // keep a static Robot around to do the screencapture
    private static Robot _robot;
    
    static {
        try {
            _robot = new Robot();
        } catch ( AWTException e ) {
        	LogHandler.log(e);
        }
    }
    
    /**
     * A user-defined screen capture strategy.
     *
     */
    public static interface IScreenCaptureHandler {
    	
    	/**
    	 * Create a screenshot file with the given name.
    	 * @param name - the name of the screenshot
    	 * @return a screenshot file (note: may be <code>null</code>
    	 */
    	File createScreenCapture(String name);
    }
        
    private static class DefaultScreenCaptureHandler implements IScreenCaptureHandler {
       

    	
    	
    	/**
         * Save the screen pixels as a PNG image file in the current directory.
         * Existing screen cap files will be overwritten.
         * 
         * The name parameter will be used as a prefix for the name of the 
         * produced image.
         * 
         * TODO parameterize the image file location
         * 
         * @return the file into which the image was stored, 
         * 		or <code>null</code> if the image could not be stored.
         */
        public File createScreenCapture(String name) {        	
        	BufferedImage image = captureScreen();
        	if (image == null)
        		return null;
            return createScreenCaptureFile(image, name);
        }
        
    	public static BufferedImage captureScreen() {
    		// determine current screen size
    		Toolkit toolkit = Toolkit.getDefaultToolkit();
    		Dimension screenSize = toolkit.getScreenSize();
    		Rectangle screenRect = new Rectangle( screenSize );
    		
    		
    		for (int i=0; i < MAX_CAPTURE_RETRIES; ++i) {	
        		try {
        			return _robot.createScreenCapture( screenRect );
        		} catch (OutOfMemoryError e) {
        			LogHandler.log("OutOfMemoryError caught in screen capture (attempt [" + i + "])");
        			try {
						Thread.sleep(CAPTURE_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
						//just continue
					}
        		}
    		}
    		LogHandler.log("Screen Capture failed");
    		return null;
    	}
    	
    	public static File createScreenCaptureFile(BufferedImage image, String name) {
    		File file;
            try {
                
                ensureOutputDirExists();
    			// save captured image to PNG file
                String path = getOutputLocation() + name + "_" + BASE_IMAGE_NAME + "_" + _counter++ + "." + IMAGE_EXT;
    			file = new File(path);
                /*boolean writerFound = */ ImageIO.write( image, IMAGE_EXT, file );
                //System.out.println(writerFound);
            }
            catch ( IOException e) {
            	LogHandler.log(e);
            	return null;
            }
            
            // If there was a successful screencapture and the
    		// WindowTesterXMLJunitResultFormatter is in use then record the screencapture so
    		// that WindowTesterXMLJunitResultFormatter can report it as a test artifact
            String paths = System.getProperty(WINDOW_TESTER_SCREEN_CAPTURE_PATHS_KEY);
            if (paths != null) {
            	if (paths.length() > 0)
            		paths += ",";
            	paths += file.getAbsolutePath();
            	System.setProperty(WINDOW_TESTER_SCREEN_CAPTURE_PATHS_KEY, paths);
            } 
            return file;
    	}
    	
        


        
    }
    
	private static void ensureOutputDirExists() {
		File dir = new File(OUTPUT_DIR);
		if (!dir.exists())
			dir.mkdir();
	}
    
    
    /**
     * Get the output directory for screenshots.
	 */
	public static String getOutputLocation() {
		return OUTPUT_DIR + PATH_DELIM;
	}

	private static IScreenCaptureHandler handler;
    
    private static IScreenCaptureHandler getDefaultHandler() {
 //   	return new SWTGCScreenCaptureHandler();
    	return new DefaultScreenCaptureHandler();
    }
    
    /**
     * Override the default screen capture strategy with a user-defined one.
     * @param handler - a user-defined screen capture strategy, or <code>null</code> to reset the default
     */
    public static void setHandler(IScreenCaptureHandler handler) {
    	ScreenCapture.handler = handler;
    }
    
    
    /**
     * purely static class, no instances allowed
     */
    private ScreenCapture()
    {
    }

    /**
     * Save the screen pixels as a PNG image file in the current directory
     * (see {@link #createScreenCapture(String)}).
     * 
     * @return the file into which the image was stored, 
     * 		or <code>null</code> if the image could not be stored.
     */
	public static File createScreenCapture() {
		return createScreenCapture(TestMonitor.getInstance().getCurrentTestCaseID());
	}

    /**
     * Save the screen pixels as a PNG image file in the current directory.
     * Existing screen cap files will be overwritten.
     * 
     * The name parameter will be used as a prefix for the name of the 
     * produced image.
     * 
     * TODO parameterize the image file location
     * 
     * @return the file into which the image was stored, 
     * 		or <code>null</code> if the image could not be stored.
     */
    public static File createScreenCapture(String name) {
        return getHandler().createScreenCapture(name);
    }

    private static final IScreenCaptureHandler getHandler() {
    	if (handler == null)
    		handler = getDefaultHandler();
    	return handler;
    }



    /**
     * Set the output path relative to the eclipse base directory.
     */
    public static void setOutputLocation(String path) {
    	if (path == null)
    		throw new IllegalArgumentException("Path must not be null");
    	OUTPUT_DIR = path;
    }
    

}