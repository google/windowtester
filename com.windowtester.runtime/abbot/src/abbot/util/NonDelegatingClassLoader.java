package abbot.util;

import abbot.Log;

/** Provides support for loading a class <i>before</i> checking the parent
 * class loader for it.  If the shouldDelegate method returns false for a
 * given class name, it will defer to its parent class loader only if the
 * class is not found in this loader's path.  This provides a means for
 * reloading classes that would otherwise be permanently cached by the app
 * or boot class loaders.<p>
 * The name for this class is not quite correct; it <i>will</i> delegate to
 * its parent if it doesn't find a given class.
 */
public class NonDelegatingClassLoader extends PathClassLoader {

    public NonDelegatingClassLoader(String path, ClassLoader parent) {
        super(path, parent);
    }

    /** Returns whether the given class should be given to the parent class
     * loader to try before this one does.  The default implementation always
     * returns false.  Making this method return true will revert to the
     * standard class loader behavior. 
     */ 
    protected boolean shouldDelegate(String name) {
        return false;
    }

    /** Find the given class in the search path. */
    public Class findClass(String name) throws ClassNotFoundException {
        Log.debug("Looking up " + name + " with " + this);
        return super.findClass(name);
    }

    /** Load the given class, but attempt to load <i>before</i> the parent if
        shouldDelegate returns false for the given class.
    */
    protected synchronized Class loadClass(String name, boolean resolve)
        throws ClassNotFoundException {
        if (shouldDelegate(name)) {
            Log.debug("Delegating lookup for " + name);
            return super.loadClass(name, resolve);
        }
        else {
            Log.debug("Non-delegating lookup for " + name);
            Class c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                }
                catch(SecurityException se) {
                    Log.debug(se);
                    // We're not allowed to find it, so give it to the parent
                    return super.loadClass(name, resolve);
                }
                catch(ClassNotFoundException cnf) {
                    // If we can't find it, maybe the parent can
                    return super.loadClass(name, resolve);
                }
                if (resolve) {
                    resolveClass(c);
                }
            }
            else {
                Log.debug("Class already loaded " + name);
            }
            return c;
        }
    }
}
