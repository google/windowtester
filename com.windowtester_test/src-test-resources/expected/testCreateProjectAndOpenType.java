package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.locator.TableItemLocator; // ? on 3.3+ this may not get picked up

public class testCreateProjectAndOpenType extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestCreateProjectAndOpenType() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("JP21");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.click(new MenuItemLocator("Navigate/Open Type..."));
		ui.wait(new ShellDisposedCondition("Progress Information")); // ? not generated any more?
		ui.wait(new ShellShowingCondition("Open Type"));
		ui.enterText("Object");
		ui.click(new TableItemLocator("Object - java.lang")); // ? Eclipse 3.2 and before
		ui.click(new TableItemLocator("Object - java.lang - [jdk1.5.0_09]")); // ? Eclipse 3.3
		ui.click(new TableItemLocator("Object - java.lang - [jdk1.6.0_01]")); // ? Eclipse 3.3
		ui.click(new TableItemLocator("Object - java.lang - [jdk1.5.0_06]")); // ? Eclipse 3.3
		ui.click(new TableItemLocator( // ? Mac
				"Object - java.lang - [JVM 1.5.0 (MacOS X Default)]")); // ? Mac
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Open Type"));
	}

}