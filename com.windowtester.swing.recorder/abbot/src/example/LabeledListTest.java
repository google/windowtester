package example;

import java.awt.*;
import javax.swing.*;

import junit.extensions.abbot.ComponentTestFixture;
import junit.extensions.abbot.TestHelper;

import abbot.finder.Matcher;
import abbot.finder.matchers.ClassMatcher;
import abbot.script.ComponentReference;
import abbot.tester.JListTester;
import abbot.tester.JListLocation;

/** Source test code for Tutorial 2, test fixture for the LabeledList. */

public class LabeledListTest extends ComponentTestFixture {

    public void testLabelChangedOnSelectionChange() throws Throwable {
        String[] contents = { "one", "two", "three" };
        final LabeledList labeledList = new LabeledList(contents);
        showFrame(labeledList);

        // The interface abbot.finder.Matcher allows you to define whatever
        // matching specification you'd like.  We know there's only one
        // JList in the hierarchy we're searching, so we can look up by
        // class with an instance of ClassMatcher.
        Component list = getFinder().find(new ClassMatcher(JList.class));
        JListTester tester = new JListTester();

        // We could also use an instance of ClassMatcher, but this shows
        // how you can put more conditions into the Matcher.
        JLabel label = (JLabel)getFinder().find(labeledList, new Matcher() {
            public boolean matches(Component c) {
                return c.getClass().equals(JLabel.class)
                    && c.getParent() == labeledList;
            }
        });

        // Select by row index or by value
        tester.actionSelectRow(list, new JListLocation(1)); 
        // tester.actionSelect(list, new JListLocation("two"));
        assertEquals("Wrong label after selection",
                     "Selected: two", label.getText());

        tester.actionSelectRow(list, new JListLocation(2));
        assertEquals("Wrong label after selection",
                     "Selected: three", label.getText());

        tester.actionSelectRow(list, new JListLocation(0));
        assertEquals("Wrong label after selection",
                     "Selected: one", label.getText());
    }

    public LabeledListTest(String name) { super(name); }

    public static void main(String[] args) {
        TestHelper.runTests(args, LabeledListTest.class);
    }
}
