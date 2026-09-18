# Codes and Terminology

This file contains the code systems and terminology tables used in ICE input
and output messages, separated from the main guide so they can be maintained
independently. The source Implementation Guide describes the SOAP/vMR
interface; it does not provide corresponding FHIR mappings for these tables.

Vaccine CVX codes and vaccine-group mappings are in
[Supported Vaccines](supported-vaccines.md). Disease-immunity, evaluation,
and recommendation reasons are in [Reason Codes](reason-codes.md).

See the [proposed ICE Implementation Guide](../implementation/ICE_Implementation_Guide.md)
for message structure and integration guidance.

## HL7 Administrative Gender - Code System 2.16.840.1.113883.5.1

| **Code Value** | **Description** |
|----------------|-----------------|
| F              | Female          |
| M              | Male            |

## SNOMED - Code System 2.16.840.1.113883.6.5

| **Code Value** | **Description**                                 |
|----------------|-------------------------------------------------|
| 384810002      | Immunization/vaccination management (procedure) |

## Disease Immunity Value - Code System 2.16.840.1.113883.3.795.12.100.8

| **Code Value**     | **Description**    |
|--------------------|--------------------|
| DISEASE_DOCUMENTED | Disease Documented |
| PROOF_OF_IMMUNITY  | Proof of Immunity  |

## Disease - Code System 2.16.840.1.113883.6.103

When sending up disease immunity as per below codes to ICE, use the new code system specified in the below table for ICD-9-CM, or
one of the next two sections.

| **Code Value** | **Description** |
|----------------|-----------------|
| 070.1          | Hep A           |
| 070.30         | Hep B           |
| 055.9          | Measles         |
| 072.9          | Mumps           |
| 056.9          | Rubella         |
| 052.9          | Varicella       |

## Disease – Code System 2.16.840.1.113883.6.90

When sending up disease immunity as per below codes to ICE, use the new code system specified below for ICD-10-CM.

| **Code Value** | **Description**                                    |
|----------------|----------------------------------------------------|
| B15.9          | Hepatitis A without hepatic coma                   |
| B19.10         | Unspecified viral hepatitis B without hepatic coma |
| B05.9          | Measles without complication                       |
| B26.9          | Mumps without complication                         |
| B06.9          | Rubella without complication                       |
| B01.9          | Varicella without complication                     |

## Disease – Code System 2.16.840.1.113883.6.96

When sending up disease immunity as per below codes to ICE, use the new code system specified below for SNOMED-CT.

| **Code Value** | **Description**                |
|----------------|--------------------------------|
| 278971009      | Serology confirmed hepatitis A |
| 271511000      | Serology confirmed hepatitis B |
| 371111005      | Serology confirmed Measles     |
| 371112003      | Serology confirmed Mumps       |
| 278968001      | Serology confirmed Rubella     |
| 371113008      | Serology confirmed Varicella   |
| 38907003       | History of Varicella infection |

## Evaluation Validity - Code System 2.16.840.1.113883.3.795.12.100.2

| **Code Value** | **Description**       |
|----------------|-----------------------|
| VALID          | Valid Immunization    |
| ACCEPTED       | Accepted Immunization |
| INVALID        | Invalid Immunization  |
| IGNORE         | Ignore Immunization   |
| NOT_EVALUATED  | Shot Not Evaluated    |

## Evaluation Focus (Vaccine Group) - Code System 2.16.840.1.113883.3.795.12.100.1

| **Code Value** | **Description**                    |
|----------------|------------------------------------|
| 100            | Hep B Vaccine Group                |
| 810            | Hep A Vaccine Group                |
| 200            | DTP Vaccine Group                  |
| 300            | Hib Vaccine Group                  |
| 400            | Polio Vaccine Group                |
| 500            | MMR Vaccine Group                  |
| 600            | Varicella Vaccine Group            |
| 620            | Zoster Vaccine Group               |
| 750            | Pneumococcal Vaccine Group         |
| 800            | Influenza                          |
| 820            | Rotavirus Vaccine Group            |
| 830            | Meningococcal Vaccine Group        |
| 835            | Meningococcal B Vaccine Group      |
| 840            | Human Papillomavirus Vaccine Group |
| 850            | COVID-19 Vaccine Group             |
| 890            | H1N1 Influenza                     |
| 999            | “Other” Vaccine Group              |

## Recommendation Value - Code System 2.16.840.1.113883.3.795.12.100.5

| **Code Value (Returned by ICE)** | **Description (Returned by ICE)**                                                    |
|----------------------------------|--------------------------------------------------------------------------------------|
| RECOMMENDED                      | Recommended                                                                          |
| CONDITIONAL                      | Conditionally Recommended                                                            |
| FUTURE_RECOMMENDED               | Recommended in the Future                                                            |
| NOT_RECOMMENDED                  | Not Recommended                                                                      |
| RECOMMENDATION_NOT_AVAILABLE     | Recommendation Not Available (*e.g.* - ICE did not forecast for unsupported vaccine) |

## Recommendation Focus (Vaccine Group) - Code System 2.16.840.1.113883.3.795.12.100.1

| **Code Value** | **Description**                    |
|----------------|------------------------------------|
| 100            | Hep B Vaccine Group                |
| 810            | Hep A Vaccine Group                |
| 200            | DTP Vaccine Group                  |
| 300            | Hib Vaccine Group                  |
| 400            | Polio Vaccine Group                |
| 500            | MMR Vaccine Group                  |
| 600            | Varicella Vaccine Group            |
| 620            | Zoster Vaccine Group               |
| 750            | Pneumococcal Vaccine Group         |
| 800            | Influenza                          |
| 820            | Rotavirus Vaccine Group            |
| 830            | Meningococcal Vaccine Group        |
| 835            | Meningococcal B Vaccine Group      |
| 840            | Human Papillomavirus Vaccine Group |
| 850            | COVID-19 Vaccine Group             |
| 890            | H1N1 Influenza                     |
| 999            | “Other” Vaccine Group              |

## Reason code systems

See [Reason Codes](reason-codes.md) for the separately maintained
disease-immunity, evaluation, and recommendation reason code systems.
