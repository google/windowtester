
package abbot.finder.swt;

import java.util.*;
import org.eclipse.swt.widgets.Widget;

public class MultipleWidgetsFoundException extends WidgetSearchException {
	private static final long serialVersionUID = 1L;
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	Widget[] widgets;
	
	public MultipleWidgetsFoundException(Widget[] list) {
		widgets = list;
	}
	public MultipleWidgetsFoundException(String msg, Widget[] list) {
		super(msg);
		widgets = list;
	}
	public Widget[] getWidgets() { return widgets; }

	public Widget[] getWidgetArray() { return widgets; }

	public List getWidgetList() {
		int nWidgets = widgets.length; 
		List ret = new ArrayList(nWidgets);
		for (int i = 0; i < nWidgets; i++) {
			ret.add(widgets[i]);
		}
		return ret;
	}
}
