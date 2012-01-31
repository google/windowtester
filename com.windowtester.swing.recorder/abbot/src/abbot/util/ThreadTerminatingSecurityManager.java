package abbot.util;

import java.security.Permission;
import java.util.*;

import abbot.NoExitSecurityManager;

/** Provides a method for terminating threads over which you otherwise have no
 * control.  Usually works.<p>
 * NOTE: Still needs some work; if main script editor exits from the event
 * dispatch thread, an exception is thrown and the exit aborted.  Perhaps
 * ignore event dispatch threads?
 */

public abstract class ThreadTerminatingSecurityManager
    extends NoExitSecurityManager {

    public class ThreadTerminatedException extends SecurityException {
    }

    private Map terminatedGroups = new WeakHashMap();
    
    private boolean isTerminated(Thread t) {
        Iterator iter = terminatedGroups.keySet().iterator();
        while (iter.hasNext()) {
            ThreadGroup group = (ThreadGroup)iter.next();
            ThreadGroup thisGroup = t.getThreadGroup();
            if (thisGroup != null
                && group.parentOf(thisGroup)) {
                return true;
            }
        }
        return false;
    }

    /** Ensure ThreadTermination exceptions are thrown for any thread in the
     * given group when any such thread causes the security manager to be
     * invoked. 
     */ 
    public void terminateThreads(ThreadGroup group) {
        if (group == null)
            throw new NullPointerException("Thread group must not be null");
        terminatedGroups.put(group, Boolean.TRUE);
        // maybe do an interrupt/notify; but be careful b/c you might block
        // trying to sycnhronize on the thread
        if (group.activeCount() == 0) {
            try {
                group.destroy();
            }
            catch(IllegalThreadStateException e) {
            }
        }
    }

    /** Throw ThreadTerminated for any thread marked for termination. */
    public void checkPermission(Permission perm, Object context) {
        if (isTerminated(Thread.currentThread()))
            throw new ThreadTerminatedException();
        super.checkPermission(perm, context);
    }

    /** Throw ThreadTerminated for any thread marked for termination. */
    public void checkPermission(Permission perm) {
        if (isTerminated(Thread.currentThread()))
            throw new ThreadTerminatedException();
        super.checkPermission(perm);
    }
}
