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

package org.opencds.common.utilities;

/*
 * StreamUtility does utility functions related to stream conversions.
 *
 * @author Kensaku Kawamoto
 * @version 1.0
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class StreamUtility {
    
    public InputStream getInputStreamFromString(String str) {
        try {
            byte[] bytes = str.getBytes("UTF8");
            return new ByteArrayInputStream(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
            throw new RuntimeException("getInputStreamFromString had errors: " + e.getMessage());
        }
    }

}