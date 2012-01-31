package abbot.editor.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.ref.*;
import java.util.*;

import abbot.Log;

/** Provide a method for consistently painting over a given component.
    This implementation uses an invisible, added componenet in order to insert
    code at the appropriate time in the painting process.  
 */
// cf CellRendererPane
public abstract class AbstractComponentDecorator {
    private Container component;
    private Renderer renderer;
    private boolean inViewport;

    /** Create a decorator for the given component. */
    public AbstractComponentDecorator(Container c) {
        if (c instanceof JViewport
            && c.getComponentCount() != 0
            && c.getComponents()[0] instanceof Container) {
            c = (Container)c.getComponents()[0];
            // Viewports only support a single child, so instead add to the
            // child, and use the viewport's graphics to paint any areas
            // outside the child.
            inViewport = true;
        }
        else if (c instanceof RootPaneContainer) {
            c = ((RootPaneContainer)c).getLayeredPane();
        }
        else if (c instanceof JRootPane) {
            c = ((JRootPane)c).getLayeredPane();
        }
        component = c;
        renderer = new Renderer();
        // FIXME never add anything to JViewport
        component.add(renderer);
        component.repaint();
        // don't repaint the viewport here, or any custom painting
        // will be wiped out.
    }

    protected Container getComponent() { return component; }

    /** Stop decorating. */
    public void dispose() {
        Runnable action = new Runnable() { 
            public void run() {
                component.remove(renderer);
                component.repaint();
                if (inViewport)
                    component.getParent().repaint();
                synchronized(AbstractComponentDecorator.this) {
                    renderer = null;
                    component = null;
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        }
        else {
            SwingUtilities.invokeLater(action);
        }
    }

    public abstract void paint(Graphics g);

    private class Renderer extends JComponent {
        public Renderer() {
            setOpaque(false);
        }
        public void invalidate() { }
        public boolean isVisible() { return true; }
        public boolean isShowing() { return true; }
        public Rectangle getBounds(Rectangle b) {
            // Pretend to have the same bounds as the component
            b = component.getBounds(b);
            b.x = 0;
            b.y = 0;
            Log.debug("bounds: " + b);
            return b;
        }
        // must use the paint method; paintComponent will not work here
        public void paint(Graphics g) {
            synchronized(AbstractComponentDecorator.this) {
                if (inViewport) {
                    Log.debug("painting viewport");
                    Graphics g2 = component.getParent().getGraphics();
                    AbstractComponentDecorator.this.paint(g2);
                }
                Log.debug("painting component");
                AbstractComponentDecorator.this.paint(g);
            }
            // NOTE: see paintComponent from CellRendererPane
            // setBounds(-width,-height,0,0) to avoid getting any input
        }
    }
}
