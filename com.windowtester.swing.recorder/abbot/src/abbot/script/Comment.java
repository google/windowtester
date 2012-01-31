package abbot.script;

import java.util.Map;

import org.jdom.Element;


/** Represents a comment.  No other function. */
public class Comment extends Step {

    private static final String USAGE = "<!-- [text] -->";

    public Comment(Resolver resolver, Map attributes) {
        super(resolver, attributes);
    }

    public Comment(Resolver resolver, String description) {
        super(resolver, description);
    }
    
    /** Default to whitespace. */
    public String getDefaultDescription() { return ""; }

    public String toString() { return "# " + getDescription(); }

    public String getUsage() { return USAGE; }

    /** This is only used to generate the title label for the editor. */
    public String getXMLTag() { return TAG_COMMENT; }

    public Element toXML() {
        throw new RuntimeException("Comments are not elements");
    }

    /** Main run step. */
    protected void runStep() { }
}
