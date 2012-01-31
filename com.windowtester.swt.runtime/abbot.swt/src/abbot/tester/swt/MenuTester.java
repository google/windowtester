
package abbot.tester.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuTester extends WidgetTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	public static final String POPUP_ROOT_FLAG = "POPUP_ROOT";
	public static final String ROOT_FLAG ="MENU_ROOT";
	
	// TODO Put methods from WidgetTester into MenuTester and add getters
	
	/* Use the Menu's parentItem to getText */
	public String getText(final Menu menu){
        String result = (String) Robot.syncExec(menu.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        String s = "";
                        MenuItem parentItem = menu.getParentItem();
                        if(parentItem != null){
                            s = parentItem.getText();
                        }
                        if (s == null || s.equals("")) {
                           if ((menu.getStyle() & SWT.POP_UP) > 0) {
                               s = POPUP_ROOT_FLAG;
                           } else {
                               s = ROOT_FLAG;
                           }
                        }
                        return s;
                    }
                });
        return result;
	}
    
    /**
     * Proxy for {@link Menu#getParentItem().toString()}. <p/>
     */
    public String toString(final Menu m) {
        String result = (String) Robot.syncExec(m.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        String s = "";
                        MenuItem parentItem = m.getParentItem();
                        if(parentItem != null){
                            s = parentItem.toString();
                        }
                        return s;
                    }
                });
        return result;
    }

	/**
	 * Proxy for {@link Menu#getDefaultItem()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the default item.
	 */
	public MenuItem getDefaultItem(final Menu menu){
		MenuItem result = (MenuItem) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getDefaultItem();				
			}
		});
		return result;		
	}
	/**
	 * Proxy for {@link Menu#getEnabled()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return true if the menu is enabled.
	 */
	public boolean getEnabled(final Menu menu){
		Boolean result = (Boolean) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(menu.getEnabled());				
			}
		});
		return result.booleanValue();		
	}

	/**
	 * Proxy for {@link Menu#getItem(int)}.
	 * <p/>
	 * @param menu the menu under test.
	 * @param index the index of the item to get.
	 * @return the item at the index given.
	 */
	public MenuItem getItem(final Menu menu, final int index){
		MenuItem result = (MenuItem) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getItem(index);				
			}
		});
		return result;		
	}
    
	/**
	 * Proxy for {@link Menu#getParentItem()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the parent item.
	 */
	public MenuItem getParentItem(final Menu menu){
		MenuItem result = (MenuItem) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getParentItem();				
			}
		});
		return result;		
	}
    
	/**
	 * Proxy for {@link Menu#getItemCount()()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the number of items under this menu.
	 */
	public int getItemCount(final Menu menu){
		Integer result = (Integer) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(menu.getItemCount());				
			}
		});
		return result.intValue();	
	}
    
	/**
	 * Proxy for {@link Menu#getItems()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the children.
	 */
	public MenuItem[] getItems(final Menu menu){
		MenuItem[] result = (MenuItem[]) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getItems();				
			}
		});
		return result;		
	}

	/**
	 * Proxy for {@link Menu#getParent()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the parent.
	 */
	public Decorations getParent(final Menu menu){
		Decorations result = (Decorations) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getParent();				
			}
		});
		return result;	
	}
    
	/**
	 * Proxy for {@link Menu#getParentMenu()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the parent menu.
	 */
	public Menu getParentMenu(final Menu menu){
		Menu result = (Menu) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getParentMenu();				
			}
		});
		return result;	
	}
    
	/**
	 * Proxy for {@link Menu#getShell()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the shell of the menu.
	 */
	public Shell getShell(final Menu menu){
		Shell result = (Shell) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getShell();				
			}
		});
		return result;	
	}
    
	/**
	 * Proxy for {@link Menu#getVisible()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the menu's visibility state.
	 */
	public boolean getVisible(final Menu menu){
		Boolean result = (Boolean) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(menu.getVisible());				
			}
		});
		return result.booleanValue();		
	}
    
	/**
	 * Proxy for {@link Menu#indexOf(org.eclipse.swt.widgets.MenuItem)}.
	 * <p/>
	 * @param menu the menu under test.
	 * @param menuItem the item to check.
	 * @return the index of the item given.
	 */
	public int indexOf(final Menu menu, final MenuItem menuItem) {
		Integer result = (Integer) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(menu.indexOf(menuItem));				
			}
		});
		return result.intValue();	
	}
    
	/**
	 * Proxy for {@link Menu#isEnabled()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return true if the menu is enabled.
	 */
	public boolean isEnabled(final Menu menu){
		Boolean result = (Boolean) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(menu.isEnabled());				
			}
		});
		return result.booleanValue();		
	}
    
	/**
	 * Proxy for {@link Menu#isVisible()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the menu's visibility state.
	 */
	public boolean isVisible(final Menu menu){
		Boolean result = (Boolean) Robot.syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(menu.isVisible());				
			}
		});
		return result.booleanValue();		
	}
    
	/**
	 * Factory method.
	 */
	public static MenuTester getMenuTester() {
		return (MenuTester)(getTester(Menu.class));
	}

}
