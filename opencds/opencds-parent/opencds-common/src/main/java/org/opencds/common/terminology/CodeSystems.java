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

package org.opencds.common.terminology;

/**
 * <p>ConceptSystems provides an enumeration of code systems supported within OpenCDS.
 * 
 * Code system names are considered non-normative, and implementations should not depend on them.
 * 
 * Currently, code system names use the "symbolic name" listed in the HL7 OID registry at http://www.hl7.org/oid/index.cfm.
 * 
 * The OID listing at http://wiki.ihe.net/index.php?title=PCC_TF-2/Namespaces_and_Vocabularies is also considered as reference material.</p>
 * 
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class CodeSystems 
{
//	public final static String CODE_SYSTEM_OID_HL7_V3_CODE_SYSTEM = "2.16.840.1.113883.5"; 
	// TODO: see if this HL7 OID should be used, or OID for each value set (e.g., gender) should be separately enumerated

	//replaced by SimpleKnowledgeRepository lookup using openCDSCodeSystems.xml file
//	public final static String CODE_SYSTEM_OID_CPT4 = "2.16.840.1.113883.6.12"; 
//	public final static String CODE_SYSTEM_OID_ICD9CM_DIAGNOSES = "2.16.840.1.113883.6.103"; 
//	public final static String CODE_SYSTEM_OID_ICD9CM_PROCEDURES = "2.16.840.1.113883.6.104"; 
//	public final static String CODE_SYSTEM_OID_LOINC = "2.16.840.1.113883.6.1"; 
//	public final static String CODE_SYSTEM_OID_SNOMED_CT = "2.16.840.1.113883.6.96"; 
	public final static String CODE_SYSTEM_OID_OPENCDS_CONCEPTS = "2.16.840.1.113883.3.795.12.1.1"; 
	public final static String CODE_SYSTEM_OID_OPENCDS_TEMPLATES = "2.16.840.1.113883.3.795.11";
	
//	/**
//	 * Returns name of code system.  Returns null if match not found
//	 * @param codeSystemOID
//	 * @return
//	 */
//	//replaced by routine of same name in SimpleKnowledgeRepository to grab name from xml file opencds-knowledge-repository\src\main\resources\openCDSCodeSystems.xml	
//	public final static String getCodeSystemName(String codeSystemOID)
//	{
//		
//	/*	if (codeSystemOID.equals(CODE_SYSTEM_OID_HL7_V3_CODE_SYSTEM))
//		{
//			return CODE_SYSTEM_NAME_HL7_V3_CODE_SYSTEM;
//		}
//		else if (codeSystemOID.equals(CODE_SYSTEM_OID_CPT4))
//		{
//			return CODE_SYSTEM_NAME_CPT4;
//		}
//		else if (codeSystemOID.equals(CODE_SYSTEM_OID_ICD9CM_DIAGNOSES))
//		{
//			return CODE_SYSTEM_NAME_ICD9CM_DIAGNOSES;
//		}
//		else if (codeSystemOID.equals(CODE_SYSTEM_OID_ICD9CM_PROCEDURES))
//		{
//			return CODE_SYSTEM_NAME_ICD9CM_PROCEDURES;
//		}
//		else if (codeSystemOID.equals(CODE_SYSTEM_OID_LOINC))
//		{
//			return CODE_SYSTEM_NAME_LOINC;
//		}
//		else if (codeSystemOID.equals(CODE_SYSTEM_OID_SNOMED_CT))
//		{
//			return CODE_SYSTEM_NAME_SNOMED_CT;
//		}
//		else if (codeSystemOID.equals(CODE_SYSTEM_OID_OPEN_CDS))
//		{
//			return CODE_SYSTEM_NAME_OPEN_CDS;
//		}
//		return null;	*/	
//	}
}