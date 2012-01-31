package example;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
/**
 * Source code for Tutorial 1.
 * Simple arrow button that fires one event when first clicked, then sends a
 * stream of events while held down. 
 */

public class ArrowButton extends JPanel {
    /** Directions. */
    public static final String 
        LEFT = "LEFT", 
        RIGHT = "RIGHT", 
        UP = "UP", 
        DOWN = "DOWN";

    private String direction;
    private String lastPaintedDirection;
    private static ArrowRoller roller;
    private boolean down;
    private boolean in;
    private ActionListener listeners;
    private static final int SIZE = 12;
    private static final int BORDER = 2;

    private static class ArrowRoller extends Thread {
        /** How long to wait before repeating. */
        private static final int DELAY_MS = 400;
        /** How long between repeated firings. */
        private static final int REPEAT_MS = 100;
        private boolean die = false;
        private ArrowButton target;
        private int state;

        ArrowRoller () {
            super ("ArrowRoller");
            setDaemon(true);
        }

        synchronized void abort () {
            die = true;
            notify ();
            target = null;
            ++ state;
        }

        synchronized void addListener (ArrowButton t) {
            target = t;
            ++ state;
            notify ();
        }

        synchronized void removeListener (ArrowButton t) {
            if (target == t) {
                target = null;
                ++ state;
            }
        }

        public void run () {
            while (!die) {
                try {
                    ArrowButton target;
                    int state;
                    synchronized (this) {
                        while ((target = this.target) == null)
                            wait ();
                        state = this.state;
                    }
                    sleep (DELAY_MS);
                    while (state == this.state) {
                        target.fireActionEvent ();
                        sleep (REPEAT_MS);
                    }
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public ArrowButton () {
        this(LEFT);
    }

    public ArrowButton (String direction) {
        enableEvents (AWTEvent.MOUSE_EVENT_MASK);
        setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
        lastPaintedDirection = direction;
        setDirection(direction);
    }

    public void setDirection (String d) {
        if (d.equals(LEFT))
            direction = LEFT;
        else if (d.equals(RIGHT))
            direction = RIGHT;
        else if (d.equals(UP))
            direction = UP;
        else
            direction = DOWN;
        repaint();
    }

    public String getDirection () {
        return direction;
    }

    public Dimension getMinimumSize () {
        return getPreferredSize ();
    }

    public Dimension getPreferredSize () {
        return new Dimension (SIZE, SIZE);
    }

    public Dimension getMaximumSize () {
        return getPreferredSize ();
    }

    public void update (Graphics g) {
        if (lastPaintedDirection == direction) {
            paint (g);
        }
        else {
            super.update (g);
            lastPaintedDirection = direction;
        }
    }

    public void paint (Graphics g) {
        super.paint(g);
        int w = getSize ().width, h = getSize ().height;
        g.setColor ((in && down) ? getBackground ().brighter () :
                    getBackground ().darker ());
        if (direction == LEFT) {
            g.drawLine (w - 2, 2, w - 2, h - 2);
            g.drawLine (w - 2, h - 2, 2, (h - 2) / 2);
            g.setColor ((in && down) ? getBackground ().darker () :
                        getBackground().brighter());
            g.drawLine (2, (h - 2) / 2, w - 2, 2);
        }
        else if (direction == RIGHT) {
            g.drawLine (2, 2, w - 2, (h - 2) / 2);
            g.drawLine (2, h - 2, w - 2, (h - 2) / 2);
            g.setColor ((in && down) ? getBackground ().darker () :
                        getBackground().brighter());
            g.drawLine (2, 2, 2, h - 2);
        }
        else if (direction == UP){
            g.drawLine (2, h - 2, w - 2, h - 2);
            g.drawLine (w - 2, h - 2, (w - 2) / 2, 2);
            g.setColor ((in && down) ? getBackground ().darker () :
                        getBackground().brighter());
            g.drawLine (2, h - 2, (w - 2) / 2, 2);
        }
        else {
            g.drawLine ((w - 2) / 2, h - 2, w - 2, 2);
            g.setColor ((in && down) ? getBackground ().darker () :
                        getBackground().brighter());
            g.drawLine (2, 2, (w - 2) / 2, h - 2);
            g.drawLine (2, 2, w - 2, 2);
        }
    }

    protected synchronized ArrowRoller getRoller () {
        if (roller == null) {
            roller = new ArrowRoller ();
            roller.start();
        }
        return roller;
    }

    public synchronized void destroyRoller () {
        roller.abort ();
        roller = null;
    }

    protected void processMouseEvent(MouseEvent e) {
        ArrowRoller roller = getRoller();
        switch (e.getID ()) {
        case MouseEvent.MOUSE_PRESSED:
            fireActionEvent ();
            roller.addListener (this);
            down = in = true;
            repaint ();
            break;
        case MouseEvent.MOUSE_RELEASED:
            roller.removeListener (this);
            down = in = false;
            repaint ();
            break;
        case MouseEvent.MOUSE_ENTERED:
            in = true;
            if (down) {
                roller.addListener (this);
                repaint ();
            }
            break;
        case MouseEvent.MOUSE_EXITED:
            in = false;
            if (down) {
                roller.removeListener (this);
                repaint ();
            }
            break;
        }
        super.processMouseEvent (e);
    }

    public void addActionListener (ActionListener l) {
        listeners = AWTEventMulticaster.add (l, listeners);
    }

    public void removeActionListener (ActionListener l) {
        listeners = AWTEventMulticaster.remove (l, listeners);
    }

    protected void fireActionEvent () {
        if (listeners != null){
            ActionEvent ev = new ActionEvent (this, 
                                              ActionEvent.ACTION_PERFORMED, 
                                              direction);
            listeners.actionPerformed (ev);
        }
    }
}
