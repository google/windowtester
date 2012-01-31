package abbot.tester;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.table.JTableHeader;

import abbot.i18n.Strings;

import com.windowtester.runtime.util.StringComparator;

/** Provides encapsulation of the location of a col on a JTableHeader (a coordinate,
 * item index or value). 
 */

public class JTableHeaderLocation extends ComponentLocation {
    private String value = null;
    private int col = -1;

    public JTableHeaderLocation() {
    }

    public JTableHeaderLocation(String value) {
        this.value = value;
    }

    public JTableHeaderLocation(int col) {
        if (col < 0) {
            String msg = Strings.get("tester.JTableHeader.invalid_index",
                                     new Object[] { new Integer(col) });
            throw new LocationUnavailableException(msg);
        }
        this.col = col;
    }

    public JTableHeaderLocation(Point where) {
        super(where);
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.tableheader.bad_format",
                           new Object[] { encoded });
    }

    /** Convert the given index into a coordinate. */
    protected Point indexToPoint(JTableHeader header, int index) {
        if (index < 0 || index >= header.getColumnModel().getColumnCount()) {
            String msg = Strings.get("tester.JTableHeader.invalid_index", 
                                     new Object[] { new Integer(index) });
            throw new LocationUnavailableException(msg);
        }
        Rectangle rect = header.getHeaderRect(index);
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }

    /** Find the first String match in the columns and return the index. */
    private int valueToIndex(JTableHeader header, String value) {
        int size = header.getColumnModel().getColumnCount();
        for (int i=0;i < size;i++) {
            String str = header.getTable().getModel().getColumnName(i);
            if (StringComparator.matches(str, value)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(JTableHeader header) {
        if (value != null)
            return valueToIndex(header, value);
        if (col != -1) {
            return col;
        }
        return header.columnAtPoint(super.getPoint(header));
    }

    /** Return a concrete point for the abstract location. */
    public Point getPoint(Component c) {
        JTableHeader header = (JTableHeader)c;
        if (value != null || col != -1)
            return indexToPoint(header, getIndex(header));
        return super.getPoint(header);
    }

    public Rectangle getBounds(Component c) {
        JTableHeader header = (JTableHeader)c;
        int index = getIndex(header);
        if (index == -1) {
            String msg = Strings.get("tester.JTableHeader.invalid_index", 
                                     new Object[] { new Integer(index) });
            throw new LocationUnavailableException(msg);
        }
        return header.getHeaderRect(index);
    }

    public boolean equals(Object o) {
        if (o instanceof JTableHeaderLocation) {
            JTableHeaderLocation loc = (JTableHeaderLocation)o;
            if (value != null)
                return value.equals(loc.value);
            if (col != -1)
                return col == loc.col;
        }
        return super.equals(o);
    }

    public String toString() {
        if (value != null)
            return encodeValue(value);
        if (col != -1)
            return encodeIndex(col);
        return super.toString();
    }

    public ComponentLocation parse(String encoded) {
        encoded = encoded.trim();
        if (isValue(encoded)) {
            value = parseValue(encoded);
            return this;
        }
        else if (isIndex(encoded)) {
            col = parseIndex(encoded);
            return this;
        }
        return super.parse(encoded);
    }
}
