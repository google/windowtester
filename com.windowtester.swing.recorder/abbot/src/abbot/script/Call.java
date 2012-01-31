package abbot.script;

import java.lang.reflect.*;
import java.util.*;

import abbot.Log;
import abbot.i18n.Strings;

/** Class for script steps that want to invoke a method on a class.
 * Subclasses may override getMethod and getTarget to customize behavior.
 * <blockquote><code>
 * &lt;call method="..." args="..." class="..."&gt;<br>
 * </code></blockquote>
 */
public class Call extends Step {
    private String targetClassName = null;
    private String methodName;
    private String[] args;

    private static final String USAGE =
        "<call class=\"...\" method=\"...\" args=\"...\" [property=\"...\"]/>";

    public Call(Resolver resolver, Map attributes) {
        super(resolver, attributes);
        methodName = (String)attributes.get(TAG_METHOD);
        if (methodName == null)
            usage(Strings.get("call.method_missing"));
        targetClassName = (String)attributes.get(TAG_CLASS);
        if (targetClassName == null)
            usage(Strings.get("call.class_missing"));
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

    public String getDefaultDescription() {
        return getMethodName() + getArgumentsDescription();
    }

    public String getUsage() { return USAGE; }

    public String getXMLTag() { return TAG_CALL; }

    /** Convert our argument vector into a single String. */
    public String getEncodedArguments() {
        return ArgumentParser.encodeArguments(args);
    }

    protected String getArgumentsDescription() {
        return "("
            + ArgumentParser.replace(getEncodedArguments(),
                                     ArgumentParser.ESC_COMMA, ",") + ")";
    }

    /** Set the String representation of the arguments for this Call step. */
    public void setArguments(String[] args) {
        if (args == null)
            args = new String[0];
        this.args = args;
    }

    /** Designate the arguments for this Call step.  The format of this String
        is a comma-separated list of String representations.  See the
        abbot.parsers package for supported String representations.
        <p>
        @see ArgumentParser#parseArgumentList for a description of the format.
        @see #setArguments(String[]) for the preferred method of indicating
        the argument list.
    */
    public void setArguments(String encodedArgs) {
        if (encodedArgs == null)
            args = new String[0];
        else 
            args = ArgumentParser.parseArgumentList(encodedArgs);
    }

    public void setMethodName(String mn) {
        if (mn == null) {
            throw new NullPointerException("Method name may not be null");
        }
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
            usage(Strings.get("call.class_missing"));
        targetClassName = cn;
    }

    /** Attributes to save in script.  */
    public Map getAttributes() {
        Map map = super.getAttributes();
        map.put(TAG_CLASS, getTargetClassName());
        map.put(TAG_METHOD, getMethodName());
        if (args.length != 0) {
            map.put(TAG_ARGS, getEncodedArguments());
        }
        return map;
    }

    /** Return the arguments as an array of String.
        @deprecated use getArguments().
     */
    public String[] getArgs() { return getArguments(); }

    /** Return the arguments as an array of String. */
    public String[] getArguments() { return args; }

    protected void runStep() throws Throwable {
        try {
            invoke();
        }
        catch(Exception e) {
            Log.debug(e);
            throw e;
        }
    }

    protected Object evaluateParameter(Method m, String param, Class type)
        throws Exception {
        return ArgumentParser.eval(getResolver(), param, type);
    }

    /** Convert the String representation of the arguments into actual
     * arguments. 
     */
    protected Object[] evaluateParameters(Method m, String[] params) 
        throws Exception {
        Object[] args = new Object[params.length];
        Class[] types = m.getParameterTypes();
        for (int i=0;i < args.length;i++) {
            args[i] = evaluateParameter(m, params[i], types[i]);
        }
        return args;
    }

    /** Make the target method invocation.  This uses
        <code>evaluateParameters</code> to convert the String representation
        of the arguments into actual arguments.
        Tries all matching methods of N arguments.
    */
    protected Object invoke() throws Throwable {
        boolean retried = false;
        Method[] m = getMethods();
        for (int i=0;i < m.length;i++) {
            try {
                Object[] params = evaluateParameters(m[i], args);
                try {
                    Object target = getTarget(m[i]);
                    Log.debug("Invoking " + m[i] + " on " + target
                              + getEncodedArguments() + "'");
                    if (target != null
                        && !m[i].getDeclaringClass().
                        isAssignableFrom(target.getClass())) {
                        // If the class loader mismatches, try to resolve it
                        if (retried) {
                            String msg = "Class loader mismatch? target "
                                + target.getClass().getClassLoader()
                                + " vs. method "
                                + m[i].getDeclaringClass().getClassLoader();
                            throw new IllegalArgumentException(msg);
                        }
                        retried = true;
                        m = resolveMethods(m[i].getName(),
                                           target.getClass(), null);
                        i=-1; 
                        continue;
                    }
                    if ((m[i].getModifiers()&Modifier.PUBLIC) == 0
                        || (m[i].getDeclaringClass().getModifiers()
                            & Modifier.PUBLIC) == 0) {
                        Log.debug("Bypassing compiler access restrictions on "
                                  + "method " + m[i]);
                        m[i].setAccessible(true);
                    }
                    return m[i].invoke(getTarget(m[i]), params);
                }
                catch(java.lang.reflect.InvocationTargetException ite) {
                    throw ite.getTargetException();
                }
            }
            catch(IllegalArgumentException e) {
                if (i == m.length - 1)
                    throw e;
            }
        }
        throw new IllegalArgumentException("Can't invoke method "
                                           + m[0].getName());
    }

    /** Return matching methods to be used for invocation.  */
    public Method getMethod()
        throws ClassNotFoundException, NoSuchMethodException {
        return resolveMethod(getMethodName(), getTargetClass(), null);
    }

    /** Return matching methods to be used for invocation.  */
    protected Method[] getMethods()
        throws ClassNotFoundException, NoSuchMethodException {
        return resolveMethods(getMethodName(), getTargetClass(), null);
    }

    /** Get the class of the target of the method invocation.  This is public
     * to provide editors access to the class being used (for example,
     * providing a menu of all available methods).
     */
    public Class getTargetClass() throws ClassNotFoundException {
        return resolveClass(getTargetClassName());
    }

    /** Return the target of the invocation.  The default implementation
     * always returns null for static methods; it will attempt to instantiate
     * a target for non-static methods.
     */
    protected Object getTarget(Method m) throws Throwable {
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

    /** Look up all methods in the given class with the given name and return
        type, having the number of arguments in this step.
        @see #getArguments()
        @throws NoSuchMethodException if no matching method is found
    */
    protected Method[] resolveMethods(String name, Class cls,
                                      Class returnType)
        throws NoSuchMethodException {
        // use getDeclaredMethods to include class methods
        Log.debug("Resolving methods on " + cls);
        Method[] mlist = cls.getMethods();
        ArrayList found = new ArrayList();
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
            throw new NoSuchMethodException(Strings.get("call.no_matching_method",
                                                        new Object[] {
                name, (returnType == null 
                       ? "*" 
                       : returnType.toString()),
                String.valueOf(args.length), cls }));
        }

        // TODO Now sort according to restrictiveness of method arguments
        Method[] list = (Method[])found.toArray(new Method[found.size()]);
        //Arrays.sort(list);

        return list;
    }

    /** Look up the given method name in the given class with the requested
        return type, having the number of arguments in this step.
        @throws IllegalArgumentException if not exactly one match exists
        @see #getArguments()
     */
    protected Method resolveMethod(String name, Class cls, Class returnType)
        throws NoSuchMethodException {
        Method[] methods = resolveMethods(name, cls, returnType);
        if (methods.length != 1) {
            return disambiguateMethod(methods);
        }
        return methods[0];
    }

    /** Try to distinguish betwenn the given methods.  
        @throws IllegalArgumentException indicating the appropriate target
        method can't be distinguished.
    */
    protected Method disambiguateMethod(Method[] methods){
        String msg = Strings.get("call.multiple_methods", new Object[] {
            methods[0].getName(), methods[0].getDeclaringClass()
        });
        throw new IllegalArgumentException(msg);
    }
}
