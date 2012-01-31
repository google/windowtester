package abbot.tester.swt.eclipse;

import java.util.ResourceBundle;

import org.eclipse.swt.widgets.Display;

/**
 * abstract base class for testing wizard dialogs
 * 
 * in order to close the dialog, this class clicks Cancel 
 */
public abstract class BaseCancelDialogTester extends BaseJFaceDialogTester
{
	private static final String JFACE_BUNDLE_NAME = "org.eclipse.jface.messages";
	protected static final ResourceBundle _bundleForJFace = ResourceBundle.getBundle(JFACE_BUNDLE_NAME);
	private static final String KEY_BUTTON_CANCEL = "cancel";
	public static final String BUTTON_CANCEL = _bundleForJFace.getString(KEY_BUTTON_CANCEL);
	
    public BaseCancelDialogTester(String title, Display display)
    {
        super(title, display);
    }

    /**
     *@Override
     */
    protected void doCloseDialog(boolean ok) throws Throwable
    {
        clickButton(BUTTON_CANCEL);
    }
    
    
}
