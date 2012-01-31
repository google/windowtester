package abbot.editor.editors;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.*;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.editor.widgets.ArrayEditor;
import abbot.editor.widgets.TextArea;
import abbot.editor.widgets.TextField;

/** Provide base-level step editor support with step change notification.  */
// NOTE: this should really be done with beans instead...

public abstract class StepEditor extends JPanel
    implements ActionListener, Scrollable, XMLConstants {

    private Step step;
    private JLabel label;
    JTextField description;
    private LayoutManager layout;
    private ArrayList listeners = new ArrayList();

    protected static final int MARGIN = 4;
    private boolean fieldChanging = false;

    protected static Color DEFAULT_FOREGROUND = null;
    protected static Color ERROR_FOREGROUND = Color.red;

    public StepEditor(Step step) {
        setBorder(new EmptyBorder(2,2,2,2));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        layout = getLayout();
        this.step = step;
        label = new JLabel(step.getXMLTag());
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setToolTipText(step.getUsage());
        add(label);
        description = addTextField(null, step.getDescription());
        description.setName(TAG_DESC);
        description.setToolTipText(Strings.get("editor.step.description.tip"));
        if (DEFAULT_FOREGROUND == null) {
            DEFAULT_FOREGROUND = description.getForeground();
        }
    }

    /** Keep a reasonable minimum width. */
    public Dimension getMinimumSize() {
        Dimension min = super.getMinimumSize();
        min.width = 200;
        return min;
    }

    /** Keep a reasonable minimum width. */
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = 200;
        return size;
    }

    /** We don't want to become infinitely wide due to text fields. */
    public Dimension getMaximumSize() {
        Dimension max = super.getMaximumSize();
        max.width = 400;
        return max;
    }

    protected JCheckBox addCheckBox(String title, boolean value) {
        JCheckBox cb = new JCheckBox(title);
        cb.setSelected(value);
        cb.addActionListener(this);
        add(cb);
        return cb;
    }

    /** Provide a combo box that short-circuits unnecessary and
        problem-causing event notifications.
    */
    private class ComboBox extends JComboBox {
        private JTextField editor;
        private boolean configuringEditor;
        public ComboBox() { }
        public ComboBox(ComboBoxModel model) { super(model); }
        public ComboBox(Object[] values) { super(values); }
        public void addImpl(Component c, Object constraints, int index) {
            if (c instanceof JTextField) {
                editor = (JTextField)c;
                TextField.decorate(editor);
            }
            super.addImpl(c, constraints, index);
        }

        /** Disallow recursive calls, which occur when someone sets the combo
            box contents in response to the combo box selection. */
        public void configureEditor(ComboBoxEditor editor, Object item) {
            // Avoids IllegalStateExceptions from the text field ("Attempt to
            // mutate in notification" errors).
            if (!configuringEditor) {
                configuringEditor = true;
                super.configureEditor(editor, item);
                configuringEditor = false;
            }
        }

        /** Sets the foreground color of the editor text. */
        public void setForeground(Color c) {
            if (editor != null) {
                editor.setForeground(c);
            }
        }

        /** Overridden to prevent recursive calls. */
        public void fireActionEvent() {
            if (!fieldChanging) {
                fieldChanging = true;
                super.fireActionEvent();
                fieldChanging = false;
            }
        }
        /** Overridden to prevent recursive calls. */
        public void fireItemStateChanged(ItemEvent e) {
            if (!fieldChanging) {
                fieldChanging = true;
                super.fireItemStateChanged(e);
                fieldChanging = false;
            }
        }
    }

    private static final String NONE = "<None>";
    private class RefModel
        extends AbstractListModel implements ComboBoxModel {
        private Resolver resolver;
        private boolean includeNone;
        private Object selected;
        private Collection set;
        public RefModel(Resolver r, boolean includeNone) {
            resolver = r;
            this.includeNone = includeNone;
            set = resolver.getComponentReferences();
        }
        public Object getElementAt(int i) {
            checkContents();
            if (includeNone) {
                if (i == 0)
                    return NONE;
                --i;
            }
            return ((ComponentReference)set.toArray()[i]).getID();
        }
        public int getSize() { 
            checkContents();
            int size = set.size();
            return includeNone ? size + 1 : size;
        }
        public void setSelectedItem(Object o) {
            if (o instanceof ComponentReference)
                o = ((ComponentReference)o).getID();
            else if (o == null)
                o = NONE;
            selected = o;
            checkContents();
        }
        public Object getSelectedItem() {
            checkContents();
            return selected == NONE ? null : selected;
        }
        // Always check whether this model is synched with the resolver's set
        // of references.
        private void checkContents() {
            Collection current = resolver.getComponentReferences();
            if (set.size() != current.size()) {
                set = current;
                if (!fieldChanging) {
                    fieldChanging = true;
                    fireContentsChanged(this, 0, set.size()-1);
                    fieldChanging = false;
                }
            }
        }
    }

    protected JComboBox addComponentSelector(String title, String refid,
                                             Resolver resolver, 
                                             boolean allowNone) {
        // NOTE: the combo box has no method of refreshing its contents when
        // references are added/removed/changed in the resolver
        JComboBox cb = new ComboBox(new RefModel(resolver, allowNone));
        cb.setSelectedItem(refid);
        cb.addActionListener(this);
        add(title, cb);
        return cb;
    }

    protected JComboBox addComboBox(String title,
                                    Object value, Object[] values) {
        JComboBox cb = new ComboBox(values);
        cb.setEditable(true);
        cb.setSelectedItem(value);
        cb.addActionListener(this);
        add(title, cb);
        return cb;
    }

    protected JTextField addTextField(String title, String value) {
        return addTextField(title, value, null);
    }

    protected JTextField addTextField(String title, String value,
                                      String defaultValue) {
        JTextField field =
            new abbot.editor.widgets.TextField(value, defaultValue);
        field.addActionListener(this);
        add(title, field);
        return field;
    }

    protected ArrayEditor addArrayEditor(String title, Object[] values) {
        ArrayEditor ed = new ArrayEditor(values);
        ed.addActionListener(this);
        // Make sure we resize/repaint when items are added or removed
        ed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand() != ArrayEditor.ACTION_ITEM_CHANGED) {
                    revalidate();
                    repaint();
                }
            }
        });
        add(title, ed);
        return ed;
    }

    protected JButton addButton(String title) {
        JButton button = new JButton(title);
        button.addActionListener(this);
        add(button);
        return button;
    }

    protected JTextArea addTextArea(String title, String value) {
        final TextArea text = new TextArea(value != null ? value : "");
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder(UIManager.getBorder("TextField.border"));
        text.addActionListener(this);
        add(title, text);
        return text;
    }

    /** Automatically remove the strut spacing and the component. */
    public void remove(Component comp) {
        if (getLayout() == layout) {
            Component[] children = super.getComponents();
            for (int i=1;i < children.length;i++) {
                if (children[i] == comp) {
                    super.remove(children[i-1]);
                    break;
                }
            }
        }
        super.remove(comp);
    }

    /** Auto-add a label with a component. */
    public Component add(String name, Component comp) {
        if (name != null) {
            JLabel label = new JLabel(name);
            label.setLabelFor(comp);
            add(label);
        }
        return add(comp);
    }

    /** Automatically add a vertical struct with a component. */
    public Component add(Component comp) {
        if (getLayout() == layout) {
            super.add(Box.createVerticalStrut(MARGIN));
            if (comp instanceof JComponent) {
                ((JComponent)comp).setAlignmentX(JComponent.LEFT_ALIGNMENT);
            }
        }
        return super.add(comp);
    }

    /** Respond to UI changes by updating the step data. */
    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == description) {
            // When the description is cleared (but only when entered by ENTER
            // or FOCUS events), reset it to the default
            String text = description.getText();
            String cmd = ev.getActionCommand();
            if ("".equals(text)) {
                if (!TextField.isDocumentAction(ev.getActionCommand())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            description.setText(step.getDefaultDescription());
                            description.selectAll();
                        }
                    });
                    step.setDescription(null);
                    fireStepChanged();
                }
            }
            // Only explicitly set the step data if the data is different
            // from the default.
            else if (cmd == TextField.ACTION_TEXT_REVERTED
                       || !text.equals(step.getDefaultDescription())) {
                step.setDescription(text);
                fireStepChanged();
            }
        }
    }

    public void addStepChangeListener(StepChangeListener scl) {
        synchronized(listeners) {
            listeners.add(scl);
        }
    }

    public void removeStepChangeListener(StepChangeListener scl) {
        synchronized(listeners) {
            listeners.remove(scl);
        }
    }

    /** This method should be invoked after any change to step data. */
    protected void fireStepChanged() {
        synchronized(listeners) {
            Iterator iter = listeners.iterator();
            while (iter.hasNext()) {
                StepChangeListener scl = (StepChangeListener)iter.next();
                scl.stepChanged(step);
            }
        }
        // The default description may have changed; ensure the text field is
        // up to date 
        if (!description.getText().equals(step.getDescription())) {
            description.setText(step.getDescription());
        }
    }

    /** Return the appropriate editor panel for the given Step.
     * Custom editors must be named after the step class name, and be defined
     * in the abbot.editor.editors package, e.g. abbot.script.Launch expects
     * abbot.editor.editors.LaunchEditor, abbot.script.Assert expects
     * abbot.editor.editors.AssertEditor. 
     */
    public static StepEditor getEditor(Step step) {
        Class stepClass = step.getClass();
        Log.debug("Looking up editor for " + step + " using " + stepClass);
        String className = stepClass.getName();
        className = "abbot.editor.editors."
            + className.substring(className.lastIndexOf(".") + 1) + "Editor";
        try {
            Log.debug("Trying " + className);
            Class cls = Class.forName(className);
            Class[] types = new Class[] { stepClass };
            Constructor ctor = cls.getConstructor(types);
            return (StepEditor)ctor.newInstance(new Object[] { step });
        }
        catch(ClassNotFoundException e) {
            // ignore this one
        }
        catch(Exception e) {
            Log.warn(e);
        }
        return null;
    }

    /** Always maintain the minimum width. */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    public int getScrollableBlockIncrement(Rectangle visible,
                                           int orient, int direction) {
        return orient == SwingConstants.HORIZONTAL
            ? visible.width : visible.height;
    }
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    public int getScrollableUnitIncrement(Rectangle visible,
                                          int orient, int direction) {
        return orient == SwingConstants.HORIZONTAL 
            ? 10 : description.getSize().height;
    }
    public String toString() {
        return getClass().getName() + " for " + label.getText();
    }
}
