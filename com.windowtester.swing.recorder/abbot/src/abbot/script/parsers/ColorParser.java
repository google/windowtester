package abbot.script.parsers;

import java.awt.Color;

/** Convert a {@link String} into a {@link Color}. */
public class ColorParser implements Parser {
    public ColorParser() { }
    public Object parse(String input) throws IllegalArgumentException {
        // NOTE: may want to provide additional parsing, e.g.
        // #00CC00
        // R:G:B
        // Color.toString (although this is not guaranteed to be consistent)
        // or some other format
        Color c = Color.getColor(input);
        if (c != null)
            return c;
        throw new IllegalArgumentException("Can't convert '" + input
                                           + "' to java.awt.Color");
    }
}
