package org.cdsframework.ice.util;

import java.io.File;

public final class FileUtils {

	
	/**
	 * Given a parent directory in File and sub-directory represented as a string, returns true, if the directory is present (and accessible), and false is not.
	 * @param pParentDirectory
	 * @param pChildDirectory
	 * @return
	 */
	public static boolean isSupportingDirectoryAndSubDirectoryPresent(File pParentDirectory, String pChildDirectory) {
		
		if (pParentDirectory == null) {
			return false;
		}
		File lSD = null;
		if (pChildDirectory == null || pChildDirectory.length() == 0) {
			lSD = pParentDirectory;
		}
		else {
			lSD = new File(pParentDirectory, pChildDirectory);
		}
		if (lSD.isDirectory() == false) {
			return false;
		}
		else {
			return true;
		}
	}
	

	/**
	 * Given a directory represented as a File, returns true if the directory is present (and accessible), false if not
	 * @param pDirectory
	 * @return
	 */
	public static boolean isSupportingDirectoryPresent(File pDirectory) {
		
		return isSupportingDirectoryAndSubDirectoryPresent(pDirectory, null);
	}
	
}
