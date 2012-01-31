package abbot.script;

/** Exception to indicate a Component reference does not exist. */
public class NoSuchReferenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public NoSuchReferenceException() { }
    public NoSuchReferenceException(String msg) { super(msg); }
}
