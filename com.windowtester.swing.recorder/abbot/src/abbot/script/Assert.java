package abbot.script;

import java.lang.reflect.Array;
import java.util.Map;
import javax.swing.tree.TreePath;

import abbot.*;
import abbot.i18n.Strings;
import abbot.tester.ComponentTester;
import abbot.util.ExtendedComparator;

/** Encapsulate an assertion (or a wait).  Usage:<br>
 * <blockquote><code>
 * &lt;assert method="[!]assertXXX" args="..." [class="..."]&gt;<br>
 * &lt;assert method="[!](get|is|has)XXX" component="component_id" value="..."&gt;<br>
 * &lt;assert method="[!]XXX" args="..." class="..."&gt;<br>
 * <br>
 * &lt;wait ... [timeout="..."] [pollInterval="..."]&gt;<br>
 * </code></blockquote>
 * The first example above invokes a core assertion provided by the
 * {@link abbot.tester.ComponentTester} class; the class tag is required for
 * assertions based on a class derived from
 * {@link abbot.tester.ComponentTester}; the class tag indicates the
 * {@link java.awt.Component} class, not the Tester class (the appropriate
 * tester class will be derived automatically).<p>
 * The second format indicates a property check on the given component, and an
 * expected value must be provided; the method name must start with "is",
 * "get", or "has".  Finally, any arbitrary static boolean method may be used
 * in the assertion; you must specify the class and arguments.<p>  
 * You can invert the sense of either form of assertion by inserting a '!'
 * character before the method name, or adding an <code>invert="true"</code>
 * attribute. 
 * <p>
 * The default timeout for a wait is ten seconds; the default poll interval
 * (sleep time between checking the assertion) is 1/10 second.  Both may be
 * set as XML attributes (<code>pollInterval</code> and <code>timeout</code>).
 */

public class Assert extends PropertyCall {
    /** Default interval between checking the assertion in a wait. */
    public static final int DEFAULT_INTERVAL = 100;
    /** Default timeout before a wait will indicate failure. */
    public static final int DEFAULT_TIMEOUT = 10000;

    private static final String ASSERT_USAGE = 
        "<assert component=... method=... value=... [invert=true]/>\n"
        + "<assert method=[!]... [class=...]/>";

    private static final String WAIT_USAGE = 
        "<wait component=... method=... value=... [invert=true] "
        + "[timeout=...] [pollInterval=...]/>\n"
        + "<wait method=[!]... [class=...] [timeout=...] [pollInterval=...]/>";

    private String expectedResult = "true";
    private boolean invert;
    private boolean wait;

    private long interval = DEFAULT_INTERVAL;
    private long timeout = DEFAULT_TIMEOUT;

    /** Construct an assert step from XML. */
    public Assert(Resolver resolver, Map attributes) {
        super(resolver, patchAttributes(attributes));
        wait = attributes.get(TAG_WAIT) != null;
        String to = (String)attributes.get(TAG_TIMEOUT);
        if (to != null) {
            try { timeout = Integer.parseInt(to); }
            catch(NumberFormatException exc) { }
        }
        String pi = (String)attributes.get(TAG_POLL_INTERVAL);
        if (pi != null) {
            try { interval = Integer.parseInt(pi); }
            catch(NumberFormatException exc) { }
        }
        init(Boolean.valueOf((String)attributes.get(TAG_INVERT)).
             booleanValue(), (String)attributes.get(TAG_VALUE));
    }

    /** Assertion provided by the ComponentTester class, or an arbitrary
        static call.
    */
    public Assert(Resolver resolver, String desc,
                  String targetClassName, String methodName, String[] args, 
                  String expectedResult, boolean invert) {
        super(resolver, desc,
              targetClassName != null
              ? targetClassName
              : ComponentTester.class.getName(),
              methodName, args);
        init(invert, expectedResult);
    }

    /** Assertion provided by a ComponentTester subclass which operates on a
     * Component subclass.
     */
    public Assert(Resolver resolver, String desc,
                  String methodName, String[] args, 
                  Class testedClass, String expectedResult, 
                  boolean invert) {
        super(resolver, desc, testedClass.getName(), methodName, args);
        init(invert, expectedResult);
    }

    /** Property assertion on Component subclass. */
    public Assert(Resolver resolver, String desc,
                  String methodName, String componentID,
                  String expectedResult, boolean invert) {
        super(resolver, desc, methodName, componentID);
        init(invert, expectedResult);
    }

    /** The canonical form for a boolean assertion is to return true, and set
     * the invert flag if necessary.
     */
    private void init(boolean inverted, String value) {
        if ("false".equals(value)) {
            inverted = !inverted;
            value = "true";
        }
        expectedResult = value != null ? value : "true";
        invert = inverted;
    }
    /** Changes the behavior of this step between failing if the condition is
        not met and waiting for the condition to be met.
        @param wait If true, this step returns from its {@link #runStep()}
        method only when its condition is met, throwing a
        {@link WaitTimedOutError} if the condition is not met within the
        timeout interval. 
        @see #setPollInterval(long)
        @see #getPollInterval()
        @see #setTimeout(long)
        @see #getTimeout()
    */
    public void setWait(boolean wait) { this.wait = wait; }
    public boolean isWait() { return wait; }
    public void setPollInterval(long interval) { this.interval = interval; }
    public long getPollInterval() { return interval; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
    public long getTimeout() { return timeout; }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String result) {
        init(invert, result);
    }

    public boolean isInverted() { return invert; }

    public void setInverted(boolean invert) {
        init(invert, expectedResult);
    }

    /** Strip inversion from the method name. */
    private static Map patchAttributes(Map map) {
        String method = (String)map.get(TAG_METHOD);
        if (method != null && method.startsWith("!")) {
            map.put(TAG_METHOD, method.substring(1));
            map.put(TAG_INVERT, "true");
        }
        // If no class is specified, default to ComponentTester
        String cls = (String)map.get(TAG_CLASS);
        if (cls == null) {
            map.put(TAG_CLASS, "abbot.tester.ComponentTester");
        }
        return map;
    }

    public String getXMLTag() { 
        return wait ? TAG_WAIT : TAG_ASSERT;
    }

    public String getUsage() {
        return wait ? WAIT_USAGE : ASSERT_USAGE;
    }

    public String getDefaultDescription() {
        String mname = getMethodName();
        // assert/is/get doesn't really add any information, so drop it
        if (mname.startsWith("assert"))
            mname = mname.substring(6);
        else if (mname.startsWith("get") || mname.startsWith("has"))
            mname = mname.substring(3);
        else if (mname.startsWith("is"))
            mname = mname.substring(2);
        // FIXME this is cruft; really only need i18n for wait for X/assert X
        // [!][$.]m [==|!= v]
        String expression = mname + getArgumentsDescription();
        if (getComponentID() != null) 
            expression = "${" + getComponentID() + "}." + expression;
        if (invert && "true".equals(expectedResult))
            expression = "!" + expression;
        if (!"true".equals(expectedResult)) {
            expression += invert ? " != " : " == ";
            expression += expectedResult;
        }
        if (wait && timeout != DEFAULT_TIMEOUT) {
            expression += " "
                + Strings.get((timeout > 5000
                               ? "wait.seconds"
                               : "wait.milliseconds"),
                              new Object[] { 
                                  String.valueOf(timeout > 5000
                                                 ? timeout / 1000
                                                 : timeout)
                              });
        }
        return Strings.get((wait ? "wait.desc" : "assert.desc"),
                           new Object[] { expression });
    }

    public Map getAttributes() {
        Map map = super.getAttributes();
        if (invert) {
            map.put(TAG_INVERT, "true");
        }
        if (!expectedResult.equalsIgnoreCase("true")) {
            map.put(TAG_VALUE, expectedResult);
        }
        if (timeout != DEFAULT_TIMEOUT)
            map.put(TAG_TIMEOUT, String.valueOf(timeout));
        if (interval != DEFAULT_INTERVAL)
            map.put(TAG_POLL_INTERVAL, String.valueOf(interval));
        return map;
    }

    /** Check the assertion.  */
    protected void evaluateAssertion() throws Throwable {
        Object expected, actual;
        Class type = getMethod().getReturnType();
        boolean compareStrings = false;

        try {
            expected =
                ArgumentParser.eval(getResolver(), expectedResult, type);
        }
        catch(IllegalArgumentException iae) {
            // If we can't convert the string to the expected type,
            // match the string against the result's toString method instead
            expected = expectedResult;
            compareStrings = true;
        }

        actual = invoke();

        // Special-case TreePaths; we want to string-compare the underlying
        // objects rather than the TreePaths themselves.
        // If this comes up again we'll look for a generalized solution.
        if (expected instanceof TreePath && actual instanceof TreePath) {
            expected = ArgumentParser.toString(((TreePath)expected).getPath());
            actual = ArgumentParser.toString(((TreePath)actual).getPath());
        }

        if (compareStrings) {
            actual = ArgumentParser.toString(actual);
        }

        if (invert) {
            assertNotEquals(toString(), expected, actual);
        }
        else {
            assertEquals(toString(), expected, actual);
        }
    }

    /** Use our own comparison, to get the extended array comparisons. */
    private void assertEquals(String message, Object expected, 
                              Object actual) {
        if (!ExtendedComparator.equals(expected, actual)) {
            String msg = Strings.get("assert.comparison_failed", new Object[] {
                (message != null ? message + " " : ""),
                ArgumentParser.toString(actual)
            });
            throw new AssertionFailedError(msg, this);
        }
    }
    
    /** Use our own comparison, to get the extended array comparisons. */
    private void assertNotEquals(String message, Object expected, 
                                 Object actual) {
        if (ExtendedComparator.equals(expected, actual)) {
            String msg = message != null ? message : "";
            throw new AssertionFailedError(msg, this);
        }
    }

    /** Run this step. */
    protected void runStep() throws Throwable {
        if (!wait) {
            evaluateAssertion();
        }
        else {
            long now = System.currentTimeMillis();
            long remaining = timeout;
            while (remaining > 0) {
                long start = now;
                try {
                    try {
                        evaluateAssertion();
                        return;
                    }
                    catch(AssertionFailedError exc) {
                        // keep waiting
                        Log.debug(exc);
                    }
                    Thread.sleep(interval);
                }
                catch(InterruptedException ie) {
                }
                now = System.currentTimeMillis();
                remaining -= now - start;
            }
            throw new WaitTimedOutError(Strings.get("wait.timed_out",
                                                    new Object[] {
                String.valueOf(timeout), toString() }));
        }
    }

}
