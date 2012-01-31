package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.JMenuItem;
import javax.swing.plaf.TabbedPaneUI;

import abbot.Platform;
import abbot.tester.ComponentLocation;
import abbot.tester.JTabbedPaneLocation;
import abbot.script.*;

/**
 * Record basic semantic events you might find on an JTabbedPane. <p>
 * <ul>
 * <li>Select a tab.
 * </ul>
 */
public class JTabbedPaneRecorder extends JComponentRecorder {

    private JTabbedPane tabbedPane;

    public JTabbedPaneRecorder(Resolver resolver) {
        super(resolver);
    }

    public boolean accept(AWTEvent event) {
        if (isClick(event)) {
            MouseEvent me = (MouseEvent)event;
            JTabbedPane tp = tabbedPane = (JTabbedPane)me.getComponent();
            TabbedPaneUI ui = tp.getUI();
            int index = ui.tabForCoordinate(tp, me.getX(), me.getY());
            if (index != -1) {
                setStatus("Selecting tab '" + tp.getTitleAt(index));
                init(SE_CLICK);
                return true;
            }
            if (Platform.isOSX()) {
                int xmin = tp.getWidth();
                int xmax = 0;
                int ymin = tp.getHeight();
                int ymax = 0;
                for (int i=0;i < tp.getTabCount();i++) {
                    Rectangle rect = ui.getTabBounds(tp, i);
                    if (rect.x >= 0) {
                        xmin = Math.min(rect.x, xmin);
                        xmax = Math.max(rect.x + rect.width, xmax);
                        ymin = Math.min(rect.y, ymin);
                        ymax = Math.max(rect.y + rect.height, ymax);
                    }
                }
                if (me.getX() > xmax && me.getX() < tp.getWidth() - xmin 
                    && me.getY() >= ymin && me.getY() <= ymax) {
                    // Expect a popup menu, then let popup menu recording take
                    // over. 
                    init(SE_MENU);
                    return true;
                }
            }
        }
        return super.accept(event);
    }

    /** Special case for OSX tab selection from popup menu. */
    protected Step createMenuSelection(Component menuItem) {
        ComponentReference ref = getResolver().addComponent(tabbedPane);
        ComponentLocation loc =
            new JTabbedPaneLocation(((JMenuItem)menuItem).getText());
        Step step = new Action(getResolver(),
                               null, "actionSelectTab",
                               new String[] {
                                   ref.getID(), loc.toString(),
                               });
        return step;
    }

    /** Parse clicks, notably those that select a tab. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        ComponentReference cr = getResolver().addComponent(target);
        JTabbedPane tp = (JTabbedPane)target;
        TabbedPaneUI ui = tp.getUI();
        int index = ui.tabForCoordinate(tp, x, y);
        if (index != -1) {
            // NOTE only tab selections are allowed for when clicking on tabs;
            // no multi-clicks or other buttons are saved, although nothing
            // prevents manual generation of such actions.
            return new Action(getResolver(), 
                              null, "actionSelectTab",
                              new String[] {
                                  cr.getID(),
                                  getLocationArgument(tp, x, y),
                              }, javax.swing.JTabbedPane.class);
        }
        return super.createClick(target, x, y, mods, count);
    }
}

