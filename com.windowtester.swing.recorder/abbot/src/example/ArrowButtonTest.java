package example;

import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.JPanel;

import junit.extensions.abbot.*;
import abbot.tester.ComponentTester;

/**
 * Source code for Tutorial 1.
 * Simple unit tests for example.ArrowButton.  Also demonstrates the use of
 * ComponentTestFixture. 
 */

public class ArrowButtonTest extends ComponentTestFixture {

    private ComponentTester tester;
    protected void setUp() {
        tester = ComponentTester.getTester(ArrowButton.class);
    }

    private String gotClick;
    public void testClick() {
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                gotClick = ev.getActionCommand();                            
            }
        };

        ArrowButton left = new ArrowButton(ArrowButton.LEFT);
        ArrowButton right = new ArrowButton(ArrowButton.RIGHT);
        ArrowButton up = new ArrowButton(ArrowButton.UP);
        ArrowButton down = new ArrowButton(ArrowButton.DOWN);

        left.addActionListener(al);
        right.addActionListener(al);
        up.addActionListener(al);
        down.addActionListener(al);

        JPanel pane = new JPanel();
        pane.add(left);
        pane.add(right);
        pane.add(up);
        pane.add(down);
        // This method provided by ComponentTestFixture
        showFrame(pane);

        gotClick = null;
        tester.actionClick(left);        
        assertEquals("Action failed", ArrowButton.LEFT, gotClick);
        gotClick = null;
        tester.actionClick(right);        
        assertEquals("Action failed", ArrowButton.RIGHT, gotClick);
        gotClick = null;
        tester.actionClick(up);        
        assertEquals("Action failed", ArrowButton.UP, gotClick);
        gotClick = null;
        tester.actionClick(down);        
        assertEquals("Action failed", ArrowButton.DOWN, gotClick);
    }

    private int count = 0;
    public void testRepeatedFire() {
        ArrowButton arrow = new ArrowButton(ArrowButton.LEFT);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                ++count;
            }
        };
        arrow.addActionListener(al);
        showFrame(arrow);

        // Hold the button down for 5 seconds
        tester.mousePress(arrow);
        tester.actionDelay(5000);
        tester.mouseRelease();
        assertTrue("Didn't get any repeated events", count > 1);
    }

    public ArrowButtonTest(String name) { super(name); }

    public static void main(String[] args) {
        TestHelper.runTests(args, ArrowButtonTest.class);
    }
}
