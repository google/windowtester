package junit.extensions.abbot;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.*;

import junit.framework.*;
import junit.runner.*;
import abbot.Log;
import abbot.Platform;
import abbot.util.PathClassLoader;

/** Collects all available classes derived from ScriptTestCase in the current
 * classpath.
 */

public class ScriptTestCollector extends LoadingTestCollector {
    private ClassLoader loader;

    private static final String PACKAGE = "junit.extensions.abbot.";

    public ScriptTestCollector() {
        this(null);
    }

    public ScriptTestCollector(ClassLoader loader) {
        if (loader == null) {
            String path = System.getProperty("java.class.path");
            loader = new PathClassLoader(path);
        }
        this.loader = loader;
    }

    private String convertURLsToClasspath(URL[] urls) {
        String PS = System.getProperty("path.separator");
        String path = "";
        for (int i=0;i < urls.length;i++) {
            if (!"".equals(path))
                path += PS;
            URL url = urls[i];
            if (url.getProtocol().equals("file")) {
                String file = url.getFile();
                if (Platform.isWindows() && file.startsWith("/"))
                    file = file.substring(1);
                path += file;
            }
        }
        return path;
    }

    /** Override to use something other than java.class.path. */
    public Enumeration collectTests() {
        String jcp = System.getProperty("java.class.path");
        String classPath = loader instanceof URLClassLoader
            ? convertURLsToClasspath(((URLClassLoader)loader).getURLs())
            : jcp;
        Hashtable hash = collectFilesInPath(classPath);
        if (loader instanceof URLClassLoader)
            hash.putAll(collectFilesInPath(jcp));
        return hash.elements();
    }

    private ArrayList splitClassPath(String classPath) {
        ArrayList result= new ArrayList();
        String separator= System.getProperty("path.separator");
        StringTokenizer tokenizer= new StringTokenizer(classPath, separator);
        while (tokenizer.hasMoreTokens()) 
            result.add(tokenizer.nextToken());
        return result;
    }

    /** Collect files in zip archives as well as raw class files. */
    public Hashtable collectFilesInPath(String classPath) {
        Hashtable hash = super.collectFilesInPath(classPath);
        Collection paths = splitClassPath(classPath);
        Iterator iter = paths.iterator();
        while (iter.hasNext()) {
            String el = (String)iter.next();
            if (el.endsWith(".zip") || el.endsWith(".jar")) {
                hash.putAll(scanArchive(el));
            }
        }
        return hash;
    }

    protected Map scanArchive(String name) {
        Map map = new HashMap();
        try {
            ZipFile zip = new ZipFile(name);
            Enumeration en = zip.entries();
            while (en.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)en.nextElement();
                if (!entry.isDirectory()) {
                    String filename = entry.getName();
                    if (isTestClass(filename)) {
                        String cname = classNameFromFile(filename);
                        map.put(cname, cname);
                    }
                }
            }
        }
        catch(IOException e) {
        }
        return map;
    }

    protected boolean isTestClass(String classFileName) {
        boolean isTest = classFileName.endsWith(".class")
            && classFileName.indexOf("Test") > 0
            && classFileName.indexOf('$') == -1;

        if (isTest) {
            String className = classNameFromFile(classFileName);
            try {
                Class testClass = Class.forName(className, true, loader);
                Class scriptFixture =
                    Class.forName(PACKAGE + "ScriptFixture",
                                  true, loader);
                Class scriptSuite =
                    Class.forName(PACKAGE + "ScriptTestSuite",
                                  true, loader);
                return (scriptFixture.isAssignableFrom(testClass)
                        || scriptSuite.isAssignableFrom(testClass))
                    && Modifier.isPublic(testClass.getModifiers())
                    && TestSuite.getTestConstructor(testClass) != null;
            }
            catch(ClassNotFoundException e) {
            }
            catch(NoClassDefFoundError e) {
            }
            catch(NoSuchMethodException e) {
            }
        }
        return false;
    }

    protected String classNameFromFile(String classFileName) {
        String name = super.classNameFromFile(classFileName);
        return name.replace('/', '.');
    }
}
