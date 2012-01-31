package abbot.editor.widgets;

import java.awt.*;

/** Provides a persistent border around a component, drawn <i>after</i> the
    component itself is drawn.
*/
public class Highlighter extends AbstractComponentDecorator {
    private static final float WIDTH = 2;
    private static final Color BASE = Color.red;
    private static final Color COLOR =
        new Color(BASE.getRed(), BASE.getGreen(), BASE.getBlue(), 64);
    public Highlighter(Component c) {
        super(c instanceof Container ? (Container)c : c.getParent());
    }
    public void paint(Graphics graphics) {
        Component c = getComponent();
        Graphics2D g = (Graphics2D)graphics;
        g.setColor(COLOR);
        g.setStroke(new BasicStroke(WIDTH));
        g.drawRect(Math.round(0 + WIDTH/2),
                   Math.round(0 + WIDTH/2),
                   Math.round(c.getWidth()-WIDTH),
                   Math.round(c.getHeight()-WIDTH));
        g.fillRect(Math.round(0 + WIDTH/2),
                   Math.round(0 + WIDTH/2),
                   Math.round(c.getWidth()-WIDTH),
                   Math.round(c.getHeight()-WIDTH));
    }
}
