package abbot.script.swt;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.eclipse.swt.widgets.Widget;

import abbot.AssertionFailedError;
import abbot.Log;
import abbot.WaitTimedOutError;
import abbot.i18n.Strings;
import abbot.script.InvalidScriptException;
import abbot.swt.Resolver;
import abbot.tester.swt.WidgetTester;
import abbot.util.ExtendedComparator;

/** Encapsulate an assertion (or a wait).  Usage:<br>
 * <blockquote><code>
 * &lt;assert method="[!]assertXXX" args="..." [class="..."]&gt;<br>
 * &lt;assert method="[!](get|is|has)XXX" widget="widget_id" value="..."&gt;<br>
 * &lt;assert method="[!]XXX" args="..." class="..."&gt;<br>
 * <br>
 * &lt;wait ... [timeout="..."] [pollInterval="..."]&gt;<br>
 * </code></blockquote>
 * In the first example above, the class tag is required for assertions based on 
 * a class derived from ComponentTester; the class tag indicates the Component 
 * class, not the Tester class (the appropriate tester class will be derived 
 * automatically).
 * The second format indicates a property check on the given widget, and an
 * expected value must be provided; the method name must start with "is",
 * "get", or "has".  Finally, any arbitrary static boolean method may be used
 * in the assertion; you must specify the class and arguments.<p>  
 * You can invert the sense of either form of assertion by inserting a '!'
 * character before the method name, or adding an <code>invert="true"</code>
 * attribute. 
 * <p>
 * The default timeout for a wait is ten seconds; the default poll interval
 * (sleep time between checking the assertion) is 1/10 second.
 */
// FIXME we'd also like to be able to do things like getWidth() < 100

public class Assert extends PropertyCall {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    /** Default interval between checking the assertion in a wait. */
    public static final int DEFAULT_INTERVAL = 100;
    /** Default timeout before a wait will indicate failure. */
    public static final int DEFAULT_TIMEOUT = 10000;

    private static final String ASSERT_USAGE = 
        "<assert widget=... method=... value=... [invert=true]/>\n"
        + "<assert method=[!]... [class=...]/>";

    private static final String WAIT_USAGE = 
        "<wait widget=... method=... value=... [invert=true] "
        + "[timeout=...] [pollInterval=...]/>\n"
        + "<wait method=[!]... [class=...] [timeout=...] [pollInterval=...]/>";

    private String expectedResult = "true";
    private boolean invert = false;
    private boolean wait = false;

    private long interval = DEFAULT_INTERVAL;
    private long timeout = DEFAULT_TIMEOUT;

    /** Construct an assert step from XML. */
    public Assert(Resolver resolver, HashMap attributes) {
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

    /** Assertion provided by the WidgetTester class. */
    public Assert(Resolver resolver, String desc,
                  String methodName, String[] args, 
                  String expectedResult, boolean invert) {
        super(resolver, desc, WidgetTester.class.getName(),
              methodName, args);
        init(invert, expectedResult);
    }

    /** Assertion provided by a WidgetTester subclass which operates on a
     * Widget subclass.
     */
    public Assert(Resolver resolver, String desc,
                  String methodName, String[] args, 
                  Class testedClass, String expectedResult, 
                  boolean invert) {
        super(resolver, desc, testedClass.getName(), methodName, args);
        init(invert, expectedResult);
    }

    /** Property assertion on Widget subclass. */
    public Assert(Resolver resolver, String desc,
                  String methodName, String[] args, 
                  String widgetID, String expectedResult, 
                  boolean invert) {
        super(resolver, desc, methodName, args, widgetID);
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

        // Accommodate deprecated usage; Assert steps previously saved only
        // the Widget subclass of the target or of the first argument to a
        // WidgetTester static method.  That was bad.
        try {
            Class cls = getTargetClass();
            Log.debug("Target class is " + cls.getName());
            if (Widget.class.isAssignableFrom(cls)) {
                try {
                    resolveMethod(getMethodName(), cls, null);
                }
                catch(IllegalArgumentException iae) {
                    Log.debug("Attempting to repair usage, method "
                              + getMethodName());
                    // Method doesn't exist on this class; if it exists on a
                    // WidgetTester, fix things up.  Otherwise, mark it as
                    // an error. 
                    WidgetTester tester = WidgetTester.getTester(cls);
                    try {
                        resolveMethod(getMethodName(),
                                      tester.getClass(), null);
                        setTargetClassName(tester.getClass().getName());
                        // old usage set the widget id; it is included in
                        // the arg list, and widget ID now means the method
                        // target *only* 
                        setWidgetID(null);
                    }
                    catch(IllegalArgumentException e) {
                        // Leave things alone and let it fail when it is run
                        Log.debug(e);
                    }
                }
            }
        }
        catch(InvalidScriptException ise) {
            setScriptError(ise);
        }
        // End deprecated usage handling
    }
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
    private static HashMap patchAttributes(HashMap map) {
        String method = (String)map.get(TAG_METHOD);
        if (method != null && method.startsWith("!")) {
            map.put(TAG_METHOD, method.substring(1));
            map.put(TAG_INVERT, "true");
        }
        // If no class is specified, defualt to WidgetTester
        String cls = (String)map.get(TAG_CLASS);
        if (cls == null) {
            map.put(TAG_CLASS, "abbot.swt.tester.WidgetTester");
        }
        return map;
    }

    public String getXMLTag() { 
        return wait ? TAG_WAIT : TAG_ASSERT;
    }

    public String getUsage() {
        return wait ? WAIT_USAGE : ASSERT_USAGE;
    }

    protected String getDefaultDescription() {
        String mname = getMethodName();
        // assert/is/get doesn't really add any information, so drop it
        if (mname.startsWith("assert"))
            mname = mname.substring(6);
        else if (mname.startsWith("get"))
            mname = mname.substring(3);
        else if (mname.startsWith("is"))
            mname = mname.substring(2);
        // [!][$.]m [==|!= v]
        String desc = Strings.get((wait ? "WaitDesc" : "AssertDesc"),
                                  new Object[] {
            ((invert && "true".equals(expectedResult)) ? "!" : ""),
            (getWidgetID() != null ? ("${" + getWidgetID() + "}.") : ""),
            mname + "(" + getEncodedArguments() + ") ",
            (expectedResult.equals("true")
             ? "" : ((invert
                      ? Strings.get("NotEquals")
                      : Strings.get("Equals"))) + " "),
            (expectedResult.equals("true")
             ? "" : expectedResult),
            (wait && timeout != DEFAULT_TIMEOUT
             ? Strings.get((timeout > 5000
                            ? "Seconds"
                            : "Milliseconds"),
                           new Object[] { String.valueOf(timeout > 5000
                                                         ? timeout/1000
                                                         : timeout) }) : "")
        });
        return desc;
    }

    public HashMap getAttributes() {
        HashMap map = super.getAttributes();
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

    /** Check the assertion.  This is exported so that it can be used by
     * derivatives of Assert (e.g. Wait). 
     */
    protected void doCheck() throws Throwable {
        Object expected, actual;
        boolean matchStrings = false;
        // If we can't convert the string to the expected type,
        // match the string against the result's toString method instead
        try {
            expected = ArgumentParser.eval(getResolver(), expectedResult, 
                                           getMethod().getReturnType());
        }
        catch(IllegalArgumentException iae) {
            expected = expectedResult;
            matchStrings = true;
        }

        actual = invoke();

        if (matchStrings)
            actual = actual.toString();

        if (invert) {
            assertNotEquals(this.toString(), expected, actual);
        }
        else {
            assertEquals(this.toString(), expected, actual);
        }
    }

    /** Print out arrays by individual element. */
    protected String toString(Object obj) {
        if (obj.getClass().isArray()) {
            String str = "[";
            String comma = "";
            for (int i=0;i < Array.getLength(obj);i++) {
                str += comma + toString(Array.get(obj, i));
                comma = ",";
            }
            return str + "]";
        }
        return obj.toString();
    }

    /** Use our own comparison, to get the extended array comparisons. */
    private void assertEquals(String message, Object expected, 
                              Object actual) {
        if (!ExtendedComparator.equals(expected, actual)) {
            String msg = Strings.get("ComparisonFailed", new Object[] {
                (message != null ? message + " " : ""),
                toString(actual)
            });
            throw new AssertionFailedError(msg);
        }
    }
    
    /** Use our own comparison, to get the extended array comparisons. */
    private void assertNotEquals(String message, Object expected, 
                                 Object actual) {
        if (ExtendedComparator.equals(expected, actual)) {
            String msg = message != null ? message : "";
            throw new AssertionFailedError(msg);
        }
    }

    /** Run this step. */
    protected void runStep() throws Throwable {
        if (wait) {
            long now = System.currentTimeMillis();
            long remaining = timeout;
            String error = toString();
            while (remaining > 0) {
                long start = now;
                try {
                    try {
                        doCheck();
                        return;
                    }
                    catch(AssertionFailedError exc) {
                        // keep waiting
                        error = exc.getMessage();
                    }
                    Thread.sleep(interval);
                }
                catch(InterruptedException ie) {
                }
                now = System.currentTimeMillis();
                remaining -= now - start;
            }
            throw new WaitTimedOutError(Strings.get("WaitTimedOut",
                                                    new Object[] {
                String.valueOf(timeout), toString() }));
        }
        else {
            doCheck();
        }
    }

}
