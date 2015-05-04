## Official WindowTester FAQ ##

  * https://developers.google.com/java-dev-tools/wintester/html/faq/faq

## Unofficial WindowTester FAQ / Workarounds ##

### Errors during closing the application. ###

**Example:**
```
ui.click(new MenuItemLocator("File/Exit"));
```

**Workaround:**

Do not record closing the application, closing is done automatically at the end of a test. Just make sure that there are no unsaved changes (eg. in an Eclipse RCP application). Otherwise a message dialog pops-up and prevents a graceful shutdown.


---


### Entering text (eg. in a text field) does not work correctly during test playback, what should I do? ###

**Example:**

Test code:
```
ui.enterText("path/to/something");
```

Text entered during playback: `path7to7something`

This happens especially with non-US languages and special characters (eg. umlauts).

**Workaround:**

In the setup() method of your test add the following lines (eg. for German locale):
```
Locale.setDefault(new Locale("de", "DE"));
WT.setLocaleToCurrent();
```


---


### Clicking on a hyperlink on a scrollable SWT forms page that is not visible does not work, what can I do? ###

Automatic scrolling to hyperlinks in SWT form widgets is not yet supported.

**Workaround:**

  * Use `ui.keyClick(WT.PAGE_DOWN);` or `ui.keyClick(WT.ARROW_DOWN);`
  * You might need to call the methods multiple times to scroll to the right area.


---


### Clicking/asserting tree items that contain forward or backslashes does not work. ###

**Example:**

Tree structure:
  * a
    * b//c <-- click
    * d
  * e

Actual tree item path (without escaping): a/b//c

Recorded click event:
```
ui.click(new TreeItemLocator("a/b\\\\/\\\\/c"));
```
**=> Error during playback.**

Recorded assert event:
```
ui.assertThat(new TreeItemLocator("a/b\\\\/\\\\/c").isVisible());
```
**=> Works during playback.**


**Workaround:**
  * look at the error carefully
  * add/delete slashes to match the actual tree item name
    * in this case the following would work:
```
ui.click(new TreeItemLocator("a/b\\/\\/c"));
```


---


### Error: Unable to find widget ("Unsupported widget exception") ###

**Workaround:**
  * add the following line to your test code, right before(!) the line with the unsupported widget:
```
new DebugHelper().printWidgets();
```
**=> This prints the widget hierarchy to the console. With the hierarchy information you can try to manually construct a unique path to the widget.**

**Workaround 2:**

Use named widgets:
  * In the source code of your application add:
```
MyCustomWidget customWidget = new MyCustomWidget();
customWidget.setData("name", "specialWidget");
```
  * In your test case add:
```
ui.click(new NamedWidgetLocator("specialWidget"));
```


---
