package abbot.tester;

import java.awt.*;
import java.awt.event.*;
import java.lang.ref.WeakReference;
import java.util.Stack;
import javax.swing.SwingUtilities;

import abbot.Log;
import abbot.util.*;

/** Class to keep track of a given input state.  Includes mouse/pointer
 * position and keyboard modifier key state.<p>
 * Synchronization assumes that any given instance might be called from more
 * than one event dispatch thread.
 */
// TODO: add a BitSet with the full keyboard key press state
public class InputState {

    private static final int BUTTON_MASK = (MouseEvent.BUTTON1_MASK
                                            |MouseEvent.BUTTON2_MASK
                                            |MouseEvent.BUTTON3_MASK);
    
    /** Current mouse position, in component coordinates. */
    private Point mouseLocation = new Point(0, 0);
    /** Current mouse position, in screen coordinates. */
    private Point mouseLocationOnScreen = new Point(0, 0);
    /** Keep a stack of mouse-entered components.  Note that the pointer is
        still considered within a frame when the mouse enters a contained
        component.
    */
    private Stack componentStack = new Stack();
    private Stack locationStack = new Stack();
    private Stack screenLocationStack = new Stack();
    private int buttonsDown;
    private int modifiersDown;
    private long lastEventTime;
    private int clickCount;
    private Component dragSource;
    private int dragX, dragY;
    private EventNormalizer normalizer;

    public InputState() {
        long mask = MouseEvent.MOUSE_MOTION_EVENT_MASK
            |MouseEvent.MOUSE_EVENT_MASK|KeyEvent.KEY_EVENT_MASK;
        AWTEventListener listener = new SingleThreadedEventListener() {
            protected void processEvent(AWTEvent event) {
                update(event);
            }
        };
        normalizer = new EventNormalizer();
        normalizer.startListening(listener, mask);
    }

    public synchronized void clear() {
        componentStack.clear();
        locationStack.clear();
        buttonsDown = 0;
        modifiersDown = 0;
        lastEventTime = 0;
        clickCount = 0;
        dragSource = null;
    }

    public void dispose() {
        normalizer.stopListening();
        normalizer = null;
    }

    /** Explicitly update the internal state.  Allows Robot to update the
     * state with events it has just posted.
     */
    void update(AWTEvent event) {
        if (event instanceof MouseEvent) {
            updateState((MouseEvent)event);
        }
        else if (event instanceof KeyEvent) {
            updateState((KeyEvent)event);
        }
    }

    protected void updateState(KeyEvent ke) {
        // Ignore "old" events
        if (ke.getWhen() < lastEventTime)
            return;

        synchronized(this) {
            lastEventTime = ke.getWhen();
            modifiersDown = ke.getModifiers();
            // FIXME add state of individual keys
        }
    }

    protected void updateState(MouseEvent me){
        // Ignore "old" events
        if (me.getWhen() < lastEventTime) {
            if (Log.isClassDebugEnabled(getClass()))
                Log.debug("Ignoring " + Robot.toString(me));
            return;
        }
        Point where = me.getPoint();
        // getComponentAt and getLocationOnScreen want the tree lock, so be
        // careful not to use any additional locks at the same time to avoid
        // deadlock.
        Point eventScreenLoc = null;
        boolean screenLocationFound = true;
        // Determine the current mouse position in screen coordinates
        try {
            eventScreenLoc = AWT.getLocationOnScreen(me.getComponent());
        }
        catch(IllegalComponentStateException e) {
            // component might be hidden by the time we process this event
            screenLocationFound = false;
        }
        synchronized(this) {
            lastEventTime = me.getWhen();
            // When a button is released, only that button appears in the
            // modifier mask
            int whichButton = me.getModifiers() & BUTTON_MASK;
            if (me.getID() == MouseEvent.MOUSE_RELEASED
                || me.getID() == MouseEvent.MOUSE_CLICKED) {
                buttonsDown &= ~whichButton;
                modifiersDown &= ~whichButton;
            }
            else {
                buttonsDown |= whichButton;
                modifiersDown |= whichButton;
            }
            clickCount = me.getClickCount();
            if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                dragSource = me.getComponent();
                dragX = me.getX();
                dragY = me.getY();
            }
            else if (me.getID() == MouseEvent.MOUSE_RELEASED
                     || me.getID() == MouseEvent.MOUSE_MOVED) {
                dragSource = null;
            }

            if (me.getID() == MouseEvent.MOUSE_ENTERED) {
                componentStack.push(new WeakReference(me.getComponent()));
                locationStack.push(me.getPoint());
                screenLocationStack.push(screenLocationFound
                                         ? eventScreenLoc : me.getPoint());

            }
            else if (me.getID() == MouseEvent.MOUSE_EXITED) {
                if (componentStack.empty()) {
                    if (Log.isClassDebugEnabled(getClass()))
                        Log.debug("Got " + Robot.toString(me)
                                  + " but component not on stack");
                }
                else {
                    componentStack.pop();
                    locationStack.pop();
                    screenLocationStack.pop();
                }
            }
            if (screenLocationFound) {
                if (componentStack.empty()) {
                    mouseLocation = null;
                }
                else {
                    mouseLocation = new Point(where);
                }
                mouseLocationOnScreen.setLocation(eventScreenLoc);
                mouseLocationOnScreen.translate(where.x, where.y);
            }
        }
    }

    /** Return the component under the given coordinates in the given parent
        component.  Events are often generated only for the outermost
        container, so we have to determine if the pointer is actually within a
        child.  Basically the same as Component.getComponentAt, but recurses
        to the lowest-level component instead of only one level.  Point is in
        component coordinates.<p> 
        The default Component.getComponentAt can return invisible components
        (JRootPane has an invisible JPanel (glass pane?) which will otherwise
        swallow everything).<p>
        NOTE: getComponentAt grabs the TreeLock, so this should *only* be
        invoked on the event dispatch thread, preferably with no other locks
        held.  Use it elsewhere at your own risk.<p>
        NOTE: What about drags outside a component?
    */
    public static Component getComponentAt(Component parent, Point p) {
        Log.debug("Checking " + p + " in " + Robot.toString(parent));
        Component c = SwingUtilities.getDeepestComponentAt(parent, p.x, p.y);
        Log.debug("Deepest is " + Robot.toString(c));
        return c;
    }

    /** Return the most deeply nested component which currently contains the
     * pointer. 
     */
    public synchronized Component getUltimateMouseComponent() {
        Component c = getMouseComponent();
        if (c != null) {
            Point p = getMouseLocation();
            c = getComponentAt(c, p);
        }
        return c;
    }

    /** Return the last known Component to contain the pointer, or null if
        none.  Note that this may not correspond to the component that
        actually shows up in AWTEvents.
    */
    public synchronized Component getMouseComponent() {
        Component comp = null;
        if (!componentStack.empty()) {
            WeakReference ref = (WeakReference)componentStack.peek();
            comp = (Component)ref.get();
            // Make sure we don't return a component that has gone away.
            if (comp == null || !comp.isShowing()) {
                Log.debug("Discarding unavailable component");
                componentStack.pop();
                locationStack.pop();
                screenLocationStack.pop();
                comp = getMouseComponent();
                if (comp != null) {
                    mouseLocation = (Point)locationStack.peek();
                    mouseLocationOnScreen = (Point)screenLocationStack.peek();
                }
            }
        }
        if (Log.isClassDebugEnabled(getClass()))
            Log.debug("Current component is " + Robot.toString(comp));
        return comp;
    }

    public synchronized boolean isDragging() { return dragSource != null; }
    public synchronized Component getDragSource() { return dragSource; }
    public synchronized void setDragSource(Component c) {
        dragSource = c;
    }
    public synchronized Point getDragOrigin() {
        return new Point(dragX, dragY);
    }
    public synchronized int getClickCount() { return clickCount; }
    protected synchronized void setClickCount(int count) {
        clickCount = count;
    }
    public synchronized long getLastEventTime() { return lastEventTime; }
    protected synchronized void setLastEventTime(long t) { lastEventTime = t; }
    /** Returns all currently active modifiers. */
    public synchronized int getModifiers() { return modifiersDown; }
    protected synchronized void setModifiers(int m) { modifiersDown = m; }
    /** Returns the currently pressed key modifiers. */
    public synchronized int getKeyModifiers() {
        return modifiersDown & ~BUTTON_MASK;
    }
    public synchronized int getButtons() { return buttonsDown; }
    protected synchronized void setButtons(int b) { buttonsDown = b; }
    /** Returns the mouse location relative to the component that currently
        contains the pointer, or null if outside all components.
    */
    public synchronized Point getMouseLocation() {
        return mouseLocation != null ? new Point(mouseLocation) : null;
    }
    /** Returns the last known mouse location. */
    public synchronized Point getMouseLocationOnScreen() {
        return new Point(mouseLocationOnScreen);
    }
}

