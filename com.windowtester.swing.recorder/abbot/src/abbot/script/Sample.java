package abbot.script;

import java.util.Map;


/** Encapsulate capture of a value.  Usage:<br>
 * <blockquote><code>
 * &lt;sample property="..." method="assertXXX" [class="..."]&gt;<br>
 * &lt;sample property="..." method="(get|is|has)XXX" component="component_id"&gt;<br>
 * </code></blockquote>
 * The <b>sample</b> step stores the value found from the given method
 * {@link abbot.tester.ComponentTester} class; the class tag is required for
 * methods based on a class derived from {@link abbot.tester.ComponentTester};
 * the class tag indicates the {@link java.awt.Component} class, not the 
 * Tester class (the appropriate tester class will be derived automatically).
 * The second format indicates a property sample on the given component.
 * In both cases, the result of the invocation will be saved in the current
 * {@link abbot.script.Resolver} as a property with the given property name.
 */

public class Sample extends PropertyCall {
    private static final String USAGE = 
        "<sample property=... component=... method=.../>\n"
        + "<sample property=... method=... [class=...]/>";

    private String propertyName = null;

    public Sample(Resolver resolver, Map attributes) {
        super(resolver, attributes);
        propertyName = (String)attributes.get(TAG_PROPERTY);
    }

    /** Component property sample. */
    public Sample(Resolver resolver, String description,
                  String methodName, String id, String propName) {
        super(resolver, description, methodName, id);
        propertyName = propName;
    }

    /** Static method property sample. */
    public Sample(Resolver resolver, String description,
                  String className, String methodName, String[] args,
                  String propName) {
        super(resolver, description, className, methodName, args);
        propertyName = propName;
    }

    public Map getAttributes() {
        Map map = super.getAttributes();
        if (propertyName != null) {
            map.put(TAG_PROPERTY, propertyName);
        }
        return map;
    }

    public String getDefaultDescription() {
        return getPropertyName() + "=" + super.getDefaultDescription();
    }

    public String getUsage() { return USAGE; }
    public String getXMLTag() { return TAG_SAMPLE; }

    public void setPropertyName(String name) {
        propertyName = name;
    }

    public String getPropertyName() {
        return propertyName;
    }

    /** Store the results of the invocation in the designated property as a
        String-encoded value.
    */
    protected Object invoke() throws Throwable {
        Object obj = super.invoke();
        if (propertyName != null) {
            getResolver().setProperty(propertyName, obj);
        }
        return obj;
    }
}
