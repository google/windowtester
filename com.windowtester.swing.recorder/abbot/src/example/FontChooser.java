package example;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Select a font
 */

public class FontChooser extends JPanel 
    implements PropertyChangeListener, ItemListener {
    protected Font specifiedFont = new Font ("Serif", Font.PLAIN, 10);
    protected JComboBox name;
    protected JCheckBox bold, italic;
    protected NumberChooser size;
    protected static String[] availableFonts = null;
  
    public FontChooser () {
        setLayout (new GridLayout (2, 2));
        if (availableFonts == null){
            GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            availableFonts = ge.getAvailableFontFamilyNames();
        }
        name = new JComboBox (availableFonts);
        name.setSelectedItem (specifiedFont.getName ());
        name.addItemListener (this);
        add(name);
        size = new NumberChooser(1, 128, specifiedFont.getSize());
        add (size);
        size.setColumns (3);
        size.addPropertyChangeListener (this);
        add (bold = new JCheckBox ("bold"));
        bold.setSelected (specifiedFont.isBold ());
        bold.addItemListener (this);
        add (italic = new JCheckBox ("italic"));
        italic.setSelected (specifiedFont.isItalic ());
        italic.addItemListener (this);
    }

    public void setSpecifiedFont(Font f) {
        name.setSelectedItem (f.getName ());
        bold.setSelected (f.isBold ());
        italic.setSelected (f.isItalic ());
        size.setValue (f.getSize ());
        fireStateChange ();
    }

    public Font getSpecifiedFont () {
        return new Font ((String)name.getSelectedItem (), 
                         (bold.isSelected() ? Font.BOLD: 0) |
                         (italic.isSelected() ? Font.ITALIC: 0),
                         size.getValue());
    }
  
    public void itemStateChanged (ItemEvent e) {
        fireStateChange();
    }

    public void propertyChange (PropertyChangeEvent e) {
        fireStateChange();
    }

    protected PropertyChangeSupport listeners =
        new PropertyChangeSupport (this);

    public void addPropertyChangeListener(PropertyChangeListener l) {
        listeners.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.removePropertyChangeListener (l);
    }

    /** update our internal font, then tell everyone about it */
    protected void fireStateChange() {
        Font oldFont = specifiedFont;
        specifiedFont = getSpecifiedFont();
        listeners.firePropertyChange ("style", oldFont, specifiedFont);
    }

    /** Put up a frame containing a font chooser to make it easy for a script
     * to play with.
     */
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Font Chooser unit test");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        final JPanel panel = new JPanel(new BorderLayout());
        //panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        String text = "The quick brown fox jumped over the lazy dog";
        FontChooser chooser = new FontChooser();
        panel.add(chooser, BorderLayout.NORTH);
        final JLabel label = new JLabel(text);
        panel.add(label, BorderLayout.CENTER);
        label.setFont(chooser.getSpecifiedFont());
        frame.getContentPane().add(panel);
        ((JPanel)frame.getContentPane()).setBorder(new EmptyBorder(4,4,4,4));
        
        // Position the frame away from the screen edge to avoid stupid
        // toolbars and such
        frame.setLocation(new Point(50, 50));
        frame.setSize(400, 300);
        frame.pack();

        Dimension s1 = panel.getPreferredSize();
        Dimension s2 = frame.getPreferredSize();
        final int hoff = s2.height - s1.height;
        final int width = s2.width;
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                label.setFont((Font)ev.getNewValue());
                if (frame != null) {
                    Dimension size = panel.getPreferredSize();
                    size.height += hoff;
                    size.width = width;
                    frame.setSize(size);
                }
            }
        });
        frame.show();
    }
}

