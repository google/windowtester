package abbot.script;

import java.awt.Component;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import abbot.Log;
import abbot.finder.*;
import abbot.i18n.Strings;
import abbot.tester.ComponentTester;

/**
 * Provides access to one step (line) from a script.  A Step is the basic
 * unit of execution.  
 * <b>Custom Step classes</b><p>
 * All custom {@link Step} classes must supply a {@link Constructor} with the
 * signature <code>&lt;init&gt;(${link Resolver}, {@link Map})</code>.  If 
 * the step has contents (e.g. {@link Sequence}), then it should also
 * provide a {@link Constructor} with the signature
 * <code>&lt;init&gt;({@link Resolver}, {@link Element}, {@link Map})</code>.
 * <p>
 * The XML tag for a given {@link Step} will be used to auto-generate the
 * {@link Step} class name, e.g. the tag &lt;aphrodite/&gt; causes the
 * parser to create an instance of class <code>abbot.script.Aphrodite</code>,
 * using one of the {@link Constructor}s described above. 
 * <p>
 * All derived classes should include an entry in the
 * <a href={@docRoot}/../abbot.xsd>schema</a>, or validation must be turned
 * off by setting the System property
 * <code>abbot.script.validate=false</code>. 
 * <p>
 * You can make the custom <code>Aphrodite</code> step do just about anything
 * by overriding the {@link #runStep()} method.  
 * <p>
 * See the source for any {@link Step} implementation in this package for
 * examples. 
 */
public abstract class Step implements XMLConstants, XMLifiable, Serializable {

    private String description = null;
    private Resolver resolver;
    /** Error encountered on parse. */
    private Throwable invalidScriptError = null;

    public Step(Resolver resolver, Map attributes) {
        this(resolver, "");
        Log.debug("Instantiating " + getClass());
        if (Log.expectDebugOutput) {
            Iterator iter = attributes.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String)iter.next();
                Log.debug(key + "=" + attributes.get(key));
            }
        }
        parseStepAttributes(attributes);
    }

    public Step(Resolver resolver, String description) {
        // Kind of a hack; a Script is its own resolver
        if (resolver == null) {
            if (!(this instanceof Resolver)) {
                throw new Error("Resolver must be provided");
            }
            resolver = (Resolver)this;
        }
        else if (this instanceof Resolver) {
            resolver = (Resolver)this;
        }
        this.resolver = resolver;
        if ("".equals(description))
            description = null;
        this.description = description;
    }
    
    /** Only exposed so that Script may invoke it on load from disk. */
    protected final void parseStepAttributes(Map attributes) {
        Log.debug("Parsing attributes for " + getClass());
        description = (String)attributes.get(TAG_DESC);
    }

    /** Main run method.  Should <b>never</b> be run on the event dispatch
     * thread, although no check is explicitly done here.
     */
    public final void run() throws Throwable {
        if (invalidScriptError != null)
            throw invalidScriptError;
        Log.debug("Running " + toString());
        runStep();
    }

    /** Implement the step's behavior here. */
    protected abstract void runStep() throws Throwable;

    public String getDescription() { 
        return description != null ? description : getDefaultDescription();
    }
    public void setDescription(String desc) { 
        description = desc;
    }

    /** Define the XML tag to use for this script step. */
    public abstract String getXMLTag();

    /** Provide a usage String for this step. */
    public abstract String getUsage();

    /** Return a reasonable default description for this script step.
        This value is used in the absence of an explicit description. 
     */
    public abstract String getDefaultDescription();

    /** For use by subclasses when an error is encountered during parsing. 
     * Should only be used by the XML parsing ctors.
     */
    protected void setScriptError(Throwable thr) {
        if (invalidScriptError == null) {
            invalidScriptError = thr;
        }
        else {
            Log.warn("More than one script error encountered: " + thr);
            Log.warn("Already have: " + invalidScriptError);
        }
    }

    /** Throw an invalid script exception describing the proper script
     * usage.  This should be used by derived classes whenever parsing
     * indicates invalid input.
     */ 
    protected void usage() {
        usage(null);
    }

    /** Store an invalid script exception describing the proper script
     * usage.  This should be used by derived classes whenever parsing
     * indicates invalid input.  
     */ 
    protected void usage(String details) {
        String msg = getUsage();
        if (details != null) {
            msg = Strings.get("step.usage", new Object[] { msg, details });
        }
        setScriptError(new InvalidScriptException(msg));
    }

    /** Attributes to save in script. */
    public Map getAttributes() {
        Map map = new HashMap();
        if (description != null
            && !description.equals(getDefaultDescription()))
            map.put(TAG_DESC, description);
        return map;
    }

    public Resolver getResolver() { return resolver; }

    /** Override if the step actually has some contents.  In most cases, it
     * won't.
     */
    protected Element addContent(Element el) { 
        return el;
    }

    /** Add an attribute to the given XML Element.  Attributes are kept in
        alphabetical order. */
    protected Element addAttributes(Element el) {
        // Use a TreeMap to keep the attributes sorted on output
        Map atts = new TreeMap(getAttributes());
        Iterator iter = atts.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = (String)atts.get(key);
            if (value == null) {
                Log.warn("Attribute '" + key + "' value was null in step "
                         + getXMLTag());
                value = "";
            }
            el.setAttribute(key, value);
        }
        return el;
    }

    /** Convert this Step into a String suitable for editing.  The default is
        the XML representation of the Step. */
    public String toEditableString() {
        return toXMLString(this);
    }

    /** Provide a one-line XML string representation. */
    public static String toXMLString(XMLifiable obj) {
        // Comments are the only things that aren't actually elements...
        if (obj instanceof Comment) {
            return "<!-- " + ((Comment)obj).getDescription() + " -->";
        }
        Element el = obj.toXML();
        StringWriter writer = new StringWriter();
        try {
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(el, writer);
        }
        catch(IOException io) {
            Log.warn(io);
        }
        return writer.toString();
    }

    /** Convert the object to XML.  */
    public Element toXML() {
        return addAttributes(addContent(new Element(getXMLTag())));
    }

    /** Create a new step from an in-line XML string. */
    public static Step createStep(Resolver resolver, String str) 
        throws InvalidScriptException, IOException {
        StringReader reader = new StringReader(str);
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(reader);
            Element el = doc.getRootElement();
            return createStep(resolver, el);
        }
        catch(JDOMException e) {
            throw new InvalidScriptException(e.getMessage());
        }
    }

    /** Convert the attributes in the given XML Element into a Map of
        name/value pairs. */
    protected static Map createAttributeMap(Element el) {
        Log.debug("Creating attribute map for " + el);
        Map attributes = new HashMap();
        Iterator iter = el.getAttributes().iterator();
        while (iter.hasNext()) {
            Attribute att = (Attribute)iter.next();
            attributes.put(att.getName(), att.getValue());
        }
        return attributes;
    }

    /**
     * Factory method, equivalent to a "fromXML" for step creation.  Looks for
     * a class with the same name as the XML tag, with the first letter
     * capitalized.  For example, &lt;call /&gt; is abbot.script.Call.
     */
    public static Step createStep(Resolver resolver, Element el) 
        throws InvalidScriptException {
        String tag = el.getName();
        Map attributes = createAttributeMap(el);
        String name = tag.substring(0, 1).toUpperCase() + tag.substring(1);
        if (tag.equals(TAG_WAIT)) {
            attributes.put(TAG_WAIT, "true");
            name = "Assert";
        }
        try {
            name = "abbot.script." + name;
            Log.debug("Instantiating " + name);
            Class cls = Class.forName(name);
            try {
                // Steps with contents require access to the XML element
                Class[] argTypes = new Class[] {
                    Resolver.class, Element.class, Map.class
                };
                Constructor ctor = cls.getConstructor(argTypes);
                return (Step)ctor.newInstance(new Object[] {
                    resolver, el, attributes
                });
            }
            catch(NoSuchMethodException nsm) {
                // All steps must support this ctor
                Class[] argTypes = new Class[] { 
                    Resolver.class, Map.class
                };
                Constructor ctor = cls.getConstructor(argTypes);
                return (Step)ctor.newInstance(new Object[] {
                    resolver, attributes
                });
            }
        }
        catch(ClassNotFoundException cnf) {
            String msg = Strings.get("step.unknown_tag", new Object[] { tag });
            throw new InvalidScriptException(msg);
        }
        catch(InvocationTargetException ite) {
            Log.warn(ite);
            throw new InvalidScriptException(ite.getTargetException().
                                             getMessage());
        }
        catch(Exception exc) {
            Log.warn(exc);
            throw new InvalidScriptException(exc.getMessage());
        }
    }

    protected String simpleClassName(Class cls) {
        return ComponentTester.simpleClassName(cls);
    }

    /** Return a description of this script step. */
    public String toString() {
        return getDescription();
    }

    /** Returns the Class corresponding to the given class name.  Provides
     * just-in-time classname resolution to ensure loading by the proper class
     * loader. <p>
     */
    public Class resolveClass(String className) throws ClassNotFoundException {
        ClassLoader cl = getResolver().getContextClassLoader();
        return Class.forName(className, true, cl);
    }

    /** Look up an appropriate ComponentTester given an arbitrary
     * Component-derived class.
     * If the class is derived from abbot.tester.ComponentTester, instantiate
     * one; if it is derived from java.awt.Component, return a matching Tester.
     * Otherwise return abbot.tester.ComponentTester.<p>
     * @throws ClassNotFoundException If the given class can't be found.
     * @throws IllegalArgumentException If the tester cannot be instantiated.
     */
    protected ComponentTester resolveTester(String className) 
        throws ClassNotFoundException {
        Class testedClass = resolveClass(className);
        if (Component.class.isAssignableFrom(testedClass))
            return ComponentTester.getTester(testedClass);
        else if (ComponentTester.class.isAssignableFrom(testedClass)) {
            try {
                return (ComponentTester)testedClass.newInstance();
            }
            catch(Exception e) {
                String msg = "Custom ComponentTesters must provide "
                    + "an accessible no-args Constructor: " + e.getMessage();
                throw new IllegalArgumentException(msg);
            }
        }
        String msg = "The given class '" + className
            + "' is neither a Component nor a ComponentTester";
        throw new IllegalArgumentException(msg);
    }

    private void writeObject(ObjectOutputStream out) {
        // NOTE: this is only to avoid drag/drop errors
        out = null;
    }

    private void readObject(ObjectInputStream in) {
        // NOTE: this is only to avoid drag/drop errors
        in = null;
    }
}
