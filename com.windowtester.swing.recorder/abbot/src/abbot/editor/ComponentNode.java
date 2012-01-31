package abbot.editor;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import abbot.Log;
import abbot.finder.*;
import abbot.i18n.Strings;
import abbot.tester.Robot;
import abbot.util.AWT;

/** Provides a JTree-compatible node model for displaying a given hierarchy. */
public class ComponentNode extends DefaultMutableTreeNode {

    private Hierarchy hierarchy;
    private Map map;
    private boolean loaded;

    /** Constructor for the root node of a hierarchy. */
    public ComponentNode(Hierarchy hierarchy) {
        super(null, true);
        this.hierarchy = hierarchy;
        map = new WeakHashMap();
    }

    protected ComponentNode(ComponentNode parent, Object obj) {
        super(obj, (obj == null
                    || obj instanceof Container
                    || obj instanceof MenuContainer));
        hierarchy = parent.hierarchy;
        map = parent.map;
        map.put(obj, this);
    }

    public ComponentNode(ComponentNode parent, Component comp) {
        this(parent, (Object)comp);
    }

    public ComponentNode(ComponentNode parent, MenuComponent comp) {
        this(parent, (Object)comp);
    }

    public ComponentNode(ComponentNode parent, MenuItem comp) {
        this(parent, (Object)comp);
    }

    public TreeNode getChildAt(int index) {
        load();
        return super.getChildAt(index);
    }

    public int getChildCount() {
        load();
        return super.getChildCount();
    }

    public void reload() {
        reload(hierarchy);
    }

    public void reload(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
        map.clear();
        loaded = false;
    }

    private void load() {
        if (loaded)
            return;

        loaded = true;
        removeAllChildren();
        Object obj = getUserObject();
        if(isRoot()) {
            Iterator iter = hierarchy.getRoots().iterator();
            while (iter.hasNext()) {
                add(new ComponentNode(this, (Component)iter.next()));
            }
        }
        else if(obj instanceof Container) {
            // Specially handle AWT MenuBar
            if (obj instanceof Frame) {
                Frame f = (Frame)obj;
                if (f.getMenuBar() != null) {
                    add(new ComponentNode(this, f.getMenuBar()));
                }
            }
            Collection children =
                hierarchy.getComponents(getComponent());
            Iterator iter = children.iterator();
            while (iter.hasNext()) {
                add(new ComponentNode(this, (Component)iter.next()));
            }
        }
        // Specially handle AWT menus
        else if(obj instanceof MenuBar) {
            MenuBar mb = (MenuBar)obj;
            for (int i=0;i < mb.getMenuCount();i++) {
                add(new ComponentNode(this, mb.getMenu(i)));
            }
        }
        else if(obj instanceof Menu) {
            Menu menu = (Menu)obj;
            for (int i=0;i < menu.getItemCount();i++) {
                add(new ComponentNode(this, menu.getItem(i)));
            }
        }
    }

    /** Return the component that appears as a parent in the ComponentNode
     * hierarchy.
     */
    Component getParent(Component c) {
        return hierarchy.getParent(c);
    }

    /** Returns the Component represented, or null if this is either the root
     * or a java.awt.MenuComponent.
     */
    public Component getComponent() {
        if (getUserObject() instanceof Component)
            return (Component)getUserObject();
        return null;
    }

    public int hashCode() {
        return(isRoot() ? super.hashCode() : getUserObject().hashCode());
    }

    /** Return true if the represented components are the same. */
    public boolean equals(Object other) {
        return this == other
            || ((other instanceof ComponentNode) 
                && (getUserObject() == ((ComponentNode)other).getUserObject()));
    }

    public String toString() {
        if(isRoot()) {
            return getChildCount() == 0 
                ? Strings.get("NoComponents")
                : Strings.get("AllFrames");
        }
        return Robot.toString(getUserObject());
    }

    /** Return the nearest node corresponding to the given component. 
        Behavior is undefined if the node is not reachable from the root
        node.  If the component is elided in the underlying hierarchy, returns
        the nearest parent node that is not elided.
     */
    public ComponentNode getNode(Component comp) {
        if (comp == null) {
            return (ComponentNode)getRoot();
        }
        ComponentNode node = (ComponentNode)map.get(comp);
        if (node == null) {
            Component parentComp = getParent(comp);
            ComponentNode parent = getNode(parentComp);
            if (parent == null) {
                return getNode(parentComp);
            }
            // Fall back to parent if no child matches.
            node = parent;
            for (int i=0;i < parent.getChildCount();i++) {
                ComponentNode child = (ComponentNode)parent.getChildAt(i);
                if (child.getComponent() == comp) {
                    node = child;
                    break;
                }
            }
        }
        return node;
    }

    /** Return the TreePath for the given Component, assuming it is in the
        same hierarchy as this node.  Returns as much of the ancestor path as
        is available in the hierarchy.
    */
    public TreePath getPath(Component comp) {
        ComponentNode node = getNode(comp);
        return new TreePath(node.getPath());
    }
}
