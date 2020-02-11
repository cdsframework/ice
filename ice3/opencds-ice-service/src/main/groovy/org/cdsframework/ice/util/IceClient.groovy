package org.cdsframework.ice.util

import groovy.util.logging.Log4j
import groovy.xml.MarkupBuilder

@Log4j
class IceClient {

	private static final String WS_URL = "http://localhost:28080/opencds-decision-support-service/evaluate"
	private static final String DSS_NS = "http://www.omg.org/spec/CDSS/201105/dss"
	private static final String EVALUATE = 'evaluate'
	private static final String EVALUATE_AT_SPEC_TIME = 'evaluateAtSpecifiedTime'

	public static String sendEvaluateMessage(Map params, String payload) {
		return getResponsePayload(IceWsUtil.sendToEndpoint(WS_URL, evaluate(EVALUATE, params, payload)))
	}

	public static String sendEvaluateAtSpecifiedTimeMessage(String payload) {

		def params = [
			kmEvaluationRequest:[scopingEntityId: 'org.nyc.cir', businessId: 'ICE', version: '1.0.0'],
			specifiedTime: '2019-12-15'
		]
		def params1_0_0 = [
			kmEvaluationRequest:[scopingEntityId: 'org.nyc.cir', businessId: 'ICE', version: '1.0.0'],
			specifiedTime: '2015-01-24'
		]
		def params1_1_0 = [
			kmEvaluationRequest:[scopingEntityId: 'org.nyc.cir', businessId: 'ICE', version: '1.1.0'],
			specifiedTime: '2015-01-24'
		]
		def paramsDenver = [
			kmEvaluationRequest:[scopingEntityId: 'gov.denver.dph', businessId: 'ICE', version: '1.0.1'],
			specifiedTime: '2015-01-24'
		]
		def paramsBounce = [
			kmEvaluationRequest:[scopingEntityId: 'org.opencds', businessId: 'bounce', version: '1.5.5'],
			specifiedTime: '2015-01-24'
		]
		return getResponsePayload(IceWsUtil.sendToEndpoint(WS_URL, evaluate(EVALUATE_AT_SPEC_TIME, params, payload)))
	}

	
	private static String evaluate(String method, Map params, String payload) {
		def sw = new StringWriter()
		def xml = new MarkupBuilder(sw)
		xml."dss:$method"('xmlns:dss': DSS_NS) {
			interactionId(scopingEntityId: "edu.utah", interactionId: "123456", submissionTime: new Date().format("yyyy-MM-dd HH:mm:ss.S").replaceFirst(" ", "T"))
			if (method == EVALUATE_AT_SPEC_TIME) {
				specifiedTime(params.specifiedTime)
			}
			evaluationRequest(clientLanguage: "", clientTimeZoneOffset: "") {
				kmEvaluationRequest {
					kmId(params.kmEvaluationRequest) //scopingEntityId: params.kmERequest.scopingEntityId, businessId: params.kmERequest.businessId, version: params.kmERequest.version)
				}
				dataRequirementItemData {
					driId(itemId: "testPayload") {
						containingEntityId(params.containingEntityId)
					}
					data {
						informationModelSSId(scopingEntityId: "org.opencds.vmr", businessId: "VMR", version: "1.0")
						base64EncodedPayload(encodeRequest(payload))
					}
				}
			}
		}
		if (params.debug) {
			log.debug "REQUEST: "
			log.debug sw.toString()
		}
		sw.toString()
	}

	private static String encodeRequest(String request) {
		request.bytes.encodeBase64().toString()
	}

	private static String getResponsePayload(String result) {
		new String(new XmlSlurper().parseText(result).evaluationResponse.finalKMEvaluationResponse.kmEvaluationResultData.data.base64EncodedPayload.text().decodeBase64())
	}

}

