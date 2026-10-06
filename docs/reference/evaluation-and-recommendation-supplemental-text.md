# Evaluation and Recommendation Supplemental Text

This page lists supplemental evaluation and recommendation text code values and descriptions used by ICE. For the response forms and the `LEGACY`, `CODED`, and `BOTH` output modes, see [Output Configuration](../configuration/output-configuration.md#supplemental-text).

The generic `SUPPLEMENTAL_TEXT` reason is documented with the [Evaluation and Recommendation Reason Codes](evaluation-and-recommendation-reasons.md). The codes below identify specific supplemental text.

## Evaluation Supplemental Text

**ICE SOAP/vMR CodeSystem:** `2.16.840.1.113883.3.795.12.100.51`  
**ICE FHIR CodeSystem:** [`supplemental-evaluation-reason.json`](../../fhir/ice/supplemental-evaluation-reason.json)

| Code Value | Description | Vaccine Group |
|---|---|---|
| `COVID19_MIN_INTERVAL_8W_2ND_BOOSTER` | The interval between this dose and the prior shot is less than the minimum interval. The dose does not need to be repeated unless the patient is severely immunosuppressed, in which case the repeated dose should be administered after the dose given in error by at least 28 days.”<br>Patients who previously completed a 3-dose monovalent primary series are recommended to receive 1 bivalent booster dose, at least 8 weeks after completion of the monovalent primary series." | COVID-19 Pandemic |
| `COVID19_MIN_INTERVAL_8W` | The timing of the administration of this shot does not follow the guidelines regarding the minimum interval of 8 weeks | COVID-19 Pandemic |
| `COVID19_MIN_INTERVAL_4M` | The timing of the administration of this shot does not follow the guidelines regarding the minimum interval of 4 months. | COVID-19 Pandemic |
| `COVID19_MIN_INTERVAL_8W_1ST_BOOSTER` | The timing of the administration of this shot does not follow the guidelines regarding the minimum interval of 8 weeks required for the 1st Booster Dose. | COVID-19 Pandemic |
| `COVID19_MIN_INTERVAL_5M_1ST_BOOSTER` | The timing of the administration of this shot does not follow the guidelines regarding the minimum interval of 5 months required for the 1st Booster Dose. | COVID-19 Pandemic |
| `COVID19_MIN_INTERVAL_4M_2ND_BOOSTER` | The timing of the administration of this shot does not follow the guidelines regarding the minimum interval of 4 months required for the 2nd Booster Dose. | COVID-19 Pandemic |
| `COVID19_MIN_AGE` | The timing of the administration of this shot does not follow the guidelines regarding the minimum age. | COVID-19 Pandemic |
| `COVID19_MIN_AGE` | The timing of the administration of this shot does not follow the guidelines regarding the minimum age and/or minimum interval. | COVID-19 Pandemic |
| `DTP_PERTUSSIS_NEEDED` | Pertussis is needed to complete the series. | DTP |
| `DTP_DT_AGE_CONTRAINDICATION` | DT should only be administered to children 6 weeks through 6 years of age with a contraindication to pertussis vaccine. | DTP |
| `MENB_SAME_DAY_DIFFERENT_PRODUCTS_UNDETERMINED` | The patient record indicates that different Meningococcal B products were administered on the same day. Based on the available information, the product administered is undetermined and therefore unable to be evaluated | Meningococcal B |
| `PNEUMOCOCCAL_UNSPECIFIED_CVX` | CVX 109 and CVX 152 are unspecified vaccine formulations. Please update this record with the specific vaccine administered so that the shot can be evaluated accurately and the next dose in the series can be recommended. | Pneumococcal |
| `POLIO_CVX_89` | CVX 89 represents an unspecified formulation vaccine. Trivalent oral polio vaccine ceased to be used globally in April 2016. If the patient received bivalent or monovalent oral polio vaccine (bOPV or mOPV), additional protection is needed. | Polio |
| `DTP_DT_NO_LONGER_AVAILABLE_AS_OF_20230501_ASSUMED_DTAP` | Since DT is no longer available as of 5/1/2023, this shot is assumed to be a DTaP. Please check your records. | DTP |
| `ZOSTAVAX_UNAVAILABLE_US` | Zostavax (CVX 121) has been unavailable in the United States since November 18, 2020. Please verify the vaccine code. | Zoster |
| `MPOX_MIN_INTERVAL_28D` | The interval between this dose and the prior shot is less than the minimum interval. The dose does not need to be repeated unless the patient is severely immunosuppressed, in which case the repeated dose should be administered after the dose given in error by at least 28 days.”<br>Patients who previously completed a 3-dose monovalent primary series are recommended to receive 1 bivalent booster dose, at least 8 weeks after completion of the monovalent primary series." | Mpox |

## Recommendation Supplemental Text

**ICE SOAP/vMR CodeSystem:** `2.16.840.1.113883.3.795.12.100.50`  
**ICE FHIR CodeSystem:** [`supplemental-recommendation-reason.json`](../../fhir/ice/supplemental-recommendation-reason.json)

| Code Value | Description | Vaccine Group |
|---|---|---|
| `TARGET_DOSE1_NOVAVAX_3W_OTHERS_8_12W` | The interval to target dose 1 depends on the patient's prior history and product to be used. If the last shot was an updated Novavax, Novavax can be administered in 3 weeks (as long as the patient is 12 years of age). If the last shot was not Novavax, administer at an interval of 8 weeks (for administration of Comirnaty, Novavax, or Spikevax) or 12 weeks (for administration of mNEXSPIKE). | COVID-19 Seasonal |
| `TARGET_DOSE1_NOVAVAX_3W_OTHERS_8_12W_V2` | The interval to target dose 1 depends on the patient's prior history and product to be used. If the last shot was an updated Novavax, Novavax can be administered in 3 weeks. If the last shot was not Novavax, administer at an interval of 8 weeks (for administration of Comirnaty, Novavax, or Spikevax) or 12 weeks (for administration of mNEXSPIKE). | COVID-19 Seasonal |
| `TARGET_DOSE2_6MO_MIN_8_12W_BY_PRODUCT` | The recommended interval to target dose 2 is 6 months. The minimum interval to target dose 2 depends on the product to be used. For administration of Comirnaty, Novavax, or Spikevax, minimum interval = 8 weeks. For administration of mNEXSPIKE, minimum interval = 12 weeks. | COVID-19 Seasonal |
| `SUPPLEMENTAL_TEXT_ADMINISTER_TDAP_OR_TD` | Administer either Tdap or Td. | DTP |
| `MATERNAL_UNK_OR_WITHIN_14D_RSV_MAB` | If the mother of the patient was not vaccinated during her pregnancy, her vaccination status is unknown, or she was vaccinated within 14 days of the patient date of birth, then administer RSV mAb dose based on patient weight shortly before or during the RSV season. | RSV |
| `RSV_75PLUS_50_74_AT_RISK_SINGLE_DOSE` | The RSV evaluations and recommendations in ICE primarily target the routine series. RSV is recommended as a single lifetime dose for all adults 75 years of age and older and adults 50 through 74 years of age and older who are at increased risk of severe RSV disease. Note: RSV vaccine is also recommended for pregnant persons at 32 through 36 weeks gestation, using seasonal administration. | RSV |
| `RSV_75PLUS_50_74_AT_RISK` | The RSV evaluations and recommendations in ICE primarily target the routine series. RSV is recommended as a single lifetime dose for all adults 75 years of age and older and adults 50 through 74 years of age who are at increased risk of severe RSV disease. | RSV |
| `ADULT_RISK_PCV20_21_OR_PCV15_THEN_PPSV23` | The Pneumococcal evaluations and recommendations in ICE primarily target the routine series. Adults ages 19-49 with underlying medical conditions or other risk factors who have not previously received PCV or whose previous vaccination history is unknown should receive 1 dose of PCV (either PCV20, PCV21 or PCV15). When PCV15 is used, it should be followed by a dose of PPSV23 at the appropriate interval(s). Patients who previously received a PCV13 should receive PCV20 or PCV21. Refer to ACIP for additional information. | Pneumococcal |
| `IF_NO_PPSV23_USE_PCV20_OR_PCV21_STOP` | If PPSV23 is unavailable, PCV20 or PCV21 may be administered. No further doses of any type of pneumococcal vaccine are recommended after PCV20 or PCV21 is given. | Pneumococcal |
| `PCV15_OR_PCV20` | Administer either PCV15 or PCV20. | Pneumococcal |
| `PCV20_OR_PCV21_SCDM_AFTER_5Y` | Consider administering PCV20 or PCV21 based on Shared Clinical Decision-Making (SCDM), at least 5 years after the most recent dose of pneumococcal vaccine. | Pneumococcal |
| `CHOLERA_NOT_ROUTINE_SEE_ACIP` | Cholera vaccination is not routinely recommended in the U.S. and only indicated for select patients at increased risk. Refer to ACIP and the CDC Yellow Book: Health Information for International Travel for additional information. | Cholera |
| `JE_NOT_ROUTINE_ACCEL_18_65_SEE_ACIP` | Japanese Encephalitis vaccination is not routinely recommended in the U.S. and only indicated for select patients at increased risk. The only age group for which an accelerated schedule is approved is 18-65 years old. Refer to ACIP and the CDC Yellow Book: Health Information for International Travel for additional information. | Japanese Encephalitis |
| `TYPHOID_NOT_ROUTINE_SEE_ACIP` | Typhoid vaccination is not routinely recommended in the U.S. and is only indicated for select patients at increased risk. Refer to ACIP and the CDC Yellow Book: Health Information for International Travel for additional information. | Typhoid |
| `YELLOW_FEVER_LIVE_MIN_INTERVALS_SEE_ACIP` | Yellow fever vaccination is not routinely recommended in the U.S. and is only indicated for select patients at increased risk. Yellow fever is a live vaccine; a 28-day interval is needed between a yellow fever vaccine and a live vaccine from another vaccine group (30 days is needed for BCG). Refer to ACIP and the CDC Yellow Book: Health Information for International Travel for additional information. | Yellow Fever |
| `POLIO_ASSUME_VACCINATED` | Unless there are specific reasons to believe they were not vaccinated, most adults who were born and raised in the United States can assume they were vaccinated against polio as children. Refer to ACIP for additional information. | Polio |
| `POLIO_COMPLETE_HIGH_RISK` | Adults who completed their polio vaccination but who are at increased risk of exposure to poliovirus may receive one lifetime IPV booster. Refer to ACIP for additional information. | Polio |
| `HEP_B_3DOSE_TWINRIX_ALT_VACCINE` | If the Twinrix vaccine (CVX 104) is not available, another adult Hep B vaccine may be administered. | Hep B |
| `HEP_A_3DOSE_TWINRIX_ALT_VACCINE` | If the Twinrix vaccine (CVX 104) is not available, another age-appropriate Hep A vaccine may be administered. | Hep A |
| `HPV_NOT_ROUTINE_27_THROUGH_45` | HPV is not routinely recommended for all patients aged 27 through 45 years. Use Shared Clinical Decision-Making to identify individuals who may benefit. | HPV |
| `MENINGB_RISK_BASED_RECOMMENDATION` | Meningococcal B is a risk-based recommendation for patients age 10 years and older and a shared clinical decision-making recommendation for healthy patients 16–23 years of age (preferred age 16-18 years). | Meningococcal B |
| `MENINGB_COMPLETE_HIGH_RISK` | Persons who have completed a MenB vaccine primary series and who remain or become at increased risk for invasive meningococcal disease are recommended to receive booster vaccination. Booster doses need to be from the same manufacturer used for doses in the primary series. | Meningococcal B |
| `POST_3DOSE_MONO_BIVALENT_BOOSTER_8W` | Patients who previously completed a 3-dose monovalent primary series are recommended to receive 1 bivalent booster dose, at least 8 weeks after completion of the monovalent primary series. | COVID-19 Pandemic |
