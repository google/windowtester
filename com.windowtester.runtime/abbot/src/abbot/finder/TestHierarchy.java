package abbot.finder;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.SwingUtilities;

import abbot.Log;
import abbot.util.*;

/** Provide isolation of a Component hierarchy to limit consideration to only
    those Components created during the lifetime of this Hierarchy instance.
    Extant Components (and any subsequently generated subwindows) are ignored
    by default.<p>
    Implicitly auto-filters windows which are disposed (i.e. generate a
    WINDOW_CLOSED event), but also implicitly un-filters them if they should
    be shown again.  Any Window explicitly disposed with
    {@link #dispose(Window)} will be ignored permanently.<p> 
*/
public class TestHierarchy extends AWTHierarchy {

    // Map of components to ignore
    private Map filtered = new WeakHashMap();
    // Map of components implicitly filtered; these will be implicitly
    // un-filtered if they are re-shown.
    private Map transientFiltered = new WeakHashMap();

    private static boolean trackAppletConsole =
        Boolean.getBoolean("abbot.applet.track_console");
    /** Avoid GC of the weak reference. */
    private AWTEventListener listener;

    /** Create a new TestHierarchy which does not contain any UI
     * Components which might already exist.
     */ 
    public TestHierarchy() {
        this(true);
    }

    /**
	 * @return the listener
	 */
	protected AWTEventListener getListener() {
		return listener;
	}
    
    /** Create a new TestHierarchy, indicating whether extant Components
     * should be omitted from the Hierarchy.  
     */
    public TestHierarchy(boolean ignoreExisting) {
        if (ignoreExisting)
            ignoreExisting();
        // Watch for introduction of transient dialogs so we can automatically
        // filter them on dispose (WINDOW_CLOSED).  Don't do anything when the
        // component is simply hidden, since we can't tell whether it will be
        // re-used. 
        listener = new TransientWindowListener();
    }

    public boolean contains(Component c) {
        return super.contains(c) && !isFiltered(c);
    }

    /** Dispose of the given Window, but only if it currently exists within
     * the hierarchy.  It will no longer appear in this Hierarchy or be
     * reachable in a hierarchy walk. 
     */
    public void dispose(Window w) {
        if (contains(w)) {
            super.dispose(w);
            setFiltered(w, true);
        }
    }

    /** Make all currently extant components invisible to this Hierarchy,
     * without affecting their current state.
     */
    public void ignoreExisting() {
        Iterator iter = getRoots().iterator();
        while (iter.hasNext()) {
            setFiltered((Component)iter.next(), true);
        }
    }

    /** Returns all available root Windows, excluding those which have been
     * filtered.
     */
    public Collection getRoots() {
        Collection s = super.getRoots();
        s.removeAll(filtered.keySet());
        return s;
    }

    /** Returns all sub-components of the given Component, omitting those
     * which are currently filtered.
     */
    public Collection getComponents(Component c) {
        if (!isFiltered(c)) {
            Collection s = super.getComponents(c);
            // NOTE: this only removes those components which are directly
            // filtered, not necessarily those which have a filtered ancestor.
            s.removeAll(filtered.keySet());
            return s;
        }
        return EMPTY;
    }

    private boolean isWindowFiltered(Component c) {
        Window w = AWT.getWindow(c);
        return w != null && isFiltered(w);
    }

    /** Returns true if the given component will not be considered when
     * walking the hierarchy.  A Component is filtered if it has explicitly
     * been filtered via {@link #setFiltered(Component,boolean)}, or if
     * any <code>Window</code> ancestor has been filtered.
     */
    public boolean isFiltered(Component c) {
        if (c == null)
            return false;
        if ("sun.plugin.ConsoleWindow".equals(c.getClass().getName()))
            return !trackAppletConsole;
        return filtered.containsKey(c)
            || ((c instanceof Window) && isFiltered(c.getParent()))
            || (!(c instanceof Window) && isWindowFiltered(c));
    }

    /** Indicates whether the given component is to be included in the
        Hierarchy.  If the component is a Window, recursively applies the
        action to all owned Windows. 
    */
    public void setFiltered(Component c, boolean filter) {
        if (AWT.isSharedInvisibleFrame(c)) {
            Iterator iter = getComponents(c).iterator();
            while (iter.hasNext()) {
                setFiltered((Component)iter.next(), filter);
            }
        }
        else {
            if (filter) {
                filtered.put(c, Boolean.TRUE);
                transientFiltered.remove(c);
            }
            else {
                filtered.remove(c);
            }
            if (c instanceof Window) {
                Window[] owned = ((Window)c).getOwnedWindows();
                for (int i=0;i < owned.length;i++) {
                    setFiltered(owned[i], filter);
                }
            }
        }
    }

    /** Provides for automatic filtering of auto-generated Swing dialogs. */
    private class TransientWindowListener implements AWTEventListener {
        private class DisposeAction implements Runnable {
            private Window w;
            public DisposeAction(Window w) {
                this.w = w;
            }
            public void run() {
                setFiltered(w, true);
                transientFiltered.put(w, Boolean.TRUE);
                Log.debug("window " + w.getName() + " filtered");
            }
        }

        public TransientWindowListener() {
            // Add a weak listener so we don't leave a listener lingering
            // about. 
            long mask = WindowEvent.WINDOW_EVENT_MASK
                | ComponentEvent.COMPONENT_EVENT_MASK;
            new WeakAWTEventListener(this, mask);
        }

        public void eventDispatched(AWTEvent e) {
            if (e.getID() == WindowEvent.WINDOW_OPENED
                || (e.getID() == ComponentEvent.COMPONENT_SHOWN
                    && e.getSource() instanceof Window)) {
                Window w = (Window)e.getSource();
                if (transientFiltered.containsKey(w)) {
                    setFiltered(w, false);
                }
                // Catch new sub-windows of filtered windows (i.e. dialogs
                // generated by a test harness UI).
                else if (isFiltered(w.getParent())) {
                    setFiltered(w, true);
                }
            }
            else if (e.getID() == WindowEvent.WINDOW_CLOSED) {
                final Window w = (Window)e.getSource();
                // *Any* window disposal should result in the window being
                // ignored, at least until it is again displayed.
                if (!isFiltered(w)) {
                    // Filter only *after* any handlers for this event have
                    // finished.  
                    Log.debug("queueing dispose of " + w.getName());
                    SwingUtilities.invokeLater(new DisposeAction(w));
                }
            }
        }
    }
}
