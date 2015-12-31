package org.cdsframework.ice.util;


public class StringUtils {
	
	public static boolean isNullOrEmpty(String pS) {
		
		if (pS == null || pS.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
