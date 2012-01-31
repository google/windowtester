package abbot.tester;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.lang.reflect.Field;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import abbot.Log;

/**
 * @author twall@users.sf.net
 */
public class JToolBarTester extends JComponentTester {
    /** @return whether the bar is currently floating. */
    public boolean isFloating(JToolBar bar) {
        ToolBarUI ui = bar.getUI();
        return ui instanceof BasicToolBarUI
            && ((BasicToolBarUI)ui).isFloating();
    }
    /** Drag the tool bar to the given location, causing it to float. 
     * @throws ActionFailedException if the toolbar is not floatable 
     */
    public void actionFloat(Component c, int x, int y) {
        JToolBar bar = (JToolBar)c;
        if (!bar.isFloatable()) {
            throw new ActionFailedException("The JToolBar is not floatable");
        }
        if (isFloating(bar)) {
            throw new ActionFailedException("The JToolBar is already floating");
        }
        Window w = SwingUtilities.getWindowAncestor(c);
        // NOTE: should x, y be the window coordinates or where the
        // cursor moves when the jtoolbar is dropped?
        JToolBarLocation loc = new JToolBarLocation();
        actionDrag(c, loc);
        actionDrop(w, x - w.getX(), y - w.getY());
        if (!isFloating(bar))
            throw new ActionFailedException("Bar not floated");
    }
    
    /** Make the given {@link JToolBar} float. */
    public void actionFloat(Component c) {
        Window w = SwingUtilities.getWindowAncestor(c);
        Point where = w.getLocation();
        actionFloat(c, where.x, where.y);
    }
    
    /** Drop the {@link JToolBar} to the requested constraint position, 
     * which must be one of the constants {@link BorderLayout#NORTH NORTH}, 
     * {@link BorderLayout#EAST EAST}, {@link BorderLayout#SOUTH SOUTH},
     * or {@link BorderLayout#WEST WEST}. 
     */
    public void actionUnfloat(Component c, String constraint) {
        if (!BorderLayout.NORTH.equals(constraint)
            && !BorderLayout.EAST.equals(constraint)
            && !BorderLayout.SOUTH.equals(constraint)
            && !BorderLayout.WEST.equals(constraint)) {
            throw new IllegalArgumentException("Invalid drop location");
        }
        JToolBar bar = (JToolBar)c;
        Container dock = null;
        if (bar.getUI() instanceof BasicToolBarUI) {
            try {
                Field f = BasicToolBarUI.class.
                    getDeclaredField("dockingSource");
                f.setAccessible(true);
                dock = (Container)f.get(bar.getUI());
            }
            catch(Exception e) {
                Log.warn(e);
            }
        }
        if (dock == null) {
            throw new ActionFailedException("Can't determine dock");
        }
        actionDrag(bar, new JToolBarLocation());
        actionDrop(dock, new DockLocation(bar, constraint));
        if (isFloating(bar))
            throw new ActionFailedException("Failed to dock the tool bar ("
                                            + constraint + ")");
    }
    
    /** The only interesting location is where you grab the JToolBar. */
    private class JToolBarLocation extends ComponentLocation {
        public Point getPoint(Component c) {
            JToolBar bar = (JToolBar)c;
            Insets insets = bar.getInsets();
            int x, y;
            if (Math.max(Math.max(Math.max(insets.left, insets.top), 
                                  insets.right), insets.bottom) == insets.left){
                x = insets.left/2;
                y = c.getHeight()/2;
            }
            else if (Math.max(Math.max(insets.top, insets.right), 
                              insets.bottom) == insets.top) {
                x = c.getWidth()/2;
                y = insets.top/2;
            }
            else if (Math.max(insets.right, insets.bottom) == insets.right) {
                x = c.getWidth() - insets.right/2;
                y = c.getHeight()/2;
            }
            else {
                x = c.getWidth()/2;
                y = c.getHeight() - insets.bottom/2;
            }
            return new Point(x, y);
        }
    }
    private class DockLocation extends ComponentLocation {
        private String constraint;
        private JToolBar bar;
        public DockLocation(JToolBar bar, String constraint) {
            if (!BorderLayout.NORTH.equals(constraint)
                && !BorderLayout.EAST.equals(constraint)
                && !BorderLayout.SOUTH.equals(constraint)
                && !BorderLayout.WEST.equals(constraint)) {
                throw new IllegalArgumentException("Invalid dock location");
            }
            this.constraint = constraint;
            this.bar = bar;
        }
        public Point getPoint(Component c) {
            if (!(c instanceof Container)) {
                throw new LocationUnavailableException("Dock is not a container");
            }
            Container dock = (Container)c;
            int x, y;
            Insets insets = dock.getInsets();
            // BasicToolBarUI prioritizes location N/E/W/S by proximity
            // to the respective border.  Close to top border is N, even
            // if close to the left or right border.
            int offset = bar.getOrientation() == SwingConstants.HORIZONTAL
                ? bar.getHeight() : bar.getWidth();
            if (BorderLayout.NORTH.equals(constraint)) {
                x = dock.getWidth()/2;
                y = insets.top;
            }
            else if (BorderLayout.EAST.equals(constraint)) {
                x = dock.getWidth() - insets.right - 1;
                y = dock.getHeight()/2;
                // Make sure we don't get mistaken for NORTH
                if (y < insets.top + offset) {
                    y = insets.top + offset;
                }
            }
            else if (BorderLayout.WEST.equals(constraint)) {
                x = insets.left;
                y = dock.getHeight()/2;
                // Make sure we don't get mistaken for NORTH
                if (y < insets.top + offset) {
                    y = insets.top + offset;
                }
            }
            else {
                x = dock.getWidth()/2;
                y = dock.getHeight() - insets.bottom - 1;
                // Make sure we don't get mistaken for EAST or WEST
                if (x < insets.left + offset) {
                    x = insets.left + offset;
                }
                else if (x > dock.getWidth() - insets.right - offset - 1) {
                    x = dock.getWidth() - insets.right - offset - 1;
                }
            }
            return new Point(x, y);
        }
    }
    
    /** Close a floating toolbar, making it go back to its
     * original container in its last known location.
     * @param c the JToolBar instance
     */
    public void actionUnfloat(Component c) {
        Window w = SwingUtilities.getWindowAncestor(c);
        close(w);
        waitForIdle();
    }
}
