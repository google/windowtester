package abbot.script.swt;

import java.util.HashMap;

import org.jdom.Element;

import abbot.swt.Resolver;

/** Represents a comment.  No other function. */
public class Comment extends Step {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    private static final String USAGE = "<!-- [text] -->";

    public Comment(Resolver resolver, HashMap attributes) {
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
