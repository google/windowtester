package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.WindowEvent;

import abbot.Log;
import abbot.script.*;

/**
 * Record basic semantic events you might find on an Window. <p>
 */
public class FrameRecorder extends WindowRecorder {

    private Frame frame;
    private int newState;

    public FrameRecorder(Resolver resolver) {
        super(resolver);
    }

    private int WINDOW_STATE_CHANGED = 9 + WindowEvent.WINDOW_FIRST;

    protected synchronized void init(int recordingType) {
        super.init(recordingType);
        frame = null;
    }

    /** Additionally handle state change events (1.4 and later). */
    protected boolean isWindowEvent(AWTEvent event) {
        return ((event.getSource() instanceof Frame)
                && event.getID() == WINDOW_STATE_CHANGED)
            || super.isWindowEvent(event);
    }

    protected boolean parseWindowEvent(AWTEvent event) {
        int id = event.getID();
        boolean consumed = true;
        if (id == WINDOW_STATE_CHANGED) {
            frame = (Frame)event.getSource();
            newState = getExtendedState(frame);
            setFinished(true);
        }
        else {
            consumed = super.parseWindowEvent(event);
        }
        return consumed;
    }

    protected Step createStep() {
        if (getRecordingType() == SE_WINDOW && frame != null) {
            return createFrameStateChange(frame, newState);
        }
        return super.createStep();
    }

    protected Step createFrameStateChange(Frame frame, int newState) {
        ComponentReference ref = getResolver().addComponent(frame);
        return new Action(getResolver(), null, 
                          newState == Frame.NORMAL
                          ? "actionNormalize" : "actionMaximize",
                          new String[] { ref.getID() }, Frame.class);
    }

    protected Step createResize(Window window, Dimension size) {
        Step step = null;
        if (((Frame)window).isResizable()) {
            ComponentReference ref = getResolver().addComponent(window);
            step = new Action(getResolver(), 
                              null, "actionResize",
                              new String[] { ref.getID(),
                                             String.valueOf(size.width),
                                             String.valueOf(size.height),
                              }, Frame.class);
        }
        return step;
    }

    protected int getExtendedState(Frame frame) {
        try {
            Integer state = (Integer)
                Frame.class.getMethod("getExtendedState", new Class[] { }).
                invoke(frame, new Object[] { });
            Log.debug("State is " + state);
            return state.intValue();
        }
        catch(Exception e) {
            return frame.getState();
        }
    }

}

