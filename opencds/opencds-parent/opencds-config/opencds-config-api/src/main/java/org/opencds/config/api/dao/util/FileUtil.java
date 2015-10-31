package org.opencds.config.api.dao.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil implements ResourceUtil {
    @Override
    public List<String> findFiles(String path, boolean traverse) {
        return findFiles(Paths.get(path).toFile(), traverse);
    }
    
    /**
     * TODO FIXME: Does not handle symlinks...
     * 
     * @param path
     * @return
     */
    private List<String> findFiles(File path, boolean traverse) {
        if (!path.isDirectory()) {
            throw new IllegalArgumentException("File is not a directory: " + path.getAbsolutePath());
        }
        List<String> files = new ArrayList<>();
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                if (traverse) {
                    files.addAll(findFiles(file, traverse)); // recursive--does not handle symlinks
                }
            } else {
                files.add(file.getAbsolutePath());
            }
        }
        return files;
    }
    
    @Override
    public InputStream getResourceAsStream(String resource) {
        try {
            return new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an ArrayList containing fileName String objects in the specified
     * file path, with the file name starting with and ending with the strings
     * specified. Sample filePath: "C:\Temp\Folder1" or "C:\Temp\Folder1\".
     * (both types ok --> tested).
     * 
     * @param path
     * @param startsWith
     * @param endsWith
     * @return
     */
    @Override
    public List<String> listMatchingResources(final String path, final String startsWith, final String endsWith) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return ((startsWith == null || startsWith.isEmpty() || pathname.getName().startsWith(startsWith)) && (endsWith == null
                        || endsWith.isEmpty() || pathname.getName().endsWith(endsWith)));
            }
        };

        File parentDirectory = new File(path);
        File[] files = parentDirectory.listFiles(filter);

        List<String> fileNameList = new ArrayList<String>();
        if (files != null) {
            for (File file : files) {
                fileNameList.add(file.getName());
            }
        }
        return fileNameList;
    }

}
