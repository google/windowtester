package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.*;

import abbot.script.*;
import abbot.tester.WindowTracker;

/**
 * Record basic semantic events you might find on an Window. <p>
 */
public class WindowRecorder extends ContainerRecorder {

    private Window window;
    private Point where;
    private Dimension size;

    public WindowRecorder(Resolver resolver) {
        super(resolver);
    }

    protected void init(int recordingType) {
        super.init(recordingType);
        window = null;
        where = null;
        size = null;
    }

    protected boolean isWindowEvent(AWTEvent event) {
        return (event.getSource() instanceof Window
                // Checking for window ready avoids picking up
                // spurious resize events on first window show
                && ((Window)event.getSource()).isShowing() 
                && WindowTracker.getTracker().
                isWindowReady((Window)event.getSource())
                && (event.getID() == ComponentEvent.COMPONENT_MOVED
                    || event.getID() == ComponentEvent.COMPONENT_RESIZED))
            || event.getID() == WindowEvent.WINDOW_CLOSING
            || super.isWindowEvent(event);
    }

    protected boolean parseWindowEvent(AWTEvent event) {
        int id = event.getID();
        boolean consumed = true;
        if (id == ComponentEvent.COMPONENT_MOVED) {
            window = (Window)event.getSource();
            where = window.getLocationOnScreen();
            setFinished(true);
        }
        else if (id == ComponentEvent.COMPONENT_RESIZED) {
            window = (Window)event.getSource();
            size = window.getSize();
            setFinished(true);
        }
        else if (id == WindowEvent.WINDOW_CLOSING) {
            window = (Window)event.getSource();
            setFinished(true);
        }
        else {
            consumed = super.parseWindowEvent(event);
        }
        return consumed;
    }

    protected Step createStep() {
        Step step;
        if (getRecordingType() == SE_WINDOW && window != null) {
            if (where != null) {
                step = createMove(window, where);
            }
            else if (size != null) {
                step = createResize(window, size);
            }
            else {
                step = createClose(window);
            }
        }
        else {
            step = super.createStep();
        }
        return step;
    }

    protected Step createClose(Window window) {
        ComponentReference ref = getResolver().addComponent(window);
        return new Action(getResolver(),
                          null, "actionClose",
                          new String[] { ref.getID() }, Window.class);
    }

    protected Step createMove(Window window, Point where) {
        // If the window is not yet showing, ignore it
        if (!WindowTracker.getTracker().isWindowReady(window))
            return null;

        ComponentReference ref = getResolver().addComponent(window);
        return new Action(getResolver(), 
                          null, "actionMove",
                          new String[] { ref.getID(),
                                         String.valueOf(where.x),
                                         String.valueOf(where.y)
                          }, Window.class);
    }

    protected Step createResize(Window window, Dimension size) {
        // If the window is not yet showing, ignore it
        if (!window.isShowing())
            return null;

        ComponentReference ref = getResolver().addComponent(window);
        return new Action(getResolver(), 
                          null, "actionResize",
                          new String[] { ref.getID(),
                                         String.valueOf(size.width),
                                         String.valueOf(size.height),
                          },
                          Window.class);
    }
}

