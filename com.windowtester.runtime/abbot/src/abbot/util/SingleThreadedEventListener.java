package abbot.util;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import abbot.Log;
import abbot.tester.Robot;

/** Provide an AWTEventListener which ensures all events are handled on the
    event dispatch thread.  This allows the recorders and other
    listeners to safely manipulate GUI objects without concern for event
    dispatch thread-safety. 
    <p>
    Window.show generates WINDOW_OPENED (and possibly hierarchy and other
    events) to any listeners from whatever thread the method was invoked on.
    <p>
    NOTE: Applet runners may run several simultaneous event dispatch threads
    when displaying multiple applets simultaneously.  If this listener is
    installed in the parent context of those dispatch threads, it will be
    invoked on each of those threads, possibly simultaneously.
*/
public abstract class SingleThreadedEventListener implements AWTEventListener {
    private ArrayList deferredEvents = new ArrayList();

    private Runnable action = new Runnable() {
        public void run() {
            processDeferredEvents();
        }
    };

    /** Event reception callback.  */
    public void eventDispatched(AWTEvent event) {
        if (!SwingUtilities.isEventDispatchThread()) {
            // Often the application under test will invoke Window.show, which
            // spawns hierarchy events.  We want to ensure we respond to those
            // events on the dispatch thread to avoid deadlock.
            Log.debug("deferring event handling of " + Robot.toString(event));
            synchronized(deferredEvents) {
                deferredEvents.add(event);
            }
            // Ensure that in the absence of any subsequent event thread
            // events deferred events still get processed.
            // If regular events are received before this action is run, the
            // deferred events will be processed prior to those events and the
            // action will do nothing.
            SwingUtilities.invokeLater(action);
        }
        else {
            // Ensure any deferred events are processed prior to subsequently
            // posted events.
            processDeferredEvents();
            processEvent(event);
        }
    }

    /** Process any events that were generated off the event queue but not
        immediately handled.
    */
    protected void processDeferredEvents() {
        // Make a copy of the deferred events and empty the queue
        ArrayList queue = new ArrayList();
        synchronized(deferredEvents) {
            // In the rare case where there are multiple simultaneous dispatch
            // threads, it's possible for deferred events to get posted while
            // another event is being processed.  At most this will mean a few
            // events get processed out of order, but they will likely be from
            // different event dispatch contexts, so it shouldn't matter.
            queue.addAll(deferredEvents);
            deferredEvents.clear();
        }
        while (queue.size() > 0) {
            AWTEvent prev = null;
            Log.debug("processing deferred event");
            // Process any events that were generated
            prev = (AWTEvent)queue.get(0);
            queue.remove(0);
            processEvent(prev);
        }
    }

    /** This method is not protected by any synchronization locks (nor should
        it be); in the presence of multiple simultaneous event dispatch
        threads, the listener must be threadsafe.
    */
    protected abstract void processEvent(AWTEvent event);
}


