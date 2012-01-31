package abbot.swt.eclipse.tests.tester.dialog;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import abbot.finder.matchers.swt.TextMatcher;
import abbot.finder.swt.TestHierarchy;
import abbot.swt.eclipse.utils.WorkbenchUtilities;
import abbot.tester.swt.MenuItemTester;
import abbot.tester.swt.eclipse.BaseCancelDialogTester;
import abbot.tester.swt.eclipse.BaseJFaceDialogTester;
import abbot.tester.swt.eclipse.BaseOKCancelDialogTester;

public class AbstractDialogTesterTest extends TestCase {
	private static final String DIALOG_TIPS_AND_TRICKS = "Tips and Tricks";
	private static final String DIALOG_WORKSPACE_LAUNCHER = "Workspace Launcher";
	private static final String MENU_FILE_SWITCHWORKSPACE = "&File/Switch &Workspace...";
	private static final String MENU_HELP_TIPSANDTRICKS = "&Help/&Tips and Tricks...";
	
	private static final String EXPECTED_ASSERTION_FAILURE_MESSAGE = "This assertion should fail and should be handled by this test.";
	
	private Display _display;
	private TestHierarchy _hierarchy;
	private MenuItemTester _menuItemTester;
	private Shell _workbench;

	public AbstractDialogTesterTest(String name) {
		super(name);
	}
	
	/**
	 * @Override
	 */
	protected void setUp() {
		WorkbenchUtilities.bringWorkbenchToFront();
		_display = Display.getDefault();
		_hierarchy = new TestHierarchy(_display);
		_workbench = WorkbenchUtilities.getWorkbenchWindow().getShell();
		_menuItemTester = new MenuItemTester();
	}
	
	/**
	 * @Override
	 */
	protected void tearDown() {
		_menuItemTester = null;
		_workbench = null;
		_hierarchy = null;
		_display = null;
	}
	
	public void testBaseJFaceDialogTester() {
		String title = DIALOG_WORKSPACE_LAUNCHER;
		BaseJFaceDialogTester dialogTester = new BaseJFaceDialogTester(title,_display) {

			/** 
			 * @Overide
			 */
			protected void invokeDialog() throws Throwable {
				_menuItemTester.actionSelectMenuItem(MENU_FILE_SWITCHWORKSPACE,_workbench,_workbench,100);
			}

			/** 
			 * @Overide
			 */
			protected void doTestDialog() throws Throwable {
				assertNotNull("The org.eclipse.jface.messages bundle was NULL.",_bundleForJFace);
			}

			/** 
			 * @Overide
			 */
			protected void doCloseDialog(boolean ok) throws Throwable {
				String KEY_BUTTON_CANCEL = "cancel";
				String BUTTON_CANCEL = _bundleForJFace.getString(KEY_BUTTON_CANCEL);
				clickButton(BUTTON_CANCEL);
			}
		};
		dialogTester.runDialog();
	}
	
	public void testBaseCancelDialogTester() {
		String title = DIALOG_WORKSPACE_LAUNCHER;
		BaseCancelDialogTester dialogTester = new BaseCancelDialogTester(title,_display) {

			/** 
			 * @Overide
			 */
			protected void invokeDialog() throws Throwable {
				_menuItemTester.actionSelectMenuItem(MENU_FILE_SWITCHWORKSPACE,_workbench,_workbench,100);
			}

			/** 
			 * @Overide
			 */
			protected void doTestDialog() throws Throwable {
				Button cancel = (Button)_finder.find(_dialogShell,new TextMatcher(BaseCancelDialogTester.BUTTON_CANCEL));
			}
		};
		dialogTester.runDialog();
	}
	
	public void testBaseOKCancelDialogTester_OK() {
		String title = DIALOG_TIPS_AND_TRICKS;
		BaseOKCancelDialogTester dialogTester = new BaseOKCancelDialogTester(title,_display) {

			/** 
			 * @Overide
			 */
			protected void invokeDialog() throws Throwable {
				_menuItemTester.actionSelectMenuItem(MENU_HELP_TIPSANDTRICKS,_workbench,_workbench,100);
			}

			/** 
			 * @Overide
			 */
			protected void doTestDialog() throws Throwable {
				Button cancel = (Button)_finder.find(_dialogShell,new TextMatcher(BaseOKCancelDialogTester.BUTTON_CANCEL));
				Button ok = (Button)_finder.find(_dialogShell,new TextMatcher(BaseOKCancelDialogTester.BUTTON_OK));
			}
		};
		dialogTester.runDialog();
	}
	
	public void testBaseOKCancelDialogTester_Cancel() {
		String title = DIALOG_WORKSPACE_LAUNCHER;
		BaseOKCancelDialogTester dialogTester = new BaseOKCancelDialogTester(title,_display) {

			/** 
			 * @Overide
			 */
			protected void invokeDialog() throws Throwable {
				_menuItemTester.actionSelectMenuItem(MENU_FILE_SWITCHWORKSPACE,_workbench,_workbench,100);
			}

			/** 
			 * @Overide
			 */
			protected void doTestDialog() throws Throwable {
				Button cancel = (Button)_finder.find(_dialogShell,new TextMatcher(BaseOKCancelDialogTester.BUTTON_CANCEL));
				Button ok = (Button)_finder.find(_dialogShell,new TextMatcher(BaseOKCancelDialogTester.BUTTON_OK));
				assertTrue(EXPECTED_ASSERTION_FAILURE_MESSAGE,false);
			}
		};
		try {
			dialogTester.runDialog();
		} catch (Throwable e) {
			return;
		}
		fail("Did not catch expected assertion failure:"+EXPECTED_ASSERTION_FAILURE_MESSAGE);
	}
	
//	public void testBaseFinishCancelDialogTester_Finish() {
//		//@todo: unimplemented test
//	}

//	public void testBaseFinishCancelDialogTester_Cancel() {
//		//@todo: unimplemented test
//	}
	
//	public void testBaseYesNoDialogTester_Yes() {
//		//@todo: unimplemented test
//  }

//  public void testBaseYesNoDialogTester_No() {
//		//@todo: unimplemented test
//  }
	
}