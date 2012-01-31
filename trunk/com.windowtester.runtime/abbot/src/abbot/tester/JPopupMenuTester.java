package abbot.tester;

import java.awt.Component;

import javax.swing.*;

/** Provides access to JPopupMenu contents.  No special user actions. */
public class JPopupMenuTester extends JComponentTester {

    /** Return an identifying tag for the popup menu. */
    public String deriveTag(Component comp) {
        Component invoker = ((JPopupMenu)comp).getInvoker();
        return invoker == null ? "Popup menu"
            : "Popup on " + getTag(invoker);
    }

    /** Return the contents of the popup menu as a String array. */
    public String[] getMenuLabels(Component comp) {
        JPopupMenu menu = (JPopupMenu)comp;
        MenuElement[] els = menu.getSubElements();
        String[] result = new String[els.length];
        for (int i=0;i < els.length;i++) {
            Component mi = els[i].getComponent();
            if (mi instanceof JMenuItem) {
                result[i] = ((JMenuItem)mi).getText();
            }
            else {
                result[i] = "-";
            }
        }
        return result;
    }
}
