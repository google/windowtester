package junit.extensions.abbot;

import java.awt.Component;
import java.awt.Window;
import java.util.Iterator;

import junit.framework.TestCase;
import abbot.Log;
import abbot.finder.AWTHierarchy;
import abbot.finder.BasicFinder;
import abbot.finder.ComponentFinder;
import abbot.finder.Hierarchy;
import abbot.finder.Matcher;
import abbot.finder.TestHierarchy;
import abbot.script.Resolver;
import abbot.script.Script;

/** Simple wrapper for testing objects which require a Resolver. */

public class ResolverFixture extends TestCase {

    /** Simple matcher that may be used to verify that a specific component is
        found by a given ComponentFinder.
    */    
    protected class ComponentMatcher implements Matcher {
        private Component component;
        public ComponentMatcher(Component c) {
            component = c;
        }
        public boolean matches(Component c) {
            return c == component;
        }
    }

    private Hierarchy hierarchy;
    private ComponentFinder finder;
    private Resolver resolver;
    
    /** Obtain a consistent hierarchy. */
    protected Hierarchy getHierarchy() { return hierarchy; }
    
    /** Provide for derived classes to provide their own Hierarchy. */
    protected Hierarchy createHierarchy() {
        return new TestHierarchy();
    }

    /** Obtain a component finder to look up components. */
    protected ComponentFinder getFinder() { return finder; }

    /** Obtain a consistent resolver. */
    protected Resolver getResolver() { return resolver; }

    /** Fixture setup is performed here, to avoid problems should a derived
        class define its own setUp and neglect to invoke the superclass
        method. 
    */
    protected void fixtureSetUp() throws Throwable {
        hierarchy = createHierarchy();

        finder = new BasicFinder(hierarchy);
        // FIXME kind of a hack, but Script is the only implementation of
        // Resolver we've got at the moment.
        resolver = new Script(hierarchy);
    }

    /** Fixture teardown is performed here, to avoid problems should a derived
        class define its own tearDown and neglect to invoke the superclass
        method.  
    */
    protected void fixtureTearDown() throws Throwable {
        Iterator iter = hierarchy.getRoots().iterator();
        while (iter.hasNext()) {
            hierarchy.dispose((Window)iter.next());
        }
        // Explicitly set these null, since the test fixture instance may
        // be kept around by the test runner
        hierarchy = null;
        resolver = null;
        finder = null;
    }

    /** Override the default <code>junit.framework.TestCase#RunBare()</code>
        to ensure proper test harness setup and teardown that won't
        likely be accidentally overridden by a derived class. 
    */
    public void runBare() throws Throwable {
        Log.log("setting up fixture: " + getName());
        Throwable exception = null;
        fixtureSetUp();
        try {
            super.runBare();
        }
        catch(Throwable e) {
            exception = e;
        }
        finally {
            Log.log("tearing down fixture: " + getName());
            try {
                fixtureTearDown();
            }
            catch(Throwable tearingDown) {
                if (exception == null)
                    exception = tearingDown;
            }
        }
        if (exception != null)
            throw exception;
    }

    /** Construct a test case with the given name.  */
    public ResolverFixture(String name) {
        super(name);
    }

    /** Default Constructor.  The name will be automatically set from the
        selected test method.
    */ 
    public ResolverFixture() { }
}
