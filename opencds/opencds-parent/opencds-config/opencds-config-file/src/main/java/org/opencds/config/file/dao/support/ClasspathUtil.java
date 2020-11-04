package org.opencds.config.file.dao.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.dao.util.FileUtil;
import org.opencds.config.api.dao.util.ResourceUtil;

public class ClasspathUtil implements ResourceUtil {
	private static final Logger log = LogManager.getLogger();
    private static final ResourceUtil fileUtil = new FileUtil();

    private String jarPrefix;
    private List<String> entries;

    public ClasspathUtil(String jarPrefix) {
        this.jarPrefix = jarPrefix;
        entries = listEntriesInJarFromClasspath(jarPrefix);
    }

    @Override
    public List<String> findFiles(final String path, boolean traverse) {
        List<String> files = new ArrayList<>();
        for (String entry : entries) {
            if (entry.startsWith(path) && !entry.endsWith("/")) {
                files.add(entry);
            }
        }
        return files;
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        return this.getClass().getClassLoader().getResourceAsStream(resource);
    }

    private List<String> listEntriesInJarFromClasspath(String name) {
        List<String> entries = new ArrayList<String>();
        JarFile jarFile = null;
        try {
            log.debug("looking for jars with name matching: " + name);
            // first, find a matching jar on the classpath
            List<String> jars = findOnClasspath(name);
            for (String jar : jars) {
                log.debug("Jar: " + jar);
            }
            // if no matching jar is found on the classpath, find them
            // on the current thread
            if (jars == null || jars.isEmpty()) {
                jars = findInContextClassLoader(name);
            }
            // we want one and only one matching jar
            if (jars.size() > 1) {
                throw new OpenCDSRuntimeException(
                        "Found too many configuration jar matches for name: "
                                + name);
            } else if (jars.size() == 0) {
                throw new OpenCDSRuntimeException(
                        "Found zero configuration jar matches for name: "
                                + name);
            }
            String location = jars.get(0);
            if (new File(location).isDirectory()) {
                entries = listMatchingResources(location, null, null);
            } else {
                /*
                 * Now, let's get the content listing to index for future
                 * requests. Once we have the listing, we can get the contents
                 * of each file contained within the jar, directly from the
                 * classpath.
                 */
                jarFile = new JarFile(jars.get(0));
                log.debug("JarFile: " + jarFile.getName());
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry entry = jarEntries.nextElement();
                    log.debug("adding entry: " + entry.getName());
                    entries.add(entry.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException(e.getMessage());
        } finally {
            try {
                if (jarFile != null) {
                    jarFile.close();
                }
            } catch (IOException ee) {
                // ignore...
            }
        }
        return entries;
    }

    private List<String> findOnClasspath(String name) {
        String classpath = System.getProperty("java.class.path");
        List<String> jars = new ArrayList<String>();
        for (String jar : classpath.split(File.pathSeparator)) {
            if (jar.contains(jarPrefix)) {
                jars.add(jar);
            }
        }
        return jars;
    }

    private List<String> findInContextClassLoader(String name) {
        List<String> jars = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL[] urls = new URL[0];
        if (classLoader != null) {
            urls = ((URLClassLoader) classLoader).getURLs();
            log.debug("URLs from classpath: " + urls);
            for (URL url : urls) {
                log.debug("CLASSPATH URL: " + url);
            }
            jars = filterByName(urls, name);
        }
        return jars;
    }

    private List<String> filterByName(URL[] list, String name) {
        List<String> jars = new ArrayList<String>();
        for (URL jar : list) {
            log.debug("  Found jar: " + jar);
            if (jar.getPath().contains(name)) {
                jars.add(jar.getPath());
            }
        }
        return jars;
    }

    @Override
    public List<String> listMatchingResources(String path, String startsWith, String endsWith) {
        return ClasspathUtil.fileUtil.listMatchingResources(path, startsWith, endsWith);
    }

}
