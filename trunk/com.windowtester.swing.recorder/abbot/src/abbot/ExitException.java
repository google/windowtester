package abbot;

/** Provide a tagging interface and storage for attempted exits from code
    under test.
*/
public class ExitException extends SecurityException {
    private int status;
    public ExitException(String msg, int status) {
        super(msg + " (" + status + ") on " + Thread.currentThread());
        this.status = status;
        Log.log("Exit exception created at "
                + Log.getStack(Log.FULL_STACK, this));
    }
    public int getStatus() {
        return status;
    }
}
