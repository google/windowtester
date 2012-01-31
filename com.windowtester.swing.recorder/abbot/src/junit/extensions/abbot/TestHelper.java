package junit.extensions.abbot;

import java.util.ArrayList;
import java.util.Locale;

import junit.framework.*;
import junit.textui.*;

import abbot.Log;

/** Provides automatic test suite generation given command-line arguments.
 * Also allows for a single test to be run if it is specified.  Supports "-v"
 * (verbose) to display each test's name prior to running the test (aids in
 * identifying which test failed when the VM crashes).
 * The locale may be set explicitly by the following system properties:
 * <ul>
 * <li>abbot.locale.language
 * <li>abbot.locale.country
 * <li>abbot.locale.variant
 * </ul>
 */

public class TestHelper {
    protected TestHelper() { }

    public static TestSuite generateSuite(Class[] classes) {
        TestSuite suite = new TestSuite();
        for (int i=0;i < classes.length;i++) {
            try {
                java.lang.reflect.Method suiteMethod =
                    classes[i].getMethod("suite", null);
                suite.addTest((Test)suiteMethod.invoke(null, null));
            }
            catch(Exception exc) {
                suite.addTest(new TestSuite(classes[i]));
            }
        }
        return suite;
    }

    private static boolean printTestNames = false;
    protected static String[] parseArgs(String[] args) {
        String language = System.getProperty("abbot.locale.language");
        if (language != null) {
            String country = System.getProperty("abbot.locale.country", "");
            String variant = System.getProperty("abbot.locale.language", "");
            Locale locale = new Locale(language, country, variant);
            Locale.setDefault(locale);
            System.out.println("Using locale " + locale.getDisplayName());
        }
        ArrayList newArgs = new ArrayList();
        for (int i=0;i < args.length;i++) {
            if (args[i].equals("-v")) 
                printTestNames = true;
            else {
                newArgs.add(args[i]);
            }
        }
        return (String[])newArgs.toArray(new String[newArgs.size()]);
    }

    protected static Test collectTests(String[] args, Class testClass)
        throws NoSuchMethodException,
               InstantiationException,
               IllegalAccessException,
               java.lang.reflect.InvocationTargetException {
        Test test;
        if (args.length == 1 && args[0].startsWith("test")) {
            try {
                test = (Test)testClass.newInstance();
                ((TestCase)test).setName(args[0]);
            }
            catch(InstantiationException e) {
                test = (Test)testClass.getConstructor(new Class[]{
                    String.class
                }).newInstance(new Object[] { args[0] });
            }
        }
        else {
            try {
                test = (Test)testClass.getMethod("suite", null).
                    invoke(null, null);
            }
            catch(Exception exc) {
                test = new TestSuite(testClass);
            }
        }
        return test;
    }

    protected static void runTest(Test test) {
        try {
            TestRunner runner = new TestRunner(new ResultPrinter(System.out) {
                public void startTest(Test test) {
                    if (printTestNames)
                        getWriter().print(test.toString());
                    super.startTest(test);
                }
                public void endTest(Test test) {
                    super.endTest(test);
                    if (printTestNames)
                        getWriter().println();
                }
            });
            try {
                TestResult r = runner.doRun(test, false);
                if (!r.wasSuccessful())
                    System.exit(-1);
                System.exit(0);
            }
            catch(Throwable thr) {
                System.err.println(thr.getMessage());
                System.exit(-2);
            }
        }
        catch(Exception exc) {
            System.err.println(exc.getMessage());
            System.exit(-2);
        }
    }

    public static void runTests(String[] args, Class testClass) {
        args = Log.init(args);
        args = parseArgs(args);
        try {
            runTest(collectTests(args, testClass));
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            System.exit(-2);
        }
    }
}
