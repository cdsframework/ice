/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class XMLSupportingDataFilenameFilterImpl implements FilenameFilter {
	
	private static final Logger logger = LogManager.getLogger();
	
	
	public XMLSupportingDataFilenameFilterImpl() {
		if (logger.isDebugEnabled()) {
			logger.info("XMLFilenameFilterImpl instantiated");
		}
	}
	
	/**
	 * Given a directory and a filter, return true if the filename is an XML file.
	 */
	@Override
	public boolean accept(File pDir, String pFilename) {
		
		String _METHODNAME = "accept(): ";
		
		if (pDir == null || pDir.isDirectory() == false) {
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "Specified directory is either null or not a directory: " + (pDir == null ? "null" : pDir.getAbsolutePath()));
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Dir: " + pDir.getAbsolutePath() + "; " + pFilename);
		}
		if (pFilename == null || pFilename.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "Filename not specified");
			}			
			return false;
		}
		Pattern p = Pattern.compile("[a-zA-Z0-9_\\- ]+\\.xml");
		Matcher m = p.matcher(pFilename);
		boolean b = m.matches();
		if (logger.isDebugEnabled()) {
			if (b == true) {
				logger.debug(_METHODNAME + "Filename matches pattern; filename is: " + pFilename);
			}
			else {
				logger.debug(_METHODNAME + "Filename does not match pattern; filename is: " + pFilename);
			}
		}
		
		return b;
	}
	
}
