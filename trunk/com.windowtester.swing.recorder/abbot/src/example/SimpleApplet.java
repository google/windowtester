package example;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

/** A very simple AWT-based applet.  See applet.xml and applet.hmlt.<p>
    <blockquote><code><pre>
    &lt;applet code="example.SimpleApplet" width=250 height&gt;
    &lt;/applet&gt;
    </pre></code></blockquote>
    <p>
    @author kelvinr@users.sourceforge.net, twall@users.sourceforge.net
 */

public class SimpleApplet extends Applet { 

    String msg = "This is a simple applet"; 

    public void init() { 
        Label push = new Label("Press a button"); 
        final Button hi = new Button("High"); 
        final Button lo = new Button("Low"); 
        final Button show = new Button("?");
        Component parent = this;
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        if (!(parent instanceof Frame))
            parent = new Frame("Dummy Frame");
        final Dialog dialog = new Dialog((Frame)parent, "Dialog", true);
        dialog.add(new Label("This is a dialog"));
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                we.getWindow().hide();
            }
        });
        
        // Adds labels and buttons to applet window 
        add(push); 
        add(hi); 
        add(lo); 
        add(show);
        add(new TextField("text here"));

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) { 
                if (ae.getSource() == hi) {
                    msg = "Up, up and away!"; 
                } 
                else if (ae.getSource() == lo) {
                    msg = "How low can you go?"; 
                } 
                else {
                    dialog.pack();
                    dialog.show();
                }
                repaint(); 
            }
        };
        hi.addActionListener(al); 
        lo.addActionListener(al); 
        show.addActionListener(al);

        tagThread("applet init");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tagThread("example.SimpleApplet");
            }
        });
    } 

    private void tagThread(String tag) {
        Thread thread = Thread.currentThread();
        String name = thread.getName();
        thread.setName(name + " (" + tag + ")");
    }

    public String getMessage() {
        return msg;
    }

    public void paint(Graphics g) { 
        g.drawString(getMessage(), 20, 120); 
    } 

} 

