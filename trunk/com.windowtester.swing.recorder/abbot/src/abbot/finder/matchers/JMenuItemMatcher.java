package abbot.finder.matchers;

import java.awt.Component;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.windowtester.runtime.util.StringComparator;

import abbot.finder.Matcher;
import abbot.util.ExtendedComparator;

/**
 * Matches a {@link JMenuItem} given a simple label or a menu path of the
 * format "menu|submenu|menuitem", for example "File|Open|Can of worms".
 * 
 * @author twall
 * @version $Id: JMenuItemMatcher.java,v 1.2 2008-01-11 20:10:25 drubel Exp $
 */
public class JMenuItemMatcher implements Matcher {
    private String label;

    public JMenuItemMatcher(String label) {
        this.label = label;
    }

    private String getPath(JMenuItem item) {
        Component parent = item.getParent();
        if (parent instanceof JPopupMenu) {
            parent = ((JPopupMenu)parent).getInvoker();
        }
        if (parent instanceof JMenuItem) {
            return getPath((JMenuItem)parent) + "|" + item.getText();
        }
        return item.getText();
    }

    public boolean matches(Component c) {
        if (c instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem)c;
            String text = mi.getText();
            return StringComparator.matches(text, label)
                || StringComparator.matches(getPath(mi), label);
        }
        return false;
    }

}
