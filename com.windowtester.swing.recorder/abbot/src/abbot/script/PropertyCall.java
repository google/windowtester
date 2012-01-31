package abbot.script;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.Map;

import abbot.tester.ComponentTester;

/** Provides support for using property-like methods, including select
 * non-static method access to Components.  Specifically, allows specification
 * of a ComponentReference to be used as the method invocation target.  If a
 * ComponentReference is given, then the class of the component reference is
 * used as the target class.<p>
 */

public abstract class PropertyCall extends Call {

    private String componentID = null;

    /** Create a PropertyCall based on loaded XML attributes. */
    public PropertyCall(Resolver resolver, Map attributes) {
        super(resolver, patchAttributes(resolver, attributes));
        componentID = (String)attributes.get(TAG_COMPONENT);
    }

    /** Create a PropertyCall based on a static invocation on an
        arbitrary class.
    */
    public PropertyCall(Resolver resolver, String description,
                                 String className, String methodName,
                                 String[] args) {
        super(resolver, description, className, methodName, args);
        componentID = null;
    }

    /** Create a PropertyCall with a Component target.  The target
        class name is derived from the given component reference ID. */
    public PropertyCall(Resolver resolver, String description,
                                 String methodName, String id) {
        super(resolver, description,
              getRefClass(resolver, id), methodName, null);
        componentID = id;
    }

    /** Return the component reference ID used by this method invocation. */
    public String getComponentID() {
        return componentID;
    }

    /** Set the component reference ID used by method invocation.  The class
     * of the component referenced by the component reference will replace the 
     * current target class.
     */
    public void setComponentID(String id) {
        if (id == null) {
            componentID = null;
        }
        else {
            ComponentReference ref = getResolver().getComponentReference(id);
            if (ref != null) {
                componentID = id;
                setTargetClassName(ref.getRefClassName());
            }
            else
                throw new NoSuchReferenceException(id);
        }
    }

    /** Save attributes specific to this Step class. */
    public Map getAttributes() {
        Map map = super.getAttributes();
        if (componentID != null) {
            map.remove(TAG_CLASS);
            map.put(TAG_COMPONENT, componentID);
        }
        return map;
    }

    /** Return the target of the method invocation. */
    protected Object getTarget(Method m) throws Throwable {
        if (componentID != null) {
            return ArgumentParser.eval(getResolver(), componentID,
                                       Component.class);
        }
        return super.getTarget(m);
    }

    /** Insert default values if necessary. */
    private static Map patchAttributes(Resolver resolver, Map map) {
        String id = (String)map.get(TAG_COMPONENT);
        if (id != null) {
            map.put(TAG_CLASS, getRefClass(resolver, id));
        }
        return map;
    }

    private final static String[] prefixes = { "is", "has", "get" };
    private final static Class[] returnTypes = {
        boolean.class, boolean.class, null,
    };

    /** Returns whether the given method is a property accessor.  In addition
     * to standard is/get/has property accessors, this includes
     * pseudo-property methods on ComponentTester objects.
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
                && ((ComponentTester.class.isAssignableFrom(dc)
                     && params.length == 1
                     && Component.class.isAssignableFrom(params[0]))
                    || params.length == 0)
                && (returnTypes[i] == null 
                    || returnTypes[i].equals(rt))) {
                return true;
            }
        }
        return false;
    }

    public String getDefaultDescription() {
        String desc = super.getDefaultDescription();
        if (getComponentID() != null) {
            desc = getComponentID() + "." + desc;
        }
        return desc;
    }

    /** Obtain the class of the given reference's component, or return
     * java.awt.Component if not found.
     */
    private static String getRefClass(Resolver r, String id) {
        ComponentReference ref = r.getComponentReference(id);
        return ref == null
            ? Component.class.getName() : ref.getRefClassName();
    }
}
