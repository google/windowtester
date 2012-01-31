package abbot.util;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import abbot.Log;

/** Handler for uncaught exceptions on any event dispatch thread.
    Once this has been installed, the class must be accessible by any
    subsequently launched dispatch thread.<p>

    This handler is installed by setting the System property
    sun.awt.exception.handler.  See javadoc for java.awt.EventDispatchThread
    for details.  This is sort of a patch to Sun's implementation, which only
    checks the property once and caches the result ever after.  This
    implementation will always chain to the handler indicated by the current
    value of the property.<p>

    It is most definitely NOT safe to try to install several of these on
    different threads.
 */
public class EventDispatchExceptionHandler {
    /** See javadoc for java.awt.EventDispatchThread. */
    public static final String PROP_NAME = "sun.awt.exception.handler";

    private static boolean installed = false;
    private static boolean canInstall = true;

    /** Install a handler for event dispatch exceptions.  This is kind
        of a hack, but it's Sun's hack.
        See the javadoc for java.awt.EventDispatchThread for details.
        NOTE: we throw an exception immediately, which 
        ensures that our handler is installed, since otherwise 
        someone might set this property later.
        java.awt.EventDispatchThread doesn't actually load the handler
        specified by the property until an exception is caught by the
        event dispatch thread.  SwingSet2 in 1.4.1 installs its own.
        Note that a new instance is created for each exception thrown.

        @throws RuntimeException if the handler cannot be installed.
        @throws IllegalStateException if this method is invoked from an event
        dispatch thread. 
        @throws IllegalArgumentException if the given class is not derived
        from this one. 

        // TODO: read the private static field
        // String EventDispatchThread.handlerClassName and override it if
        // necessary.
    */
    public void install() {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Handler must not be installed from the event dispatch thread");
        }

        Class cls = getClass();
        final String className = cls.getName();
        try {
            cls.newInstance();
        }
        catch(Exception e) {
            String msg = "Exception handler ("
                + cls + ") must have an accessible no-args Constructor: " + e;
            throw new IllegalArgumentException(msg);
        }
        if (installed) {
            // If we've already installed an instance of
            // this handler, all we need to do is set the
            // property name.
            Log.debug("Exception handler class already installed");
            System.setProperty(PROP_NAME, className);
        }
        else if (!canInstall) {
            Log.warn("Can't install event dispatch exception handler");
        }
        else {
            Log.log("Attempting to install handler " + className);
            class PropertiesHolder {
                /** Preserve the system properties state. */
                public java.util.Properties properties = null;
            }
            final PropertiesHolder holder = new PropertiesHolder();
            // Even if it's been set to something else, we can override it
            // if there hasn't been an event exception thrown yet.
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    holder.properties = (java.util.Properties)
                        System.getProperties().clone();
                    // Set the property just before throwing the exception;
                    // OSX sets the property as part of AWT startup, so
                    // we have to override it here.
                    System.setProperty(PROP_NAME, className);
                    throw new DummyException();
                }
            });
            // Does nothing but wait for the previous invocation to finish
            AWT.invokeAndWait(new Runnable() { public void run() { } });
            System.setProperties(holder.properties);
            String oldHandler = System.getProperty(PROP_NAME);
            
            if (installed) {
                if (oldHandler != null) {
                    Log.debug("Replaced an existing event exception handler ("
                              + oldHandler + ")");
                }
            }
            else {
                canInstall = false;
                String msg = "The handler for event "
                    + "dispatch thread exceptions could not be installed";
                if (oldHandler != null) {
                    msg += " (" + oldHandler + " has already been "
                        + "set and cached; there is no way to override it)";
                }
                Log.warn(msg);
                throw new RuntimeException(msg);
            }
        }
    }

    /** Define this to handle the exception as needed.  
     * Default prints a warning to System.err.
     */
    protected void exceptionCaught(Throwable thrown) {
        System.err.println("Exception caught on event dispatch thread: " + thrown);
    }

    /** Handle exceptions thrown on the event dispatch thread. */
    public void handle(Throwable thrown) {
        Log.debug("Handling event dispatch exception: " + thrown);
        String handler = System.getProperty(PROP_NAME);
        boolean handled = false;
        if (handler != null && !handler.equals(getClass().getName())) {
            Log.debug("A user exception handler ("
                      + handler + ") has been set, invoking it");
            try {
                ClassLoader cl =
                    Thread.currentThread().getContextClassLoader();
                Class c = Class.forName(handler, true, cl);
                c.getMethod("handle", new Class[] { Throwable.class }).
                    invoke(c.newInstance(), new Object[] { thrown });
                handled = true;
            }
            catch(Throwable e) {
                Log.warn("Could not invoke user handler: " + e);
            }
        }
        // The exception may be created by a different class loader
        // so compare by name only
        if (thrown instanceof DummyException) {
            // Install succeeded
            Log.debug("Installation succeeded");
            installed = true;
        }
        else {
            if (!handled) {
                Log.debug("Handling exception on event dispatch thread: "
                          + thrown + " with " + getClass());
                Log.debug(thrown);
                exceptionCaught(thrown);
            }
        }
        Log.debug("Handling done");
    }

    public static boolean isInstalled() {
        return installed;
    }

    private static class DummyException extends RuntimeException {
    	private static final long serialVersionUID = 1L;
        public String toString() {
            return super.toString() + " " + getClass().getClassLoader();
        }
    }
}

