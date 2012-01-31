package abbot.script.parsers;

/** This interface provides a method for converting a {@link String} into some
 * destination class type.  This interface was designed as an extensible
 * method of converting {@link String}s into arbitrary target classes when
 * parsing scripted arguments to methods.  When a script is run and a method is
 * resolved, the {@link String} arguments are converted to the classes
 * required for the method invocation.  Built-in conversions are provided for
 * {@link abbot.script.ComponentReference}s and all the basic types, including
 * arrays.<p>
 * @see ColorParser
 * @see FileParser
 * @see TreePathParser
 */
public interface Parser {
    Object parse(String string) throws IllegalArgumentException;
}
