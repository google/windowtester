package abbot.swt.eclipse.utils;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import abbot.finder.matchers.swt.ClassMatcher;
import abbot.finder.matchers.swt.TextMatcher;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.TestHierarchy;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.swt.eclipse.tests.TestsStrings;
import abbot.tester.swt.TreeItemTester;
import abbot.tester.swt.WidgetTester;

/**
 * This utility will invoke a new wizard by clicking File > New > Other,
 * then navigating the tree of available wizards.
 * 
 * @author Chris Jaun
 * @version $Id: InvokeNewWizard.java,v 1.1 2005-12-19 20:28:33 pq Exp $
 */

public class InvokeNewWizard extends TestCase {
	
	// testers used to find new wizard
	final static TreeItemTester treeItemTester = (TreeItemTester) WidgetTester.getTester(TreeItem.class);
	
	/**
	 * This method will invoke a new wizard by going to File > New > Other and then navigated the wizard tree.
	 * 
	 * @param wizardTreePath the tree path to the wizard
	 * @param shell the parent shell
	 */
	
	public static void invoke(String wizardTreePath, Shell shell) {
		invoke(wizardTreePath, "/", shell);
	}
	
	/**
	 * This method will invoke a new wizard by going to File > New > Other and then navigated the wizard tree.
	 * 
	 * @param wizardTreePath the tree path to the wizard
	 * @param delim a custom delimeter used when parsing path to tree item
	 * @param shell the parent shell
	 */
	// added custom delim support not working yet
	public static void invoke(String wizardTreePath, String delim, Shell shell) {
		final Shell parentShell = shell;
		final String aDelim = delim;
		final String wizardPath = wizardTreePath;
		
		// get a BasicFinder
		final Display display = parentShell.getDisplay();
		final TestHierarchy hierarchy = new TestHierarchy(display);
		final BasicFinder finder = new BasicFinder(hierarchy);
		
		// thread to drive new wizard
		final Thread wizMain = new Thread() {
			public void run() {
				WidgetTester.waitForShellShowing(TestsStrings.getString("newWizard.title"));

				try {
					Tree tree = (Tree)finder.find (new ClassMatcher(Tree.class));
					Shell newWizardShell = (Shell) finder.find(new TextMatcher(TestsStrings.getString("newWizard.title")));
					treeItemTester.actionClickTreeItem(wizardPath, aDelim, tree, 2);
					//treeItemTester.actionClickTreeItem(wizardPath, tree, newWizardShell, 100, 2);
				} catch (WidgetNotFoundException e) {
					e.printStackTrace();
					finder.printWidgets();
					fail(TestsStrings.getString("notFound.error")); //$NON-NLS-1$
				} catch (MultipleWidgetsFoundException e) {
					e.printStackTrace();
					finder.printWidgets();
					fail(TestsStrings.getString("multipleFound.error")); //$NON-NLS-1$
				}

			}
		};
		wizMain.start();
		InvokeMenuItem.invoke(TestsStrings.getString("FileNewOther.path"), parentShell);
		Utils.safeJoin(wizMain);
	}
}
