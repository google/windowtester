
package abbot.swt;

import java.util.Collection;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Widget;

import abbot.Log;
import abbot.script.swt.WidgetReference;

/**
 * A simple implementation of the Resolver interface.
 */
public class SimpleResolver extends Resolver {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	private HashMap refs = new HashMap();
	private WeakHashMap widgets = new WeakHashMap();
	private HashMap properties = new HashMap();
	
	public WidgetReference getWidgetReference(String name) {
		synchronized(refs) {
			Log.debug("Looking for reference with id '" + name + "' among "
					  + refs.size() + " references");
			return (WidgetReference)refs.get(name);
		}
	}

//	public WidgetReference getWidgetReference(Widget widget) {
//		synchronized(refs) {
//			WidgetReference match = 
//				(WidgetReference)widgets.get(widget);
//			if (match == null) {
//				/* want to get a widget reference from a given widget */
//					DefaultWidgetFinder.getFinder().matchWidget(widget,
//												   refs.values().iterator(),
//												   WidgetFinder.MATCH_EXACT);
//				if (match != null) {
//					Log.debug("Cacheing component match");
//					widgets.put(widget, match);
//				}
//			}
//			return match;
//		}
//	}

	public WidgetReference addWidget(Widget widget) {
		// See if we've already got it
		synchronized(refs) {
			WidgetReference ref =
				getWidgetReference(widget);
				/* references are being used here to track widgets */
			if (ref == null) {
				// Nope, create a new one
				/*Log.debug("Component " + ComponentTester.toString(comp)
				  + " not yet referenced, adding it");*/
				if(widget==null){
					System.out.print("");
				}
				ref = new WidgetReference(this, widget);
				refs.put(ref.getID(), ref);
				widgets.put(widget, ref);
				if (ref.getID().equals("")) {
					Log.warn("Reference id is empty: " + ref);
				}
			}
			else {
				/*Log.debug("Component " + ComponentTester.toString(comp)
				  + " already referenced, using " + ref);*/
			}
			return ref;
		}
	}

	public void addWidgetReference(WidgetReference ref) {
		synchronized(refs) {
			 refs.put(ref.getID(), ref);
		 }
	}

	public String getUniqueID(WidgetReference ref) {
		// Use the component's name, if available
		 String id = ref.getID();
		 String cname = ref.getRefClassName();
		 cname = cname.substring(cname.lastIndexOf(".") + 1);
		 if (id == null) {
			 // Don't ever use an empty string for the ID
			 if (ref.getName() != null
				 && !"".equals(ref.getName())) {
				 id = ref.getName();
			 }
			 else if (ref.getTag() != null
					  && !"".equals(ref.getTag())) {
				 id = ref.getTag();
			 }
			 else {
				 id = cname + " Instance";
			 }
		 }

		 String ext = "";
		 int count = 1;
		 while (refs.get(id + ext) != null) {
			 ext = " " + count++;
		 }
		 return id + ext;

	}

	public Collection getWidgetReferences() {
		synchronized(refs) {
			return ((HashMap)refs.clone()).values();
		}
	}
	
	public String getProperty(String name) {
		return (String)properties.get(name);
	}

	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

}
