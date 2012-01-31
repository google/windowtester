package abbot.script;

import java.awt.Component;
import java.util.*;
import java.util.Map;
import abbot.Log;
import abbot.finder.Hierarchy;
import abbot.i18n.Strings;
import abbot.script.Launch.ThreadedLaunchListener;

/** Provides a method of defining a single script as the UI application test
 * context for multiple scripts.  A script which uses a fixture step (as
 * opposed to an explicit launch) will only instantiate the fixture if it does
 * not already exist.<p>
 * A Fixture will only be run once for any consecutive group of
 * <code>Script</code>s that refer to it.  The {@link StepRunner} class is
 * normally used to control execution and will manage fixture setup/teardown
 * as needed. 
 */
public class Fixture extends Script implements UIContext {

    private static Fixture currentFixture = null;

    public Fixture(String filename, Hierarchy h) {
        super(filename, h);
    }
    
    /** Construct a <code>Fixture</code> from its XML attributes. */
    public Fixture(Resolver parent, Map attributes) {
        super(parent, attributes);
        setHierarchy(parent.getHierarchy());
    }

    /** Run the entire fixture, using the given runner as a controller/monitor. */
    public void launch(StepRunner runner) throws Throwable {
        runner.run(this);
    }

    /** @return Whether this fixture is currently launched. */
    public boolean isLaunched() {
        return equivalent(currentFixture);
    }

    /** Don't re-run if already launched. */
    protected void runStep(StepRunner runner) throws Throwable {
        if (!isLaunched()) {
            if (currentFixture != null)
                currentFixture.terminate();
            currentFixture = this;
            super.runStep(runner);
        }
    }

    public void terminate() {
        Log.debug("fixture terminate");
        if (equivalent(currentFixture)) {
            if (currentFixture != this) {
                currentFixture.terminate();
            }
            else {
                UIContext context = getUIContext();
                if (context != null)
                    context.terminate();
                currentFixture = null;
            }
        }
    }

    public String getXMLTag() { return TAG_FIXTURE; }
    
    public String getDefaultDescription() {
        String ext = isForked() ? " &" : "";
        String desc = Strings.get("fixture.desc",
                                  new Object[] { getFilename(), ext });
        return desc.indexOf(UNTITLED_FILE) != -1 ? UNTITLED : desc;
    }
    
    /** Two fixtures are equivalent if they have the same XML representation. */
    public boolean equivalent(UIContext f) {
        return f instanceof Fixture 
            && (equals(f) || getFullXMLString().equals(((Fixture)f).getFullXMLString()));
    }
}
