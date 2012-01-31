package abbot.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import abbot.*;
import abbot.finder.matchers.NameMatcher;
import abbot.finder.BasicFinder;
import abbot.editor.actions.*;
import abbot.editor.widgets.*;
import abbot.i18n.Strings;

import com.apple.mrj.*;

/**
 * Provides the primary frame for the Costello script editor.  Maintains the
 * LAF used when first created, restoring it temporarily when displaying any
 * new components. 
 *
 * FIXME needs major refactoring:
 * Export actions (via ActionMap) 
 * Use generic menu setup provided by a special array of actions
 *
 * @author Kyle Girard, twall
 */
public class ScriptEditorFrame
    extends JFrame implements EditorConstants, abbot.Version {

    private static int STEP_EDITOR_MIN_HEIGHT = 75;

    private JLabel                  currentTestSuiteLabel;
    private JTextField              testScriptDescription;
    private JButton                 testSuiteSelectionButton;
    private JButton                 runButton;
    private JComboBox               testScriptSelector;
    // script on left, step editor on right
    private JSplitPane              scriptSplit;
    // script split above, component browser below
    private JSplitPane              scriptBrowserSplit;
    private ScriptTable             scriptTable;
    private JTextArea               statusBar;
    private JDialog                 statusWindow;
    private boolean                 statusShown;
    private JTextArea               statusText;
    private ComponentBrowser 	    componentBrowser;
    private ActionMap               actionMap;
    private Preferences             prefs;
    private ImageIcon               logo;
    private JMenu insertMenu;
    private int INSERT_BASE_COUNT;
    private JMenu captureMenu;
    private JMenu actionMenu;
    private TwoStateEditorMenu assertMenu;
    private TwoStateEditorMenu waitMenu;
    private JDialog aboutBox;
    private JPanel lastEditor;
    private LookAndFeelPreserver preserver;

    /**
     * Constructs a ScriptEditorFrame with a title and a scriptable
     */
    public ScriptEditorFrame(String[][] menus, ActionMap actionMap,
                             ActionListener listener,
                             String title, ScriptTable scriptTable,
                             Preferences preferences) {
        super(title);   
        setName("ScriptEditor");
        prefs = preferences;

        this.scriptTable = scriptTable;
        this.actionMap = actionMap;
        java.net.URL url = getClass().getResource("icons/abbot.gif");
        logo = new ImageIcon(url);
        setIconImage(logo.getImage());
        // Allow us to cancel out of a close.
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        initComponents(listener);
        setJMenuBar(createMenus(menus));

        // Make sure we always use the same LAF, regardless of changes made by
        // the code under test.
        preserver = new LookAndFeelPreserver(this);
    }

    private JMenuBar createMenus(String[][] menus) {
        JMenuBar menuBar = new JMenuBar();
        for (int i=0;i < menus.length;i++) {
            String[] keys = menus[i];
            if (keys.length > 0) {
                JMenu menu = new EditorMenu(keys[0]);
                for (int j=1;j < keys.length;j++) {
                    String key = keys[j];
                    if (key == null) {
                        menu.add(new JSeparator());
                    }
                    else {
                        javax.swing.Action action = actionMap.get(key);
                        menu.add(createMenuItem(action));
                    }
                }
                if (i == menus.length-1) {
                    menuBar.add(Box.createHorizontalGlue());
                }
                menuBar.add(menu);
            }
        }
        return menuBar;
    }

    /**
     * Returns the componentBrowser.
     * @return ComponentBrowser
     */
    public ComponentBrowser getComponentBrowser() {
        return componentBrowser;
    }
    
    /**
     * Sets the componentBrowser.
     * @param componentBrowser The componentBrowser to set
     */
    public void setComponentBrowser(ComponentBrowser componentBrowser) {
        this.componentBrowser = componentBrowser;
        scriptBrowserSplit.setBottomComponent(componentBrowser);
    }

    /**
     * Returns the scriptTable.
     * @return ScriptTable
     */
    public ScriptTable getScriptTable() {
        return scriptTable;
    }

    public String getStatus() {
        return statusBar.getText();
    }

    /** Set the initial size based on saved prefs. */
    private void setInitialBounds() {
        Log.debug("bounds=" + getBounds());
        Dimension size =
            new Dimension(prefs.getIntegerProperty("width", getWidth()),
                          prefs.getIntegerProperty("height", getHeight()));
        if (size.width < 200 || size.height < 200) {
            Log.warn("Size is rather small: " + size
                     + ", using defaults");
        }
        else {
            setSize(size);
        }

        Rectangle screen = getGraphicsConfiguration().getBounds();
        int x = screen.x + (screen.width - getWidth()) / 2;
        int y = screen.y + (screen.height - getHeight()) / 2;
        Point where = new Point(prefs.getIntegerProperty("x", x),
                                prefs.getIntegerProperty("y", y));
        setLocation(where);

        int split1 = prefs.getIntegerProperty("split.script.stepeditor", -1);
        if (split1 < 10 || split1 > getHeight() - 10)
            split1 = -1;
        scriptSplit.setDividerLocation(split1);

        int split2 = prefs.getIntegerProperty("split.script.browser", -1);
        if (split2 < 10 || split2 > getWidth() - 10)
            split2 = -1;
        scriptBrowserSplit.setDividerLocation(split2);
        Log.debug("post=" + getBounds()
                  + " split1=" + split1 + " split2=" + split2);
    }

    /** Save size and position information before hiding. */
    boolean firstShow = true;
    public void show() {
        if (firstShow) {
            firstShow = false;
            setInitialBounds();
        }
        super.show();
    }

    public void hide() {
        if (isShowing()) {
            prefs.setProperty("x", String.valueOf(getX()));
            prefs.setProperty("y", String.valueOf(getY()));
            prefs.setProperty("width", String.valueOf(getWidth()));
            prefs.setProperty("height", String.valueOf(getHeight()));
            prefs.setProperty("split.script.stepeditor",
                              String.valueOf(scriptSplit.getDividerLocation()));
            prefs.setProperty("split.script.browser",
                              String.valueOf(scriptBrowserSplit.getDividerLocation()));
            prefs.save();
        }
        super.hide();
    }

    /** Set the text for the status window.  The first argument is the short
        text and the second is additional optional text to be displayed in a
        larger dialog. 
    */
    public void setStatus(final String msg, final String extended,
                          final Color color) {
        // setText is thread-safe w/r/t the dispatch thread, but setForeground
        // is not 
        statusBar.setText(msg);
        statusText.setText(msg + (extended != null
                                  ? "\n" + extended : ""));
        Runnable action = new Runnable() {
            public void run() {
                statusBar.setForeground(color);
                statusText.setForeground(color);
                if (statusWindow.isShowing()) 
                    resizeStatusWindow();
            }
        };
        if (!color.equals(statusBar.getForeground()))
            abbot.util.AWT.invokeAction(action);
    }
    
    public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        Rectangle screen = getGraphicsConfiguration().getBounds();
        pref.width = Math.min(pref.width, screen.width);
        pref.height = Math.min(pref.height, screen.height);
        return pref;
    }

    /**
     * Returns the testSuiteDescription.
     * @return JLabel
     */
    public JLabel getCurrentTestSuiteLabel() {
        return currentTestSuiteLabel;
    }

    /**
     * Returns the testScriptSelector.
     * @return JComboBox
     */
    public JComboBox getTestScriptSelector() {
        return testScriptSelector;
    }
    
    /**
     * Returns the testScriptDescription.
     * @return JTextField
     */
    public JTextField getTestScriptDescription() {
        return testScriptDescription;
    }

    private void initComponents(ActionListener al) {
        JPanel pane = (JPanel)getContentPane();

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.setBorder(new EmptyBorder(4,4,4,4));
        {
            JLabel suiteLabel = new JLabel(getString("AbbotSuite")) {
                public Dimension getMaximumSize() {
                    return super.getPreferredSize();
                }
            };
            suiteLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            currentTestSuiteLabel = new JLabel(getString("NoSuite"));
            currentTestSuiteLabel.setHorizontalAlignment(SwingConstants.LEFT);
            top.add(suiteLabel);
            top.add(Box.createHorizontalStrut(8));
            top.add(currentTestSuiteLabel);
            top.add(Box.createHorizontalGlue());
            top.add(Box.createHorizontalStrut(8));

            Action action = actionMap.get(ScriptEditor.ACTION_SELECT_TESTSUITE);
            if (action != null) {
                testSuiteSelectionButton = new EditorButton(action);
                top.add(testSuiteSelectionButton);
                top.add(Box.createHorizontalStrut(8));
            }

            action = actionMap.get(ScriptEditor.ACTION_RUN);
            if (action != null) {
                runButton = new EditorButton(action);
                top.add(runButton);
            }
        }

        JPanel center = new JPanel(new BorderLayout());
        {
            testScriptSelector = new JComboBox();
            JPanel scriptPane = new JPanel(new BorderLayout());
            {
                testScriptDescription = new abbot.editor.widgets.TextField("");
                testScriptDescription.addActionListener(al);
                String tip = TextFormat.
                    tooltip(Strings.get("editor.script_description.tip"));
                testScriptDescription.setToolTipText(tip);
                
                JScrollPane scroll = new JScrollPane(scriptTable) {
                    public Dimension getPreferredSize() {
                        return new Dimension(250, 200);
                    }
                    public Dimension getMinimumSize() {
                        return new Dimension(250, super.getMinimumSize().height);
                    }
                };
                scroll.setBorder(null);
                scroll.getViewport().setBackground(scriptTable.getBackground());
                
                scriptSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                // script gets extra space
                scriptSplit.setResizeWeight(1.0);
                scriptSplit.setDividerSize(4);
                scriptSplit.setBorder(null);
                scriptSplit.setLeftComponent(scroll);
                scriptBrowserSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                // script gets extra space
                scriptBrowserSplit.setResizeWeight(1.0);
                scriptBrowserSplit.setDividerSize(4);
                scriptBrowserSplit.setBorder(null);
                scriptBrowserSplit.setTopComponent(scriptSplit);
                scriptPane.add(testScriptDescription, BorderLayout.NORTH);
                scriptPane.add(scriptBrowserSplit, BorderLayout.CENTER);
            }
            //center.add(buttons, BorderLayout.NORTH);
            center.add(testScriptSelector, BorderLayout.NORTH);
            center.add(scriptPane, BorderLayout.CENTER);
        }

        statusText = new JTextArea("");
        statusText.setEditable(false);
        statusText.setBackground(getContentPane().getBackground());
        statusText.setColumns(80);
        statusText.setLineWrap(true);
        statusText.setWrapStyleWord(true);
        statusWindow = createStatusWindow();
        JPanel wp = (JPanel)statusWindow.getContentPane();
        wp.add(new JScrollPane(statusText));

        statusBar = new JTextArea(getString("Initializing"));
        statusBar.setEditable(false);
        statusBar.setBackground(getContentPane().getBackground());
        statusBar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                boolean hasMoreText =
                    !statusBar.getText().equals(statusText.getText());
                boolean hasWideText =
                    statusBar.getPreferredSize().width > statusBar.getSize().width;
                Log.debug("has more=" + hasMoreText + ", hasWide=" + hasWideText);
                if (hasMoreText || hasWideText) {
                    resizeStatusWindow();
                    statusWindow.show();
                    resizeStatusWindow();
                }
            }
        });
        statusBar.setToolTipText(getString("Status.tip"));

        pane.setLayout(new BorderLayout());
        pane.setBorder(new EmptyBorder(4,4,4,4));
        pane.add(top, BorderLayout.NORTH);
        pane.add(center, BorderLayout.CENTER);
        pane.add(statusBar, BorderLayout.SOUTH);

        componentBrowser = null;
    }

    /** Create a checkbox or regular menu item as appropriate. */
    private JMenuItem createMenuItem(javax.swing.Action action) {
        JMenuItem item = action instanceof EditorToggleAction
            ? (JMenuItem)new CustomCheckBoxMenuItem((EditorToggleAction)action)
            : (JMenuItem)new EditorMenuItem(action);
        return item;
    }

    public void showAboutBox() { 
        if (aboutBox == null) {
            String title = Strings.get("actions.editor-about.title");
            aboutBox = new JDialog(this, title, true);
            JPanel pane = (JPanel)aboutBox.getContentPane();
            pane.setLayout(new BorderLayout());
            pane.add(new LogoLabel(), BorderLayout.CENTER);
            JLabel label = new JLabel(VERSION);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            pane.add(label, BorderLayout.SOUTH);
            pane.setBorder(new EmptyBorder(4,4,4,4));
            aboutBox.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    aboutBox.hide();
                }
            });
            aboutBox.pack();
            aboutBox.setResizable(false);
        }
        // Center on the parent frame
        aboutBox.setLocation(getLocation().x + getWidth()/2
                             - aboutBox.getWidth()/2,
                             getLocation().y + getHeight()/2
                             - aboutBox.getHeight()*2/3);
        aboutBox.show();
    }

    public void setAssertOptions(boolean wait, boolean invert) {
        getComponentBrowser().updateAssertText(wait, invert);
        assertMenu.setSecondary(invert);
        waitMenu.setSecondary(invert);
    }

    private void populateMenu(JMenu menu, ArrayList actions) {
        Iterator iter = actions.iterator();
        while (iter.hasNext()) {
            Action action = (Action)iter.next();
            if (action == null) 
                menu.add(new JSeparator());
            else {
                JMenuItem mi = new EditorMenuItem(action);
                menu.add(mi);
            }
        }
    }

    /** Fill the menu with available actionXXX methods for the given class. */
    public void populateInsertMenu(ArrayList actions) {
        if (INSERT_BASE_COUNT == 0) {
            INSERT_BASE_COUNT = insertMenu.getItemCount();
        }
        else {
            while (insertMenu.getItemCount() > INSERT_BASE_COUNT) {
                insertMenu.remove(INSERT_BASE_COUNT);
            }
        }
        insertMenu.add(new JSeparator());
        insertMenu.add(actionMenu = new EditorMenu("menus.insert-action"));
        insertMenu.add(assertMenu =
                       new TwoStateEditorMenu("menus.insert-assert",
                                              "menus.insert-assert.neg"));
        insertMenu.add(waitMenu =
                       new TwoStateEditorMenu("menus.insert-wait",
                                              "menus.insert-wait.neg"));

        Collections.sort(actions);
        populateMenu(actionMenu, actions);
    }

    /** Fill the menu with available assertXXX methods for the given class. */
    public void populateAssertMenu(ArrayList actions) {
        assertMenu.removeAll();
        populateMenu(assertMenu, actions);
    }

    /** Same as populateAssertMenu, but makes them waits instead. */
    public void populateWaitMenu(ArrayList actions) {
        waitMenu.removeAll();
        populateMenu(waitMenu, actions);
    }

    /** Create the list of recordable GUI actions. */
    public void populateCaptureMenu(ArrayList actions) {
        captureMenu.removeAll();
        populateMenu(captureMenu, actions);
    }

    public JPanel getEditor() { return lastEditor; }
    public void setEditor(final JPanel editor) {
        if (editor != null) {
            // preserve the divider location as the editor is changed
            JScrollPane scroll = new JScrollPane(editor);
            int loc = scriptSplit.getDividerLocation();
            scroll.getViewport().setBackground(editor.getBackground());
            Dimension minSize = editor.getMinimumSize();
            minSize.height = STEP_EDITOR_MIN_HEIGHT;
            scroll.getViewport().setMinimumSize(minSize);
            scriptSplit.setRightComponent(scroll);
            // Preserve the divider location as the editor changes
            if (lastEditor != null)
                scriptSplit.setDividerLocation(loc);
        }
        else {
            scriptSplit.setRightComponent(null);
        }
        lastEditor = editor;
    }

    /** If a resource happens to be missing, use its key instead. */
    private static String getString(String key) {
        String value = Strings.get(key);
        if (value == null) 
            value = key;
        return value;
    }

    private void resizeStatusWindow() {
        statusWindow.setResizable(true);
        statusWindow.pack();
        if (!statusShown) {
            Dimension size = statusWindow.getSize();
            Point vwhere = getLocationOnScreen();
            if (size.width < getWidth()) {
                size.width = getWidth();
                statusWindow.setSize(size);
            }
            Point where = new Point();
            where.x = vwhere.x + 10;
            where.y = vwhere.y + getHeight() - size.height;
            if (where.y < vwhere.y + 10) {
                where.y = vwhere.y + 10;
            }
            statusWindow.setLocation(where);
            statusShown = true;
        }
        statusWindow.setResizable(false);
        statusWindow.repaint();
    }

    private JDialog createStatusWindow() {
        JDialog dialog = 
            new JDialog(this, Strings.get("Status.title"), false) {
                public Dimension getMaximumSize() {
                    Dimension max = super.getMaximumSize();
                    Rectangle screen = getGraphicsConfiguration().getBounds();
                    max.height = Math.min(Math.min(max.height,
                                                   screen.height * 3 / 4),
                                          ScriptEditorFrame.this.
                                          getMaximumSize().height);
                    max.width = Math.min(Math.min(max.width,
                                                  screen.width * 3 / 4),
                                         ScriptEditorFrame.this.
                                         getMaximumSize().width);
                    Log.debug("maximum size is " + max);
                    return max;
                }
                public Dimension getMinimumSize() {
                    Dimension min = super.getMinimumSize();
                    min.height = Math.max(150, min.height);
                    Log.debug("minimum size is " + min);
                    return min;
                }
                public Dimension getPreferredSize() {
                    Dimension pref = super.getPreferredSize();
                    pref.width = Math.min(pref.width,
                                          ScriptEditorFrame.this.
                                          getPreferredSize().width);
                                          
                    Log.debug("preferred size is " + pref);
                    return pref;
                }
            };
        return dialog;
    }

    /** Display a confirmation dialog. */
    public int showConfirmation(String msg) {
        return showConfirmation(msg, JOptionPane.YES_NO_OPTION);
    }

    /** Display a confirmation dialog. */
    public int showConfirmation(String msg, int opts) {
        msg = TextFormat.dialog(msg);
        return JOptionPane.showConfirmDialog(this, msg,
                                             Strings.get("Confirm"), opts);
    }

    /** Global facility for obtaining a user input String. */
    public String showInputDialog(String title, String msg,
                                  String initial) {
        msg = TextFormat.dialog(msg);
        return (String)JOptionPane.showInputDialog(this, msg, title,
                                                   JOptionPane.PLAIN_MESSAGE,
                                                   null, null, initial);
    }

    /** Global facility for message dialogs. */
    public void showMessage(String title, String msg) {
        msg = TextFormat.dialog(msg);
        JOptionPane.showMessageDialog(this, msg);
    }

    /** Global facility for warning dialog. */
    public void showWarning(String msg) {
        showWarning(Strings.get("Warning.title"), msg);
    }

    /** Global facility for warning dialog. */
    public void showWarning(String title, String msg) {
        msg = TextFormat.dialog(msg);
        JOptionPane.showMessageDialog(this, msg, title,
                                      JOptionPane.WARNING_MESSAGE);
    }

    /** Global facility for error dialogs. */
    public void showError(String msg) {
        showError(Strings.get("Error.title"), msg);
    }

    /** Global facility for error dialogs. */
    public void showError(String title, String msg) {
        msg = TextFormat.dialog(msg);
        JOptionPane.showMessageDialog(this, msg, title, 
                                      JOptionPane.ERROR_MESSAGE);
    }

    private class EditorMenu extends JMenu {
        public EditorMenu(String key) {
            super(getString(key));
            setName(key);
            // sort of a hack; update member variables when these special
            // menus get created.
            if (key.equals(MENU_INSERT)) {
                insertMenu = this;
            }
            else if (key.equals(MENU_CAPTURE)) {
                captureMenu = this;
            }

            setMnemonic(key);
        }
        protected void setMnemonic(String key) {
            Mnemonic mnemonic = Mnemonic.getMnemonic(Strings.get(key));
            mnemonic.setMnemonic(this);

            // This can go away once the properties files have been
            // updated to use ampersands instead of VK keycodes
            if (mnemonic.keycode == KeyEvent.VK_UNDEFINED) {
                int code = EditorAction.getMnemonic(key);
                if (code != KeyEvent.VK_UNDEFINED) {
                    setMnemonic(code);
                }
            }
        }
    }
    private class TwoStateEditorMenu extends EditorMenu {
        private String primary, secondary;
        public TwoStateEditorMenu(String primary, String secondary) {
            super(primary);
            this.primary = primary;
            this.secondary = secondary;
        }
        public void setSecondary(boolean state) {
            setMnemonic(state ? secondary : primary);
        }
    }
    private class EditorButton extends JButton {
        public EditorButton(Action action) {
            super(action);
            setName((String)action.getValue(EditorAction.NAME));
            Integer i = (Integer)action.getValue(EditorAction.MNEMONIC_INDEX);
            if (i != null)
                Mnemonic.setDisplayedMnemonicIndex(this, i.intValue());
        }
    }
    private class EditorMenuItem extends JMenuItem {
        public EditorMenuItem(Action action) {
            super(action);
            setName((String)action.getValue(EditorAction.NAME));
            Integer i = (Integer)action.getValue(EditorAction.MNEMONIC_INDEX);
            if (i != null)
                Mnemonic.setDisplayedMnemonicIndex(this, i.intValue());
            // prior to 1.4, the accelerator key is not automatically set
            setAccelerator((KeyStroke)action.getValue(Action.ACCELERATOR_KEY));
        }
    }
}
