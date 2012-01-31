package example;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

/**
 * Pick a number, any number.  Well, maybe not any number.
 */

public class NumberChooser extends JPanel implements ActionListener {
    protected ArrowButton up, down;
    protected JTextField field;
  
    public NumberChooser () {
        setLayout (new BorderLayout ());
        JPanel buttons = new JPanel(new GridLayout(0, 1));
        down = new ArrowButton(ArrowButton.DOWN);
        down.addActionListener (this);
        add (field = new JTextField(4), BorderLayout.CENTER);
        field.addActionListener (this);
        up = new ArrowButton(ArrowButton.UP);
        up.addActionListener (this);
        buttons.add (up);
        buttons.add (down);
        add(buttons, BorderLayout.EAST);
    }
    public NumberChooser(int min, int max, int value){
        this();
        setMinimum(min);
        setMaximum(max);
        setValue(value);
    }

    public void requestFocus () {
        field.requestFocus ();
    }

    public void setColumns (int c) {
        field.setColumns (c);
    }

    public int getColumns () {
        return field.getColumns ();
    }

    public synchronized void setValue (int v) {
        field.setText (String.valueOf (v));
        fireValueChange (getValue ());
    }

    public int getValue () {
        try {
            return clamp (Integer.parseInt (field.getText ()));
        } catch (NumberFormatException ex) {
            return clamp (0);
        }
    }

    protected int minimum = Integer.MIN_VALUE;
  
    public synchronized void setMinimum (int m) {
        minimum = m;
    }

    public int getMinimum () {
        return minimum;
    }

    protected int maximum = Integer.MAX_VALUE;
  
    public synchronized void setMaximum (int m) {
        maximum = m;
    }

    public int getMaximum () {
        return maximum;
    }

    protected int clamp (int v) {
        return Math.max (minimum, Math.min (maximum, v));
    }

    protected int step = 1;
  
    public synchronized void setStep (int s) {
        if (s <= 0)
            throw new IllegalArgumentException ("Step too small (" + s + ").");
        step = s;
    }

    public int getStep () {
        return step;
    }

    public synchronized void actionPerformed (ActionEvent e) {
        int value = getValue ();
        if (e.getSource () == down) {
            if (value > minimum) {
                value = (value - step > value) ? minimum : clamp (value - step);
                setValue (value);
                fireValueChange (value);
            }
        } else if (e.getSource () == up) {
            if (value < maximum) {
                value = (value + step < value) ? maximum : clamp (value + step);
                setValue (value);
                fireValueChange (value);
            }
        } else if (e.getSource () == field) {
            try {
                int v = Integer.parseInt (e.getActionCommand ());
                if ((v < minimum) || (v > maximum))
                    getToolkit ().beep ();
                else
                    fireValueChange (v);
            } catch (NumberFormatException ex) {
                getToolkit ().beep ();
            }
        }
    }

    protected PropertyChangeSupport listeners =
        new PropertyChangeSupport (this);

    public void addPropertyChangeListener (PropertyChangeListener l) {
        listeners.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        listeners.removePropertyChangeListener (l);
    }

    Integer oValue = new Integer (0);

    protected void fireValueChange (int v) {
        listeners.firePropertyChange ("value", oValue, 
                                      oValue = new Integer (v));
    }

    public static void main(String[] args){
        try {
            final JFrame frame = new JFrame("NumberChooser unit test");
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    Window w = e.getWindow();
                    w.setVisible(false);
                    w.dispose();
                    System.exit(0);
                }
            });
            JPanel panel = new JPanel(new GridLayout(0, 1));
            final JLabel label = new JLabel("Enter a value");
            NumberChooser chooser = new NumberChooser();
            panel.add(label);
            panel.add(chooser);
            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent ev) {
                    System.out.println(((Integer)ev.getNewValue()).intValue());
                }
            });
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);
        }
        catch (Exception exc) {
            System.out.println("Exception: " + exc.getMessage());
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
