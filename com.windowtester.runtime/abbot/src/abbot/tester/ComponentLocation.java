package abbot.tester;

import java.awt.*;
import java.util.StringTokenizer;

import abbot.i18n.Strings;

/** Provides encapsulation of a visible Component-relative location.    
 * "Visible" in this context means currently accessible by the pointer 
 * (possibly via scrolling).  A hidden node in a collapsed tree path 
 * would <strong>not</strong> be considered visible.
 * <p>
 * This class the specifics of location so that {@link ComponentTester} 
 * primitives
 * ({@link ComponentTester#actionClick(Component,ComponentLocation)},
 * {@link ComponentTester#actionShowPopupMenu(Component,ComponentLocation)},
 * etc) may be directed to specific elements of  
 * substructure on a <code>Component</code> (list rows, tree paths, table
 * cells, substructure values, etc.).
 * <p>
 * Classes derived from <code>ComponentLocation</code> should provide
 * constructors for each type of location indication, e.g. value, index, and
 * {@link Point}.  The {@link #toString()} method should provide an encoded
 * {@link String} suitable for use by the {@link #parse(String)} method, which
 * must convert the {@link String} encoding back into a proper
 * <code>ComponentLocation</code>. 
 * <p>
 * By convention, {@link Point} locations are specified with (x,y) notation.
 * Indexed locations should use square brackets, e.g. [i] or [r,c] and
 * value locations should use a quoted {@link String}, e.g.
 * '"cuckoo for cocoa puffs"'.  The specific syntax allowed will vary by
 * specific <code>ComponentLocation</code> type.  The base
 * <code>ComponentLocation</code> implementation
 * supports only the explicit (x,y) notation.
 * <p>
 * Recorders
 * should use the {@link String} value by default for consistency.  The
 * special value {@link #CENTER} is provided to indicate the center of a
 * {@link Component}.
 * <p>
 * The method {@link #badFormat(String)} should provide usage-like information
 * indicating the acceptable forms of input for this class.
 *
 * @see JListLocation
 * @see JTreeLocation
 * @see JTableLocation
 */
public class ComponentLocation {

    /** Special <code>ComponentLocation</code> encoding which represents
        the center of the component.
    */
    public static final String CENTER = "(center)";

    private Point where = null;

    /** Create a simple location which represents the center of a component. */
    public ComponentLocation() { }

    /** Create a simple location. */
    public ComponentLocation(Point where) {
        this.where = new Point(where);
    }

    /** Convert the abstract location into a concrete one.  Returns
     * a {@link Point} relative to the given {@link Component}.
     */
    public Point getPoint(Component c)
        throws LocationUnavailableException {
        return where != null 
            ? new Point(where) : new Point(c.getWidth()/2, c.getHeight()/2);
    }

    /** Convert the abstract location into a concrete area, relative
     * to the given <code>Component</code>.  If a point has
     * been specified, returns a 1x1 rectangle, otherwise returns the
     * a rectangle at (0, 0) of the Component's size.
     */
    public Rectangle getBounds(Component c)
        throws LocationUnavailableException {
        if (where == null)
            return new Rectangle(0, 0, c.getWidth(), c.getHeight());
        return new Rectangle(where.x, where.y, 1, 1);
    }

    /** Returns whether the given object is an equivalent 
     * <code>ComponentLocation</code>.
     */
    public boolean equals(Object o) {
        if (o instanceof ComponentLocation) {
            ComponentLocation loc = (ComponentLocation)o;
            return (where == null && loc.where == null)
                || (where != null && where.equals(loc.where));
        }
        return false;
    }

    public String toString() {
        if (where != null)
            return "(" + where.x + "," + where.y + ")";
        return CENTER;
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.component.bad_format",
                           new Object[] { encoded });
    }

    protected String encodeIndex(int index) {
        return "[" + index + "]";
    }

    /** Returns whether the given (trimmed) <code>String</code> is an encoded
        index.
    */
    protected boolean isIndex(String encoded) {
        return encoded.startsWith("[") && encoded.endsWith("]");
    }

    /** Extract the encoded index. */
    protected int parseIndex(String encoded) {
        try {
            return Integer.parseInt(encoded.
                                    substring(1, encoded.length()-1).trim());
        }
        catch(NumberFormatException e) {
            throw new IllegalArgumentException(badFormat(encoded));
        }
    }

    protected String encodeValue(String value) {
        return "\"" + value + "\"";
    }

    /** Returns whether the given (trimmed) <code>String</code> is an encoded
        value.
    */
    protected boolean isValue(String encoded) {
        return encoded.startsWith("\"") && encoded.endsWith("\"");
    }

    /** Extract the encoded value. */
    protected String parseValue(String encoded) {
        return encoded.substring(1, encoded.length()-1);
    }

    /** Convert the given encoding into the proper location.
        Allowed formats: (x, y)
        <p>
     */
    public ComponentLocation parse(String encoded) {
        encoded = encoded.trim();
        if (encoded.equals(CENTER)) {
            where = null;
            return this;
        }
        if (encoded.startsWith("(") && encoded.endsWith(")")) {
            StringTokenizer st =
                new StringTokenizer(encoded.substring(1, encoded.length()-1),
                                    ",");
            if (st.countTokens() == 2) {
                try {
                    int x = Integer.parseInt(st.nextToken().trim());
                    int y = Integer.parseInt(st.nextToken().trim());
                    where = new Point(x, y);
                    return this;
                }
                catch(NumberFormatException nfe) {
                }
            }
        }
        throw new IllegalArgumentException(badFormat(encoded));
    }
}
