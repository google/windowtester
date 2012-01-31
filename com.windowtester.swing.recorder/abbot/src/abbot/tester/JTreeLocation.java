package abbot.tester;

import java.awt.*;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;

import com.windowtester.runtime.util.StringComparator;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.parsers.TreePathParser;
import abbot.util.ExtendedComparator;

/** Provides encapsulation of a visible location on a {@link JTree}.
 *  A row index or a {@link String}ified {@link TreePath} (i.e. each
 * {@link TreePath} component is a {@link String}) may be used to indicate the
 * location.  Note that if a {@link TreePath} is used, it must be visible at
 * the time the location is used in order to be valid.
 */
// TODO: add flag for "in icon" vs in hit region
public class JTreeLocation extends ComponentLocation {
    private int row = -1;
    /** TreePath of Strings. */
    private TreePath treePath;
    private boolean inExpansion;

    /** Create an uninitialized JTreeLocation.  If queried for its
     * {@link Point} will return the default location.
     */
    public JTreeLocation() {
    }

    /** Create a JTreeLocation corresponding to the given row, by index. */
    public JTreeLocation(int row) {
        this(row, false);
    }

    /** Create a JTreeLocation corresponding to the given row, by index. */
    public JTreeLocation(int row, boolean inExpansion) {
        if (row < 0) {
            String msg = Strings.get("tester.JTree.row_not_visible",
                                     new Object[] { new Integer(row) });
            throw new LocationUnavailableException(msg);
        }
        this.row = row;
        this.inExpansion = inExpansion;
    }

    /** Create a JTreeLocation corresponding to the given TreePath.  The
     * TreePath must consist of usable String representations that can be 
     * used in later comparisons.  The default
     * &ltclassname&gt;@&lt;hashcode&gt; returned by
     * {@link Object#toString()} is not usable; if that is all that is
     * available, refer to the row number instead.
     */
    public JTreeLocation(TreePath treePath) {
        this(treePath, false);
    }

    /** Create a JTreeLocation corresponding to the given TreePath.  The
     * TreePath must consist of usable String representations that can be 
     * used in later comparisons.  The default
     * &lt;classname&gt;@&lt;hashcode&gt; format returned by
     * {@link Object#toString()} is not usable; if that is all that is
     * available, refer to the row number instead.
     */
    public JTreeLocation(TreePath treePath, boolean inExpansion) {
        this.treePath = treePath;
        this.inExpansion = inExpansion;
    }

    public JTreeLocation(Point p) {
        super(p);
    }

    public void setInExpansion(boolean in) {
        inExpansion = in;
    }
    public boolean isInExpansion() { return inExpansion; }

    /** Convert the given row to an x, y coordinate.
        @throws LocationUnavailableException if the row is not visible.
     */
    protected Point rowToPoint(JTree tree, int row) {
        TreePath path = tree.getPathForRow(row);
        if (path == null) {
            String msg = Strings.get("tester.JTree.row_not_visible",
                                     new Object[] { new Integer(row) });
            throw new LocationUnavailableException(msg);
        }
        return pathToPoint(tree, path);
    }

    /** Convert the given path to an x, y coordinate.
        @throws LocationUnavailableException if any part of the path is
        hidden. 
     */
    protected Point pathToPoint(JTree tree, TreePath path) {
        path = findMatchingPath(tree, path);
        Rectangle rect = tree.getPathBounds(path);
        if (rect == null) {
            String msg = Strings.get("tester.JTree.path_not_visible",
                                     new Object[] { path });
            throw new LocationUnavailableException(msg);
        }
        if (inExpansion) 
            // FIXME this is only an approximation; should probably get the
            // location from the UI somehow, but it's not available in the API
            // FIXME this will probably be bogus if the tree rows are really 
            // tall
            return new Point(rect.x - rect.height/2,
                             rect.y + rect.height/2);
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }

    /** Return the path represented by this JTree location.  
        @return null if the path can not be found.
    */
    public TreePath getPath(JTree tree) {
        if (treePath != null) {
            try {
                return findMatchingPath(tree, treePath);
            }
            catch(LocationUnavailableException e) {
                return null;
            }
        }
        if (row != -1) {
            return tree.getPathForRow(row);
        }
        Point where = super.getPoint(tree);
        return tree.getPathForLocation(where.x, where.y);
    }

    /** Return the row represented by this JTree location.  
        @return -1 if the row is not found.
    */
    int getRow(JTree tree) {
        if (treePath != null) 
            return tree.getRowForPath(getPath(tree));
        else if (row != -1)
            return row;
        Point where = super.getPoint(tree);
        return tree.getRowForLocation(where.x, where.y);
    }

    /** Return a concrete point for the abstract location. */
    public Point getPoint(Component c) {
        JTree tree = (JTree)c;
        if (treePath != null) {
            // convert the string-based path to a real path
            return pathToPoint(tree, treePath);
        }
        if (row != -1) {
            return rowToPoint(tree, row);
        }
        return super.getPoint(c);
    }

    public Rectangle getBounds(Component c) {
        JTree tree = (JTree)c;
        int row = getRow(tree);
        if (row == -1) {
            Point where = getPoint(c);
            return new Rectangle(where.x, where.y, 1, 1);
        }
        Rectangle rect = tree.getRowBounds(row);
        if (rect == null) {
            String msg = Strings.get("tester.JTree.row_not_visible",
                                     new Object[] { new Integer(row) });
            throw new LocationUnavailableException(msg);
        }
        if (inExpansion) {
            rect.x -= rect.height;
            rect.width = rect.height;
        }
        return rect;
    }

    public boolean equals(Object o) {
        if (o instanceof JTreeLocation) {
            JTreeLocation loc = (JTreeLocation)o;
            if (loc.inExpansion != inExpansion)
                return false;
            if (treePath != null) {
                if (treePath.getPathCount() != loc.treePath.getPathCount())
                    return false;
                for (int i=0;i < treePath.getPathCount();i++) {
                    if (treePath.getPathComponent(i) == null) {
                        if (loc.treePath.getPathComponent(i) != null)
                            return false;
                    }
                    else if (!treePath.getPathComponent(i).
                             equals(loc.treePath.getPathComponent(i))) {
                        return false;
                    }
                }
                return true;
            }
            if (row != -1)
                return row == loc.row;
        }
        return super.equals(o);
    }

    public String toString() {
        String s = inExpansion ? "+" : "";
        if (treePath != null) {
            return s + encodeValue(treePath.toString());
        }
        if (row != -1) 
            return s + encodeIndex(row);
        return super.toString();
    }

    public ComponentLocation parse(String encoded) {
        encoded = encoded.trim();
        if (encoded.startsWith("+")) {
            inExpansion = true;
            encoded = encoded.substring(1);
        }
        if (isValue(encoded)) {
            String path = parseValue(encoded);
            treePath = (TreePath)new TreePathParser().parse(path);
            return this;
        }
        else if (isIndex(encoded)) {
            row = parseIndex(encoded);
            return this;
        }
        return super.parse(encoded);
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.tree.bad_format",
                           new Object[] { encoded });
    }

    /** Return whether the given Object matches the final element of the given
        TreePath.
    */
    private static boolean matchesLastComponent(JTree tree, Object pattern,
                                                TreePath path) {
        // For matching the root node, or for other nodes you don't care about
        // whether they match
        if (pattern == null)
            return true;
        if (pattern.equals(path.getLastPathComponent()))
            return true;
        // Try a string comparison
        String objString = JTreeTester.valueToString(tree, path);
        return StringComparator.matches(objString, pattern.toString());
    }

    /** Given a {@link TreePath} of {@link String}s, return the equivalent
        {@link TreePath} for the given {@link JTree} constructed from objects
        from the tree's model.
        @throws LocationUnavailableException if no matching path is found.
    */
    public static TreePath findMatchingPath(JTree tree, TreePath path) {
        Object[] input = path.getPath();
        TreeModel model = tree.getModel();
        Object root = model.getRoot();
        // If the root is not visible and it doesn't match the first path
        // element, start the path with the invisible root.
        if (!tree.isRootVisible()
            && !matchesLastComponent(tree, input[0], new TreePath(root))) {
            Object[] tmp = new Object[input.length+1];
            System.arraycopy(input, 0, tmp, 1, input.length);
            tmp[0] = null; // null always matches root
            input = tmp;
        }
        TreePath realPath =
            findMatchingPath(tree, new TreePath(root), input);
        if (realPath != null)
            return realPath;
        String msg = Strings.get("tester.JTree.path_not_found",
                                 new Object[] { path });
        throw new LocationUnavailableException(msg);
    }

    /** Build up the given tree path with Objects from the TreeModel which
     * match the given array of Objects.
     */
    private static TreePath findMatchingPath(JTree tree,
                                             TreePath realPath,
                                             Object[] input) {
        // The given array is a tree path of objects which may or may not be
        // node objects.  Convert them to existing tree objects if necessary.
        // Return null if any of them are not found.
        Log.debug("Comparing " + realPath + " with " + input[0]
                  + " from " + java.util.Arrays.asList(input));
        TreeModel model = tree.getModel();
        if (!matchesLastComponent(tree, input[0], realPath)) {
            Log.debug("no root match");
        }
        else {
            Log.debug("node matched: " + realPath.getLastPathComponent());
            if (input.length == 1)
                return realPath;

            Object[] subs = new Object[input.length-1];
            System.arraycopy(input, 1, subs, 0, subs.length);
            Object obj = realPath.getLastPathComponent();
            int count = model.getChildCount(obj);
            Log.debug("Obj " + obj + " (" + obj.getClass() + ") has " + count);
            // Find the right child to match
            for (int i=0;i < count;i++) {
                Log.debug("checking child " + i);
                Object child = model.getChild(obj, i);
                TreePath newPath =
                    findMatchingPath(tree, realPath.pathByAddingChild(child),
                                     subs);
                if (newPath != null) {
                    return newPath;
                }
            }
            Log.debug("No child path matched");
        }
        return null;
    }
}
