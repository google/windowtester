package junit.extensions.abbot;

import junit.framework.TestCase;

import abbot.Log;
import abbot.script.*;
import abbot.util.AWTFixtureHelper;
import abbot.finder.*;

/** Simple wrapper for a test script to run under JUnit.  If the script
 * does not contain a launch step, the hierarchy used will include existing
 * components.  No automatic cleanup of components is performed, since it is
 * assumed that a Terminate step within the script will trigger that operation
 * if it is required.<p>
 */
public class ScriptFixture extends TestCase {

    private static AWTFixtureHelper oldContext = null;
    private static final Hierarchy DUMMY_HIERARCHY = new AWTHierarchy();
    private StepRunner runner;
    
    /** Construct a test case with the given name, which <i>must</i> be the
     * filename of the script to run.
     */
    public ScriptFixture(String filename) { 
        // It is essential that the name be passed to super() unmodified, or
        // the JUnit GUI will consider it a different test.
        super(filename);
    }

    /** Saves the current UI state for restoration when the
        fixture (if any) is terminated.  Also sets up a 
        {@link TestHierarchy} for the duration of the test.
    */
    protected void setUp() throws Exception {
        if (oldContext == null) {
            oldContext = new AWTFixtureHelper();
        }
        runner = new StepRunner(oldContext);
        // Support for deprecated ComponentTester.assertFrameShowing usage
        // only.  Eventually this will go away.
        AWTHierarchy.setDefault(runner.getHierarchy());
    }

    protected void tearDown() throws Exception {
        AWTHierarchy.setDefault(null);
        runner = null;
    }

    /** Override the default TestCase runTest method to invoke the script.
        The {@link Script} is created and a default {@link StepRunner} is used
        to run it.
        @see junit.framework.TestCase#runTest
     */
    protected void runTest() throws Throwable {
        Script script = new Script(getName(), DUMMY_HIERARCHY);
        Log.log("Running " + script + " with " + getClass());

        try {
            runner.run(script);
        }
        finally {
            Log.log(script.toString() + " finished");
        }
    }

    /** Assumes each argument is an Abbot script.  Runs each one. */
    public static void main(String[] args) {
        ScriptTestSuite.main(args);
    }
}
