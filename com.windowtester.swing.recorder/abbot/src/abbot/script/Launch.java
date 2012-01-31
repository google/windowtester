package abbot.script;

import java.awt.Window;
import java.lang.reflect.*;
import java.util.Map;
import java.util.Iterator;

import abbot.finder.Hierarchy;
import abbot.*;
import abbot.i18n.Strings;

/** 
 * Provides scripted static method invocation.  Usage:<br>
 * <blockquote><code>
 *  &lt;launch class="package.class" method="methodName" args="..."
 * classpath="..." [threaded=true]&gt;<br>
 * </code></blockquote><p> 
 * The args attribute is a comma-separated list of arguments to pass to the
 * class method, and may use square brackets to denote an array,
 * e.g. "[one,two,three]" will be interpreted as an array length 3 
 * of String.  The square brackets may be escaped ('\[' or '\]') to include
 * them literally in an argument.
 * <p>
 * The class path attribute may use either colon or semicolon as a path
 * separator, but should preferably use relative paths to avoid making the
 * containing script platform- and location-dependent.<p>
 * In most cases, the classes under test will <i>only</i> be found under the
 * custom class path, and so the parent class loader will fail to find them.
 * If this is the case then the classes under test will be properly discarded
 * on each launch when a new class loader is created.
 * <p>
 * The 'threaded' attribute is provided in case your code under test requires
 * GUI event processing prior to returning from its invoked method.  An
 * example might be a main method which invokes dialog and waits for the
 * response before continuing.  In general, it's better to refactor the code
 * if possible so that the main method turns over control to the event
 * dispatch thread as soon as possible.  Otherwise, if the application under
 * test is background threaded by the Launch step, any runtime exceptions
 * thrown from the launch code will cause errors in the launch step out of
 * sequence with the other script steps.  While this won't cause any problems
 * for the Abbot framework, it can be very confusing for the user.<p>
 * Note that if the "reload" attribute is set true (i.e. Abbot's class loader
 * is used to reload code under test), ComponentTester extensions must also be
 * loaded by that class loader, so the path to extensions should be included
 * in the Launch class path.<p> 
 */
public class Launch extends Call implements UIContext {
    /** Allow only one active launch at a time. */
    private static Launch currentLaunch = null;
    
    private String classpath = null;
    private boolean threaded = false;
    private transient AppClassLoader classLoader;
    private transient ThreadedLaunchListener listener;
    
    private static final String USAGE = 
        "<launch class=\"...\" method=\"...\" args=\"...\" "
        + "[threaded=true]>";

    public Launch(Resolver resolver, Map attributes) {
        super(resolver, attributes);
        classpath = (String)attributes.get(TAG_CLASSPATH);
        String thr = (String)attributes.get(TAG_THREADED);
        if (thr != null)
            threaded = Boolean.valueOf(thr).booleanValue();
    }

    public Launch(Resolver resolver, String description,
                  String className, String methodName, String[] args) {
        this(resolver, description, className, methodName, args, null, false);
    }

    public Launch(Resolver resolver, String description,
                  String className, String methodName, String[] args,
                  String classpath, boolean threaded) {
        super(resolver, description, className, methodName, args);
        this.classpath = classpath;
        this.threaded = threaded;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String cp) {
        classpath = cp;
        // invalidate class loader
        classLoader = null;
    }

    public boolean isThreaded() {
        return threaded;
    }

    public void setThreaded(boolean thread) {
        threaded = thread;
    }

    protected AppClassLoader createClassLoader() {
        return new AppClassLoader(classpath);
    }

    /** Install the class loader context for the code being launched.  The
     * context class loader for the current thread is modified.
     */
    protected void install() {
        ClassLoader loader = getContextClassLoader();
        // Everything else loaded on the same thread as this 
        // launch should be loaded by this custom loader.  
        if (loader instanceof AppClassLoader
            && !((AppClassLoader)loader).isInstalled()) {
            ((AppClassLoader)loader).install();
        }
    }
    
    protected void synchronizedRunStep() throws Throwable {
        // A bug in pre-1.4 VMs locks the toolkit prior to notifying AWT event
        // listeners.  This causes a deadlock when the main method invokes
        // "show" on a component which triggers AWT events for which there are
        // listeners.  To avoid this, grab the toolkit lock first so that the
        // locks are acquired in the same order by either sequence.
        // (Unfortunately, some swing code locks the tree prior to
        // grabbing the toolkit lock, so there's still opportunity for
        // deadlock).  One alternative (although very heavyweight) is to
        // always fork a separate VM.
        //
        // If threaded, take the danger of deadlock over the possibility that
        // the main method will never return and leave the lock forever held.
        // NOTE: this is guaranteed to deadlock if "main" calls
        // EventQueue.invokeAndWait. 
        if (Platform.JAVA_VERSION < Platform.JAVA_1_4
            && !isThreaded()) {
            synchronized(java.awt.Toolkit.getDefaultToolkit()) {
                super.runStep();
            }
        }
        else {
            super.runStep();
        }
    }

    /** Perform steps necessary to remove any setup performed by 
     * this <code>Launch</code> step.
     */ 
    public void terminate() {
        Log.debug("launch terminate");
        if (currentLaunch == this) {
            // Nothing special to do, dispose windows normally
            Iterator iter = getHierarchy().getRoots().iterator();
            while (iter.hasNext())
                getHierarchy().dispose((Window)iter.next());
            if (classLoader != null) {
                classLoader.uninstall();
                classLoader = null;
            }
            currentLaunch = null;
        }
    }

    /** Launches the UI described by this <code>Launch</code> step,
     * using the given runner as controller/monitor. 
     */
    public void launch(StepRunner runner) throws Throwable {
        runner.run(this);
    }
    
    /** @return Whether the code described by this launch step is currently active. */
    public boolean isLaunched() {
        return currentLaunch == this;
    }

    public Hierarchy getHierarchy() {
        return getResolver().getHierarchy();
    }
    
    public void runStep() throws Throwable {
        if (currentLaunch != null)
            currentLaunch.terminate();
        currentLaunch = this;
        install();
        System.setProperty("abbot.framework.launched", "true");
        if (isThreaded()) {
            Thread threaded = new Thread("Threaded " + toString()) {
                public void run() {
                    try {
                        synchronizedRunStep();
                    }
                    catch(AssertionFailedError e) {
                        if (listener != null)
                            listener.stepFailure(Launch.this, e);
                    }
                    catch(Throwable t) {
                        if (listener != null)
                            listener.stepError(Launch.this, t);
                    }
                }
            };
            threaded.setDaemon(true);
            threaded.setContextClassLoader(classLoader);
            threaded.start();
        }
        else {
            synchronizedRunStep();
        }
    }

    /** Overrides the default implementation to always use the class loader
     * defined by this step.  This works in cases where the Launch step has
     * not yet been added to a Script; otherwise the Script will provide an
     * implementation equivalent to this one.
     */
    public Class resolveClass(String className) throws ClassNotFoundException {
        return Class.forName(className, true, getContextClassLoader());
    }

    /** Return the class loader that uses the classpath defined in this
     * step.
     */ 
    public ClassLoader getContextClassLoader() {
        if (classLoader == null) {
            // Use a custom class loader so that we can provide additional
            // classpath and also optionally reload the class on each run.
            // FIXME maybe classpath should be relative to the script?  In this
            // case, it's relative to user.dir
            classLoader = createClassLoader();
        }
        return classLoader;
    }

    public Class getTargetClass() throws ClassNotFoundException {
        Class cls = resolveClass(getTargetClassName());
        Log.debug("Target class is " + cls.getName());
        return cls;
    }

    /** Return the target for the method invocation.  All launch invocations
     * must be static, so this always returns null.
     */ 
    protected Object getTarget(Method m) {
        return null;
    }

    /** Return the method to be used for invocation. */
    public Method getMethod()
        throws ClassNotFoundException, NoSuchMethodException {
        return resolveMethod(getMethodName(), getTargetClass(), null);
    }

    public Map getAttributes() {
        Map map = super.getAttributes();
        if (classpath != null) {
            map.put(TAG_CLASSPATH, classpath);
        }
        if (threaded) {
            map.put(TAG_THREADED, "true");
        }
        return map;
    }

    public String getDefaultDescription() {
        String desc = Strings.get("launch.desc",
                                  new Object[] { getTargetClassName()
                                                 + "." + getMethodName()
                                                 + "(" + getEncodedArguments()
                                                 + ")"});
        return desc;
    }

    public String getUsage() { return USAGE; }

    public String getXMLTag() { return TAG_LAUNCH; }

    /** Set a listener to respond to events when the launch step is
     * threaded.
     */
    public void setThreadedLaunchListener(ThreadedLaunchListener l) {
        listener = l;
    }

    public interface ThreadedLaunchListener {
        public void stepFailure(Launch launch, AssertionFailedError error);
        public void stepError(Launch launch, Throwable throwable);
    }

    /** No two launches are ever considered equivalent.  If you want
     * a shared {@link UIContext}, use a {@link Fixture}.  
     * @see abbot.script.UIContext#equivalent(abbot.script.UIContext)
     * @see abbot.script.StepRunner#run(Step)
     */
    public boolean equivalent(UIContext context) {
        return false;
    }
}
