package abbot.tester;

import java.awt.*;

import javax.swing.*;

import abbot.i18n.Strings;
import abbot.script.ArgumentParser;

/** Provide actions and assertions for a {@link JList} component.
    The {@link JList} substructure is a "row", and {@link JListLocation}
    provides different identifiers for a row.
    <ul>
    <li>Select an item by index
    <li>Select an item by value (its string representation)
    </ul>
    Note that {@link JList} uses "index" and "value" in its API.  For
    convenience, the <code>JListTester</code> API also provides "row" and
    "item" as synonyms for "index". 

    @see JListLocation
 */
// TODO multi-select

// Putting "Location" into ComponentTester removes the need for any subclass
// to duplicate click, click(mods), click(mods, count), as well as make
// specific methods unnecessary.

public class JListTester extends JComponentTester {

    /** Convert the value in the list at the given index into a reasonable
        string representation, or null if one can not be obtained.
    */
    static String valueToString(JList list, int index) {
        Object value = list.getModel().getElementAt(index);
        Component cr = list.getCellRenderer().
            getListCellRendererComponent(list, value, index, false, false);
        String string = null;
        if (cr instanceof javax.swing.JLabel) {
            string = ((javax.swing.JLabel)cr).getText();
            if (string != null)
                string = string.trim();
            if (!"".equals(string)
                && !ArgumentParser.isDefaultToString(string))
                return string;
        }
        string = ArgumentParser.toString(value);
        return string == ArgumentParser.DEFAULT_TOSTRING
            ? null : string;
    }

    /** JList doesn't provide direct access to its contents, so make up for
     * that oversight.
     */
    public Object getElementAt(JList list, int index) {
        return list.getModel().getElementAt(index);
    }

    /** Return the size of the given list. */
    public int getSize(JList list) {
        return list.getModel().getSize();
    }

    /** Return an array of strings that represents the list's contents. */
    public String[] getContents(JList list) {
        ListModel model = list.getModel();
        String[] values = new String[model.getSize()];
        for (int i=0;i < values.length;i++) {
            values[i] = model.getElementAt(i).toString();
        }
        return values;
    }

    /** Select the given index.
        Equivalent to actionSelectRow(c, new JListLocation(index)).
    */
    public void actionSelectIndex(Component c, int index) {
        actionSelectRow(c, new JListLocation(index));
    }

    /** Select the first item in the list matching the given String
        representation of the item.<p>
        Equivalent to actionSelectRow(c, new JListLocation(item)).
    */
    public void actionSelectItem(Component c, String item) {
        actionSelectRow(c, new JListLocation(item));
    }

    /** Select the first value in the list matching the given String
        representation of the value.<p>
        Equivalent to actionSelectRow(c, new JListLocation(value)).
    */
    public void actionSelectValue(Component c, String value) {
        actionSelectRow(c, new JListLocation(value));
    }

    /** Select the given row.  Does nothing if the index is already
     * selected.
     */
    public void actionSelectRow(Component c, JListLocation location) {
        JList list = (JList)c;
        int index = location.getIndex(list);
        if (index < 0 || index >= list.getModel().getSize()) {
            String msg = Strings.get("tester.JList.invalid_index",
                                     new Object[] { new Integer(index) });
            throw new ActionFailedException(msg);
        }
        if (list.getSelectedIndex() != index) {
            super.actionClick(c, location);
        }
    }

    /** Parse the String representation of a JListLocation into the actual
        JListLocation object.
    */
    public ComponentLocation parseLocation(String encoded) {
        return new JListLocation().parse(encoded);
    }

    /** Return the value, row, or coordinate location. */
    public ComponentLocation getLocation(Component c, Point p) {
        JList list = (JList)c;
        int index = list.locationToIndex(p);
        String value = valueToString(list, index);
        if (value != null) {
            return new JListLocation(value);
        }
        else if (index != -1) {
            return new JListLocation(index);
        }
        return new JListLocation(p);
    }
}
