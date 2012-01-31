package abbot.tester;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;

import abbot.Log;
import abbot.i18n.Strings;

import com.windowtester.runtime.util.StringComparator;

/** Provides encapsulation of a location on a JTabbedPane (notably a tab).
    Use the JTabbedPaneLocation#JTabbedPaneLocation(Point) ctor to indicate a
    specific coordinate. 
*/

public class JTabbedPaneLocation extends ComponentLocation {

    private String tabName = null;
    private int index = -1;
    private String tabTitles = "";

    public JTabbedPaneLocation() {
    }

    public JTabbedPaneLocation(String tabName) {
        this.tabName = tabName;
    }

    public JTabbedPaneLocation(int index) {
        if (index < 0) {
            String msg = Strings.get("tester.JTabbedPane.invalid_index", 
                                     new Object[] { new Integer(index) });
            throw new LocationUnavailableException(msg);
        }
        this.index = index;
    }

    public JTabbedPaneLocation(Point p) {
        super(p);
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.tab.bad_format",
                           new Object[] { encoded });
    }

    /** Convert the given row, col into a coordinate pair. */
    private Point indexToPoint(JTabbedPane tabs, int index) {
        if (index < 0 || index >= tabs.getTabCount()) {
            String msg = Strings.get("tester.JTabbedPane.invalid_index", 
                                     new Object[] { new Integer(index) });
            throw new LocationUnavailableException(msg);
        }
        Log.debug("converting index " + index);
        TabbedPaneUI ui = tabs.getUI();
        Rectangle rect = ui.getTabBounds(tabs, index);
        // TODO: figure out the effects of tab layout policy
        if (rect == null || rect.x < 0) {
            throw new TabNotVisibleException(index);
        }
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }

    /** Return the row, col of the first object matching the given String. */
    private int tabNameToIndex(JTabbedPane tabs, String name) {
        for (int i=0;i < tabs.getTabCount();i++) {
            String value = tabs.getTitleAt(i);
            if (StringComparator.matches(value, name))
                return i;
            else {
            	tabTitles+= "(" + value + ")";
            }
        }
        return -1;
    }

    public Point getPoint(Component c) {
        JTabbedPane tabs = (JTabbedPane)c;
        int idx = index;
        if (tabName != null) {
            if ((idx = tabNameToIndex(tabs, tabName)) == -1) {
            //	String msg = Strings.get("tester.JTabbedPane.invalid_name", 
            //        	new Object[] { tabName });
              String msg = "Tab " + tabName + " not found. Found : " + tabTitles;
            	throw new LocationUnavailableException(msg);
            }
        }
        if (idx != -1) {
            return indexToPoint(tabs, idx);
        }
        return super.getPoint(tabs);
    }

    public Rectangle getBounds(Component c) {
        JTabbedPane tabs = (JTabbedPane)c;
        TabbedPaneUI ui = tabs.getUI();
        Point p = getPoint(tabs);
        int idx = ui.tabForCoordinate(tabs, p.x, p.y);
        return idx == -1 ? null : ui.getTabBounds(tabs, idx);
    }

    // FIXME if they correspond to the same tab, are they equal?
    public boolean equals(Object o) {
        if (o instanceof JTabbedPaneLocation) {
            JTabbedPaneLocation loc = (JTabbedPaneLocation)o;
            if (tabName != null)
                return tabName.equals(loc.tabName);
            if (index != -1)
                return index == loc.index;
        }
        return super.equals(o);
    }

    public String toString() {
        if (tabName != null)
            return encodeValue(tabName);
        if (index != -1)
            return encodeIndex(index);
        return super.toString();
    }

    public ComponentLocation parse(String encoded) {
        encoded = encoded.trim();
        if (isValue(encoded)) {
            tabName = parseValue(encoded);
            return this;
        }
        if (isIndex(encoded)) {
            index = parseIndex(encoded);
            return this;
        }
        return super.parse(encoded);
    }

    /** This exception is thrown if a given tab is not currently visible.
        Some LAFs may not display all tabs concurrently.  OSX, for example, 
        puts tabs that don't fit into a popup menu.
    */
    class TabNotVisibleException extends LocationUnavailableException {
    	private static final long serialVersionUID = 1L;

        public int index;
        public TabNotVisibleException(int index) {
            super("Tab " + index + " not visible");
            this.index = index;
        }
    }
}
