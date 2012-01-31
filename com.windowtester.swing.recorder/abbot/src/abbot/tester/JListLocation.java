package abbot.tester;

import java.awt.*;

import javax.swing.JList;

import com.windowtester.runtime.util.StringComparator;

import abbot.i18n.Strings;
import abbot.util.ExtendedComparator;

/** Provides encapsulation of the location of a row on a JList (a coordinate,
 * item index or value). 
 */

public class JListLocation extends ComponentLocation {
    private String value = null;
    private int row = -1;

    public JListLocation() { }

    public JListLocation(String value) {
        this.value = value;
    }

    public JListLocation(int row) {
        if (row < 0) {
            String msg = Strings.get("tester.JList.invalid_index",
                                     new Object[] { new Integer(row) });
            throw new LocationUnavailableException(msg);
        }
        this.row = row;
    }

    public JListLocation(Point where) {
        super(where);
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.list.bad_format",
                           new Object[] { encoded });
    }

    /** Convert the given index into a coordinate. */
    protected Point indexToPoint(JList list, int index) {
        if (index < 0 || index >= list.getModel().getSize()) {
            String msg = Strings.get("tester.JList.invalid_index", 
                                     new Object[] { new Integer(index) });
            throw new LocationUnavailableException(msg);
        }
        Rectangle rect = list.getCellBounds(index, index);
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }

    /** Find the first String match in the list and return the index. */
    private int valueToIndex(JList list, String value) {
        int size = list.getModel().getSize();
        for (int i=0;i < size;i++) {
            String str = JListTester.valueToString(list, i);
            if (StringComparator.matches(str, value)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(JList list) {
        if (value != null)
            return valueToIndex(list, value);
        if (row != -1) {
            return row;
        }
        return list.locationToIndex(super.getPoint(list));
    }

    /** Return a concrete point for the abstract location. */
    public Point getPoint(Component c) {
        JList list = (JList)c;
        if (value != null || row != -1)
            return indexToPoint(list, getIndex(list));
        return super.getPoint(list);
    }

    public Rectangle getBounds(Component c) {
        JList list = (JList)c;
        int index = getIndex(list);
        if (index == -1) {
            String msg = Strings.get("tester.JList.invalid_index", 
                                     new Object[] { new Integer(index) });
            throw new LocationUnavailableException(msg);
        }
        return list.getCellBounds(index, index);
    }

    public boolean equals(Object o) {
        if (o instanceof JListLocation) {
            JListLocation loc = (JListLocation)o;
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
        if (isIndex(encoded)) {
            row = parseIndex(encoded);
            return this;
        }
        return super.parse(encoded);
    }
}
