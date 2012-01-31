package abbot.tester.swt;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
/* $codepro.preprocessor.if version >= 3.1 $ */
import org.eclipse.swt.widgets.Link;
/* $codepro.preprocessor.endif $ */

public class LinkTester extends ControlTester {

    /**
     * Proxy for {@link Link.addSelectionListener(SelectionListener listener).
     */
	/* $codepro.preprocessor.if version >= 3.1 $ */
    public void addSelectionListener(final Link l, final SelectionListener listener) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.addSelectionListener(listener);
            }
        });
    }
    /* $codepro.preprocessor.endif $ */
    
    /**
     * Proxy for {@link Link#getText()}.
     */
    /* $codepro.preprocessor.if version >= 3.1 $ */
    public String getText(final Link l) {
        String result = (String) Robot.syncExec(l.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return l.getText();
            }
        });
        return result;
    }
    /* $codepro.preprocessor.endif $ */

    /**
     * Proxy for {@link Link.removeSelectionListener(SelectionListener listener).
     */
    /* $codepro.preprocessor.if version >= 3.1 $ */
    public void removeSelectionListener(final Link l, final SelectionListener listener) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.removeSelectionListener(listener);
            }
        });
    }
    /* $codepro.preprocessor.endif $ */

    /**
     * Proxy for {@link Link.setText(String text).
     */
    /* $codepro.preprocessor.if version >= 3.1 $ */
    public void setText(final Link l, final String text) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.setText(text);
            }
        });
    }
    /* $codepro.preprocessor.endif $ */

	/**
	 * Get link text offset index.
	 * @since 3.9.1
	 */
	public Rectangle getOffset(final Link l, final int index) {
        Rectangle result = (Rectangle) Robot.syncExec(l.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                try {
                	Class linkClass = l.getClass();
					Field field = linkClass.getDeclaredField("offsets");
					field.setAccessible(true);
					Point[] offsets = (Point[]) field.get(l);
					Field layoutField = linkClass.getDeclaredField("layout");
					layoutField.setAccessible(true);
					TextLayout layout = (TextLayout) layoutField.get(l);
					Point offset = offsets[index];
					boolean synthesized = false;
					if (layout == null) {
						/*
						 * in win32, the layout is coming back null.
						 * The remedy is to synthesize our own.
						 */
						synthesized = true;
						layout = synthesizeLayout(l);
					}
					
					Rectangle bounds = layout.getBounds(offset.x, offset.y);
					if (synthesized)
						layout.dispose();
					return bounds;
					
				} catch (Throwable th) {
					return null;
				}
            }
        });
        return result;
	}
	
	protected TextLayout synthesizeLayout(Link l) {
		TextLayout layout = new TextLayout(l.getDisplay());
		layout.setOrientation(SWT.LEFT_TO_RIGHT);
		layout.setText(l.getText());
		layout.setFont(l.getFont());
		return layout;
	}
	
}
