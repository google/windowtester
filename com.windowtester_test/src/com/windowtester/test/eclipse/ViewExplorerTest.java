package com.windowtester.test.eclipse;

import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewExplorer;

public class ViewExplorerTest extends BaseTest {

    ViewExplorer viewExplorer = new ViewExplorer();

    public void testSpelunk() {
        viewExplorer.spelunk();
    }

    public void testFindCategory() {
        assertEquals("General", viewExplorer.findCategory("Welcome"));
    }

}
