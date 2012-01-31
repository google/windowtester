package abbot.script.parsers;

import java.io.File;

/** Convert a {@link String} into a {@link java.io.File}.  May optionally
 * provide a file prefix indicating a root directory for relative paths.
 */
public class FileParser implements Parser {
    public FileParser() { }
    public Object parse(String input) throws IllegalArgumentException {
        File file = new File(input);
        if (!file.isAbsolute()) {
            file = new File(relativeTo() + File.separator + input);
        }
        return file;
    }
    public String relativeTo() {
        return "";
    }
}
