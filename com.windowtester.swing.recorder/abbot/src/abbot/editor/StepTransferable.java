package abbot.editor;

import java.awt.datatransfer.*;
import java.util.*;

import abbot.script.Step;

public class StepTransferable implements Transferable {

    public static final DataFlavor STEP_FLAVOR =
        new DataFlavor("application/x-java-serialized-object;class=abbot.script.Step", "Abbot script step");
    public static final DataFlavor STEP_LIST_FLAVOR =
        new DataFlavor("application/x-java-serialized-object;class=java.util.ArrayList", "List of Abbot script steps");

    // A single step is available as itself or as a list
    private static final DataFlavor[] FLAVORS = {
        STEP_FLAVOR, STEP_LIST_FLAVOR, 
    };

    // Can't get a list as a single step
    private static final DataFlavor[] LIST_FLAVORS = {
        STEP_LIST_FLAVOR
    };

    private static List FLAVOR_LIST = Arrays.asList(FLAVORS);
    private static List LIST_FLAVOR_LIST = Arrays.asList(LIST_FLAVORS);

    private Step step;
    private List steps;
    private List flavorList;
    private DataFlavor[] flavors;

    public StepTransferable(Step step) {
        this.step = step;
        this.steps = new ArrayList();
        steps.add(step);
        flavorList = FLAVOR_LIST;
        flavors = FLAVORS;
    }

    public StepTransferable(List steps) {
        this.step = null;
        this.steps = steps;
        flavorList = LIST_FLAVOR_LIST;
        flavors = LIST_FLAVORS;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavorList.contains(flavor);
    }

    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException {
        if (flavor.isMimeTypeEqual(STEP_FLAVOR.getMimeType())) {
            if (step != null)
                return step;
        }
        else if (flavor.isMimeTypeEqual(STEP_LIST_FLAVOR.getMimeType())) {
            return steps;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    public String toString() { 
        return "Transferable "
            + (step != null ? step.toString() : "List of Steps");
    }
}
