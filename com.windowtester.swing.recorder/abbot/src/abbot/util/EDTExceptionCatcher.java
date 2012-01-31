/*
 * Copyright (c) 2005 Timothy Wall, All Rights Reserved
 */
package abbot.util;

import abbot.*;


/** Provide access to the most recent exception caught on the event
    dispatch thread.
 */ 
public class EDTExceptionCatcher
    extends EventDispatchExceptionHandler {

    private static Throwable throwable = null;
    private static long when = -1;

    public void install() {
        clear();
        super.install();
    }

    /** Return the most recent exception caught on the dispatch thread, or
        <code>null</code> if none has been thrown or the exception has been
        cleared.  Also clears the exception. 
    */
    public synchronized static Throwable getThrowable() {
        Throwable t = throwable;
        clear();
        return t;
    }

    /** Returns when the most recent exception was caught on the dispatch
        thread, or -1 if none has been thrown or the exception has been
        cleared.
    */
    public synchronized static long getThrowableTime() {
        return when;
    }

    public synchronized static void clear() {
        throwable = null;
        when = -1;
    }

    protected void exceptionCaught(Throwable thr) {
        if (!(thr instanceof ExitException)) {
            Log.log("Caught " + thr.getClass());
            synchronized(EDTExceptionCatcher.class) {
                when = System.currentTimeMillis();
                throwable = thr;
            }
        }
    }
}
