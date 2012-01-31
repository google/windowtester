package abbot.finder;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Collection;

/** Provides access to all components in a hierarchy. */
public interface Hierarchy {
    /** Provides all root components in the hierarchy.  Similar to
     * Frame.getFrames().
     */
    Collection getRoots();
    /** Returns all sub-components of the given component.  What constitutes a
     * sub-component may vary depending on the Hierarchy implementation.
     */
    Collection getComponents(Component c);
    /** Return the parent component for the given Component. */
    Container getParent(Component c);
    /** Returns whether the hierarchy contains the given Component. */
    boolean contains(Component c);
    /** Provide proper disposal of the given Window, appropriate to this
     * Hierarchy.  After disposal, the Window and its descendents will no
     * longer be reachable from this Hierarchy.
     */
    void dispose(Window w);
}
