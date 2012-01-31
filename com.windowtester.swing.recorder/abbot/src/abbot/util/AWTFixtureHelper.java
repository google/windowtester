package abbot.util;

import java.awt.AWTEvent;
import java.awt.event.*;

import abbot.Log;
import abbot.ExitException;
import abbot.util.*;
import abbot.script.*;
import abbot.tester.Robot;

/** Provides reusable preservation/restoration of AWT UI/System state.  Also
 * handles standardized fixture event logging and catching exceptions on the
 * AWT event dispatch thread (EDT).
 * This class should be used at setup and teardown of your chosen fixture.
 * @see junit.extensions.abbot.ComponentTestFixture
 * @see abbot.script.StepRunner  
 */

public class AWTFixtureHelper {
    private AWTEventListener listener = null;
    private SystemState state;
    /** Create an instance of AWTFixtureHelper which makes a snapshot of the
        current VM state.
    */
    public AWTFixtureHelper() {
        // Preserve all system properties to restore them later
        state = new SystemState();

        // Install our own event handler, which will forward events thrown on
        // the event queue
        try {
            new EDTExceptionCatcher().install();
        }
        catch(RuntimeException re) {
            // Not fatal if we can't install, since most tests don't
            // depend on it.  We won't be able to throw errors that were
            // generated on the event dispatch thread, though.
        }
        // Only enable event logging if debug is enabled for this class
        // Facilitate debugging by logging all events
        if (Boolean.getBoolean("abbot.fixture.log_events")) {
            long mask = Properties.getProperty("abbot.fixture.event_mask",
                                               Long.MIN_VALUE,
                                               Long.MAX_VALUE,
                                               abbot.editor.recorder.
                                               EventRecorder.
                                               RECORDING_EVENT_MASK);

            Log.log("Using mask value " + mask);
            listener = new AWTEventListener() {
                public void eventDispatched(AWTEvent event) {
                    if (listener != null)
                        Log.log(Robot.toString(event));
                }
            };
            new WeakAWTEventListener(listener, mask);
        }
    }

    /** Returns the last exception thrown on the event dispatch thread, or
        <code>null</code> if no such exception has been thrown.
    */
    public Throwable getEventDispatchError() {
        return EDTExceptionCatcher.getThrowable();
    }

    /** Returns the time of the last exception thrown on the event dispatch
        thread.
    */
    public long getEventDispatchErrorTime() {
        return EDTExceptionCatcher.getThrowableTime();
    }

    /** Restore the state that was preserved when this object was created. */
    public void restore() {
        AWT.dismissAWTPopup();
        state.restore();

        // Encourage GC of unused components, which reduces the load on
        // future tests.
        System.gc();
        System.runFinalization();
    }
}

