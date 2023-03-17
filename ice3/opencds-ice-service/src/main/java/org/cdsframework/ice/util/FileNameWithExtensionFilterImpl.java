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
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileNameWithExtensionFilterImpl implements FilenameFilter {
	
	private String baseFilename = null;
	private Set<String> validFileExtensions = null;
	
	private static final Logger logger = LogManager.getLogger();


	/**
	 * Set up a filename filter that requires an extension. If an invalid argument is provided, IllegalArgumentException is thrown.
	 * @param pBasename Filename filter checks if supplied argument to accept() starts with whatever is supplied for this argument
	 * @param pValidFileExtensions filter checks if filename ends with one of the extensions provided in this collection (always prefixed by a ".")
	 * (e.g. - ".txt" or "txt" both check that the filename ends with ".txt"). If no extensions are supplied, any filename checks will not match.
	 */
	public FileNameWithExtensionFilterImpl(String pStartsWith, String[] pValidFileExtensions) {

		String _METHODNAME = "FileNameWithExtensionFilterImpl(): ";
		if (pStartsWith == null) {
			baseFilename = "";
		}
		else {
			baseFilename = pStartsWith;
		}
		
		validFileExtensions = new HashSet<String>();
		if (pValidFileExtensions != null) {
			for (int x=0; x < pValidFileExtensions.length; x++) {
				String lValidFileExtension = pValidFileExtensions[x];
				if (lValidFileExtension.startsWith(".")) {
					lValidFileExtension = lValidFileExtension.substring(1);
				}
				if (lValidFileExtension.length() > 0) {
					validFileExtensions.add(lValidFileExtension);
				}
			}
		}
		
		if (logger.isDebugEnabled()) {
			String lDebugStr = "Base filename pattern: " + baseFilename + "; Extensions: ";
			for (String lExtension : validFileExtensions) {
				lDebugStr += lExtension + ", ";
			}
			logger.debug(_METHODNAME + lDebugStr);
		}
	}
	
	@Override
	public boolean accept(File pDir, String pFilename) {

		String _METHODNAME = "accept(): ";
		
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Dir: " + (pDir == null ? "" : pDir.getAbsolutePath()) + "; " + pFilename);
		}
		if (pFilename == null || pFilename.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "Filename not specified");
			}			
			return false;
		}
		if (logger.isDebugEnabled())
			logger.debug(_METHODNAME + "Filename check for '" + pFilename + "'; filename starting pattern: " + baseFilename);

		if (pFilename == null || baseFilename == null) {
			return false;
		}
		
		int lInd = pFilename.lastIndexOf('.');
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Indice of filename extension is " + lInd);
		}
		if (lInd < 0) {
			return false;
		}
		String lExtension = pFilename.substring(lInd+1);
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Filename extension is " + lExtension);
		}
		if (pFilename.startsWith(baseFilename) && validFileExtensions.contains(lExtension)) {
			return true;
		}
		else {
			return false;
		}
	}

}
