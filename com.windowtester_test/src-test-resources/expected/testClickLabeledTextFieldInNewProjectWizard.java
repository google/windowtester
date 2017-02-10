package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testClickLabeledTextFieldInNewProjectWizard extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestClickLabeledTextFieldInNewProjectWizard()
			throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new FilteredTreeItemLocator("Java Project")); // ? https://fogbugz.instantiations.com/default.php?44689
		ui.click(new FilteredTreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new LabeledTextLocator("&Project name:"));
		ui.enterText("DiddyWahDiddy");
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
	}

}