
package abbot.swt;

import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import abbot.script.swt.WidgetReference;

/**
 * Auto-generated interface for a subset of abbot.Resolver's functionality
 */
public abstract class Resolver {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	Display display;
	
//	public abstract WidgetReference getWidgetReference(String name);
//	public abstract WidgetReference getWidgetReference(Widget widget);
	public abstract WidgetReference addWidget(Widget widget);
	public abstract void addWidgetReference(WidgetReference ref);
	public abstract String getUniqueID(WidgetReference ref);
	public abstract Collection getWidgetReferences();
	
	/** Provide temporary storage of values. */
	public abstract void setProperty(String name, String value);
	/** Provide retrieval of values from temporary storage. */
	public abstract String getProperty(String name);
	
	public Display getDisplay(){
		return display;
	}
}