package abbot.script;

import java.util.Map;

import abbot.i18n.Strings;

/** Placeholder step to indicate to a script that it should terminate.
    Doesn't actually do anything itself.
 */

public class Terminate extends Step {

    private static final String USAGE = 
        "<terminate/>";

    public Terminate(Resolver resolver, Map attributes) {
        super(resolver, attributes);
    }

    public Terminate(Resolver resolver, String description) {
        super(resolver, description);
    }

    public void runStep() {
        // does nothing
    }

    public String getDefaultDescription() {
        return Strings.get("terminate.desc");
    }

    public String getUsage() { return USAGE; }

    public String getXMLTag() { return TAG_TERMINATE; }
}

