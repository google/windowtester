package abbot;


public class WaitTimedOutError extends AssertionFailedError {
	private static final long serialVersionUID = 1L;

    public WaitTimedOutError() { }
    public WaitTimedOutError(String msg) { super(msg); }
}
