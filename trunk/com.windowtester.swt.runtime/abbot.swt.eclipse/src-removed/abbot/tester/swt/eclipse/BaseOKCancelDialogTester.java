package abbot.tester.swt.eclipse;

import org.eclipse.swt.widgets.Display;

/**
 * abstract base class for testing dialogs
 * 
 * in order to close the dialog, this class clicks OK if there was no error or
 * Cancel otherwise
 */
public abstract class BaseOKCancelDialogTester extends BaseCancelDialogTester
{
	private static final String KEY_BUTTON_OK = "ok";
	public static final String BUTTON_OK = _bundleForJFace.getString(KEY_BUTTON_OK);
	
    public BaseOKCancelDialogTester( String title, Display display )
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
            clickButton(BUTTON_OK);
        }
        else
        {
            super.doCloseDialog(ok);
        }
    }
}
