package abbot.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.*;

import junit.extensions.abbot.*;
//import junit.runner.TestCollector;
import abbot.BugReport;
import abbot.Log;
import abbot.ExitException;
import abbot.NoExitSecurityManager;
import abbot.AssertionFailedError;
import abbot.Platform;
import abbot.editor.actions.*;
import abbot.editor.editors.*;
import abbot.editor.recorder.*;
import abbot.editor.widgets.*;
import abbot.editor.widgets.TextField;
import abbot.finder.*;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.script.Action;
import abbot.tester.*;
import abbot.tester.Robot;
import abbot.util.*;
import abbot.util.Properties;
import abbot.util.ThreadTerminatingSecurityManager.ThreadTerminatedException;

import com.apple.mrj.*;

/**
 * This is the 'model' behind the script editor UI.<p>
 *
 * Acts as a resolver, using the currently in-context script as the component
 * resolver. <p>
 */

/* To add new actions, add the action to the list in initActions(),
 * and optionally add it to the menu layout in initMenus.  Define a name for
 * it in EditorConstants, and an inner Action class for it which uses that
 * name. 
 */
// Apologies for the extreme cruftiness and lack of proper factoring.  This
// was written at the same time as the underlying framework, and refactored
// (sort of) into model/view at the same time, so it's hardly a shining
// example of clean design.  Don't know if it would have been any better
// written TDD, though.
public class ScriptEditor 
    implements ActionListener, Resolver, EditorConstants {

    private static int selectKey;
    private static int captureKey;
    private static int captureImageKey;

    static {
        try {
            new EventExceptionHandler().install();
        }
        catch(Exception e) {
            // Ignore for now
        }
        String key = System.getProperty("abbot.editor.select_key", "F1");
        selectKey = KeyStroke.getKeyStroke(key).getKeyCode();
        key = System.getProperty("abbot.editor.capture_key", "F2");
        captureKey = KeyStroke.getKeyStroke(key).getKeyCode();
        key = System.getProperty("abbot.editor.capture_image_key", "F3");
        captureImageKey = KeyStroke.getKeyStroke(key).getKeyCode();
    }

    // if set, log all events, even those going to filtered components
    private static final boolean LOG_ALL_EVENTS =
        Boolean.getBoolean("abbot.editor.log_all_events");
    private static final long FIXTURE_EVENT_MASK =
        Long.getLong("abbot.fixture.event_mask", 
                    EventRecorder.RECORDING_EVENT_MASK).longValue();

    /** Key to use to invert an assertion/wait. */
    public static final int KC_INVERT = KeyEvent.VK_SHIFT;
    /** Key to use to insert a wait instead of an assertion.   Use option key
        on mac, control key anywhere else. */
    public static final int KC_WAIT =
        Platform.isMacintosh() ? KeyEvent.VK_ALT : KeyEvent.VK_CONTROL;
    /** Flag for informational status. */
    private static final int INFO = 0;
    /** Flag to indicate a warning. */
    private static final int WARN = 1;
    /** Flag to indicate an error. */
    private static final int ERROR = 2;
    /** Flag to indicate a script failure. */
    private static final int FAILURE = 3;
    /** Prefixes for different types of status messages. */
    private static final String[] statusFormat =
        { "Normal", "Warning", "Error", "Failure" };
    private static final Color[] statusColor = {
        Color.black,
        Color.orange.darker(),
        Color.red,
        Color.red,
    };

    private ArrayList insertActions = new ArrayList();
    private ArrayList assertActions = new ArrayList();
    private ArrayList waitActions = new ArrayList();
    private ArrayList captureActions = new ArrayList();

    /** Adapter for representing the script itself, providing access to
     * individual script steps.
     */
    private ScriptModel scriptModel;
    private ScriptTable scriptTable;
    private EditorSecurityManager securityManager;
    private SecurityManager oldSecurityManager;

    private int nonce;
    /** Keep all application under test threads in the same group to make them
     * easier to track. */
    private ThreadGroup appGroup;
    private TestHierarchy hierarchy;
    private Hierarchy oldHierarchy;
    private Recorder[] recorders;
    private java.util.List savedStateWhileRecording;
    /** Allow exits from anywhere until the editor is fully initialized. */
    private boolean rootIsExiting = true;
    private boolean exiting;
    private boolean hiding;
    private boolean ignoreStepEvents;
    /** Whether to ignore incoming AWT events. */
    private boolean ignoreEvents;
    /** Is there a script or launch step currently running? */
    private boolean isScriptRunning;
    /** When was some portion of the app exercised? */
    private long lastLaunchTime;
    /** Are we trying to capture an image? */
    private boolean capturingImage;
    /** What component is currently "selected" for capture? */
    private Component captureComponent;
    private Component innermostCaptureComponent;
    private Highlighter highlighter;
    /** Is this the first editor launched, or one under test? */
    private boolean isRootEditor = true;
    /** AWT input state. */
    private static InputState state = Robot.getState();
    /** Generic filter to select a test script. */
    private ScriptFilter filter = new ScriptFilter();

    /** Current test case class (should derive from AWTTestCase). */
    private Class testClass;
    /** Current test suite.  */
    private ScriptTestSuite testSuite;
    /** Current test script. */
    private Script testScript;
    /** Is the current script a temporary placeholder? */
    private File tempFile;
    /** Runner used to execute the script. */
    private StepRunner runner;
    /** Current set of scripts, based on the test suite (if any). */
    private List testScriptList;
    /** Currently selected component.  Note that this may be a dummy
     * component.
     */
    private Component selectedComponent;
    /** Currently selected reference, if any. */
    private ComponentReference selectedReference;

    /** Are we currently recording events? */
    private boolean recording;
    /** The current recorder to pass events for capture. */
    private Recorder recorder;
    /** Since recorder starts with a key release, and stops with a key press,
        make sure we don't start immediately after stopping.
    */
    private boolean justStoppedRecording;
    /** Need to be able to set the combo box selection w/o reacting to the
     * resulting posted action.
     */
    private boolean ignoreComboBox;
    /** Where to stop. */
    private Step stopStep;

    // GUI components
    private JFileChooser chooser;
    private ScriptEditorFrame view;
    private ComboBoxModel model;

    private boolean invertAssertions;
    private boolean waitAssertions;
    private ActionMap actionMap;
    private String name;

    /**
     * Constructs a ScriptEditor which handles script editing logic.
     * ScriptEditorFrame provides the view/controller.
     * @see ScriptEditorFrame
     */
    public ScriptEditor() {

        if (Boolean.getBoolean("abbot.framework.launched")) {
            isRootEditor = false;
            name = "Script Editor (under test)";
        }
        else {
            System.setProperty("abbot.framework.launched", "true");
            name = "Script Editor (root)";
        }

        // TODO: clean this up
        actionMap = initActions();
        hierarchy = initContext(isRootEditor);
        recorders = initRecorders();
        view = initFrame(isRootEditor);
        hierarchy.setFiltered(view, true);
        updateDynamicActions(view);

        view.setComponentBrowser(createComponentBrowser());
        addEventHandlers(view);

        // Clear the status only if there were no errors
        if (view.getStatus().equals(Strings.get("Initializing"))) {
            setStatus(Strings.get("Ready"));
        }
        rootIsExiting = false;
    }

    /** Provides a convenient menu setup definition.
        Use a defined action name to indicate that action's place within the
        menu.  Null values indicate menu separators.
     */
    private String[][] initMenus() {
        ArrayList fileMenu = new ArrayList();
        ArrayList helpMenu = new ArrayList();
        fileMenu.addAll(Arrays.asList(new String[] {
            MENU_FILE,
            ACTION_SCRIPT_NEW,
            ACTION_SCRIPT_DUPLICATE,
            ACTION_SCRIPT_OPEN,
            null,
            ACTION_SCRIPT_SAVE,
            ACTION_SCRIPT_SAVE_AS,
            ACTION_SCRIPT_RENAME,
            ACTION_SCRIPT_CLOSE,
            null,
            ACTION_SCRIPT_DELETE,
        }));

        helpMenu.add(MENU_HELP);
        if (!Platform.isOSX()) {
            fileMenu.add(null);
            fileMenu.add(ACTION_EDITOR_QUIT);
            helpMenu.add(ACTION_EDITOR_ABOUT);
        }
        helpMenu.addAll(Arrays.asList(new String[] {
            ACTION_EDITOR_USERGUIDE,
            ACTION_EDITOR_WEBSITE,
            ACTION_EDITOR_EMAIL,
            ACTION_EDITOR_BUGREPORT,
        }));

        return new String[][] {
            (String[])fileMenu.toArray(new String[fileMenu.size()]),
            {
                MENU_EDIT,
                ACTION_STEP_CUT,
                null,
                ACTION_STEP_MOVE_UP,
                ACTION_STEP_MOVE_DOWN,
                ACTION_STEP_GROUP,
                null,
                ACTION_SELECT_COMPONENT,
                null,
                ACTION_SCRIPT_CLEAR,
            },
            {
                MENU_TEST,
                ACTION_RUN,
                ACTION_RUN_TO,
                ACTION_RUN_SELECTED,
                null,
                ACTION_EXPORT_HIERARCHY,
                null,
                ACTION_RUN_LAUNCH,
                ACTION_RUN_TERMINATE,
                null,
                ACTION_TOGGLE_STOP_ON_FAILURE,
                ACTION_TOGGLE_STOP_ON_ERROR,
                ACTION_TOGGLE_FORKED,
                ACTION_GET_VMARGS,
                ACTION_TOGGLE_SLOW_PLAYBACK,
                ACTION_TOGGLE_AWT_MODE,
            },
            {
                MENU_INSERT,
                ACTION_INSERT_ANNOTATION,
                ACTION_INSERT_APPLET,
                ACTION_INSERT_CALL,
                ACTION_INSERT_COMMENT,
                ACTION_INSERT_EXPRESSION,
                ACTION_INSERT_LAUNCH,
                ACTION_INSERT_FIXTURE,
                ACTION_INSERT_SAMPLE,
                ACTION_INSERT_SCRIPT,
                ACTION_INSERT_SEQUENCE,
                ACTION_INSERT_TERMINATE,
            },
            {
                MENU_CAPTURE,
            },
            (String[])helpMenu.toArray(new String[helpMenu.size()]),
        };
    }

    // All editor actions should be defined here
    private ActionMap initActions() {
        javax.swing.Action[] actions = {
            new EditorAboutAction(),
            new EditorEmailAction(),
            new EditorBugReportAction(),
            new EditorWebsiteAction(),
            new EditorUserGuideAction(),
            new EditorQuitAction(),
            new ScriptOpenAction(),
            new ScriptNewAction(),
            new ScriptDuplicateAction(),
            new ScriptSaveAction(),
            new ScriptSaveAsAction(),
            new ScriptRenameAction(),
            new ScriptCloseAction(),
            new ScriptDeleteAction(),
            new ScriptClearAction(),
            new StepCutAction(),
            new StepMoveUpAction(),
            new StepMoveDownAction(),
            new StepGroupAction(),
            new RunAction(),
            new RunToAction(),
            new RunSelectedAction(),
            new RunLaunchAction(),
            new RunTerminateAction(),
            new GetVMArgsAction(),
            new SelectTestSuiteAction(),
            new ExportHierarchyAction(),
            new ToggleForkedAction(),
            new InsertLaunchAction(),
            new InsertFixtureAction(),
            new InsertAppletAction(),
            new InsertTerminateAction(),
            new InsertCallAction(),
            new InsertSampleAction(),
            new InsertSequenceAction(),
            new InsertScriptAction(),
            new InsertCommentAction(),
            new InsertExpressionAction(),
            new InsertAnnotationAction(),
            new ToggleStopOnFailureAction(),
            new ToggleStopOnErrorAction(),
            new ToggleSlowPlaybackAction(),
            new ToggleAWTModeAction(),
            new CaptureImageAction(),
            new CaptureComponentAction(),
            new SelectComponentAction(),
        };
        ActionMap map = new ActionMap();
        for (int i=0;i < actions.length;i++) {
            Object key = actions[i].getValue(EditorAction.ACTION_KEY);
            map.put(key, actions[i]);
        }
        return map;
    }

    /**
     *  Add event handlers to their respective components
     */
    private void addEventHandlers(final ScriptEditorFrame view) {
        scriptTable.getSelectionModel().
            addListSelectionListener(new ScriptTableSelectionHandler());
        scriptTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if ((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                    if (view.getEditor() == null)
                        setStepEditor();
                }
            }
        });
        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {                          
                if ((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                    int size = scriptTable.getRowCount();            
                    scriptTable.clearSelection();                    
                    scriptTable.setCursorLocation(size); 
                }                                                              
            }                                                                  
        };
        view.addMouseListener(ml);
        view.getTestScriptSelector().addItemListener(new ScriptSelectorItemHandler());
        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                quitApplication();
            }
        });
        if (Platform.isOSX()) {
//NOT Supported in Mac Java5+
//            // Mac has it's own dedicated Quit/About menu items
//            MRJApplicationUtils.registerQuitHandler(new MRJQuitHandler() {
//                public void handleQuit() {
//                    quitApplication();
//                }
//            });
//            MRJApplicationUtils.registerAboutHandler(new MRJAboutHandler() {
//                public void handleAbout() {
//                    view.showAboutBox();
//                }
//            });
        }
    }

    /**
     * Determines if the editor is testing itself and initializes the 
     * security manager accordingly.  
     */
    private TestHierarchy initContext(boolean isRoot) {
        TestHierarchy hierarchy = new TestHierarchy() {
            private String desc = "Test hierarchy for " + name;
            public String toString() {
                return desc;
            }
        };
        // eventually this should go away; all hierarchy usage should be
        // explicit. 
        oldHierarchy = AWTHierarchy.getDefault();
        if (isRoot) {
            AWTHierarchy.setDefault(hierarchy);
            initSecurityManager();
            try {
                new EventExceptionHandler().install();
            }
            catch(Exception e) {
                Log.warn(e);
            }
        }
        return hierarchy;
    }

    private Recorder[] initRecorders() {
        // Use the editor as the resolver, since the actual resolver will
        // be the currently scoped script.
        Recorder[] recorders = new Recorder[] {
            new EventRecorder(this, false),
            new EventRecorder(this, true),
        };

        ActionListener recorderListener = new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                setStatus(event.getActionCommand());
            }
        };
        for (int i=0;i < recorders.length;i++) {
            recorders[i].addActionListener(recorderListener);
        }
        return recorders;
    }

    /** Initialize the primary editor frame. */
    private ScriptEditorFrame initFrame(final boolean isRoot) {

        scriptModel = new ScriptModel() {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        runner = new EditorStepRunner();
        runner.setTerminateOnError(false);
        runner.addStepListener(new StepListener() {
            public void stateChanged(StepEvent ev) {
                if (ignoreStepEvents)
                    return;
                reflectScriptExecutionState(ev);
            }
        });
        // Customize the ScriptTable so we can vary the color of any given
        // step based on our last run status (of which the table itself should
        // be ignorant).
        scriptTable = new ScriptTable(scriptModel) {
            public Color getStepColor(Step step, boolean selected) {
                Color color = super.getStepColor(step, selected);
                // Make the stop step appear a different color
                if (step == stopStep) {
                    Color stopColor = getSelectionBackground().darker();
                    color = stopColor;
                }
                Throwable thr = runner.getError(step);
                if (thr != null) {
                    Color tint = (thr instanceof AssertionFailedError)
                        ? statusColor[ScriptEditor.FAILURE]
                        : statusColor[ScriptEditor.ERROR];
                    if (step instanceof Sequence) {
                        color = color.brighter();
                    }
                    if (selected) {
                        color = mixColors(color, tint);
                    }
                    else {
                        color = tint;
                    }
                }
                return color;
            }
        };

        // Override default "cut" action in table
        ActionMap amap = scriptTable.getActionMap();
        amap.put("cut", actionMap.get(ACTION_STEP_CUT));
        InputMap imap = scriptTable.getInputMap();
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggle");

        // Only allow the script editor to dispose of the frame
        String prefFile = Preferences.PROPS_FILENAME;
        if (!isRoot) {
            prefFile += ".tmp";
        }
        Preferences prefs = new Preferences(prefFile);
        String title = Strings.get("ScriptEditor.title",
                                   new Object[] {""});
        ScriptEditorFrame f = new ScriptEditorFrame(initMenus(),
                                                    actionMap, this,
                                                    title,
                                                    scriptTable, prefs) {
            /** Don't allow code under test to hide the editor. */
            public void hide() {
                if (hiding || isDisposeFromRootEditor()) {
                    hiding = false;
                    super.hide();
                }
            }
            public void dispose() {
                // Need to prevent arbitrary disposal by the code under test.
                // Only allow the dispose if one of the following is true:
                // a) we triggered it (exiting == true)
                // b) the root script editor is disposing us
                if (exiting || isDisposeFromRootEditor()) {
                    super.dispose();
                }
            }
            public String getName() {
                String name = super.getName();
                if (isRoot)
                    name += " (root)";
                return name;
            }
        };
        return f;
    }

    /** Provide a color that is a mix of the two given colors. */
    private Color mixColors(Color c1, Color c2) {
        return new Color((c1.getRed() + c2.getRed())/2,
                         (c1.getGreen() + c2.getGreen())/2,
                         (c1.getBlue() + c2.getBlue())/2);
    }

    /** Return whether the root editor disposed of this instance. */
    private boolean isDisposeFromRootEditor() {
        // FIXME cf how applets prevent disposal of embedded frame
        // AWTHierarchy surrounds disposal calls with the property
        // abbot.finder.disposal set to "true"
        return !isRootEditor && Boolean.getBoolean("abbot.finder.disposal");
    }

    private void createAsserts(ArrayList list, ComponentTester tester,
                               boolean wait) {
        list.clear();
        Method[] methods = tester.getAssertMethods();
        for (int i = 0; i < methods.length; i++) {
            list.add(new TesterMethodAction(tester, methods[i], wait));
        }
        methods = tester.getComponentAssertMethods();
        if (list.size() != 0 && methods.length != 0)
            list.add(null);
        for (int i = 0; i < methods.length; i++) {
            list.add(new TesterMethodAction(tester, methods[i], wait));
        }
        methods = tester.getPropertyMethods();
        if (list.size() != 0 && methods.length != 0)
            list.add(null);
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if (name.startsWith("is"))
                name = name.substring(2);
            else if (name.startsWith("get") || name.startsWith("has"))
                name = name.substring(3);
            list.add(new TesterMethodAction(tester, methods[i], wait));
        }
    }

    /** Return a script step encapsulating an image comparison.  
     * Assumes the script context is not null.
     */
    private Step captureComponentImage(Component comp) {
        Step step = null;
        ComponentTester tester = ComponentTester.getTester(comp);
        java.awt.image.BufferedImage img =
            tester.capture(comp, !(comp instanceof Window));
        try {
            // Save the image file relative to the current context
            ComponentReference ref = addComponent(comp);
            File scriptFile = ((Script)getResolverContext()).getFile();
            File newFile = new File(getResolverContext().getDirectory(),
                                    scriptFile.getName()
                                    + "-" + ref.getID()
                                    + ".jpg");
            int index = 1;
            while (newFile.exists()) {
                newFile = new File(getResolverContext().getDirectory(),
                                   scriptFile.getName()
                                   + "-" + ref.getID()
                                   + "-" + index++ + ".jpg");
            }
            ImageComparator.writeJPEG(newFile, img);
            // Note that the pathname is saved relative to the script
            // context. 
            step = new Assert(getResolverContext(), null,
                              ComponentTester.class.getName(),
                              "assertImage", 
                              new String[] {
                                  ref.getID(), newFile.getName(), "true"
                              },
                              "true", false);
        }
        catch(IOException io) {
            Log.warn(io);
        }
        return step;
    }

    /** Start recording, launching the code under test if necessary. */
    private void startRecording(Recorder rec) {
        Log.debug("Starting recorder");
        boolean noWindows = countShowingWindows(null) == 0;
        boolean canLaunch = testScript != null
            && testScript.hasLaunch() && !isAppLaunched();
        if (noWindows && !canLaunch) {
            // Can't launch, and there are no windows to
            // record on, so show a warning
            view.showError(Strings.get("NoWindows.title"),
                           Strings.get("NoWindows"));
        }
        else {
            if (recorder != null)
                stopRecording(true);
            Log.debug("Now recording with " + rec);
            recording = true;
            recorder = rec;
            
            setStatus("Please wait...");
            // Disable the UI while recording; we re-enable the frame itself
            // to avoid being unable to click on it later (w32, 1.4.1)
            savedStateWhileRecording = AWT.disableHierarchy(view);
            view.setEnabled(true);
            // get us out of the way
            // FIXME this puts us WAY back on linux; this is only a
            // problem in that the status bar is often hidden
            // Maybe make a floating status while the recorder is
            // running. 
            // Only go back if the app is already up, otherwise the
            // about-to-be-launched app is hidden.
            if (!noWindows)
                view.toBack();
            recorder.start();

            if (noWindows) {
                launch(false);
            }
        }
    }

    /** Stop recording and update the recorder actions' state. */
    private void stopRecording(boolean discardRecording) {
        Log.debug("Stopping recorder");
        recording = false;
        int type = INFO;
        String extended = null;
        String status = Strings.get(discardRecording
                                    ? "RecorderCanceled"
                                    : "RecorderFinished");
        try {
            recorder.terminate();
        }
        catch(RecordingFailedException e) {
            String msg = Strings.get("editor.recording.stop_failure");
            Throwable error = e.getReason() instanceof BugReport
                ? e.getReason() : new BugReport(msg, e.getReason());
            Log.log("Recording stop failure: " + error.toString());
            view.showWarning(msg);
            status = error.getMessage();
            extended = error.toString();
            type = ERROR;
        }
        try {
            if (!discardRecording) {
                Step step = recorder.getStep();
                // Ignore empty results
                if (!(step instanceof Sequence 
                      && ((Sequence)step).size() == 0)) {
                    addStep(step);
                }
            }
        }
        finally {
            recorder = null;
            AWT.reenableHierarchy(savedStateWhileRecording);
        }
        view.toFront();
        setStatus(status, extended, type);
    }

    private RecordAllAction recordAllAction;
    private RecordAllAction recordAllMotionAction;

    private void updateDynamicActions(ScriptEditorFrame view) {
        Class cls = selectedComponent == null
            ? Component.class : selectedComponent.getClass();
        ComponentTester tester = ComponentTester.getTester(cls);
        // assert submenu
        createAsserts(assertActions, tester, false);
        // wait submenu
        createAsserts(waitActions, tester, true);
        // insert submenu (only include one instance of each uniquely-named
        // method. 
        insertActions.clear();
        Map map = new HashMap();
        Method[] methods = tester.getActions();
        for (int i=0;i < methods.length;i++) {
            TesterMethodAction action =
                new TesterMethodAction(tester, methods[i], true);
            map.put(action.getName(), action);
        }
        methods = tester.getComponentActions();
        for (int i = 0; i < methods.length; i++) {
            TesterMethodAction action =
                new TesterMethodAction(tester, methods[i], true);
            map.put(action.getName(), action);
        }
        insertActions.addAll(map.values());
        // capture actions

        captureActions.clear();
        recordAllAction = new RecordAllAction(ACTION_CAPTURE,
                                              recorders[0], false);
        captureActions.add(recordAllAction);
        recordAllMotionAction = new RecordAllAction(ACTION_CAPTURE_ALL,
                                                    recorders[1], true);
        captureActions.add(recordAllMotionAction);
        captureActions.add(null);
        captureActions.add(new CaptureImageAction());
        captureActions.add(new CaptureComponentAction());

        view.populateInsertMenu(insertActions);
        view.populateAssertMenu(assertActions);
        view.populateWaitMenu(waitActions);
        view.populateCaptureMenu(captureActions);
    }

    private void setSelected(String which, boolean select) {
        javax.swing.Action action = actionMap.get(which);
        if (action != null) {
            ((EditorToggleAction)action).setSelected(select);
        }
        else {
            Log.warn("Toggle action " + which + " is missing");
        }
    }

    private void setEnabled(String which, boolean enable) {
        javax.swing.Action action;
        if (which == ACTION_DYNAMIC) {
            ArrayList[] lists = new ArrayList[] {
                captureActions, waitActions, assertActions, insertActions
            };
            for (int i=0;i < lists.length;i++) {
                Iterator iter = lists[i].iterator();
                while (iter.hasNext()) {
                    action = (javax.swing.Action)iter.next();
                    if (action != null)
                        action.setEnabled(enable);
                }
            }
        }
        else {
            action = actionMap.get(which);
            if (action != null) {
                action.setEnabled(enable);
            }
            else {
                Log.warn("Action " + which + " is missing");
            }
        }
    }

   /**
     * Initalize the componentBrowser and listeners to the scriptTable
     */
    private ComponentBrowser createComponentBrowser() {
        ComponentBrowser cb = new ComponentBrowser(this, hierarchy);
        cb.setEnabled(false);
        cb.addSelectionListener(new ComponentBrowserListener() {
            public void selectionChanged(ComponentBrowser src,
                                         Component comp,
                                         ComponentReference ref) {
                setSelectedComponent(comp, ref);
            }
            public void propertyAction(ComponentBrowser src,
                                       Method m, Object value,
                                       boolean sample) {
                if (selectedComponent == null)
                    return;
                addPropertyMethodCall(m, value, sample);
            }
        });
        return cb;
    }

    /**
     * Install a new security manager to prevent launched applications
     * from exiting the JVM.  This is only a partial solution; ideally
     * we'd like to be able to kill all the launched app's threads, or force
     * an unload of the class and reload it.
     * Should only be installed once, in the root editor context.
     */
    private void initSecurityManager() {
        if (Boolean.getBoolean("abbot.no_security_manager"))
            return;

        securityManager = new EditorSecurityManager();
        try {
            oldSecurityManager = System.getSecurityManager();
            System.setSecurityManager(securityManager);
        }
        catch(Exception e) {
            oldSecurityManager = securityManager = null;
            Log.warn(e);
        }
    }

    /** Respond to various components. */
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == view.getTestScriptSelector()
            && !ignoreComboBox) {
            Script script = (Script)
                view.getTestScriptSelector().getSelectedItem();
            if (script != testScript)
                setScript(script);
        }
        else if (ev.getSource() == view.getTestScriptDescription()) {
            if (testScript != null) {
                JTextField tf = view.getTestScriptDescription();
                String desc = tf.getText();
                if ("".equals(desc)) {
                    String cmd = ev.getActionCommand();
                    if (!TextField.isDocumentAction(cmd)) {
                        tf.setText(testScript.getDefaultDescription());
                        testScript.setDescription(null);
                    }
                }
                else if (!desc.equals(testScript.getDefaultDescription())) {
                    testScript.setDescription(desc);
                }
            }
        }
        else {
            Log.warn("Unrecognized event: " + ev.getActionCommand()
                     + "(" + ev.getID() + ")");
        }
    }

    /** Remove the selected step. */
    private void cutSelection() {
        int row = scriptTable.getSelectedRow();
        if (row == -1) {
            Log.warn("Unexpected cut state");
            return;
        }
        scriptModel.removeSteps(scriptTable.getSelectedSteps());
        int count = scriptTable.getRowCount();
        if (count > 0) {
            if (row >= count)
                row = count - 1;
            scriptTable.setRowSelectionInterval(row, row);
            scriptTable.setCursorLocation(row + 1);
        }
        setActionsEnabledState();
        setStatus("");
    }

    private void moveSelectionUp() {
        scriptTable.moveUp();
        setActionsEnabledState();
    }

    /** Move the selected step down. */
    private void moveSelectionDown() {
        scriptTable.moveDown();
        setActionsEnabledState();
    }

    /** Put the current selection into a sequence. */
    private void groupSelection() {
        int row = scriptTable.getSelectedRow();
        Sequence seq = new Sequence(getResolverContext(), (String)null);
        List list = scriptTable.getSelectedSteps();
        Step first = (Step)list.get(0);
        Sequence parent = scriptModel.getParent(first);
        int index = parent.indexOf(first);
        scriptModel.removeSteps(list);
        Iterator iter = list.iterator();
        Step last = parent;
        while (iter.hasNext()) {
            last = (Step)iter.next();
            seq.addStep(last);
        }
        scriptModel.insertStep(parent, seq, index);
        scriptModel.toggle(row);
        scriptTable.setRowSelectionInterval(row, scriptModel.getRowOf(last));
        setActionsEnabledState();
    }

    /** Insert a launch step. */
    void insertLaunch() {
        Step step = new Launch(getResolverContext(),
                               LaunchEditor.HELP_DESC,
                               "abbot.editor.ScriptEditor",
                               "main",
                               new String[] { "[]" },
                               ".", false);
        addStep(step);
    }

    /** Insert an applet step. */
    void insertApplet() {
        Step step = new Appletviewer(getResolverContext(),
                                     AppletviewerEditor.HELP_DESC,
                                     "your.applet.class.here",
                                     new HashMap(),
                                     null, null, null);
        addStep(step);
    }

    /** Insert a terminate step. */
    void insertTerminate() {
        Step step = new Terminate(getResolverContext(),
                                  (String) null);
        scriptTable.setCursorLocation(scriptTable.getRowCount());
        addStep(step);
    }

    private void insertCall(boolean sample) {
        if (sample) {
            addStep(new Sample(getResolverContext(),
                               (String)null, Strings.get("YourClassName"),
                               Strings.get("YourMethodName"), null,
                               Strings.get("YourPropertyName")));
        }
        else {
            addStep(new Call(getResolverContext(),
                             (String)null, Strings.get("YourClassName"),
                             Strings.get("YourMethodName"), null));
        }
    }

    /** Insert a new, empty sequence. */
    private void insertSequence() {
        addStep(new Sequence(getResolverContext(),
                             (String) null, null));
    }

    private void insertComment() {
        addStep(new Comment(getResolverContext(), ""));
    }

    private void insertExpression() {
        addStep(new Expression(getResolverContext(), ""));
    }

    private void insertAnnotation() {
  
    }

    /** Insert another script as a step in this one. */
    private void insertScript(boolean fixture) {
        JFileChooser chooser = getChooser(filter);
        chooser.setCurrentDirectory(getWorkingDirectory());
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch(IOException e) {
                    view.showError(e.toString());
                }
            }
            Script script = fixture
                ? new Fixture(file.getAbsolutePath(), hierarchy)
                : new Script(file.getAbsolutePath(), hierarchy);
            try {
                script.load();
                addStep(script);
            }
            catch (Exception exc) {
                view.showError(exc.toString());
            }
        }
    }

    /** Returns the current test suite's directory, if available, the
        directory of the current script, if available, or the current working 
        directory.  If the current script has not yet been saved, 
        uses the current working directory.
    */
    private File getWorkingDirectory() {
        return testSuite != null
            ? testSuite.getDirectory()
            : (testScript != null && !editingTempFile()
               ? testScript.getDirectory() 
               : new File(System.getProperty("user.dir")));
    }

    /** Return a file chooser that filters for test scripts. */
    // FIXME some open, close operations should be sticky w/r/t
    // last directory used
    private JFileChooser getChooser(FileFilter f) {
        if (chooser == null) {
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(getWorkingDirectory());
        }
        chooser.setFileFilter(f);
        return chooser;
    }

    /** Set the test case to the one corresponding to the given index. */
    private void setScript(int index) {
        if (getScripts().size() == 0) {
            setScript((Script) null);
            if (testSuite != null)
                setStatus(Strings.get("NoScripts"), null, WARN);

        }
        else {
            if (index >= getScripts().size())
                index = getScripts().size() - 1;
            setScript(getScriptAt(index));
        }
    }

    private void setScript(Script script) {
        if (script == testScript && script != null)
            return;

        Log.debug("Setting script to '" + script + "'");
        if (script != null) {
            try {
                script.load();
            }
            catch(InvalidScriptException ise) {
                Log.warn(ise);
                setScript((String)null);
                view.showError("Invalid Script", ise.toString());
                return;
            }
            catch(Exception e) {
                setScript((String)null);
                Log.warn(e);
                return;
            }
        }

        if (testScript != null) {
            UIContext context = testScript.getUIContext();
            if (context != null && !context.equivalent(runner.getCurrentContext()))
                runner.terminate();
        }
        testScript = script;
        scriptTable.clearSelection();
        scriptModel.setScript(script);
        if (script == null) {
            scriptTable.setCursorLocation(0);
            scriptTable.setEnabled(false);
            view.getTestScriptDescription().setText("");
            view.getTestScriptDescription().setEnabled(false);
            setStatus(Strings.get("NoScript"));
        }
        else {
            scriptTable.setEnabled(true);
            scriptTable.setCursorLocation(script.hasLaunch() ? 1 : 0);
            if (chooser != null) {
                chooser.setCurrentDirectory(script.getDirectory());
            }
            view.getTestScriptDescription().setText(script.getDescription());
            view.getTestScriptDescription().setEnabled(true);
            setStatus(Strings.get("editor.editing_script", new Object[] {
                testScript.getName()
            }));
        }
        setActionsEnabledState();

        ignoreComboBox = true;
        view.getTestScriptSelector().setSelectedItem(testScript);
        ignoreComboBox = false;
        updateTitle();
    }

    /** Update the state of all actions.  This method should be invoked after
     * any GUI state change.
     */
    private void setActionsEnabledState() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setActionsEnabledState();
                }
            });
            return;
        }

        boolean haveScript = testScript != null;
        boolean haveSelection = scriptTable.getSelectedRow() != -1;

        setEnabled(ACTION_SCRIPT_OPEN, true);
        setEnabled(ACTION_TOGGLE_STOP_ON_FAILURE, haveScript);
        setSelected(ACTION_TOGGLE_STOP_ON_FAILURE,
                    haveScript && runner.getStopOnFailure());
        setEnabled(ACTION_TOGGLE_STOP_ON_ERROR, haveScript);
        setSelected(ACTION_TOGGLE_STOP_ON_ERROR,
                    haveScript && runner.getStopOnError());
        setEnabled(ACTION_TOGGLE_FORKED, haveScript);
        setSelected(ACTION_TOGGLE_FORKED, haveScript && testScript.isForked());
        setEnabled(ACTION_GET_VMARGS, haveScript && testScript.isForked());
        setEnabled(ACTION_TOGGLE_SLOW_PLAYBACK, haveScript);
        setSelected(ACTION_TOGGLE_SLOW_PLAYBACK,
                    haveScript && testScript.isSlowPlayback());
        setEnabled(ACTION_TOGGLE_AWT_MODE, haveScript);
        setSelected(ACTION_TOGGLE_AWT_MODE,
                    haveScript && testScript.isAWTMode());

        setEnabled(ACTION_RUN, haveScript);
        setEnabled(ACTION_RUN_TO, haveScript && haveSelection);
        setEnabled(ACTION_RUN_SELECTED,
                   haveScript && haveSelection && isAppLaunched());
        setEnabled(ACTION_EXPORT_HIERARCHY,
                   haveScript && isAppLaunched());
        setEnabled(ACTION_RUN_LAUNCH, haveScript
                   && testScript.hasLaunch()
                   && !isAppLaunched());
        setEnabled(ACTION_RUN_TERMINATE, isAppLaunched());
        setEnabled(ACTION_SCRIPT_NEW, true);
        setEnabled(ACTION_SCRIPT_DUPLICATE, haveScript);
        setEnabled(ACTION_SCRIPT_SAVE, haveScript);
        setEnabled(ACTION_SCRIPT_SAVE_AS, haveScript);
        setEnabled(ACTION_SCRIPT_RENAME, haveScript);
        setEnabled(ACTION_SCRIPT_DELETE, haveScript);
        setEnabled(ACTION_SCRIPT_CLOSE, haveScript);

        setEnabled(ACTION_STEP_CUT, haveScript && haveSelection);
        setEnabled(ACTION_STEP_MOVE_UP, 
                   haveScript && haveSelection && scriptTable.canMoveUp());
        setEnabled(ACTION_STEP_MOVE_DOWN, 
                   haveScript && haveSelection && scriptTable.canMoveDown());
        setEnabled(ACTION_STEP_GROUP, haveScript && haveSelection);
        setEnabled(ACTION_SCRIPT_CLEAR, haveScript);

        setEnabled(ACTION_INSERT_LAUNCH, 
                   haveScript && !testScript.hasLaunch());
        setEnabled(ACTION_INSERT_FIXTURE, 
                   haveScript && !testScript.hasLaunch());
        setEnabled(ACTION_INSERT_APPLET, 
                   haveScript && !testScript.hasLaunch());
        setEnabled(ACTION_INSERT_TERMINATE, 
                   haveScript && !testScript.hasTerminate());
        setEnabled(ACTION_INSERT_SCRIPT, haveScript);
        setEnabled(ACTION_INSERT_CALL, haveScript);
        setEnabled(ACTION_INSERT_SAMPLE, haveScript);
        setEnabled(ACTION_INSERT_SEQUENCE, haveScript);
        setEnabled(ACTION_INSERT_COMMENT, haveScript);
        setEnabled(ACTION_INSERT_EXPRESSION, haveScript);
        setEnabled(ACTION_INSERT_ANNOTATION, haveScript);
        setEnabled(ACTION_DYNAMIC, haveScript);
        
        view.getComponentBrowser().setEnabled(!isScriptRunning);
    }

    /** Set the current test script.  */
    void setScript(String filename) {
        Script script = filename != null
            ? new Script(filename, hierarchy) : null;
        setScript(script);
    }

    /** Indicate the component and/or reference currently in use. */
    private void setSelectedComponent(Component c,
                                      ComponentReference ref) {

        if (c == selectedComponent && ref == selectedReference)
            return;

        boolean updateActions = c != selectedComponent;
        selectedComponent = c;
        selectedReference = ref;
        String status;
        if (ref != null) {
            status = Strings.get(c == null ? "ComponentReferenceX"
                                 : "ComponentReference",
                                 new Object[] { ref.getID() });
        }
        else if (c != null) {
            status = hierarchy.contains(c)
                ? Strings.get("UnreferencedComponent")
                : Strings.get("editor.component_filtered");
        }
        else {
            status = Strings.get("NoComponent");
        }
        setStatus(status);
        if (updateActions) {
            updateDynamicActions(view);
        }
    }

    private void setTestSuite(String suiteClassname) {
        setTestSuite(suiteClassname, null);
    }

    /** Sets the currently selected test suite, updating all gui components
        appropriately.  */
    private void setTestSuite(String suiteClassname, ClassLoader cl) {
        if (cl == null)
            cl = getClass().getClassLoader();
        Log.debug("Setting test suite to " + suiteClassname);
        testSuite = null;
        testScriptList = null;
        if (suiteClassname != null) {
            try {
                // FIXME use a dynamic class loader so we can reload after
                // changes to the suite/fixture class. 
                Class cls = Class.forName(suiteClassname, true, cl);
                if (!ScriptFixture.class.isAssignableFrom(cls)
                    && !ScriptTestSuite.class.isAssignableFrom(cls)) {
                    view.showWarning(Strings.get("editor.wrong_class",
                                                 new Object[] { cls.getName() }));
                }
                else {
                    testClass = cls;
                    Method suiteMethod = null;
                    testSuite = null;
                    try {
                        suiteMethod = testClass.getMethod("suite", new Class[0]);
                        testSuite =
                            (ScriptTestSuite) suiteMethod.invoke(null, new Class[0]);
                    }
                    catch (NoSuchMethodException nsm) {
                        view.showError(nsm.toString());
                        testSuite = null;
                    }
                    catch (InvocationTargetException ite) {
                        view.showError(ite.toString());
                    }
                    catch (IllegalAccessException iae) {
                        view.showError(iae.toString());
                    }
                }
            }
            catch (ClassNotFoundException e) {
                view.showWarning(Strings.get("editor.suite_not_found",
                                             new Object[] { suiteClassname }));
            }
        }
        if (testSuite == null) {
            view.getCurrentTestSuiteLabel().setText(Strings.get("NoSuite"));
            model = new DefaultComboBoxModel();
            view.getTestScriptSelector().setModel(model);
            view.getTestScriptSelector().setEnabled(false);
            model.setSelectedItem(null);
        }
        else {
            view.getTestScriptSelector().setEnabled(true);
            view.getCurrentTestSuiteLabel().setText(testSuite.toString());
            Object oldSelection = view.getTestScriptSelector().getSelectedItem();
            ignoreComboBox = true;
            view.getTestScriptSelector().setEnabled(true);
            // Workaround for indexing bug on OSX
            view.getTestScriptSelector().setSelectedItem(null);
            List list = getScripts();
            Object[] data = list.toArray(new Object[list.size()]);
            model = new DefaultComboBoxModel(data);
            view.getTestScriptSelector().setModel(model);
            // If the test suite didn't actually change, then keep the old
            // selection. 
            if (getScripts().contains(oldSelection))
                model.setSelectedItem(oldSelection);
            ignoreComboBox = false;
        }
    }

    /** Set the frame title to the default. */
    private void updateTitle() {
        String title = Strings.get("ScriptEditor.title", new Object[] {
            testScript != null
            ? (" (" + testScript.getName() + ")")
            : ""
        });
        view.setTitle(title);
    }

    /** Pull up a dialog with all available test suites.  */
    private void browseTests() {
    	throw new IllegalStateException("browsing tests disabled");
    	
//    	ClassLoader cl = getContextClassLoader();
//        String path = cl instanceof PathClassLoader 
//            ? ((PathClassLoader)cl).getClassPath() : null;
//        if (path == null)
//            path = System.getProperty("java.class.path");
//
//        TestSelector selector = new TestSelector(view, path);
//        selector.setVisible(true);
//        String className = selector.getSelectedItem();
//        if (className != null && checkSaveBeforeClose()) {
//            terminate();
//            boolean none = className.equals(TestSelector.TEST_NONE);
//            setTestSuite(none ? null : className, cl);
//            setScript(0);
//            if (none) {
//                setStatus(Strings.get("editor.no_suite"));
//            }
//        }
    }

    /** @return true if it's ok to exit. */
    protected boolean checkSaveBeforeClose() {
        // query save/cancel/exit
        if (testScript != null && (testScript.isDirty())) {
            int opt = view.showConfirmation(Strings.get("ScriptModified"),
                                            JOptionPane.YES_NO_CANCEL_OPTION);
            if (opt == JOptionPane.CANCEL_OPTION
                || opt == JOptionPane.CLOSED_OPTION) {
                return false;
            }
            else if (opt == JOptionPane.YES_OPTION) {
                saveScript();
            }
        }
        return true;
    }

    private void closeScript() {
        if (!checkSaveBeforeClose())
            return;
        setScript((Script)null);
    }

    /** Quit the application. */
    void quitApplication() {
        if (!checkSaveBeforeClose())
            return;

        Log.debug("editor quit" + (isRootEditor ? " (root)" : ""));
        dispose();
        if (isRootEditor) {
            rootIsExiting = true;
        }
        else {
            AWTHierarchy.setDefault(oldHierarchy);
        }
        System.exit(0);
    }

    /** Set the contents of the status message. */
    public void setStatus(String msg) {
        setStatus(msg, null, INFO);
    }

    /** Set the contents of the status message to the given exception. */
    private String getStackTrace(Throwable thr) {
        StringWriter writer = new StringWriter();
        thr.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /** Set the contents of the status message. */
    public void setStatus(String msg, String extended, int type) {
        String text = Strings.get(statusFormat[type], new Object[] { msg });
        view.setStatus(text, extended, statusColor[type]);
        // Save all messages to the log
        Log.log(text);
    }

    private void setStatusForStep(Step step) {
        if (step == null) {
            setStatus("");
            return;
        }

        Throwable error = runner.getError(step);
        boolean fromScript = step == testScript;
        String msg = error != null ? error.toString() : null;
        String inStep = Strings.get("InStep", new Object[] { step });
        String where = Strings.get("StepAt", new Object[] {
            Script.getFile(step), 
            new Integer(Script.getLine(step))
        }) + "\n";
        // Don't need location info for these
        if (error instanceof AssertionFailedError
            && ((AssertionFailedError)error).getLine() != 0) {
            where = "";
        }

        String extended = null;
        int type = INFO;
        if (error != null) {
            Log.log(error);
            boolean isFailure = (error instanceof AssertionFailedError);
            type = isFailure ? FAILURE : ERROR;
            extended = getStackTrace(error);
            // If we're not stopping on failure, don't mention it
            // specifically
            if (fromScript) {
                if ((isFailure && !runner.getStopOnFailure())
                    || (!isFailure && !runner.getStopOnError())) {
                    msg = Strings.get(isFailure
                                      ? "ScriptFailure"
                                      : "ScriptError");
                }
                else {
                    // Otherwise ignore messages from the script itself
                    return;
                }
            }
            else {
                extended = inStep + "\n" + where + extended;
            }
        }                          
        else if (fromScript) {
            msg = Strings.get("ScriptSuccess");
        }
        // If nothing interesting happened, leave the status alone
        if (msg != null)
            setStatus(msg, extended, type);
    }

    /** Return a ComponentReference corresponding to the currently selected
        Component or ComponentReference, creating one if necessary.
    */
    private String getSelectedComponentID() {
        String id = null;
        if (selectedReference != null) {
            id = selectedReference.getID();
        }
        else if (selectedComponent != null) {
            ComponentReference ref = addComponent(selectedComponent);
            id = ref.getID();
            setStatus(Strings.get("ComponentReference", new Object[] {id}));
        }
        return id;
    }

    /** Add either an Action or an Assert method provided by the given
        Tester. */
    private void addTesterCall(Method method, ComponentTester tester,
                               boolean wait, String docs) {
        boolean invert = invertAssertions;
        Class[] params = method.getParameterTypes();
        String id = getSelectedComponentID();
        Class componentClass = selectedComponent != null ? 
            selectedComponent.getClass() : null;
        boolean componentArg0 = params.length > 0 
            && Component.class.isAssignableFrom(params[0]);
        
        String argString =
            view.showInputDialog(Strings.get("IdentifyArguments"), docs, 
                                 componentArg0 ? id : null);
        if (argString == null)
            return;

        String[] args = ArgumentParser.parseArgumentList(argString);
        try {
            insertTesterCall(tester, method, componentClass,
                             id, args, wait, invert);
        }
        catch (IllegalArgumentException iae) {
            Log.warn(iae);
        }
        catch (NoSuchReferenceException nsr) {
            Log.warn(nsr);
        }
    }

    /** Invoked after the user has selected a component and a property for
     * it.
     */
    private void addPropertyMethodCall(Method method,
                                       Object value, boolean sample) {
        String id = getSelectedComponentID();
        String methodName = method.getName();
        String[] args = ComponentTester.class.
            isAssignableFrom(method.getDeclaringClass())
            ? new String[] { id } : null;
        String targetClassName = method.getDeclaringClass().getName();
        if (sample) {
            String varName = Strings.get("YourPropertyName");
            Sample step = args == null
                ? new Sample(getResolverContext(), null, methodName, id, varName)
                : new Sample(getResolverContext(), null, targetClassName, methodName,
                             args, varName);
            addStep(step);
        }
        else {
            String expectedValue = ArgumentParser.toString(value);
            Assert step = args == null
                ? new Assert(getResolverContext(), null, methodName, id,
                             expectedValue, invertAssertions)
                : new Assert(getResolverContext(), null, targetClassName, methodName, 
                             args, expectedValue, invertAssertions);
            step.setWait(waitAssertions);
            addStep(step);
        }
    }

    /** Returns null if not found. */
    public String getComponentID(Component comp) {
        ComponentReference ref = getComponentReference(comp);
        return ref != null ? ref.getID() : null;
    }

    /** Insert a new step at the current cursor location.  */
    void addStep(Step step) {
        Sequence parent = scriptTable.getCursorParent();
        int index = scriptTable.getCursorParentIndex();
        scriptModel.insertStep(parent, step, index);
        int row = scriptModel.getRowOf(step);
        scriptTable.setRowSelectionInterval(row, row);
        scriptTable.setCursorLocation(row + 1);
        setActionsEnabledState();
    }

    /** Create a new script. */
    private void newScript(boolean copyFixture) {
        if (!checkSaveBeforeClose())
            return;
        File file;
        try {
            file = File.createTempFile(Script.UNTITLED_FILE, ".xml");
            tempFile = file;
        }
        catch(IOException io) {
            JFileChooser chooser = getChooser(filter);
            File dir = getWorkingDirectory();
            chooser.setCurrentDirectory(dir);
            chooser.setSelectedFile(new File(dir, Script.UNTITLED_FILE + ".xml"));
            if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = chooser.getSelectedFile();
        }
        if (!file.exists() || file.isFile()) {
            newScript(file, copyFixture);
        }
        else {
            // FIXME display an error
        }
    }

    /** Create a new script at the given filename, or open it if it already
     * exists.  Optionally copies the fixture from the current script.
     */
    void newScript(File file, boolean copyFixture) {
        if (!checkSaveBeforeClose())
            return;

        try {
            boolean insert = file.createNewFile() || file.length() == 0;
            Script srcFixture = testScript;
            setScript(file.getAbsolutePath());
            if (insert) {
                if (!copyFixture || srcFixture == null) {
                    insertTerminate();
                    insertLaunch();
                }
                else {
                    copyFixture(srcFixture);
                }
            }
            setStatus(Strings.get(copyFixture ? "FixtureDuplicated"
                                  : "NewScriptCreated",
                                  new Object[] { file.getName() }));
        }
        catch(IOException io) {
            view.showError("File Error", io.toString());
        }
    }

    /** Copy the fixture from the given script into the current one. */
    public void copyFixture(Script src) {
        Step first = src.getStep(0);
        Step last = src.getStep(src.size() - 1);
        if (first instanceof UIContext) {
            setStatus(Strings.get("editor.adding_launch"));
            addStep(first);
        }
        if (src.size() > 1 && (src.getStep(1) instanceof Assert)) {
            Assert wait = (Assert)src.getStep(1);
            if (wait.isWait()
                && wait.getMethodName().equals("assertFrameShowing")) {
                setStatus(Strings.get("editor.adding_wait"));
                addStep(wait);
            }
        }
        if (last instanceof Terminate) {
            setStatus(Strings.get("editor.adding_terminate"));
            addStep(last);
        }
    }

    /** Launch the GUI under test. */
    private void launch(boolean terminateRecorder) {
        if (testScript == null) {
            Log.warn("null testScript");
            return;
        }

        ignoreStepEvents = true;
        invertAssertions = false;
        waitAssertions = false;
        view.setAssertOptions(waitAssertions, invertAssertions);

        // Clean up any extant windows
        if (terminateRecorder && recorder != null) {
            // Assume we want to keep the results
            stopRecording(false);
        }

        setStatus(Strings.get("Launching"));
        // FIXME time out and flag an error if the launch never returns
        // (even if threaded, it should return at some point)
        runSteps(testScript, true, 
                 Strings.get("LaunchingDone"),
                 new Runnable() {
            public void run() {
                ignoreStepEvents = false;
            }
        });
    }

    /** Do everything we can to dispose of the application under test. */
    private void terminate() {
        if (recorder != null) {
            // Assume we want to keep the results
            stopRecording(false);
        }
        scriptTable.clearSelection();
        try {
            runner.terminate();
        }
        catch(Throwable e) {
            Log.warn(e);
        }
    }

    private void openScript() {
        if (!checkSaveBeforeClose())
            return;
        JFileChooser chooser = getChooser(filter);
        chooser.setCurrentDirectory(getWorkingDirectory());
        if (testScript != null) {
            chooser.setSelectedFile(testScript.getFile());
        }
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            setScript(file.getAbsolutePath());
        }
        if (!getScripts().contains(testScript)
            && model != null) {
            model.setSelectedItem(null);
        }
    }

    private void stopOnFailureToggle() {
        if (testScript != null) {
            runner.setStopOnFailure(!runner.getStopOnFailure());
        }
    }

    private void stopOnErrorToggle() {
        if (testScript != null) {
            runner.setStopOnError(!runner.getStopOnError());
        }
    }

    private void forkedToggle() {
        if (testScript != null) {
            testScript.setForked(!testScript.isForked());
        }
        setActionsEnabledState();
    }

    private void getVMArgs() {
        String args = view.showInputDialog(Strings.get("GetVMArgs.title"),
                                           Strings.get("GetVMArgs.msg"),
                                           testScript.getVMArgs());
        if (args != null) {
            testScript.setVMArgs(args);
        }
    }

    private void slowPlaybackToggle() {
        if (testScript != null) {
            testScript.setSlowPlayback(!testScript.isSlowPlayback());
        }
    }

    private void awtModeToggle() {
        if (testScript != null) {
            testScript.setAWTMode(!testScript.isAWTMode());
        }
    }

    private void runScript(Step stopAt) {
        if (testScript == null) {
            Log.warn("null testScript");
            return;
        }
        Log.debug("Running test case " + testScript);
        
        stopStep = stopAt;
        setStatus(Strings.get("actions.run.start"));
        Runnable completion = new Runnable() {
            public void run() {
                // When the script finishes, bring the main
                // app forward.
                view.toFront();
            }
        };
        runSteps(testScript, false, 
                 Strings.get("actions.run.finish"),
                 completion);
    }

    private void exportHierarchy() {
        JFileChooser chooser = getChooser(null);
        if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            setStatus(Strings.get("actions.export-hierarchy.start"));
            final File file = chooser.getSelectedFile();
            new Thread("Hierarchy Export") {
                public void run() {
                    HierarchyWriter hw = new HierarchyWriter(hierarchy);
                    try {
                        FileWriter writer = new FileWriter(file);
                        hw.writeHierarchy(writer);
                    }
                    catch(IOException io) {
                        view.showError(Strings.get("SaveFailed.title"),
                                       io.toString());
                    }
                    setStatus(Strings.get("actions.export-hierarchy.finish"));
                }
            }.start();
        }
    }

    private void runSelectedSteps() {
        final List stepList = scriptTable.getSelectedSteps();
        if (testScript == null
            || stepList == null || stepList.size() == 0
            || !isAppLaunched()) {
            Log.warn("inconsistent program state");
            return;
        }

        Sequence steps = new Sequence(getResolverContext(), null, stepList);
        final int row0 = scriptModel.getRowOf((Step)stepList.get(0));
        final int row1 = scriptModel.getRowOf((Step)stepList.get(steps.size()-1));
        setStatus(Strings.get("actions.run-selected.start"));
        hideView();
        runSteps(steps, false, 
                 Strings.get("actions.run-selected.finish"),
                 new Runnable() {
            public void run() {
                scriptTable.setRowSelectionInterval(row0, row1);
                view.show();
            }
        });
    }

    /** Invoke the given test script.  All test execution is done on a dedicated
     * thread/thread group to avoid interfering with the event dispatch thread.
     */
    private void runSteps(final Step which, final boolean launch,
                          final String completionMessage,
                          final Runnable onCompletion) {
        Log.debug("running " + which);
        setActionsEnabledState();
        lastLaunchTime = System.currentTimeMillis();
        final List savedState = AWT.disableHierarchy(view);
        Runnable action = new LaunchAction(which, savedState, onCompletion, completionMessage, launch);
        String groupName = "AUT Thread Group for " + this + ":" + nonce++;
        if (appGroup == null) {
            appGroup = new ThreadGroup(groupName) {
                public void uncaughtException(Thread t, Throwable thrown) {
                    if (!(thrown instanceof ExitException)
                                    && !(thrown instanceof ThreadDeath)) {
                        Log.warn("Application thread exception not caught: " + t);
                        Log.warn(thrown);
                    }
                }
            };
        }
        Thread launcher = new Thread(appGroup, action,
                                     "Script runner:" + nonce);
        launcher.setDaemon(true);
        view.getComponentBrowser().setEnabled(false);
        view.setEditor(null);
        isScriptRunning = true;
        launcher.start();
    }

    /** Remove all contents from the current script. */
    private void clearScript() {
        if (testScript == null) {
            Log.warn("null testScript");
            return;
        }
        if (view.showConfirmation(Strings.get("editor.confirm.clear_script"))
            == JOptionPane.YES_OPTION) {
            scriptTable.clearSelection();
            scriptTable.setCursorLocation(0);
            testScript.clear();
            scriptModel.setScript(testScript);
        }
    }

    /** Delete the currently selected script/test case. */
    private void deleteScript() {
        if (testScript == null) {
            Log.warn("null testScript");
            return;
        }
        if (view.showConfirmation(Strings.get("editor.confirm.delete_script"))
            == JOptionPane.YES_OPTION) {
            File file = testScript.getFile();
            int index = view.getTestScriptSelector().getSelectedIndex();
            file.delete();
            setTestSuite(testClass.getName(), testClass.getClassLoader());
            setScript(index);
        }
    }

    /** Change the file backing the current script/test case, renaming
     * its file if <code>rename</code> is true, or saving to a new destination
     * if not. 
     */
    private void saveAsScript(boolean rename) {
        if (testScript == null) {
            Log.warn("null testScript");
            return;
        }
        File oldFile = testScript.getFile();
        JFileChooser chooser = getChooser(filter);
        chooser.setCurrentDirectory(getWorkingDirectory());
        // FIXME make sure it falls within the test suite's script set?
        //chooser.setFileFilter(testScript.getFileFilter());
        Log.debug("Showing save dialog");
        if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            Log.debug("Accepted");
            File newFile = chooser.getSelectedFile();
            if (rename) {
                if (!oldFile.renameTo(newFile)) {
                    view.showError(Strings.get("editor.save.rename_failed",
                                               new Object[] { 
                                                   oldFile, newFile
                                               }));
                    return;
                }
            }
            testScript.setFile(newFile);
            saveScript();
            updateTitle();

            if (testSuite != null && testSuite.accept(newFile)) {
                setTestSuite(testClass.getName(),
                             testSuite.getClass().getClassLoader());
            }
            else{
                setTestSuite(null);
            }
        
            if (rename) {
                setStatus(Strings.get("ScriptRename", new Object[]{
                    newFile.getName()
                }));
            }
            else {
                setStatus(Strings.get("ScriptSaved", new Object[]{
                    newFile.getName()
                }));
            }
            // Combo box doesn't know that the script has changed its
            // toString 
            view.getTestScriptSelector().repaint();

            // Default script description reflects the path
            String text = testScript != null
                ? testScript.getDescription() : "";
            view.getTestScriptDescription().setText(text);
        }
    }

    private boolean editingTempFile() {
        return testScript != null
            && testScript.getFile().equals(tempFile);
    }
    
    /** Save the current script/test case state to disk. */
    private void saveScript() {
        if (testScript == null) {
            Log.warn("null testScript");
            return;
        }
        // If the file is a temporary file, prompt for its real location
        if (editingTempFile()) {
            // This will recurse back into saveScript, so we're done
            Log.debug("Directory is temporary directory, need to rename");
            saveAsScript(false);
            return;
        }
        File file = testScript.getFile();
        File parent = file.getParentFile();
        boolean canWrite = (!file.exists() && parent.canWrite())
            || (file.exists() && file.canWrite());
        if (!canWrite) {
            String msg = Strings.get("NoFilePermission",
                                     new Object[] { file.toString() });
            view.showError(Strings.get("SaveFailed.title"), msg);
        }
        else {
            try {
                setStatus(Strings.get("Saving", new Object[] { file }));
                saveNestedScripts(testScript);
                setStatus(Strings.get("Saved", new Object[] { file }));
            }
            catch (IOException exc) {
                view.showError(Strings.get("SaveFailed.title"),
                               exc.toString());
            }
        }
    }

    void saveNestedScripts(Sequence seq) throws IOException {
        if (seq instanceof Script)
            ((Script)seq).save();

        Iterator iter = seq.steps().iterator();
        while (iter.hasNext()) {
            Step step = (Step)iter.next();
            if (step instanceof Sequence) {
                saveNestedScripts((Sequence)step);
            }
        }
    }

    private EventNormalizer normalizer = new EventNormalizer();

    /** Start listening to GUI events. */
    private void startListening() {
        normalizer.startListening(new SingleThreadedEventListener() {
            protected void processEvent(AWTEvent event) {
                ScriptEditor.this.processEvent(event);
            }
        }, FIXTURE_EVENT_MASK);
        view.getComponentBrowser().setEnabled(true);
    }

    /** Return the number of windows that are showing. */
    private int countShowingWindows(Window root) {
        int count = root != null && root.isShowing() ? 1 : 0;
        Iterator iter = root == null
            ? hierarchy.getRoots().iterator()
            : hierarchy.getComponents(root).iterator();
        while (iter.hasNext()) {
            Component c = (Component)iter.next();
            if (c instanceof Window) {
                count += countShowingWindows((Window)c);
            }
        }
        return count;
    }

    private int DONT_CARE = -1;
    private boolean isKeyPress(AWTEvent event, int code, int modifiers) {
        return event.getID() == KeyEvent.KEY_PRESSED
            && ((KeyEvent)event).getKeyCode() == code
            && (((KeyEvent)event).getModifiers() == modifiers
                || modifiers == DONT_CARE);
    }
    private boolean isKeyRelease(AWTEvent event, int code, int modifiers) {
        return event.getID() == KeyEvent.KEY_RELEASED
            && ((KeyEvent)event).getKeyCode() == code
            && (((KeyEvent)event).getModifiers() == modifiers
                || modifiers == DONT_CARE);
    }

    /** The editor does many things with the event stream, including logging
     * events, passing them off to the recorder, and updating its internal
     * state.
     */
    private void processEvent(AWTEvent event) {
        Object src = event.getSource();
        boolean isComponent = src instanceof Component;
        boolean isFiltered =
            isComponent && hierarchy.isFiltered((Component)src);
        // Keep a log of all events we see on non-filtered components
        if (isRootEditor) {
            if ((LOG_ALL_EVENTS || (!isFiltered && !ignoreEvents))
                && Boolean.getBoolean("abbot.fixture.log_events")) {
                Log.log("ED: " + Robot.toString(event)
                        + " (" + Thread.currentThread() + ")");
            }
        }
        // Allow only component events and AWT menu actions
        if (!isComponent && !(src instanceof MenuComponent)) {
            Log.warn("Source not a Component or MenuComponent: " + event);
            return;
        }
        // If the script is running (or even being launched), the code
        // under test may do things that should really be done on the event
        // dispatch thread (initial component show and such).  
        // If this is the case, defer mucking about with AWT until the code
        // under test has stabilized.
        if (isScriptRunning) {
            return;
        }

        if (!handleEditorTransient(event)
            && !handleRecordingControl(event)
            && !handleImageCaptureControl(event)
            && !handleComponentSelection(event)
            && recorder != null && recording && !isFiltered) {
            Log.debug("recorder process event");
            try {
                recorder.record(event);
            }
            catch(RecordingFailedException e) {
                // Stop recording, but keep what we've got so far
                stopRecording(false);
                String msg = Strings.get("editor.recording.failure");
                Throwable error = e.getReason() instanceof BugReport
                    ? e.getReason() : new BugReport(msg, e.getReason());
                Log.log("Recording failure: " + error.toString());
                setStatus(error.getMessage(), error.toString(), ERROR);
                view.showWarning(msg);
            }
        }

        updateComponents(event);
    }

    private boolean handleComponentSelection(AWTEvent event) {
        Component ultimateComponent = state.getUltimateMouseComponent();
        if (ultimateComponent != null
            && !hierarchy.isFiltered(ultimateComponent)) {
            boolean keySelect =
                isKeyPress(event, selectKey, KeyEvent.SHIFT_MASK);
            boolean mouseSelect = 
                event.getID() == MouseEvent.MOUSE_PRESSED
                && AWT.isTertiaryButton(((MouseEvent)event).getModifiers());
            boolean makeReference =
                isKeyPress(event, selectKey,
                           KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK);
            
            if (keySelect || mouseSelect || makeReference) {
                Component selected = event instanceof MouseEvent
                ? InputState.getComponentAt((Component)event.getSource(),
                                            ((MouseEvent)event).getPoint())
                                            : ultimateComponent;
                selectComponent(selected, makeReference);
                return true;
            }
        }
        return false;
    }
    /** Update the state of components whose appearance depends on keyboard 
     * state.
     * @param event
     */
    private void updateComponents(AWTEvent event) {
        // Adjust the state of the assert/sample options
        if (isKeyPress(event, KC_INVERT, DONT_CARE)) {
            invertAssertions = true;
            view.setAssertOptions(waitAssertions, invertAssertions);
        }
        else if (isKeyRelease(event, KC_INVERT, DONT_CARE)) {
            invertAssertions = false;
            view.setAssertOptions(waitAssertions, invertAssertions);
        }
        else if (isKeyPress(event, KC_WAIT, DONT_CARE)) {
            waitAssertions = true;
            view.setAssertOptions(waitAssertions, invertAssertions);
        }
        else if (isKeyRelease(event, KC_WAIT, DONT_CARE)) {
            waitAssertions = false;
            view.setAssertOptions(waitAssertions, invertAssertions);
        }
    }

    private boolean handleEditorTransient(AWTEvent event) {
        // Make sure we filter any transient windows generated by the
        // script editor's frame.  This avoids some of the hierarchy event
        // NPEs present on pre-1.4 VMs.
        if (event.getID() == WindowEvent.WINDOW_OPENED
            && ((WindowEvent)event).getWindow().getParent() == view) {
            hierarchy.setFiltered(((WindowEvent)event).getWindow(), true);
            view.getComponentBrowser().refresh();
            return true;
        }
        return false;
    }
    private boolean handleImageCaptureControl(AWTEvent event) {
        Object src = event.getSource();
        boolean isComponent = event.getSource() instanceof Component;
        boolean isFiltered =
            isComponent && hierarchy.isFiltered((Component)event.getSource());
        Component ultimateComponent = state.getUltimateMouseComponent();
        if (capturingImage) {
            // Cancel an image capture on ESC
            if (isKeyRelease(event, KeyEvent.VK_ESCAPE, 0)) {
                imageCaptureCancel();
            }
            else if (captureComponent != null
                     && isKeyPress(event, KeyEvent.VK_UP, 0)
                     && !(src instanceof Window)) {
                imageCaptureSelect(true);
            }
            else if (captureComponent != null
                     && isKeyPress(event, KeyEvent.VK_DOWN, 0)
                     && !(src instanceof Window)) {
                imageCaptureSelect(false);
            }
            else if (isKeyRelease(event, captureImageKey, KeyEvent.SHIFT_MASK)
                     && !isFiltered && testScript != null
                     && ultimateComponent != null) {
                imageCapture();
            }
            return true;
        }
        else if (isKeyRelease(event, captureImageKey, KeyEvent.SHIFT_MASK)
                 && !isFiltered && testScript != null
                 && ultimateComponent != null) {
            imageCaptureStart(ultimateComponent);
            return true;
        }
        return false;
    }

    private boolean handleRecordingControl(AWTEvent e) {
        boolean editorActivated = e.getID() == WindowEvent.WINDOW_ACTIVATED
            && e.getSource() == view;
        boolean appGone = isAppLaunched()
            && countShowingWindows(null) == 0
            && (appGroup != null && appGroup.activeCount() == 0)
            && System.currentTimeMillis() - lastLaunchTime > 7500;
        if (appGone) 
            Log.debug("Code under test no longer running");
        boolean isComponent = e.getSource() instanceof Component;
        boolean isFiltered =
            isComponent && hierarchy.isFiltered((Component)e.getSource());

        if (isKeyPress(e, captureKey, KeyEvent.SHIFT_MASK)
            || editorActivated || appGone) {
            Log.debug("stop recording trigger");
            if (recording) {
                stopRecording(false);
                justStoppedRecording = true;
                if (capturingImage)
                    imageCaptureCancel();
            }
            else {
                justStoppedRecording = false;
            }
            return true;
        }
        // Start recording all events on alt+shift+F2
        else if (isKeyRelease(e, captureKey,
                              KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK)) {
            Log.debug("start recording trigger");
            if (!isFiltered && !recording && !justStoppedRecording) {
                Log.debug("Start recording events (+motion)");
                startRecording(recorders[1]);
                return true;
            }
        }
        // Start recording on shift+F2
        else if (isKeyRelease(e, captureKey, KeyEvent.SHIFT_MASK)) {
            Log.debug("start recording trigger");
            if (!isFiltered && !recording && !justStoppedRecording) {
                Log.debug("Start recording events");
                startRecording(recorders[0]);
                return true;
            }
        }
        return false;
    }

    private void selectComponent(Component c, boolean makeReference) {
        Log.debug("Selected: " + Robot.toString(c));
        // We usually want the combo box itself, not its LAF button
        if (c != null
            && (c.getParent() instanceof JComboBox)) 
            c = c.getParent();
        if (makeReference && getResolverContext() != null) {
            getResolverContext().addComponent(c);
            // FIXME tell the reference browser that the list of references
            // has changed. 
        }
        view.getComponentBrowser().setSelectedComponent(c);
    }

    private void imageCaptureStart(Component ultimateComponent) {
        Log.debug("image capture locate");
        recording = false;
        capturingImage = true;
        captureComponent = ultimateComponent;
        innermostCaptureComponent = captureComponent;
        if (captureComponent != null) {
            highlighter = new Highlighter(captureComponent);
        }
        setStatus("Image capture target is "
                  + Robot.toString(captureComponent));
    }

    private void imageCaptureCancel() {
        Log.debug("stop image capture command");
        highlighter.dispose();
        setStatus("Image capture canceled");
        captureComponent = innermostCaptureComponent = null;
        capturingImage = false;
    }

    private void imageCaptureSelect(boolean up) {
        if (up) {
            Log.debug("image capture move up");
            Component parent = captureComponent.getParent();
            if (parent != null) {
                Log.debug("Changing from "
                          + Robot.toString(captureComponent)
                          + " to " + Robot.toString(parent));
                highlighter.dispose();
                highlighter = new Highlighter(parent);
                captureComponent = parent;
            }
        }
        else {
            Log.debug("image capture move down");
            if (captureComponent instanceof Container) {
                Component[] subs = ((Container)captureComponent).getComponents();
                for (int i=0;i < subs.length;i++) {
                    if (SwingUtilities.
                        isDescendingFrom(innermostCaptureComponent,
                                         subs[i])) {
                        Log.debug("Changing from "
                                  + Robot.toString(captureComponent)
                                  + " to " + Robot.toString(subs[i]));
                        highlighter.dispose();
                        highlighter = new Highlighter(subs[i]);
                        captureComponent = subs[i];
                        break;
                    }
                }
            }
        }
        setStatus("Image capture target is "
                  + Robot.toString(captureComponent));
    }

    private void imageCapture() {
        Log.debug("image capture snapshot");
        setStatus("Capturing image...");
        view.repaint();
        highlighter.dispose();
        // Must wait for the highlight to go away
        new Thread("wait for repaint") {
            public void run() {
                while (Toolkit.getDefaultToolkit().getSystemEventQueue().
                       peekEvent() != null) {
                    try { sleep(10); } catch(InterruptedException e) { }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Step step = captureComponentImage(captureComponent);
                        if (step != null) {
                            // If we capture while recording, add it to the
                            // recorder's stream.  Otherwise, add it directly.
                            if (recorder != null) {
                                recorder.insertStep(step);
                            }
                            else {
                                addStep(step);
                            }
                        }
                        setStatus("Capturing image...done");
                        captureComponent = innermostCaptureComponent = null;
                        capturingImage = false;
                    }
                });
            }
        }.start();
    }

    /** Return a List of script filenames in the current suite. */
    private List getScripts() {
        if (testScriptList == null) {
            testScriptList = getScripts(testSuite);
        }
        return testScriptList;
    }

    /** Return a List of script filenames contained in the given Test. */
    private List getScripts(junit.framework.Test node) {
        ArrayList names = new ArrayList();
        if (node == null) {
        }
        else if (node instanceof ScriptFixture) {
            names.add(((ScriptFixture)node).getName());
        }
        else if (node instanceof junit.framework.TestSuite) {
            Enumeration e = ((junit.framework.TestSuite) node).tests();
            while (e.hasMoreElements()) {
                junit.framework.Test test =
                    (junit.framework.Test) e.nextElement();
                names.addAll(getScripts(test));
            }
        }
  /*      else if (node instanceof junit.extensions.TestDecorator) {
            junit.framework.Test base =
                ((junit.extensions.TestDecorator)node).getTest();
            names.addAll(getScripts(base));
        }
  */      //Log.debug("Test scripts under " + node + ": " + names.size());
        return names;
    }

    /** Returns the test case at the given index.  */
    private Script getScriptAt(int index) {
        List filenames = getScripts();
        if (index >= filenames.size())
            index = filenames.size() - 1;
        return new Script((String)filenames.get(index), hierarchy);
    }

    /** Create a new step and insert it at the cursor. */
    private void insertTesterCall(ComponentTester tester,
                                  Method method,
                                  Class componentClass,
                                  String id, String[] argList,
                                  boolean wait, boolean invert)
        throws NoSuchReferenceException {
        String expectedResult = "true";
        String methodName = method.getName();

        if (methodName.startsWith("assert")) {
            Assert step;
            if (id == null) {
                // Built-in ComponentTester assertion
                step = new Assert(getResolverContext(), null,
                                  method.getDeclaringClass().getName(),
                                  methodName, argList, expectedResult,
                                  invert);
            }
            else {
                // Property method on a component
                ComponentReference ref = getComponentReference(id);
                if (ref == null)
                    throw new NoSuchReferenceException(id);
                step = new Assert(getResolverContext(), null,
                                  methodName, argList,
                                  method.getDeclaringClass(),
                                  expectedResult, invert);
            }
            step.setWait(wait);
            addStep(step);
        }
        else if (methodName.startsWith("action")) {
            if (id == null) {
                // non-component action
                addStep(new Action(getResolverContext(), null, methodName, argList));
            }
            else {
                ComponentReference ref = getComponentReference(id);
                if (ref == null)
                    throw new NoSuchReferenceException(id);
                addStep(new Action(getResolverContext(), null,
                                   methodName, argList,
                                   method.getDeclaringClass()));
            }
        }
        else {
            // It's an tester-provided property method
            ComponentReference ref = getComponentReference(id);
            if (ref == null) 
                throw new NoSuchReferenceException(id);
            addStep(new Assert(getResolverContext(), null,
                               methodName, argList,
                               tester.getTestedClass(componentClass), 
                               expectedResult, invert));
        }
    }

    /** Update the UI to reflect the current state of the script's
     * execution.
     */ 
    private void reflectScriptExecutionState(StepEvent ev) {
        Step step = ev.getStep();
        String cmd = ev.getType();
        Log.debug("Got step event " + ev);
        if (cmd.equals(StepEvent.STEP_START)) {
            if (step == stopStep) {
                runner.stop();
                stopStep = null;
            }
            if (step != testScript) {
                final int row = scriptModel.getRowOf(step);
                if (row == -1) {
                    // Step not visible, ignore
                }
                else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            scriptTable.setRowSelectionInterval(row, row);
                            scriptTable.setCursorLocation(row + 1);
                            Rectangle rect = scriptTable.
                                getCellRect(row, 0, true);
                            scriptTable.scrollRectToVisible(rect);
                        }
                    });
                }
                int i = testScript.indexOf(step);
                if (i != -1) {
                    setStatus(Strings.get("RunningStep", new Object[] {
                        String.valueOf(i+1),
                        String.valueOf(testScript.size())
                    }));
                }
            }
        }
        else if (cmd.equals(StepEvent.STEP_FAILURE)
                 || cmd.equals(StepEvent.STEP_ERROR)) {
            // Make sure the table updates its colors
            int index = scriptModel.getRowOf(step);
            if (index != -1) {
                scriptModel.fireTableRowsUpdated(index, index);
            }
            setStatusForStep(step);
        }
    }

    Resolver getResolverContext() {
        return scriptTable.getScriptContext();
    }

    /** From abbot.Resolver. */
    public ComponentReference getComponentReference(String refid) {
        return getResolverContext() != null
            ? getResolverContext().getComponentReference(refid) : null;
    }

    /** From abbot.Resolver. */
    public ComponentReference getComponentReference(Component comp) {
        return getResolverContext() != null
            ? getResolverContext().getComponentReference(comp) : null;
    }

    /** From abbot.Resolver. */
    public void addComponentReference(ComponentReference ref) {
        if (getResolverContext() == null) {
            throw new RuntimeException(Strings.get("NoContext"));
        }
        getResolverContext().addComponentReference(ref);
    }

    /** From abbot.Resolver. */
    public ComponentReference addComponent(Component comp) {
        if (getResolverContext() == null) {
            throw new RuntimeException(Strings.get("NoContext"));
        }
        return getResolverContext().addComponent(comp);
    }

    /** From abbot.Resolver. */
    public Collection getComponentReferences() {
        if (getResolverContext() == null) {
            return new HashSet();
        }
        return getResolverContext().getComponentReferences();
    }

    /** From abbot.Resolver. */
    public String getContext(Step step) {
        Resolver r = getResolverContext();
        if (r != null)
            return r.getContext(step);
        return "unknown";
    }

    /** From abbot.Resolver. */
    public File getDirectory() {
        if (getResolverContext() == null) {
            return new File(System.getProperty("user.dir"));
        }
        return getResolverContext().getDirectory();
    }

    /** From abbot.Resolver. */
    public void setProperty(String name, Object value) {
        if (getResolverContext() != null) {
            getResolverContext().setProperty(name, value);
        }
    }

    /** From abbot.Resolver. */
    public Object getProperty(String name) {
        if (getResolverContext() != null) {
            return getResolverContext().getProperty(name);
        }
        return null;
    }

    /** From abbot.Resolver. */
    public ClassLoader getContextClassLoader() {
        if (getResolverContext() != null) {
            return getResolverContext().getContextClassLoader();
        }
        return Thread.currentThread().getContextClassLoader();
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public String toString() { return name; }

    private void stepSelectionChanged() {
        // ensure the stop step colorization gets cleared 
        stopStep = null;
        setStepEditor();
        setActionsEnabledState();
    }
    
    // FIXME this is slow on OSX
    private void setStepEditor(Step step) {
        final StepEditor editor;
        ignoreEvents = true;
        if (step != null
            && (editor = StepEditor.getEditor(step)) != null) {
            view.setEditor(editor);
            // make sure the script model listens to changes from the
            // step editor 
            editor.addStepChangeListener(new StepChangeListener() {
                public void stepChanged(Step step) {
                    int row = scriptModel.getRowOf(step);
                    if (row != -1)
                        scriptModel.fireTableRowsUpdated(row, row);
                }
            });
        }
        else {
            Log.debug("No editor available for '" + step + "'");
            view.setEditor(null);
        }
        ignoreEvents = false;
        // Update the component browser if the context changes
        view.getComponentBrowser().setResolver(getResolverContext());
    }

    private static String usage() {
        return ScriptEditor.class.getName() + " [suite classname]";
    }

    private class LaunchAction implements Runnable {
        private final Step which;
        private final List savedState;
        private final Runnable onCompletion;
        private final String completionMessage;
        private final boolean launch;

        private LaunchAction(Step which, List savedState, Runnable onCompletion, 
                               String completionMessage, boolean launch) {
            this.which = which;
            this.savedState = savedState;
            this.onCompletion = onCompletion;
            this.completionMessage = completionMessage;
            this.launch = launch;
        }

        public void run() {
            try {
                if (launch) {
                    if (which instanceof Script) {
                        UIContext context = which instanceof UIContext
                            ? (UIContext)which
                            : ((Script)which).getUIContext();
                        if (context != null)
                            context.launch(runner);
                    }
                }
                else {
                    runner.run(which);
                }
                if (completionMessage != null)
                    setStatus(completionMessage);
            }
            catch(Throwable e) {
                // launch didn't work, get rid of it
                if (launch) 
                    terminate();
                setStatus(e.getMessage(), getStackTrace(e), ERROR);
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    AWT.reenableHierarchy(savedState);
                    isScriptRunning = false;
                    setActionsEnabledState();
                    if (onCompletion != null) {
                        onCompletion.run();
                    }
                }
            });
        }
    }

    /**
     * This class responds to changes in the script table's selection.
     */
    private class ScriptTableSelectionHandler
        implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent ev) {
            if (ev.getValueIsAdjusting() || isScriptRunning)
                return;
            stepSelectionChanged();
        }
    }

    private void setStepEditor() {
        Step step = scriptTable.getSelectedRowCount() == 1
            ? scriptModel.getStepAt(scriptTable.getSelectedRow()) : null;
        setStepEditor(step);
        setStatusForStep(step);
        setActionsEnabledState();
    }

    /**
     * Handle the current test case/script selection from the current test
     * suite (if any).
     */
    private class ScriptSelectorItemHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange() 
                && !ignoreComboBox) {
                if (checkSaveBeforeClose()) {
                    setScript(new Script((String)e.getItem(), hierarchy));
                }
                else {
                    setScript(testScript);
                }
            }
        }
    }

    /** Provide a global editor context for anyone else that wants to display
     * an appropriate dialog message.
     */
    // FIXME move to ScriptEditorFrame
    private static ScriptEditor editor = null;

    /** This action shows an input dialog to collect arguments for a given
     * method on a ComponentTester class and adds an assertion or action to
     * the current script. 
     */
    private class TesterMethodAction
        extends EditorAction implements Comparable {
        private Method method;
        private ComponentTester tester;
        private boolean wait;

        public TesterMethodAction(ComponentTester t, Method m, boolean w) {
            super(m.getName());
            putValue(NAME, getName(m));
            putValue(SMALL_ICON, getIcon(m));
            wait = w;
            tester = t;
            method = m;
        }

        public int compareTo(Object o) {
            if (o instanceof TesterMethodAction) {
                return getName().compareTo(((TesterMethodAction)o).getName());
            }
            return 0;
        }

        public void actionPerformed(ActionEvent ev) {
            addTesterCall(method, tester, wait, getArgumentsDescription());
        }

        /** Return the human-readable menu name for the action. */
        private String getName(Method m) {
            String name = m.getName();
            // First try the resource bundle, then a system property
            String menu = Strings.get(name + ".menu", true);
            if (menu == null) {
                menu = System.getProperty(name + ".menu");
                // Default to the stripped-down method name
                if (menu == null) {
                    if (name.startsWith("action")
                        || name.startsWith("assert"))
                        menu = name.substring(6);
                    else if (name.startsWith("is"))
                        menu = name.substring(2);
                    else if (name.startsWith("get"))
                        menu = name.substring(3);
                    else
                        menu = name;
                    // Break up words if we can
                    menu = TextFormat.wordBreak(menu);
                }
            }
            return menu;
        }
    
        private Icon getIcon(Method m) {
            // This loads the icon if it's in a jar file.  Doesn't seem to work
            // when loading from a raw class file in the classpath.
            Icon icon = null;
            String path = Strings.get(m.getName() + ".icon", true);
            if (path == null) {
                path = System.getProperty(m.getName() + ".icon");
            }
            if (path != null) {
                URL url = ScriptEditor.class.getResource(path);
                if (url == null) {
                    url = ScriptEditor.class.getResource("icons/" + path + ".gif");
                }
                if (url != null) {
                    icon = new ImageIcon(url);
                }
            }
            return icon;
        }
    
        /** Provide a description of required arguments for this method. */
        private String getArgumentsDescription() {
            String name = method.getName();
            String cname = Robot.simpleClassName(method.getDeclaringClass());
            String args = Strings.get(cname + "." + name + ".args", true);
            if (args == null) {
                args = System.getProperty(cname + "." + name + ".args");
                if (args == null) {
                    args = method.toString();
                }
            }
            return args;
        }
        
        public String getName() { return (String)getValue(NAME); }
        public int hashCode() { return getName().hashCode(); }
        public boolean equals(Object o) {
            return o instanceof TesterMethodAction
                && getName().equals(((TesterMethodAction)o).getName());
        }
    }

    /** Security manager to prevent applications under test from exiting.
     * StepRunner provides one of these, but we need to do additional 
     * checking in the context of the script editor.
     */
    private class EditorSecurityManager
        extends NoExitSecurityManager {

        public void checkRead(String file) {
            // avoid annoying drive A: bug on w32
        }
        /** We do additional checking to allow exit from the editor itself. */
        public void checkExit(int status) {
            // Only allow exits by the root script editor
            if (!rootIsExiting) {
                super.checkExit(status);
            }
        }
        protected void exitCalled(int status) {
            terminate();
        }
    }

    private void hideView() {
        hiding = true;
        view.hide();
    }

    private void disposeView() {
        // Close this frame; flag to the view to allow it to be disposed
        exiting = true;
        view.dispose();
    }

    void dispose() {
        // Close any application under test
        terminate();
        normalizer.stopListening();
        hideView();
        disposeView();

        // Get rid of the security manager
        if (securityManager != null) {
            System.setSecurityManager(oldSecurityManager);
            securityManager = null;
        }
    }

    private static void bugCheck() {
        Window w = Costello.getSplashScreen();
        if (w != null) {
            // Test for bugs that the user should know about
            String[] bugs = Bugs.bugCheck(Costello.getSplashScreen());
            for (int i=0;i < bugs.length;i++) {
                String title = Strings.get("BugWarning.title");
                String msg = TextFormat.dialog(bugs[i]);
                JOptionPane.showMessageDialog(w, msg, title, 
                                              JOptionPane.WARNING_MESSAGE);
            }
        }            
    }

    /** Launch the script editor, with an argument of either a test suite
     * class or a script filename.
     */
    public static void main(String[] args) {
        try {
            args = Log.init(args);
            
            bugCheck();

            if (args.length > 1) {
                System.err.println("usage: " + usage());
                System.exit(1);
            }

            editor = new ScriptEditor();
            
            // Load the requested script or suite
            String arg = args.length == 1 ? args[0] : null;
            if (arg != null
                && new File(arg).exists()
                && Script.isScript(new File(arg))) {
                editor.setTestSuite(null);
                editor.setScript(arg);
            }
            else {
                editor.setTestSuite(arg);
                editor.setScript(0);
            }
            
            // Make sure everything currently extant is
            // ignored in the test hierarchy.
            editor.hierarchy.ignoreExisting();
            editor.view.pack();
            editor.view.show();
            // Don't start listening to events until we're done generating them
            // (the "show" above can trigger deadlocks when the framework is
            // under test).
            editor.startListening();
        }
        catch(Throwable e) {
            if (editor != null)
                editor.dispose();
            System.err.println("Unexpected exception trying to launch "
                               + "the script editor");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @return Returns whether the code under test is currently launched.
     */
    private boolean isAppLaunched() {
        if (testScript != null) {
            UIContext ctxt = testScript.getUIContext();
            if (ctxt != null)
                return ctxt.isLaunched();
        }
        return false;
    }

    private class RecordAllAction extends EditorAction {
        private Recorder recorder;
        public RecordAllAction(String actionName, Recorder rec,
                               boolean extraModifier) {
            super(actionName);
            recorder = rec;
            int mask = InputEvent.SHIFT_MASK;
            if (extraModifier)
                mask |= InputEvent.ALT_MASK;
            putValue(ACCELERATOR_KEY, 
                     KeyStroke.getKeyStroke(captureKey, mask));
        }
        public void actionPerformed(ActionEvent ev) {
            Log.debug("Menu action: start recording due to "
                      + ev.getActionCommand());
            startRecording(recorder);
        }
    }

    private class EditorAboutAction extends EditorAction {
        public EditorAboutAction() { super(ACTION_EDITOR_ABOUT); }
        public void actionPerformed(ActionEvent e) {
            view.showAboutBox();
        }
    }

    private class EditorEmailAction extends EditorAction {
        public EditorEmailAction() { super(ACTION_EDITOR_EMAIL); }
        public void actionPerformed(ActionEvent e) {
            new Thread("mailing-list") {
                public void run() {
                    try {
                        Launcher.mail(Strings.get("editor.email.list"),
                                      Strings.get("editor.email.subject"),
                                      Strings.get("editor.email.body",
                                                  new Object[] {
                                                      BugReport.getSystemInfo()
                                                  }));
                    }
                    catch(IOException e) {
                        view.showWarning(e.getMessage());
                    }
                }
            }.start();
        }
    }

    private class EditorBugReportAction extends EditorAction {
        public EditorBugReportAction() { super(ACTION_EDITOR_BUGREPORT); }
        public void actionPerformed(ActionEvent e) {
            new Thread("bug-report") {
                public void run() {
                    try {
                        Launcher.open(Strings.get("editor.submit_bug"));
                    }
                    catch(IOException e) {
                        view.showWarning(e.getMessage());
                    }
                }
            }.start();
        }
    }

    private class EditorWebsiteAction extends EditorAction {
        public EditorWebsiteAction() { super(ACTION_EDITOR_WEBSITE); }
        public void actionPerformed(ActionEvent e) {
            new Thread("mailing-list") {
                public void run() {
                    try {
                        Launcher.open(Strings.get("editor.website"));
                    }
                    catch(IOException e) {
                        view.showWarning(e.getMessage());
                    }
                }
            }.start();
        }
    }

    private class EditorUserGuideAction extends EditorAction {
        public EditorUserGuideAction() { super(ACTION_EDITOR_USERGUIDE); }
        public void actionPerformed(ActionEvent e) {
            new Thread("mailing-list") {
                public void run() {
                    try {
                        Launcher.open(Strings.get("editor.userguide"));
                    }
                    catch(IOException e) {
                        view.showWarning(e.getMessage());
                    }
                }
            }.start();
        }
    }

    private class EditorQuitAction extends EditorAction {
        public EditorQuitAction() { super(ACTION_EDITOR_QUIT); }
        public void actionPerformed(ActionEvent e) {
            quitApplication();
        }
    }
    private class ScriptNewAction extends EditorAction {
        public ScriptNewAction() { super(ACTION_SCRIPT_NEW); }
        public void actionPerformed(ActionEvent e) {
            newScript(false);
        }
    }
    private class ScriptDuplicateAction extends EditorAction {
        public ScriptDuplicateAction() { super(ACTION_SCRIPT_DUPLICATE); }
        public void actionPerformed(ActionEvent e) {
            newScript(true);
        }
    }
    private class ScriptOpenAction extends EditorAction {
        public ScriptOpenAction() { super(ACTION_SCRIPT_OPEN); }
        public void actionPerformed(ActionEvent e) {
            openScript();
        }
    }
    private class ScriptClearAction extends EditorAction {
        public ScriptClearAction() { super(ACTION_SCRIPT_CLEAR); }
        public void actionPerformed(ActionEvent e) {
            clearScript();
        }
    }
    private class ScriptDeleteAction extends EditorAction {
        public ScriptDeleteAction() { super(ACTION_SCRIPT_DELETE); }
        public void actionPerformed(ActionEvent e) {
            deleteScript();
        }
    }
    private class ScriptSaveAction extends EditorAction {
        public ScriptSaveAction() { super(ACTION_SCRIPT_SAVE); }
        public void actionPerformed(ActionEvent e) {
            saveScript();
        }
    }
    private class ScriptSaveAsAction extends EditorAction {
        public ScriptSaveAsAction() { super(ACTION_SCRIPT_SAVE_AS); }
        public void actionPerformed(ActionEvent e) {
            saveAsScript(false);
        }
    }
    private class ScriptRenameAction extends EditorAction {
        public ScriptRenameAction() { super(ACTION_SCRIPT_RENAME); }
        public void actionPerformed(ActionEvent e) {
            saveAsScript(true);
        }
    }
    private class ScriptCloseAction extends EditorAction {
        public ScriptCloseAction() { super(ACTION_SCRIPT_CLOSE); }
        public void actionPerformed(ActionEvent e) {
            closeScript();
        }
    }
    private class StepCutAction extends EditorAction {
        public StepCutAction() { super(ACTION_STEP_CUT); }
        public void actionPerformed(ActionEvent e) {
            cutSelection();
        }
    }
    private class StepMoveUpAction extends EditorAction {
        public StepMoveUpAction() { super(ACTION_STEP_MOVE_UP); }
        public void actionPerformed(ActionEvent e) {
            moveSelectionUp();
        }
    }
    private class StepMoveDownAction extends EditorAction {
        public StepMoveDownAction() { super(ACTION_STEP_MOVE_DOWN); }
        public void actionPerformed(ActionEvent e) {
            moveSelectionDown();
        }
    }
    private class StepGroupAction extends EditorAction {
        public StepGroupAction() { super(ACTION_STEP_GROUP); }
        public void actionPerformed(ActionEvent e) {
            groupSelection();
        }
    }
    private class RunAction extends EditorAction {
        public RunAction() { super(ACTION_RUN); }
        public void actionPerformed(ActionEvent e) {
            runScript(null);
        }
    }
    private class RunSelectedAction extends EditorAction {
        public RunSelectedAction() { super(ACTION_RUN_SELECTED); }
        public void actionPerformed(ActionEvent e) {
            runSelectedSteps();
        }
    }
    private class RunToAction extends EditorAction {
        public RunToAction() { super(ACTION_RUN_TO); }
        public void actionPerformed(ActionEvent e) {
            runScript(scriptTable.getSelectedStep());
        }
    }
    private class ExportHierarchyAction extends EditorAction {
        public ExportHierarchyAction() { super(ACTION_EXPORT_HIERARCHY); }
        public void actionPerformed(ActionEvent e) {
            exportHierarchy();
        }
    }
    private class SelectTestSuiteAction extends EditorAction {
        public SelectTestSuiteAction() { super(ACTION_SELECT_TESTSUITE); }
        public void actionPerformed(ActionEvent e) {
            browseTests();
        }
    }
    private class RunLaunchAction extends EditorAction {
        public RunLaunchAction() { super(ACTION_RUN_LAUNCH); }
        public void actionPerformed(ActionEvent e) {
            launch(true);
        }
    }
    private class RunTerminateAction extends EditorAction {
        public RunTerminateAction() { super(ACTION_RUN_TERMINATE); }
        public void actionPerformed(ActionEvent e) {
            terminate();
        }
    }
    private class InsertLaunchAction extends EditorAction {
        public InsertLaunchAction() { super(ACTION_INSERT_LAUNCH); }
        public void actionPerformed(ActionEvent e) {
            insertLaunch();
        }
    }
    private class InsertFixtureAction extends EditorAction {
        public InsertFixtureAction() { super(ACTION_INSERT_FIXTURE); }
        public void actionPerformed(ActionEvent e) {
            insertScript(true);
        }
    }
    private class InsertAppletAction extends EditorAction {
        public InsertAppletAction() { super(ACTION_INSERT_APPLET); }
        public void actionPerformed(ActionEvent e) {
            insertApplet();
        }
    }
    private class InsertTerminateAction extends EditorAction {
        public InsertTerminateAction() { super(ACTION_INSERT_TERMINATE); }
        public void actionPerformed(ActionEvent e) {
            insertTerminate();
        }
    }
    private class InsertCallAction extends EditorAction {
        public InsertCallAction() { super(ACTION_INSERT_CALL); }
        public void actionPerformed(ActionEvent e) {
            insertCall(false);
        }
    }
    private class InsertSampleAction extends EditorAction {
        public InsertSampleAction() { super(ACTION_INSERT_SAMPLE); }
        public void actionPerformed(ActionEvent e) {
            insertCall(true);
        }
    }
    private class InsertSequenceAction extends EditorAction {
        public InsertSequenceAction() { super(ACTION_INSERT_SEQUENCE); }
        public void actionPerformed(ActionEvent e) {
            insertSequence();
        }
    }
    private class InsertScriptAction extends EditorAction {
        public InsertScriptAction() { super(ACTION_INSERT_SCRIPT); }
        public void actionPerformed(ActionEvent e) {
            insertScript(false);
        }
    }
    private class InsertCommentAction extends EditorAction {
        public InsertCommentAction() { super(ACTION_INSERT_COMMENT); }
        public void actionPerformed(ActionEvent e) {
            insertComment();
        }
    }
    private class InsertExpressionAction extends EditorAction {
        public InsertExpressionAction() { super(ACTION_INSERT_EXPRESSION); }
        public void actionPerformed(ActionEvent e) {
            insertExpression();
        }
    }
    private class InsertAnnotationAction extends EditorAction {
        public InsertAnnotationAction() { super(ACTION_INSERT_ANNOTATION); }
        public void actionPerformed(ActionEvent e) {
            insertAnnotation();
        }
    }
    private class GetVMArgsAction extends EditorAction {
        public GetVMArgsAction() { super(ACTION_GET_VMARGS); }
        public void actionPerformed(ActionEvent e) {
            getVMArgs();
        }
    }

    private class SelectComponentAction extends EditorAction {
        public SelectComponentAction() {
            super(ACTION_SELECT_COMPONENT);
            putValue(ACCELERATOR_KEY, 
                     KeyStroke.getKeyStroke(selectKey, InputEvent.SHIFT_MASK));
        }
        public void actionPerformed(ActionEvent e) {
            // This is really only for documentation purposes; there is
            // nothing to capture if the menu item can be activated, since
            // the editor has focus when the menu is selected.
            view.showWarning(Strings.get("actions.select-component.desc"));
        }
    }

    private class CaptureComponentAction extends EditorAction {
        public CaptureComponentAction() {
            super(ACTION_CAPTURE_COMPONENT);
            putValue(ACCELERATOR_KEY, 
                     KeyStroke.getKeyStroke(selectKey,
                                            InputEvent.SHIFT_MASK
                                            |InputEvent.ALT_MASK));
        }
        public void actionPerformed(ActionEvent e) {
            // This is really only for documentation purposes; there is
            // nothing to capture if the menu item can be activated, since
            // the editor has focus when the menu is selected.
            view.showWarning(Strings.get("actions.capture-component.desc"));
        }
    }

    private class CaptureImageAction extends EditorAction {
        public CaptureImageAction() {
            super(ACTION_CAPTURE_IMAGE);
            putValue(ACCELERATOR_KEY,
                     KeyStroke.getKeyStroke(captureImageKey,
                                            InputEvent.SHIFT_MASK));
        }
        public void actionPerformed(ActionEvent e) {
            // This is really only for documentation purposes; there is
            // nothing to capture if the menu item can be activated, since
            // the editor has focus when the menu is selected.
            view.showWarning(Strings.get("actions.capture-image.desc"));
        }
    }

    private class ToggleForkedAction extends EditorToggleAction {
        public ToggleForkedAction() { super(ACTION_TOGGLE_FORKED); }
        public void actionPerformed(ActionEvent e) {
            forkedToggle();
        }
    }
    private class ToggleSlowPlaybackAction extends EditorToggleAction {
        public ToggleSlowPlaybackAction() { super(ACTION_TOGGLE_SLOW_PLAYBACK); }
        public void actionPerformed(ActionEvent e) {
            slowPlaybackToggle();
        }
    }
    private class ToggleAWTModeAction extends EditorToggleAction {
        public ToggleAWTModeAction() { super(ACTION_TOGGLE_AWT_MODE); }
        public void actionPerformed(ActionEvent e) {
            awtModeToggle();
        }
    }
    private class ToggleStopOnErrorAction extends EditorToggleAction {
        public ToggleStopOnErrorAction() { super(ACTION_TOGGLE_STOP_ON_ERROR); }
        public void actionPerformed(ActionEvent e) {
            stopOnErrorToggle();
        }
    }
    private class ToggleStopOnFailureAction extends EditorToggleAction {
        public ToggleStopOnFailureAction() { super(ACTION_TOGGLE_STOP_ON_FAILURE); }
        public void actionPerformed(ActionEvent e) {
            stopOnFailureToggle();
        }
    }

    private class EditorStepRunner extends StepRunner {
        /** We use a single runner througout the editor's lifetime, 
         * so one saved UI context will suffice.
         */
        public EditorStepRunner() {
            super(new AWTFixtureHelper());
        }
        public Hierarchy getHierarchy() { return ScriptEditor.this.hierarchy; }
        public void terminate() {
            super.terminate();
            setActionsEnabledState();
            view.getComponentBrowser().refresh();
        }
    }
}
