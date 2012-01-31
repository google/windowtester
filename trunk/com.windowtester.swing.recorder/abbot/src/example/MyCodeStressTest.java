package example;

import junit.extensions.abbot.*;
import junit.framework.*;
import abbot.script.*;
import abbot.util.AWTFixtureHelper;
import abbot.finder.TestHierarchy;

/** Simple example of a stress test on an app. */
public class MyCodeStressTest extends TestCase {

    public static Test suite() {
        final int ITERATIONS = 10;
        TestSuite suite = new TestSuite();
        for (int i=0;i < ITERATIONS;i++) {
            suite.addTest(new ScriptFixture("src/example/StressMyCode.xml"));
        }
        return suite;
    }

    public static void main(String[] args) {
        TestHelper.runTests(args, MyCodeStressTest.class);
    }
}
