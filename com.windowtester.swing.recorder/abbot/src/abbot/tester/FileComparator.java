package abbot.tester;

import java.io.*;

/** 
 * Compare two files or filenames.  Original concept contributed by A. Smith
 * Montebello. 
 * @author asmithmb
 * @version 1.0
 */

public class FileComparator implements java.util.Comparator {

    /** 
     *  Read files into streams and call byte by byte comparison method
     *  @param f1 First File or filename to compare.
     *  @param f2 Second File or filename to compare.
     */
    public int compare(Object f1, Object f2) {

        if ((f1 == f2) || (f1 != null && f1.equals(f2)))
            return 0;
        // Call null < object
        if (f1 == null)
            return -1;
        // Call object > null
        if (f2 == null)
            return 1;

        File file1, file2;
        if (f1 instanceof File) {
            file1 = (File)f1;
        }
        else if (f1 instanceof String) {
            file1 = new File((String)f1);
        }
        else {
            throw new IllegalArgumentException("Expecting a File or String");
        }
        if (f2 instanceof File) {
            file2 = (File)f2;
        }
        else if (f2 instanceof String) {
            file2 = new File((String)f2);
        }
        else {
            throw new IllegalArgumentException("Expecting a File or String");
        }
        if (file1.equals(file2)) {
            return 0;
        }

        if (!file1.exists() || !file1.isFile()) {
            throw new IllegalArgumentException("File '" + file1 + "' does not exist");
        }
        if (!file2.exists() || !file2.isFile()) {
            throw new IllegalArgumentException("File '" + file2 + "' does not exist");
        }

        if (file1.length() != file2.length()) {
            return (int)(file1.length() - file2.length());
        }

        InputStream is1 = null;
        InputStream is2 = null;
        try {
            is1 = new BufferedInputStream(new FileInputStream(file1));
            is2 = new BufferedInputStream(new FileInputStream(file2));
            
            int b1 = -2;
            int b2 = -2;
            try {
                while ((b1 = is1.read()) != -1
                       && (b2 = is2.read()) != -1) {
                    if (b1 != b2)
                        return b1 - b2;
                    b1 = b2 = -2;
                }
            }
            catch(IOException io) {
                return b1 == -2 ? -1 : 1;
            }
            finally {
                is1.close(); is1 = null;
                is2.close(); is2 = null;
            }
            return 0;
        }
        catch(FileNotFoundException fnf) {
            return is1 == null ? -1 : 1;
        }
        catch(IOException io) {
            return is1 == null ? -1 : 1;
        }
    }

    /** Comparators are equal if they're the same class. */
    public boolean equals(Object obj) {
        return obj == this
            || (obj != null && obj.getClass().equals(getClass()));
    }
}
