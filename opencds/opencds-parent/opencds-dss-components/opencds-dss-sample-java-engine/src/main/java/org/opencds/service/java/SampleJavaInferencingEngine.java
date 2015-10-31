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

package org.opencds.service.java;
 
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.opencds.common.structures.TimingData;
import org.opencds.common.structures.TimingDataKM;
import org.opencds.knowledgeRepository.SimpleKnowledgeRepository;
import org.opencds.service.evaluate.PayloadInboundProcessorFactory;
import org.opencds.vmr.v1_0.mappings.in.structures.DSSRequestDataItem;
import org.opencds.vmr.v1_0.mappings.in.structures.DSSRequestKMItem;
import org.opencds.vmr.v1_0.mappings.interfaces.IPayloadInboundProcessor;
import org.opencds.vmr.v1_0.mappings.out.MarshalVMR2VMRSchemaPayload;


/**
 * SampleJavaInferencingEngine.java
 * 
 * <p>Adapter to use Drools to process the evaluate operation of the DSS web service:
 * This class is designed to use data input in standard Java classes, to facilitate its use from various settings.
 * Mapping of the input data to the internal Java classes is done by input mappers and output mappers, with a set 
 * each for every external data format to be processed. 
 * 
 * 
 * Simply stated, input messages contain a list of rules (Knowledge Modules, or KMs) to run, and structured data
 * to run against those KMs.  The submitted data can be in any structure for which there is a mapper.  Currently,
 * OpenCDS supports the HL7 balloted VMR structure.
 * 
 * Additional structures for the submitted data may be developed, possibly including the CDA / CCD schema structure
 * <p/>
 * <p>Copyright 2011 OpenCDS.org</p>
 * <p>Company: OpenCDS</p>
 *
 * @author David Shields
 * @version 2.1 for Drools 5.3
 * @date 11-09-2011
 * 
 */
public class SampleJavaInferencingEngine //extends InferenceEngineAdapter
{		
    private static 	SampleJavaInferencingEngine 	instance;  //singleton instance
	private static 	Log 							log 	= LogFactory.getLog(SampleJavaInferencingEngine.class);
	
//	private 			HashMap<String,List<CD>>	myKMIdToReportableConditionMap = new HashMap<String,List<CD>>();
//	// key = String for KMId
//	// target = reportableCondition CD
//	
//	private 			HashMap<String,List<CD>>	myReportableConditionToLabTestClassificationMap = new HashMap<String,List<CD>>();
//	// key = String for Reportable Condition Code
//	// target = labDetectionCriterion: labTestTypeClassification CD
//	
//	private 			HashMap<String,List<CD>>	myLabTestClassificationToInferenceEngineAdapterMap = new HashMap<String,List<CD>>();
//	// key = String for Reportable Condition Code
//	// target = labDetectionCriterion: labTestTypeClassification CD
	

        
    public SampleJavaInferencingEngine()
    {
        initialize();
    }

	protected void initialize()
    {
        log.info(" beginning OpenCDS Reportable Disease DSSEvaluateAdapter");
    }
	
    // public functions
    public static synchronized SampleJavaInferencingEngine getInstance()
    {
        if (instance == null)
        {
            instance = new SampleJavaInferencingEngine();
        }
        return instance;
    }
    

	/** 
	 * big picture pseudo code for following method:
	 * 
	 * 			getResponse:
	 * 				load KM 
     *  			load data from inputPayloadString 
     *  			execute KM 
     *  			unload results to outputString
     */	
	public String getResponse(	DSSRequestKMItem dSSRequestKMItem
	) throws
		InvalidDriDataFormatExceptionFault, 
		RequiredDataNotProvidedExceptionFault, 
		EvaluationExceptionFault, 
		InvalidTimeZoneOffsetExceptionFault, 
		UnrecognizedScopedEntityExceptionFault, 
		UnrecognizedLanguageExceptionFault, 
		UnsupportedLanguageExceptionFault,
		DSSRuntimeExceptionFault
	{
		
		String 	requestedKmId			= dSSRequestKMItem.getRequestedKmId();
		
		DSSRequestDataItem dSSRequestDataItem = dSSRequestKMItem.getDssRequestDataItem();
		
		String 	externalFactModelSSId	= dSSRequestDataItem.getExternalFactModelSSId();
		String 	inputPayloadString		= dSSRequestDataItem.getInputPayloadString();
		Date 	evalTime				= dSSRequestDataItem.getEvalTime();
		String 	clientLanguage			= dSSRequestDataItem.getClientLanguage();
		String 	clientTimeZoneOffset 	= dSSRequestDataItem.getClientTimeZoneOffset();
		String	interactionId			= dSSRequestDataItem.getInteractionId();
		
		log.info("begin eval Id: " + interactionId 
				+ ", KmId: "+ requestedKmId 
				+ ", SSId: " + externalFactModelSSId 
				+ ", evalTime: " + evalTime + ", clTimeZone: " + clientTimeZoneOffset 
				+ ", clLang: " + clientLanguage);
		
		//setup list for facts
		HashMap<String, List<?>>		allFactLists			= new HashMap<String, List<?>>();
		org.opencds.vmr.v1_0.internal.FocalPersonId	focalPersonId = new org.opencds.vmr.v1_0.internal.FocalPersonId();
										
		
/** 
 * Load fact map from specific externalFactModels, as specified in externalFactModel SSId...
 * 
 * Every separately identified SSId, by definition, specifies separate input and output mappings.
 * Input mappings are used here, and then output mappings are used following the session.execute.
 */
		String 			requiredSSID 			= SimpleKnowledgeRepository.getRequiredSSIdForKMId(requestedKmId);
		if (( requiredSSID == null ) || ( "".equals(requiredSSID) )) {
			throw new InvalidDriDataFormatExceptionFault( "OpenCDS does not recognize SSId='" 
					+ externalFactModelSSId + "', and cannot continue" );
		}

		String 			requiredUnmarshaller 	= SimpleKnowledgeRepository.getRequiredOpenCDSUnmarshallerClassNameForSSID(requiredSSID);
		if (( requiredUnmarshaller == null ) || ( "".equals(requiredUnmarshaller) )) {
			throw new InvalidDriDataFormatExceptionFault( "OpenCDS does not recognize unmarshaller class '" 
					+ requiredUnmarshaller + "' for SSId='" 
					+ externalFactModelSSId + "', and cannot continue" );
		}
		
		TimingData timingData = dSSRequestDataItem.getTimingData();
		timingData.setStartUnmarshalTime(new AtomicLong(System.currentTimeMillis()));		
		IPayloadInboundProcessor payloadInboundProcessor = 
			PayloadInboundProcessorFactory.getPayloadInboundProcessor(externalFactModelSSId);
		TimingDataKM timingDataKM = dSSRequestKMItem.getKmTimingData();
//		org.opencds.vmr.v1_0.schema.CDSInput.FocalPerson.id focalPersonIdExternal = new FocalPersonId();
//		org.opencds.vmr.v1_0.schema.CDSInput.FocalPerson.setId( ((IPayloadInboundProcessor) payloadInboundProcessor).mappingInbound( 
//				inputPayloadString, evalTime, timingData ) );
//		dSSRequestKMItem.setTimingData(timingData);
		log.debug("unmarshalling completed for: " + requestedKmId + ", focalPersonId: " + focalPersonId);

		dSSRequestKMItem.getKmTimingData().setStartInferenceEngineAdapterTime(new AtomicLong(System.currentTimeMillis()));		
		
/** 
 * Get the KnowledgeModule 
 * 
 */
		
//		File	xmlFile		= null;
//
//		xmlFile		= SimpleKnowledgeRepository.getInstance().getResourceAsFileWithoutException("knowledgeModules", requestedKmId + ".xml");
		InputStream criteria = SimpleKnowledgeRepository.getResourceAsStream("knowledgeModules", requestedKmId + ".xml");

		if (criteria  == null) {
			throw new DSSRuntimeExceptionFault( "Reportable Disease Inferencing Engine did not find any VALID '.xml' file for: '" 
					+ requestedKmId + "'." );						
		} else {
			//FIXME unmarshall the XML into HashMaps 
		}
		
		timingDataKM.setStartInferenceEngine(new AtomicLong(System.currentTimeMillis()));
/*	
 * Process the data			
 ********************************************************************************
 */				
		
//FIXME		loop through all the data, and apply the KnowledgeModule criteria
		
/*					
 ********************************************************************************
 */				
		dSSRequestKMItem.getKmTimingData().setFinishInferenceEngine(new AtomicLong(System.currentTimeMillis()));
        
		/** 
		 * Retrieve the Results for this requested KM and stack them in the DSS fkmResponse
		 * Each additional requested KM will have a separate output payload
		 */
		log.debug("stacking results for " + requestedKmId );
        dSSRequestKMItem.getKmTimingData().setFinishInferenceEngineAdapterTime(new AtomicLong(System.currentTimeMillis()));
		
    	MarshalVMR2VMRSchemaPayload marshaller		= MarshalVMR2VMRSchemaPayload.getInstance();
        String						outputString	= marshaller.mappingOutbound( 
        															allFactLists,
//        															evalTime,
//        															focalPersonId,
//        															clientLanguage,
//        															clientTimeZoneOffset,
        															dSSRequestKMItem);
        log.debug("completed Drools dss engine for: " + requestedKmId);
        return outputString;
	}
	
	public static void main(String [ ] args) 
	{
//		String 				payloadString 	= null;
//		String				filePath		= null;
//		filePath = "C:/OpenCDS/opencds-knowledge-repository-data/src/main/resources/samples/ConstructedExampleNestedLabsForBeaconProjectForTesting.xml";
//		try {
//			payloadString = FileUtility.getInstance().getFileContentsAsString(new File (filePath));
//		} catch (DSSRuntimeExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//
//		DSSRequestKMItem	dSSRequestKMItem = new DSSRequestKMItem(); 
//		
////		decodedDSSRequest.setRequestedKmId("org.opencds^bounce^1.5.3");
//		dSSRequestKMItem.setRequestedKmId("org.opencds^Reportable_Disease^2.0.0");
//		dSSRequestKMItem.setExternalFactModelSSId("org.opencds.vmr^VMR^1.0");
//		dSSRequestKMItem.setInputPayloadString(payloadString);
//		dSSRequestKMItem.setEvalTime(new Date());
//		dSSRequestKMItem.setClientLanguage("");
//		dSSRequestKMItem.setClientTimeZoneOffset("");
//		TimingData timingData = new TimingData();
//		dSSRequestKMItem.setTimingData(timingData);
//		timingData.setStartInferenceEngineAdapterTime(System.currentTimeMillis());
//		
//		try {
//			OpenCDSMain.newInstance();  //initializes KR
//			String structuredDroolsResult = SampleJavaInferencingEngine.getInstance().getResponse( 
//					dSSRequestKMItem);
//			
//			System.out.println(structuredDroolsResult);
//			System.out.println("finished");
//			
//		} catch (InvalidDriDataFormatExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (UnrecognizedLanguageExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (RequiredDataNotProvidedExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (UnsupportedLanguageExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (UnrecognizedScopedEntityExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (EvaluationExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (InvalidTimeZoneOffsetExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (DSSRuntimeExceptionFault e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}		
	}

//	/* (non-Javadoc)
//	 * @see org.opencds.service.evaluate.InferenceEngineAdapter#getOneResponse(org.opencds.vmr.v1_0.internal.FocalPersonId, org.opencds.common.structures.DSSRequestKMItem, java.util.Map, org.opencds.common.structures.TimingData)
//	 */
//	@Override
//	public String getOneResponse(FocalPersonId focalPersonId,
//			DSSRequestKMItem dSSRequestKMItem, Map<String, List<?>> allFactLists,
//			TimingData timingData) throws InvalidDriDataFormatExceptionFault,
//			UnrecognizedLanguageExceptionFault,
//			RequiredDataNotProvidedExceptionFault,
//			UnsupportedLanguageExceptionFault,
//			UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault,
//			InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
			
		


