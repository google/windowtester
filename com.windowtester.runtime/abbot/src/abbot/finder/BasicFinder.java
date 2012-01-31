package abbot.finder;

import java.awt.Container;
import java.awt.Component;
import java.awt.Window;
import java.util.*;
import javax.swing.SwingUtilities;

import abbot.i18n.Strings;

/** Provides basic component lookup, examining each component in turn.
    Searches all components of interest in a given hierarchy.
 */

public class BasicFinder implements ComponentFinder {
    private Hierarchy hierarchy;

    private static final ComponentFinder DEFAULT =
        new BasicFinder(new AWTHierarchy());
    public static ComponentFinder getDefault() { return DEFAULT; }

    private class SingleComponentHierarchy implements Hierarchy {
        private Component root;
        private ArrayList list = new ArrayList();
        public SingleComponentHierarchy(Container root) {
            this.root = root;
            list.add(root);
        }
        public Collection getRoots() {
            return list;
        }
        public Collection getComponents(Component c) { 
            return getHierarchy().getComponents(c);
        }
        public Container getParent(Component c) {
            return getHierarchy().getParent(c);
        }
        public boolean contains(Component c) {
            return getHierarchy().contains(c)
                && SwingUtilities.isDescendingFrom(c, root);
        }
        public void dispose(Window w) { getHierarchy().dispose(w); }
    }

    public BasicFinder() {
        this(AWTHierarchy.getDefault());
    }

    public BasicFinder(Hierarchy h) {
        hierarchy = h;
    }

    protected Hierarchy getHierarchy() {
        return hierarchy;
    }

    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy under the given root is the desired
        one.
    */
    public Component find(Container root, Matcher m) 
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        Hierarchy h = root != null
            ? new SingleComponentHierarchy(root) : getHierarchy();
        return find(h, m);
    }

    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy used by this ComponentFinder is the desired
        one.
    */
    public Component find(Matcher m)
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        return find(getHierarchy(), m);
    }

    protected Component find(Hierarchy h, Matcher m)
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        Set found = new HashSet();
        Iterator iter = h.getRoots().iterator();
        while (iter.hasNext()) {
            findMatches(h, m, (Component)iter.next(), found);
        }
        if (found.size() == 0) {
            String msg = Strings.get("finder.not_found", 
                                     new Object[] { m.toString() });
            throw new ComponentNotFoundException(msg);
        }
        else if (found.size() > 1) {
            Component[] list = (Component[])
                found.toArray(new Component[found.size()]);
            if (!(m instanceof MultiMatcher)) {
                String msg = Strings.get("finder.multiple_found",
                                         new Object[] { m.toString() });
                throw new MultipleComponentsFoundException(msg, list);
            }
            return ((MultiMatcher)m).bestMatch(list);
        }
        return (Component)found.iterator().next();
    }
        
    protected void findMatches(Hierarchy h, Matcher m,
                               Component c, Set found) {
        if (found.size() == 1 && !(m instanceof MultiMatcher))
            return;

        Iterator iter = h.getComponents(c).iterator();
        while (iter.hasNext()) {
            findMatches(h, m, (Component)iter.next(), found);
        }
        if (m.matches(c)) {
            found.add(c);
        }
    }
}
