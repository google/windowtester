package abbot.editor.recorder;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import abbot.BugReport;
import abbot.Log;
import abbot.Platform;
import abbot.i18n.Strings;
import abbot.script.Action;
import abbot.script.Assert;
import abbot.script.Event;
import abbot.script.Resolver;
import abbot.script.Sequence;
import abbot.script.Step;
import abbot.script.XMLConstants;
import abbot.tester.Robot;
import abbot.util.AWT;

/** 
 * Provides recording of raw AWT events and high-level semantic events.
 * This is the main controller for any SemanticRecorder objects.
 */

// TODO: Are there other instances (cf JInternalFrame) where we'd like this
// recorder to be the listener to other events?
// TODO: add internal frame listener, re-post events when heard
// TODO: discard events on LAF pieces of internal frames
// TODO: extract filters as plugins
// TODO: run all semantic recorder tests through this class, using canned
// event streams instead of robot-generated events; keep the robot-generated
// event streams, though, to test whether the stream has changed

public class EventRecorder
    extends Recorder implements SemanticEvents {
    
    private static final String ANY_KEY = null;
    private static final int EITHER = 0;
    private static final int PRESS = 1;
    private static final int RELEASE = 2;
    private boolean captureMotion;
    private long lastStepTime;
    ArrayList steps = new ArrayList();
    
    protected AWTEvent capturedEvent;

    /** Put all built-in recorder classes here.  Don't worry though, 'cause if
     * it doesn't get added here it'll get found dynamically.
     */
    protected static final Class[] recorderClasses = {
        AbstractButton.class,
        Button.class,
        Component.class,
        Container.class,
        Dialog.class,
        Frame.class,
        JComboBox.class,
        JComponent.class,
        JInternalFrame.class,
        JList.class,
        JMenuItem.class,
        JTabbedPane.class,
        JTable.class,
        JTextComponent.class,
        JTree.class,
        Window.class,
    };

    /** Create a Recorder for use in capturing raw AWTEvents.  Indicate
     * whether mouse motion should be captured, and what semantic event type
     * to capture.
     */
    public EventRecorder(Resolver resolver, boolean captureMotion) { 
        super(resolver);
        this.captureMotion = captureMotion;
        // Install existing semantic recorders
        for (int i=0;i < recorderClasses.length;i++) {
            getSemanticRecorder(recorderClasses[i]);
        }
    }

    /** Return the name of the type of GUI action to be recorded. */
    public String toString() {
        return captureMotion 
            ? Strings.get("actions.capture-all")
            : Strings.get("actions.capture");
    }

    public void start() {
        super.start();
        steps.clear();
        MessageFormat mf = new MessageFormat(Strings.get("RecordingX"));
        setStatus(mf.format(new Object[] { toString() }));
        lastStepTime = getLastEventTime();
    }

    private boolean isKey(Step step, String code, int type) {
        boolean match = false;
        if (step instanceof Event) {
            Event se = (Event)step;
            match = "KeyEvent".equals(se.getType())
                && (type == EITHER
                    || (type == PRESS
                        && "KEY_PRESSED".equals(se.getKind()))
                    || (type == RELEASE
                        && "KEY_RELEASED".equals(se.getKind())))
                && (code == ANY_KEY
                    || code.equals(se.getAttribute(XMLConstants.TAG_KEYCODE)));
        }
        return match;
    }

    private boolean isKeyString(Step step) {
        return (step instanceof Action)
            && ((Action)step).getMethodName().equals("actionKeyString");
    }

    private boolean isKeyStroke(Step step, String keycode) {
        if (step instanceof Action) {
            Action action = (Action)step;
            if (action.getMethodName().equals("actionKeyStroke")) {
                String[] args = action.getArguments();
                return (keycode == ANY_KEY
                        || (args.length > 1 && args[1].equals(keycode))
                        || (keycode.startsWith("VK_NUMPAD") 
                            && args[1].equals("VK_" + keycode.substring(9))));
            }
        }
        return false;
    }

    private void removeTerminalShift() {
        // Remove the terminal SHIFT keypress
        if (steps.size() > 0) {
            Step step = (Step)steps.get(steps.size()-1);
            while (isKey(step, "VK_SHIFT", PRESS)) {
                steps.remove(step);
                if (steps.size() == 0)
                    break;
                step = (Step)steps.get(steps.size()-1);
            }
        }
    }

    /** Eliminate redundant modifier keys surrounding keystrokes or
     * keystrings.
     */
    private void removeExtraModifiers() {
        setStatus("Removing extra modifiers");
        for (int i=0;i < steps.size();i++) {
            Step step = (Step)steps.get(i);
            if (isKey(step, ANY_KEY, PRESS)) {
                Event se = (Event)step;
                String cs = se.getAttribute(XMLConstants.TAG_KEYCODE);
                int code = AWT.getKeyCode(cs);
                boolean remove = false;
                boolean foundKeyStroke = false;
                if (AWT.isModifier(code)) {
                    for (int j=i+1;j < steps.size();j++) {
                        Step next = (Step)steps.get(j);
                        if (isKey(next, cs, RELEASE)) {
                            if (foundKeyStroke) {
                                steps.remove(j);
                                remove = true;
                            }
                            break;
                        }
                        else if (isKeyStroke(next, ANY_KEY)
                                 || isKeyString(next)) {
                            foundKeyStroke = true;
                            remove = true;
                        }
                        else if (!isKey(next, ANY_KEY, EITHER)) {
                            break;
                        }
                    }
                }
                if (remove) {
                    steps.remove(i--);
                }
            }
        }
    }

    /** Combine multiple keystroke actions into keystring actions. */
    private void coalesceKeyStrings() {
        setStatus("Coalescing key strings");
        for (int i=0;i < steps.size();i++) {
            Step step = (Step)steps.get(i);
            if (isKeyString(step)) {
                int j = i;
                while (++j < steps.size()) {
                    Step next = (Step)steps.get(j);
                    if (isKeyString(next)) {
                        Action action = (Action)step;
                        String[] args1 = action.getArguments();
                        String[] args2 = ((Action)next).getArguments();
                        action.setArguments(new String[] {
                            args1[0], args1[1] + args2[1]
                        });
                        setStatus("Joining '" + args1[1]
                                  + "' and '" + args2[1] + "'");
                        steps.remove(j--);
                    }
                    else {
                        setStatus("Next step is not a key string: " + next);
                        break;
                    }
                }
            }
        }
    }

    /** Eliminate redundant key press/release events surrounding a keytyped
     * event.  
     */
    private void coalesceKeyEvents() {
        setStatus("Coalescing key events");
        for (int i=0;i < steps.size();i++) {
            Step step = (Step)steps.get(i);
            if (isKey(step, ANY_KEY, PRESS)) {
                // In the case of modifiers, remove only if the presence of
                // the key down/up is redundant.
                Event se = (Event)step;
                String cs = se.getAttribute(XMLConstants.TAG_KEYCODE);
                int code = AWT.getKeyCode(cs);
                // OSX option modifier should be ignored, since it is used to
                // generate input method events.
                boolean isOSXOption =
                    Platform.isOSX() && code == KeyEvent.VK_ALT;
                if (AWT.isModifier(code) && !isOSXOption)
                    continue;

                // In the case of non-modifier keys, walk the steps until we
                // find the key release, then optionally replace the key press
                // with a keystroke, or remove it if the keystroke was already
                // recorded.  This sorts out jumbled key press/release events.
                boolean foundKeyStroke = false;
                boolean foundRelease = false;
                for (int j=i+1;j < steps.size();j++) {
                    Step next = (Step)steps.get(j);
                    // If we find the release, remove it and this
                    if (isKey(next, cs, RELEASE)) {
                        foundRelease = true;
                        String target = ((Event)next).getComponentID();
                        steps.remove(j);
                        steps.remove(i);
                        // Add a keystroke only if we didn't find any key
                        // input between press and release (except on OSX,
                        // where the option key generates input method events
                        // which aren't recorded). 
                        if (!foundKeyStroke && !isOSXOption) {
                            String mods = 
                                se.getAttribute(XMLConstants.TAG_MODIFIERS);
                            String[] args = 
                                (mods == null || "0".equals(mods)
                                 ? new String[] { target, cs }
                                 : new String[] { target, cs, mods});
                            Step typed =
                                new Action(getResolver(), 
                                           null, "actionKeyStroke", args);
                            steps.add(i, typed);
                            setStatus("Insert artifical " + typed);
                        }
                        else {
                            setStatus("Removed redundant key events ("
                                      + cs + ")");
                            --i;
                        }
                        break;
                    }
                    else if (isKeyStroke(next, ANY_KEY)
                             || isKeyString(next)) {
                        foundKeyStroke = true;
                        // If it's a numpad keycode, use the numpad
                        // keycode instead of the resulting numeric character
                        // keystroke. 
                        if (cs.startsWith("VK_NUMPAD")) {
                            foundKeyStroke = false;
                            steps.remove(j--);
                        }
                    }
                }
                // We don't like standalone key presses
                if (!foundRelease) {
                    setStatus("Removed extraneous key press (" + cs + ")");
                    steps.remove(i--);
                }
            }
        }
    }

    // Required for OS X, remove modifier keys when they're only used to
    // invoke MB2/3
    private boolean pruneButtonModifier = false;
    private int lastButton = 0;
    /** Used only on Mac OS, to remove key modifiers that are used to simulate
     * mouse buttons 2 and 3.  Returns whether the event should be ignored.
     */
    private boolean pruneClickModifiers(AWTEvent event) {
        lastButton = 0;
        boolean ignoreEvent = false;
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent me = (MouseEvent)event;
            int buttons = me.getModifiers() 
                & (MouseEvent.BUTTON2_MASK|MouseEvent.BUTTON3_MASK);
            pruneButtonModifier = buttons != 0;
            lastButton = buttons;
        }
        else if (event.getID() == KeyEvent.KEY_RELEASED
                 && pruneButtonModifier) {
            pruneButtonModifier = false;
            KeyEvent ke = (KeyEvent)event;
            int code = ke.getKeyCode();
            if ((code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_ALT
                 && (lastButton & MouseEvent.BUTTON2_MASK) != 0)
                || (code == KeyEvent.VK_META 
                    && (lastButton & MouseEvent.BUTTON3_MASK) != 0)) {
                if (steps.size() > 1) {
                    Step step = (Step)steps.get(steps.size()-2);
                    if ((code == KeyEvent.VK_CONTROL 
                         && isKey(step, "VK_CONTROL", PRESS)
                        || (code == KeyEvent.VK_ALT
                            && isKey(step, "VK_ALT", PRESS)))
                        || (code == KeyEvent.VK_META
                            && isKey(step, "VK_META", PRESS))) {
                        // might be another one
                        steps.remove(steps.size()-2);
                        pruneButtonModifier = true;
                        ignoreEvent = true;
                    }
                }
            }
        }
        return ignoreEvent;
    }

    /** Ignore any key presses at the end of the recording. */
    private void removeTrailingKeyPresses() {
        while (steps.size() > 0
               && isKey((Step)steps.get(steps.size()-1), ANY_KEY, PRESS)) {
            steps.remove(steps.size()-1);
        }
    }

    /** Remove keypress events preceding and following ActionMap actions. */
    private void removeShortcutModifierKeyPresses() {
        int current = 0;
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        String modifier = AWT.getKeyCode(AWT.maskToKeyCode(mask));
        while (current < steps.size()) {
            Step step = (Step)steps.get(current);
            if (isKey(step, modifier, PRESS)) {
                Log.debug("Found possible extraneous modifier");
                int keyDown = current;
                Action action = null;
                while (++current < steps.size()) {
                    step = (Step)steps.get(current);
                    if (step instanceof Action) {
                        if ("actionActionMap".
                            equals(((Action)step).getMethodName())) {
                            action = (Action)step;
                            continue;
                        }
                    }
                    else if (isKey(step, modifier, RELEASE)) {
                        if (action != null) {
                            Log.debug("Removing extraneous shortcut modifier");
                            steps.remove(current);
                            steps.remove(keyDown);
                            current = keyDown - 1;
                        }
                    }
                    break;
                }
            }
            ++current;
        }
    }

    /** Insert an arbitrary script step into the currently recorded stream. */
    public void insertStep(Step step) {
        steps.add(step);
        if ((step instanceof Assert)
            && ((Assert)step).getMethodName().equals("assertFrameShowing")) {
            long timeout = ((Assert)step).getTimeout();
            long delta = System.currentTimeMillis() - lastStepTime;
            if (delta > timeout)
                timeout += delta;
            ((Assert)step).setTimeout(timeout);
        }
        lastStepTime = getLastEventTime();
    }

    /**
     * Return a sequence containing all the semantic and basic events captured
     * thus far.
     */
    protected Step createStep() {
        removeTerminalShift();
        coalesceKeyEvents();
        removeExtraModifiers();
        coalesceKeyStrings();
        removeShortcutModifierKeyPresses();
        removeTrailingKeyPresses();

        return new Sequence(getResolver(), null, steps);
    }

    /** The current semantic recorder, if any. */
    private SemanticRecorder semanticRecorder = null;

    /** Return whether an event was generated.  Assumes a SemanticRecorder is
        active.
        @throws RecordingFailedException if an error was encountered.
    */
    private boolean saveSemanticEvent() throws RecordingFailedException {
        Log.log("Storing event from current semantic recorder");
        try {
            Step step = semanticRecorder.getStep();
            if (step != null) {
                insertStep(step);
                setStatus("Added " + step);
            }
            else {
                setStatus("No semantic event found, events skipped");
            }
            semanticRecorder = null;
            return step != null;
        }
        catch(BugReport bug) {
        	// changed to windowtester exception
        	//   throw new RecordingFailedException(bug);
        	throw new com.windowtester.swing.recorder.RecordingFailedException(bug);
        }
        catch(Exception e) {
            Log.log("Recording failed when saving action: " + e);
            // 1/3/07 kp: change message to windowtester message
            //String msg = Strings.get("editor.recording.exception");
            String msg = "Windowtester recording exception";
       //     throw new RecordingFailedException(new BugReport(msg, e));
            throw new com.windowtester.swing.recorder.RecordingFailedException(new BugReport(msg, e));
        }
    }

    public void terminate() throws RecordingFailedException {
        Log.log("EventRecorder terminated");
        if (semanticRecorder != null) {
            saveSemanticEvent();
        }
    }

    /** Handle an event.  This can either be ignored or contribute to the
     * recording.
     * For a given event, if no current semantic recorder is active,
     * select one based on the event's component.  If the semantic recorder
     * accepts the event, then it is used to consume each subsequent event,
     * until its recordEvent method returns true, indicating that the semantic
     * event has completed.
     */ 
    protected void recordEvent(java.awt.AWTEvent event)
        throws RecordingFailedException {

        // Discard any key/button release events at the start of the recording.
        if (steps.size() == 0
            && event.getID() == KeyEvent.KEY_RELEASED) {
            Log.log("Ignoring initial release event");
            return;
        }

        SemanticRecorder newRecorder = null;

        // Process extraneous key modifiers used to simulate mouse buttons
        // Only check events while we have no semantic recorder, though,
        // because we wish to ignore everything between the modifiers
        if (Platform.isMacintosh() && semanticRecorder == null) {
            if (pruneClickModifiers(event))
                return;
        }

        if (semanticRecorder == null) {
            SemanticRecorder sr = (event.getSource() instanceof Component)
                ? getSemanticRecorder((Component)event.getSource())
                // Use ComponentRecorder for MenuComponents
                : getSemanticRecorder(Component.class);
            if (sr.accept(event)) {
                semanticRecorder = newRecorder = sr;
                setStatus("Recording semantic event with " + sr);
                if (event.getSource() instanceof JInternalFrame) {
                    // Ideally, adding an extra listener would be done by the
                    // JInternalFrameRecorder, but the object needs more state
                    // than is available to the recorder (notably to be able
                    // to send events to the primary recorder).  If something
                    // else turns up similar to this, then the EventRecorder
                    // should be made available to the semantic recorders.
                    //
                    // Must add a listener, since COMPONENT_HIDDEN is not sent
                    // on JInternalFrame close (1.4.1).
                    JInternalFrame f = (JInternalFrame)event.getSource();
                    new InternalFrameWatcher(f);
                }
            }
        }

        // If we're currently recording a semantic event, continue to do so
        if (semanticRecorder != null) {
            boolean consumed = semanticRecorder.record(event);
            boolean finished = semanticRecorder.isFinished();
            if (finished) {
                Log.debug("Semantic recorder is finished");
                saveSemanticEvent();
            }
            // If not consumed, need to check for semantic recorder (again)
            // (but avoid recursing indefinitely)
            if (!consumed && newRecorder == null) {
                Log.debug("Event was not consumed, parse it again");
                recordEvent(event);
            }
        }
        else {
            captureRawEvent(event);
        }
    }

    /** Capture the given event as a raw event. */
    private void captureRawEvent(AWTEvent event) {

        // FIXME maybe measure time delay between events and insert delay
        // events? 
        int id = event.getID();
        boolean capture = false;
        switch(id) {
        case MouseEvent.MOUSE_PRESSED:
        case MouseEvent.MOUSE_RELEASED:
            capture = true;
            break;
        case KeyEvent.KEY_PRESSED:
        case KeyEvent.KEY_RELEASED:
            KeyEvent e = (KeyEvent)event;
            capture = e.getKeyCode() != KeyEvent.VK_UNDEFINED;
            if (!capture) {
                Log.warn("VM bug: no valid keycode on key "
                         + (id == KeyEvent.KEY_PRESSED ? "press" : "release"));
            }
            break;
        case MouseEvent.MOUSE_ENTERED:
        case MouseEvent.MOUSE_EXITED:
   //     case MouseEvent.MOUSE_MOVED:
        case MouseEvent.MOUSE_DRAGGED:
            capture = captureMotion;
            break;
        default:
            break;
        }
        if (capture) {
            Event step = new Event(getResolver(), null, event);
            capturedEvent = event;
            insertStep(step);
            setStatus("Added event " + step);
        }
    }

    /** Events of interest when recording all actions. */
    public static final long RECORDING_EVENT_MASK = 
        AWTEvent.MOUSE_EVENT_MASK
        | AWTEvent.MOUSE_MOTION_EVENT_MASK
        | AWTEvent.KEY_EVENT_MASK
        | AWTEvent.WINDOW_EVENT_MASK
        /*| AWTEvent.PAINT_EVENT_MASK*/
        /*| AWTEvent.HIERARCHY_EVENT_MASK
          | AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK*/
        | AWTEvent.COMPONENT_EVENT_MASK
        | AWTEvent.FOCUS_EVENT_MASK
        // required for non-standard input 
        | AWTEvent.INPUT_METHOD_EVENT_MASK
        // For java.awt.Choice selections
        | AWTEvent.ITEM_EVENT_MASK
        // required to capture MenuItem actions
        | AWTEvent.ACTION_EVENT_MASK;

    /** Return the events of interest to this Recorder.  */
    public long getEventMask() { 
        return RECORDING_EVENT_MASK;
    }

    /** Maps component classes to corresponding semantic recorders. */
    private HashMap semanticRecorders = new HashMap();

    /** Return the semantic recorder for the given component. */
    private SemanticRecorder getSemanticRecorder(Component comp) {
        // FIXME extract into AWT.getLAFParent?
        // Account for LAF implementations that use a JButton on top
        // of the combo box
        if ((comp instanceof JButton)
            && (comp.getParent() instanceof JComboBox)) {
            comp = comp.getParent();
        }
        // Account for LAF components of JInternalFrame
        else if (AWT.isInternalFrameDecoration(comp)) {
            while (!(comp instanceof JInternalFrame))
                comp = comp.getParent();
        }
        return getSemanticRecorder(comp.getClass());
    }

    /** Return the semantic recorder for the given component class. */
    protected SemanticRecorder getSemanticRecorder(Class cls) {
   // 	System.out.println("getting recorder for: " + cls);
        if (!(Component.class.isAssignableFrom(cls))) {
            throw new IllegalArgumentException("Class (" + cls + ") must derive from "
                                               + "Component");
        }
        SemanticRecorder sr = (SemanticRecorder)semanticRecorders.get(cls);
        if (sr == null) {
            Class ccls = Robot.getCanonicalClass(cls);
            if (ccls != cls) {
                sr = getSemanticRecorder(ccls);
                // Additionally cache the mapping from the non-canonical class
                semanticRecorders.put(cls, sr);
                return sr;
            }
            String cname = Robot.simpleClassName(cls);
            try {
                //cname = "abbot.editor.recorder." + cname + "Recorder";
            	cname = getRecoderName(cname);
                Class recorderClass = Class.forName(cname);
                Constructor ctor = recorderClass.getConstructor(new Class[] {
                    Resolver.class, 
                });
                sr = (SemanticRecorder)ctor.newInstance(new Object[] { 
                    getResolver()
                });
                sr.addActionListener(getListener());
            }
            catch(InvocationTargetException e) {
                Log.warn(e);
            }
            catch(NoSuchMethodException e) {
                sr = getSemanticRecorder(cls.getSuperclass());
            }
            catch(InstantiationException e) {
                sr = getSemanticRecorder(cls.getSuperclass());
            }
            catch(IllegalAccessException iae) {
                sr = getSemanticRecorder(cls.getSuperclass());
            }
            catch(ClassNotFoundException cnf) {
                sr = getSemanticRecorder(cls.getSuperclass());
            }
            // Cache the results for future reference
            semanticRecorders.put(cls, sr);
        }
        return sr;
    }

    /** Special adapter to catch events on JInternalFrame instances. */
    private class InternalFrameWatcher
        extends AbstractInternalFrameWatcher {
        public InternalFrameWatcher(JInternalFrame f) { super(f); }
        protected void dispatch(AWTEvent e) {
            record(e);
        }
    }
    
    /**
     * Extracted the generation of recorder name, so as to override
     * @author keertip
     * 10/11/06
     */
    
    protected String getRecoderName(String cname){
    	return "abbot.editor.recorder." + cname + "Recorder";
    }
}
