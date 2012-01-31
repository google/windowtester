package abbot.tester;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.KeyEvent;

import abbot.Log;
import abbot.Platform;

/**
 * Tester for the java.awt.FileDialog.
 *
 * @author Vrata Venet, European Space Agency, Madrid-Spain (av@iso.vilspa.esa.es) 
 * @author Tim Wall (twall:users.sf.net)
 * NOTE: different platforms do different things when the dialog is hidden.
 * w32 returns null for the file if FileDialog.hide is invoked; other
 * platforms may leave the file as is.  OSX (as of 1.4.2) won't let you hide
 * the dialog.
 */
public class FileDialogTester extends DialogTester {

    /**
     * This sets the file path for the fd
     */
    public void actionSetFile(Component comp, final String file) {
        if (null == file || "".equals(file))
            throw new IllegalArgumentException("File name in FileDialog should be non-null and non-empty");
        final FileDialog dialog = (FileDialog) comp;
        invokeAndWait(new Runnable() {
            public void run() {
                dialog.setFile(file);
            }
        });
    }
  
    public void actionSetDirectory(Component comp, final String dir) {
        if (null == dir || "".equals(dir))
            throw new IllegalArgumentException("File name in FileDialog should be non-null and non-empty");
        final FileDialog dialog = (FileDialog) comp;
        invokeAndWait(new Runnable() {
            public void run() {
                dialog.setDirectory(dir);
            }
        });
    }

    /** Accept the currently selected file. */
    public void actionAccept(Component comp){
        Log.debug("accept");
        final FileDialog fd = (FileDialog)comp;
        final String file = fd.getFile();
        if (file == null)
            throw new ActionFailedException("No file selected");

        // HACK:
        // sun.awt.windows.WFileDialogPeer posts an InvocationEvent which sets
        // the FileDialog file to null when Dialog.hide is called.
        // Install an event queue which can catch the posted event and ignore
        // it.  Only fully tested against sun.awt.windows.WFileDialogPeer.
        FileDialogQueue queue = new FileDialogQueue();
        try {
            fd.getToolkit().getSystemEventQueue().push(queue);
            
            // 1.4.2 bug workaround: FileDialog.hide doesn't work
            if (Platform.isOSX())
                actionKeyStroke(KeyEvent.VK_ESCAPE);
            
            invokeAndWait(new Runnable() {
                public void run() {
                    fd.hide();
                }
            });
            waitForIdle();
            fd.setFile(file);
        }
        finally {
            queue.dispose();
        }
    }
  
    private class FileDialogQueue extends EventQueue {
        private boolean disposed = false;
        private boolean installed = false;
        public void dispose() {
            boolean remove = false;
            synchronized(this) {
                if (!disposed) {
                    disposed = true;
                    remove = true;
                }
            }
            if (remove) {
                try { pop(); }
                catch(java.util.EmptyStackException es) { }
            }
        }
        protected void dispatchEvent(AWTEvent e) {
            synchronized(this) {
                if (!installed) {
                    installed = true;
                    String name = Thread.currentThread().getName();
                    Thread.currentThread().setName(name + " (abbot FileDialogTester)");
                }
            }

            // Ignore FileDialogPeer events while disposing the dialog
            if (e.paramString().indexOf("FileDialogPeer") != -1) {
                Log.debug("ignoring peer event: " + e);
                // Nothing else to handle, restore the original queue
                dispose();
            }
            else {
                super.dispatchEvent(e);
            }
        }
    }

    /** Close the file dialog without selecting a file. */
    public void actionCancel(Component comp) {
        final FileDialog fd = (FileDialog)comp;
        if (Platform.isOSX()) {
            // bug in OSX 1.4.2: activate causes another dialog to appear!
            //activate(fd); 
            // Assume dialog has focus
            actionKeyStroke(KeyEvent.VK_ESCAPE);
        }
        else {
            // the w32 native peer sets the file to null on dialog hide, but
            // other platforms might not, so do it explicitly.
            fd.setFile(null);
            invokeAndWait(new Runnable() {
                public void run() {
                    fd.hide();
                }
            });
        }
    }
}
