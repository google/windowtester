package abbot.tester.swt;

/** Indicates that a ComponentTester action failed due to the component not
 * being visible on screen.
 */
public class WidgetNotShowingException extends ActionFailedException {
	private static final long serialVersionUID = 1L;

	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	public WidgetNotShowingException() { }
    public WidgetNotShowingException(String msg) { super(msg); }
}
