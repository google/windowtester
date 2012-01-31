package example;

import java.awt.*;
import java.awt.event.*;

import abbot.Log;
import abbot.tester.ComponentTester;

public class AWTCode {

    private static class PopupListener extends MouseAdapter {
        PopupMenu menu;
        public PopupListener(PopupMenu menu) {
            this.menu = menu;
        }
        private void showPopup(MouseEvent e) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger())
                showPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger())
                showPopup(e);
        }
    }

    public static void main(String[] args) {
        args = Log.init(args);
        Frame frame = new Frame("AWT Code");
        MenuBar mb = new MenuBar() {
            protected void processEvent(AWTEvent e) {
                Log.debug("Got " + ComponentTester.toString(e));
                super.processEvent(e);
            }
        };
        Menu menu = new Menu("File") {
            protected void processEvent(AWTEvent e) {
                Log.debug("Got " + ComponentTester.toString(e));
                super.processEvent(e);
            }
        };
        MenuItem mi = new MenuItem("Open") {
            protected void processEvent(AWTEvent e) {
                Log.debug("Got " + ComponentTester.toString(e));
                super.processEvent(e);
            }
        };
        menu.add(mi);
        menu.add(new CheckboxMenuItem("Check Me"));
        mb.add(menu);
        TextField tf = new TextField("Text Field");
        TextArea ta = new TextArea("Text Area with wide/long text"
                                   + "\n\n\n\n\n\n\n");
        ta.setSize(200, 100);

        Panel pane = new Panel();
        // Button, Canvas, Checkbox, Choice, Label, List, Scrollbar
        // TextComponent, TextField, TextArea
        // Container, Panel, ScrollPane, Window, Frame, Dialog
        Choice choice = new Choice();
        choice.add("One"); choice.add("Two");
        List list = new List();
        list.add("One"); list.add("Two"); list.add("Three");
        ScrollPane sp = new ScrollPane();
        Canvas canvas = new Canvas();
        canvas.setSize(500, 500);
        sp.add(canvas);
        sp.setSize(100, 100);

        pane.add(new Button("Button"));
        pane.add(sp); // canvas within scrollpane
        pane.add(new Checkbox("Checkbox"));
        pane.add(choice);
        Label label = new Label("Label");
        pane.add(label);
        pane.add(list);
        pane.add(tf);
        pane.add(new Scrollbar());
        pane.add(ta);

        PopupMenu popup = new PopupMenu("MyPopupMenu");
        popup.add(mi = new MenuItem("first"));
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Got first popup item");
            }
        });
        popup.add(new MenuItem("second"));
        popup.add(new CheckboxMenuItem("check me"));
        pane.add(popup);
        pane.addMouseListener(new PopupListener(popup));

        frame.setMenuBar(mb);
        frame.add(pane);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setSize(300, 400);
        frame.show();
    }
}
