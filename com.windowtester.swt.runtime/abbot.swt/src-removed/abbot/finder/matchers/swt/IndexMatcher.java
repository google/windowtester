package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Widget;

import abbot.Log;
import abbot.finder.swt.Matcher;

public class IndexMatcher implements Matcher {

	private Matcher _matcher;
	private int _index;
	private int _current = -1;
	
	public IndexMatcher(Matcher matcher, int index) {
		_index = index;
		_matcher = matcher;
	}
	
	public boolean matches(Widget widget) {
		boolean matches = false;
		if(_matcher.matches(widget)) {
			_current++;
            Log.debug("Found match for matcher:\n"+_matcher+"\n Must check index:["+_current+"=="+_index+"]");
			if(_current == _index) {
				matches = true;
			}  
		}
		return matches;
	}
    
    public String toString() {
        //return "\nCould NOT MATCH INDEX:"+_index+"\nFor:\n"+_matcher.toString();
    	return "Index Matcher (" + _matcher + ", " + _index +")";
    }
}
