package abbot.swt;

import org.eclipse.swt.widgets.Display;

import abbot.finder.ComponentSearchException;

public class MultipleDisplaysFoundException extends ComponentSearchException {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	Display[] displays;
	
	public MultipleDisplaysFoundException(Display[] list) {
		displays = list;
	}
	public MultipleDisplaysFoundException(String msg, Display[] list) {
		super(msg);
		displays = list;
	}
	public Display[] getWidgets() { return displays; }
}
