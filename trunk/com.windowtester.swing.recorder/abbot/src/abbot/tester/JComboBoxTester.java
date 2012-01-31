package abbot.tester;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.*;

import com.windowtester.runtime.util.StringComparator;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.util.ExtendedComparator;
import abbot.util.AWT;
import abbot.finder.*;
import abbot.finder.matchers.*;

public class JComboBoxTester extends JComponentTester {

    private JListTester listTester = new JListTester();

    /** Return an array of strings that represent the combo box list. 
     * Note that the current selection might not be included, since it's
     * possible to have a custom (edited) entry there that is not included in
     * the default contents.
     */
    public String[] getContents(JComboBox cb) {
        ArrayList list = new ArrayList();
        for (int i=0;i < cb.getItemCount();i++) {
            list.add(cb.getItemAt(i).toString());
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    public void actionSelectIndex(Component comp, final int index) {
        final JComboBox cb = (JComboBox)comp;

        // activate it, if not already showing 
        if (!cb.getUI().isPopupVisible(cb)) {
            // NOTE: if the index is out of range, the selected item will be
            // one end or the other of the list.
            if (cb.isEditable()) {
                // Location of popup button activator is LAF-dependent
                invokeAndWait(new Runnable() {
                    public void run() { 
                        cb.getUI().setPopupVisible(cb, true);
                    }
                });
            }
            else {
                actionClick(cb);
            }
        }
        JList list = findComboList(cb);
        listTester.actionSelectIndex(list, index);
    }

    /** Find the JList in the popup raised by this combo box. */
    public JList findComboList(JComboBox cb) {
        Component popup = AWT.findActivePopupMenu();
        if (popup == null) {
            long now = System.currentTimeMillis();
            while ((popup = AWT.findActivePopupMenu()) == null) {
                if (System.currentTimeMillis() - now > popupDelay)
                    throw new ActionFailedException(Strings.get("tester.JComboBox.popup_not_found"));
                sleep();
            }
        }
        
        Component comp = findJList((Container)popup);
        if (comp == null)
            throw new ActionFailedException(Strings.get("tester.JComboBox.popup_not_found"));
        return (JList)comp;
    }

    private JList findJList(Container parent) {
        try {
            ComponentFinder finder = BasicFinder.getDefault();
            return (JList)finder.find(parent, new ClassMatcher(JList.class));
        }
        catch(ComponentSearchException e) {
            return null;
        }
    }

    /** If the value looks meaningful, return it, otherwise return null. */
    public String getValueAsString(JComboBox combo,
                                   JList list,
                                   Object item, int index) {
        String value = item.toString();
        // If the value is the default Object.toString method (which
        // returns <class>@<pointer value>), try to find something better.
        if (value.startsWith(item.getClass().getName() + "@")) {
            Component c = combo.getRenderer().
                getListCellRendererComponent(list, item, index, true, true);
            if (c instanceof javax.swing.JLabel)
                return ((javax.swing.JLabel)c).getText();
            return null;
        }
        return value;
    }

    public void actionSelectItem(Component comp, String item) {
        JComboBox cb = (JComboBox)comp;
        Object obj = cb.getSelectedItem();
        if ((obj == null && item == null)
            || (obj != null
                && StringComparator.matches(obj.toString(), item)))
            return;

        for (int i=0;i < cb.getItemCount();i++) {
            obj = cb.getItemAt(i);
            Log.debug("Comparing against '" + obj + "'");
            if ((obj == null && item == null)
                || (obj != null
                    && StringComparator.matches(obj.toString(), item))){
                actionSelectIndex(comp, i);
                return;
            }
        }
        // While actions are supposed to represent real user actions, it's
        // possible that the current environment does not match sufficiently,
        // so we need to throw an appropriate exception that can be used to
        // diagnose the problem.
        String mid = "[";
        StringBuffer contents = new StringBuffer();
        for (int i=0;i < cb.getItemCount();i++) {
            contents.append(mid);
            contents.append(cb.getItemAt(i).toString());
            mid = ", ";
        }
        contents.append("]");
        throw new ActionFailedException(Strings.get("tester.JComboBox.item_not_found",
                                                    new Object[] {
                                                        item,
                                                        contents.toString()
                                                    }));
    }
}
