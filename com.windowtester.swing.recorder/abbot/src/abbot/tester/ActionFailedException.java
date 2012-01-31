package abbot.tester;

/** Indicates that a ComponentTester action failed to execute properly. */
public class ActionFailedException extends RuntimeException {
    public ActionFailedException(String msg) { super(msg); }
}
