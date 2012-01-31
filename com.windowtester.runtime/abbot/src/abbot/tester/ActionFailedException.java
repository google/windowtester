package abbot.tester;

/** Indicates that a ComponentTester action failed to execute properly. */
public class ActionFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public ActionFailedException(String msg) { super(msg); }
}
