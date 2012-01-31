
package abbot.script.swt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Vector;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.InvalidScriptException;
import abbot.swt.Resolver;

/** Class for script steps that want to invoke a method on a class.
 * Subclasses may override getMethod and getTarget to customize behavior.
 * <blockquote><code>
 * &lt;call method="..." args="..." class="..." [property="..."]&gt;<br>
 * </code></blockquote>
 * <p>
 * If a property is indicated, the stringified result of the call will be
 * stored in a property for later retrieval into any string as ${property}.
 */
public class Call extends Step {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	private String targetClassName = null;
	private String methodName;
	private String[] args;
	private String propertyName = null;

	private static final String USAGE =
		"<call class=\"...\" method=\"...\" args=\"...\" [property=\"...\"]/>";

	public Call(Resolver resolver, HashMap attributes) {
		super(resolver, attributes);
		setMethodName((String)attributes.get(TAG_METHOD));
		setTargetClassName((String)attributes.get(TAG_CLASS));
		String argList = (String)attributes.get(TAG_ARGS);
		if (argList == null)
			argList = "";
		args = ArgumentParser.parseArgumentList(argList);
	}

	public Call(Resolver resolver, String description, 
				String className, String methodName, String[] args) {
		super(resolver, description);
		this.targetClassName = className;
		this.methodName = methodName;
		this.args = args != null ? args : new String[0];
	}

	protected String getDefaultDescription() {
		return getMethodName() + "(" + getEncodedArguments() + ")";
	}

	public String getUsage() { return USAGE; }

	public String getXMLTag() { return TAG_CALL; }

	/** Convert our argument vector into a single String. */
	public String getEncodedArguments() {
		String argList = "";
		for (int i=0;i < args.length;i++) {
			if (i != 0)
				argList += ",";
			argList += encode(args[i]);
		}
		return argList;
	}

	public void setArguments(String argList) {
		args = ArgumentParser.parseArgumentList(argList);
	}

	public void setMethodName(String mn) {
		if (mn == null)
			usage(Strings.get("MethodNameMissing"));
		methodName = mn;
	}

	/** Method name to save in script. */
	public String getMethodName() {
		return methodName;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String cn) {
		if (cn == null)
			usage(Strings.get("ClassNameMissing"));
		targetClassName = cn;
	}

	/** Attributes to save in script.  FIXME use a hash table */
	public HashMap getAttributes() {
		HashMap map = super.getAttributes();
		map.put(TAG_CLASS, getTargetClassName());
		map.put(TAG_METHOD, getMethodName());
		if (args.length != 0) {
			map.put(TAG_ARGS, getEncodedArguments());
		}
		return map;
	}

	/** Return the arguments as an array of String. */
	public String[] getArgs() { return args; }

	protected void runStep() throws Throwable {
		invoke();
	}

	/** Deferred evaluation of arguments allows us to refer to components that
	 * don't necessarily exist when the script is read in. 
	 */
	// NOTE: the default invocation is expected to be performed on a
	// ComponentTester target. 
	protected Object invoke() throws Throwable {
		try {
			Method m = getMethod();
			Log.debug("Invoking " + m + " with " + getEncodedArguments());
		
			Object[] params = (Object[])ArgumentParser.eval((Resolver)getResolver(), (String[])args, 
									(Class[])m.getParameterTypes());
			return m.invoke(getTarget(), params);
		}
		catch(java.lang.reflect.InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	/** Return the method to be used for invocation.  The concrete
	 * implementation of this method should invoke the method
	 * resolveMethod with the appropriate arguments.
	 */ 
	protected Method getMethod() throws InvalidScriptException {
		return resolveMethod(getMethodName(), getTargetClass(), null);
	}

	/** Get the class of the target of the method invocation.  This is public
	 * to provide editors access to the class being used (for example,
	 * providing a menu of all available methods).
	 */
	public Class getTargetClass() throws InvalidScriptException {
		return resolveClass(getTargetClassName());
	}

	/** Return the target of the invocation.  The default implementation
	 * always returns null for static methods; it will attempt to instantiate
	 * a target for non-static methods.
	 */
	protected Object getTarget() throws Throwable {
		Method m = getMethod();
		if ((m.getModifiers() & Modifier.STATIC) == 0) {
			try {
				return getTargetClass().newInstance();
			}
			catch(Exception e) {
				setScriptError(new InvalidScriptException("Can't create an object instance of class " + getTargetClassName() + " for non-static method " + m.getName()));
			}
		}
		return null;
	}

	/** Look up the given method name in the given class. */
	protected Method resolveMethod(String name, Class cls, Class returnType) {
		Method method = null;
		// use getDeclaredMethods to include class methods
		Method[] mlist = cls.getMethods();
		Vector found = new Vector();
		for (int i=0;i < mlist.length;i++) {
			Method m = mlist[i];
			Class[] params = m.getParameterTypes();
			if (m.getName().equals(name)
				&& params.length == args.length 
				&& (returnType == null
					|| m.getReturnType().equals(returnType))) {  
				found.add(m);
			}
		}
		if (found.size() == 0) {
			throw new IllegalArgumentException(Strings.get("NoMatchingMethod",
														   new Object[] {
				name, (returnType == null 
					   ? "void" 
					   : returnType.toString()),
				String.valueOf(args.length), cls }));
		}
		else if (found.size() != 1) {
			throw new IllegalArgumentException(Strings.get("MultipleMethods",
														   new Object[] {
				name, cls }));
		}
		Log.debug("found '" + name + "' in " + cls);
		return (Method)found.get(0);
	}

	static String encode(String arg) {
		if (arg == null)
			return "(null)";
		arg = ArgumentParser.replace(arg, "\\,", "--ESCAPED-COMMA--");
		arg = ArgumentParser.replace(arg, ",", "\\,");
		return ArgumentParser.replace(arg, "--ESCAPED-COMMA--", "\\,");
	}
}
