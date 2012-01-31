package abbot.tester;

import java.awt.image.BufferedImage;
import java.io.*;

import abbot.Log;

import com.sun.image.codec.jpeg.*;

/**
   This code expects the availability of the com.sun.image.codec.jpeg
   extensions from the Sun JDK 1.3 or JRE. 

   Original comparison code contributed by asmithmb.

   author: asmithmontebello@aol.com, twall
*/
public class ImageComparator implements java.util.Comparator {

    private FileComparator comparator = new FileComparator();

    private static File convertToJPEGFile(Object obj)
        throws IOException {
        if (obj != null) {
            if (obj instanceof String) {
                obj = new File((String)obj);
            }
            if (obj instanceof BufferedImage) {
                File file = File.createTempFile("ImageComparator", ".jpg");
                Log.debug("Creating " + file);
                writeJPEG(file, (BufferedImage)obj);
                file.deleteOnExit();
                return file;
            }
            if (obj instanceof File) {
                return (File)obj;
            }
        }
        return null;
    }

    /** Write the given buffered image to disk. */
    public static void writeJPEG(File file, BufferedImage img)
        throws IOException {
//NOT Supported in Mac Java5+
//        FileOutputStream os = new FileOutputStream(file);
//        JPEGImageEncoder ie = JPEGCodec.createJPEGEncoder(os);
//        JPEGEncodeParam param = ie.getDefaultJPEGEncodeParam(img);
//        // Lossless, please
//        param.setQuality(1.0f, false);
//        ie.setJPEGEncodeParam(param);
//        ie.encode(img);
//        os.close();
    	throw new UnsupportedOperationException();
    }

    /**
       Compare two images.  May be BufferedImages or File arguments.
    */
    public int compare(Object obj1, Object obj2) {
        try {
            obj1 = convertToJPEGFile(obj1);
        }
        catch(IOException io) {
            obj1 = null;
        }
        try {
            obj2 = convertToJPEGFile(obj2);
        }
        catch(IOException io) {
            obj2 = null;
        }
        Log.debug("Comparing " + obj1 + " and " + obj2);
        return comparator.compare(obj1, obj2);
    }

    /** Comparators are equal if they're the same class. */
    public boolean equals(Object obj) {
        return obj == this
            || (obj != null && obj.getClass().equals(getClass()));
    }
}
