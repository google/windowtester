package abbot.script.parsers;

import javax.swing.tree.TreePath;

import abbot.i18n.Strings;
import abbot.script.ArgumentParser;

/** Convert a {@link String} into a {@link javax.swing.tree.TreePath}.  */
public class TreePathParser implements Parser {

    /** The string representation of a TreePath is what is usually generated
        by its toString method, e.g.
        <p>
        [root, parent, child]
        <p>
        Nodes which contain a comma need to have that comma preceded by a
        backslash to avoid it being interpreted as two separate nodes.<p>
        NOTE: The returned TreePath is only a TreePath constructed of Strings;
        it requires further manipulation to be turned into a true TreePath as
        it relates to a given Tree.
    */
    public Object parse(String input) throws IllegalArgumentException {
        if (!(input.startsWith("[") && input.endsWith("]"))) {
            String msg = Strings.get("parser.treepath.bad_format",
                                     new Object[] { input });
            throw new IllegalArgumentException(msg);
        }
        input = input.substring(1, input.length()-1);
        // Use our existing utility for parsing a comma-separated list
        String[] nodeNames = ArgumentParser.parseArgumentList(input);
        // Strip off leading space, if there is one
        for (int i=0;i < nodeNames.length;i++) {
            if (nodeNames[i] != null && nodeNames[i].startsWith(" "))
                nodeNames[i] = nodeNames[i].substring(1);
        }
        TreePath path = new TreePath(nodeNames);
        return path;
    }
}
