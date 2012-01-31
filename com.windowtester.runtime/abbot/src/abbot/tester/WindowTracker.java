package abbot.tester;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import abbot.Log;
import abbot.util.NamedTimer;
import abbot.util.Properties;
import abbot.util.WeakAWTEventListener;

/** Keep track of all known root windows, and all known showing/hidden/closed
 * windows.
 */
public class WindowTracker {
    
    private static class Holder {
        public static final WindowTracker INSTANCE = new WindowTracker();
    }
    /** Maps unique event queues to the set of root windows found on each
        queue. 
     */
    private Map contexts;
    /** Maps components to their corresponding event queues. */
    private Map queues;
    private ContextTracker contextTracker;
    /** Windows which for which isShowing is true but are not yet ready for
        input. */
    private Map pendingWindows = new WeakHashMap();
    /** Windows which we deem are ready to use.  */
    private Map openWindows = new WeakHashMap();
    /** Windows which are not visible. */
    private Map hiddenWindows = new WeakHashMap();
    /** Windows which have sent a WINDOW_CLOSE event. */
    private Map closedWindows = new WeakHashMap();
    private WindowReadyTracker windowReadyTracker;
    private java.awt.Robot robot;
    static int WINDOW_READY_DELAY = 
        Properties.getProperty("abbot.window_ready_delay", 5000, 0, 60000);
    private Timer windowReadyTimer;
    
    /** Only ever want one of these. */
    public static WindowTracker getTracker() {
        return Holder.INSTANCE;
    }

    /** Create an instance of WindowTracker which will track all windows
     * coming and going on the current and subsequent app contexts. 
     * WARNING: if an applet loads this class, it will only ever see stuff in
     * its own app context.
     */
    WindowTracker() {
        contextTracker = new ContextTracker();
        long mask = WindowEvent.WINDOW_EVENT_MASK
            | ComponentEvent.COMPONENT_EVENT_MASK;
        new WeakAWTEventListener(contextTracker, mask);
        windowReadyTracker = new WindowReadyTracker();
        mask = InputEvent.MOUSE_MOTION_EVENT_MASK
            |InputEvent.MOUSE_EVENT_MASK|InputEvent.PAINT_EVENT_MASK;
        new WeakAWTEventListener(windowReadyTracker, mask);
        // hold the event queue references weakly
        // each queue maps to a set of components (actually a weak hash map to
        // allow GC of the component keys).
        contexts = new WeakHashMap();
        contexts.put(Toolkit.getDefaultToolkit().getSystemEventQueue(),
                     new WeakHashMap());
        // hold both the component references and the event queues weakly
        queues = new WeakHashMap();
        // Populate stuff that may already have shown/been hidden
        Frame[] frames = Frame.getFrames();
        synchronized(openWindows) {
            for (int i=0;i < frames.length;i++) {
                scanExistingWindows(frames[i]);
            }
        }
        try {
            robot = new java.awt.Robot();
        }
        catch(AWTException e) {
        }
        windowReadyTimer = new NamedTimer("Window Ready Timer", true);
    }

    private void scanExistingWindows(Window w) {
        // Make sure we catch subsequent show/hide events for this window
        new WindowWatcher(w);
        Window[] windows = w.getOwnedWindows();
        for (int i=0;i < windows.length;i++) {
            scanExistingWindows(windows[i]);
        }
        openWindows.put(w, Boolean.TRUE);
        if (!w.isShowing()) {
            hiddenWindows.put(w, Boolean.TRUE);
        }
        noteContext(w);
    }

    /** Returns whether the window is ready to receive OS-level event input.
        A window's "isShowing" flag may be set true before the WINDOW_OPENED
        event is generated, and even after the WINDOW_OPENED is sent the
        window peer is not guaranteed to be ready.
    */ 
    public boolean isWindowReady(Window w) {
        synchronized(openWindows) {
            if (openWindows.containsKey(w)
                && !hiddenWindows.containsKey(w)) {
                return true;
            }
        }
        if (robot != null)
            checkWindow(w, robot);
        return false;
    }

    /** Return the event queue corresponding to the given component.  In most
        cases, this is the same as
        Component.getToolkit().getSystemEventQueue(), but in the case of
        applets will bypass the AppContext and provide the real event queue.
    */
    public EventQueue getQueue(Component c) {
        // Components above the applet in the hierarchy may or may not share
        // the same context with the applet itself.
        while (!(c instanceof java.applet.Applet) && c.getParent() != null)
            c = c.getParent();
        synchronized(contexts) {
            WeakReference ref = (WeakReference)queues.get(c);
            EventQueue q = ref != null ? (EventQueue)ref.get() : null;
            if (q == null)
                q = c.getToolkit().getSystemEventQueue();
            return q;
        }
    }

    /** Returns all known event queues. */
    public Collection getEventQueues() {
        HashSet set = new HashSet();
        synchronized(contexts) {
            set.addAll(contexts.keySet());
            Iterator iter = queues.values().iterator();
            while (iter.hasNext()) {
                WeakReference ref = (WeakReference)iter.next();
                EventQueue q = (EventQueue)ref.get();
                if (q != null)
                    set.add(q);
            }
        }
        return set;
    }

    /** Return all available root Windows.  A root Window is one
     * that has a null parent.  Nominally this means a list similar to that
     * returned by Frame.getFrames(), but in the case of an Applet may return
     * a few Dialogs as well.
     */
    public Collection getRootWindows() {
        Set set = new HashSet();
        // Use Frame.getFrames() here in addition to our watched set, just in
        // case any of them is missing from our set.
        synchronized(contexts) {
            Iterator iter = contexts.keySet().iterator();
            while (iter.hasNext()) {
                EventQueue queue = (EventQueue)iter.next();
                Map map = (Map)contexts.get(queue);
                set.addAll(map.keySet());
            }
        }
        Frame[] frames = Frame.getFrames();
        for (int i=0;i < frames.length;i++) {
            set.add(frames[i]);
        }
        //Log.debug(String.valueOf(list.size()) + " total Frames");
        return set;
    }

    /** Provides tracking of window visibility state.  We explicitly add this
     * on WINDOW_OPEN and remove it on WINDOW_CLOSE to avoid having to process
     * extraneous ComponentEvents.
     */
    private class WindowWatcher
        extends WindowAdapter implements ComponentListener {
        public WindowWatcher(Window w) {
            w.addComponentListener(this);
            w.addWindowListener(this);
        }
        public void componentShown(ComponentEvent e) {
            markWindowShowing((Window)e.getSource());
        }
        public void componentHidden(ComponentEvent e) {
            synchronized(openWindows) {
                //Log.log("Marking " + e.getSource() + " hidden");
                hiddenWindows.put(e.getSource(), Boolean.TRUE);
                pendingWindows.remove(e.getSource());
            }
        }
        public void windowClosed(WindowEvent e) {
            e.getWindow().removeWindowListener(this);
            e.getWindow().removeComponentListener(this);
        }
        public void componentResized(ComponentEvent e) { }
        public void componentMoved(ComponentEvent e) { }
    }

    /** Whenever we get a window that's on a new event dispatch thread, take
     * note of the thread, since it may correspond to a new event queue and
     * AppContext. 
     */
    // FIXME what if it has the same app context? can we check?
    private class ContextTracker implements AWTEventListener {
        public void eventDispatched(AWTEvent ev) {

            ComponentEvent event = (ComponentEvent)ev;
            Component comp = event.getComponent();
            // This is our sole means of accessing other app contexts
            // (if running within an applet).  We look for window events
            // beyond OPENED in order to catch windows that have already
            // opened by the time we start listening but which are not
            // in the Frame.getFrames list (i.e. they are on a different
            // context).   Specifically watch for COMPONENT_SHOWN on applets,
            // since we may not get frame events for them.
            if (!(comp instanceof java.applet.Applet)
                && !(comp instanceof Window)) {
                return;
            }

            int id = ev.getID();
            if (id == WindowEvent.WINDOW_OPENED) {
                noteOpened(comp);
            }
            else if (id == WindowEvent.WINDOW_CLOSED) {
                noteClosed(comp);
            }
            else if (id == WindowEvent.WINDOW_CLOSING) {
                // ignore
            }
            // Take note of all other window events
            else if ((id >= WindowEvent.WINDOW_FIRST
                      && id <= WindowEvent.WINDOW_LAST)
                     || id == ComponentEvent.COMPONENT_SHOWN) {
                synchronized(openWindows) {
                    if (!getRootWindows().contains(comp)
                        || closedWindows.containsKey(comp)) {
                        noteOpened(comp);
                    }
                }
            }
            // The context for root-level windows may change between
            // WINDOW_OPENED and subsequent events.
            synchronized(contexts) {
                WeakReference ref = (WeakReference)queues.get(comp);
                if (ref != null
                    && !comp.getToolkit().getSystemEventQueue().
                    equals(ref.get())) {
                    noteContext(comp);
                }
            }
        }
    }

    private class WindowReadyTracker implements AWTEventListener {
        public void eventDispatched(AWTEvent e) {
            if (e.getID() == MouseEvent.MOUSE_MOVED
                || e.getID() == MouseEvent.MOUSE_DRAGGED) {
                Component c = (Component)e.getSource();
                Window w = c instanceof Window
                    ? (Window)c
                    : SwingUtilities.getWindowAncestor(c);
                markWindowReady(w);
            }
        }
    }
    private void noteContext(Component comp) {
        EventQueue queue = comp.getToolkit().getSystemEventQueue();
        synchronized(contexts) {
            Map map = (Map)contexts.get(queue);
            if (map == null) {
                contexts.put(queue, map = new WeakHashMap());
            }
            if (comp instanceof Window && comp.getParent() == null) {
                map.put(comp, Boolean.TRUE);
            }
            queues.put(comp, new WeakReference(queue));
        }
    }

    private void noteOpened(Component comp) {
        //Log.log("Noting " + comp + " opened");
        noteContext(comp);
        // Attempt to ensure the window is ready for input before recognizing
        // it as "open".  There is no Java API for this, so we institute an
        // empirically tested delay.
        if (comp instanceof Window) {
            new WindowWatcher((Window)comp);
            markWindowShowing((Window)comp);
            // Native components don't receive events anyway...
            if (comp instanceof FileDialog) {
                markWindowReady((Window)comp);
            }
        }
    }

    private void noteClosed(Component comp) {
        if (comp.getParent() == null) {
            EventQueue queue = comp.getToolkit().getSystemEventQueue();
            synchronized(contexts) {
                Map whm = (Map)contexts.get(queue);
                if (whm != null)
                    whm.remove(comp);
                else {
                    EventQueue foundQueue = null;
                    Iterator iter = contexts.keySet().iterator();
                    while (iter.hasNext()) {
                        EventQueue q = (EventQueue)iter.next();
                        Map map = (Map)contexts.get(q);
                        if (map.containsKey(comp)) {
                            foundQueue = q;
                            map.remove(comp);
                        }
                    }
                    if (foundQueue == null) {
                        Log.log("Got WINDOW_CLOSED on "
                                + Robot.toString(comp)
                                + " on a previously unseen context: "
                                + queue + "("
                                + Thread.currentThread() + ")");
                    }
                    else {
                        Log.log("Window " + Robot.toString(comp)
                                + " sent WINDOW_CLOSED on "
                                + queue + " but sent WINDOW_OPENED on "
                                + foundQueue);
                    }
                }
            }
        }
        synchronized(openWindows) {
            //Log.log("Marking " + comp + " closed");
            openWindows.remove(comp);
            hiddenWindows.remove(comp);
            closedWindows.put(comp, Boolean.TRUE);
            pendingWindows.remove(comp);
        }
    }

    /** Mark the given Window as ready for input.  Indicate whether any
     * pending "mark ready" task should be canceled.
     */
    private void markWindowReady(Window w) {
        synchronized(openWindows) {
            // If the window was closed after the check timer started running,
            // it will have canceled the pending ready.
            // Make sure it's still on the pending list before we actually
            // mark it ready.
            if (pendingWindows.containsKey(w)) {
                //Log.log("Noting " + w + " ready");
                closedWindows.remove(w);
                hiddenWindows.remove(w);
                openWindows.put(w, Boolean.TRUE);
                pendingWindows.remove(w);
            }
        }
    }

    /** Indicate a window has set isShowing true and needs to be marked ready
        when it is actually ready.
    */
    private void markWindowShowing(final Window w) {
        synchronized(openWindows) {
            pendingWindows.put(w, Boolean.TRUE);
        }
    }

    private Insets getInsets(Container c) {
        try {
            Insets insets = c.getInsets();
            if (insets != null)
                return insets;
        }
        catch(NullPointerException e) {
            // FileDialog.getInsets() throws (1.4.2_07)
        }
        return new Insets(0, 0, 0, 0);
    }

    private static int sign = 1;
    /** Actively check whether the given window is ready for input.
     * @param robot
     * @see #isWindowReady
     */
    private void checkWindow(final Window w, java.awt.Robot robot) {
        // Must avoid frame borders, which are insensitive to mouse
        // motion (at least on w32).
        final Insets insets = getInsets(w);
        final int width = w.getWidth();
        final int height = w.getHeight();
        int x = w.getX() + insets.left
            + (width-(insets.left+insets.right))/2;
        int y = w.getY() + insets.top
            + (height-(insets.top+insets.bottom))/2;
        if (x != 0 && y != 0) {
            robot.mouseMove(x, y);
            if (width > height)
                robot.mouseMove(x + sign, y);
            else
                robot.mouseMove(x, y + sign);
            sign = -sign;
        }
        synchronized(openWindows) {
            if (pendingWindows.get(w) == Boolean.TRUE
                && isEmptyFrame(w)) {
                // Force the frame to be large enough to receive events
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        int nw = Math.max(width,
                                          insets.left + insets.right + 3);
                        int nh = Math.max(height,
                                          insets.top + insets.bottom + 3);
                        w.setSize(nw, nh);
                    }
                });
            }
            // At worst, time out and say the window is ready
            // after the configurable delay
            TimerTask task = new TimerTask() {
                public void run() {
                    markWindowReady(w);
                }
            };
            windowReadyTimer.schedule(task, WINDOW_READY_DELAY);
            pendingWindows.put(w, task);
        }
    }

    /** We can't get any motion events on an empty frame. */
    private boolean isEmptyFrame(Window w) {
        Insets insets = getInsets(w);
        return insets.top + insets.bottom == w.getHeight()
            || insets.left + insets.right == w.getWidth();
    }
}
