package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.*;

import abbot.*;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.tester.Robot;

/**
 * The <code>Recorder</code> provides a mechanism for recording an event stream and
 * generating a sequence of script steps from that stream.
 * <p>
 * NOTE: when writing a recorder, be very careful not to test for
 * platform-specific behavior, and avoid being susceptible to
 * platform-specific bugs.  Please make sure the recorder works on both
 * pointer-focus and click-to-focus window managers, as well as on at least
 * two platforms.<p>
 */
public abstract class Recorder {
    private ActionListener al;
    private Resolver resolver;
    private long lastEventTime = 0;

    /** Create a Recorder for use in converting events into script steps. */
    public Recorder(Resolver resolver) {
        this.resolver = resolver;
    }

    /** The recorder supports zero or one listeners. */
    public void addActionListener(ActionListener al) {
        this.al = al;
    }

    protected ActionListener getListener() { return al; }

    /** Start recording a new event stream. */
    public void start() {
        lastEventTime = System.currentTimeMillis();
    }

    /** Indicate the end of the current event input stream. */
    public abstract void terminate() throws RecordingFailedException;

    public long getLastEventTime() {
        return lastEventTime;
    }

    /** Create a step or sequence of steps based on the event stream so far. */
    protected abstract Step createStep();

    /** Return a step or sequence of steps representing the steps created thus
        far, or null if none.
    */
    public Step getStep() {
        return createStep();
    }

    /** Insert an arbitrary step into the recording stream. */
    public void insertStep(Step step) {
        // Default does nothing
    }

    /** Process the given event. 
        @throws RecordingFailedException if an error was encountered and
        recording should discontinue.
    */
    public void record(java.awt.AWTEvent event)
        throws RecordingFailedException {
    
        if (Log.isClassDebugEnabled(getClass()))
            Log.debug("REC: " + Robot.toString(event));
        lastEventTime = System.currentTimeMillis();
        try {
            recordEvent(event);
        }
        catch(RecordingFailedException e) {
        	System.err.println("!!!exception: ");
        	e.printStackTrace();
            throw e;
        }
        catch(Throwable thrown) {
        	thrown.printStackTrace();
            Log.log("REC: Unexpected failure: " + thrown);
          //  String msg = Strings.get("editor.recording.exception");
          //  throw new RecordingFailedException(new BugReport(msg, thrown));
            String msg = "recording.exception";
            throw new com.windowtester.swing.recorder.RecordingFailedException(new BugReport(msg, thrown));
        }
    }

    /** Implement this to actually handle the event.
        @throws RecordingFailedException if an error was encountered and
        recording should be discontinued.
     */
    protected abstract void recordEvent(AWTEvent event)
        throws RecordingFailedException;

    /** Return the events of interest to this Recorder. */
    public long getEventMask() {
        return -1;
    }

    /** @return the {@link Resolver} to be used by this <code>Recorder</code>. */
    protected Resolver getResolver() { return resolver; }

    /** Indicate the current recording state, so that the status may be
     * displayed elsewhere.
     */
    protected void setStatus(String msg) {
        if (al != null) {
            ActionEvent event = 
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, msg);
            al.actionPerformed(event);
        }
    }
}
