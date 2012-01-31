package abbot.finder;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import abbot.Log;
import abbot.finder.matchers.*;
import abbot.ExitException;
import abbot.tester.WindowTracker;
import abbot.tester.Robot;
import abbot.util.AWT;
import abbot.util.Bugs;

/** Provides access to the current AWT hierarchy. */
public class AWTHierarchy implements Hierarchy {
    protected static final WindowTracker tracker = WindowTracker.getTracker();
    protected static final Collection EMPTY = new ArrayList();

    private static Hierarchy defaultHierarchy = null;
    /** Obtain a default Hierarchy.  This method is provided only to support
     * the deprecated <code>ComponentTester.assertFrameShowing()</code> method.
     */
    public static Hierarchy getDefault() {
        /*System.out.println("Using default Hierarchy: "
          + Log.getStack(Log.FULL_STACK));*/
        return defaultHierarchy != null
            ? defaultHierarchy : new AWTHierarchy();
    }
    /** Set the default Hierarchy. This method is provided only to support
     * the deprecated <code>ComponentTester.assertFrameShowing()</code> method.
     */
    public static void setDefault(Hierarchy h) {
        defaultHierarchy = h;
    }

    /** Returns whether the given component is reachable from any of the root
     * windows.  The default is to consider all components to be contained in
     * the hierarchy, whether they are reachable or not (NOTE: isReachable is
     * a distinctly different operation).
     */
    public boolean contains(Component c) {
        return true;
    }

    /** Properly dispose of the given Window, making it and its native
     * resources available for garbage collection.
     */
    public void dispose(final Window w) {
        if (AWT.isAppletViewerFrame(w)) {
            // Don't dispose, it must quit on its own
            return;
        }

        Log.debug("Dispose " + w);
        Window[] owned = w.getOwnedWindows();

        for (int i=0;i < owned.length;i++) {
            // Window.dispose is recursive; make Hierarchy.dispose recursive
            // as well.
            dispose(owned[i]);
        }

        if (AWT.isSharedInvisibleFrame(w)) {
            // Don't dispose, or any child windows which may be currently
            // ignored (but not hidden) will be hidden and disposed.
            return;
        }

        // Ensure the dispose is done on the swing thread so we can catch any
        // exceptions.  If Window.dispose is called from a non-Swing thread,
        // it will invokes the dispose action on the Swing thread but in that
        // case we have no control over exceptions.
        Runnable action = new Runnable() {
            public void run() {
                try {
                    // Distinguish between the abbot framework disposing a
                    // window and anyone else doing so.
                    System.setProperty("abbot.finder.disposal", "true");
                    w.dispose();
                    System.setProperty("abbot.finder.disposal", "false");
                }
                catch(NullPointerException npe) {
                    // Catch bug in AWT 1.3.1 when generating hierarchy
                    // events 
                    Log.log(npe);
                }
                catch(ExitException e) {
                    // Some apps might call System.exit on WINDOW_CLOSED
                    Log.log("Ignoring SUT exit: " + e);
                }
                catch(Throwable e) {
                    // Don't allow other exceptions to interfere with
                    // disposal.
                    Log.warn(e);
                    Log.warn("An exception was thrown when disposing "
                             + " the window " + Robot.toString(w)
                             + ".  The exception is ignored");
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        }
        else {
            try { SwingUtilities.invokeAndWait(action); }
            catch(Exception e) { }
        }
    }

    /** Return all root components in the current AWT hierarchy. */
    public Collection getRoots() {
        return tracker.getRootWindows();
    }

    /** Return all descendents of interest of the given Component.
        This includes owned windows for Windows, children for Containers.
     */
    public Collection getComponents(Component c) {
        if (c instanceof Container) {
            Container cont = (Container)c;
            ArrayList list = new ArrayList();
            list.addAll(Arrays.asList(cont.getComponents()));
            // Add other components which are not explicitly children, but
            // that are conceptually descendents 
            if (c instanceof JMenu) {
                list.add(((JMenu)c).getPopupMenu());
            }
            else if (c instanceof Window) {
                list.addAll(Arrays.asList(((Window)c).getOwnedWindows()));
            }
            else if (c instanceof JDesktopPane) {
                // Add iconified frames, which are otherwise unreachable.
                // For consistency, they are still considerered children of
                // the desktop pane.
                list.addAll(findInternalFramesFromIcons(cont));
            }
            return list;
        }
        return EMPTY;
    }

    private Collection findInternalFramesFromIcons(Container cont) {
        ArrayList list = new ArrayList();
        int count = cont.getComponentCount();
        for (int i=0;i < count;i++) {
            Component child = cont.getComponent(i);
            if (child instanceof JInternalFrame.JDesktopIcon) {
                JInternalFrame frame =
                    ((JInternalFrame.JDesktopIcon)child).
                    getInternalFrame();
                if (frame != null)
                    list.add(frame);
            }
            // OSX puts icons into a dock; handle icon manager situations here
            else if (child instanceof Container) {
                list.addAll(findInternalFramesFromIcons((Container)child));
            }
        }                            
        return list;
    }

    public Container getParent(Component c) {
        Container p = c.getParent();
        if (p == null && c instanceof JInternalFrame) {
            // workaround for bug in JInternalFrame: COMPONENT_HIDDEN is sent
            // before the desktop icon is set, so
            // JInternalFrame.getDesktopPane will throw a NPE if called while
            // dispatching that event.  Reported against 1.4.x.
            JInternalFrame.JDesktopIcon icon =
                ((JInternalFrame)c).getDesktopIcon();
            if (icon != null) {
                p = icon.getDesktopPane();
            }
            // p = ((JInternalFrame)c).getDesktopPane();
        }
        return p;
    }
}
