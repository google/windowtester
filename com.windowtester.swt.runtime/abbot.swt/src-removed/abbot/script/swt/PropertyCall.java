package abbot.script.swt;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.eclipse.swt.widgets.Widget;

import abbot.script.NoSuchReferenceException;
import abbot.swt.Resolver;
import abbot.tester.swt.WidgetTester;

/** Provide select non-static method access in addition to standard Call
 * capabilities.  Specifically, allows specification of a WidgetReference
 * to be used as the method invocation target.  If a WidgetReference is
 * given, then the class of the widget reference is used as the target
 * class.<p>
 * This isn't a great name (and since it's a meta-class, it's easily
 * changeable) but right now I can't think of one.
 */

public abstract class PropertyCall extends Call {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    private String widgetID = null;

    /** Create a PropertyCall based on loaded XML attributes. */
    public PropertyCall(Resolver resolver, HashMap attributes) {
        super(resolver, patchAttributes(resolver, attributes));
        widgetID = (String)attributes.get(TAG_COMPONENT);
    }

    /** Create a PropertyCall based on a static invocation. */
    public PropertyCall(Resolver resolver, String description,
                        String className, String methodName, String[] args) {
        super(resolver, description, className, methodName, args);
        widgetID = null;
    }

    /** Create a PropertyCall with a widget target. */
    public PropertyCall(Resolver resolver, String description,
                        String methodName, String[] args, String id) {
        super(resolver, description, getRefClass(resolver, id),
              methodName, args);
        widgetID = id;
    }

    /** Return the widget reference ID used by this method invocation. */
    public String getWidgetID() {
        return widgetID;
    }

    /** Set the widget reference ID used by method invocation.  The class
     * of the widget referenced by the widget reference will replace the 
     * current target class.
     */
    public void setWidgetID(String id) {
        if (id == null) {
            widgetID = null;
        }
        else {
            WidgetReference ref = getResolver().getWidgetReference(id);
            if (ref != null) {
                widgetID = id;
                setTargetClassName(ref.getRefClassName());
            }
            else
                throw new NoSuchReferenceException(id);
        }
    }

    /** Save attributes specific to this Step class. */
    public HashMap getAttributes() {
        HashMap map = super.getAttributes();
        if (widgetID != null) {
            map.remove(TAG_CLASS);
            map.put(TAG_COMPONENT, widgetID);
        }
        return map;
    }

    /** Return the target of the method invocation. */
    protected Object getTarget() throws Throwable {
        if (widgetID != null) {
            return ArgumentParser.eval(getResolver(), widgetID,
                                       Widget.class);
        }
        return super.getTarget();
    }

    /** Insert default values if necessary. */
    private static HashMap patchAttributes(Resolver resolver, HashMap map) {
        String id = (String)map.get(TAG_COMPONENT);
        if (id != null) {
            map.put(TAG_CLASS, getRefClass(resolver, id));
        }
        return map;
    }

    private final static String[] prefixes = { "is", "get", "has" };
    private final static Class[] returnTypes = {
        boolean.class, null, boolean.class
    };

    /** Is the given method a property accessor?  In addition to standard
     * is/get/has property accessors, this includes pseudo-property methods on
     * WidgetTester objects.
     */
    public static boolean isPropertyMethod(Method m) {
        String name = m.getName();
        Class rt = m.getReturnType();
        Class[] params = m.getParameterTypes();
        Class dc = m.getDeclaringClass();
        for (int i=0;i < prefixes.length;i++) {
            if (name.startsWith(prefixes[i])
                && name.length() > prefixes[i].length()
                && Character.isUpperCase(name.charAt(prefixes[i].length()))
                && ((WidgetTester.class.isAssignableFrom(dc)
                     && params.length == 1
                     && Widget.class.isAssignableFrom(params[0]))
                    || (Widget.class.isAssignableFrom(dc)
                        && params.length == 0))
                && (returnTypes[i] == null 
                    || returnTypes[i].equals(rt))) {
                return true;
            }
        }
        return false;
    }

    protected String getDefaultDescription() {
        String desc = super.getDefaultDescription();
        if (getWidgetID() != null) {
            desc = getWidgetID() + "." + desc;
        }
        return desc;
    }

    /** Obtain the class of the given reference's widget, or return
     * org.eclipse.swt.widgets.Widget if not found.
     */
    private static String getRefClass(Resolver r, String id) {
        WidgetReference ref = r.getWidgetReference(id);
        return ref == null ? "org.eclipse.swt.widgets.Widget" : ref.getRefClassName();
    }
}
