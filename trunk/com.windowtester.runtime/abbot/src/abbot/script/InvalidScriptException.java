package abbot.script;

import org.jdom.Element;

/** Exception to indicate the script being parsed is invalid. */
public class InvalidScriptException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public InvalidScriptException(String msg) {
        super(msg);
    }
    public InvalidScriptException(String msg, Element el) {
        super(msg + " (when parsing " + el.toString() + ")");
    }
}
