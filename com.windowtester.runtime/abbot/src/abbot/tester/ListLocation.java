package abbot.tester;

import java.awt.Component;
import java.awt.List;
import java.awt.Point;
import java.awt.Rectangle;

import abbot.i18n.Strings;

import com.windowtester.runtime.util.StringComparator;

/** Provides encapsulation of the location of a row on a List (a coordinate,
 * item index or value). 
 */

public class ListLocation extends ComponentLocation {
    private String value = null;
    private int row = -1;

    public ListLocation() {
    }

    public ListLocation(String value) {
        this.value = value;
    }

    public ListLocation(int row) {
        if (row < 0) {
            String msg = Strings.get("tester.JList.invalid_index",
                                     new Object[] { new Integer(row) });
            throw new LocationUnavailableException(msg);
        }
        this.row = row;
    }

    public ListLocation(Point where) {
        super(where);
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.list.bad_format",
                           new Object[] { encoded });
    }

    /** Find the first String match in the list and return the index. */
    private int valueToIndex(List list, String value) {
        int size = list.getItemCount();
        for (int i=0;i < size;i++) {
            if (StringComparator.matches(list.getItem(i), value)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(List list) {
        if (value != null)
            return valueToIndex(list, value);
        if (row != -1) {
            return row;
        }
        throw new LocationUnavailableException("Can't derive an index from a Point on java.awt.List");
    }

    /** Return a concrete point for the abstract location. */
    public Point getPoint(Component c) {
        List list = (List)c;
        if (value != null || row != -1)
            throw new LocationUnavailableException("Can't derive a Point from an index on java.awt.List");
        return super.getPoint(list);
    }

    public Rectangle getBounds(Component c) {
        throw new LocationUnavailableException("Can't determine bounds on java.awt.List");
    }

    public boolean equals(Object o) {
        if (o instanceof ListLocation) {
            ListLocation loc = (ListLocation)o;
            if (value != null)
                return value.equals(loc.value);
            if (row != -1)
                return row == loc.row;
        }
        return super.equals(o);
    }

    public String toString() {
        if (value != null)
            return encodeValue(value);
        if (row != -1)
            return encodeIndex(row);
        return super.toString();
    }

    public ComponentLocation parse(String encoded) {
        encoded = encoded.trim();
        if (isValue(encoded)) {
            value = parseValue(encoded);
            return this;
        }
        else if (isIndex(encoded)) {
            row = parseIndex(encoded);
            return this;
        }
        return super.parse(encoded);
    }
}
