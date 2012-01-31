package abbot.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.net.URL;

import abbot.finder.*;
import abbot.util.*;

/** Provides a Tree view into a given Hierarchy of components.  Refreshes its
    display based on changes to the default AWT hierarchy.  
    The selection path is preserved to the extent possible across changes in
    the hierarchy.
 */
public class ComponentTree extends JTree {
    private Hierarchy hierarchy;
    private ComponentNode root;
    private HierarchyMonitor monitor;
    private DefaultTreeModel model;
    private transient boolean ignoreSelectionChanges;
    /** Hash of class name to icon. */
    private ComponentTreeIcons icons = new ComponentTreeIcons();

    /** Supports optionally suppressing selection notifications while
        the hierarchy is reloading.
    */ 
    private class SelectionModel extends DefaultTreeSelectionModel {
        private transient boolean settingSelection;
        protected void fireValueChanged(TreeSelectionEvent e) {
            if (settingSelection || ignoreSelectionChanges)
                return;
            super.fireValueChanged(e);
        }
    }

    private class HierarchyMonitor implements AWTEventListener {
        public void eventDispatched(AWTEvent ev) {
            switch(ev.getID()) {
            case WindowEvent.WINDOW_OPENED:
            case WindowEvent.WINDOW_CLOSED:
            case ComponentEvent.COMPONENT_SHOWN: {
                Component c = ((ComponentEvent)ev).getComponent();
                if (hierarchy.contains(c)) {
                    reload(hierarchy.getParent(c));
                }
                break;
            }
            case ContainerEvent.COMPONENT_ADDED:
            case ContainerEvent.COMPONENT_REMOVED: {
                ContainerEvent e = (ContainerEvent)ev;
                Component c = e.getComponent();
                Window w = AWT.getWindow(c);
                // CellRendererPanes send out these events in swarms,
                // every time they repaint a cell, so ignore them
                if (!(c instanceof CellRendererPane)
                    && hierarchy.contains(c)
                    && w != null && hierarchy.contains(w)) {
                    // TODO: delay reload on tooltip hide to allow access to
                    // the tooltip; otherwise the tooltip will go away and be
                    // removed from the tree as soon as the cursor moves to
                    // the tree to manipulate it.
                    reload(c);
                }
                break;
            }
            default:
                break;
            }
        }
    }

    private class Renderer extends DefaultTreeCellRenderer {
        public Renderer() {
            URL url = getClass().getResource("icons/component.gif");
            if (url != null) {
                setLeafIcon(new ImageIcon(url));
            }
            url = getClass().getResource("icons/container.gif");
            if (url != null) {
                setOpenIcon(new ImageIcon(url));
                setClosedIcon(new ImageIcon(url));
            }
        }
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean sel,
                                                      boolean exp,
                                                      boolean leaf,
                                                      int row,
                                                      boolean focus) {
            Component c =
                super.getTreeCellRendererComponent(tree, value,
                                                   sel, exp, leaf,
                                                   row, focus);
            if (c instanceof JLabel) {
                JLabel label = (JLabel)c;
                Icon icon = null;
                if (value == root) {
                    URL url = getClass().
                        getResource("icons/hierarchy-root.gif");
                    if (url != null)
                        icon = new ImageIcon(url);
                }
                else {
                    Component c1 = ((ComponentNode)value).getComponent();
                    if (c1 != null) {
                        icon = icons.getIcon(c1.getClass());
                    }
                }
                if (icon != null) {
                    label.setIcon(icon);
                }
            }
            return c;
        }
    }

    public ComponentTree(Hierarchy h) {
        super(new ComponentNode(h));
        setSelectionModel(new SelectionModel());
        setCellRenderer(new Renderer());
        setShowsRootHandles(true);
        setScrollsOnExpand(true);

        hierarchy = h;
        model = (DefaultTreeModel)getModel();
        root = (ComponentNode)model.getRoot();

        monitor = new HierarchyMonitor();
        long mask = ContainerEvent.CONTAINER_EVENT_MASK
            | ComponentEvent.COMPONENT_EVENT_MASK
            | WindowEvent.WINDOW_EVENT_MASK;
        new WeakAWTEventListener(monitor, mask);
    }

    public void setHierarchy(Hierarchy h) {
        hierarchy = h;
        root.reload(h);
        reload();
    }

    /** Set the current selection path, ensuring that it is visible. */
    public void setSelectionPath(TreePath path) {
        super.setSelectionPath(path);
        makeVisible(path);
        Rectangle rect = getPathBounds(path);
        if (rect != null)
            scrollRectToVisible(rect);
    }

    /** Returns the path to the given component.  If the component does not
     * exist in the current hierarchy, returns as much of its parent path as
     * does exist.  
     */
    public TreePath getPath(Component comp) {
        return root.getPath(comp);
    }

    /** Reloads the entire hierarchy. */
    public void reload() {
        reload(null);
    }

    /** Reloads the hierarchy starting at the given component. */
    public void reload(Component comp) {
        ComponentNode node = comp != null ? root.getNode(comp) : root;
        TreePath path = getSelectionPath();
        Component selected = path == null 
            ? null : ((ComponentNode)path.getLastPathComponent()).getComponent();
        if (node == null)
            node = root;

        // suppress selection change notifications until we're certain we
        // can't restore the original selection.
        ignoreSelectionChanges = true;
        node.reload();
        model.reload(node);
        if (selected != null) {
            TreePath newPath = root.getPath(selected);
            // if the selection is exactly the same as it was before the
            // reload, suppress selection change notifications
            ignoreSelectionChanges = path.equals(newPath);
            setSelectionPath(newPath);
        }
        ignoreSelectionChanges = false;
    }
}
