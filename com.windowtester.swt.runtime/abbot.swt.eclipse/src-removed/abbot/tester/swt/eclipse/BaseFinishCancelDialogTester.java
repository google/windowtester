package abbot.tester.swt.eclipse;

import org.eclipse.swt.widgets.Display;

/**
 * abstract base class for testing wizard dialogs
 * 
 * in order to close the dialog, this class clicks Finish if there was no error or
 * Cancel otherwise
 */
public abstract class BaseFinishCancelDialogTester extends BaseCancelDialogTester
{
	private static final String KEY_BUTTON_BACK = "backButton";
	private static final String KEY_BUTTON_FINISH = "finish";
	private static final String KEY_BUTTON_NEXT = "nextButton";
	public static final String BUTTON_BACK = _bundleForJFace.getString(KEY_BUTTON_BACK);
	public static final String BUTTON_FINISH = _bundleForJFace.getString(KEY_BUTTON_FINISH);
	public static final String BUTTON_NEXT = _bundleForJFace.getString(KEY_BUTTON_NEXT);
	
    public BaseFinishCancelDialogTester( String title, Display display )
    {
        super( title, display );
    }

    /**
     * @Override
     */
    protected void doCloseDialog( boolean ok ) throws Throwable
    {
        if ( ok )
        {
            clickButton(BUTTON_FINISH);
        }
        else
        {
            super.doCloseDialog(ok);
        }
    }
}
