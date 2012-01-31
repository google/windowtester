package abbot.tester.swt.eclipse;

import java.util.ResourceBundle;

import org.eclipse.swt.widgets.Display;

/**
 * abstract base class for testing wizard dialogs
 * 
 * in order to close the dialog, this class clicks Finish if there was no error or
 * Cancel otherwise
 */
public abstract class BaseJFaceDialogTester extends AbstractDialogTester
{
	private static final String JFACE_BUNDLE_NAME = "org.eclipse.jface.messages";
	/**
	 * The ResourceBundle for org.eclipse.jface (messages.properties)
	 * 
	 * EXAMPLE:
	 * private static final String KEY_BUTTON_CANCEL = "cancel";
	 * public static final String BUTTON_CANCEL = _bundleForJFace.getString(KEY_BUTTON_CANCEL);
	 * 
	 */
	protected static final ResourceBundle _bundleForJFace = ResourceBundle.getBundle(JFACE_BUNDLE_NAME);
	
    public BaseJFaceDialogTester(String title, Display display)
    {
        super(title, display);
    }
}
