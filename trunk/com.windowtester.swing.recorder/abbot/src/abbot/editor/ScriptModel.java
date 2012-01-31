package abbot.editor;

import java.io.IOException;
import java.util.*;

import javax.swing.table.AbstractTableModel;

import org.jdom.Element;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.*;

/** Formats a Script for display in a table.  Keeps track of
 * "open" nodes to create a tree-like display
 * NOTE: this is a brute-force implementation with no attempts at
 * optimization.   But it's a very simple tree+table implementation.
 */
class ScriptModel extends AbstractTableModel {
    private HashSet openSequences = new HashSet();
    private HashMap parents = new HashMap();
    private Script script = null;
    private ArrayList rows = new ArrayList();

    /** Encapsulate information we need to manipulate a row.  Note that Entry
     * objects exist only for those steps which are "visible", i.e. children
     * of closed sequences have no Entry.
     */
    private class Entry implements XMLifiable {
        public Step step;
        public Sequence parent;
        public int nestingDepth;
        public Entry(Step step, Sequence parent, int nestingDepth) {
            this.step = step;
            this.parent = parent;
            this.nestingDepth = nestingDepth;
        }
        /** What to display. */
        public String toString() { 
            String str = step.toString();
            if ((step instanceof Script)
                && !step.getDescription().startsWith("Script")) {
                str += " - " + step.getDescription();
            }
            return str;
        }
        /** What to edit. */        
        public String toEditableString() {
            return step.toEditableString();
        }
        public Element toXML() {
            return step.toXML();
        }
    }

    public ScriptModel() {
        this(null);
    }

    public ScriptModel(Script script) {
        setScript(script);
    }

    private void layout(boolean scanParents) {
        if (scanParents)
            mapParents(script);
        rows.clear();
        if (script != null) {
            addSubRows(script, 0);
        }
        //Log.debug("Layout finished with " + rows.size() + " rows");
        fireTableDataChanged();
    }

    /** Remove the given step from the script. */
    public synchronized void removeStep(Step step) {
        getParent(step).removeStep(step);
        openSequences.remove(step);
        layout(true);
    }

    /** Remove all the given steps.  If any are not found, an exception is
        thrown before any changes are made. 
    */
    public synchronized void removeSteps(List steps) {
        Iterator iter = steps.iterator();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            getParent(step);
        }
        iter = steps.iterator();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            getParent(step).removeStep(step);
            openSequences.remove(step);
        }
        layout(true);
    }

    /** Insert the given step at the given index in its parent. */
    public synchronized void insertStep(Sequence parent, Step step, int index) {
        parent.addStep(index, step);
        layout(true);
    }

    /** Insert the steps into the given sequence at the given index. */
    public synchronized void insertSteps(Sequence parent, List steps, int index) {
        Iterator iter = steps.iterator();
        while (iter.hasNext()) {
            parent.addStep(index++, (Step)iter.next());
        }
        layout(true);
    }

    private Entry getEntry(int row) {
        if (row > rows.size() -1 || row < 0)
            throw new IllegalArgumentException("Row " + row
                                               + " out of bounds ("
                                               + rows.size() + " available)");
        return (Entry)rows.get(row);
    }
    
    /** Returns -1 if the step is not found or not visible. */
    public synchronized int getRowOf(Step step) {
        if (step != script) {
            //Log.debug("Checking " + rows.size() + " rows");
            for (int i=0;i < rows.size();i++) {
                Entry entry = getEntry(i);
                if (entry.step.equals(step))
                    return i;
                else
                    Log.debug("Not in " + entry.step);
            }
            Log.debug("Step " + step + " not found in (maybe not visible)");
        }
        return -1;
    }
    
    /** Return whether the given row is "open". */
    public synchronized boolean isOpen(int row) {
        return openSequences.contains(getEntry(row).step);
    }
    
    public synchronized boolean isOpen(Step step) {
        return openSequences.contains(step);
    }

    /** Toggle the open state of the node, if it's a sequence. */
    public synchronized void toggle(int row) {
        Step step = getEntry(row).step;
        if (step instanceof Sequence) {
            if (openSequences.contains(step))
                openSequences.remove(step);
            else
                openSequences.add(step);
            layout(false);
        }
    }

    /** Set the script to display.  Don't allow any model accesses until
        this method has completed.
    */
    public synchronized void setScript(Script script) {
        //Log.debug("Setting table model script to " + script);
        this.script = script;
        openSequences.clear();
        if (script != null) {
            openSequences.add(script);
        }
        layout(true);
        //Log.debug("Model has " + rows.size() + " rows");
    }
    public synchronized int getRowCount() { 
        if (script == null) 
            return 0;
        return rows.size();
    }
    public int getColumnCount() { return 1; }
    public synchronized Step getStepAt(int row) {
        return getEntry(row).step;
    }

    private void validate(int row, int col) {
        if (row < 0 || row > getRowCount()-1)
            throw new IllegalArgumentException("Invalid row " + row);
        if (col != 0)
            throw new IllegalArgumentException("Invalid column " + col);
    }

    /** Returns the step at the given row. */
    public synchronized Object getValueAt(int row, int col) {
        validate(row, col);
        return getStepAt(row);
    }
    /** Assumes value is XML for a script step.
     */
    // FIXME: I don't think this is used any longer now that editors are
    // available for all script steps.
    public synchronized void setValueAt(Object value, int row, int col) {
        validate(row, col);
        if (col == 0) {
            //Log.debug("Setting value at " + row + " to " + value);
            Entry entry = getEntry(row);
            if (entry.step instanceof Script) {
                // FIXME maybe use a file chooser instead?
                //Log.debug("Set script value to " + value);
                Script old = (Script)entry.step;
                Script step = new Script((String)value, script.getHierarchy());
                step.setRelativeTo(old.getRelativeTo());
                Sequence parent = entry.parent != null
                    ? entry.parent : script;
                parent.setStep(parent.indexOf(old), step);
                layout(true);
            }
            else if (entry.step instanceof Sequence) {
                String desc = (String)value;
                if (!"".equals(desc)
                    && !entry.step.getDescription().equals(desc)) {
                    entry.step.setDescription(desc);
                }
            }
            else {
                try {
                    Step step = Step.createStep(script, (String)value);
                    Sequence parent = entry.parent != null 
                        ? entry.parent : script;
                    parent.setStep(parent.indexOf(entry.step), step); 
                    layout(true);
                }
                catch(IllegalArgumentException e) {
                    Log.warn(e);
                }
                catch(InvalidScriptException e) {
                    // Edit rejected
                    Log.warn(e);
                }
                catch(IOException e) {
                    Log.warn(e);
                }
            }
        }
    }
    public String getColumnName(int col) { return ""; }
    public Class getColumnClass(int col) {
        if (col == 0)
            return Entry.class;
        return Object.class;
    }
    public Script getScript() { return script; }

    public synchronized int getNestingDepthAt(int row) {
        return row < 0 || row >= getRowCount()
            ? 0 : getEntry(row).nestingDepth;
    }

    public synchronized Script getScriptOf(int row) {
        validate(row, 0);
        Entry entry = getEntry(row);
        Sequence parent = entry.parent;
        while (!(parent instanceof Script))
            parent = getParent(parent);
        return (Script)parent;
    }

    /** Return the parent sequence of the given step. */
    public synchronized Sequence getParent(Step step) {
        Sequence seq = (Sequence)parents.get(step);
        if (seq == null) {
            throw new IllegalArgumentException("Step " + step
                                               + " not found in "
                                               + getScript());
        }
        return seq;
    }

    /** Keep track of the parent for any given step to aid in editing. */
    private void mapParents(Sequence seq) {
        if (seq == null)
            return;
        else if (seq == getScript()) {
            parents.clear();
        }
        Iterator iter = seq.steps().iterator();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            parents.put(step, seq);
            if (step instanceof Sequence) {
                mapParents((Sequence)step);
            }
        }
    }

    /** Add row entries corresponding to the contents of the given sequence
     * if it's toggled open.
     */ 
    private void addSubRows(Sequence seq, int level) {
        if (openSequences.contains(seq)) {
            //Log.debug("Adding " + seq.steps().size() + " rows");
            Iterator iter = seq.steps().iterator();
            while (iter.hasNext()) {
                Step step = (Step)iter.next();
                //Log.debug("Adding " + step);
                rows.add(new Entry(step, seq, level));
                if (step instanceof Sequence) {
                    addSubRows((Sequence)step, level + 1);
                }
            }
        }
    }

    /** Move the given steps and all between them to the new location.
        If the steps are being moved later in the same sequence, the index
        represents the target index <i>before</i> the move.
     */
    public synchronized void moveSteps(Sequence parent, List steps, int index) {
        Step indexStep = index < parent.size() ? parent.getStep(index) : null;
        // Remove all, then insert all; otherwise moving steps down in a
        // sequence would fail
        Iterator iter = steps.iterator();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            getParent(step).removeStep(step);
        }
        iter = steps.iterator();
        index = indexStep != null ? parent.indexOf(indexStep) : parent.size();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            parent.addStep(index++, step);
        }
        layout(true);
    }
}
