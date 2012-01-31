package abbot.util;

import java.io.File;
import java.net.*;
import java.util.StringTokenizer;
import java.util.ArrayList;

import abbot.Platform;

/** Provide a class loader that loads from a custom path.   Similar to
 * sun.misc.Launcher$AppClassLoader (the usual application class loader),
 * except that it doesn't do the security checks that AppClassLoader does.
 * If path given is null, uses java.class.path.
 */
public class PathClassLoader extends java.net.URLClassLoader {

    private String classPath;
    private static final Factory factory = new Factory();

    /** Create a class loader that loads classes from the given path. */
    public PathClassLoader(String path) {
        this(path, null);
    }

    /** Create a class loader that loads classes from the given path. */
    public PathClassLoader(String path, ClassLoader parent){
        super(getURLs(path != null ? path 
                      : System.getProperty("java.class.path"), ":;"),
              parent, factory);
        this.classPath = path != null
            ? path : System.getProperty("java.class.path");
    }

    public String getClassPath() { return classPath; }

    protected synchronized Class loadClass(String name, boolean resolve) 
        throws ClassNotFoundException {
        int i = name.lastIndexOf('.');
        if(i != -1) {
            SecurityManager sm = System.getSecurityManager();
            if(sm != null)
                sm.checkPackageAccess(name.substring(0, i));
        }
        return super.loadClass(name, resolve);
    }

    /** Returns an array of URLs based on the given classpath string. */
    static URL[] getURLs(String p, String separators) {
        String s = p != null
            ? p : System.getProperty("java.class.path");
        File files[] = s != null ? convertPathToFiles(s, separators) : new File[0];
        URL[] urls = new URL[files.length];
        for (int i=0;i < urls.length;i++) {
            try {
                urls[i] = files[i].toURL();
            }
            catch(MalformedURLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return urls;
    }

    /** Returns an array of filenames (including path). */
    public static String[] convertPathToFilenames(String path) {
        return convertPathToFilenames(path, ":;");
    }

    /** Convert the given path string into an array of File. */
    public static File[] convertPathToFiles(String path, String seps) {
        String[] names = convertPathToFilenames(path, ":;");
        ArrayList files = new ArrayList();
        for (int i=0;i < names.length;i++) {
            files.add(new File(names[i]));
        }
        return (File[])files.toArray(new File[files.size()]);
    }

    static String[] convertPathToFilenames(String path, String seps) {
        if (path == null)
            path = "";
        boolean fixDrives = Platform.isWindows() && seps.indexOf(":") != -1;
        StringTokenizer st = new StringTokenizer(path, seps);
        ArrayList names = new ArrayList();
        while (st.hasMoreTokens()) {
            String fp = st.nextToken();
            // Fix up w32 absolute pathnames
            if (fixDrives && fp.length() == 1 && st.hasMoreTokens()) {
                char ch = fp.charAt(0);
                if ((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')) {
                    fp += ":" + st.nextToken();
                }
            }
            names.add(fp);
        }
        return (String[])names.toArray(new String[names.size()]);
    }

    /** Taken from sun.misc.Launcher. */
    private static class Factory implements URLStreamHandlerFactory {
        private static final String PREFIX = "sun.net.www.protocol";
        private Factory() { }
        public URLStreamHandler createURLStreamHandler(String protocol) {
            String name = PREFIX + "." + protocol + ".Handler";
            try {
                Class c = Class.forName(name);
                return (URLStreamHandler)c.newInstance();
            }
            catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch(InstantiationException e) {
                e.printStackTrace();
            }
            catch(IllegalAccessException e) {
                e.printStackTrace();
            }
            throw new Error("could not load " 
                            + protocol + "system protocol handler");
        }
    }

    public String toString() {
        return super.toString() + " (classpath=" + classPath + ")";
    }
}
