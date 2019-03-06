package org.cdsframework.ice.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.omg.dss.DSSRuntimeExceptionFault;

public class TestEvaluationAtSpecifiedTimeImpl {


	/**
	 * Returns a List containing Strings that represent individual lines in sourceFile.
	 *
	 * @param sourceFile
	 * @return
	 * @throws DSSRuntimeExceptionFault 
	 */
	public static ArrayList<String> getLinesFromFile(File sourceFile) {
		ArrayList<String> arrayToReturn = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			String line = null;
			reader = new BufferedReader(new FileReader(sourceFile));

			while ((line = reader.readLine()) != null)
			{
				arrayToReturn.add(line);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("unable to getLinesFromFile '" + sourceFile.getPath() + "' because: " + e.getMessage());
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to close Reader: " + e.getMessage());
			}
		}

		return arrayToReturn;
	}

	public static String getFileContentsAsString(File sourceFile) {
    	List<String> fileLines = getLinesFromFile(sourceFile);
        StringBuffer buffer = new StringBuffer(5000);

        for (int k = 0; k < fileLines.size(); k++)
        {
            buffer.append(fileLines.get(k));
        }
        return buffer.toString();

    }
	
	public static void main(String[] args) {

		/*
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'CDS_CHL_Test_Female_16_24_PastMonth', version: '1.0.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		 */

		String filePath = "/usr/local/projects/ice3dev-resources/testing/ongoing-testin-2.xml";
		File lF = new File(filePath);
		String lRequestPayload = getFileContentsAsString(lF);
		
		String responsePayload="";
		responsePayload = IceClient.sendEvaluateAtSpecifiedTimeMessage(lRequestPayload);
		System.out.print(responsePayload);
	}

}
