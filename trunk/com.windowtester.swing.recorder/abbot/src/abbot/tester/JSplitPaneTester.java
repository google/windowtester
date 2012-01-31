package abbot.tester;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import abbot.i18n.*;

/** Provides user actions on a JSplitPane. */
public class JSplitPaneTester extends JComponentTester {

    /** Set the divider position proportionally. */
    public void actionMoveDivider(Component c,
                                  final double proportionalLocation) {
        JSplitPane split = (JSplitPane)c;
        int max = split.getMaximumDividerLocation();
        int position = (int)(max * proportionalLocation);
        actionMoveDividerAbsolute(split, position);
    }

    /** Set the divider position to an absolute position.  */
    public void actionMoveDividerAbsolute(Component c, final int location) {
        final JSplitPane split = (JSplitPane)c;
        int old = split.getDividerLocation();
        // Move as close as possible, then programmatically set the position
        if (split.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
            mouseMove(c, c.getWidth()/2, old);
            mousePress(InputEvent.BUTTON1_MASK);
            mouseMove(c, c.getWidth()/2, location);
            mouseRelease();
        }
        else {
            mouseMove(c, old, c.getHeight()/2);
            mousePress(InputEvent.BUTTON1_MASK);
            mouseMove(c, location, c.getHeight()/2);
            mouseRelease();
        }
        invokeAndWait(c, new Runnable() {
            public void run() {
                split.setDividerLocation(location);
            }
        });
    }
}
