package org.cdsframework.ice.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.cdsframework.ice.util.TimePeriod;

public interface TargetSeriesInterface {

	public boolean containsRuleProcessed(String ruleName);

	public boolean containsTargetDose(TargetDose td);

	public SeriesRules getSeriesRules();

	public int determineDoseNumberInSeries();

	public int determineEffectiveNumberOfDosesInSeriesByDate(Date asOfDate, boolean includeShotsOnDate);

	public int determineNumberOfDosesAdministeredInSeries();

	public int determineNumberOfDosesAdministeredInSeriesByDate(Date asOfDate, boolean includeShotsOnDate);

	public Date getAdministrationDateOfTargetDoseByShotNumberNumber(int shotNumber);

	public TimePeriod getAbsoluteMinimunIntervalForTargetDose(int doseNumber);

	public String getAbsoluteMinimumIntervalForTargetDoseInStringFormat(int doseNumber);

	public TimePeriod getAbsoluteMinimumAgeForTargetDose(int doseNumber);

	public String getAbsoluteMinimumAgeForTargetDoseInStringFormat(int doseNumber);

	public Map<String, Integer> getAllEvaluationValidityCountsByDisease();

	public Map<String, Integer> getEvaluationValidityCountsByDiseasesSpecified(List<String> pDiseasesOfInterest);

	public List<Vaccine> getAllPermittedVaccinesForTargetDose(int doseNumber);

	public List<Vaccine> getAllowableVaccinesForTargetDose(int doseNumber);

	public List<Vaccine> getPreferableVaccinesForTargetDose(int doseNumber);

	public Collection<String> getDiseasesSupportedByThisSeries();

	public int getDoseAfterWhichSeriesWasMarkedComplete();

	public int getDoseNumberToRecommend();

	public List<Date> getLiveVirusDatesAccountedForInRecommendedFinalEarliestDate();

	public List<Date> getLiveVirusDatesAccountedForInRecommendedFinalDate();

	public List<Date> getAdjuvantDatesAccountedForInRecommendedFinalEarliestDate();

	public List<Date> getAdjuvantDatesAccountedForInRecommendedFinalDate();

	public int getManuallySetDoseNumberToRecommend();

	public int getNumberOfDosesInSeries();

	public int getNumberOfShotsAdministeredInSeriesExcludingDuplicateShotsOnTheSameDay();

	public int getNumberOfShotsAdministeredInSeries();

	public Date getSeasonStartDate();

	public Date getSeasonEndDate();

	public Date getOffSeasonStartDate();

	public Date getOffSeasonEndDate();

	public boolean targetSeasonesExists();

	public boolean getPerformPostForecastCheck();

	public TimePeriod getRecommendedAgeForTargetDose(int doseNumber);

	public TimePeriod getRecommendedIntervalForTargetDose(int doseNumber);

	public Date getFinalEarliestDate();

	public Date getFinalRecommendationDate();

	public Date getFinalOverdueDate();

	public List<Recommendation> getFinalRecommendations();

	public RecommendationStatus getRecommendationStatus();

	public Vaccine getRecommendationVaccine();

	public Schedule getScheduleBackingSeries();

	public String getSeriesName();

	public List<String> getSeriesRulesProcessed();

	public TargetDose getTargetDoseByAdministeredShotNumber(int doseNumber);

	public String getTargetSeriesIdentifier();

	public String getVaccineGroup();

	public boolean isAllowableVaccineForDoseRule(Vaccine v, int doseNumber);

	public boolean isHistoryEvaluationInitiated();

	public boolean isImmunityToAllDiseasesRecorded();

	public boolean isForecastDateDisplayedForConditionalRecommendations();

	public boolean isPostForecastCheckCompleted();

	public boolean isSeriesComplete();

	public boolean isSelectedSeries();

	/////////////////////

	public void addAdjuvantDateAccountedForInRecommendedFinalEarliestDate(Date pAdjuvantDate);

	public void addAdjuvantDateAccountedForInRecommendedFinalDate(Date pAdjuvantDate);

	public void addLiveVirusDateAccountedForInRecommendedFinalEarliestDate(Date pLiveVirusDate);

	public void addLiveVirusDateAccountedForInRecommendedFinalDate(Date pLiveVirusDate);

	public void addSkipDoseEntryForSpecifiedDisease(int doseNumberToSkipFrom, int doseNumberToSkipTo);

	public void addSkipDoseEntryForSpecifiedDisease(int doseNumberToSkipFrom, int doseNumberToSkipTo, String disease);

	public void addSeriesRuleProcessed(String ruleName);

	public boolean adjustRecommendationStatusAndReasonByEvalTime(Date evalTime);

	public void convertToSpecifiedSeries(String seriesToConvertTo, int doseNumberFromWhichToBeginSwitch, boolean useDoseIntervalOfPriorDoseFromSwitchToSeries);

	public void clearRecommendations();

	public void removeTargetDoseFromSeries(TargetDose td);

	public void setFinalEarliestDate(Date finalEarliestDate);

	public void setFinalRecommendationDate(Date pFinalRecommendationDate);

	public void setFinalOverdueDate(Date finalLatestRecommendationDate);

	public void setForecastDateToBeDisplayedForConditionalRecommendations(boolean yesno);

	public void setHistoryEvaluationInitiated(boolean truefalse);

	public void setImmunityToAllDiseasesRecorded(boolean immunityToAllDiseasesRecorded);

	public void setManuallySetAccountForLiveVirusIntervalsInRecommendation(boolean yesno);

	public void setManuallySetDoseNumberToRecommend(int doseNumberToRecommend);

	public void setPostForecastCheckCompleted(boolean postForecastCheckCompleted);

	public void setRecommendationStatus(RecommendationStatus recommendationStatus);

	public void setRecommendationVaccine(Vaccine recommendationVaccine);

	public void setSeriesComplete(boolean pSeriesComplete);
}
