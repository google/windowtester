package abbot.tester.swt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

/**
 * @version $Id: ItemTester.java,v 1.1 2005-12-19 20:28:31 pq Exp $
 */
public class ItemTester extends WidgetTester{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";

	/* Begin getters */
	/**
	 * Proxy for {@link Item#getImage()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the image placed on the item.
	 */
	public Image getImage(final Item item){
		Image result = (Image) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getImage();
			}
		});
		return result;
	}
    
	/**
	 * Proxy for {@link Item#getText()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the text of the item.
	 */
	public String getText(final Item item){
		String result = (String) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getText();
			}
		});
		return result;
	}
	/* End getters */
}
