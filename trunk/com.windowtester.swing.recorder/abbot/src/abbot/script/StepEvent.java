package abbot.script;

import abbot.Log;

public class StepEvent extends java.util.EventObject implements Cloneable {
    /** The step has begun executing. */
    public static final String STEP_START = "step-start";
    /** The step is N% complete. */
    public static final String STEP_PROGRESS = "step-progress";
    /** The step has finished. */
    public static final String STEP_END = "step-end";
    /** The step encountered an error. */
    public static final String STEP_ERROR = "step-error";
    /** The step failed.  This represents a test that failed to produce the
        expected results.  */
    public static final String STEP_FAILURE = "step-failure";

    /** Multi-use field.  Currently only used by STEP_PROGRESS. */
    private int id;
    /** What type of step event (start, end, etc.) */
    private String type;
    /** Error or failure, if any. */
    private Throwable throwable = null;
    
    public StepEvent(Step source, String type, int id, Throwable throwable) {
        super(source);
        Log.debug("Source is " + source);
        this.type = type;
        this.id = id;
        this.throwable = throwable;
    }

    public Object clone() {
        return new StepEvent((Step)getSource(), type, id, throwable);
    }
    public Step getStep() { return (Step)getSource(); }
    public String getType() { return type; }
    public int getID() { return id; }
    public String toString() {
        return type + ", (step " + getStep() + ")";
    }
    public Throwable getError() {
        return throwable;
    }
}
