// [keyword][]AND=&&
// [keyword][]OR=||


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Patient
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// [condition][]The [Ii]mmunization [Ss]chedule information {assign_oSchedule} must be known to complete writing this rule={assign_oSchedule} : Schedule()

[condition][]The [Pp]atient [Ii]nformation {assign_oEvaluatedPerson} must be known to complete writing this rule={assign_oEvaluatedPerson} : EvaluatedPerson()
[condition][]- [Tt]he [Pp]atient's birthdate is {aOp}  {dtDate}=demographics.birthTime {aOp} {dtDate}
[condition][]- [Tt]he [Pp]atient is [Ff]emale=demographics.gender.code != null, demographics.gender.equals(schedule.getSupportedCdsLists().getCdsListItem(BaseDataPerson._GENDER_FEMALE.cdsListItemName).getCdsListItemCD())
[condition][]- [Tt]he [Pp]atient is [Mm]ale=demographics.gender.code != null, demographics.gender.equals(schedule.getSupportedCdsLists().getCdsListItem(BaseDataPerson._GENDER_MALE.cdsListItemName).getCdsListItemCD())
[condition][]- [Mm]ake [Nn]ote of the [Pp]atient's birthdate as {assign_dtBirthDate}={assign_dtBirthDate} : demographics.birthTime
[condition][]- [Mm]ake [Nn]ote of the [Dd]ate as {assign_dtDateAtAge} when the [Pp]atient is {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})} of [Aa]ge={assign_dtDateAtAge} : TimePeriod.addTimePeriod(demographics.birthTime, {sDuration})
[condition][]- [Mm]ake [Nn]ote of the [Dd]ate as {assign_dtDateAtAge} when the [Pp]atient is {refer_oTimePeriod} of [Aa]ge={assign_dtDateAtAge} : TimePeriod.addTimePeriod(demographics.birthTime, {refer_oTimePeriod})

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Disease Immunity 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[condition][]The [Pp]atient has [Ii]mmunity to a [Dd]isease=exists DiseaseImmunity()
[condition][]- [Tt]he [Vv]accine [Gg]roup affected by the reported [Ii]mmunity is {dd_oSupportedVaccineGroupConcept}=vaccineGroup == {dd_oSupportedVaccineGroupConcept}
[condition][]- [Tt]he [Dd]ate of [Ii]mmunity is {aOp}  {dtImmunityDate}=dateOfImmunity {aOp}  {dtImmunityDate}
[condition][]- [Mm]ake [Nn]ote of the [Dd]ate of [Ii]mmunity as {assign_dtImmunityDate}={assign_dtImmunityDate} : dateOfImmunity
[condition][][Tt]he [Pp]atient is [Ii]mmune to all of the [Dd]iseases specified by {refer_oCollectionOfDiseases} as of the [Dd]ate {refer_dtDate}=List(size == {refer_oCollectionOfDiseases}.size()) from accumulate(DiseaseImmunity($d : disease, dateOfImmunity <= {refer_dtDate}, $r : immunityReason, disease memberOf {refer_oCollectionOfDiseases}), collectList($d))
[condition][][Tt]he [Pp]atient is [Ii]mmune to all of the [Dd]iseases in {refer_oCollectionOfDiseases}=List(size == {refer_oCollectionOfDiseases}.size()) from accumulate(DiseaseImmunity($d : disease, dateOfImmunity <= evalTime, disease memberOf {refer_oCollectionOfDiseases}), collectList($d))
[condition][]The [Ii]mmunization [Hh]istory indicates that the [Pp]atient has obtained [Ii]mmunity to {ddOpenCdsImmunityConcept1Disease} due to reason {ddOpenCdsReasonConcept}=$of : ObservationFocusConcept(openCdsConceptCode == {ddOpenCdsImmunityConcept1Disease}) and $ov : ObservationCodedValueConcept(conceptTargetId == $of.conceptTargetId, openCdsConceptCode == {ddOpenCdsReasonConcept}) and $or : ObservationResult(id == $ov.conceptTargetId)
[consequence][]Make [Nn]ote of the [Pp]atient's [Ii]mmunity to {ddOpenCdsDiseaseConcept} with [Ii]mmunity [Dd]ate as {assign_oDate} and [Ee]valuation [Rr]eason {ddEvaluationReason} and [Rr]ecommendation [Rr]eason {ddRecommendationReason}=Date {assign_oDate} = ICELogicHelper.extractSingularDateValueFromIVLDate($or.getObservationEventTime()); DiseaseImmunity diseaseImmunity = new DiseaseImmunity({ddOpenCdsDiseaseConcept}, {assign_oDate}, {ddEvaluationReason}, {ddRecommendationReason}); insert(diseaseImmunity);
[consequence][]Log that [Ii]mmunity was noted for {sDiseaseName} and [Ii]mmunity [Dd]ate {refer_oDate}=ICELogicHelper.logDRLDebugMessage(drools.getRule().getName(), "Added {sDiseaseName} Immunity as of date " + {refer_oDate}.toString());


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TargetDose
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[condition][]There exists {entity:an |another |}[Aa]dministered [Ss]hot=exists TargetDose()
[condition][]There does not exist {entity:an |another |}[Aa]dministered [Ss]hot=not TargetDose()
[condition][]There is {entity:an |another |}[Aa]dministered [Ss]hot {assign_oTargetDose} that needs to be [Ee]valuated={assign_oTargetDose} : TargetDose(status == DoseStatus.EVALUATION_IN_PROCESS)
[condition][]There is {entity:an |}[Aa]dministered [Ss]hot {assign_oTargetDose} distinct from {assign_oOtherTargetDose}={assign_oTargetDose} : TargetDose(uniqueId != {assign_oOtherTargetDose}.uniqueId)
[condition][]There is {entity:an |}[Aa]dministered [Ss]hot {assign_oTargetDose}={assign_oTargetDose} : TargetDose()
[condition][]- [Tt]he [Uu]nique [Ii]dentifier of the [Ss]hot is {aOp} {sUniqueId}={sUniqueId} {aOp} uniqueId 
[condition][]- [Tt]hat is the [Ss]ame [Ss]hot as {refer_oTargetDose}=this == {refer_oTargetDose} 
[condition][]- [Tt]hat is [Nn]ot the [Ss]ame [Ss]hot as {refer_oTargetDose}=this != {refer_oTargetDose} 
[condition][]- [Tt]he [Ss]hot has [Nn]ot been [Ee]valuated yet=status == DoseStatus.EVALUATION_IN_PROCESS || status == DoseStatus.EVALUATION_NOT_STARTED
[condition][]- [Tt]he [Ss]hot belongs to the [Pp]rimary [Ss]eries=isPrimarySeriesShot() == true
[condition][]- [Tt]he [Ss]hot does not belong to the [Pp]rimary [Ss]eries=isPrimarySeriesShot() == false
[condition][]- [Tt]he [Ss]hot belongs to the [Ss]eries {oTargetSeries}=associatedTargetSeries == {oTargetSeries}
[condition][]- [Tt]he [Ss]hot does not belong to the [Ss]eries {oTargetSeries}=associatedTargetSeries != {oTargetSeries}
[condition][]- [Tt]he [Ss]hot belongs to the [Vv]accine [Gg]roup {dd_oVaccineGroupCdsListItem} and the [Ss]eries with [Nn]ame {sSeriesName}=associatedVaccineGroup == {dd_oVaccineGroupCdsListItem}, associatedSeriesName == {sSeriesName}
[condition][]- [Tt]he [Ss]hot belongs to the [Vv]accine [Gg]roup {dd_oVaccineGroupCdsListItem}=associatedVaccineGroup == {dd_oVaccineGroupCdsListItem}
[condition][]- [Tt]he [Ss]eries that the [Ss]hot belongs to is [Cc]omplete=associatedTargetSeries.isSeriesComplete() == true
[condition][]- [Tt]he [Ss]eries that the [Ss]hot belongs to is [Nn]ot [Cc]omplete=associatedTargetSeries.isSeriesComplete() == false
[condition][]- [Tt]he [Vv]accine [Aa]dministered is a [Ll]ive [Vv]irus [Vv]accine=vaccineComponent.isLiveVirusVaccine == true
[condition][]- [Tt]he [Vv]accine [Aa]dministered is a [Ss]elect [Aa]djuvant [Pp]roduct=vaccineComponent.isSelectAdjuvantProduct == true
[condition][]- [Tt]he [Vv]accine [Aa]dministered is not {dd_oVaccineCdsListItem:[a-zA-Z0-9\\.\\-\\_\\"]+}=vaccineComponent.cdsConceptName != {dd_oVaccineCdsListItem} || administeredVaccine.cdsConceptName != {dd_oVaccineCdsListItem}
[condition][]- [Tt]he [Vv]accine [Aa]dministered is {dd_oVaccineCdsListItem:[a-zA-Z0-9\\.\\-\\_\\"]+}=vaccineComponent.cdsConceptName == {dd_oVaccineCdsListItem} || administeredVaccine.cdsConceptName == {dd_oVaccineCdsListItem}
[condition][]- [Tt]he [Vv]accine [Aa]dministered a member of {list_oVaccineCdsListItem:[\\(]+[a-zA-Z0-9\\.\\-_\\"\\,\\ \\(\\)]+[\\)]+}=vaccineComponent.cdsConceptName in {list_oVaccineCdsListItem} || administeredVaccine.cdsConceptName in {list_oVaccineCdsListItem}
[condition][]- [Tt]he [Vv]accine [Aa]dministered not a member of {list_oVaccineCdsListItem:[\\(]+[a-zA-Z0-9\\.\\-_\\"\\,\\ \\(\\)]+[\\)]+}=vaccineComponent.cdsConceptName not in {list_oVaccineCdsListItem} || administeredVaccine.cdsConceptName not in {list_oVaccineCdsListItem}
[condition][]- [Tt]he [Vv]accine [Aa]dministered has a [Mm]aximum [Vv]alid [Aa]ge=vaccineComponent.validMaximumAgeForUse != null
[condition][]- [Tt]he [Vv]accine [Aa]dministered has a [Mm]inimum [Vv]alid [Aa]ge=vaccineComponent.validMinimumAgeForUse != null
[condition][]- [Tt]he [Aa]dministered [Ss]hot [Nn]umber is {aOp}  {nAdministeredShotNumber}=administeredShotNumberInSeries {aOp}  {nAdministeredShotNumber}
[condition][]- [Tt]he [Dd]ose [Nn]umber in the [Ss]eries is one of {list_oDoseNumbers:([\\(]{1})([0-9\\.\\-_\\"\\,\\ \\(\\)]+)([\\)]{1})}=$assign_nDoseNumber : new Integer(getDoseNumberInSeries()).toString(), $assign_nDoseNumber in {list_oDoseNumbers} 
[condition][]- [Tt]he [Dd]ose [Nn]umber in the [Ss]eries is one of {list_oDoseNumbers:([\\(]{1})[a-zA-Z0-9\\.\\_\\,\\\-\\+\\$\\ ]+([\\)]{1})}=$assign_nDoseNumber : new Integer(getDoseNumberInSeries()).toString(), $assign_nDoseNumber in {list_oDoseNumbers} 
[condition][]- [Tt]he [Dd]ose [Nn]umber in the [Ss]eries is {aOp} {nDoseNumber}=doseNumberInSeries {aOp} {nDoseNumber}
[condition][]- [Tt]hat has already been [Ee]valuated and whose [Ss]hot [Vv]alidity [Ss]tatus is not {oShotValidityStatus}=status != DoseStatus.{oShotValidityStatus} && (status == DoseStatus.INVALID || status == DoseStatus.VALID || status == DoseStatus.ACCEPTED)
[condition][]- [Tt]hat has already been [Ee]valuated and whose [Ss]hot [Vv]alidity [Ss]tatus is {oShotValidityStatus}=status == DoseStatus.{oShotValidityStatus} && (status == DoseStatus.INVALID || status == DoseStatus.VALID || status == DoseStatus.ACCEPTED)
[condition][]- [Tt]hat has already been [Ee]valuated and whose [Ss]hot [Vv]alidity is VALID or ACCEPTED=status == DoseStatus.VALID || status == DoseStatus.ACCEPTED
[condition][]- [Tt]hat has already been [Ee]valuated and whose [Ss]hot [Vv]alidity is ACCEPTED=status == status == DoseStatus.ACCEPTED
[condition][]- [Tt]hat has already been [Ee]valuated and whose [Ss]hot [Vv]alidity is VALID=status == DoseStatus.VALID
[condition][]- [Tt]hat has already been [Ee]valuated and whose [Ss]hot [Vv]alidity is INVALID=status == DoseStatus.INVALID
[condition][]- [Tt]hat has already been [Ee]valuated=status == DoseStatus.INVALID || status == DoseStatus.VALID || status == DoseStatus.ACCEPTED
[condition][]- [Tt]he [Aa]dministration [Dd]ate of the [Ss]hot is {aOp:[\=\\<\\>]+}  {dtOtherDate}=administrationDate {aOp} {dtOtherDate}
[condition][]- [Tt]he [Ss]hot has not already been marked as a [Ll]ive [Vv]irus [Cc]onflict \(as we do not want this [Rr]ule executing more than necessary\)=containsInvalidReason(BaseDataEvaluationReason._TOO_EARLY_LIVE_VIRUS.getCdsListItemName()) == false
[condition][]- [Tt]he [Ss]hot has not already been marked as a [Ss]elect [Aa]djuvant [Pp]roduct [Ii]nterval [Cc]onflict \(as we do not want this [Rr]ule executing more than necessary\)=containsInvalidReason(BaseDataEvaluationReason._SELECT_ADJUVANT_PRODUCT_INTERVAL.getCdsListItemName()) == false
[condition][]- [Mm]ake [Nn]ote of the [Aa]dministered [Ss]hot [Nn]umber as {assign_nAdministeredShotNumber}={assign_nAdministeredShotNumber} : administeredShotNumberInSeries
[condition][]- [Mm]ake [Nn]ote of the [Dd]ose [Nn]umber as {assign_nDoseNumber}={assign_nDoseNumber} : doseNumberInSeries
[condition][]- [Mm]ake [Nn]ote of the {entity:intended |target |actual|evaluated |}[Dd]ose [Nn]umber for this [Ss]hot as {assign_nDoseNumber}={assign_nDoseNumber} : doseNumberInSeries
[condition][]- [Mm]ake [Nn]ote of the [Dd]ate this [Ss]hot was [Aa]dministered as {assign_dtAdministrationDate}={assign_dtAdministrationDate} : administrationDate
[condition][]- [Mm]ake [Nn]ote of the [Dd]iseases [Tt]argeted by the [Vv]accine [Aa]dministered as {assign_oCollectionOfDiseases}={assign_oCollectionOfDiseases} : vaccineComponent.allDiseasesTargetedForImmunity
[condition][]- [Mm]ake [Nn]ote of the [Mm]inimum [Vv]accine [Aa]ge for this [Ss]hot as {assign_strValidMinimumAge}={assign_strValidMinimumAge} : vaccineComponent.validMinimumAgeForUse, {assign_strValidMinimumAge} != null
[condition][]- [Mm]ake [Nn]ote of the [Mm]aximum [Vv]accine [Aa]ge for this [Ss]hot as {assign_strValidMaximumAge}={assign_strValidMaximumAge} : vaccineComponent.validMinimumAgeForUse, {assign_strValidMaximumAge} != null
[condition][]- [Mm]ake [Nn]ote of the [Aa]bsolute [Mm]inimum [Aa]ge of this [Dd]ose as {assign_oTimePeriod}=getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()) != null, {assign_oTimePeriod} : getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()).getAbsoluteMinimumAge(), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Aa]bsolute [Mm]inimum [Ii]nterval from [Tt]his [Dd]ose to the [Nn]ext [Dd]ose as {assign_oTimePeriod}=getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()) != null, {assign_oTimePeriod} : getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()).getAbsoluteMinimumInterval(), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Ee]arliest [Rr]ecommended [Aa]ge of [Tt]his [Dd]ose as {assign_oTimePeriod}=getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()) != null, {assign_oTimePeriod} : getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()).getEarliestRecommendedAge(), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Ee]arliest [Rr]ecommended [Ii]nterval from [Tt]his [Dd]ose to the [Nn]ext [Dd]ose as {assign_oTimePeriod}=getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()) != null, {assign_oTimePeriod} : getAssociatedTargetSeries().obtainDoseRuleForSeriesByDoseNumber(this.getDoseNumberInSeries()).getEarliestRecommendedInterval(), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Aa]ssociated [Ss]eries as {assign_oTargetSeries}={assign_oTargetSeries} : associatedTargetSeries, {assign_oTargetSeries} != null
[condition][]- [Mm]ake [Nn]ote of [Aa]ll [Ee]valuation [Rr]easons for this [Ss]hot as {assign_oCollectionOfReasons}={assign_oCollectionOfReasons} : allEvaluationReasonsFromAllReasonSets
[condition][]- [Mm]ake [Nn]ote of the [Aa]dministered [Vv]accine as {assign_oVaccineAdministered}={assign_oVaccineAdministered} : administeredVaccine 
[condition][]- [Tt]he [Cc]ollection {oCollection} contains {oCollectionElement}={oCollection} contains {oCollectionElement}
[condition][]- [Tt]he [Cc]ollection {oCollection} does not contain {oCollectionElement}={oCollection} not contains {oCollectionElement}
[condition][]- [Tt]he [Nn]umeric  {oNumericOne:[\\$]?[a-zA-Z0-9\\.\\_]+}  is {aOp}  {nNumericTwo:([0-9]+)([\\.][0-9]+)?}={oNumericOne} {aOp} {nNumericTwo}
[condition][]- [Tt]he [Nn]umeric  {oNumericOne:([0-9]+)([\\.][0-9]+)?}  is {aOp}  {nNumericTwo:[\\$]?[a-zA-Z0-9\\.\\_]+}={oNumericOne} {aOp} {nNumericTwo}
[condition][]- [Tt]he [Nn]umeric  {oNumericOne:[\\$]?[a-zA-Z0-9\\.\\_]+}  is {aOp}  {nNumericTwo:[\\$]?[a-zA-Z0-9\\.\\_]+}={oNumericOne} {aOp} {nNumericTwo}
[condition][]- [Tt]he [Dd]ate {dtObjectOne} {aOp}  {dtObjectTwo}={dtObjectOne} != null && {dtObjectTwo} != null && {dtObjectOne} {aOp} {dtObjectTwo} 


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TargetSeries
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[condition][]There exists {entity:a |another |}[Ss]eries=exists TargetSeries()
[condition][]There does not exist {entity:a |another |}[Ss]eries=not TargetSeries()
[condition][]There is a [Ss]eries {assign_oTargetSeries} that needs [Ff]orecasting={assign_oTargetSeries} : TargetSeries(recommendationStatus == RecommendationStatus.FORECASTING_IN_PROGRESS)
[condition][]There is a [Ss]eries {assign_oTargetSeries} that contains the {entity:[Ss]hot|[Dd]ose}  {refer_oTargetDose}={assign_oTargetSeries} : TargetSeries(containsTargetDose({refer_oTargetDose}))
[condition][]There is a [Ss]eries {assign_oTargetSeries} distinct from {assign_oOtherTargetSeries}={assign_oTargetSeries} : TargetSeries(this != {assign_oOtherTargetSeries})
[condition][]There is a [Ss]eries {assign_oTargetSeries} identified by {assign_oSameTargetSeries}={assign_oTargetSeries} : TargetSeries(this == {assign_oSameTargetSeries})
[condition][]There is a [Ss]eries {assign_oTargetSeries}={assign_oTargetSeries} : TargetSeries()
[condition][]- [Tt]he [Ss]eries does not {entity:also |}contain {entity:the [Dd]ose|the [Ss]hot|}  {refer_oTargetDose}=containsTargetDose({refer_oTargetDose}) == false
[condition][]- [Tt]he [Ss]eries {entity:also |}contains {entity:the [Dd]ose|the [Ss]hot|}  {refer_oTargetDose}=containsTargetDose({refer_oTargetDose})
[condition][]- [Tt]hat is the [Ss]ame [Ss]eries as {refer_oTargetSeries}=this == {refer_oTargetSeries}
[condition][]- [Tt]he [Nn]ame of the [Ss]eries is {sNameOfSeries}=seriesRules.seriesName == {sNameOfSeries}
[condition][]- [Tt]he [Ss]eries belongs to the [Vv]accine [Gg]roup {dd_oVaccineGroupCdsListItem}=seriesRules.vaccineGroup == {dd_oVaccineGroupCdsListItem}
[condition][]- [Tt]he [Ss]eries is [Cc]omplete=isSeriesComplete() == true
[condition][]- [Tt]he [Ss]eries is [Nn]ot [Cc]omplete=isSeriesComplete() == false
[condition][]- [Tt]he [Ss]eries is a [Ss]easonal [Ss]eries=targetSeasonExists() == true
[condition][]- [Tt]he [Ss]eries is a [Nn]ot a [Ss]easonal [Ss]eries=targetSeasonExists() == false
[condition][]- [Tt]he [Ss]hot {refer_oTargetDose} [Ff]alls within the [Ss]eason [Ss]tart and [Ss]top [Dd]ates of the [Ss]eries=targetSeason == null || targetSeason.dateIsApplicableToSeason({refer_oTargetDose}.getAdministrationDate(), false) == true
[condition][]- [Tt]he [Ss]hot {refer_oTargetDose} does not [Ff]all within the [Ss]eason [Ss]tart and [Ss]top [Dd]ates of the [Ss]eries=targetSeason != null && targetSeason.dateIsApplicableToSeason({refer_oTargetDose}.getAdministrationDate(), false) == false
[condition][]- [Tt]he [Nn]umber of [Dd]oses [Rr]equired to [Cc]omplete this [Ss]eries is {aOp}  {nDoseNumber}=seriesRules.numberOfDosesInSeries {aOp}  {nDoseNumber}
[condition][]- [Tt]he [Nn]umber of [Aa]dministered [Ss]hots excluding [Dd]uplicate [Ss]hots on the [Ss]ame [Dd]ay is {aOp}  {nNumberOfShots}=numberOfShotsAdministeredInSeriesExcludingDuplicateShotsOnTheSameDay {aOp}  {nNumberOfShots}
[condition][]- [Tt]he [Nn]umber of [Aa]dministered [Ss]hots is {aOp}  {nNumberOfShots}=numberOfShotsAdministeredInSeries {aOp}  {nNumberOfShots}
[condition][]- [Tt]he [Ee]ffective [Dd]ose [Nn]umber in the [Ss]eries is {aOp}  {nEffectiveDoseNumberInSeries}=determineDoseNumberInSeries {aOp} {nEffectiveDoseNumberInSeries}
[condition][]- [Tt]he [Ee]ffective [Nn]umber of [Dd]oses in the [Ss]eries before {dtDate} is {aOp}  {nNumberOfValidAcceptedDoses}=determineEffectiveNumberOfDosesInSeriesByDate({dtDate}, false) {aOp}  {nNumberOfValidAcceptedDoses}
[condition][]- [Tt]he [Ee]ffective [Nn]umber of [Dd]oses in the [Ss]eries on or before {dtDate} is {aOp}  {nNumberOfValidAcceptedDoses}=determineEffectiveNumberOfDosesInSeriesByDate({dtDate}, true) {aOp}  {nNumberOfValidAcceptedDoses}
[condition][]- [Tt]he [Ee]ffective [Nn]umber of [Dd]oses [Aa]dministered in the [Ss]eries is {aOp}  {nEffectiveNumberOfDosesInSeries}=determineEffectiveNumberOfDosesInSeries {aOp} {nEffectiveNumberOfDosesInSeries}
[condition][]- [Tt]he [Nn]umber of [Dd]oses [Aa]dministered is {aOp}  {nNumberOfDoses}=determineNumberOfDosesAdministeredInSeries() {aOp}  {nNumberOfDoses}
[condition][]- [Tt]he [Nn]umber of [Dd]oses [Aa]dministered before {dtDate} is {aOp}  {nNumberOfDoses}=determineNumberOfDosesAdministeredInSeriesByDate({dtDate}, false) {aOp}  {nNumberOfDoses}
[condition][]- [Tt]he [Nn]umber of [Dd]oses [Aa]dministered on or before {dtDate} is {nNumberOfDoses}=determineNumberofDosesAdministeredInSeriesByDate({dtDate}, true) ==  {nNumberOfDoses}
[condition][]- [Tt]he [Vv]accine {oVaccine} is [Pp]ermitted for [Dd]ose [Nn]umber {nDoseNumber} in this [Ss]eries=seriesRules.isAllowableVaccineForDoseRule({oVaccine}, {nDoseNumber}) == true
[condition][]- [Tt]he [Vv]accine {oVaccine} is [Nn]ot [Pp]ermitted for [Dd]ose [Nn]umber {nDoseNumber} in this [Ss]eries=seriesRules.isAllowableVaccineForDoseRule({oVaccine}, {nDoseNumber}) == false
[condition][]- [Tt]here is an [Aa]bsolute [Mm]inimum [Ii]nterval for [Dd]ose {nDoseNumber} in this [Ss]eries=getAbsoluteMinimumIntervalForTargetDoseInStringFormat({nDoseNumber}) != null
[condition][]- [Tt]here is an [Aa]bsolute [Mm]inimum [Aa]ge for [Dd]ose {nDoseNumber} in this [Ss]eries=getAbsoluteMinimumAgeForTargetDoseInStringFormat({nDoseNumber}) != null
[condition][]- [Tt]his [Rr]ule {sRuleName} has not executed before for this [Ss]eries \(and we do not want the [Rr]ule executing more than once for the [Ss]eries\)=containsRuleProcessed({sRuleName}) == false
[condition][]- [Pp]ost [Pp]rocessing on the [Ss]eries [Ff]orecast has not already been run=isPostForecastCheckCompleted() == false
[condition][]- [Pp]ost [Pp]rocessing on the [Ss]eries [Ff]orecast has been run=isPostForecastCheckCompleted() == true
[condition][]- [Pp]erform [Pp]ost [Ff]orecast [Cc]heck is [Ss]et=getPerformPostForecastCheck() == true
[condition][]- [Pp]erform [Pp]ost [Ff]orecast [Cc]heck is [Ss]et=getPerformPostForecastCheck() == false
[condition][]- [Cc]lear [Rr]ecommendations for [Cc]onsideration=
[condition][]- [Aa] [Ff]orecast for the [Ss]eries has not been made yet=recommendationStatus == RecommendationStatus.FORECASTING_IN_PROGRESS
[condition][]- [Aa] [Ff]orecast for the [Ss]eries has been made and a [Rr]ecommendation [Dd]ate has been determined=recommendationStatus == RecommendationStatus.RECOMMENDED || recommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE || recommendationStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED, finalRecommendationDate != null
[condition][]- [Aa] [Ff]orecast for the [Ss]eries has been made and a shot is [Rr]ecommended=recommendationStatus == RecommendationStatus.RECOMMENDED || recommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE
[condition][]- [Aa] [Ff]orecast for the [Ss]eries has been made and a shot is [Rr]ecommended or [Cc]onditionally [Rr]ecommended=recommendationStatus == RecommendationStatus.RECOMMENDED || recommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE || recommendationStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED
[condition][]- [Aa] [Ff]orecast for the [Ss]eries has been made and a [Ss]pecific [Vv]accine is [Rr]ecommended=recommendationVaccine != null && (recommendationStatus == RecommendationStatus.RECOMMENDED || recommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE || recommendationStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED)
[condition][]- [Aa] [Ff]orecast for the [Ss]eries has been made=recommendationStatus == RecommendationStatus.RECOMMENDED || recommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE || recommendationStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED || recommendationStatus == RecommendationStatus.NOT_RECOMMENDED
[condition][]- [Aa]t least one [Ss]hot has been [Aa]dministered=numberOfShotsAdministeredInSeries > 0
[condition][]- [Mm]ake [Nn]ote of the [Aa]bsolute [Mm]inimum [Ii]nterval for [Dd]ose  {nDoseNumber:[0-9]+}  in this [Ss]eries as {assign_oTimePeriod}={assign_oTimePeriod} : getAbsoluteMinimumIntervalForTargetDose({nDoseNumber}), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Aa]bsolute [Mm]inimum [Aa]ge for [Dd]ose  {nDoseNumber:[0-9]+}  in this [Ss]eries as {assign_oTimePeriod}={assign_oTimePeriod} : getAbsoluteMinimumAgeForTargetDose({nDoseNumber}), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Mm]inimum [Ii]nterval for [Dd]ose  {nDoseNumber:[0-9]+}  in this [Ss]eries as {assign_oTimePeriod}={assign_oTimePeriod} : getMinimumIntervalForTargetDose({nDoseNumber}), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Mm]inimum [Aa]ge for [Dd]ose  {nDoseNumber:[0-9]+}  in this [Ss]eries as {assign_oTimePeriod}={assign_oTimePeriod} : getMinimumAgeForTargetDose({nDoseNumber}), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Rr]ecommended [Aa]ge for [Dd]ose  {nDoseNumber:[0-9]+}  in this [Ss]eries as {assign_oTimePeriod}={assign_oTimePeriod} : getRecommendedAgeForTargetDose({nDoseNumber}), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Rr]ecommended [Ii]nterval for [Dd]ose  {nDoseNumber:[0-9]+}  in this [Ss]eries as {assign_oTimePeriod}={assign_oTimePeriod} : getRecommendedIntervalForTargetDose({nDoseNumber}), {assign_oTimePeriod} != null
[condition][]- [Mm]ake [Nn]ote of the [Vv]accine [Gg]roup that this [Ss]eries belongs to as {assign_strVaccineGroupCdsListItem}={assign_strVaccineGroupCdsListItem} : seriesRules.vaccineGroup
[condition][]- [Mm]ake [Nn]ote of the [Dd]iseases supported by this [Ss]eries as {assign_oCollectionOfDiseases}={assign_oCollectionOfDiseases} : diseasesSupportedByThisSeries
[condition][]- [Mm]ake [Nn]ote of the [Nn]umber of [Dd]oses [Rr]equired to [Cc]omplete this [Ss]eries as {assign_nNumberOfDosesRequired}={assign_nNumberOfDosesRequired} : seriesRules.numberOfDosesInSeries
[condition][]- [Mm]ake [Nn]ote of the [Dd]ate that the most recent [Ss]hot was [Aa]dministered as {assign_dtShotDate}={assign_dtShotDate} : getAdministrationDateOfTargetDoseByShotNumberNumber(getNumberOfShotsAdministeredInSeries())
[condition][]- [Mm]ake [Nn]ote of the [Ee]arliest [Dd]ate as {assign_dtRecommendationDate}={assign_dtRecommendationDate} : finalEarliestDate, {assign_dtRecommendationDate} != null
[condition][]- [Mm]ake [Nn]ote of the [Rr]ecommendation [Dd]ate as {assign_dtRecommendationDate}={assign_dtRecommendationDate} : finalRecommendationDate, {assign_dtRecommendationDate} != null
[condition][]- [Mm]ake [Nn]ote of the [Oo]verdue [Dd]ate as {assign_dtRecommendationDate}={assign_dtRecommendationDate} : finalOverdueDate, {assign_dtRecommendationDate} != null
/////// [condition][]- [Mm]ake [Nn]ote of the [Rr]ecommended [Vv]accine as {assign_strRecommendationShot}={assign_strRecommendationShot} : recommendationVaccine.cdsConceptName
[condition][]- [Mm]ake [Nn]ote of the [Rr]ecommended [Vv]accine as {assign_strRecommendationShot}={assign_strRecommendationShot} : recommendationVaccine!.cdsConceptName
[condition][]- [Mm]ake [Nn]ote of the [Nn]umber of [Ss]hots [Aa]dministered as {assign_nNumberOfShotsAdministered}={assign_nNumberOfShotsAdministered} : numberOfShotsAdministeredInSeries()
[condition][]- [Mm]ake [Nn]ote of the [Nn]umber of [Dd]oses [Aa]dministered as {assign_nNumberOfDoses}={assign_nNumberOfDoses} : determineNumberOfDosesAdministeredInSeries()
[condition][]- [Mm]ake [Nn]ote of the [Ee]ffective [Dd]ose [Nn]umber in the [Ss]eries as {assign_nEffectiveDoseNumber}={assign_nEffectiveDoseNumber} : determineDoseNumberInSeries()
[condition][]- [Mm]ake [Nn]ote of the [Ee]ffective [Dd]ose [Nn]umber as {assign_nEffectiveDoseNumber} after which the [Ss]eries was [Mm]arked [Cc]omplete={assign_nEffectiveDoseNumber} : getDoseAfterWhichSeriesWasMarkedComplete()
[condition][]- [Mm]ake [Nn]ote of the [Ss]eason [Ss]tart [Dd]ate as {assign_dtSeasonStartDate}={assign_dtSeasonStartDate} : getSeasonStartDate()
[condition][]- [Mm]ake [Nn]ote of the [Ss]eason [Ee]nd [Dd]ate as {assign_dtSeasonEndDate}={assign_dtSeasonEndDate} : getSeasonEndDate()
[condition][]- [Mm]ake [Nn]ote of the [Oo]ff [Ss]eason [Ss]tart [Dd]ate as {assign_dtOffSeasonStartDate}={assign_dtOffSeasonStartDate} : getOffSeasonStartDate()
[condition][]- [Mm]ake [Nn]ote of the [Oo]ff [Ss]eason [Ee]nd [Dd]ate as {assign_dtOffSeasonEndDate}={assign_dtOffSeasonEndDate} : getOffSeasonEndDate()
[condition][]- [Mm]ake [Nn]ote of the [Ll]ast [Ss]hot [Aa]dministered in the [Ss]eries as {assign_oLastShotInSeries}={assign_oLastShotInSeries} : getLastShotAdministeredInSeries()
[condition][]- [Mm]ake [Nn]ote of [Ss]hot [Aa]dministered by [Ss]hot [Nn]umber {nShotNumber} in the [Ss]eries as {assign_oShotInSeries}={assign_oShotInSeries} : getTargetDoseByAdministeredShotNumber({nShotNumber})
[condition][]- [Mm]ake [Nn]ote of [Aa]ll [Vv]accines [Pp]ermitted for [Dd]ose {nDoseNumber} in the [Ss]eries as {assign_oListVaccines}={assign_oListVaccines} : getAllPermittedVaccinesForTargetDose({nDoseNumber})
[condition][]- [Mm]ake [Nn]ote of the [Aa]llowable [Vv]accines for [Dd]ose {nDoseNumber} in the [Ss]eries as {assign_oListVaccines}={assign_oListVaccines} : getAllowableVaccinesForTargetDose({nDoseNumber})
[condition][]- [Mm]ake [Nn]ote of the [Pp]referable [Vv]accines for [Dd]ose {nDoseNumber} in the [Ss]eries as {assign_oListVaccines}={assign_oListVaccines} : getPreferableVaccinesForTargetDose({nDoseNumber})
[condition][]- [Tt]he [Cc]ollection {oCollection} contains {oCollectionElement}={oCollection} contains {oCollectionElement}
[condition][]- [Tt]he [Cc]ollection {oCollection} does not contain {oCollectionElement}={oCollection} not contains {oCollectionElement}
[condition][]- [Tt]he [Nn]umeric  {oNumericOne:[\\$]?[a-zA-Z0-9\\.\\_]+}  is {aOp}  {nNumericTwo:([0-9]+)([\\.][0-9]+)?}={oNumericOne} {aOp} {nNumericTwo}
[condition][]- [Tt]he [Nn]umeric  {oNumericOne:([0-9]+)([\\.][0-9]+)?}  is {aOp}  {nNumericTwo:[\\$]?[a-zA-Z0-9\\.\\_]+}={oNumericOne} {aOp} {nNumericTwo}
[condition][]- [Tt]he [Nn]umeric  {oNumericOne:[\\$]?[a-zA-Z0-9\\.\\_]+}  is {aOp}  {nNumericTwo:[\\$]?[a-zA-Z0-9\\.\\_]+}={oNumericOne} {aOp} {nNumericTwo}
/////// [condition][]- [Tt]he [Ss]tring {strObject} {aOp}  {strValue}={strObject} != null && {strObject} {aOp} {strValue}
[condition][]- [Tt]he [Ss]tring {strObject} {aOp}  {strValue}={strObject} != null && {strObject} {aOp} {strValue} || {strObject} == null && {strValue} == null
[condition][]- [Tt]he [Dd]ate {dtObjectOne} {aOp}  {dtObjectTwo}={dtObjectOne} != null && {dtObjectTwo} != null && {dtObjectOne} {aOp} {dtObjectTwo}
 
//
// TargetDose accumulates
//
[condition][]Verify that the [Cc]ount of [Dd]oses [Aa]dministered in Series {refer_oTargetSeries} with [Vv]accine a member of {dd_oVaccineCdsList:[\\(]+[a-zA-Z0-9\\.\\-_\\"\\,\\ \\(\\)]+[\\)]+} is {aOp_num}  {nNumberOfDoses}=accumulate($td : TargetDose(status == DoseStatus.VALID, vaccineComponent.cdsConceptName in {dd_oVaccineCdsList} || $td.administeredVaccine.cdsConceptName in {dd_oVaccineCdsList}) from {refer_oTargetSeries}.targetDoses; $countNum: count($td); $countNum {aOp_num}  {nNumberOfDoses})
[condition][]Verify that the [Cc]ount of [Dd]oses [Aa]dministered in Series {refer_oTargetSeries} with [Vv]accine {dd_oVaccineCdsListItem} is {aOp_num}  {nNumberOfDoses}=accumulate($td : TargetDose(status == DoseStatus.VALID, vaccineComponent.cdsConceptName == {dd_oVaccineCdsListItem} || $td.administeredVaccine.cdsConceptName == {dd_oVaccineCdsListItem}) from {refer_oTargetSeries}.targetDoses; $countNum: count($td); $countNum {aOp_num}  {nNumberOfDoses})
[condition][]Verify that the [Uu]nique [Cc]ount of [Ss]hots [Aa]dministered in Series {refer_oTargetSeries} by [Dd]ate is {aOp_num}  {nNumberOfShots}=Set(size {aOp_num} {nNumberOfShots}) from accumulate(TargetDose($shotDate : administrationDate) from {refer_oTargetSeries}.targetDoses, collectSet($shotDate))


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// EVAL Conditions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[keyword][][Cc]onfirm {conditions}=eval( {conditions} )
[condition][][Tt]he [Vv]ariable {refer_oVariable} is {aOp}  {oValue}={refer_oVariable}  {aOp}  {oValue}
[condition][][Tt]he [Aa]ge of the [Pp]atient {refer_oEvaluatedPerson} at the [Tt]ime the [Vv]accine was [Aa]dministered for [Dd]ose {refer_oTargetDose} is [Gg]reater [Tt]han the [Mm]aximum [Aa]llowable [Aa]ge for the [Vv]accine=(TimePeriod.compareElapsedTimePeriodToDateRange({refer_oEvaluatedPerson}.getDemographics().getBirthTime(), {refer_oTargetDose}.getAdministrationDate(), {refer_oTargetDose}.getVaccineComponent().getValidMaximumAgeForUse()) > 0)
[condition][][Ee]lapsed [Tt]ime between {dtDateOne} and {dtDateTwo}  {aOp}  {nDuration:[0-9]+}  {enumTimePeriod_durationType:[a-zA-Z0-9\.]+}=(TimePeriod.compareElapsedTimePeriodToDateRange({dtDateOne}, {dtDateTwo}, new TimePeriod({nDuration}, {enumTimePeriod_durationType})) {aOp} 0)
[condition][][Ee]lapsed [Tt]ime between {dtDateOne} and {dtDateTwo}  {aOp:[\=\\<\\>]+}  {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}=(TimePeriod.compareElapsedTimePeriodToDateRange({dtDateOne}, {dtDateTwo}, {sDuration}) {aOp} 0)
[condition][][Ee]lapsed [Tt]ime between {dtDateOne} and {dtDateTwo}  {aOp}  {refer_Duration:([\\$]{1})[a-zA-Z0-9_]+}=(TimePeriod.compareElapsedTimePeriodToDateRange({dtDateOne}, {dtDateTwo}, {refer_Duration}) {aOp} 0)
[condition][][Tt]he [Dd]ate {dtDateOne} is after {strDateTwo:[\\"]{1}[0-9]+[\\-]{1}[a-zA-Z]+[\\-]{1}[0-9]+[\\"]{1}}={dtDateOne} != null && {dtDateOne}.after(TimePeriod.generateDateFromStringInDroolsDateFormat({strDateTwo}))
[condition][][Tt]he [Dd]ate {dtDateOne} is after {dtDateTwo:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={dtDateOne} != null && {dtDateTwo} != null && {dtDateOne}.after({dtDateTwo})
[condition][][Tt]he [Dd]ate {dtDateOne} is before {strDateTwo:[\\"]{1}[0-9]+[\\-]{1}[a-zA-Z]+[\\-]{1}[0-9]+[\\"]{1}}={dtDateOne} != null && {dtDateOne}.before(TimePeriod.generateDateFromStringInDroolsDateFormat({strDateTwo}))
[condition][][Tt]he [Dd]ate {dtDateOne} is before {dtDateTwo:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={dtDateOne} != null && {dtDateTwo} != null && {dtDateOne}.before({dtDateTwo})
[condition][][Tt]he [Dd]ate {dtDateOne} is on the same date or before {strDateTwo:[\\"]{1}[0-9]+[\\-]{1}[a-zA-Z]+[\\-]{1}[0-9]+[\\"]{1}}={dtDateOne} != null {dtDateOne}.compareTo(TimePeriod.generateDateFromStringInDroolsDateFormat({strDateTwo}))) <= 0
[condition][][Tt]he [Dd]ate {dtDateOne} is on the same date or before {dtDateTwo:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={dtDateOne} != null && {dtDateTwo} != null && {dtDateOne}.compareTo({dtDateTwo}) <= 0
[condition][][Tt]he [Dd]ate {dtDateOne} is on the same date or after {strDateTwo:[\\"]{1}[0-9]+[\\-]{1}[a-zA-Z]+[\\-]{1}[0-9]+[\\"]{1}}={dtDateOne} != null && {dtDateOne}.compareTo(TimePeriod.generateDateFromStringInDroolsDateFormat({strDateTwo}))) >= 0
[condition][][Tt]he [Dd]ate {dtDateOne} is on the same date or after {dtDateTwo:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={dtDateOne} != null && {dtDateTwo} != null && {dtDateOne}.compareTo({dtDateTwo}) >= 0
[condition][][Tt]he [Dd]ate {dtDateOne} is on the same day as {strDateTwo:[\\"]{1}[0-9]+[\\-]{1}[a-zA-Z]+[\\-]{1}[0-9]+[\\"]{1}}={dtDateOne} != null && {dtDateOne}.equals(TimePeriod.generateDateFromStringInDroolsDateFormat({strDateTwo}))
[condition][][Tt]he [Dd]ate {dtDateOne} is on the same day as {dtDateTwo:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={dtDateOne} != null && {dtDateTwo} != null && {dtDateOne}.equals({dtDateTwo})
[condition][]\([Cc]alculate [Dd]ate from addition of {refer_dtDate} with TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}\)=TimePeriod.addTimePeriod({refer_dtDate}, {sDuration})
[condition][]\([Cc]alculate [Dd]ate from addition of {refer_dtDate} with TimePeriod {refer_oDuration:([\\$]{1})(\w)+(\s){0}}\)=TimePeriod.addTimePeriod({refer_dtDate}, {refer_oDuration})


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                          CONSEQUENCES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TargetDose Actions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[consequence][][Ss]et the [Ss]hot [Ss]tatus of {refer_oTargetDose} to [Vv]alid={refer_oTargetDose}.setStatus(DoseStatus.VALID);
[consequence][][Ss]et the [Ss]hot [Ss]tatus of {refer_oTargetDose} to [Aa]ccepted={refer_oTargetDose}.setStatus(DoseStatus.ACCEPTED);
[consequence][][Ss]et the [Ss]hot [Ss]tatus of {refer_oTargetDose} to [Ii]nvalid={refer_oTargetDose}.setStatus(DoseStatus.INVALID);
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Vv]alid=
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Insufficient Antigen"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.INSUFFICIENT_ANTIGEN"); insert(new ICEFactTypeFinding(SupportedFactConcept._INVALID_VACCINE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Below Minimum Age for Final Dose"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.BELOW_MINIMUM_AGE_FINAL_DOSE"); insert(new ICEFactTypeFinding(SupportedFactConcept._BELOW_MINIMUM_AGE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Below Minimum Age"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.BELOW_MINIMUM_AGE_SERIES"); insert(new ICEFactTypeFinding(SupportedFactConcept._BELOW_MINIMUM_AGE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Below Minimum Interval"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.BELOW_MINIMUM_INTERVAL"); insert(new ICEFactTypeFinding(SupportedFactConcept._BELOW_MINIMUM_INTERVAL.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Duplicate Same Day"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.DUPLICATE_SAME_DAY");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Too Early Live Virus"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.TOO_EARLY_LIVE_VIRUS");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Select Adjuvant Product Interval"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.SELECT_ADJUVANT_PRODUCT_INTERVAL");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Prior to DOB"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.PRIOR_TO_DOB");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to the following reason code {sInvalidReasonCode}={refer_oTargetDose}.addInvalidReason({sInvalidReasonCode});
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Vaccine not Permitted for this Dose \(Booster Only\)"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.BOOSTER_ONLY"); insert(new ICEFactTypeFinding(SupportedFactConcept._INVALID_VACCINE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Vaccine not Permitted for this Dose"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_ALLOWED_FOR_THIS_DOSE"); insert(new ICEFactTypeFinding(SupportedFactConcept._INVALID_VACCINE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Below Minimum Interval \(PCV PPSV\)"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.BELOW_MIN_INTERVAL_PCV_PPSV"); insert(new ICEFactTypeFinding(SupportedFactConcept._BELOW_MINIMUM_INTERVAL.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Outside Flu Season"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.OUTSIDE_FLU_VAC_SEASON");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "Missing Antigen"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.MISSING_ANTIGEN");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to "D_AND_T_INVALID/P_VALID"={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.D_AND_T_INVALID/P_VALID");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Proof of Immunity"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.PROOF_OF_IMMUNITY");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Above Recommended Age"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.ABOVE_REC_AGE_SERIES");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Below Recommended Age"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.BELOW_REC_AGE_SERIES"); insert(new ICEFactTypeFinding(SupportedFactConcept._BELOW_MINIMUM_AGE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Vaccine Not Licensed For Males"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_LICENSED_FOR_MALES");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Vaccine Not Allowed For This Dose"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_ALLOWED_FOR_THIS_DOSE"); insert(new ICEFactTypeFinding(SupportedFactConcept._INVALID_VACCINE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Vaccine Not Allowed"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_ALLOWED"); insert(new ICEFactTypeFinding(SupportedFactConcept._INVALID_VACCINE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Extra Dose"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.EXTRA_DOSE");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Below Minimum Age for Final Dose"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.BELOW_MINIMUM_AGE_FINAL_DOSE"); insert(new ICEFactTypeFinding(SupportedFactConcept._BELOW_MINIMUM_AGE.getConceptCodeValue(), {refer_oTargetDose}));
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Vaccine Not Counted Based on Most Recent Vaccine Given"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_COUNTED_BASED_ON_MOST_RECENT_VACCINE_GIVEN");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to "Vaccine Not Part of This Series"={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_PART_OF_THIS_SERIES");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted for this [Ss]eries={refer_oTargetDose}.addAcceptedReason("EVALUATION_REASON_CONCEPT.UNSPECIFIED_REASON");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid for this [Ss]eries={refer_oTargetDose}.addInvalidReason("EVALUATION_REASON_CONCEPT.UNSPECIFIED_REASON");
[consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Nn]ot [Ee]valuated due to "Vaccine Not Supported"={refer_oTargetDose}.addNotEvalatedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_SUPPORTED");
////////////// DO NOT USE [consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Aa]ccepted due to {oReason:[\\$]?[a-zA-Z0-9\\.\\_\\(\\)\\/\\"]+}={refer_oTargetDose}.addAcceptedReason({oReason});
////////////// DO NOT USE [consequence][][Ii]nclude the [Rr]eason for [Ss]hot {refer_oTargetDose} [Ii]nvalid due to {oReason:[\\$]?[a-zA-Z0-9\\.\\_\\(\\)\\/\\"]+}={refer_oTargetDose}.addInvalidReason({oReason});
[consequence][][Rr]emove [Ee]valuation [Rr]eason {strReason:[\\"]{1}[a-zA-Z0-9\\.\\_\\ ]+[\\"]{1}} from [Ss]hot {refer_oTargetDose:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={refer_oTargetDose}.removeEvaluationReasonFromAllReasonSets({strReason});
[consequence][][Rr]emove [Ee]valuation [Rr]eason {oReason:[\\$]?[a-zA-Z0-9\\.\\_\\(\\)]+} from [Ss]hot {refer_oTargetDose:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={refer_oTargetDose}.removeEvaluationReasonFromAllReasonSets({oReason});
[consequence][][Rr]emove [Aa]ll [Ee]valuation [Rr]easons from [Ss]hot {refer_oTargetDose:[\\$]?[a-zA-Z0-9\\.\\_\\]+}={refer_oTargetDose}.removeAllEvaluationReasonsFromAllReasonSets();
[consequence][][Mm]ark the [Ss]hot {refer_oTargetDose} as [Nn]ot [Ii]gnored={refer_oTargetDose}.setIsShotIgnoredForCompletionOfSeries(false);
[consequence][][Mm]ark the [Ss]hot {refer_oTargetDose} as [Ii]gnored={refer_oTargetDose}.setIsShotIgnoredForCompletionOfSeries(true);
[consequence][][Ss]et [Dd]ose [Nn]umber of {refer_oTargetDose} to {nDoseNumber}=modify({refer_oTargetDose}) \{ setDoseNumberInSeries({nDoseNumber}); \};
////////////// [consequence][][Mm]ark the [Ss]hot {refer_oTargetDose} as [Ee]valuation [Nn]ot [Ss]tarted for this [Ss]eries=modify({refer_oTargetDose}) \{ setStatus(DoseStatus.EVALUATION_NOT_STARTED), removeAllEvaluationReasonsFromAllReasonSets(); \};


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TargetSeries Actions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[consequence][][Cc]lear [Ff]orecasted [Rr]ecommendations from [Cc]onsideration in [Ss]eries {refer_oTargetSeries}={refer_oTargetSeries}.clearRecommendations();
[consequence][][Rr]emove the [Ss]hot {refer_oTargetDose} from [Ee]valuation as a part of the [Ss]eries {refer_oTargetSeries}=modify({refer_oTargetSeries}) \{ removeTargetDoseFromSeries({refer_oTargetDose}); \};
[consequence][][Mm]ark that [Ee]valuation of [Ss]hot {refer_oTargetDose} is complete and therefore should not be reevaluated by any other rules=modify ({refer_oTargetDose}) \{ setStatus(DoseStatus.EVALUATION_COMPLETE) \}
[consequence][][Ss]kip [Ss]eries [Dd]ose [Nn]umber to {nToDoseNumber} from {nFromDoseNumber} for [Dd]isease {dd_oSupportedDiseaseConcept} in [Ss]eries {refer_oTargetSeries}=modify({refer_oTargetSeries}) \{ addSkipDoseEntryForSpecifiedDisease({nFromDoseNumber}, {nToDoseNumber}, {dd_oSupportedDiseaseConcept}); \}
[consequence][][Ss]kip [Ss]eries [Dd]ose [Nn]umber to {nToDoseNumber} from {nFromDoseNumber} for all [Dd]iseases in the [Ss]eries {refer_oTargetSeries}=modify({refer_oTargetSeries}) \{ addSkipDoseEntryForDose({nFromDoseNumber}, {nToDoseNumber}); \}
[consequence][][Cc]onvert from [Ss]eries {refer_oTargetSeries_SeriesToSwitchFrom} to {refer_oTargetSeries_SeriesToSwitchTo} starting with [Dd]ose [Nn]umber {nDoseNumber} and [Ee]valuate [Uu]sing [Ii]nterval for [Pp]rior [Dd]ose to this [Dd]ose from [Ss]witchedTo [Ss]eries={refer_oTargetSeries_SeriesToSwitchFrom}.convertToSpecifiedSeries({refer_oTargetSeries_SeriesToSwitchTo}.seriesName, {nDoseNumber}, true); for (TargetDose d : {refer_oTargetSeries_SeriesToSwitchTo}.targetDoses) \{ retract(d); \} retract({refer_oTargetSeries_SeriesToSwitchTo}); update({refer_oTargetSeries_SeriesToSwitchFrom});
[consequence][][Cc]onvert from [Ss]eries {refer_oTargetSeries_SeriesToSwitchFrom} to {refer_oTargetSeries_SeriesToSwitchTo} starting with [Dd]ose [Nn]umber {nDoseNumber} and [Ee]valuate [Uu]sing [Ii]nterval for [Pp]rior [Dd]ose to this [Dd]ose from [Ss]witchedFrom [Ss]eries={refer_oTargetSeries_SeriesToSwitchFrom}.convertToSpecifiedSeries({refer_oTargetSeries_SeriesToSwitchTo}.seriesName, {nDoseNumber}, false); for (TargetDose d : {refer_oTargetSeries_SeriesToSwitchTo}.targetDoses) \{ retract(d); \} retract({refer_oTargetSeries_SeriesToSwitchTo}); update({refer_oTargetSeries_SeriesToSwitchFrom});
[consequence][][Rr]efresh all [Ff]acts in the [Ss]eries {refer_oTargetSeries} for [Ee]valuation=modify ({refer_oTargetSeries}) \{ setRecommendationStatus(RecommendationStatus.NOT_FORECASTED); \}
[consequence][][Rr]efresh all [Ff]acts in the [Ss]hot {refer_oTargetDose}=update({refer_oTargetDose});
[consequence][][Mm]ark that the [Ss]eries {refer_oTargetSeries} cannot be forecasted as [Cc]omplete={refer_oTargetSeries}.setSeriesComplete(false);
[consequence][][Mm]ark that the [Ss]eries {refer_oTargetSeries} can be forecasted as [Cc]omplete={refer_oTargetSeries}.setSeriesComplete(true);
[consequence][][Mm]ark the Series {refer_oTargetSeries} [Nn]ot [Cc]omplete={refer_oTargetSeries}.setSeriesComplete(false);
[consequence][][Mm]ark the Series {refer_oTargetSeries} [Cc]omplete={refer_oTargetSeries}.setSeriesComplete(true);
[consequence][][Aa]dd {nDuration}  {oDurationType} to {dtDate} and [Mm]ake [Nn]ote of the newly [Cc]alculated [Dd]ate as {assign_dtDateCalculated}=Date {assign_dtDateCalculated} = TimePeriod.addTimePeriod({dtDate}, new TimePeriod({nDuration}, {oDurationType}));
[consequence][][Aa]dd {oTimePeriod} to {dtDate} and [Mm]ake [Nn]ote of the newly [Cc]alculated [Dd]ate as {assign_dtDateCalculated}=Date {assign_dtDateCalculated} = TimePeriod.addTimePeriod({dtDate}, {oTimePeriod});
[consequence][][Mm]ark that the [Rr]ecommendation for [Ss]eries {refer_oTargetSeries} must take [Ll]ive [Vv]irus [Ii]nterval into [Aa]ccount={refer_oTargetSeries}.setManuallySetAccountForLiveVirusIntervalsInRecommendation(true);
[consequence][][Mm]ark that the [Rr]ecommendation for [Ss]eries {refer_oTargetSeries} must not take [Ll]ive [Vv]irus [Ii]nterval into [Aa]ccount={refer_oTargetSeries}.setManuallySetAccountForLiveVirusIntervalsInRecommendation(false);
[consequence][][Mm]ake [Nn]ote of the [Aa]bsolute [Mm]inimum [Ii]nterval for [Dd]ose {nDoseNumber} in the [Ss]eries {refer_oTargetSeries} as {assign_strTimePeriod}=String {assign_strTimePeriod}={refer_oTargetSeries}.getAbsoluteMinimumIntervalForTargetDoseInStringFormat({nDoseNumber});
[consequence][][Mm]ake [Nn]ote of the [Aa]bsolute [Mm]inimum [Aa]ge for [Dd]ose {nDoseNumber} in the [Ss]eries {refer_oTargetSeries} as {assign_strTimePeriod}=String {assign_strTimePeriod}={refer_oTargetSeries}.getAbsoluteMinimumAgeForTargetDoseInStringFormat({nDoseNumber});
[consequence][][Mm]ake [Nn]ote of the [Rr]ecommended [Aa]ge for [Dd]ose {nDoseNumber} in the [Ss]eries {refer_oTargetSeries} as {assign_strTimePeriod}=String {assign_strTimePeriod}={refer_oTargetSeries}.getRecommendedAgeForTargetDoseInStringFormat({nDoseNumber});
[consequence][][Mm]ake [Nn]ote of the [Rr]ecommended [Ii]nterval for [Dd]ose {nDoseNumber} in the [Ss]eries {refer_oTargetSeries} as {assign_strTimePeriod}=String {assign_strTimePeriod}={refer_oTargetSeries}.getRecommendedIntervalForTargetDoseInStringFormat({nDoseNumber});
[consequence][][Cc]reate a [Rr]ecommendation as {assign_oRecommendation} with [Ss]tatus {enum_RecommendationStatus} for the [Ss]eries {refer_oTargetSeries}=Recommendation {assign_oRecommendation} = new Recommendation({refer_oTargetSeries}); {assign_oRecommendation}.setRecommendationStatus({enum_RecommendationStatus});
[consequence][][Cc]reate a [Rr]ecommendation as {assign_oRecommendation} for the [Ss]eries {refer_oTargetSeries}=Recommendation {assign_oRecommendation} = new Recommendation({refer_oTargetSeries});
[consequence][][Ss]et the [Rr]ecommendation [Ss]tatus for {refer_oRecommendation} to {enum_RecommendationStatus}={refer_oRecommendation}.setRecommendationStatus({enum_RecommendationStatus});
[consequence][][Ss]et the [Rr]ecommendation [Ee]arliest [Ff]orecast [Dd]ate for {refer_oRecommendation} to {dtForecastDate}={refer_oRecommendation}.setEarliestDate({dtForecastDate});
[consequence][][Ss]et the [Rr]ecommendation [Rr]ecommended [Ff]orecast [Dd]ate for {refer_oRecommendation} to {dtForecastDate}={refer_oRecommendation}.setRecommendationDate({dtForecastDate});
[consequence][][Ss]et the [Rr]ecommendation [Oo]verdue [Ff]orecast [Dd]ate for {refer_oRecommendation} to {dtForecastDate}={refer_oRecommendation}.setOverdueDate({dtForecastDate});
[consequence][][Ss]et the [Rr]ecommendation [Rr]eason for {refer_oRecommendation} to {oCD}={refer_oRecommendation}.setRecommendationReason({oCD});
[consequence][][Ss]et the [Rr]ecommendation [Vv]accine for {refer_oRecommendation} to {dd_strCdsConceptValue}={refer_oRecommendation}.setRecommendedVaccine(schedule.getVaccineByCdsConceptValue({dd_strCdsConceptValue}));
[consequence][][Uu]nset the [Rr]ecommendation [Vv]accine for {refer_oRecommendation}={refer_oRecommendation}.setRecommendedVaccine(null);
[consequence][][Ii]nclude a [Rr]ecommendation as {assign_oRecommendation} with [Ss]tatus {enum_RecommendationStatus} and [Rr]ecommended [Ff]orecast [Dd]ate {dtForecastDate} for [Cc]onsideration in the final [Ff]orecast of the [Ss]eries {refer_oTargetSeries}=Recommendation {assign_oRecommendation} = new Recommendation({refer_oTargetSeries}); {assign_oRecommendation}.setRecommendationDate({dtForecastDate}); {assign_oRecommendation}.setRecommendationStatus({enum_RecommendationStatus}); insert({assign_oRecommendation});
[consequence][][Ii]nclude a [Rr]ecommendation as {assign_oRecommendation} with [Ee]arliest [Ff]orecast [Dd]ate {dtForecastDate} for [Cc]onsideration in the final [Ff]orecast of the [Ss]eries {refer_oTargetSeries}=Recommendation {assign_oRecommendation} = new Recommendation({refer_oTargetSeries}); {assign_oRecommendation}.setEarliestDate({dtForecastDate}); insert({assign_oRecommendation});
[consequence][][Ii]nclude a [Rr]ecommendation as {assign_oRecommendation} with [Rr]ecommended [Ff]orecast [Dd]ate {dtForecastDate} for [Cc]onsideration in the final [Ff]orecast of the [Ss]eries {refer_oTargetSeries}=Recommendation {assign_oRecommendation} = new Recommendation({refer_oTargetSeries}); {assign_oRecommendation}.setRecommendationDate({dtForecastDate}); insert({assign_oRecommendation});
[consequence][][Ii]nclude a [Rr]ecommendation as {assign_oRecommendation} with [Oo]verdue [Ff]orecast [Dd]ate {dtForecastDate} for [Cc]onsideration in the final [Ff]orecast of the [Ss]eries {refer_oTargetSeries}=Recommendation {assign_oRecommendation} = new Recommendation({refer_oTargetSeries}); {assign_oRecommendation}.setOverdueDate({dtForecastDate}); insert({assign_oRecommendation});
[consequence][][Ii]nclude the [Rr]ecommendation {refer_oRecommendation} for [Cc]onsideration in the final [Ff]orecast of the [Ss]eries=insert({refer_oRecommendation});
[consequence][][Ll]ogically [Ii]nsert the [Rr]ecommendation {refer_oRecommendation} into [Ww]orking [Mm]emory for [Pp]otential [Cc]onsideration in the final [Ff]orecast of the [Ss]eries=insertLogical({refer_oRecommendation});

// Consider adding reevaluation of all shots in the series
/////// [consequence][][Rr]eevaluate all [Ss]hots in the [Ss]eries {refer_oTargetSeries}=


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// DoseRule Actions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[consequence][][Cc]reate the next DoseRule as {assign_oDoseRule} which may be added to the SeriesRules for [Ss]eries {refer_oTargetSeries}=DoseRule {assign_oDoseRule} = new DoseRule(); {assign_oDoseRule}.setDoseNumber({refer_oTargetSeries}.getSeriesRules().getNumberOfDosesInSeries()+1);
[consequence][][Oo]btain the existing DoseRule for [Dd]ose [Nn]umber {nDoseNumber} in the [Ss]eries {refer_oTargetSeries} as {assign_oDoseRule}=DoseRule {assign_oDoseRule} = DoseRule.constructDeepCopyOfDoseRuleObject({refer_oTargetSeries}.obtainDoseRuleForSeriesByDoseNumber({nDoseNumber})); if ({assign_oDoseRule} == null) \{ {assign_oDoseRule} = new DoseRule(); \}
/////// [consequence][][Ss]et the [Dd]ose [Nn]umber for [Dd]oseRule {assign_oDoseRule} to {nDoseNumber}={assign_oDoseRule}.setDoseNumber({nDoseNumber});
[consequence][][Ss]et the [Aa]bsolute [Mm]inimum [Aa]ge for [Dd]oseRule {assign_oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={assign_oDoseRule}.setAbsoluteMinimumAge(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Aa]bsolute [Mm]inimum [Aa]ge for [Dd]oseRule {assign_oDoseRule} to TimePeriod {refer_oTimePeriod}={assign_oDoseRule}.setAbsoluteMinimumAge({refer_oTimePeriod});
[consequence][][Ss]et the [Mm]inimum [Aa]ge for [Dd]oseRule {assign_oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={assign_oDoseRule}.setMinimumAge(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Mm]inimum [Aa]ge for [Dd]oseRule {assign_oDoseRule} to TimePeriod {refer_oTimePeriod}={assign_oDoseRule}.setMinimumAge({refer_oTimePeriod});
[consequence][][Ss]et the [Aa]bsolute [Mm]inimum [Ii]nterval for [Dd]oseRule {assign_oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={assign_oDoseRule}.setAbsoluteMinimumInterval(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Aa]bsolute [Mm]inimum [Ii]nterval for [Dd]oseRule {assign_oDoseRule} to TimePeriod {refer_oTimePeriod}={assign_oDoseRule}.setAbsoluteMinimumInterval({refer_oTimePeriod});
[consequence][][Ss]et the [Mm]inimum [Ii]nterval for [Dd]oseRule {assign_oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={assign_oDoseRule}.setMinimumInterval(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Mm]inimum [Ii]nterval for [Dd]oseRule {assign_oDoseRule} to TimePeriod {refer_oTimePeriod}={assign_oDoseRule}.setAbsoluteMinimumInterval({refer_oTimePeriod});
[consequence][][Ss]et the [Ee]arliest [Rr]ecommended [Aa]ge for [Dd]oseRule {oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={oDoseRule}.setEarliestRecommendedAge(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Ee]arliest [Rr]ecommended [Aa]ge for [Dd]oseRule {oDoseRule} to TimePeriod {refer_oTimePeriod}={oDoseRule}.setEarliestRecommendedAge(refer_oTimePeriod);
[consequence][][Ss]et the [Ee]arliest [Rr]ecommended [Aa]ge for [Dd]oseRule {oDoseRule} to that of [Dd]oseRule {refer_oDoseRule}={oDoseRule}.setEarliestRecommendedAge({refer_oDoseRule}.getEarliestRecommendedAge());
[consequence][][Ss]et the [Ee]arliest [Rr]ecommended [Ii]nterval for [Dd]oseRule {oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={oDoseRule}.setEarliestRecommendedInterval(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Ee]arliest [Rr]ecommended [Ii]nterval for [Dd]oseRule {oDoseRule} to TimePeriod {refer_oTimePeriod}={oDoseRule}.setEarliestRecommendedInterval({refer_oTimePeriod});
[consequence][][Ss]et the [Ee]arliest [Rr]ecommended [Ii]nterval for [Dd]oseRule {oDoseRule} to that of [Dd]oseRule {refer_oDoseRule}={oDoseRule}.setEarliestRecommendedInterval({refer_oDoseRule}.getEarliestRecommendedInterval());
[consequence][][Ss]et the [Ll]atest [Rr]ecommended [Aa]ge for [Dd]oseRule {oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={oDoseRule}.setLatestRecommendedAge(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Ll]atest [Rr]ecommended [Aa]ge for [Dd]oseRule {oDoseRule} to TimePeriod {refer_oTimePeriod}={oDoseRule}.setLatestRecommendedAge({refer_oTimePeriod});
[consequence][][Ss]et the [Ll]atest [Rr]ecommended [Aa]ge for [Dd]oseRule {oDoseRule} to that of [Dd]oseRule {refer_oDoseRule}={oDoseRule}.setLatestRecommendedAge({refer_oDoseRule}.getLatestRecommendedAge());
[consequence][][Ss]et the [Ll]atest [Rr]ecommended [Ii]nterval for [Dd]oseRule {assign_oDoseRule} to TimePeriod {sDuration:([\\"]{1})([-|+]?[0-9]+[Yy])?([-|+]?[0-9]+[Mm])?([-|+]?[0-9]+[Ww])?([-|+]?[0-9]+[Dd])?([\\"]{1})}={assign_oDoseRule}.setLatestRecommendedInterval(new TimePeriod({sDuration}));
[consequence][][Ss]et the [Ll]atest [Rr]ecommended [Ii]nterval for [Dd]oseRule {assign_oDoseRule} to TimePeriod {refer_oTimePeriod}={assign_oDoseRule}.setLatestRecommendedInterval({refer_oTimePeriod});
[consequence][][Ss]et the [Ll]atest [Rr]ecommended [Ii]nterval for [Dd]oseRule {oDoseRule} to that of [Dd]oseRule {refer_oDoseRule}={oDoseRule}.setLatestRecommendedInterval({refer_oDoseRule}.getLatestRecommendedInterval());
[consequence][][Aa]dd a [Pp]referable [Vv]accine {refer_oVaccine} to DoseRule {refer_oDoseRule}={refer_oDoseRule}.addPreferableVaccine({refer_oListVaccines}); 
[consequence][][Aa]dd a [Aa]llowable [Vv]accine {refer_oVaccine} to DoseRule {refer_oDoseRule}={refer_oDoseRule}.addAllowableVaccine({refer_oListVaccines}); 
[consequence][][Ss]et the [Ll]ist of [Pp]referable [Vv]accines for [Dd]oseRule {refer_oDoseRule} to {refer_oListVaccines}={refer_oDoseRule}.setPreferableVaccines({refer_oListVaccines}); 
[consequence][][Ss]et the [Ll]ist of [Aa]llowable [Vv]accines for [Dd]oseRule {refer_oDoseRule} to {refer_oListVaccines}={refer_oDoseRule}.setAllowableVaccines({refer_oListVaccines}); 
[consequence][][Aa]dd the new DoseRule {refer_oDoseRule} to the SeriesRules for [Ss]eries {refer_oTargetSeries}={refer_oTargetSeries}.addVaccineGroupDoseRule({refer_oDoseRule});
[consequence][][Rr]eplace the existing DoseRule in the [Ss]eries {refer_oTargetSeries} with {refer_oDoseRule}={refer_oTargetSeries}.modifyVaccineGroupDoseRule({refer_oDoseRule});


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Other Recommendation / Forecasting actions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

[consequence][][Mm]ark [Ff]orecasting of the [Ss]eries {refer_oTargetSeries} [Cc]omplete=modify ({refer_oTargetSeries}) \{setRecommendationStatus(RecommendationStatus.FORECASTING_COMPLETE); \}
[consequence][][Rr]efresh all [Ff]acts in the [Ss]eries {refer_oTargetSeries} for [Ff]orecasting=modify ({refer_oTargetSeries}) \{ setRecommendationStatus(RecommendationStatus.FORECASTING_IN_PROGRESS); \}
[consequence][][Ss]et the [Dd]ose [Nn]umber of [Rr]ecommendation [Ff]orecast to {nDoseNumberOfForecast} in [Ss]eries {refer_oTargetSeries}={refer_oTargetSeries}.setManuallySetDoseNumberToRecommend({nDoseNumberOfForecast});
[consequence][][Mm]ark that [Pp]ost [Pp]rocessing on the [Ff]orecast of the [Ss]eries {refer_oTargetSeries} has been [Rr]un={refer_oTargetSeries}.setPostForecastCheckCompleted(true);
[consequence][][Ss]et the [Rr]ecommended [Vv]accine for the [Ff]orecast in the [Ss]eries {refer_oTargetSeries} to {dd_oSupportedVaccineConcept}={refer_oTargetSeries}.setRecommendationVaccine(schedule.getVaccineByCdsConceptValue({dd_oSupportedVaccineConcept}));
[consequence][][Uu]nset the [Rr]ecommended [Vv]accine for the [Ff]orecast in the [Ss]eries {refer_oTargetSeries}={refer_oTargetSeries}.setRecommendationVaccine(null);
[consequence][][Ss]et [Dd]isplay [Ff]orecast [Dd]ate for [Cc]onditional [Rr]ecommendations in the [Ss]eries {refer_oTargetSeries} to [Tt]rue={refer_oTargetSeries}.setForecastDateToBeDisplayedForConditionalRecommendations(true);
[consequence][][Ss]et [Dd]isplay [Ff]orecast [Dd]ate for [Cc]onditional [Rr]ecommendations in the [Ss]eries {refer_oTargetSeries} to [Ff]alse={refer_oTargetSeries}.setForecastDateToBeDisplayedForConditionalRecommendations(false);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Logging actions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
[consequence][][Rr]ecord that this [Ss]eries [Rr]ule was [Pp]rocessed for the TargetSeries {refer_oTargetSeries}={refer_oTargetSeries}.addSeriesRuleProcessed(drools.getRule().getName());
[consequence][][Rr]ecord that this [Dd]ose [Rr]ule was [Pp]rocessed for the TargetDose {refer_oTargetDose}={refer_oTargetDose}.addDoseRuleProcessed(drools.getRule().getName());
[consequence][][Ll]og that this [Dd]ose [Rr]ule fired for the [Dd]ose {refer_oTargetDose} in the Series {refer_oTargetSeries}=ICELogicHelper.logDRLDebugMessage(drools.getRule().getName(), {refer_oTargetDose}.toString() + " in TargetSeries " + {refer_oTargetSeries}.getSeriesName());
[consequence][][Ll]og that this [Dd]ose [Rr]ule fired for the [Dd]ose {refer_oTargetDose}=ICELogicHelper.logDRLDebugMessage(drools.getRule().getName(), {refer_oTargetDose}.toString() + " in TargetSeries " + {refer_oTargetDose}.getAssociatedTargetSeries().getSeriesName());
[consequence][][Ll]og that this [Ss]eries [Rr]ule fired for the [Ss]eries {refer_oTargetSeries}=ICELogicHelper.logDRLDebugMessage(drools.getRule().getName(), "in TargetSeries " + {refer_oTargetSeries}.getSeriesName());
[consequence][][Ll]og that this [Rr]ule fired=ICELogicHelper.logDRLDebugMessage(drools.getRule().getName(), "fired");
[consequence][][Ll]og [Dd]ebugging [Ii]nformation about [Oo]bject [Nn]amed {strObjectName} and [Vv]alue {oDebugObject}=ICELogicHelper.logDRLDebugMessage("DEBUG INFORMATION for " + drools.getRule().getName(), {strObjectName} + ": " + {oDebugObject});


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// IceResult Fact Object START
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////// ***
// IceResult Fact Object condition SUBSTITUTIONS START
[condition][]where IceResult [Ff]inding is {sIceResultFinding} includes an associated TargetDose=iceResultFinding == {sIceResultFinding}, targetDose != null
///////// ***


///////// ***
// IceResult Fact Object condition WHERE clauses START
[condition][]IceResult [Ff]inding=iceResultFinding
[condition][]IceResult [Aa]dministration [Dd]ate=targetDose.administrationDate
[condition][]IceResult [Dd]iseases [Tt]argeted=targetDose.administeredVaccine.allDiseasesTargetedForImmunity
[condition][]where {attr:[A-Za-z0-9\\.\\(\\)\\ ]+} {aOp}  {value}={attr} {aOp}  {value} 
[condition][]as well as {attr} {aOp}  {value}=, {attr} {aOp}  {value}

// e.g. - // - with associated administered shot $currentShot and administration date > "blah"
//
///////// ***


///////// ***
// Primary IceResult Fact Object conditions and sub-conditions
[condition][]There exists {entity:an |another |}IceFact=exists ICEFactTypeFinding()
[condition][]There is not {entity:an |another |}IceFact=not ICEFactTypeFinding() 
[condition][]There is {entity:an |another |}IceFact {oICEFactTypeFinding}={oICEFactTypeFinding} : ICEFactTypeFinding()
[condition][]- [Tt]hat has [Ff]inding {sIceResultFinding}=iceResultFinding == {sIceResultFinding}
[condition][]- [Tt]hat has [Aa]ssociated [Ss]eries {oTargetSeries}=associatedTargetSeries == {oTargetSeries}
[condition][]- [Tt]hat has [Aa]ssociated [Aa]dministered [Ss]hot {oTargetDose} {attr}=targetDose == {oTargetDose} {attr}
[condition][]- [Tt]hat has [Aa]ssociated [Aa]dministered [Ss]hot {oTargetDose}=targetDose == {oTargetDose}
[condition][]- [Tt]here is an [Aa]ssociated [Aa]dministered [Ss]hot in the [Ss]eries {refer_oTargetSeries}=associatedTargetSeries == {refer_oTargetSeries} 
[condition][]- [Mm]ake [Nn]ote of the IceResult [Ff]inding as {assign_sIceResultFinding}={assign_sIceResultFinding} : iceResultFinding
[condition][]- [Mm]ake [Nn]ote of the [Aa]ssociated [Aa]dministered [Ss]hot as {assign_oTargetDose}={assign_oTargetDose} : targetDose, targetDose != null
[condition][]- [Mm]ake [Nn]ote of the [Aa]ssociated [Ss]eries as {assign_oTargetSeries}={assign_oTargetSeries} : associatedTargetSeries, associatedTargetSeries != null
///////// ***


///////// ***
// IceResult Fact Object Consequences START
[consequence][][Ll]ogically [Ii]nsert an IceFact {sIceResultFinding} with TargetDose {oTargetDose} into [Ww]orking [Mm]emory=insertLogical(new ICEFactTypeFinding({sIceResultFinding}, {oTargetDose}));
[consequence][][Ll]ogically [Ii]nsert an IceFact {sIceResultFinding} with TargetSeries {oTargetSeries} into [Ww]orking [Mm]emory=insertLogical(new ICEFactTypeFinding({sIceResultFinding}, {oTargetSeries}));
[consequence][][Ll]ogically [Ii]nsert an IceFact {sIceResultFinding} into [Ww]orking [Mm]emory=insertLogical(new ICEFactTypeFinding({sIceResultFinding}));
[consequence][][Ii]nsert an IceFact {sIceResultFinding} with TargetDose {oTargetDose} into [Ww]orking [Mm]emory=insert(new ICEFactTypeFinding({sIceResultFinding}, {oTargetDose}));
[consequence][][Ii]nsert an IceFact {sIceResultFinding} with TargetSeries {oTargetSeries} into [Ww]orking [Mm]emory=insert(new ICEFactTypeFinding({sIceResultFinding}, {oTargetSeries}));
[consequence][][Ii]nsert an IceFact {sIceResultFinding} into [Ww]orking [Mm]emory=insert(new ICEFactTypeFinding({sIceResultFinding}));
[consequence][][Rr]etract IceFact {oICEFactTypeFinding} from [Ww]orking [Mm]emory=retract({oICEFactTypeFinding});
///////// ***


///////// ***
// IceResult Fact Object Accumulates START
[condition][]Verify that the [Cc]ount of IceFacts \({IceResultConditions}\) is {aOp}  {nNumberOfConditions}=accumulate($irf : ICEFactTypeFinding({IceResultConditions}); $countNum: count($irf); $countNum {aOp}  {nNumberOfConditions})

// e.g. //Verify that the Count of IceResult Findings (where IceResult Finding is "yeah" and there is a TargetDose and the TargetDose administration date > "blah" and the TargetDose targets the disease "blah2") is > 5
// or e.g. //Verify that the Count of IceResult Findings (where IceResult Finding is "yeah" and there is a TargetDose with administration date > "blah" and diseases targeted contains "blah2") is > 5
///////// ***

// [condition][]where there is a [Ff]inding {sIceResultFinding} only=iceResultFinding == {sIceResultFinding}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// IceResult Fact Object END
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

