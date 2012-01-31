package abbot.script.swt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.widgets.Widget;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import abbot.Log;
import abbot.finder.matchers.swt.NameMatcher;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.i18n.Strings;
import abbot.script.InvalidScriptException;
import abbot.script.NoSuchReferenceException;
import abbot.script.XMLConstants;
import abbot.script.XMLifiable;
import abbot.swt.Resolver;
import abbot.finder.swt.WidgetFinder;
import abbot.tester.swt.WidgetTester;

/**
 * Provides access to one step (line) from a script.  A Step is the basic
 * unit of execution.  All derived classes should have a tag "sampleStep" with 
 * a corresponding class abbot.script.SampleStep.  The class must supply at
 * least a Constructor with the signature SampleStep(Resolver, HashMap).  If
 * the step has contents (e.g. Sequence), then it should also provide
 * SampleStep(Resolver, Element, HashMap).
 */
public abstract class Step implements XMLConstants, XMLifiable, Serializable {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	private String description = null;
	private Resolver resolver;
	private WidgetTester tester;
	/** Error encountered on parse. */
	private Throwable invalidScriptError = null;

	public Step(Resolver resolver, HashMap attributes) {
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
			throw new Error("Resolver must be provided");
			// TODO CREATE NEW RESOLVER HERE
		}
	
		this.resolver = resolver;
		if ("".equals(description))
			description = null;
		this.description = description;
	}
    
	protected final void parseStepAttributes(HashMap attributes) {
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
	protected abstract String getDefaultDescription();

	/** For use by subclasses when an error is encountered during parsing. 
	 * Should only be used by the XML parsing ctors.
	 */
	protected void setScriptError(Throwable thr) {
		if (invalidScriptError != null)
			invalidScriptError = thr;
		else 
			Log.warn("More than one script error encountered: " + thr);
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
			MessageFormat mf =
				new MessageFormat(Strings.get("UsageDetails"));
			msg = mf.format(new Object[] { msg, details });
		}
		setScriptError(new InvalidScriptException(msg));
	}

	/** Attributes to save in script. */
	public HashMap getAttributes() {
		HashMap map = new HashMap();
		if (description != null
			&& !description.equals(getDefaultDescription()))
			map.put(TAG_DESC, description);
		return map;
	}

	/** Resolve the given name into a Widget. */
	protected Widget resolve(String name) 
		throws NoSuchReferenceException,
			   WidgetNotFoundException,
			   MultipleWidgetsFoundException {
		//WidgetReference ref = resolver.getWidgetReference(name);
		// TODO: Make sure this actually works
		if (name != null && name !="") {
			return getFinder().find(new NameMatcher(name));
		}
		throw new NoSuchReferenceException(name);
	}

	public WidgetFinder getFinder() {
		return BasicFinder.getDefault();
	}
	public Resolver getResolver() { return resolver; }

	/** Override if the step actually has some contents.  In most cases, it
	 * won't.
	 */
	protected Element addContent(Element el) { 
		return el;
	}

	protected Element addAttributes(Element el) {
		HashMap atts = getAttributes();
		Iterator iter = atts.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String)iter.next();
			String value = (String)atts.get(key);
			el.setAttribute(key, value);
		}
		return el;
	}

	public String toEditableString() {
		return toXMLString(this);
	}

	/** Provide a one-line XML string representation. */
	public static String toXMLString(XMLifiable obj) {
		// Comments are the only things that aren't actually elements...
		if (obj instanceof Comment) {
			return "<!--" + ((Comment)obj).getDescription() + "-->";
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
		throws InvalidScriptException {
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
		catch (IOException e) {
			/* Added due to changes in abbot; exception handler
			 * not implemented */
			throw new InvalidScriptException(e.getMessage());
		}
	}

	protected static HashMap createAttributeMap(Element el) {
		Log.debug("Creating attribute map for " + el);
		HashMap attributes = new HashMap();
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
		HashMap attributes = createAttributeMap(el);
		String name = tag.substring(0, 1).toUpperCase() + tag.substring(1);
		if (tag.equals(TAG_WAIT)) {
			attributes.put(TAG_WAIT, "true");
			name = "Assert";
		}
		try {
			Class cls = Class.forName("abbot.script." + name,
									  true, Thread.currentThread().
									  getContextClassLoader());
			try {
				// Steps with contents require access to the XML element
				Class[] argTypes = new Class[] {
					Resolver.class, Element.class, HashMap.class
				};
				Constructor ctor = cls.getConstructor(argTypes);
				return (Step)ctor.newInstance(new Object[] {
					resolver, el, attributes
				});
			}
			catch(NoSuchMethodException nsm) {
				// All steps must support this ctor
				Class[] argTypes = new Class[] { 
					Resolver.class, HashMap.class
				};
				Constructor ctor = cls.getConstructor(argTypes);
				return (Step)ctor.newInstance(new Object[] {
					resolver, attributes
				});
			}
		}
		catch(ClassNotFoundException cnf) {
			MessageFormat mf =
				new MessageFormat(Strings.get("UnknownTag"));
			throw new InvalidScriptException(mf.format(new Object[] { tag }));
		}
		catch(Exception exc) {
			throw new InvalidScriptException(exc.getMessage());
		}
	}

	protected String simpleClassName(Class cls) {
		return WidgetTester.simpleClassName(cls);
	}

	/** Return a description of this script step. */
//	public String toString() {
//		return getDescription();
//	}

	/** Returns the Class corresponding to the given class name.  Provides
	 * just-in-time classname resolution to ensure loading by the proper class
	 * loader. <p>
	 * NOTE: only works if the app under test has been launched.
	 */
	public Class resolveClass(String className) throws InvalidScriptException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			return Class.forName(className, true, cl);
		}
		catch(ClassNotFoundException cnf) {
			// Unrecoverable; script must be fixed
			MessageFormat mf =
				new MessageFormat(Strings.get("ClassNotFound"));
			throw new InvalidScriptException(mf.format(new Object[] {
				className
			}));
		}
	}

	/** Look up an appropriate WidgetTester given an arbitrary class.
	 * If the class is derived from abbot.swt.tester.WidgetTester, instantiate
	 * one; if it is derived from java.awt.Widget, return a matching Tester.
	 * Otherwise return abbot.swt.tester.WidgetTester.<p>
	 * The class is looked up based on the appropriate context for the Step.
	 */
	protected WidgetTester resolveTester(String compClassName) 
		throws InvalidScriptException {
		Class testedClass = resolveClass(compClassName);
		WidgetTester tester =
			WidgetTester.getTester(org.eclipse.swt.widgets.Widget.class);
		if (WidgetTester.class.isAssignableFrom(testedClass)) {
			try {
				tester = (WidgetTester)testedClass.newInstance();
			}
			catch(Exception e) {
				throw new InvalidScriptException(e.getMessage());
			}
		}
		else if (org.eclipse.swt.widgets.Widget.class.isAssignableFrom(testedClass)) {
			tester = WidgetTester.getTester(testedClass);
		}
		Log.debug("Tester for " + testedClass.getName() + " is " 
				  + tester.getClass()
				  + " (" + tester.getClass().getClassLoader() + ")");
		return tester;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		// NOTE: this is only to avoid drag/drop errors
	}

	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException {
		// NOTE: this is only to avoid drag/drop errors
	}
}
