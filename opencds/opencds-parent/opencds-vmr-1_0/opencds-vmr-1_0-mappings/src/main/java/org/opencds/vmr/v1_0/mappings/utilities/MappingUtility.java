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

package org.opencds.vmr.v1_0.mappings.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.utilities.DateUtility;
import org.opencds.common.utilities.MiscUtility;
import org.opencds.vmr.v1_0.internal.GoalValue;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.ADXP;
import org.opencds.vmr.v1_0.internal.datatypes.ANY;
import org.opencds.vmr.v1_0.internal.datatypes.AddressPartType;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.CS;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.ENXP;
import org.opencds.vmr.v1_0.internal.datatypes.EntityNamePartQualifier;
import org.opencds.vmr.v1_0.internal.datatypes.EntityNamePartType;
import org.opencds.vmr.v1_0.internal.datatypes.EntityNameUse;
import org.opencds.vmr.v1_0.internal.datatypes.II;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.IVLINT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;
import org.opencds.vmr.v1_0.internal.datatypes.IVLREAL;
import org.opencds.vmr.v1_0.internal.datatypes.IVLRTO;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.PostalAddressUse;
import org.opencds.vmr.v1_0.internal.datatypes.REAL;
import org.opencds.vmr.v1_0.internal.datatypes.RTO;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;
import org.opencds.vmr.v1_0.internal.datatypes.TelecommunicationAddressUse;
import org.opencds.vmr.v1_0.internal.datatypes.TelecommunicationCapability;

/**
 * <p>MappingUtility is used to perform mapping functions between external and internal
 * data structures.  In this class, internal datatypes are always fully specified, and
 * the schema datatypes are imported from the schema.
 * </p>
 * 
 * <p>Basic principle we used for inbound mapping:
 * 
 * 1. Map into a null hen there is a null or no useable value in the input.  
 * 2. If there appears to be part of a complex datatype, but required attributes are missing 
 * 		then an exception is thrown.
 * </p>
 * <p/>
 * 
 * <p>Initial Create Date: 2010-10-04</p>
 * <p>Last Update Date: 2011-04-11</p>
 * <p>Version history: v1.00 (2010-10-04).  Initial creation.</p>
 * <p>v2.00 (2011-02-09).  Revision to 2nd major version of vMR schema and internal classes, known as vmr.alpha.
 * <p>v3.00 (2011-08-01).  Revision to 3rd major version of vMR schema and internal classes, known as vmr.v1_0.
 *							This revision does not flatten internal datatypes other than II.
 *							This revision follows the method naming conventions of 
 *								<datatype>2<datatype>Internal() and
 *								<datatype>Internal2<datatype>
 *							This revision imports all of the internal datatypes, and fully specifies the external ones
 *							
 * @author David Shields, Daryl Chertcoff
 * @version 3.00
 */

public class MappingUtility extends java.lang.Object {

	private static Log logger = LogFactory.getLog(MappingUtility.class);
	
	
	
	protected static CD noInformation = setNoInfo();
	
	protected static CD setNoInfo() {
		CD noInfo = new CD();
		noInfo.setCodeSystem("");
		noInfo.setCode("");
		return noInfo;		
	}
	
	public static CD OPENCDS_NO_INFORMATION = noInformation;
	
	public MappingUtility()
	{
	}
	
	public static ANY aNY2ANYInternal(org.opencds.vmr.v1_0.schema.ANY pANY) {
	
		if (pANY == null)
			return null;
		
//		ANY lANY = new ANY(pANY);
		ANY lANY = new ANY();
		return lANY;
	}
	
	public static org.opencds.vmr.v1_0.schema.ANY aNYInternal2ANY(ANY pANY) {
		
		if (pANY == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.ANY lANY = new org.opencds.vmr.v1_0.schema.ANY();
		return lANY;
	}
	
	public static GoalValue targetGoalValue2TargetGoalValueInternal(org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue pANY) {
		
		if (pANY == null)
			return null;
		
		GoalValue lANY = new GoalValue();
		
		     if (pANY.getBoolean() != null) lANY.set_boolean( bL2BLInternal( ((org.opencds.vmr.v1_0.schema.BL)pANY.getBoolean()) ));
		else if (pANY.getConcept() != null) lANY.setConcept( cD2CDInternal( ((org.opencds.vmr.v1_0.schema.CD)pANY.getConcept()) ));
		else if (pANY.getDecimal() != null) lANY.setDecimal( rEAL2REALInternal((org.opencds.vmr.v1_0.schema.REAL)pANY.getDecimal()) );
		else if (pANY.getDecimalRange() != null) lANY.setDecimalRange( iVLREAL2IVLREALInternal( ((org.opencds.vmr.v1_0.schema.IVLREAL)pANY.getDecimalRange()) ));
		else if (pANY.getInteger() != null) lANY.setInteger( iNT2INTInternal( ((org.opencds.vmr.v1_0.schema.INT)pANY.getInteger()) ));
		else if (pANY.getIntegerRange() != null) lANY.setIntegerRange( iVLINT2IVLINTInternal( ((org.opencds.vmr.v1_0.schema.IVLINT)pANY.getIntegerRange()) ));
		else if (pANY.getPhysicalQuantity() != null) lANY.setPhysicalQuantity( pQ2PQInternal( ((org.opencds.vmr.v1_0.schema.PQ)pANY.getPhysicalQuantity()) ));
		else if (pANY.getPhysicalQuantityRange() != null) lANY.setPhysicalQuantityRange( iVLPQ2IVLPQInternal( ((org.opencds.vmr.v1_0.schema.IVLPQ)pANY.getPhysicalQuantityRange()) ));
		else if (pANY.getRatio() != null) lANY.setRatio( rTO2RTOInternal( ((org.opencds.vmr.v1_0.schema.RTO)pANY.getRatio()) ));
		else if (pANY.getRatioRange() != null) lANY.setRatioRange( iVLRTO2IVLRTOInternal( ((org.opencds.vmr.v1_0.schema.IVLRTO)pANY.getRatioRange()) ));
		else if (pANY.getSimpleConcept() != null) lANY.setSimpleConcept( cS2CSInternal( ((org.opencds.vmr.v1_0.schema.CS)pANY.getSimpleConcept()) ));
		else if (pANY.getText() != null) lANY.setText( sT2STInternal( ((org.opencds.vmr.v1_0.schema.ST)pANY.getText()) ));
		else if (pANY.getTime() != null) lANY.setTime( tS2DateInternal( ((org.opencds.vmr.v1_0.schema.TS)pANY.getTime()) ));
		else if (pANY.getTimeRange() != null) lANY.setTimeRange( iVLTS2IVLDateInternal( ((org.opencds.vmr.v1_0.schema.IVLTS)pANY.getTimeRange()) ));
		
		return lANY;
	}
	
	public static org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue targetGoalValueInternal2targetGoalValue(GoalValue pANY) {
		
		if (pANY == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue lANY = new org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue();
		
		     if (pANY.get_boolean() != null) lANY.setBoolean( bLInternal2BL( ((BL)pANY.get_boolean()) ));
		else if (pANY.getConcept() != null) lANY.setConcept( cDInternal2CD( ((CD)pANY.getConcept()) ));
		else if (pANY.getDecimal() != null) lANY.setDecimal( rEALInternal2REAL((REAL)pANY.getDecimal()) );
		else if (pANY.getDecimalRange() != null) lANY.setDecimalRange( iVLREALInternal2IVLREAL( ((IVLREAL)pANY.getDecimalRange()) ));
		else if (pANY.getInteger() != null) lANY.setInteger( iNTInternal2INT( ((INT)pANY.getInteger()) ));
		else if (pANY.getIntegerRange() != null) lANY.setIntegerRange( iVLINTInternal2IVLINT( ((IVLINT)pANY.getIntegerRange()) ));
		else if (pANY.getPhysicalQuantity() != null) lANY.setPhysicalQuantity( pQInternal2PQ( ((PQ)pANY.getPhysicalQuantity()) ));
		else if (pANY.getPhysicalQuantityRange() != null) lANY.setPhysicalQuantityRange( iVLPQInternal2IVLPQ( ((IVLPQ)pANY.getPhysicalQuantityRange()) ));
		else if (pANY.getRatio() != null) lANY.setRatio( rTOInternal2RTO( ((RTO)pANY.getRatio()) ));
		else if (pANY.getRatioRange() != null) lANY.setRatioRange( iVLRTOInternal2IVLRTO( ((IVLRTO)pANY.getRatioRange()) ));
		else if (pANY.getSimpleConcept() != null) lANY.setSimpleConcept( cSInternal2CS( ((String)pANY.getSimpleConcept()) ));
		else if (pANY.getText() != null) lANY.setText( sTInternal2ST( ((String)pANY.getText()) ));
		else if (pANY.getTime() != null) lANY.setTime( dateInternal2TS( ((java.util.Date)pANY.getTime()) ));
		else if (pANY.getTimeRange() != null) lANY.setTimeRange( iVLDateInternal2IVLTS( ((IVLDate)pANY.getTimeRange()) ));

		return lANY;
	}
	
	public static ObservationValue observationValue2ObservationValueInternal(org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue pANY) 
	throws DataFormatException, InvalidDataException {
		
		if (pANY == null)
			return null;
		
		ObservationValue lANY = new ObservationValue();
		
		     if (pANY.getAddress() != null) lANY.setAddress( aD2ADInternal( ((org.opencds.vmr.v1_0.schema.AD)pANY.getAddress()) ));
		else if (pANY.getBoolean() != null) lANY.set_boolean( bL2BLInternal( ((org.opencds.vmr.v1_0.schema.BL)pANY.getBoolean()) ));
		else if (pANY.getConcept() != null) lANY.setConcept( cD2CDInternal( ((org.opencds.vmr.v1_0.schema.CD)pANY.getConcept()) ));
		else if (pANY.getDecimal() != null) lANY.setDecimal( rEAL2REALInternal((org.opencds.vmr.v1_0.schema.REAL)pANY.getDecimal()) );
		else if (pANY.getDecimalRange() != null) lANY.setDecimalRange( iVLREAL2IVLREALInternal( ((org.opencds.vmr.v1_0.schema.IVLREAL)pANY.getDecimalRange()) ));
		else if (pANY.getIdentifier() != null) lANY.setIdentifier( iI2FlatId( ((org.opencds.vmr.v1_0.schema.II)pANY.getIdentifier()) ));
		else if (pANY.getInteger() != null) lANY.setInteger( iNT2INTInternal( ((org.opencds.vmr.v1_0.schema.INT)pANY.getInteger()) ));
		else if (pANY.getIntegerRange() != null) lANY.setIntegerRange( iVLINT2IVLINTInternal( ((org.opencds.vmr.v1_0.schema.IVLINT)pANY.getIntegerRange()) ));
		else if (pANY.getName() != null) lANY.setName( eN2ENInternal( ((org.opencds.vmr.v1_0.schema.EN)pANY.getName()) ));
		else if (pANY.getPhysicalQuantity() != null) lANY.setPhysicalQuantity( pQ2PQInternal( ((org.opencds.vmr.v1_0.schema.PQ)pANY.getPhysicalQuantity()) ));
		else if (pANY.getPhysicalQuantityRange() != null) lANY.setPhysicalQuantityRange( iVLPQ2IVLPQInternal( ((org.opencds.vmr.v1_0.schema.IVLPQ)pANY.getPhysicalQuantityRange()) ));
		else if (pANY.getRatio() != null) lANY.setRatio( rTO2RTOInternal( ((org.opencds.vmr.v1_0.schema.RTO)pANY.getRatio()) ));
		else if (pANY.getRatioRange() != null) lANY.setRatioRange( iVLRTO2IVLRTOInternal( ((org.opencds.vmr.v1_0.schema.IVLRTO)pANY.getRatioRange()) ));
		else if (pANY.getSimpleConcept() != null) lANY.setSimpleConcept( cS2CSInternal( ((org.opencds.vmr.v1_0.schema.CS)pANY.getSimpleConcept()) ));
		else if (pANY.getTelecom() != null) lANY.setTelecom( tEL2TELInternal( ((org.opencds.vmr.v1_0.schema.TEL)pANY.getTelecom()) ));
		else if (pANY.getText() != null) lANY.setText( sT2STInternal( ((org.opencds.vmr.v1_0.schema.ST)pANY.getText()) ));
		else if (pANY.getTime() != null) lANY.setTime( tS2DateInternal( ((org.opencds.vmr.v1_0.schema.TS)pANY.getTime()) ));
		else if (pANY.getTimeRange() != null) lANY.setTimeRange( iVLTS2IVLDateInternal( ((org.opencds.vmr.v1_0.schema.IVLTS)pANY.getTimeRange()) ));
		
		return lANY;
	}
	
	public static org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue observationValueInternal2ObservationValue(ObservationValue pANY) 
	throws DataFormatException, InvalidDataException {
		
		if (pANY == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue lANY = new org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue();
		
		     if (pANY.getAddress() != null) lANY.setAddress( aDInternal2AD( ((AD)pANY.getAddress()) ));
		else if (pANY.get_boolean() != null) lANY.setBoolean( bLInternal2BL( ((BL)pANY.get_boolean()) ));
		else if (pANY.getConcept() != null) lANY.setConcept( cDInternal2CD( ((CD)pANY.getConcept()) ));
		else if (pANY.getDecimal() != null) lANY.setDecimal( rEALInternal2REAL((REAL)pANY.getDecimal()) );
		else if (pANY.getDecimalRange() != null) lANY.setDecimalRange( iVLREALInternal2IVLREAL( ((IVLREAL)pANY.getDecimalRange()) ));
		else if (pANY.getIdentifier() != null) lANY.setIdentifier( iIFlat2II( ((String)pANY.getIdentifier()) ));
		else if (pANY.getInteger() != null) lANY.setInteger( iNTInternal2INT( ((INT)pANY.getInteger()) ));
		else if (pANY.getIntegerRange() != null) lANY.setIntegerRange( iVLINTInternal2IVLINT( ((IVLINT)pANY.getIntegerRange()) ));
		else if (pANY.getName() != null) lANY.setName( eNInternal2EN( ((EN)pANY.getName()) ));
		else if (pANY.getPhysicalQuantity() != null) lANY.setPhysicalQuantity( pQInternal2PQ( ((PQ)pANY.getPhysicalQuantity()) ));
		else if (pANY.getPhysicalQuantityRange() != null) lANY.setPhysicalQuantityRange( iVLPQInternal2IVLPQ( ((IVLPQ)pANY.getPhysicalQuantityRange()) ));
		else if (pANY.getRatio() != null) lANY.setRatio( rTOInternal2RTO( ((RTO)pANY.getRatio()) ));
		else if (pANY.getRatioRange() != null) lANY.setRatioRange( iVLRTOInternal2IVLRTO( ((IVLRTO)pANY.getRatioRange()) ));
		else if (pANY.getSimpleConcept() != null) lANY.setSimpleConcept( cSInternal2CS( ((String)pANY.getSimpleConcept()) ));
		else if (pANY.getTelecom() != null) lANY.setTelecom( tELInternal2TEL( ((TEL)pANY.getTelecom()) ));
		else if (pANY.getText() != null) lANY.setText( sTInternal2ST( ((String)pANY.getText()) ));
		else if (pANY.getTime() != null) lANY.setTime( dateInternal2TS( ((java.util.Date)pANY.getTime()) ));
		else if (pANY.getTimeRange() != null) lANY.setTimeRange( iVLDateInternal2IVLTS( ((IVLDate)pANY.getTimeRange()) ));
	
		return lANY;
	}
	
	public static org.opencds.vmr.v1_0.schema.BL bLInternal2BL(BL pBL) {

		if (pBL == null)
			return null;

		org.opencds.vmr.v1_0.schema.BL lBL = new org.opencds.vmr.v1_0.schema.BL();
		lBL.setValue(pBL.isValue());
		return lBL;
	}

	public static BL bL2BLInternal(org.opencds.vmr.v1_0.schema.BL pBL) {

		if (pBL == null)
			return null;

		BL lBL = new BL();
		lBL.setValue(pBL.isValue());
		return lBL;
	}

	public static org.opencds.vmr.v1_0.schema.CD cDInternal2CD( CD cdInternal )
	{
		if ( (cdInternal == null) 
				|| ( (( cdInternal.getCode() == null ) || ( "".equals(cdInternal.getCode()) )) 
				  && (( cdInternal.getOriginalText() == null ) || ( "".equals(cdInternal.getOriginalText()) )) 
				  && (( cdInternal.getCodeSystem() == null ) || ( "".equals(cdInternal.getCodeSystem()) )) ) ) {
			//nothing to work with
			return null;
		}
		org.opencds.vmr.v1_0.schema.CD cd = new org.opencds.vmr.v1_0.schema.CD();
		try {
			if ( ((( cdInternal.getCode() == null ) || ( "".equals(cdInternal.getCode()) )) 
					&& (( cdInternal.getOriginalText() == null ) || ( "".equals(cdInternal.getOriginalText()) )
					|| (( cdInternal.getCodeSystem() == null ) || ( "".equals(cdInternal.getCodeSystem()) )) )) ) {
				throw new RuntimeException( "CDInternal2CD(code=" + cdInternal.getCode() + ", codeSystem=" + cdInternal.getCodeSystem() 
						+ ", " + cdInternal.getDisplayName() + ", " + cdInternal.getCodeSystemName() 
						+ ", originalText=" + cdInternal.getOriginalText() + ") must have both codeSystem and (code OR originalText).");
			}
			if ( cdInternal.getCode() != null ) {
				cd.setCode( cdInternal.getCode() );
			}
			if ( cdInternal.getCodeSystem() != null ) {
			cd.setCodeSystem( cdInternal.getCodeSystem() );
			}
			if ( cdInternal.getCodeSystemName() != null ) {
				cd.setCodeSystemName( cdInternal.getCodeSystemName() );
			}
			if (( cdInternal.getDisplayName() != null ) && ( !"".equals(cdInternal.getDisplayName()) )) {
				cd.setDisplayName( cdInternal.getDisplayName() );
			}
			if (( cdInternal.getOriginalText() != null ) && ( !"".equals(cdInternal.getOriginalText()) )) {
				cd.setOriginalText( cdInternal.getOriginalText() );
			}
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "cDInternal2CD(" + cdInternal.toString() + ") had errors: " + e.getMessage() );
		}
		return cd;
	}

	public static CD cD2CDInternal( org.opencds.vmr.v1_0.schema.CD cdSchema )
	{
		if ( (cdSchema == null) 
				|| ( (( cdSchema.getCode() == null ) || ( "".equals(cdSchema.getCode()) )) 
						&& (( cdSchema.getOriginalText() == null ) || ( "".equals(cdSchema.getOriginalText()) )) 
						&& (( cdSchema.getCodeSystem() == null ) || ( "".equals(cdSchema.getCodeSystem()) )) ) ) {
			//nothing to work with
			return OPENCDS_NO_INFORMATION;
		}
		CD cd = new CD();
		try {
			if ( ( (( cdSchema.getCode() == null ) || ( "".equals(cdSchema.getCode()) )) 
					&& (( cdSchema.getOriginalText() == null ) || ( "".equals(cdSchema.getOriginalText()) )) )
					|| (( cdSchema.getCodeSystem() == null ) || ( "".equals(cdSchema.getCodeSystem()) )) )  {
				throw new RuntimeException( "cD2CDInternal(" + cdSchema.getCode() + "," + cdSchema.getCodeSystem() + "," 
						+ cdSchema.getDisplayName() + "," + cdSchema.getCodeSystemName() + "," + cdSchema.getOriginalText()
						+ ") both codeSystem and (code OR originalText) must have a value.");
			}
			if ( cdSchema.getCode() != null ) {
				cd.setCode( cdSchema.getCode() );
			}
			if ( cdSchema.getCodeSystem() != null ) {
				cd.setCodeSystem( cdSchema.getCodeSystem() );
			}
			if ( cdSchema.getDisplayName() != null ) {
				cd.setDisplayName( cdSchema.getDisplayName() );
			}
			if ( cdSchema.getCodeSystemName() != null ) {
				cd.setCodeSystemName( cdSchema.getCodeSystemName() );
			}
			if ( cdSchema.getOriginalText() != null ) {
				cd.setOriginalText( cdSchema.getOriginalText() );
			}
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "cD2CDInternal(" + cdSchema.toString() + ") had errors: " + e.getMessage() );
		}
		return cd;
	}

	/**
	 * Convert code as a Internal String to CS
	 * @param code
	 * @return
	 */
	public static org.opencds.vmr.v1_0.schema.CS cSInternal2CS( String code )
	{
		org.opencds.vmr.v1_0.schema.CS cs = new org.opencds.vmr.v1_0.schema.CS();
		try {
			cs.setCode( code );
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "cSInternal2CS(" + code + ") had errors: " + e.getMessage() );
		}
		return cs;
	}

	/**
	 * Convert code as a CS to Internal String
	 * @param cs
	 * @return
	 */
	public static String cS2CSInternal( org.opencds.vmr.v1_0.schema.CS cs )
	{
		try {
			String st = cs.getCode();
			return st;
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "cS2Code(" + cs.toString() + ") had errors: " + e.getMessage() );
		}
	}

	/**
	 * Convert CS (not as an enumeration) to just the code (as a String)
	 * @param cd
	 * @return
	 */
	public static String cS2Code( CS cd )
	{
		try {
			String code = cd.getCode();
			return code;
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "cS2Code(" + cd.toString() + ") had errors: " + e.getMessage() );
		}
	}

//	/**
//	 * Build an external II from a root and optional extension (extension not present when the root is a GUID)
//	 * assumes that an OID and a GUID can be distinguished by the presence of "0." or "1." or "2." in
//	 * 		leading 2 characters of root when there is an OID
//	 * 		- Not in use at this time, replaced by iIFlat2II which replaces the internal II with a String
//	 * @param rootCaratExtension
//	 * @return
//	 */
//	public static org.opencds.vmr.v1_0.schema.II iIInternal2II( II rootCaratExtension )
//	{
//		if ( ( rootCaratExtension == null ) || ( "".equals(rootCaratExtension) ) ) {
//			//nothing to work with
//			return null;
//		}
//		int 	positionCarat	= rootCaratExtension.getValue().indexOf("^", 0);
//		String root 			= null;
//		String extension		= null;
//		if ( positionCarat < 0 ) {	// no carat found
//			root			= rootCaratExtension.getValue();
//		} else {
//			root			= rootCaratExtension.getValue().substring(0, positionCarat);
//			extension 		= rootCaratExtension.getValue().substring(positionCarat + 1);
//		}
//		org.opencds.vmr.v1_0.schema.II 	ii 	= new org.opencds.vmr.v1_0.schema.II();
//		try {
//			if ( root == null ) {
//				throw new RuntimeException( "iIInternal2II(" + root + "," + extension + ") must have at least a root.");
//			}
//			if ( 		 ( extension == null ) 
//					&& ( ( root.startsWith("0.") ) || ( root.startsWith("1.") ) || ( root.startsWith("2.") ) ) ) {
//				throw new RuntimeException( "iIInternal2II(" + rootCaratExtension + ") must not have OID for root without extension.");
//			} else if ( !( root.startsWith("0.") ) && !( root.startsWith("1.") ) && !( root.startsWith("2.") ) && ( extension != null ) ) {
//				throw new RuntimeException( "iIInternal2II(" + root + "," + extension + ") must have OID for root when extension exists.");
//			}
//
//			ii.setRoot( root );
//			if ( extension != null ) {
//				ii.setExtension( extension );
//			}
//		} catch (Exception e) {
//			e.printStackTrace();  
//			throw new RuntimeException( "iIInternal2II(" + rootCaratExtension + ") had errors: " + e.getMessage() );
//		}
//		return ii;
//	}

	/**
	 * Builds a String containing either a GUID or an OID 
 	 *		- throws an exception if there is NOT a root
 	 *		- throws an exception if there is a non-null and non-empty extension
	 * 		- only used for templates for CDSInput and VMR 
	 * @param context
	 * @param ii
	 * @return
	 */
	public static String iI2Root( String context, org.opencds.vmr.v1_0.schema.II ii )
	{
		try {
			if (  (ii == null ) || ( ii.getRoot() == null ) ) {
				//an ID cannot be null, nor have a null root;
				throw new DataFormatException( context + " context: iI2Root(" + ii 
						+ ") had errors: TemplateID must have GUID or OID as root" );			
			}
			String root = ii.getRoot();
//			if ( ( ii.getExtension() != null ) && !( "".equals(ii.getExtension()) ) ) {
//				// corrected to:  OID, may NOT have extension for this method
//				throw new DataFormatException( context + " context: iI2Root(" + ii.toString() 
//						+ ") had errors: TemplateID must have a root and must NOT have an extension" );			
//			}
			return root;
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "iI2Root(" + ii + ") had errors: " + e.getMessage() );
		}
	}

//	/**
//	 * Builds a String containing just the extension 
//	 * 		- not in use at this time
//	 * @param context
//	 * @param ii
//	 * @return
//	 */
//	public static String iI2Extension( String context, org.opencds.vmr.v1_0.schema.II ii )
//	{
//		if (  (ii == null ) || ( ii.getExtension() == null ) ) {
//			//return null;
//			return "";
//		}
//		try {
//			String extension = ii.getExtension();
//			return extension;
//		} catch (Exception e) {
//			e.printStackTrace();  
//			throw new RuntimeException( "iI2Extension(" + ii.toString() + ") had errors: " + e.getMessage() );
//		}
//	}

//	/**
//	 * Builds an internal id by combining root (+ "^" + an extension (if one is present) )
//	 * 		- Not in use at this time
//	 * @param ii
//	 * @return
//	 */
//	public static II iI2IIInternal( org.opencds.vmr.v1_0.schema.II ii ) {
//		String iD = null;
//		if (ii == null){
//			iD = MiscUtility.getUUIDAsString();
//		} else {
//			try {
//				iD = ii.getRoot();
//				if (( ii.getExtension() != null ) && ( !"".equals(ii.getExtension() ))) {
//					iD += "^" + ii.getExtension();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();  
//				throw new RuntimeException( "iI2IIInternal(" + ii.toString() + ") had errors: " + e.getMessage() );
//			}
//		}
//		II iiInternal = new II();
//		iiInternal.setValue(iD);
//		return iiInternal;
//	}

	/**
	 * Build an II list from a list of root and optional extension (extension not present when the root is a GUID)
	 * @param rootCaratExtension
	 * @return
	 */
	public static List<org.opencds.vmr.v1_0.schema.II> iIFlatList2IIList( String[] rootCaratExtensionList )
	{
		List<org.opencds.vmr.v1_0.schema.II> output = new ArrayList<org.opencds.vmr.v1_0.schema.II>();
		for (String input : rootCaratExtensionList) {
			output.add(iIFlat2II(input));
		}
		return output;
	}

	/**
	 * Build an external II from a root and optional extension (extension not present when the root is a GUID)
	 * assumes that an OID and a GUID can be distinguished by the presence of "0." or "1." or "2." in
	 * 		leading 2 characters of root when there is an OID
	 * @param rootCaratExtension
	 * @return
	 */
	public static org.opencds.vmr.v1_0.schema.II iIFlat2II( String rootCaratExtension )
	{
		if ( ( rootCaratExtension == null ) || ( "".equals(rootCaratExtension) ) ) {
			//nothing to work with
			return null;
		}
		int 	positionCarat	= rootCaratExtension.indexOf("^", 0);
		String root 			= null;
		String extension		= null;
		if ( positionCarat < 0 ) {	// no carat found
			root			= rootCaratExtension;
		} else {
			root			= rootCaratExtension.substring(0, positionCarat);
			extension 		= rootCaratExtension.substring(positionCarat + 1);
		}
		org.opencds.vmr.v1_0.schema.II ii = new org.opencds.vmr.v1_0.schema.II();
		try {
			if ( root == null ) {
				throw new RuntimeException( "iIFlat2II(" + root + "," + extension + ") must have at least a root.");
//			} else if ( 		 ( extension == null ) 
//					&& ( ( root.startsWith("0.") ) || ( root.startsWith("1.") ) || ( root.startsWith("2.") ) ) ) {
//				throw new RuntimeException( "iIFlat2II(" + rootCaratExtension + ") must not have OID for root without extension.");
//			} else if ( (( !root.startsWith("0.") ) && ( !root.startsWith("1.") ) && ( !root.startsWith("2.") )) 
//					&& ( extension != null ) && ( !"".equals(extension) ) ) {
//				throw new RuntimeException( "iIFlat2II(" + root + "^" + extension + ") must have OID for root when extension exists.");
			}

			ii.setRoot( root );
			ii.setExtension( extension );
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "iIFlat2II(" + rootCaratExtension + ") had errors: " + e.getMessage() );
		}
		return ii;
	}

	public static String[] iIList2FlatIdList(List<org.opencds.vmr.v1_0.schema.II> iiList ) {
		if (iiList == null) {
			return null;
		}
		String[] output = new String[iiList.size()];
		for (int i = 0; i < iiList.size(); i++) {
			output[i] = iI2FlatId(iiList.get(i));
		}
		return output;
	}

	public static String iI2FlatId(org.opencds.vmr.v1_0.schema.II ii ) {
		String iD = null;
		try {
			iD = ii.getRoot();
			if (( ii.getExtension() != null ) && ( !"".equals(ii.getExtension() ))) {
				iD += "^" + ii.getExtension();
			}
		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "iI2FlatId(" + ii.toString() + ") had errors: " + e.getMessage() );
		}
		return iD;
	}


	public static INT iNT2INTInternal(org.opencds.vmr.v1_0.schema.INT pINT) {

		if (pINT == null)
			return null;

		INT lINT = new INT();
		lINT.setValue(pINT.getValue());
		return lINT;
	}


	public static org.opencds.vmr.v1_0.schema.INT iNTInternal2INT(INT pINT) {

		if (pINT == null)
			return null;

		org.opencds.vmr.v1_0.schema.INT lINT = new org.opencds.vmr.v1_0.schema.INT();
		lINT.setValue(pINT.getValue());
		return lINT;
	}



	public static org.opencds.vmr.v1_0.schema.PQ pQInternal2PQ( PQ pqInternal ) 
	{
		if ( (( pqInternal == null ))) { 		// && (( pqInternal.getUnit() == null ) || ( "".equals(pqInternal.getUnit()) ) ) ) {
			//nothing to work with
			return null;
		}
		org.opencds.vmr.v1_0.schema.PQ pq = new org.opencds.vmr.v1_0.schema.PQ();
		try {
			if ( ( pqInternal.getUnit() == null ) || ( "".equals( pqInternal.getUnit() ) ) ) {
				throw new RuntimeException( "pQInternal2PQ( " + pqInternal.toString() + " ) must have a valid unit.");
			}
			pq.setValue( pqInternal.getValue() );
			pq.setUnit( pqInternal.getUnit() );

		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "pQInternal2PQ( " + pqInternal.toString() + " ) had errors: " + e.getMessage() );
		}
		return pq;
	}

	public static PQ pQ2PQInternal( org.opencds.vmr.v1_0.schema.PQ pqSchema ) 
	{
		if ( (( pqSchema == null ))) { 		// && (( pqSchema.getUnit() == null ) || ( "".equals(pqSchema.getUnit()) ) ) ) {
			//nothing to work with
			return null;
		}
		PQ pq = new PQ();
		try {
			if ( ( pqSchema.getUnit() == null ) || ( "".equals( pqSchema.getUnit() ) ) ) {
				throw new RuntimeException( "pQ2PQInternal( " + pqSchema.toString() + " ) must have a valid unit.");
			}
			pq.setValue( pqSchema.getValue() );
			pq.setUnit( pqSchema.getUnit() );

		} catch (Exception e) {
			e.printStackTrace();  
			throw new RuntimeException( "pQ2PQInternal( " + pqSchema.toString() + " ) had errors: " + e.getMessage() );
		}
		return pq;
	}

	public static REAL rEAL2REALInternal(org.opencds.vmr.v1_0.schema.REAL pREAL) {
		if (pREAL == null)
			return null;

		REAL lREAL = new REAL();
		lREAL.setValue(pREAL.getValue());
		return lREAL;		
	}

	public static org.opencds.vmr.v1_0.schema.REAL rEALInternal2REAL(REAL pREAL) {
		if (pREAL == null)
			return null;

		org.opencds.vmr.v1_0.schema.REAL lREAL = new org.opencds.vmr.v1_0.schema.REAL();
		lREAL.setValue(pREAL.getValue());
		return lREAL;
	}

	public static RTO rTO2RTOInternal(org.opencds.vmr.v1_0.schema.RTO pRTO) {
		// 2 elements: PQ numerator; PQ denominator
		// changed to 2 elements: Real numerator; Real denominator by des 20110921
		if (pRTO == null)
			return null;

		RTO lRTO = new RTO();
		lRTO.setNumerator(pRTO.getNumerator());
		lRTO.setDenominator(pRTO.getDenominator());
		return lRTO;		
	}

	public static org.opencds.vmr.v1_0.schema.RTO rTOInternal2RTO(RTO pRTO) {
		// 2 elements: PQ numerator; PQ denominator
		// changed to 2 elements: Real numerator; Real denominator by des 20110921
		if (pRTO == null)
			return null;

		org.opencds.vmr.v1_0.schema.RTO lRTO = new org.opencds.vmr.v1_0.schema.RTO();
		lRTO.setNumerator(pRTO.getNumerator());
		lRTO.setDenominator(pRTO.getDenominator());
		return lRTO;
	}

	public static java.util.Date tS2DateInternal(org.opencds.vmr.v1_0.schema.TS pTS) {
		//changed to use java.util.Date for internal format des 20111119

		String _METHODNAME = "tS2TSInternal(): ";

//		if (pTS == null) {
//			return null;
//		}
//	above changed to below, des 2012-10-05
		
		if ((pTS == null) || ("".equals(pTS.getValue())) || ("null".equalsIgnoreCase(pTS.getValue()))) {
			return null;
		}

		String errStr = null;
		String hl7Time = pTS.getValue();
		String formatTemplate = "yyyyMMddHHmmss.SSSZZZZZ";
		if (hl7Time == null) {
			errStr = _METHODNAME + "getValue() is null; must follow format: " + formatTemplate;
			logger.error(errStr);
			throw new RuntimeException(errStr);
		}

		int hl7TimeLen = hl7Time.length();
		if (hl7TimeLen > 23) {
			errStr = _METHODNAME + "getValue() \"" + hl7Time + "\" is too long; must follow format: " + formatTemplate;
			logger.error(errStr);
			throw new RuntimeException(errStr);
		}

		// value is populated and correct length. Parse it and return internal TS if successful
		try {
			// properly formatted; set internal TS object
			return DateUtility.getInstance().getDateFromString(hl7Time, formatTemplate.substring(0, hl7Time.length() ));
		}
		catch (Exception e) {
			errStr = _METHODNAME + "getValue() \"" + hl7Time + "\" is in wrong format; must follow format: " + formatTemplate;
			throw new RuntimeException(errStr + ": " + e.getMessage() );
		}
	}

	public static org.opencds.vmr.v1_0.schema.TS dateInternal2TS(java.util.Date pDate) {
		//changed to use java.util.Date for internal format des 20111119

		String _METHODNAME = "tSInternal2TS(): ";

		if (pDate == null) {
			return null;
		}

		String errStr = null;
		java.util.Date javaTime = pDate;
		String formatTemplate = "yyyyMMddHHmmss.SSSZZZZZ";

		// Format java.util.Date and return schema TS if successful
		org.opencds.vmr.v1_0.schema.TS lTS = new org.opencds.vmr.v1_0.schema.TS();
		try {
			// format and set schema TS object
			lTS.setValue(DateUtility.getInstance().getDateAsString(javaTime, formatTemplate));
			return lTS;
		}
		catch (Exception e) {
			errStr = _METHODNAME + "java.util.Date \"" + javaTime + "\" threw exception trying to format as: " + formatTemplate;
			throw new RuntimeException(errStr + ": " + e.getMessage() );
		}
	}

	public static org.opencds.vmr.v1_0.schema.ST sTInternal2ST(String pST) {
		// changed to use String for internal ST, des 20111024
		if (pST == null)
			return null;

		org.opencds.vmr.v1_0.schema.ST lST = new org.opencds.vmr.v1_0.schema.ST();
		lST.setValue(pST);
		return lST;
	}

	public static String sT2STInternal(org.opencds.vmr.v1_0.schema.ST pST) {
		// changed to use String for internal ST, des 20111024
		if (pST == null)
			return null;

		String lST = new String();
		lST = pST.getValue();
		return lST;
	}

	public static IVLINT iVLINT2IVLINTInternal(org.opencds.vmr.v1_0.schema.IVLINT pIVLINT) {
		if (pIVLINT == null)
			return null;
		
		IVLINT lIVLINT = new IVLINT();
		if (pIVLINT.isLowIsInclusive() != null) lIVLINT.setLowIsInclusive(pIVLINT.isLowIsInclusive());
		if (pIVLINT.isHighIsInclusive() != null) lIVLINT.setHighIsInclusive(pIVLINT.isHighIsInclusive());
		lIVLINT.setLow(pIVLINT.getLow());
		lIVLINT.setHigh(pIVLINT.getHigh());
		return lIVLINT;
	}

	public static org.opencds.vmr.v1_0.schema.IVLINT iVLINTInternal2IVLINT(IVLINT pIVLINT) {
		if (pIVLINT == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.IVLINT lIVLINT = new org.opencds.vmr.v1_0.schema.IVLINT();
		if (pIVLINT.isLowIsInclusive() != null) lIVLINT.setLowIsInclusive(pIVLINT.isLowIsInclusive());
		if (pIVLINT.isHighIsInclusive() != null) lIVLINT.setHighIsInclusive(pIVLINT.isHighIsInclusive());
		lIVLINT.setLow(pIVLINT.getLow());
		lIVLINT.setHigh(pIVLINT.getHigh());
		return lIVLINT;
	}

	public static IVLPQ iVLPQ2IVLPQInternal(org.opencds.vmr.v1_0.schema.IVLPQ pIVLPQ) {
		if (pIVLPQ == null)
			return null;
		
		IVLPQ lIVLPQ = new IVLPQ();
		lIVLPQ.setLowUnit( pIVLPQ.getLowUnit());
		lIVLPQ.setLowValue(pIVLPQ.getLowValue());
		lIVLPQ.setHighUnit(pIVLPQ.getHighUnit());
		lIVLPQ.setHighValue(pIVLPQ.getHighValue());
		if (pIVLPQ.isLowIsInclusive() != null) lIVLPQ.setLowIsInclusive(pIVLPQ.isLowIsInclusive());
		if (pIVLPQ.isHighIsInclusive() != null) lIVLPQ.setHighIsInclusive(pIVLPQ.isHighIsInclusive());
		
		return lIVLPQ;
	}
	
	public static org.opencds.vmr.v1_0.schema.IVLPQ iVLPQInternal2IVLPQ(IVLPQ pIVLPQ) {
		
		if (pIVLPQ == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.IVLPQ lIVLPQ = new org.opencds.vmr.v1_0.schema.IVLPQ();
		lIVLPQ.setLowUnit(pIVLPQ.getLowUnit());
		lIVLPQ.setLowValue(pIVLPQ.getLowValue());
		lIVLPQ.setHighUnit(pIVLPQ.getHighUnit());
		lIVLPQ.setHighValue(pIVLPQ.getHighValue());
		if (pIVLPQ.getLowIsInclusive() != null) lIVLPQ.setLowIsInclusive(pIVLPQ.getLowIsInclusive());
		if (pIVLPQ.getHighIsInclusive() != null) lIVLPQ.setHighIsInclusive(pIVLPQ.getHighIsInclusive());
		
		return lIVLPQ;
	}

	public static IVLDate iVLTS2IVLDateInternal(org.opencds.vmr.v1_0.schema.IVLTS pIVLTS) {

		if (pIVLTS == null)
			return null;

		IVLDate lIVLDate = new IVLDate();
		
//		if (pIVLTS.getLow() != null) {
//		if ( (pIVLTS.getLow() != null) && (! "".equals( pIVLTS.getLow())) ) {
		if ( (pIVLTS.getLow() != null) 
				&& (! "null".equals( pIVLTS.getLow()))
				&& (! "".equals( pIVLTS.getLow())) ) {
			org.opencds.vmr.v1_0.schema.TS low = new org.opencds.vmr.v1_0.schema.TS();//
			low.setValue( pIVLTS.getLow() );
			lIVLDate.setLow( tS2DateInternal( low ) );
		}

//		if (pIVLTS.getHigh() != null) {
//		if ( (pIVLTS.getHigh() != null) && (! "".equals(pIVLTS.getHigh())) ) {
		if ( (pIVLTS.getHigh() != null) 
				&& (! "null".equals(pIVLTS.getHigh()))
				&& (! "".equals(pIVLTS.getHigh())) ) {
			org.opencds.vmr.v1_0.schema.TS high = new org.opencds.vmr.v1_0.schema.TS();//
			high.setValue( pIVLTS.getHigh() );
			lIVLDate.setHigh( tS2DateInternal( high ) );
		}
		
		if ( pIVLTS.isLowIsInclusive() != null ) lIVLDate.setLowIsInclusive(pIVLTS.isLowIsInclusive());
		if ( pIVLTS.isHighIsInclusive() != null ) lIVLDate.setHighIsInclusive(pIVLTS.isHighIsInclusive());

		return lIVLDate;
	}

	public static org.opencds.vmr.v1_0.schema.IVLTS iVLDateInternal2IVLTS(IVLDate pIVLDate) {

		if (pIVLDate == null)
			return null;

		org.opencds.vmr.v1_0.schema.IVLTS lIVLTS = new org.opencds.vmr.v1_0.schema.IVLTS();
		
		if (pIVLDate.getLow() != null) {
			java.util.Date low = pIVLDate.getLow();
			org.opencds.vmr.v1_0.schema.TS lowExternal = dateInternal2TS( low );
			lIVLTS.setLow( lowExternal.getValue() );
		}
		
		if (pIVLDate.getHigh() != null) {
			java.util.Date high = pIVLDate.getHigh();
			org.opencds.vmr.v1_0.schema.TS highExternal = dateInternal2TS( high );
			lIVLTS.setHigh( highExternal.getValue() );
		}
		
		if ( pIVLDate.isLowIsInclusive() != null ) lIVLTS.setLowIsInclusive(pIVLDate.isLowIsInclusive());
		if ( pIVLDate.isHighIsInclusive() != null ) lIVLTS.setHighIsInclusive(pIVLDate.isHighIsInclusive());

		return lIVLTS;
	}
	
	
	public static IVLREAL iVLREAL2IVLREALInternal(org.opencds.vmr.v1_0.schema.IVLREAL pIVLREAL) {
		if (pIVLREAL == null)
			return null;
		
		IVLREAL lIVLREAL = new IVLREAL();
		if (pIVLREAL.isLowIsInclusive() != null) lIVLREAL.setLowIsInclusive(pIVLREAL.isLowIsInclusive());
		if (pIVLREAL.isHighIsInclusive() != null) lIVLREAL.setHighIsInclusive(pIVLREAL.isHighIsInclusive());
		lIVLREAL.setLow(pIVLREAL.getLow());
		lIVLREAL.setHigh(pIVLREAL.getHigh());
		return lIVLREAL;
	}
	

	public static org.opencds.vmr.v1_0.schema.IVLREAL iVLREALInternal2IVLREAL(IVLREAL ivlreal) {
		if (ivlreal == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.IVLREAL lIVLREAL = new org.opencds.vmr.v1_0.schema.IVLREAL();
		if (ivlreal.isLowIsInclusive() != null) lIVLREAL.setLowIsInclusive(ivlreal.isLowIsInclusive());
		if (ivlreal.isHighIsInclusive() != null) lIVLREAL.setHighIsInclusive(ivlreal.isHighIsInclusive());
		lIVLREAL.setLow(ivlreal.getLow());
		lIVLREAL.setHigh(ivlreal.getHigh());
		return lIVLREAL;
	}
	

	public static IVLRTO iVLRTO2IVLRTOInternal(org.opencds.vmr.v1_0.schema.IVLRTO pIVLRTO) {
		if (pIVLRTO == null)
			return null;
		
		IVLRTO lIVLRTO = new IVLRTO();
		if (pIVLRTO.isLowIsInclusive() != null) lIVLRTO.setLowIsInclusive(pIVLRTO.isLowIsInclusive());
		if (pIVLRTO.isHighIsInclusive() != null) lIVLRTO.setHighIsInclusive(pIVLRTO.isHighIsInclusive());
		lIVLRTO.setLowDenominator(pIVLRTO.getLowDenominator());
		lIVLRTO.setLowNumerator(pIVLRTO.getLowNumerator());
		lIVLRTO.setHighDenominator(pIVLRTO.getHighDenominator());
		lIVLRTO.setHighNumerator(pIVLRTO.getHighNumerator());
		return lIVLRTO;
	}


	public static org.opencds.vmr.v1_0.schema.IVLRTO iVLRTOInternal2IVLRTO(IVLRTO pIVLRTO) {
		if (pIVLRTO == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.IVLRTO lIVLRTO = new org.opencds.vmr.v1_0.schema.IVLRTO();
		if (pIVLRTO.getLowIsInclusive() != null) lIVLRTO.setLowIsInclusive(pIVLRTO.getLowIsInclusive());
		if (pIVLRTO.getHighIsInclusive() != null) lIVLRTO.setHighIsInclusive(pIVLRTO.getHighIsInclusive());
		lIVLRTO.setLowDenominator(pIVLRTO.getLowDenominator());
		lIVLRTO.setLowNumerator(pIVLRTO.getLowNumerator());
		lIVLRTO.setHighDenominator(pIVLRTO.getHighDenominator());
		lIVLRTO.setHighNumerator(pIVLRTO.getHighNumerator());
		return lIVLRTO;
	}


	/**
	 * Translate from internal EN object to external EN object
	 * @param pADEx external EN object
	 * @return EN external EN object
	 */
	public static org.opencds.vmr.v1_0.schema.EN eNInternal2EN(EN pENInt) 
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "eNInternal2EN(): ";

		if (pENInt == null) {
			return null;
		}

		String errStr = null;
		org.opencds.vmr.v1_0.schema.EN lENExt = new org.opencds.vmr.v1_0.schema.EN();

		// Create external List<ENXP> element - at least one required
		List<ENXP> lIntEntityPart = pENInt.getPart();
		if (lIntEntityPart == null || lIntEntityPart.size() == 0) { // vmr spec says there must be at least one EntityPart
			errStr = _METHODNAME + "List<ENXP> element of internal EN datatype not populated - required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new DataFormatException(errStr);
		}
		Iterator<ENXP> lIntEntityPartIter = lIntEntityPart.iterator();
		int count = 0;
		while (lIntEntityPartIter.hasNext()) {
			ENXP lENXPInt = lIntEntityPartIter.next();
			org.opencds.vmr.v1_0.schema.ENXP lENXPExp = eNXPInternal2ENXP(lENXPInt);
			lENExt.getPart().add(lENXPExp);
			count++;
		}
		if (count < 1) {
			errStr = _METHODNAME + "No int->ext translations of List<ENXP> successful - at least one member of List<ENXP> required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new InvalidDataException(errStr);
		}

		// Now transfer over internal to external List<EntityNameUse> (optional in vmr spec)
		List<EntityNameUse> lEntityNameUseListInt = pENInt.getUse();
		if (lEntityNameUseListInt != null) {
			Iterator<EntityNameUse> lEntityNameUseIntIter = lEntityNameUseListInt.iterator();
			EntityNameUse lEntityNameUseInt = lEntityNameUseIntIter.next();
			org.opencds.vmr.v1_0.schema.EntityNameUse lEntityNameUseExt = eNNameUseInternal2ENNameUse(lEntityNameUseInt);
			lENExt.getUse().add(lEntityNameUseExt);
		}

		return lENExt;
	}

	private static org.opencds.vmr.v1_0.schema.EntityNameUse eNNameUseInternal2ENNameUse(EntityNameUse pENU) 
			throws InvalidDataException {

		String _METHODNAME = "eNNameUseInternal2ENNameUse() :";

		if (pENU == null)
			return null;

		String lEntityNameUseStrInt = pENU.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal EntityNameUse value: " + lEntityNameUseStrInt);
		}
		org.opencds.vmr.v1_0.schema.EntityNameUse lEntityNameUseExt = null;
		try {
			lEntityNameUseExt = org.opencds.vmr.v1_0.schema.EntityNameUse.valueOf(lEntityNameUseStrInt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
			throw new InvalidDataException(errStr);
		}

		return lEntityNameUseExt;
	}

	/**
	 * Convert from internal ENXP to external ENXP object
	 * @param 
	 * 
	 */
	private static org.opencds.vmr.v1_0.schema.ENXP eNXPInternal2ENXP(ENXP pENXP) 
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "eNXPInternal2ENXP(): ";
		if (pENXP == null)
			return null;

		org.opencds.vmr.v1_0.schema.ENXP lENXPExt = new org.opencds.vmr.v1_0.schema.ENXP();

		// set external XP.value 
		lENXPExt.setValue(pENXP.getValue());

		// Now translate the internal EntityNamePartType to external EntityNamePartType
		EntityNamePartType lEntityNamePartTypeInt = pENXP.getType();
		if (lEntityNamePartTypeInt == null) {
			String errStr = _METHODNAME + "EntityPartType of external ENXP datatype not populated; required by vmr spec";
			throw new DataFormatException(errStr);
		}
		String lEntityNamePartTypeStrInt = lEntityNamePartTypeInt.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal EntityNamePartType value: " + lEntityNamePartTypeStrInt);
		}

		org.opencds.vmr.v1_0.schema.EntityNamePartType lEntityNamePartTypeExt = null;
		try {
			lEntityNamePartTypeExt = org.opencds.vmr.v1_0.schema.EntityNamePartType.valueOf(lEntityNamePartTypeStrInt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
			throw new InvalidDataException(errStr);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External EntityNamePartType value: " + lEntityNamePartTypeExt);
		}
		lENXPExt.setType(lEntityNamePartTypeExt);

		// Finally create the list of external EntityNamePartQualifiers (optional in spec)
		List<EntityNamePartQualifier> lPartQualifierListInt = pENXP.getQualifier();
		if (lPartQualifierListInt != null) {
			Iterator<EntityNamePartQualifier> lPartQualifierIterInt = lPartQualifierListInt.iterator();
			while (lPartQualifierIterInt.hasNext()) {
				// Take each internal EntityNamePartQualifier and convert to internal EntityNamePartQualifier for addition to internal EN
				EntityNamePartQualifier lPartQualifierInt = lPartQualifierIterInt.next();
				org.opencds.vmr.v1_0.schema.EntityNamePartQualifier lPartQualifierExt = eNPartQualifierInternal2ENPartQualifier(lPartQualifierInt);
				lENXPExt.getQualifier().add(lPartQualifierExt);
			}
		}		

		return lENXPExt;
	}

	private static org.opencds.vmr.v1_0.schema.EntityNamePartQualifier eNPartQualifierInternal2ENPartQualifier(EntityNamePartQualifier pENPQInt) 
		throws InvalidDataException {

		String _METHODNAME = "eNPartQualifierInternal2ENPartQualifier() :";

		if (pENPQInt == null)
			return null;

		String lPartQualifierStrInt = pENPQInt.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal EntityNamePartQualifier value: " + lPartQualifierStrInt);
		}
		org.opencds.vmr.v1_0.schema.EntityNamePartQualifier lPartQualifierInt = null;
		try {
			lPartQualifierInt = org.opencds.vmr.v1_0.schema.EntityNamePartQualifier.valueOf(lPartQualifierStrInt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
			throw new InvalidDataException(errStr);
		}

		return lPartQualifierInt;
	}
	

	/**
	 * Translate from external EN object to internal EN object
	 * @param pENExt external EN object
	 * @return EN internal EN object
	 */
	public static EN eN2ENInternal(org.opencds.vmr.v1_0.schema.EN pENExt) 
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "eN2ENInternal(): ";

		if (pENExt == null) {
			return null;
		}

		String errStr = null;
		EN lENInt = new EN();

		// Create internal List<ENXP> element - at least one required
		List<org.opencds.vmr.v1_0.schema.ENXP> lExtEntityPart = pENExt.getPart();
		if (lExtEntityPart == null || lExtEntityPart.size() == 0) { // vmr spec says there must be at least one EntityPart
			errStr = _METHODNAME + "List<ENXP element of EN datatype not populated - required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new DataFormatException(errStr);
		}
		Iterator<org.opencds.vmr.v1_0.schema.ENXP> lExtEntityPartIter = lExtEntityPart.iterator();
		int count = 0;
		while (lExtEntityPartIter.hasNext()) {
			org.opencds.vmr.v1_0.schema.ENXP lENXPExt = lExtEntityPartIter.next();
			ENXP lENXPInt = eNXP2ENXPInternal(lENXPExt);
			lENInt.getPart().add(lENXPInt);
			count++;
		}
		if (count < 1) {
			errStr = _METHODNAME + "No ext->int translations of List<ENXP> successful - at least one member of List<ENXP> required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new InvalidDataException(errStr);
		}

		// Now transfer over external to internal List<EntityNameUse> (optional in vmr spec)
		List<org.opencds.vmr.v1_0.schema.EntityNameUse> lEntityNameUseListExt = pENExt.getUse();
		if (lEntityNameUseListExt != null) {
			Iterator<org.opencds.vmr.v1_0.schema.EntityNameUse> lEntityNameUseExtIter = lEntityNameUseListExt.iterator();
			org.opencds.vmr.v1_0.schema.EntityNameUse lEntityNameUseExt = lEntityNameUseExtIter.next();
			EntityNameUse lEntityNameUseInt = eNNameUse2eNNameUseInternal(lEntityNameUseExt);
			lENInt.getUse().add(lEntityNameUseInt);
		}

		return lENInt;
	}


	private static EntityNameUse eNNameUse2eNNameUseInternal(org.opencds.vmr.v1_0.schema.EntityNameUse pENU) 
			throws InvalidDataException {

		String _METHODNAME = "eNNameUse2eNNameUseInternal() :";

		if (pENU == null)
			return null;

		String lEntityNameUseStrExt = pENU.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External EntityNameUse value: " + lEntityNameUseStrExt);
		}
		EntityNameUse lEntityNameUseInt = null;
		try {
			lEntityNameUseInt = EntityNameUse.valueOf(lEntityNameUseStrExt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
			throw new InvalidDataException(errStr);
		}

		return lEntityNameUseInt;
	}

	/**
	 * Convert from external ENXP to internal ENXP object
	 * @param 
	 * 
	 */
	private static ENXP eNXP2ENXPInternal(org.opencds.vmr.v1_0.schema.ENXP pENXP) 
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "eNXP2ENXPInternal(): ";
		if (pENXP == null)
			return null;

		ENXP lENXPInt = new ENXP();

		// set XP.value 
		lENXPInt.setValue(pENXP.getValue());

		// Now translate the external EntityNamePartType to external EntityNamePartType
		org.opencds.vmr.v1_0.schema.EntityNamePartType lEntityNamePartTypeExt = pENXP.getType();
		if (lEntityNamePartTypeExt == null) {
			String errStr = _METHODNAME + "EntityPartType of external ENXP datatype not populated; required by vmr spec";
			throw new DataFormatException(errStr);
		}
		String lEntityNamePartTypeStrExt = lEntityNamePartTypeExt.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External EntityNamePartType value: " + lEntityNamePartTypeStrExt);
		}
		EntityNamePartType lEntityNamePartTypeInt = null;
		try {
			lEntityNamePartTypeInt = EntityNamePartType.valueOf(lEntityNamePartTypeStrExt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
			throw new InvalidDataException(errStr);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal EntityNamePartType value: " + lEntityNamePartTypeInt);
		}
		lENXPInt.setType(lEntityNamePartTypeInt);

		// Finally create the list of internal EntityNamePartQualifiers (optional in spec)
		List<org.opencds.vmr.v1_0.schema.EntityNamePartQualifier> lPartQualifierListExt = pENXP.getQualifier();
		if (lPartQualifierListExt != null) {
			Iterator<org.opencds.vmr.v1_0.schema.EntityNamePartQualifier> lPartQualifierIterExt = lPartQualifierListExt.iterator();
			while (lPartQualifierIterExt.hasNext()) {
				// Take each internal EntityNamePartQualifier and convert to internal EntityNamePartQualifier for addition to internal EN
				org.opencds.vmr.v1_0.schema.EntityNamePartQualifier lPartQualifierExt = lPartQualifierIterExt.next();
				EntityNamePartQualifier lPartQualifierInt = eNPartQualifier2eNPartQualifierInternal(lPartQualifierExt);
				lENXPInt.getQualifier().add(lPartQualifierInt);
			}
		}		

		return lENXPInt;
	}

	private static EntityNamePartQualifier eNPartQualifier2eNPartQualifierInternal(org.opencds.vmr.v1_0.schema.EntityNamePartQualifier pENPQExt) 
			throws InvalidDataException {

		String _METHODNAME = "eNXPPartQualifier2eNXPPartQualifierInternal() :";

		if (pENPQExt == null)
			return null;

		String lPartQualifierStrInt = pENPQExt.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External EntityNamePartQualifier value: " + lPartQualifierStrInt);
		}
		EntityNamePartQualifier lPartQualifierInt = null;
		try {
			lPartQualifierInt = EntityNamePartQualifier.valueOf(lPartQualifierStrInt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
			throw new InvalidDataException(errStr);
		}

		return lPartQualifierInt;
	}

	/**
	 * Translate from external TEL object to internal TEL object
	 * @param pTELExt external TEL object
	 * @return TEL internal TEL object
	 */
	public static TEL tEL2TELInternal(org.opencds.vmr.v1_0.schema.TEL pTELExt) 
			throws DataFormatException, InvalidDataException {

		// String _METHODNAME = "tEL2TELInternal(): ";

		if (pTELExt == null) {
			return null;
		}

		TEL lTELInt = new TEL();
		
//		lTELInt.setUseablePeriod(sT2STInternal(pTELExt.getUseablePeriod()));
		if (pTELExt.getUseablePeriodOriginalText() != null) lTELInt.setUseablePeriodOriginalText(pTELExt.getUseablePeriodOriginalText());
		if (pTELExt.getValue() != null) lTELInt.setValue(pTELExt.getValue());

		// Create internal List<TelecommunicationAddressUse> optional element
		List<org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse> lTelAddrListExt = pTELExt.getUse();
		if (lTelAddrListExt != null) {
			Iterator<org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse> lTelAddrIterExt = lTelAddrListExt.iterator();
			while (lTelAddrIterExt.hasNext()) {
				org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse lTelAddrExt = lTelAddrIterExt.next();
				TelecommunicationAddressUse lTelAddrInt = tELTelecomAddrUse2TELTelecomAddrUseInternal(lTelAddrExt);
				lTELInt.getUse().add(lTelAddrInt);
			}
		}

		// Create internal List<TelecommunicationCapability> optional element
		List<org.opencds.vmr.v1_0.schema.TelecommunicationCapability> lTelecomCapaListExt = pTELExt.getCapabilities();
		if (lTelecomCapaListExt != null) {
			Iterator<org.opencds.vmr.v1_0.schema.TelecommunicationCapability> lTelecomCapaIterExt = lTelecomCapaListExt.iterator();
			while(lTelecomCapaIterExt.hasNext()) {
				org.opencds.vmr.v1_0.schema.TelecommunicationCapability lTelecomCapExt = lTelecomCapaIterExt.next();
				TelecommunicationCapability lTelecomCapaInt = tELTelecomCapa2TELTelecomCapaInternal(lTelecomCapExt);
				lTELInt.getCapabilities().add(lTelecomCapaInt);
			}
		}

		return lTELInt;
	}
	
	/**
	 * Translate from internal TEL object to external TEL object
	 * @param pTELInt internal TEL object
	 * @return TEL external TEL object
	 */
	public static org.opencds.vmr.v1_0.schema.TEL tELInternal2TEL(TEL pTELInt) 
			throws DataFormatException, InvalidDataException {

		// String _METHODNAME = "tELInternal2TEL(): ";

		if (pTELInt == null) {
			return null;
		}

		org.opencds.vmr.v1_0.schema.TEL lTELExt = new org.opencds.vmr.v1_0.schema.TEL();
		
//		lTELExt.setUseablePeriod(sTInternal2ST(pTELInt.getUseablePeriod()));
		lTELExt.setUseablePeriodOriginalText(pTELInt.getUseablePeriodOriginalText());
		if (pTELInt.getValue() != null) lTELExt.setValue(pTELInt.getValue());

		// Create external List<TelecommunicationAddressUse> optional element
		List<TelecommunicationAddressUse> lTelAddrListInt = pTELInt.getUse();
		if (lTelAddrListInt != null) {
			Iterator<TelecommunicationAddressUse> lTelAddrIterInt = lTelAddrListInt.iterator();
			while (lTelAddrIterInt.hasNext()) {
				TelecommunicationAddressUse lTelAddrInt = lTelAddrIterInt.next();
				org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse lTelAddrExt = tELTelecomAddrUseInternal2TELTelecomAddrUse(lTelAddrInt);
				lTELExt.getUse().add(lTelAddrExt);
			}
		}

		// Create external List<TelecommunicationCapability> optional element
		List<TelecommunicationCapability> lTelecomCapaListInt = pTELInt.getCapabilities();
		if (lTelecomCapaListInt != null) {
			Iterator<TelecommunicationCapability> lTelecomCapaIterInt = lTelecomCapaListInt.iterator();
			while (lTelecomCapaIterInt.hasNext()) {
				TelecommunicationCapability lTelecomCapInt = lTelecomCapaIterInt.next();
				org.opencds.vmr.v1_0.schema.TelecommunicationCapability lTelecomCapaExt = tELTelecomCapaInternal2TelecomCapa(lTelecomCapInt);
				lTELExt.getCapabilities().add(lTelecomCapaExt);
			}
		}

		return lTELExt;
	}
	
	/**
	 * @param pTC
	 * @return
	 * @throws InvalidDataException
	 */
	private static TelecommunicationAddressUse tELTelecomAddrUse2TELTelecomAddrUseInternal(org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse pTC) 
		throws InvalidDataException {
		
		String _METHODNAME = "tELTelecomAddrUse2TELTelecomAddrUseInternal() :";

		if (pTC == null)
			return null;

		String lTCStrExt = pTC.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External TelecommunicationAddressUse value: " + lTCStrExt);
		}
		TelecommunicationAddressUse lTCInt = null;
		try {
			lTCInt = TelecommunicationAddressUse.valueOf(lTCStrExt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
			throw new InvalidDataException(errStr);
		}

		return lTCInt;
	}
	
	/**
	 * Convert from the internal TelecommunicationAddressUse to external TelecommunicationAddressUse object 
	 * @param pTC external TelecommunicationAddressUse object
	 * @throws RuntimeException if no direct value mapping from internal to external TelecommunicationAddressUse enumerations exists
	 * @return
	 */
	private static org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse tELTelecomAddrUseInternal2TELTelecomAddrUse(TelecommunicationAddressUse pTC) 
			throws InvalidDataException {

		String _METHODNAME = "tELTelecomAddrUseInternal2TELTelecomAddrUse(): ";

		if (pTC == null)
			return null;

		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal TelecommunicationAddressUse value: " + pTC);
		}
		org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse lTelecomCapaExt = null;
		try {
			lTelecomCapaExt = org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse.valueOf(pTC.toString());
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
			throw new InvalidDataException(errStr);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External TelecommunicationAddressUse value: " + lTelecomCapaExt.value());
		}

		return lTelecomCapaExt;
	}

	/**
	 * @param pTC
	 * @return
	 * @throws InvalidDataException
	 */
	private static TelecommunicationCapability tELTelecomCapa2TELTelecomCapaInternal(org.opencds.vmr.v1_0.schema.TelecommunicationCapability pTC) 
			throws InvalidDataException {
			
			String _METHODNAME = "tELTelecomCapa2TELTelecomCapaInternal() :";

			if (pTC == null)
				return null;

			String lTCStrExt = pTC.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "External TelecommunicationCapability value: " + lTCStrExt);
			}
			TelecommunicationCapability lTCInt = null;
			try {
				lTCInt = TelecommunicationCapability.valueOf(lTCStrExt);
			}
			catch (IllegalArgumentException iae) {
				String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
				throw new InvalidDataException(errStr);
			}

			return lTCInt;
		}
		
		/**
		 * Convert from the internal TelecommunicationCapability to external TelecommunicationCapability object 
		 * @param pTC external TelecommunicationCapability object
		 * @throws RuntimeException if no direct value mapping from internal to external TelecommunicationCapability enumerations exists
		 * @return
		 */
		private static org.opencds.vmr.v1_0.schema.TelecommunicationCapability tELTelecomCapaInternal2TelecomCapa(TelecommunicationCapability pTC) 
				throws InvalidDataException {

			String _METHODNAME = "tELTelecomInternal2TELTelecomCapa(): ";

			if (pTC == null)
				return null;

			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "Internal TelecommunicationCapability value: " + pTC);
			}
			org.opencds.vmr.v1_0.schema.TelecommunicationCapability lTelecomCapaExt = null;
			try {
				lTelecomCapaExt = org.opencds.vmr.v1_0.schema.TelecommunicationCapability.valueOf(pTC.toString());
			}
			catch (IllegalArgumentException iae) {
				String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
				throw new InvalidDataException(errStr);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "External TelecommunicationCapability value: " + lTelecomCapaExt.value());
			}

			return lTelecomCapaExt;
		}

	
	/**
	 * Translate from external AD object to internal AD object
	 * @param pADExt external AD object
	 * @return AD internal AD object
	 */
	public static AD aD2ADInternal(org.opencds.vmr.v1_0.schema.AD pADExt) 
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "aD2ADInternal(): ";

		if (pADExt == null) {
			return null;
		}

		String errStr = null;
		AD lADInt = new AD();

		// Create internal AD List<ADXP> element of AD object - at least one required
		List<org.opencds.vmr.v1_0.schema.ADXP> lExtAddressPart = pADExt.getPart();
		if (lExtAddressPart == null || lExtAddressPart.size() == 0)	{	// vmr spec says there must be at least one AddressPart
			errStr = _METHODNAME + "List<ADXP> element of external AD datatype not populated - required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new DataFormatException(errStr);
		}
		Iterator<org.opencds.vmr.v1_0.schema.ADXP> lExtAddressPartIter = lExtAddressPart.iterator();
		int count = 0;
		while (lExtAddressPartIter.hasNext()) {
			org.opencds.vmr.v1_0.schema.ADXP lADXPExt = lExtAddressPartIter.next();
			ADXP lADXPInt = aDXP2ADXPInternal(lADXPExt);
			lADInt.getPart().add(lADXPInt);
			count++;
		}
		if (count < 1) {
			errStr = _METHODNAME + "No ext->int translations of List<ADXP> successful - at least one member of List<ADXP> required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new DataFormatException(errStr);
		}

		// Create internal AD List<PostalAddressUse> of AD object (optional according to vmr spec)
		List<org.opencds.vmr.v1_0.schema.PostalAddressUse> lExtAddressUseList = pADExt.getUse();
		if (lExtAddressUseList != null) {
			Iterator<org.opencds.vmr.v1_0.schema.PostalAddressUse> lExtAddressUseIter = lExtAddressUseList.iterator();
			while (lExtAddressUseIter.hasNext()) {
				// Take each internal PostAddressUse and convert to schema PostalAddressUse for addition to schema AD
				org.opencds.vmr.v1_0.schema.PostalAddressUse lExtAddressUse = lExtAddressUseIter.next();
				PostalAddressUse lIntAddressUse = aDPostalAddressUse2ADPostalAddressUseInternal(lExtAddressUse);
				lADInt.getUse().add(lIntAddressUse);
			}
		}

		return lADInt;
	}


	/**
	 * Translate from internal AD object to external AD object
	 * @param pADInt internal AD object
	 * @return org.opencds.vmr.v1_0.schema.AD
	 */
	public static org.opencds.vmr.v1_0.schema.AD aDInternal2AD(AD pADInt)
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "aDInternal2AD(): ";

		if (pADInt == null)
			return null;

		String errStr = null;
		org.opencds.vmr.v1_0.schema.AD lADExt = new org.opencds.vmr.v1_0.schema.AD();

		// Create external AD List<org.opencds.vmr.v1_0.schema.ADXP> element of AD object - at least one required
		List<ADXP> lIntAddressPart = pADInt.getPart();
		if (lIntAddressPart == null || lIntAddressPart.size() == 0)	{	// vmr spec says there must be at least one AddressPart
			errStr = _METHODNAME + "List<ADXP> element of internal AD datatype not populated - required by vmr spec";
			if (logger.isDebugEnabled()) {
				logger.debug(errStr);
			}
			throw new DataFormatException(errStr);
		}
		Iterator<ADXP> lIntAddressPartIter = lIntAddressPart.iterator();
		int count = 0;
		while (lIntAddressPartIter.hasNext()) {
			ADXP lADXPInt = lIntAddressPartIter.next();
			org.opencds.vmr.v1_0.schema.ADXP lADXPExt = aDXPInternal2ADXP(lADXPInt);
			lADExt.getPart().add(lADXPExt);
			count++;
		}
		if (count < 1) {
			errStr = _METHODNAME + "No int->ext translations of List<ADXP> successful - at least one member of List<ADXP> required by vmr spec";
			throw new InvalidDataException(errStr);
		}

		// Create external AD List<org.opencds.vmr.v1_0.schema.PostalAddressUse> of AD object (optional according to vmr spec)
		List<PostalAddressUse> lIntAddressUseList = pADInt.getUse();
		if (lIntAddressUseList != null) {
			Iterator<PostalAddressUse> lIntAddressUseIter = lIntAddressUseList.iterator();
			while (lIntAddressUseIter.hasNext()) {
				// Take each internal PostAddressUse and convert to schema PostalAddressUse for addition to schema AD
				PostalAddressUse lIntAddressUse = lIntAddressUseIter.next();
				org.opencds.vmr.v1_0.schema.PostalAddressUse lExtAddressUse = aDPostalAddressUseInternal2ADPostalAddressUse(lIntAddressUse);
				lADExt.getUse().add(lExtAddressUse);
			}
		}
		return lADExt;
	}


	/**
	 * Convert from external ADXP to internal ADXP object
	 * @param 
	 * 
	 */
	private static ADXP aDXP2ADXPInternal(org.opencds.vmr.v1_0.schema.ADXP pADXP) 
			throws DataFormatException, InvalidDataException {

		String _METHODNAME = "aDXP2ADXPInternal(): ";
		if (pADXP == null)
			return null;

		ADXP lADXPInt = new ADXP();

		// Populate the associated name part
		lADXPInt.setValue(pADXP.getValue());

		// Now translate the internal AddressPartType to external AddressPartType
		org.opencds.vmr.v1_0.schema.AddressPartType lAddressPartTypeExt = pADXP.getType();
		if (lAddressPartTypeExt == null) {
			String errStr = _METHODNAME + "AddressPartType of external ADXP datatype not populated; required by vmr spec";
			throw new DataFormatException(errStr);
		}
		String lAddrPartTypeStrExt = pADXP.getType().toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External AddressPartType value: " + lAddrPartTypeStrExt);
		}
		AddressPartType lAddrPartTypeInt = null;
		try {
			lAddrPartTypeInt = AddressPartType.valueOf(lAddrPartTypeStrExt);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
			throw new InvalidDataException(errStr);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal AddressPartType value: " + lAddrPartTypeInt);
		}

		lADXPInt.setType(lAddrPartTypeInt);

		return lADXPInt;
	}


	/**
	 * Convert from internal ADXP to external ADXP object
	 * @param pADXPInternal
	 * @return
	 */
	private static org.opencds.vmr.v1_0.schema.ADXP aDXPInternal2ADXP(ADXP pADXPInternal) {

		String _METHODNAME = "aDXPInternal2ADXP(): ";
		if (pADXPInternal == null)
			return null;

		org.opencds.vmr.v1_0.schema.ADXP lADXPExt = new org.opencds.vmr.v1_0.schema.ADXP();

		// Populate the associated name part
		lADXPExt.setValue(pADXPInternal.getValue());

		// Now translate the internal AddressPartType to external AddressPartType
		String lIntAddrPartTypeStr = pADXPInternal.getType().toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal AddressPartType value: " + lIntAddrPartTypeStr);
		}
		org.opencds.vmr.v1_0.schema.AddressPartType lAddrPartTypeExt = null;
		try {
			lAddrPartTypeExt = org.opencds.vmr.v1_0.schema.AddressPartType.valueOf(lIntAddrPartTypeStr);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
			throw new RuntimeException(errStr);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External AddressPartType value: " + lAddrPartTypeExt);
		}

		lADXPExt.setType(lAddrPartTypeExt);

		return lADXPExt;
	}


	/**
	 * Convert from the internal PostalAddressUse to external PostalAddressUse object 
	 * @param pPAUInternal external PostalAddressUse object
	 * @throws RuntimeException if no direct value mapping from internal to external PostalAddressUse enumerations exists
	 * @return
	 */
	private static org.opencds.vmr.v1_0.schema.PostalAddressUse aDPostalAddressUseInternal2ADPostalAddressUse(PostalAddressUse pPAUInternal) 
			throws InvalidDataException {

		String _METHODNAME = "postalAddressUseInternal2PostalAddressUse(): ";

		if (pPAUInternal == null)
			return null;

		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Internal PostalAddressUse value: " + pPAUInternal);
		}
		org.opencds.vmr.v1_0.schema.PostalAddressUse lPostalAddressExt = null;
		try {
			lPostalAddressExt = org.opencds.vmr.v1_0.schema.PostalAddressUse.valueOf(pPAUInternal.toString());
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the internal to external enumeration";
			throw new InvalidDataException(errStr);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External PostAddressUse value: " + lPostalAddressExt.value());
		}

		return lPostalAddressExt;
	}

	private static PostalAddressUse aDPostalAddressUse2ADPostalAddressUseInternal(org.opencds.vmr.v1_0.schema.PostalAddressUse pPAUExt) 
			throws InvalidDataException {

		String _METHODNAME = "postalAddressUse2PostalAddressUseInternal() :";

		if (pPAUExt == null)
			return null;

		String lExtAddressUseStr = pPAUExt.toString();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "External PostalAddressUse value: " + pPAUExt);
		}
		PostalAddressUse lPostalAddressInt = null;
		try {
			lPostalAddressInt = PostalAddressUse.valueOf(lExtAddressUseStr);
		}
		catch (IllegalArgumentException iae) {
			String errStr = _METHODNAME + "there was no direct value mapping from the external to internal enumeration";
			throw new InvalidDataException(errStr);
		}

		return lPostalAddressInt;
	}

	public static II getUUIDAsII() {
		//return new com.eaio.uuid.UUID().toString(); // using default Java UUID generator for now
		// TODO: consider switching to above UUID generator for performance
		II ii = new II();
		ii.setValue( MiscUtility.getIDAsString() );
		return ii;
	}
}
