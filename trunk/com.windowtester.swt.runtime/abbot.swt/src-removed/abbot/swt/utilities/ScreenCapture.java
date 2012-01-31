package abbot.swt.utilities;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * dumps a png of the current screen in the current directory
 * 
 */
public class ScreenCapture
{
    private static final String BASE_IMAGE_NAME = "screenshot";
    private static final String IMAGE_EXT = "png"; // best format for ui graphics

    // increment counter for unique screenshot file names
    // on a per run basis
    private static int _counter = 0;

    // keep a static Robot around to do the screencapture
    private static Robot _robot;
    
    static
    {
        try
        {
            _robot = new Robot();
        }
        catch ( AWTException e )
        {
            throw new RuntimeException( "Could not instantiate awt Robot for screen capture", e );
        }
    }
    
    /**
     * purely static class, no instances allowed
     */
    private ScreenCapture()
    {
    }

    /**
     * Save the screen pixels as a PNG image file in the current directory.
     * Existing screen cap files will be overwritten.
     * 
     * The name parameter will be used as a prefix for the name of the 
     * produced image.
     * 
     * TODO parameterize the image file location
     */
    public static void createScreenCapture(String name)
    {
        try
        {
            // determine current screen size
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            Rectangle screenRect = new Rectangle( screenSize );
            // create screen shot using an AWT Robot
            BufferedImage image = _robot.createScreenCapture( screenRect );
            // save captured image to PNG file
            ImageIO.write( image, IMAGE_EXT, new File( name+"_"+BASE_IMAGE_NAME + "_"
                + _counter++ + "." + IMAGE_EXT ) );
        }
        catch ( IOException e )
        {
            System.err.println("Error creating screen capture: " + e.getMessage() );
        }
    }
}
