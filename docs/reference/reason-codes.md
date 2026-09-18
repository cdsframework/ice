# Reason Codes

This file contains the disease-immunity, evaluation, and recommendation
reason code systems from the ICE Implementation Guide. These substantial,
independently changing value sets are maintained separately from the main
implementation guidance and the other terminology tables.

See [Codes and Terminology](codes-and-terminology.md) for the other code
systems and the
[proposed ICE Implementation Guide](../implementation/ICE_Implementation_Guide.md)
for the request and response contexts in which reason codes are used.

## Disease Immunity Reason - Code System 2.16.840.1.113883.3.795.12.100.9

| **Code Value** | **Description** |
|----------------|-----------------|
| IS_IMMUNE      | Is Immune       |

## Evaluation Reason - Code System 2.16.840.1.113883.3.795.12.100.3

| Code Value (Returned by ICE)                                                            | Description (Returned by ICE)                                                                                                           |
|-----------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| ABOVE_MAXIMUM_AGE_VACCINE                                                               | This immunization event occurred after the specified maximum age for this vaccine.                                                      |
| ABOVE_REC_AGE_SERIES                                                                    | The vaccine is administered above the recommended age for this series.                                                                  |
| BELOW_MINIMUM_AGE_FINAL_DOSE                                                            | This patient was below the minimum age for the final dose.                                                                              |
| BELOW_MINIMUM_AGE_SERIES                                                                | This patient was below the minimum age for this dose.                                                                                   |
| BELOW_MINIMUM_AGE_VACCINE                                                               | This immunization event occurred prior to the specified minimum age for this vaccine.                                                   |
| BELOW_MINIMUM_INTERVAL                                                                  | This immunization event occurred prior to the specified minimum interval for this dose.                                                 |
| BELOW_MIN_INTERVAL_PCV_PPSV                                                             | This immunization event occurred prior to the specified minimum interval between PCV and PPSV doses.                                    |
| BELOW_REC_AGE_SERIES                                                                    | The vaccine is administered below the recommended age for this series.                                                                  |
| BOOSTER_DOSE                                                                            | The vaccine administered is a booster dose.                                                                                             |
| BOOSTER_ONLY                                                                            | The vaccine administered is invalid as a primary shot; valid only as a booster dose.                                                    |
| D_AND_T_INVALID/P_VALID                                                                 | The diphtheria and tetanus components are invalid due to minimum interval violation, pertussis component valid.                         |
| DISEASE_DOCUMENTED                                                                      | Disease Documented.                                                                                                                     |
| DUPLICATE_SAME_DAY                                                                      | This immunization event is a duplicate.                                                                                                 |
| EXTRA_DOSE                                                                              | The vaccine administered is an extra dose.                                                                                              |
| INSUFFICIENT_ANTIGEN                                                                    | This vaccine contained insufficient antigen for the patient's age.                                                                      |
| INVALID_AGE                                                                             | Invalid Age.                                                                                                                            |
| MISSING_ANTIGEN                                                                         | The vaccine administered is missing an antigen.                                                                                         |
| OUTSIDE_SEASON                                                                          | This immunization was administered outside of the vaccine season.                                                                       |
| OUTSIDE_FLU_VAC_SEASON                                                                  | This immunization was administered outside of influenza vaccine season.                                                                 |
| OUTSIDE_SERIES                                                                          | Shot Administered Outside of Defined Routine Series                                                                                     |
| OUTSIDE_ROUTINE_SERIES                                                                  | Shot Administered Outside of Defined Routine Series                                                                                     |
| PRIOR_TO_DOB                                                                            | This immunization event was recorded prior to the date of birth.                                                                        |
| PROOF_OF_IMMUNITY                                                                       | Proof of Immunity.                                                                                                                      |
| SELECT_ADJUVANT_PRODUCT_INTERVAL                                                        | This immunization event occurred prior to the specified minimum interval between adjuvant products.                                     |
| SUPPLEMENTAL_TEXT                                                                       | Supplemental text is available for this immunization event. (Note: Supplemental text is populated in the “originalText” attribute)      |
| TOO_EARLY_LIVE_VIRUS                                                                    | This immunization event occurred prior to the specified minimum interval for a live vaccine dose.                                       |
| VACCINE_NOT_MEMBER_OF_SERIES                                                            | The vaccine is not a part of this series, therefore it will not be counted towards completion of this series.                           |
| WAITING_FOR_EVALUATION                                                                  | Waiting for Evaluation                                                                                                                  |
| WRONG_GENDER                                                                            | Wrong Gender                                                                                                                            |
| VACCINE_NOT_SUPPORTED                                                                   | The vaccine administered is not supported by the ICE service.                                                                           |
| VACCINE_NOT_LICENSED_FOR_MALES                                                          | The vaccine administered is not licensed for males.                                                                                     |
| VACCINE_NOT_ALLOWED                                                                     | The vaccine administered is not allowed.                                                                                                |
| VACCINE_NOT_ALLOWED_FOR_THIS_DOSE                                                       | The vaccine administered is not allowed for this dose.                                                                                  |
| VACCINE_NOT_COUNTED_BASED_ON_MOST_RECENT_VACCINE_GIVEN | The vaccine will not be counted based on the most recent vaccine given. (Most recent vaccine given determines which series is applied.) |
| VACCINE_NOT_PART_OF_THIS_SERIES                                                         | The vaccine is not a part of this series, therefore it will not be counted towards completion of this series.                           |
| VACCINE_NOT_ALLOWED_IN_US                                                               | The vaccine is not allowed in the U.S., and therefore will be marked Invalid.                                                           |
| VACCINE_NOT_APPROVED_IN_US                                                              | The vaccine has not been approved in the U.S.                                                                                           |
| VACCINE_NOT_APPROVED_IN_US_OR_BY_WHO                                                    | The vaccine is not approved for use in the U.S. or by the WHO                                                                           |
| VACCINE_NOT_YET_AVAILABLE_ON_DATE_SPECIFIED                                             | The vaccine was not yet available on the date specified.                                                                                |

## Recommendation Reason - Code System 2.16.840.1.113883.3.795.12.100.6

| **Code Value (Returned by ICE)**     | **Description (Returned by ICE)**                                                          |
|--------------------------------------|--------------------------------------------------------------------------------------------|
| ABOVE_AGE_MAY_COMPLETE               | Above recommended age but may complete series.                                             |
| ADMINISTER_COVID19_BIVALENT_VACCINE  | Administer COVID-19 bivalent vaccine                                                       |
| ADMINISTER_PCV15_OR_PCV20            | Administer PCV15 or PCV20 vaccine                                                          |
| ADMINISTER_PCV15_OR_PCV20_OR_PCV21   | Administer PCV15, PCV20 or PCV21 vaccine                                                   |
| ADMINISTER_PCV20_OR_PCV21            | Administer PCV20 or PCV21 vaccine                                                          |
| ADMINISTER_mRNA_VACCINE              | Administer mRNA vaccine *(not used)*                                                       |
| ADMINISTER_TDAP_OR_TD                | Administer Tdap or Td                                                                      |
| BASED_ON_VAC_AVAIL_AND_PRIORITY_RECS | Based on vaccine availability or priority recommendations                                  |
| BELOW_MINIMUM_AGE_HIGH_RISK_SERIES   | Below minimum age for this high risk series.                                               |
| BOOSTER_DOSE                         | A booster dose is recommended                                                              |
| CLINICAL_PATIENT_DISCRETION          | Clinical/Patient Discretion                                                                |
| COMPLETE                             | Completed vaccine series.                                                                  |
| COMPLETE_HIGH_RISK                   | Series complete, unless high risk.                                                         |
| DISEASE_DOCUMENTED                   | Disease Documented.                                                                        |
| DUE_IN_FUTURE                        | Due in the Future.                                                                         |
| DUE_NOW                              | Due Now.                                                                                   |
| HIGH_RISK                            | Recommended for high-risk groups.                                                          |
| NOT_SPECIFIED                        | Not Specified.                                                                             |
| OTHER_VACCINE_PRODUCT_POSSIBLE       | In addition to the vaccine product recommended, there are other vaccine products possible. |
| PROOF_OF_IMMUNITY                    | Proof of Immunity.                                                                         |
| SUPPLEMENTAL_TEXT                    | Supplemental Text provided in “originalText” attribute                                     |
| TOO_OLD                              | Vaccine not recommended at this age.                                                       |
| TOO_OLD_HIGH_RISK                    | Vaccine not generally recommended at this age, unless high risk.                           |
| TOO_OLD_TO_INITIATE                  | Vaccine not recommended at this age; too old to initiate.                                  |
| VAC_GROUP_NO_LONGER_REC              | This vaccine group is no longer recommended.                                               |
| NOT_SUPPORTED                        | The shots in the recommendation group were not evaluated.                                  |
