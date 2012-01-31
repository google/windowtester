package abbot.tester;

import java.awt.*;
import javax.swing.*;

import abbot.i18n.*;

/** Provides user actions on a JScrollPane. */
// TODO: ScrollBarLocation, which provides locations for thumb, arrows, etc
// TODO: multi-scroll when arrows pressed and held
// TODO: multi-scroll when block (outside thumb) pressed and held
public class JScrollBarTester extends JComponentTester {

    private static final int BLOCK_OFFSET = 4;

    protected Point getThumbLocation(JScrollBar bar, int value) {
        boolean horizontal = bar.getOrientation() == JScrollBar.HORIZONTAL;
        double fraction =
            (double)value / (bar.getMaximum() - bar.getMinimum());
        if (horizontal) {
            int arrow = bar.getHeight();
            return new Point(arrow
                             + (int)(fraction * (bar.getWidth() - 2*arrow)),
                             arrow / 2);
        }
        else {
            int arrow = bar.getWidth();
            return new Point(arrow / 2,
                             arrow
                             + (int)(fraction * (bar.getHeight() - 2*arrow)));
        }
    }

    protected Point getUnitLocation(JScrollBar bar, boolean up) {
        boolean horizontal = bar.getOrientation() == JScrollBar.HORIZONTAL;
        int arrow = horizontal ? bar.getHeight() : bar.getWidth();
        if (up) {
            return horizontal 
                ? new Point(bar.getWidth() - arrow/2, arrow/2)
                    : new Point(arrow/2, bar.getHeight() - arrow/2);
        }
        return new Point(arrow/2, arrow/2);
    }

    protected Point getBlockLocation(JScrollBar bar, boolean up) {
        boolean horizontal = bar.getOrientation() == JScrollBar.HORIZONTAL;
        Point p = getUnitLocation(bar, up);
        int offset = up ? BLOCK_OFFSET : -BLOCK_OFFSET;
        if (horizontal)
            p.x += offset;
        else
            p.y += offset;
        return p;
    }

    protected void scroll(final JScrollBar bar, final int value) {
        invokeLater(bar, new Runnable() {
            public void run() {
                bar.setValue(value);
            }
        });
    }

    protected void scroll(JScrollBar bar, int count, boolean block) {
        // For now, do it programmatically, faking the mouse movement and
        // clicking 
        Point where = block
            ? getBlockLocation(bar, count >= 0)
            : getUnitLocation(bar, count >= 0);
        // Don't really know if this is where the UI puts them
        // mouseClick(bar, where.x, where.y);
        mouseMove(bar, where.x, where.y);
        int value = bar.getValue() + count * (block
                                              ? bar.getBlockIncrement() 
                                              : bar.getUnitIncrement());
        scroll(bar, value);
    }

    /** Scroll up (or right) one unit (usually a line). */
    public void actionScrollUnitUp(Component c) {
        scroll((JScrollBar)c, 1, false);
        waitForIdle();
    }
    /** Scroll down (or left) one unit (usually a line). */
    public void actionScrollUnitDown(Component c) {
        scroll((JScrollBar)c, -1, false);
        waitForIdle();
    }
    /** Scroll up (or right) one block (usually a page). */
    public void actionScrollBlockUp(Component c) {
        scroll((JScrollBar)c, 1, true);
        waitForIdle();
    }
    /** Scroll down (or left) one block (usually a page). */
    public void actionScrollBlockDown(Component c) {
        scroll((JScrollBar)c, -1, true);
        waitForIdle();
    }

    /** Scroll to the given scroll position. */
    public void actionScrollTo(Component c, final int position) {
        JScrollBar bar = (JScrollBar)c;
        int min = bar.getMinimum();
        int max = bar.getMaximum();
        if (position < min || position > max) {
            String msg = Strings.get("tester.JScrollBar.out_of_range",
                                     new Object[] {
                                         new Integer(position),
                                         new Integer(min), new Integer(max),
                                     });
            throw new ActionFailedException(msg);
        }

        Point thumb = getThumbLocation(bar, bar.getValue());
        mouseMove(bar, thumb.x, thumb.y);

        thumb = getThumbLocation(bar, position);
        mouseMove(bar, thumb.x, thumb.y);

        scroll(bar, position);
        waitForIdle();
    }
}
