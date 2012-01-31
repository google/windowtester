package abbot.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import junit.runner.*;
import abbot.Log;
import abbot.i18n.Strings;
import abbot.util.PathClassLoader;
import abbot.editor.widgets.ArrayEditor;
import junit.extensions.abbot.ScriptTestCollector;

/**
 * A test class selector. A simple dialog to pick the name of a test suite.
 * Allows user to set the class path searched for script suite classes.
 * Thanks to JUnit for this code.  
 */
public class TestSelector extends JDialog {
    /** If the selected item is this value, then use no test case class. */
    public static final String TEST_NONE = "<None>";

    private JList fList;
    private ArrayEditor pathEditor;
    private JButton fOk;
    private String fSelectedItem;
	
    /**
     * Renders TestFailures in a JList
     */
    static class TestCellRenderer extends DefaultListCellRenderer {
        Icon fLeafIcon;
        Icon fSuiteIcon;
		
        public TestCellRenderer() {
            fLeafIcon= UIManager.getIcon("Tree.leafIcon");
            fSuiteIcon= UIManager.getIcon("Tree.closedIcon");
        }
		
        public Component getListCellRendererComponent(
                                                      JList list, Object value, int modelIndex, 
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c= super.getListCellRendererComponent(list, value, modelIndex, isSelected, cellHasFocus);
            String displayString= displayString((String)value);
			
            if (displayString.startsWith("AllTests"))
                setIcon(fSuiteIcon);
            else
                setIcon(fLeafIcon);
				
            setText(displayString);
            return c;
        }
		
        public static String displayString(String className) {
            int typeIndex= className.lastIndexOf('.');
            if (typeIndex < 0) 
                return className;
            return className.substring(typeIndex+1) + " - " + className.substring(0, typeIndex);
        }
		
        public static boolean matchesKey(String s, char ch) {
            return ch == Character.toUpperCase(s.charAt(typeIndex(s)));
        }
		
        private static int typeIndex(String s) {
            int typeIndex= s.lastIndexOf('.');
            int i= 0;
            if (typeIndex > 0) 
                i= typeIndex+1;
            return i;
        }
    }
	
    protected class DoubleClickListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                okSelected();
            }
        }
    }
	
    protected class KeySelectListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            keySelectTestClass(e.getKeyChar());
        }
    }

    public TestSelector(Frame parent, String classPath) {
        super(parent, true);
        setSize(350, 300);
        setResizable(false);
        setLocationRelativeTo(parent);
        setTitle(Strings.get("TestSelector.title"));
		
        fList= new JList();
        fList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fList.setCellRenderer(new TestCellRenderer());
        setCollector(classPath);
        JScrollPane listScroll = new JScrollPane(fList);

        String[] paths = PathClassLoader.convertPathToFilenames(classPath);
        pathEditor = new ArrayEditor(paths);

        JScrollPane scroll = new JScrollPane(pathEditor);
        JPanel path = new JPanel(new BorderLayout());
        JLabel label = new JLabel(Strings.get("selector.classpath"));
        path.add(label, BorderLayout.NORTH);
        path.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          listScroll, path);
        split.setBorder(null);

        JButton cancel =
            new JButton(UIManager.getString("OptionPane.cancelButtonText"));
        JButton none = new JButton(Strings.get("None"));
        JLabel desc = new JLabel(Strings.get("SelectTest"));
        fOk = new JButton(UIManager.getString("OptionPane.okButtonText"));
        fOk.setEnabled(false);
        getRootPane().setDefaultButton(fOk);

        defineLayout(desc, split, fOk, none, cancel);
        addListeners(fOk, none, cancel);
    }
	
    public void setCollector(String classPath) {
        // Ensure abbot.jar is always in the class path
        String fallback = System.getProperty("java.class.path");
        if (fallback.indexOf("abbot.jar") == -1) {
            fallback = System.getProperty("abbot.class.path");
            if (fallback.indexOf("abbot.jar") == -1) {
                Log.warn("abbot.jar not found in classpath");
            }
        }
        classPath += System.getProperty("path.separator") + fallback;

        ClassLoader cl = new PathClassLoader(classPath);
        TestCollector collector = new ScriptTestCollector(cl);
        final Object[] list = createTestList(collector).toArray();
        setTestList(list);
    }

    private void setTestList(final Object[] list) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setTestList(list);
                }
            });
            return;
        }
        try {
            getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            fList.setModel(new AbstractListModel() {
                public int getSize() { return list.length; }
                public Object getElementAt(int i) { return list[i]; }
            });
        } finally {
            getParent().setCursor(Cursor.getDefaultCursor());
        }
    }

    private void addListeners(final JButton ok, JButton none, JButton cancel) {
        cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
		
        none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fSelectedItem = TEST_NONE;
                    dispose();
                }
            });
        ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okSelected();
                }
            });

        fList.addMouseListener(new DoubleClickListener());
        fList.addKeyListener(new KeySelectListener());
        fList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    checkEnableOK(e);
                }
            });

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });

        pathEditor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] values = pathEditor.getValues();
                final StringBuffer buf = new StringBuffer();
                for (int i=0;i < values.length;i++) {
                    buf.append(values[i]);
                    buf.append(System.getProperty("path.separator"));
                }
                new Thread("Available classes loader") {
                    public void run() {
                        setCollector(buf.toString());
                    }
                }.start();
            }
        });
    }
	
    private void defineLayout(Component desc, Component center,
                              Component ok, Component none, Component cancel) {
        getContentPane().setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(4,4,4,4));
        getContentPane().add(desc, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);
		
        JPanel buttons = new JPanel(new GridLayout(1, 0));
        buttons.add(ok);
        buttons.add(none);
        buttons.add(cancel);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }
	
    public void checkEnableOK(ListSelectionEvent e) {
        fOk.setEnabled(fList.getSelectedIndex() != -1);
    }
	
    public void okSelected() {
        fSelectedItem= (String)fList.getSelectedValue();
        dispose();
    }
	
    public boolean isEmpty() {
        return fList.getModel().getSize() == 0;
    }
	
    public void keySelectTestClass(char ch) {
        ListModel model= fList.getModel();
        if (!Character.isJavaIdentifierStart(ch))
            return;
        for (int i= 0; i < model.getSize(); i++) {
            String s= (String)model.getElementAt(i);
            if (TestCellRenderer.matchesKey(s, Character.toUpperCase(ch))) {
                fList.setSelectedIndex(i);
                fList.ensureIndexIsVisible(i);
                return;
            }
        }
        Toolkit.getDefaultToolkit().beep();
    }
	
    public String getSelectedItem() {
        return fSelectedItem;
    }

    private List createTestList(TestCollector collector) {
        Enumeration each= collector.collectTests();
        Vector v= new Vector();
        Vector displayVector= new Vector();
        while(each.hasMoreElements()) {
            String s= (String)each.nextElement();
            v.add(s);
            displayVector.add(TestCellRenderer.displayString(s));
        }
        if (v.size() > 0)
            Sorter.sortStrings(displayVector, 0,
                               displayVector.size()-1,
                               new ParallelSwapper(v));
        return new ArrayList(v);
    }
	
    private class ParallelSwapper implements Sorter.Swapper {
        Vector fOther;
		
        ParallelSwapper(Vector other) {
            fOther= other;
        }
        public void swap(Vector values, int left, int right) {
            Object tmp= values.elementAt(left); 
            values.setElementAt(values.elementAt(right), left); 
            values.setElementAt(tmp, right);
            Object tmp2= fOther.elementAt(left);
            fOther.setElementAt(fOther.elementAt(right), left);
            fOther.setElementAt(tmp2, right);
        }			
    }
}
