package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;
/*
 * I give inheritance from ClassMatcher a try to open discussion about matcher
 * API at all. Tests on a first glance show (on my machine) that combining
 * Class type check and name check is considerably faster if combined in the 
 * way shown below. For performance it is necessary to have control
 * that the least cost check is done first, the class check seems to be extremely
 * cheap compared to the name check. This is not necessarily obvious
 * with CompositeMatcher.
 */

/** 
 * Provides matching of Widgets by widget name and widget class (optional).
 * <p/>
 * The name of the widget has to be stored in the widget's data section
 * by using {@link Widget#setData(java.lang.String, java.lang.Object) 
 * Widget.setData(String key, Object value)}. 
 * The value must be of type String. The key used is the String which can be
 * obtained by {@link #getNameTag()} and defaults to key="name".
 * @author Richard Birenheide   
 */
//TODO Offer support for mustbeShowing
public class NameMatcher extends ClassMatcher {
	private static String abbotNameTag = "name";

	/**
	 * Provides the possibility to set the name tag globally.
	 * <p/>
	 * The default for the name tag is "name". If this is not allowable for 
	 * ones application under test one may change this key generally.
	 * @param nameTag the key under which the names are stored with widgets.
	 * @see Widget#setData(java.lang.String, java.lang.Object)
	 */
	public static synchronized void setNameTag(String nameTag) {
		abbotNameTag = nameTag;
	}
	/**
	 * Retrieves the name tag.
	 * <p/>
	 * @return the current name tag used as name key in {@link Widget#getData(java.lang.String)}.
	 * 
	 */
	public static synchronized String getNameTag() {
		return abbotNameTag;
	}
	
    private final String name;
  	private final Class clazz;
  	private final String nameKey;
    /**
     * Constructs a name matcher with the name given.
     * <p/>
     * The name key will be the one valid at the time the constructor is called.
     * @param name the name to match.
     */
    public NameMatcher(String name) {
   		this(name, null);
    }
    /**
     * Constructs a name matcher with the name and the class given.
     * <p/>
     * The name key will be the one valid at the time the constructor is called. 
     * @param name the name to match.
     * @param clazz the class to match.
     */
    public NameMatcher(String name, Class clazz) {
    	this(name, Widget.class, NameMatcher.getNameTag());
    }
    /**
     * Constructs a name matcher with the name, class, and name key given.
     * <p/>
     * @param name the name to match.
     * @param clazz the class to match.
     * @param nameKey the key under which the name of the widget is stored using
     * {@link Widget#setData(java.lang.String, java.lang.Object)}.
     */
    public NameMatcher(String name, Class clazz, String nameKey) {
    	super(clazz);
    	this.name = name;
    	this.clazz = clazz;
    	this.nameKey = nameKey;
    }
    /**
     * {@inheritDoc}
     * @param w {@inheritDoc}
     * @return {@inheritDoc}
     * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
     */
    public boolean matches(final Widget w) {
    	if (this.clazz != null) {
    		boolean superResult = super.matches(w);
    		if (!superResult) {
    			return false;
    		}
    	}
    	String name = (String) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
    		public Object runWithResult() {
    			return w.getData(NameMatcher.this.nameKey);
    		}
    	});
    	if (name == null) {
    		return false;
    	}
    	return stringsMatch(this.name, name);
    	
//    	foundName = null;
//    	w.getDisplay().syncExec( new Runnable() { public void run() {
//    		foundName = (String)(w.getData("name"));
//    	}});
//        if (name == null)
//            return foundName == null; 
//        return stringsMatch(name, foundName);
    }
    public String toString() {
        return "Name matcher name =" + name + " : tag = " + this.nameKey;
    }
}
