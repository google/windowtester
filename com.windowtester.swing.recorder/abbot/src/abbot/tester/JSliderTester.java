package abbot.tester;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.*;
import javax.swing.text.*;

import abbot.*;
import abbot.script.*;
import abbot.i18n.Strings;

/** Provides access to all user actions on a JSlider. */
public class JSliderTester extends JComponentTester {

    private ComponentLocation valueToLocation(JSlider s, int value) {
        int range = s.getMaximum() - s.getMinimum();
        int x = s.getWidth()/2;
        int y = s.getHeight()/2;
        Insets insets = s.getInsets();
        float percent = (float)(value - s.getMinimum()) / range;
        if (s.getOrientation() == JSlider.VERTICAL) {
            int max = s.getHeight() - insets.top - insets.bottom - 1;
            y = (int)(percent * max);
            if (!s.getInverted()) {
                y = max - y;
            }
        }
        else {
            int max = s.getWidth() - insets.left - insets.right - 1;
            x = (int)(percent * max);
            if (s.getInverted()) {
                x = max - x;
            }
        }
        return new ComponentLocation(new Point(x, y));
    }

    /** Click at the maximum end of the slider. */
    public void actionIncrement(Component c) {
        JSlider s = (JSlider)c;
        actionClick(c, valueToLocation(s, s.getMaximum()));
    }

    /** Click at the minimum end of the slider. */
    public void actionDecrement(Component c) {
        JSlider s = (JSlider)c;
        actionClick(c, valueToLocation(s, s.getMinimum()));
    }

    /** Slide the knob to the requested value. */
    public void actionSlide(Component c, final int value) {
        final JSlider s = (JSlider)c;
        // can't do drag actions in AWT mode
        if (getEventMode() == EM_ROBOT) {
            actionDrag(c, valueToLocation(s, s.getValue()));
            actionDrop(c, valueToLocation(s, value));
        }
        // the drag is only approximate, so set the value directly
        invokeAndWait(new Runnable() {
            public void run() {
                s.setValue(value);
            }
        });
    }

    /** Slide the knob to its maximum. */
    public void actionSlideMaximum(Component c) {
        actionSlide(c, ((JSlider)c).getMaximum());
    }

    /** Slide the knob to its minimum. */
    public void actionSlideMinimum(Component c) {
        actionSlide(c, ((JSlider)c).getMinimum());
    }
}
