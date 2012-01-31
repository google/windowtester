package abbot.script.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import abbot.script.InvalidScriptException;
import abbot.swt.Resolver;
import abbot.i18n.Strings;

/** Script step which groups a sequence of other Steps.  The sub-Steps have a
 * fixed order and are executed in the order contained in the sequence.
 * Events sent by sub-Steps are propagated by this one.
 */
public class Sequence extends Step {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    private static final String USAGE = "<sequence ...>...</sequence>";
    private ArrayList sequence = new ArrayList();

    public Sequence(Resolver resolver, Element el, HashMap atts) {
        super(resolver, atts);
        try {
            parseChildren(el);
        }
        catch(InvalidScriptException ise) {
            setScriptError(ise);
        }
    }
    
    public Sequence(Resolver resolver, HashMap atts) {
        super(resolver, atts);
    }

    public Sequence(Resolver resolver, String desc) {
        this(resolver, desc, null);
    }

    /** Create an aggregate from existing AWTEvents. */
    public Sequence(Resolver resolver, String desc, List steps) {
        super(resolver, desc);
        if (steps != null) {
            Iterator iter = steps.iterator();
            synchronized(sequence) {
                while(iter.hasNext()) {
                    addStep((Step)iter.next());
                }
            }
        }
    }

    protected void parseChild(Element el) throws InvalidScriptException {
        Step step = createStep(getResolver(), el);
        addStep(step);
    }

    protected void parseChildren(Element el) throws InvalidScriptException {
        synchronized(sequence) {
            Iterator iter = el.getContent().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                if (obj instanceof Element)
                    parseChild((Element)obj);
                else if (obj instanceof org.jdom.Comment) {
                    String text = ((org.jdom.Comment)obj).getText();
                    addStep(new abbot.script.swt.Comment(getResolver(), text));
                }
            }
        }
    }

    protected String getDefaultDescription() { 
        return Strings.get("SequenceDesc",
                           new Object[] { String.valueOf(size()) });
    }
    public String getXMLTag() { return TAG_SEQUENCE; }
    protected Element addContent(Element el) {
        ArrayList seq;
        synchronized(sequence) {
            seq = (ArrayList)sequence.clone();
        }
        Iterator iter = seq.iterator();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            if (step instanceof abbot.script.swt.Comment)
                el.addContent(new org.jdom.Comment(step.getDescription()));
            else
                el.addContent(step.toXML());
        }
        return el;
    }

    /** Returns a string describing the proper XML usage for this class. */
    public String getUsage() { return USAGE; }

    /** Only thing directly editable on a sequence is its description. */
    public String toEditableString() { return getDescription(); }

    /** Process each event in our list. */
    protected void runStep() throws Throwable {
        //runStep(null);
    	//TODO implement runStep()
    }

//    /** Process each event in our list, using the given runner. */
//    protected void runStep(StepRunner runner) throws Throwable {
//        Iterator iter;
//        synchronized(sequence) {
//            iter = ((ArrayList)sequence.clone()).iterator();
//        }
//        if (runner != null) {
//            while (iter.hasNext() && !runner.stopped()) {
//                runner.runStep((Step)iter.next());
//            }
//        }
//        else {
//            while (iter.hasNext()) {
//                ((Step)iter.next()).run();
//            }
//        }
//    }

    /** Returns the number of steps contained in this one. */
    public int size() { 
        synchronized(sequence) {
            return sequence.size();
        }
    }
    /** Remove all stepchildren. More effective than Cinderella's
        stepmother. */ 
    public void clear() { 
        synchronized(sequence) {
            sequence.clear();
        }
    }
    /** Returns a list of the steps contained in this one. */
    public java.util.List steps() { 
        return sequence;
    }
    /** Returns the index of the given step in the sequence, or -1 if the step
        is not in the sequence. */
    public int indexOf(Step step) {
        synchronized(sequence) {
            return sequence.indexOf(step);
        }
    }
    /** Return the step at the given index in the sequence. */
    public Step getStep(int index) { 
        synchronized(sequence) {
            return (Step)sequence.get(index);
        }
    }
    /** Inserts a step at the given index in the sequence. */
    public void addStep(int index, Step step) {
        synchronized(sequence) {
            sequence.add(index, step);
        }
    }
    /** Adds a step to the end of the sequence. */
    public void addStep(Step step) {
        synchronized(sequence) {
            sequence.add(step);
        }
    }
    /** Replaces the step at the given index. */
    public void setStep(int index, Step step) {
        synchronized(sequence) {
            sequence.set(index, step);
        }
    }
    /** Removes the step if it exists in the sequence. */
    public void removeStep(Step step) {
        synchronized(sequence) {
            sequence.remove(step);
        }
    }
    /** Removes the step at the given index in the sequence. */
    public void removeStep(int index) {
        synchronized(sequence) {
            sequence.remove(index);
        }
    }
}
