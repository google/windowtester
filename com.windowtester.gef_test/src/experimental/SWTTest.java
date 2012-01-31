package experimental;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

import abbot.tester.swt.WidgetTester;

/**
 * 
 * IDEA: parameterize finder w/ visibility helper...
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class SWTTest extends TestCase {

	
	static interface IVisibilityTester {
		boolean isVisible(Widget w);
	}
	
//	class SetBackedVisibilityTester implements IVisibilityTester {
//
//		Set<Widget> _visibleWidgets = new HashSet<Widget>();
//		
//		void add(Widget w) {
//			_visibleWidgets.add(w);
//		}
//		
//		void remove(Widget w) {
//			_visibleWidgets.remove(w);
//		}
//	
//		public boolean isVisible(Widget w) {
//			return _visibleWidgets.contains(w);
//		}		
//	
//	}

	private static final String VISIBILITY_TAG = "visibility";


	class DataTestingVisibilityTester implements IVisibilityTester {

		private final WidgetTester _tester = new WidgetTester();
		public boolean isVisible(Widget w) {
			return _tester.getData(w, VISIBILITY_TAG) == Boolean.TRUE;
		}
		
	}
	
	
	public void test() {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				Shell shell = new Shell();
				setVisible(shell);
				assertTrue(isVisible(shell));
			}
		});
		
	}
	
	IVisibilityTester _visibilityTester = new DataTestingVisibilityTester();
	
	private boolean isVisible(Widget w) {
		return _visibilityTester.isVisible(w);
	}

	private void setVisible(Shell shell) {
		shell.setData(VISIBILITY_TAG, Boolean.TRUE);
	}
	
}
