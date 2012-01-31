package abbot.tester;

import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class JInternalFrameTester extends JComponentTester {

    private class VetoFailure {
        public PropertyVetoException e = null;
    }

    /** Maximize or normalize the given frame.  Iconified frames should use
     * deiconify, not normalize.
     */
    protected void maximize(final JInternalFrame frame, final boolean b) {

        if (b && !frame.isMaximizable())
            throw new ActionFailedException("The given JInternalFrame ("
                                            + toString(frame) + ") is not "
                                            + "maximizable");
        if (!b && frame.isIcon())
            throw new ActionFailedException("The given JInternalFrame ("
                                            + toString(frame) + ") is "
                                            + "iconified and must be "
                                            + "deiconified before it can "
                                            + "be normalized");

        Container clickTarget = frame;
        if (frame.isIcon()) {
            clickTarget = frame.getDesktopIcon();
        }
        Point p = getMaximizeLocation(clickTarget);
        mouseMove(clickTarget, p.x, p.y);
        if (frame.isIcon()) {
            iconify(frame, false);
        }
        final VetoFailure veto = new VetoFailure();
        invokeAndWait(new Runnable() {
            public void run() {
                try {
                    frame.setMaximum(b);
                }
                catch(PropertyVetoException e) {
                    veto.e = e;
                }
            }
        });
        if (veto.e != null) {
            throw new ActionFailedException("Maximize of "
                                            + Robot.toString(frame)
                                            + " was vetoed ("
                                            + veto.e + ")");
        }
    }

    public void actionMaximize(Component comp) {
        maximize((JInternalFrame)comp, true);
    }

    public void actionNormalize(Component comp) {
        maximize((JInternalFrame)comp, false);
    }

    /** Iconify/deiconify the given frame.  If the frame is already in the
     * desired state, does nothing.
     */
    protected void iconify(final JInternalFrame frame, final boolean b) {
        if ((b && frame.isIcon())
            || (!b && !frame.isIcon()))
            return;

        if (b) {
            if (!frame.isIconifiable())
                throw new ActionFailedException("The given JInternalFrame ("
                                                + toString(frame) + ") is not "
                                                + "iconifiable");
            Point p = getIconifyLocation(frame);
            mouseMove(frame, p.x, p.y);
        }
        else {
            Container c = frame.getDesktopIcon();
            Point p = getIconifyLocation(c);
            mouseMove(c, p.x, p.y);
        }
        final VetoFailure veto = new VetoFailure();
        invokeAndWait(new Runnable() {
            public void run() {
                try {
                    frame.setIcon(b);
                }
                catch(PropertyVetoException e) {
                    veto.e = e;
                }
            }
        });
        if (veto.e != null) {
            throw new ActionFailedException("Iconify of "
                                            + Robot.toString(frame)
                                            + " was vetoed ("
                                            + veto.e + ")");
        }
    }

    /** Iconify the given Frame. */
    public void actionIconify(Component comp) {
        iconify((JInternalFrame)comp, true);
    }

    /** Deiconify the given Frame. */
    public void actionDeiconify(Component comp) {
        iconify((JInternalFrame)comp, false);
    }

    /** Move the given internal frame. */
    public void actionMove(Component comp, int x, int y) {
        move((JInternalFrame)comp, x, y);
        waitForIdle();
    }

    /** Resize the given internal frame. */
    public void actionResize(Component comp, int width, int height) {
        resize((JInternalFrame)comp, width, height);
        waitForIdle();
    }

    /** Close the internal frame. */
    public void actionClose(Component comp) {
        // This is LAF-specific, so it must be done programmatically.
        final JInternalFrame frame = (JInternalFrame)comp;
        if (!frame.isClosable()) 
            throw new ActionFailedException("The given JInternalFrame ("
                                            + toString(frame) + ") is not "
                                            + "closable");
        Point p = getCloseLocation(frame);
        mouseMove(frame, p.x, p.y);
        /*
        final InternalFrameEvent ife =
            new InternalFrameEvent(frame, InternalFrameEvent.
                                   INTERNAL_FRAME_CLOSING);
        */
        // cf. BasicInternalFrameTitlePane#postClosingEvent handling of
        // close 
        // Seems to be a bug in the handling of internal frame events; they're
        // not normally posted to the AWT event queue.
        invokeAndWait(new Runnable() {
            public void run() {
                frame.doDefaultCloseAction();
            }
        });
    }
}

