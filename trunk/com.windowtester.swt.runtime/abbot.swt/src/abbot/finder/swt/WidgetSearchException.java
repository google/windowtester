package abbot.finder.swt;

/** General exception class which encapsulates all failures generated
 * attempting to find a widget in the currently available GUI.
 */
public class WidgetSearchException extends Exception {
	private static final long serialVersionUID = 1L;

    public WidgetSearchException() { }
    public WidgetSearchException(String msg) { super(msg); }
}
