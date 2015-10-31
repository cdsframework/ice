/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.config.migrate.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;

/**
 * FileUtility is a utility for File operations.
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class FileUtility implements ResourceUtility {
    private static final Log log = LogFactory.getLog(FileUtility.class);
    private static final Pattern URI_PATH_SEPARATOR = Pattern.compile("/");

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

    /**
     * Returns contents of file as a single String.
     * 
     * @param source
     * @return
     * @throws OpenCDSRuntimeException
     */
    @Override
    public String getContents(String source) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(source));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new OpenCDSRuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new OpenCDSRuntimeException(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return builder.toString();
    }

    /**
     * @param source
     * @return
     */
    @Override
    public InputStream getResourceAsInputStream(String source) {
        InputStream is = null;
        try {
            is = new FileInputStream(source);
        } catch (FileNotFoundException e) {
            log.debug("Cannot create InputStream; file resource does not exist: " + source);
        }
        return is;
    }

    @Override
    public String renormalizeSeparator(String location) {
        log.debug("Replacing path separator '/' with File.separator: location= " + location + "; File.separator= "
                + File.separator);
        String newLocation = URI_PATH_SEPARATOR.matcher(location).replaceAll(Matcher.quoteReplacement(File.separator));
        log.debug("Replacement result: " + newLocation);
        return newLocation;
    }

    public static void main(String args[]) {
        FileUtility fu = new FileUtility();
        // AddressElement myFU = new AddressElement();
        // String[] fileNameList = myFU.getResourceListing(myFU.getClass(),
        // "..\\");//org\\opencds\\vmr\\v1_0\\internal\\"); /*"opencds-vmr-1_0-internal.jar".getClass(),*/
        // for (String eachResource : fileNameList) {
        // System.out.println(eachResource);
        // }

        // // String resourceString = getResourceAsString("knowledgeModules",
        // "org.opencds^NQF_0031_v1^1.0.0.drl");
        // File resource = new
        // File("C:\\OpenCDS\\opencds-parent\\opencds-knowledge-repository\\src\\main\\resources\\knowledgeModules"
        // + "\\" + "org.opencds^NQF_0031_v1^1.0.0.drl");
        // // File resource = new
        // File("C\\\\OpenCDS\\opencds-parent\\opencds-knowledge-repository\\src\\main\\resources\\"
        // + "knowledgeModules" + "\\" + "org.opencds^NQF_0031_v1^1.0.0.drl");
        // // File resource = new File(".\\src\\main\\resources\\" +
        // "knowledgeModules" + "\\" + "org.opencds^NQF_0031_v1^1.0.0.drl");
        // String resourceString =
        // FileUtility.getInstance().getFileContentsAsString(resource);
        // if (resourceString == null) {
        // System.out.println("resourceString is null.");
        // } else {
        // System.out.println(resourceString);
        // }

        List<String> fileList = new ArrayList<String>();
        // String filePath = ".\\src\\main\\resources\\" + "schema" + "\\";
        // String filePath = ".\\";
        // String filePath =
        // "c:\\OpenCDS\\opencds-parent\\opencds-vmr-1_0\\opencds-vmr-1_0-internal\\target\\org\\opencds\\vmr\\v1_0\\internal\\";//getResourceListing("opencds-vmr-1_0-internal.jar".getClass(),
        // ".\\org\\opencds\\vmr\\v1_0\\internal\\");
        // String filePath =
        // "c:\\OpenCDS\\opencds-parent\\opencds-vmr-1_0\\opencds-vmr-1_0-internal\\target\\classes\\org\\opencds\\vmr\\v1_0\\internal\\";//getResourceListing("opencds-vmr-1_0-internal.jar".getClass(),
        // ".\\org\\opencds\\vmr\\v1_0\\internal\\");
        String filePath = "..\\opencds-vmr-1_0\\opencds-vmr-1_0-internal\\target\\classes\\org\\opencds\\vmr\\v1_0\\internal\\";// getResourceListing("opencds-vmr-1_0-internal.jar".getClass(),
                                                                                                                                // ".\\org\\opencds\\vmr\\v1_0\\internal\\");
        fileList = fu.listMatchingResources(filePath, "", "");
        // ArrayList<String> resourceList = listResourcesByType("schema");
        for (String eachResource : fileList) {
            System.out.println(eachResource);
        }

    }

}
