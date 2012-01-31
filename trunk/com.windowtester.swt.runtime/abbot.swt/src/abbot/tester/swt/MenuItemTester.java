package abbot.tester.swt;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.runtime.swt.internal.util.TextUtils;

/**
 * Allows menu items to be automatically selected and expanded.
 *
 * @version $Id: MenuItemTester.java,v 1.3 2007-11-27 17:17:39 pq Exp $
 */
public class MenuItemTester extends ItemTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	public static final String ARM_LISTENER_NAME = "a4sArmListener";
	public static final String SELECTION_LISTENER_NAME = "a4sSelectionListener";
	public static final String WATCHER_NAME = "a4sWatcher";
	public static final int PATH_CLICKING_WAIT_TIME = 500000;
    
    public static final String DEFAULT_MENUITEM_PATH_DELIMITER = "/";
	
	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */
	/**
	 * Proxy for {@link MenuItem#getAccelerator()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the accelerator.
	 */
	public int getAccelerator(final MenuItem item){
		Integer result = (Integer) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(item.getAccelerator());				
			}
		});
		return result.intValue();	
	}
	/**
	 * Proxy for {@link MenuItem#getEnabled()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's enabled state.
	 */
	public boolean getEnabled(final MenuItem item){
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(item.getEnabled());				
			}
		});
		return result.booleanValue();	
	}
	/**
	 * Proxy for {@link MenuItem#getSelection()}.
	 * <p/>
	 * @param item the item under test.
	 * @return true if the item is selected.
	 */
	public boolean getSelection(final MenuItem item){
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(item.getSelection());				
			}
		});
		return result.booleanValue();
	}
	/**
	 * Proxy for {@link MenuItem#getMenu()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's menu.
	 */
	public Menu getMenu(final MenuItem item){
		Menu result = (Menu) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getMenu();				
			}
		});
		return result;	
	}
	/**
	 * Proxy for {@link MenuItem#getParent()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's parent.
	 */
	public Menu getParent(final MenuItem item){
		Menu result = (Menu) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}
	/**
	 * Proxy for {@link MenuItem#isEnabled()}.
	 * <p/>
	 * @param item the item under test.
	 * @return true if the item is enabled.
	 */
	public boolean isEnabled(final MenuItem item) {
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(item.isEnabled());				
			}
		});
		return result.booleanValue();
	}
	/**
	 * Computes the bounds of the item given.
	 * <p/>
	 * @param item the item under test.
	 * @return the bounds of the item.
	 */
	public Rectangle getBounds(final MenuItem item){
		Rectangle result = (Rectangle) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return SWTWorkarounds.getBounds(item);				
			}
		});
		return result;
	}
	/* end getters */
    
    // returns the path for the given menu item
    public String getPathString(MenuItem item){
        return getPathString(item,DEFAULT_MENUITEM_PATH_DELIMITER);
    }
    
    public String getPathString(MenuItem item, String delimiter){
        String path = "";
        Menu parent;
        while(item!=null){
        	//!pq: adding fix to escape use of delimiter in menu items
            path= TextUtils.escapeSlashes(getText(item))+delimiter+path;
            parent = getParent(item);
            item = ((MenuTester)getTester(Menu.class)).getParentItem(parent);
        }
        path = path.substring(0,path.length()-1);
        return path;
    }
    
//    public void actionSelectMenuItem(final String path, final Control parentControl, final Decorations parentDecorations, final int delay) {
//        actionSelectMenuItem(path,parentControl,parentDecorations,delay,DEFAULT_MENUITEM_PATH_DELIMITER);
//    }
//    
//    public void actionSelectMenuItem(final String path, final Control parentControl, final Decorations parentDecorations, final int delay, String delimiter) 
//    {
//        final boolean [] done = {false};
//        final StringBuffer debugBuffer = new StringBuffer();
//        debugBuffer.append("PATH:[");
//        debugBuffer.append(path);
//        debugBuffer.append("]\n");
//        final StringTokenizer st = new StringTokenizer(path,delimiter);
//        final int expectedNumberOfArmEvents = st.countTokens();
//        final List watchers = new ArrayList(expectedNumberOfArmEvents);
//        //final List<Watcher> watchers = new ArrayList<Watcher>(expectedNumberOfArmEvents);
//        for (int i = 0; i < expectedNumberOfArmEvents; i++) {
//            watchers.add(i,new Watcher(debugBuffer));
//        }
//        Thread t = new Thread(){
//            public void run(){
//                actionClickMenuItemByPathImp(st,parentControl,parentDecorations, delay, watchers,debugBuffer,done);
//                Log.log("Leaving MenuItemTester thread for:"+path);
//            }
//        };
//        t.start();
//        wait(new Condition() {
//
//            public boolean test() {
//                return (done[0] && (getNumberOfArmedEvents(watchers) == expectedNumberOfArmEvents));
//            }
//            
//            public String toString() {
//                return path + " to have "+expectedNumberOfArmEvents+"SWT.Arm events\n\nfor path:"
//                +path+"\n\n"+debugBuffer.toString();
//            }
//            
//        });
//    }
	
//	/**
//	 * Clicks each MenuItem along the path.  Use '/' as the path delimiter, and 
//	 * note that "/item1/" resolves to a path of length 3, with item texts "", "item1", and "".
//	 * In addition, a test will freeze if clicking the last item in the path
//	 * opens another menu.
//	 * 
//	 * @param path the path to the menu item
//	 * @param parentControl the Control that owns the root pop-up menu, or null if not popup
//	 * @param parentDecorations the parent Decorations
//	 * @param delay time (in ms) to wait between item clicks
//	 * @deprecated Use actionSelectMenuItem
//	 */
//	// TODO: resolve naming issues for this method (see email), refactor to use new 
//	// matcher API, limit search to menu container as appropriate, determine when
//	// to quit	
//	// not threadsafe
//	public void actionClickMenuItemByPath(final String path, final Control parentControl, 
//			final Decorations parentDecorations, final int delay){
//		System.out.println ("You should call actionSelectMenuItem instead.  This method will be going away soon.");
//		actionSelectMenuItem(path, parentControl,  parentDecorations,  delay);
//	}
    
//    private void actionClickMenuItemByPathImp(StringTokenizer path, Control parentControl, Decorations parentDecorations, int delay, List watchers, StringBuffer buffer, boolean [] done){
//    //private void actionClickMenuItemByPathImp(StringTokenizer path, Control parentControl, Decorations parentDecorations, int delay, List<Watcher> watchers, StringBuffer buffer){
//        //  rt-click control if menu is a popup
//        if(parentControl != null){
//            Rectangle bounds = ((ControlTester)WidgetTester.getTester(Control.class)).
//                getBounds(parentControl);
//            actionClick(parentControl,bounds.x/2,bounds.y/2,"BUTTON1");
//        }
//        
//        MenuItem lastItem = null;
//        int watcherIndex = 0;
//        try {
//            do {
//                String token = path.nextToken(); 
//                lastItem = resolveAndClickItem(token, parentControl, parentDecorations, ((lastItem==null) ? (Widget)parentDecorations : (Widget)lastItem),(Watcher)watchers.get(watcherIndex));
//                actionDelay(delay);
//                watcherIndex++;
//            } while(path.hasMoreTokens());
//        } catch (WidgetNotFoundException e) {
//            e.printStackTrace();
//            buffer.append("\nWidgetNotFoundException:");
//            buffer.append(e.getMessage());
//            int numberOfArmedMenus = getNumberOfArmedEvents(watchers);
//            escapeArmedMenus(numberOfArmedMenus);
//        } catch (MultipleWidgetsFoundException e) {
//            e.printStackTrace();
//            buffer.append("\nMultipleWidgetsFoundException:");
//            buffer.append(e.getMessage());
//            int numberOfArmedMenus = getNumberOfArmedEvents(watchers);
//            escapeArmedMenus(numberOfArmedMenus);
//        } finally {
//            done[0] = true;
//        }
//    }
//	
//    private MenuItem resolveAndClickItem(String text, Control parentControl, 
//            Decorations parentDecorations, Widget parent, Watcher watcher) 
//        throws WidgetNotFoundException, MultipleWidgetsFoundException
//    {
//        BasicFinder finder = new BasicFinder(new TestHierarchy(parentDecorations.getDisplay()));
//        MenuItem item = null;
//        item = (MenuItem)finder.find(parent, new TextMatcher(text));
//        addWatcher(item, watcher);
//        actionClick(item);
//        final MenuItem itemT = item;
//        wait(new Condition() {
//            public boolean test() {
//                return itemT.isDisposed() || isArmedOrSelected(itemT);
//            }
//        }, PATH_CLICKING_WAIT_TIME);
//        
//        if(item != null && !item.isDisposed()) {
//            removeWatcher(item);
//        }
//        return item;
//    }
    
    private void escapeArmedMenus(int numberOfArmEvents) {
        for (int i = 0; i <= numberOfArmEvents;i++) {
            actionDelay(200);
            keyPress(SWT.ESC);
            keyRelease(SWT.ESC);
        }
    }

    // Listener to signal when a menu item has been clicked
    private class Watcher implements Listener{
        private volatile boolean _gotArm = false;
        //private volatile boolean _gotSelection = false;
        private StringBuffer _debugBuffer;
        
        public Watcher(StringBuffer buffer) {
            _debugBuffer = buffer;
        }
        
        public void handleEvent(org.eclipse.swt.widgets.Event e){
            if(e.type==SWT.Arm){
                _debugBuffer.append("\n[SWT.Arm]:");
                _debugBuffer.append(e);
                _gotArm = true;
            }  
            if(e.type==SWT.Selection) {
                _debugBuffer.append("\n[SWT.Selection]:");
                _debugBuffer.append(e);
                //_gotSelection= true;
            }    
        }
        
        public boolean gotArmEvent() {return _gotArm;}
        //public boolean gotSelectionEvent() {return _gotSelection;}
    }
	
    // adds a watcher-listener to the given menu item
    private void addWatcher(final MenuItem item, final Watcher watcher){
        item.getDisplay().syncExec(new Runnable(){
            public void run(){
                item.setData(WATCHER_NAME,watcher);
                item.addListener(SWT.Arm,watcher);
                item.addListener(SWT.Selection,watcher);
            }
        });
    }
    
    // removes the given item's watcher-listener
    private void removeWatcher(final MenuItem item){
        item.getDisplay().syncExec(new Runnable(){
            public void run(){
                Watcher watcher = (Watcher)item.getData(WATCHER_NAME);
                if(watcher!=null) {
                    item.removeListener(SWT.Arm,watcher);
                    item.removeListener(SWT.Selection, watcher);
                }
            }
        });
    }
    
    private int getNumberOfArmedEvents(List watchers) {
    //private int getNumberOfArmedEvents(List <Watcher> watchers) {
        int numberOfArmedEvents = 0;
        Iterator it = watchers.iterator();
        while (it.hasNext()) {
        //for (Watcher watcher : watchers) {
            Watcher watcher = (Watcher)it.next();
            if (watcher.gotArmEvent()) {
                numberOfArmedEvents++;
            }
        }
        return numberOfArmedEvents;
    }
	
	// tests whether the given item has received the Arm or Selection event
	private boolean isArmedOrSelected(final MenuItem item){
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				if (item.isDisposed()) {
					return Boolean.valueOf(false);
				}
				Watcher lWatcher = (Watcher)item.getData(WATCHER_NAME);
				return Boolean.valueOf((lWatcher == null) ? false : lWatcher.gotArmEvent());
			}
		});
		return result.booleanValue();
	}
	
	/**
	 * Factory method.
	 */
	public static MenuItemTester getMenuItemTester() {
		return (MenuItemTester)(getTester(MenuItem.class));
	}

//	/**
//	 * Get an instrumented <code>MenuItem</code> from its <code>id</code> 
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>MenuItem</code> must be unique and findable with param.
//	 */
//	public static MenuItem getInstrumentedMenuItem(String id) {
//		return getInstrumentedMenuItem(id, null);
//	}
//
//	/**
//	 * Get an instrumented <code>MenuItem</code> from its <code>id</code>
//	 * and the <code>title</code> of its shell (e.g. of the wizard
//	 * containing it). 
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>MenuItem</code> must be unique and findable with param.
//	 */
//	public static MenuItem getInstrumentedMenuItem(String id, String title) {
//		return getInstrumentedMenuItem(id, title, null);
//	}	
//	
//	/**
//	 * Get an instrumented <code>MenuItem</code> from its 
//	 * <ol>
//	 * <li><code>id</code></li>
//	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//	 * </ol>
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>MenuItem</code> must be unique and findable with param.
//	 */
//	public static MenuItem getInstrumentedMenuItem(
//			String id, String title, String text) {
//		return getInstrumentedMenuItem(id, title, text, null);
//	}
//	
//	/**
//	 * Get an instrumented <code>MenuItem</code> from its 
//	 * <ol>
//	 * <li><code>id</code></li>
//	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//	 * <li><code>shell</code> that contains it</li>
//	 * </ol>
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>MenuItem</code> must be unique and findable with param.
//	 */
//	public static MenuItem getInstrumentedMenuItem(
//		String id, String title, String text, Shell shell) {
//		MenuItem ret = null;
//		try {
//			ret = catchInstrumentedMenuItem(id, title, text, shell);
//		} catch (WidgetNotFoundException nf) {
//			Assert.fail("no instrumented MenuItem \"" + id + "\" found");
//		} catch (MultipleWidgetsFoundException mf) {
//			Assert.fail("many instrumented MenuItems \"" + id + "\" found");
//		}
//		Assert.assertNotNull("ERROR: null instrumented MenuItem", ret);
//		return ret;
//	}
//	
//	/**
//	 * Get an instrumented <code>MenuItem</code>.
//	 * 
//	 * Look in its 
//	 * <ol>
//	 * <li><code>id</code></li>
//	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//	 * <li><code>shell</code> that contains it</li>
//	 * </ol>
//	 * but don't assume it can only be found!
//	 */
//	public static MenuItem catchInstrumentedMenuItem(
//		String id, String title, String text, Shell shell)
//		throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		MenuItem ret = null;
//		WidgetFinder finder = BasicFinder.getDefault();
//		if (shell==null) {
//			try {
//				/* try to find the shell */
//				shell = (Shell)finder.find(new TextMatcher(title));
//			} catch (WidgetNotFoundException e) {
//				shell = null;
//			} catch (MultipleWidgetsFoundException e) {
//				try {
//					shell = (Shell) finder.find(new ClassMatcher(Shell.class));
//				} catch (WidgetNotFoundException e1) {
//					shell = null;
//				} catch (MultipleWidgetsFoundException e1) {
//					shell = null;
//				}
//			}			
//		}
//		/* Decide what to search on: first id, then text if id not available */
//		Matcher miMatcher;
//		if (id!=null) {
//			miMatcher = new NameMatcher(id);
//		} else {
//			miMatcher = new TextMatcher(text);
//		}		
//		try {
//			if (shell == null) {
//				ret = (MenuItem)finder.find(miMatcher);
//			} else {
//				ret = (MenuItem)finder.find(shell, miMatcher);
//			}
//		} catch (WidgetNotFoundException nf) {
//			Assert.fail("no instrumented MenuItem \"" + id + "\" found");
//		} catch (MultipleWidgetsFoundException mf) {
//			Assert.fail("many instrumented MenuItems \"" + id + "\" found");
//		}
//		
//		return ret;
//	}
	
}
