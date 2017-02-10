package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator; // ? Not generated on Mac
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition; // ? Out of order on Mac
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition; // ? Out of order
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.WT; // ? Mac only
import com.windowtester.runtime.swt.locator.LabeledLocator; // ? Mac only
import org.eclipse.swt.widgets.TabFolder; // ? Mac only
import com.windowtester.runtime.swt.locator.TabItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testClickSyntaxTabItemInAntPrefPage extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestClickSyntaxTabItemInAntPrefPage() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Preferences")); // ? 3.4M7+
		ui.click(new MenuItemLocator("Window/Preferences...")); // ? Not generated on Mac
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new FilteredTreeItemLocator("Ant")); // ? Not generated if already expanded
		ui.enterText("+"); // ? Not on Mac
		ui.keyClick(WT.ARROW_RIGHT); // ? Mac only
		ui.keyClick(WT.ARROW_RIGHT); // ? Mac only
		ui.click(new FilteredTreeItemLocator("Ant/Editor"));
		ui.click(new LabeledLocator(TabFolder.class, "Ant Editor settings:")); // ? Mac only
		ui.click(new TabItemLocator("Synta&x"));
		ui.click(new TableItemLocator("Text"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

}