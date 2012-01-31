package abbot.script;

import java.awt.Component;
import java.lang.reflect.Array;
import java.util.*;

import abbot.*;
import abbot.Log;
import abbot.i18n.Strings;
import abbot.finder.*;
import abbot.script.parsers.Parser;
import abbot.tester.*;

/** Provide parsing of a String into an array of appropriately typed
 * arguments.   Arrays are indicated by square brackets, and arguments are
 * separated by commas, e.g.<br>
 * <ul>
 * <li>An empty String array (length zero): "[]"
 * <li>Three arguments "one,two,three"
 * <li>An array of length three: "[one,two,three]"
 * <li>A single-element array of integer: "[1]"
 * <li>A single null argument: "null"
 * <li>An array of two strings: "[one,two]"
 * <li>Commas must be escaped when they would otherwise be interpreted as an
 * argument separator:<br>
 * "one,two%2ctwo,three" (2nd argument is "two,two")
 */

public class ArgumentParser {
    private ArgumentParser() { }

    private static final String ESC_ESC_COMMA = "%%2C";
    public static final String ESC_COMMA = "%2c";
    public static final String NULL = "null";
    public static final String DEFAULT_TOSTRING = "<default-tostring>";

    /** Maps class names to their corresponding string parsers. */
    private static Map parsers = new HashMap();

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
            String base = ComponentTester.simpleClassName(cls);
            String pkg = Parser.class.getPackage().getName();
            parser = findParser(pkg + "." + base + "Parser", cls);
            if (parser == null) {
                parser = findParser(pkg + ".extensions."
                                          + base + "Parser", cls);
            }
        }
        return parser;
    }

    private static boolean isBounded(String s) {
        return s.startsWith("[") && s.endsWith("]")
            || s.startsWith("\"") && s.endsWith("\"")
            || s.startsWith("'") && s.endsWith("'");
    }

    private static String escapeCommas(String s) {
        return replace(replace(s, ESC_COMMA, ESC_ESC_COMMA), ",", ESC_COMMA);
    }

    private static String unescapeCommas(String s) {
        return replace(replace(s, ESC_COMMA, ","), ESC_ESC_COMMA, ESC_COMMA);
    }

    public static String encodeArguments(String[] args) {
        StringBuffer sb = new StringBuffer();
        if (args.length > 0) {
            if (isBounded(args[0])) {
                sb.append(args[0]);
            }
            else {
                sb.append(escapeCommas(args[0]));
            }
            for (int i=1;i < args.length;i++) {
                sb.append(",");
                if (isBounded(args[i])) {
                    sb.append(args[i]);
                }
                else {
                    sb.append(escapeCommas(args[i]));
                }
            }
        }
        return sb.toString();
    }

    private static class Tokenizer extends ArrayList {
    	private static final long serialVersionUID = 1L;

        public Tokenizer(String input) {
            while (true) {
                int index = input.indexOf(",");
                if (index == -1) {
                    add(input);
                    break;
                }
                add(input.substring(0, index));
                input = input.substring(index + 1);
            }
        }
    }

    /** Convert the given encoded String into an array of Strings.
     * Interprets strings of the format "[el1,el2,el3]" to be a single (array)
     * argument (such commas do not need escaping). <p>
     * Explicit commas and square brackets in arguments must be escaped by
     * preceding the character with a backslash ('\').  The strings
     * '(null)' and 'null' are interpreted as the value null.<p>
     * Explicit spaces should be protected by double quotes, e.g.
     * " an argument bounded by spaces ".
     */
    public static String[] parseArgumentList(String encodedArgs) {
        ArrayList alist = new ArrayList();
        if (encodedArgs == null || "".equals(encodedArgs))
            return new String[0];
        // handle old method of escaped commas
        encodedArgs = replace(encodedArgs, "\\,", ESC_COMMA);
        Iterator iter = new Tokenizer(encodedArgs).iterator();
        while (iter.hasNext()) {
            String str = (String)iter.next();

            if (str.trim().startsWith("[")
                && !str.trim().endsWith("]")) {
                while (iter.hasNext()) {
                    String next = (String)iter.next();
                    str += "," + next;
                    if (next.trim().endsWith("]")) {
                        break;
                    }
                }
            }
            else if (str.trim().startsWith("\"")
                     && !str.trim().endsWith("\"")) {
                while (iter.hasNext()) {
                    String next = (String)iter.next();
                    str += "," + next;
                    if (next.trim().endsWith("\"")) {
                        break;
                    }
                }
            }
            else if (str.trim().startsWith("'")
                     && !str.trim().endsWith("'")) {
                while (iter.hasNext()) {
                    String next = (String)iter.next();
                    str += "," + next;
                    if (next.trim().endsWith("'")) {
                        break;
                    }
                }
            }
            
            if (NULL.equals(str.trim())) {
                alist.add(null);
            }
            else {
                // If it's an array, don't unescape the commas yet
                if (!str.startsWith("[")) {
                    str = unescapeCommas(str);
                }
                alist.add(str);
            }
        }
        return (String[])alist.toArray(new String[alist.size()]);
    }

    /** Performs property substitutions on the argument priort to evaluating
     * it.  Substitutions are not recursive. 
     */
    public static String substitute(Resolver resolver, String arg) {
        if (arg == null) {
            return arg;
        }

        int i = 0;
        int marker = 0;
        StringBuffer sb = new StringBuffer();
        while ((i = arg.indexOf("${", marker)) != -1) {
            if (marker < i) {
                sb.append(arg.substring(marker, i));
                marker = i;
            }
            int end = arg.indexOf("}", i);
            if (end == -1) {
                break;
            }
            String name = arg.substring(i + 2, end);
            Object value = resolver.getProperty(name);
            if (value == null) {
                value = System.getProperty(name);
            }
            if (value == null) {
                value = arg.substring(i, end + 1);
            }
            sb.append(toString(value));
            marker = end + 1;
        }
        sb.append(arg.substring(marker));
        return sb.toString();
    }

    /** Convert the given string into the given class, if possible,
     * using any available parsers if conversion to basic types fails.
     * The Resolver could be a parser, but it would need to adapt
     * automatically to whatever is the current context.<p>
     * Performs property substitution on the argument prior to evaluating it.
     * Spaces are only trimmed from the argument if spaces have no meaning for
     * the target class. 
     */
    public static Object eval(Resolver resolver, String arg, Class cls) 
        throws IllegalArgumentException, 
               NoSuchReferenceException,
               ComponentSearchException {
        // Perform property substitution
        arg = substitute(resolver, arg);

        Parser parser;
        Object result = null;
        try {
            if (arg == null || arg.equals(NULL)) {
                result = null;
            }
            else if (cls.equals(Boolean.class)
                     || cls.equals(boolean.class)) {
                result = Boolean.valueOf(arg.trim());
            }
            else if (cls.equals(Short.class)
                     || cls.equals(short.class)) {
                result = Short.valueOf(arg.trim());
            }
            else if (cls.equals(Integer.class)
                     || cls.equals(int.class)) {
                result = Integer.valueOf(arg.trim());
            }
            else if (cls.equals(Long.class)
                     || cls.equals(long.class)) {
                result = Long.valueOf(arg.trim());
            }
            else if (cls.equals(Float.class)
                     || cls.equals(float.class)) {
                result = Float.valueOf(arg.trim());
            }
            else if (cls.equals(Double.class)
                     || cls.equals(double.class)) {
                result = Double.valueOf(arg.trim());
            }
            else if (cls.equals(ComponentReference.class)) {
                ComponentReference ref =
                    resolver.getComponentReference(arg.trim());
                if (ref == null)
                    throw new NoSuchReferenceException("The resolver " 
                                                       + resolver
                                                       + " has no reference '"
                                                       + arg + "'");
                result = ref;
            }
            else if (Component.class.isAssignableFrom(cls)) {
                ComponentReference ref =
                    resolver.getComponentReference(arg.trim());
                if (ref == null)
                    throw new NoSuchReferenceException("The resolver " 
                                                       + resolver
                                                       + " has no reference '"
                                                       + arg + "'");
                // Avoid requiring the user to wait for a component to become
                // available, in most cases.  In those cases where the
                // component creation is particularly slow, an explicit wait
                // can be added.
                // Note that this is not necessarily a wait for the component
                // to become visible, since menu items are not normally
                // visible even if they're available.
                result = waitForComponentAvailable(ref);
            }
            else if (cls.equals(String.class)) {
                result = arg;
            }
            else if (cls.isArray() && arg.trim().startsWith("[")) {
                arg = arg.trim();
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
            else if ((parser = getParser(cls)) != null) {
                result = parser.parse(arg.trim());
            }
            else {
                String msg = Strings.get("parser.conversion_error",
                                         new Object[] {
                                             arg.trim(),
                                             cls.getName()
                                         });
                throw new IllegalArgumentException(msg);
            }
            return result;
        }
        catch(NumberFormatException nfe) {
            String msg = Strings.get("parser.conversion_error",
                                     new Object[] {
                                         arg.trim(),
                                         cls.getName()
                                     });
            throw new IllegalArgumentException(msg);
        }
    }

    /** Evaluate the given set of arguments into the given set of types. */
    public static Object[] eval(Resolver resolver, 
                                String[] args, Class[] params) 
        throws IllegalArgumentException,
               NoSuchReferenceException,
               ComponentSearchException {
        Object[] plist = new Object[params.length];
        for(int i=0;i < plist.length;i++) {
            plist[i] = eval(resolver, args[i], params[i]);
        }
        return plist;
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

    // TODO: move this somewhere more appropriate; make public static, maybe
    // in ComponentReference
    private static Component waitForComponentAvailable(final ComponentReference ref)
        throws ComponentSearchException {
        try {
            ComponentTester tester = 
                ComponentTester.getTester(Component.class);

            tester.wait(new Condition() {
                public boolean test() {
                    try { ref.getComponent(); }
                    catch(ComponentNotFoundException e) { return false; }
                    catch(MultipleComponentsFoundException m) { }
                    return true;
                }
                public String toString() {
                    return ref + " to become available";
                }
            }, ComponentTester.componentDelay);
        }
        catch(WaitTimedOutError wto) {
            String msg = "Could not find " + ref + ": "
                + Step.toXMLString(ref);
            throw new ComponentNotFoundException(msg);
        }
        return ref.getComponent();
    }

    /** Convert a value into a String representation.  Handles null values and
        arrays.  Returns null if the String representation is the default
        class@pointer format.
    */
    public static String toString(Object value) {
        if (value == null)
            return NULL;
        if (value.getClass().isArray()) {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            for (int i=0;i < Array.getLength(value);i++) {
                Object o = Array.get(value, i);
                if (i > 0)
                    sb.append(",");
                sb.append(toString(o));
            }
            sb.append("]");
            return sb.toString();
        }
        String s = value.toString();
        if (s == null)
            return NULL;

        if (isDefaultToString(s))
            return DEFAULT_TOSTRING;
        return s;
    }

    /** Returns whether the given String is the default toString()
     * implementation for the given Object.
     */
    public static boolean isDefaultToString(String s) {
        if (s == null)
            return false;

        int at = s.indexOf("@");
        if (at != -1) {
            String hash = s.substring(at + 1, s.length());
            try {
                Integer.parseInt(hash, 16);
                return true;
            }
            catch(NumberFormatException e) {
            }
        }
        return false;
    }

}
