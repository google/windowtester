package abbot.script.swt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Widget;

import abbot.Log;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.script.NoSuchReferenceException;
import abbot.script.parsers.Parser;
import abbot.swt.Resolver;
import abbot.tester.swt.WidgetTester;

/** Provide parsing of a String into an array of appropriately typed
 * arguments.   Arrays are indicated by square brackets, and arguments are
 * separated by commas, e.g.<br>
 * An empty String array (length zero): "[]"<br>
 * Three arguments "one,two,three"<br>
 * An array of three arguments, with embedded comma: "[one\,one,two,three]"<br>
 * An argument with square brackets: "\[one\]"<br>
 * A single null argument: "null"<br>
 */

public class ArgumentParser {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    private ArgumentParser() { }

    /** Maps class names to their corresponding string parsers. */
    private static HashMap parsers = new HashMap();

    private static boolean isExtension(String name) {
        return name.indexOf(".extensions.") != -1;
    }

    private static Parser findParser(String name, Class targetClass) {
        Log.debug("Trying " + name + " for " + targetClass);
        try {
            Class cvtClass = isExtension(name)
                ? Class.forName(name, true, targetClass.getClassLoader())
                : Class.forName(name);
			Parser parser = (Parser)cvtClass.newInstance();
            if (cvtClass.getName().indexOf(".extensions.") == -1)
                parsers.put(targetClass, parser);
            return parser;
        }
        catch(InstantiationException ie) {
            Log.debug(ie);
        }
        catch(IllegalAccessException iae) {
            Log.debug(iae);
        }
        catch(ClassNotFoundException cnf) {
            Log.debug(cnf);
        }
        return null;
    }

    /** Set the parser for a given class.  Returns the old one, if any. */
    public static Parser setParser(Class cls, Parser parser) {
		Parser old = (Parser)parsers.get(cls);
        parsers.put(cls, parser);
        return old;
    }

    /** Find a string parser for the given class.  Returns null if none
     * found.
     */
    public static Parser getParser(Class cls) {
		Parser parser = (Parser)parsers.get(cls);
        // Load core testers with the current framework's class loader
        // context, and anything else in the context of the code under test
        if (parser == null) {
            String base = WidgetTester.simpleClassName(cls);
            String pkg = Parser.class.getPackage().getName();
            parser = findParser(pkg + "." + base + "Parser", cls);
            if (parser == null) {
                parser = findParser(pkg + ".extensions."
                                          + base + "Parser", cls);
            }
        }
        return parser;
    }

    /** Convert the given encoded String into an array of Strings.
     * Interprets strings of the format "[el1,el2,el3]" to be a single (array)
     * argument. <p>
     * Explicit commas and square brackets in arguments must be escaped by
     * preceding the character with a backslash ('\').  The string
     * '(null)' is interpreted as the value null.
     */
    public static String[] parseArgumentList(String encodedArgs) {
        ArrayList alist = new ArrayList();
        if (encodedArgs == null || "".equals(encodedArgs))
            return new String[0];
        StringTokenizer st = new StringTokenizer(encodedArgs, ",");
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            // FIXME what if we want a string argument of "(null)/null"?
            if ("null".equals(str) || "(null)".equals(str)) {
                str = null;
            }
            else if ("\"\"".equals(str)) {
                str = "";
            }
            // Patch back together escaped commas
            else if (str.startsWith("[")) {
                int count = 1;
                // Replace escaped commas
                while (str.endsWith("\\") && st.hasMoreTokens()) {
                    String next = st.nextToken();
                    if (next.endsWith("]") && !next.endsWith("\\]"))
                        --count;
                    str += "," + next;
                }
                if (st.countTokens() == 0 && str.endsWith("]"))
                    --count;
                else while (st.hasMoreTokens() && count != 0) {
                    String next = st.nextToken();
                    if (next.startsWith("[") && !next.endsWith("\\[")) 
                        ++count;
                    else if (next.endsWith("]") && !next.endsWith("\\]")) 
                        --count;
                    str += "," + next;
                }
                if (count != 0) {
                    String msg = "Unterminated array '" + encodedArgs + "'";
                    throw new IllegalArgumentException(msg);
                }
            }
            else {
                while (str.endsWith("\\") && st.hasMoreTokens()) {
                    str = str.substring(0, str.length()-1)
                        + "," + st.nextToken();
                }
            }
            alist.add(str);
        }
        return (String[])alist.toArray(new String[alist.size()]);
    }

    /** Performs property substitutions on the argument priort to evaluating
     * it.  Substitutions are not recursive. 
     */
    public static String substitute(Resolver resolver, String arg) {
        int i = 0;
        int base = 0;
        while ((i = arg.indexOf("${", base)) != -1) {
            int end = arg.indexOf("}", i);
            if (end == -1)
                break;
            String name = arg.substring(i + 2, end);
            String value = resolver.getProperty(name);
            Log.debug(name + "=" + value);
            if (value != null) {
                arg = replace(arg, "${" + name + "}", value);
            }
            base = i + 1;
        }
        return arg;
    }


	/** Evaluate the given set of arguments into the given set of types. */
	public static Object[] eval(Resolver resolver,String[] args, Class[] params) 
		throws IllegalArgumentException,
			   NoSuchReferenceException,
			   WidgetNotFoundException {
		Object[] plist = new Object[params.length];
		for(int i=0;i < plist.length;i++) {
			String arg = args[i];
			plist[i] = eval(resolver, arg, params[i]);
		}
		return plist;
	}

    /** Convert the given string into the given class, if possible, using any
     * available parsers if conversion to basic types fails.
     * The Resolver could be a parser, but it would need to adapt
     * automatically to whatever is the current context.<p>
     * Performs property substitution on the argument prior to evaluating it.
     */
    public static Object eval(Resolver resolver, String arg, Class cls) 
        throws IllegalArgumentException, 
               NoSuchReferenceException,
               WidgetNotFoundException {

        // Preform property substitution
        arg = substitute(resolver, arg);

		Parser parser;
        Object result = null;
        try {
            if (arg == null) {
                result = null;
            }
            else if (cls.equals(Boolean.class)
                     || cls.equals(boolean.class)) {
                result = Boolean.valueOf(arg);
            }
            else if (cls.equals(Short.class)
                     || cls.equals(short.class)) {
                result = Short.valueOf(arg);
            }
            else if (cls.equals(Integer.class)
                     || cls.equals(int.class)) {
                result = Integer.valueOf(arg);
            }
            else if (cls.equals(Long.class)
                     || cls.equals(long.class)) {
                result = Long.valueOf(arg);
            }
            else if (cls.equals(Float.class)
                     || cls.equals(float.class)) {
                result = Float.valueOf(arg);
            }
            else if (cls.equals(Double.class)
                     || cls.equals(double.class)) {
                result = Double.valueOf(arg);
            }
            else if (cls.equals(WidgetReference.class)) {
                WidgetReference ref = resolver.getWidgetReference(arg);
                if (ref == null)
                    throw new NoSuchReferenceException("The resolver " 
                                                       + resolver
                                                       + " has no reference '"
                                                       + arg + "'");
                result = ref;
            }
            else if (Widget.class.isAssignableFrom(cls)) {
                WidgetReference ref = resolver.getWidgetReference(arg);
                if (ref == null)
                    throw new NoSuchReferenceException("The resolver " 
                                                       + resolver
                                                       + " has no reference '"
                                                       + arg + "'");
                // Avoid requiring the user to wait for a Widget to become
                // available, in most cases.  In those cases where the
                // Widget creation is particularly slow, an explicit wait
                // can be added.
                WidgetTester.waitForWidgetShowing(ref);
                try{
                	result = WidgetTester.findWidget(ref);
                		//DefaultWidgetFinder.getFinder().findWidget(ref);
                }
                catch(MultipleWidgetsFoundException mwfe){
                	mwfe.printStackTrace();
                }
            }
            else if (cls.equals(String.class)) {
                result = unescapeBrackets(arg);
            }
            else if ((parser = getParser(cls)) != null) {
                result = parser.parse(unescapeBrackets(arg));
            }
            else if (cls.isArray() && arg.startsWith("[")) {
                String[] args = 
                    parseArgumentList(arg.substring(1, arg.length()-1));
                Class base = cls.getComponentType();
                Object arr = Array.newInstance(base, args.length);
                for (int i=0;i < args.length;i++) {
                    Object obj = eval(resolver, args[i], base);
                    Array.set(arr, i, obj);
                }
                result = arr;
            }
            else {
                String msg = "Can't convert '" + arg
                    + "' to " + cls.getName();
                throw new IllegalArgumentException(msg);
            }
            return result;
        }
        catch(NumberFormatException nfe) {
            String msg = "Can't convert '" + arg
                + "' to " + cls.getName();
            throw new IllegalArgumentException(msg);
        }
    }

    static String unescapeBrackets(String arg) {
        return replace(replace(arg, "\\[", "["), "\\]", "]");
    }

    /** Replace all instances in the given String of s1 with s2. */
    public static String replace(String str, String s1, String s2) {
        StringBuffer sb = new StringBuffer(str);
        int index = 0;
        while ((index = sb.toString().indexOf(s1, index)) != -1) {
            sb.delete(index, index + s1.length());
            sb.insert(index, s2);
            index += s2.length();
        }
        return sb.toString();
    }

}
