
package abbot.swt;

/**
 * Thrown when WidgetLocator is requested to find the coordinates of an
 * unfindable Widget.
 */
public class UnableToLocateWidgetException extends Exception{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	public UnableToLocateWidgetException() { }
	public UnableToLocateWidgetException(String msg) { super(msg); }
}
