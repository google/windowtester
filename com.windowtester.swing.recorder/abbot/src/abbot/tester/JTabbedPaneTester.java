package abbot.tester;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;

import abbot.util.ExtendedComparator;
import abbot.tester.JTabbedPaneLocation.TabNotVisibleException;

/** Provides user actions on a JTabbedPane. */
public class JTabbedPaneTester extends JComponentTester {

    /** Return an array of strings that represent the tabs in the pane.
     */
    public String[] getTabs(Component comp) {
        JTabbedPane tp = (JTabbedPane)comp;
        int count = tp.getTabCount();
        ArrayList list = new ArrayList(count);
        for (int i=0;i < count;i++) {
            list.add(tp.getTitleAt(i));
        }
        return (String[])list.toArray(new String[count]);
    }

    public void actionSelectTab(Component comp, JTabbedPaneLocation loc) {
        Point pt;
        try {
            pt = loc.getPoint(comp);
            click(comp, pt.x, pt.y);
        }
        catch(TabNotVisibleException e) {
            // Set the tab directly
            ((JTabbedPane)comp).setSelectedIndex(e.index);
        }
        waitForIdle();
    }

    /** Parse the String representation of a JTableLocation into the actual
        JTableLocation object.
    */
    public ComponentLocation parseLocation(String encoded) {
        return new JTabbedPaneLocation().parse(encoded);
    }

    /** Return (in order of preference) the location corresponding to value,
     * cell, or coordinate.
     */
    public ComponentLocation getLocation(Component c, Point p) {
        JTabbedPane tabs = (JTabbedPane)c;
        TabbedPaneUI ui = tabs.getUI();
        int index = ui.tabForCoordinate(tabs, p.x, p.y);
        if (index != -1) {
            String name = tabs.getTitleAt(index);
            return new JTabbedPaneLocation(name);
        }
        return new JTabbedPaneLocation(p);
    }
}
