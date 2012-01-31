/*
 * Created on 06.04.2005
 * by Richard Birenheide (D035816)
 *
 * Copyright SAP AG 2005
 */
package junit.extensions;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import abbot.tester.swt.Robot;

/**
 * Provides a simple fixture for PDE testing. 
 * <p/>
 * The fixture tries to close all open stuff on tearDown(). To enforce this,
 * {@link junit.framework.TestCase#tearDown()} and {@link junit.framework.TestCase#setUp()}
 * have been marked final and replaced by {@link #setUpPDE()} and {@link #tearDownPDE()}.
 * This fixture is especially meant to be used in conjunction with {@link ActivePDETestSuite}.
 * @author Richard Birenheide (D035816)
 */
public abstract class PDETestFixture extends TestCase {
	/**
	 * The display as received by {@link #setUpPDE()}.
	 */
	/*
	 * Although this member is currently not used it might turn out that it is
	 * needed for proper tearDown() later. In order to not break API later on
	 * it is introduced now.
	 */
	protected Display display = null;
	/**
	 * The current implementation uses ESC key strokes to close eventually left open
	 * stuff when a tests fails. Tom make this customizable, the number of strokes
	 * in each tear down is stored in this variable. The standard is 5. If this
	 * is too few strokes (more windows could be open) or too much (not necessary
	 * under any circumstances and causing too much overhead in long running tests)
	 * the value can be set accordingly.  
	 */
	protected int escapeStrokes = 5;
	/**
	 * Sets up the fixture.
	 * <p/>
	 * Is marked as final to enforce usage of {@link #setUpPDE()} instead.
	 * Comment from overridden method:<br/>
	 * {@inheritDoc}
	 * @throws Exception {@inheritDoc}
	 * @see junit.framework.TestCase#setUp()
	 */
	protected final void setUp() throws Exception {
		this.display = this.setUpPDE();
		if (this.display == null) {
			throw new NullPointerException("Method PDETestFixture.setUpPDE() must not return null");
		}
	}
	/**
	 * Tears down the fixture.
	 * <p/>
	 * Is marked as final to enforce usage of {@link #tearDownPDE()} instead.
	 * The method tries to close any open blocking UI elements.
	 * Comment from overridden method:<br/>
	 * {@inheritDoc}
	 * @throws Exception {@inheritDoc}
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected final void tearDown() throws Exception {
		this.tearDownPDE();
		//This is in order to close cleanly any stuff (eg. menues, modal dialogues) which puts
		//the UI thread in blocking mode.
		//FIXME This is a crude workaround. Search for open stuff instead
		//and close these.
		//Richard Birenheide: I tried to find a solution for closing open menu windows
		//programmatically but did not succeed. So I made this customizable.
		Robot robot = new Robot();
		for (int i = 0; i < escapeStrokes; i++) {
			robot.keyStroke(SWT.ESC);
		}
	}
	/**
	 * Replaces the {@link TestCase#setUp()} method.
	 * <p/>
	 * Will be called as self call within setUp().<br/>
	 * The default implementation returns {@link Display#getDefault()}. When the 
	 * fixture is used in tests setting up their own environment the method should
	 * be overridden and return the display the tests are carried out on.
	 * @return a Display to invoke calls on. Must be not null and should be the 
	 * primary display of the application.
	 * @throws Exception any Exception indicating setUp() failure.
	 */
	protected Display setUpPDE() throws Exception {
		return Display.getDefault();
	}
	/**
	 * Replaces the {@link TestCase#tearDown()} method.
	 * <p/>
	 * Will be called as first self call from tearDown().
	 * @throws Exception any Exception indicating tearDown() failure.
	 */
	protected void tearDownPDE() throws Exception {
		
	}
}
