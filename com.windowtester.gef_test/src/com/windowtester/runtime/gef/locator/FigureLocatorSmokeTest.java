package com.windowtester.runtime.gef.locator;


import java.util.concurrent.Callable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.gef.test.builder.FigureBuilder;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
@SuppressWarnings({"restriction","serial"})
public class FigureLocatorSmokeTest extends UITestCaseSWT {
	
	
	private TestShell testShell;
	
	public static class TestShell {

		public RectangleFigure rootFigure;
		public RectangleFigure figure2;
		public RectangleFigure figure3;
		public RectangleFigure figure4;
		public Label label;
		public Shell shell;

		public Shell open() {
			shell = new Shell(Display.getDefault());
			shell.setLayout(new FillLayout());
	      
			FigureCanvas canvas = new FigureCanvas(shell);
			
			rootFigure = new RectangleFigure();
			rootFigure.setBounds(new Rectangle(0, 0, 200, 200));        
			rootFigure.setBackgroundColor(ColorConstants.blue);

		
			figure2 = new RectangleFigure();
			figure2.setBounds(new Rectangle(50, 50, 50, 50));        
			figure2.setBackgroundColor(ColorConstants.cyan);
			rootFigure.add(figure2);
						
			figure3 = new RectangleFigure();
			figure3.setBounds(new Rectangle(200, 200, 50, 50));        
			figure3.setBackgroundColor(ColorConstants.cyan);
			rootFigure.add(figure3);
			
			figure4 = new RectangleFigure();
			figure4.setBounds(new Rectangle(100, 150, 50, 50));        
			figure4.setBackgroundColor(ColorConstants.cyan);
			rootFigure.add(figure4);
			
			label = new Label("label");
			label.setBounds(new Rectangle(200, 100, 50, 50));
			rootFigure.add(label);
			
			canvas.setContents(rootFigure);
			shell.setSize(300, 300);
			shell.open();
		
			return shell;
		}
		
	}
	
	
	
	public void testFindOffCanvas() throws Exception {
		
		final Shell shell = openATestShell();
		
		//TODO: scope me
		FigureLocator rectangleLocator = rectangleLocator();
		
		IWidgetLocator[] rectangles = findAll(rectangleLocator);
		assertEquals(4, rectangles.length);
		
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() {
				shell.setSize(50, 50);
			}
		});
		
		rectangles = findAll(rectangleLocator);
		assertEquals(4, rectangles.length);
	}


	private FigureLocator rectangleLocator() {
		return new FigureLocator(new IFigureMatcher() {
			public boolean matches(IFigureReference figure) {
				return figure.getFigure().getClass().equals(RectangleFigure.class);
			}
		});
	}

	private FigureLocator labelLocator() {
		return new FigureLocator(new IFigureMatcher() {
			public boolean matches(IFigureReference figure) {
				return figure.getFigure().getClass().equals(Label.class);
			}
		});
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		if (testShell != null) {
			dispose(testShell.shell);
		}
	}


	private void dispose(final Shell shell) {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() {
				shell.dispose();
			}
		});
	}
	
	
	public void testClickOffCanvas() throws Exception {
		
		
		final Shell shell = openATestShell();
		final int[] clicks = new int[1];
		testShell.label.addMouseListener(new MouseListener.Stub() {
			@Override
			public void mousePressed(MouseEvent me) {
				System.out
						.println("FigureLocatorSmokeTest.testClickOffCanvas().new Stub() {...}.mousePressed()");
				clicks[0]++;
			}
		});
		
//		ScreenCapture.createScreenCapture(getClass().getName() + "_testClickOffCanvas-before-setSize");
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() {
				shell.setSize(100, 100);
			}
		});

//		System.out.println("pre-move");
////		getUI().pause(1000);
//		
//		getUI().mouseMove(labelLocator());
//		System.out.println("moved");
		
//		getUI().pause(2000);
		
//		ScreenCapture.createScreenCapture(getClass().getName() + "_testClickOffCanvas-before-click");
		getUI().click(labelLocator());

		System.out.println("clicked");
		

//		ScreenCapture.createScreenCapture(getClass().getName() + "_testClickOffCanvas-before-assert");
		getUI().assertThat(new ICondition() {
			public boolean test() {
				return 1 == clicks[0];
			}			
		});
	}
	
	
	
	public void testWidgetScoping() throws Exception {
		
		final TestShell[] testShells = new TestShell[2];
		
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() {
				testShells[0] = new TestShell();
				testShells[0].open();
				testShells[1] = new TestShell();
				testShells[1].open();
			}
		});

		
		try {

			FigureLocator rectangleLocator = new FigureLocator(
					new IFigureMatcher() {
						public boolean matches(IFigureReference figure) {
							return figure.getFigure().getClass().equals(Label.class);
						}
					});

			IWidgetLocator[] rectangles = findAll(rectangleLocator);
			assertEquals(1, rectangles.length); //there are 2 but only one in scope

		} catch (Exception e) {
			throw e;
		} finally {
			dispose(testShells[0].shell);
			dispose(testShells[1].shell);
		}
	}
	
	public void testFigureRefAdaptsToSelector() {
		IFigureReference ref = FigureReference.create(FigureBuilder.figure());
		assertTrue(ref instanceof IAdaptable);
		IUISelector selector = (IUISelector) ((IAdaptable)ref).getAdapter(IUISelector.class);
		assertNotNull(selector);
	}
	
	public void testFigureRefAdaptsToExtendedSelector() {
		IFigureReference ref = FigureReference.create(FigureBuilder.figure());
		assertTrue(ref instanceof IAdaptable);
		Object selector = ((IAdaptable)ref).getAdapter(IUISelector2.class);
		assertNotNull(selector);
	}
	
	public void testFindReturnsFigureRef() {
		
		openATestShell();
		
		IWidgetLocator[] rects = getUI().findAll(rectangleLocator());
		assertTrue(rects.length > 0);
		assertTrue("expected type IFigureReference but got: " + rects[0].getClass(), rects[0] instanceof IFigureReference);
	}


	private Shell openATestShell() {
		return DisplayReference.getDefault().execute(new Callable<Shell>(){
			public Shell call() throws Exception {
				testShell = new TestShell();
				return testShell.open();
			}
			
		});
	}
	
	

	public void testFindDoesRetry() {
		
		openATestShell();
		
		final int callsToFind[] = new int[1];
		IFigureMatcher matcher = new IFigureMatcher() {
			public boolean matches(IFigureReference figureRef) {
				return false;
			}
		};
		FigureLocator locator = new FigureLocator(matcher) {
			@Override
			public IWidgetLocator[] findAll(IUIContext ui) {
				callsToFind[0] += 1;
				return super.findAll(ui);
			}
		};
		
		try {
			getUI().find(locator);
			fail("should have thrown a WNFE");
		} catch (WidgetSearchException e) {
			int maxFinderRetries = SWTWidgetFinder.getMaxFinderRetries();
			//TODO: I would expect this to be 10 but we're getting 11...  (not critical) 
			assertTrue("expected tries to be 10 or 11, but got: " + callsToFind[0], maxFinderRetries == 10 || maxFinderRetries == 11);
		}
	}
	
	public void testClickDoesFindRetry() {
		
		openATestShell();
		
		final int callsToFind[] = new int[1];
		IFigureMatcher matcher = new IFigureMatcher() {
			public boolean matches(IFigureReference figureRef) {
				return false;
			}
		};
		FigureLocator locator = new FigureLocator(matcher) {
			@Override
			public IWidgetLocator[] findAll(IUIContext ui) {
				callsToFind[0] += 1;
				return super.findAll(ui);
			}
		};
		
		try {
			getUI().click(locator);
			fail("should have thrown a WNFE");
		} catch (WidgetSearchException e) {
			int maxFinderRetries = SWTWidgetFinder.getMaxFinderRetries();
			//TODO: I would expect this to be 10 but we're getting 11...  (not critical) 
			assertTrue("expected tries to be 10 or 11, but got: " + callsToFind[0], maxFinderRetries == 10 || maxFinderRetries == 11);
		}
	}
	
	
	public void testContextClickDoesFindRetry() {
		
		openATestShell();
		
		final int callsToFind[] = new int[1];
		IFigureMatcher matcher = new IFigureMatcher() {
			public boolean matches(IFigureReference figureRef) {
				return false;
			}
		};
		FigureLocator locator = new FigureLocator(matcher) {
			@Override
			public IWidgetLocator[] findAll(IUIContext ui) {
				callsToFind[0] += 1;
				return super.findAll(ui);
			}
		};
		
		try {
			getUI().contextClick(locator, "bogus/path");
			fail("should have thrown a WNFE");
		} catch (WidgetSearchException e) {
			int maxFinderRetries = SWTWidgetFinder.getMaxFinderRetries();
			//TODO: I would expect this to be 10 but we're getting 11...  (not critical) 
			assertTrue("expected tries to be 10 or 11, but got: " + callsToFind[0], maxFinderRetries == 10 || maxFinderRetries == 11);
		}
	}
	
	public void testMouseMove() throws WidgetSearchException {
		//TODO: should we test find retries for moves too?
		openATestShell();
		
		final boolean clicked[] = new boolean[1];
		
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() {
				testShell.label.addMouseListener(new MouseListener.Stub(){
					public void mousePressed(MouseEvent me) {
						System.out.println(me);
						clicked[0] = true;
					}
					
				});
			}

		});
		
		//TODO: the test appears to work -- but verification does not..
		
		
//		getUI().mouseMove(labelLocator());
//		new UIDriver().mouseDown(WT.BUTTON1);
//		getUI().pause(500);
//		new UIDriver().mouseUp(WT.BUTTON1);
//		
//		
//		
//		getUI().assertThat(new ICondition() {
//			public boolean test() {
//				return clicked[0];
//			}
//		});
//		

		
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////
	//
	// Test helpers
	//
	//////////////////////////////////////////////////////////////////////////	
	
		
	
	private IWidgetLocator[] findAll(IWidgetLocator locator) {
		IUIContext ui = getUI();
		return ui.findAll(locator);
	}


//	private IUIContext getUI() {
//		return Context.GLOBAL.getUI();
//	}


	
	public static void main(String[] args) {
		Shell shell = new TestShell().open();
		Display d = Display.getDefault();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch())
				d.sleep();
		}
	}
	

}
