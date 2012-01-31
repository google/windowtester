package abbot.tester.swt;

/**
 * @author Kevin T Dale
 *
 * Used during development of abbot.swt.* to indicate when code is 
 * reached that is dependent on AWT in an undesirable way.  This 
 * includes use of:
 * 			- AWTEvent (when event_model==EM_AWT)
 */
public class AWTDependentCodeException  extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	public AWTDependentCodeException(String msg) { super(msg); }
	public AWTDependentCodeException(){}
}

