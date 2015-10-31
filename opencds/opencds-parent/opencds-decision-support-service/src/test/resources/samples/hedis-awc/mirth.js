//COMMENT: Transforming String from Channel 2 into XML VMR;
var responseString = channelMap.get('vMRFromChanel2').toString();
var response = new XML(responseString);
channelMap.put('response', response);
// logger.info("Channel 3: " + response.toString());

// COMMENT: Parsing XML to fill up variables values to record them to the DW
// later
var patientId = "";
var kmId = "";
var lastUpdatedTime = "";
var denominator = "";
var numerator = "";
var assertions = "";
var concepts = "[";

var patientId = response['vmrOutput']['patient']['id']['@extension'].toString();

for (var i = 0; i < response['vmrOutput']['patient']['clinicalStatements']['observationResults']['observationResult']
		.length(); i++) {
	var thisObservationResult = response['vmrOutput']['patient']['clinicalStatements']['observationResults']['observationResult'][i];
	// logger.info("thisObservationResult=" + thisObservationResult);

	if ("assertions" == thisObservationResult['observationFocus']['@code']
			.toString()) {
		assertions = assertions
				+ "["
				+ thisObservationResult['observationValue']['text']['@value']
						.toString() + "]";
		// logger.info("assertions: " + assertions);

	} else if ("concepts" == thisObservationResult['observationFocus']['@code']
			.toString()) {
		concepts = concepts
				+ thisObservationResult['observationValue']['text']['@value']
						.toString();
		// logger.info("concepts: " + concepts);

	} else if (("ROOT" == thisObservationResult['id']['@extension'].toString())
			&& ("2.16.840.1.113883.3.795.5.1" == thisObservationResult['id']['@root']
					.toString())) {
		kmId = thisObservationResult['observationFocus']['@code'].toString();
		lastUpdatedTime = thisObservationResult['observationEventTime']['@high']
				.toString();
		// logger.info("kmID: " + kmId);
		for (var j = 0; j < thisObservationResult['relatedClinicalStatement']
				.length(); j++) {
			var relatedClinicalStatement = thisObservationResult['relatedClinicalStatement'][j];
			if ("denominator" == relatedClinicalStatement['observationResult']['id']['@extension']
					.toString()) {
				denominator = relatedClinicalStatement['observationResult']['observationValue']['integer']['@value']
						.toString();
				// logger.info("denominator: " + denominator);
			} else if ("numerator" == relatedClinicalStatement['observationResult']['id']['@extension']
					.toString()) {
				numerator = relatedClinicalStatement['observationResult']['observationValue']['integer']['@value']
						.toString();
				// logger.info("numerator: " + numerator);
			}
		}

	}
}
concepts = concepts + ']';

// COMMENT: Setting up Batch ID to be able to distinguish between different runs
var batchId = globalMap.get('HEDIS_AWC_BATCH_ID');
if ((batchId == null) || (batchId == "")) {
	batchId = DateUtil.getCurrentDate('yyyyMMddHHmm');
	logger.info("batchId created: " + batchId);
	globalMap.put('HEDIS_AWC_BATCH_ID', batchId);
}

channelMap.put('patientId', patientId);
channelMap.put('kmId', kmId);
channelMap.put('lastUpdatedTime', lastUpdatedTime.substring(0, 14));
channelMap.put('numerator', numerator);
channelMap.put('denominator', denominator);
channelMap.put('assertions', assertions);
channelMap.put('concepts', concepts);
channelMap.put('batchId', batchId);