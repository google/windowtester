package abbot.tester.swt.eclipse;

import org.eclipse.swt.widgets.Display;

/**
 * abstract base class for testing dialogs
 * 
 * in order to close the dialog, this class clicks YES if there was no error or
 * No otherwise
 */
public abstract class BaseYesNoDialogTester extends BaseJFaceDialogTester
{
	private static final String KEY_BUTTON_NO = "no";
	private static final String KEY_BUTTON_YES = "yes";
	public static final String BUTTON_NO = _bundleForJFace.getString(KEY_BUTTON_NO);
	public static final String BUTTON_YES = _bundleForJFace.getString(KEY_BUTTON_YES);
    
    public BaseYesNoDialogTester(String title, Display display)
    {
        super(title, display);
    }

    /**
     * @Override
     */
    protected void doCloseDialog(boolean ok) throws Throwable
    {
        if(ok)
        {
            clickButton(BUTTON_NO);
        }
        else
        {
            clickButton(BUTTON_YES);
        }
    }
}

