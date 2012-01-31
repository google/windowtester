package abbot.script;

import java.awt.*;
import java.util.EmptyStackException;
import java.lang.reflect.Field;

import javax.swing.SwingUtilities;

import abbot.Log;
import abbot.Platform;
import abbot.i18n.Strings;
import abbot.util.*;

/**
 * A custom class loader which installs itself as if it were the application
 * class loader.  A classpath of null is equivalent to the system property
 * java.class.path.<p> 
 * The class loader may optionally load a class <i>before</i> the parent class
 * loader gets a chance to look for the class (instead of the default
 * behavior, which always delegates to the parent class loader first).  This
 * behavior enables the class to be reloaded simply by using a new instance of
 * this class loader with each launch of the app.<p>  
 * This class mimics the behavior of sun.misc.Launcher$AppClassLoader as much
 * as possible.<p>
 * Bootstrap classes are always delegated to the bootstrap loader, with the
 * exception of the sun.applet package, which should never be delegated, since
 * it does not work properly unless it is reloaded.<p>  
 * The parent of this class loader will be the normal, default AppClassLoader
 * (specifically, the class loader which loaded this class will be used).
 */
public class AppClassLoader extends NonDelegatingClassLoader {

    /** A new event queue installed for the lifetime of this class loader. */
    private AppEventQueue eventQueue;
    /** This class loader is used to load bootstrap classes that must be
     * preloaded.
     */
    private BootstrapClassLoader bootstrapLoader;
    /** For use in checking if a class is in the framework class path. */
    private NonDelegatingClassLoader extensionsLoader;
    /** Whether the framework itself is being tested. */
    private boolean frameworkIsUnderTest = false;

    /** Old class loader context for the thread where this loader was
     * installed.
     */
    private ClassLoader oldClassLoader = null;
    private Thread installedThread = null;
    private String oldClassPath = null;
    private class InstallationLock {}
    private InstallationLock lock = new InstallationLock();
	 
    /** Constructs an AppClassLoader using the current classpath (as found in
        java.class.path).
    */
    public AppClassLoader() {
        this(null);
    }

    /**
     * Constructs a AppClassLoader with a custom classpath, indicating
     * whether the class loader should delegate to its parent class loader
     * prior to searching for a given resource.<p>
     * The class path argument may use either a colon or semicolon to separate
     * its elements.<p>
     */
    public AppClassLoader(String classPath) {
        super(classPath, AppClassLoader.class.getClassLoader());
        bootstrapLoader = new BootstrapClassLoader();
        // Use this one to look up extensions; we want to reload them, but may
        // need to look in the framework class path for them.  
        // Make sure it *only* loads extensions, though, and defers all other
        // lookups to its parent.
        extensionsLoader =
            new NonDelegatingClassLoader(System.getProperty("java.class.path"),
                                         AppClassLoader.class.
                                         getClassLoader()) {
                protected boolean shouldDelegate(String name) {
                    return !isExtension(name);
                }
            };
        // Don't want to open a whole new can of class loading worms!
        /*
        // If we're testing the framework itself, then absolutely DO NOT
        // delegate those classes. 
        if (getClassPath().indexOf("abbot.jar") != -1) {
            frameworkIsUnderTest = true;
        }
        */
    }

    public boolean isEventDispatchThread() {
        return (eventQueue != null
            && Thread.currentThread() == eventQueue.thread)
            || (eventQueue == null
                && SwingUtilities.isEventDispatchThread());
    }

    /** Should the parent class loader try to load this class first? */
    // FIXME we should only need the delegate flag if stuff in the classpath
    // is also found on the system classpath, e.g. the framework itself
    // Maybe just set it internally in case the classpaths overlap?
    protected boolean shouldDelegate(String name) {
        return bootstrapLoader.shouldDelegate(name)
            && !isExtension(name)
            && !(frameworkIsUnderTest && isFrameworkClass(name));
    }

    private boolean isFrameworkClass(String name) {
        return name.startsWith("abbot.")
            || name.startsWith("junit.extensions.abbot.");
    }

    private boolean isExtension(String name) {
        return name.startsWith("abbot.tester.extensions.")
            || name.startsWith("abbot.script.parsers.extensions.");
    }

    /**
     * Finds and loads the class with the specified name from the search
     * path.  If the class is a bootstrap class and must be preloaded, use our
     * own bootstrap loader.  If it is an extension class, use our own
     * extensions loader.
     *
     * @param name the name of the class
     * @return the resulting class
     * @exception ClassNotFoundException if the class could not be found
     */
    public Class findClass(String name) throws ClassNotFoundException {
        if (isBootstrapClassRequiringReload(name)) {
            try {
                return bootstrapLoader.findClass(name);
            }
            catch(ClassNotFoundException cnf) {
                Log.warn(cnf);
            }
        }

        // Look for extensions first in the framework class path (with a
        // special loader), then in the app class path.
        // Extensions *must* have the same class loader as the corresponding
        // custom components
        try {
            return super.findClass(name);
        }
        catch(ClassNotFoundException cnf) {
            if (isExtension(name)) {
                return extensionsLoader.findClass(name);
            }
            throw cnf;
        }
    }

    /** Ensure that everything else subsequently loaded on the same thread or
     * any subsequently spawned threads uses the given class loader.  Also
     * ensure that classes loaded by the event dispatch thread and threads it
     * spawns use the given class loader.
     */
    public void install() {

        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException(Strings.get("appcl.invalid_state"));
        }

        if (installedThread != null) {
            String msg = Strings.get("appcl.already_installed",
                                     new Object[] { installedThread });
            throw new IllegalStateException(msg);
        }
        
        // Change the effective classpath, but make sure it's available if
        // someone needs to access it.
        oldClassPath = System.getProperty("java.class.path");
        System.setProperty("abbot.class.path", oldClassPath);
        System.setProperty("java.class.path", getClassPath());
        Log.debug("java.class.path set to "
                  + System.getProperty("java.class.path"));
        
        // Install our own handler for catching exceptions on the event
        // dispatch thread.
        try {
            new EventExceptionHandler().install();
        }
        catch(Exception e) {
            // ignore any exceptions, since they're not fatal
        }

        eventQueue = new AppEventQueue();
        eventQueue.install();
        
        Thread current = Thread.currentThread();
        installedThread = current;
        oldClassLoader = installedThread.getContextClassLoader();
        installedThread.setContextClassLoader(this);
    }
    
    public boolean isInstalled() {
        synchronized(lock) {
            return eventQueue != null;
        }
    }

    /** Reverse the effects of install.   Has no effect if the class loader
     * has not been installed on any thread.
     */
    public void uninstall() {
        // Ensure that no two threads attempt to uninstall 
        synchronized(lock) {
            if (eventQueue != null) {
                eventQueue.uninstall();
                eventQueue = null;
            }
            if (installedThread != null) {
                installedThread.setContextClassLoader(oldClassLoader);
                oldClassLoader = null;
                installedThread = null;
                System.setProperty("java.class.path", oldClassPath);
                oldClassPath = null;
            }
        }
    }

    private class AppEventQueue extends EventQueue {

        private Thread thread;

        /** Ensure the class loader for the event dispatch thread is the right
            one.
        */
        public void install() {
            Runnable installer = new Runnable() {
                public void run() {
                    Toolkit.getDefaultToolkit().
                        getSystemEventQueue().push(AppEventQueue.this);
                }
            };
            // Avoid deadlock with the event queue, in case it has the tree
            // lock (pickens). 
            AWT.invokeAndWait(installer);
            Runnable threadTagger = new Runnable() {
                public void run() {
                    thread = Thread.currentThread();
                    thread.setContextClassLoader(AppClassLoader.this);
                    thread.setName(thread.getName() + " (AppClassLoader)");
                }
            };
            AWT.invokeAndWait(threadTagger);
        }

        /** Pop this and any subsequently pushed event queues. */
        public void uninstall() {
            Log.debug("Uninstalling AppEventQueue");
            try {
                pop();
                thread = null;
            }
            catch(EmptyStackException ese) {
            }
            Log.debug("AppEventQueue uninstalled");
        }

        public String toString() {
            return "Abbot Event Queue: " + thread;
        }
    }
    /** List of bootstrap classes we most definitely want to be loaded by this
     * class loader, rather than any parent, or the bootstrap loader.
     */
    private String[] mustReloadPrefixes = {
        "sun.applet.",  // need the whole package, not just AppletViewer/Main
    };
    /** Does the given class absolutely need to be preloaded? */
    private boolean isBootstrapClassRequiringReload(String name) {
        for (int i=0;i < mustReloadPrefixes.length;i++) {
            if (name.startsWith(mustReloadPrefixes[i]))
                return true;
        }
        return false;
    }

    /** Returns the path to the primary JRE classes, not including any
     * extensions.  This is primarily needed for loading
     * sun.applet.AppletViewer/Main, since most other classes in the bootstrap
     * path should <i>only</i> be loaded by the bootstrap loader.
     */
    private static String getBootstrapPath() {
        return System.getProperty("sun.boot.class.path");
    }

    /** Provide access to bootstrap classes that we need to be able to
     * reload.
     */ 
    private class BootstrapClassLoader extends NonDelegatingClassLoader {
        public BootstrapClassLoader() {
            super(getBootstrapPath(), null);
        }
        protected boolean shouldDelegate(String name) {
            // Exclude all bootstrap classes, except for those we know we
            // *must* be reloaded on each run in order to have function
            // properly (e.g. applet)  
            return !isBootstrapClassRequiringReload(name)
                && !"abbot.script.AppletSecurityManager".equals(name);
        }
    }

    public String toString() {
        return super.toString() + " (java.class.path="
            + System.getProperty("java.class.path") + ")";
    }
}
