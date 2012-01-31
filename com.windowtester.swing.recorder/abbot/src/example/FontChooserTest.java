package example;

import java.io.File;

import junit.extensions.abbot.*;
import junit.framework.Test;
import abbot.Log;

/** Collects scripts which test the FontChooser GUI component.  */

public class FontChooserTest extends ScriptFixture {

    public FontChooserTest(String filename) {
        super(filename);
    }
    
    /** Provide a default test suite for this test case. */
    public static Test suite() { 
        return new ScriptTestSuite(FontChooserTest.class,
                                   "src/example/scripts/FontChooser") {
            public boolean accept(File file) {
                return super.accept(file)
                    && !file.getName().equals("fixture.xml");
            }
        };
    }

    public static void main(String[] args) {
        TestHelper.runTests(args, FontChooserTest.class);
    }
}
