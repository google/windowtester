package abbot;


public class WaitTimedOutError extends AssertionFailedError {
    public WaitTimedOutError() { }
    public WaitTimedOutError(String msg) { super(msg); }
}
