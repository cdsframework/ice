# Disease and Disease Immunity Codes

**Applicability:** ICE SOAP/vMR API disease-immunity inputs, including disease codes, value codes, and reason codes.

The disease-immunity value and reason codes below are used together. The disease codes identify the disease reported as disease
immunity in vMR. ICE FHIR API (FHIR R6) clients send a supported IVCI contextual condition instead;
see [Contextual Conditions](contextual-conditions.md). For SNOMED CT disease codes used in FHIR `targetDisease` response elements,
see [FHIR Target Diseases](target-diseases.md). The FHIR disease-immunity source and reason CodeSystems are maintained in [
`disease-immunity-source.json`](../../fhir/ice/disease-immunity-source.json) and [
`disease-immunity-reason.json`](../../fhir/ice/disease-immunity-reason.json).

## Disease Codes

### ICD-9-CM — Code System 2.16.840.1.113883.6.103

| **Code Value** | **Description** |
|----------------|-----------------|
| 070.1          | Hep A           |
| 070.30         | Hep B           |
| 055.9          | Measles         |
| 072.9          | Mumps           |
| 056.9          | Rubella         |
| 052.9          | Varicella       |

### ICD-10-CM — Code System 2.16.840.1.113883.6.90

| **Code Value** | **Description**                                    |
|----------------|----------------------------------------------------|
| B15.9          | Hepatitis A without hepatic coma                   |
| B19.10         | Unspecified viral hepatitis B without hepatic coma |
| B05.9          | Measles without complication                       |
| B26.9          | Mumps without complication                         |
| B06.9          | Rubella without complication                       |
| B01.9          | Varicella without complication                     |

### SNOMED CT — Code System 2.16.840.1.113883.6.96

| **Code Value** | **Description**                |
|----------------|--------------------------------|
| 278971009      | Serology confirmed hepatitis A |
| 271511000      | Serology confirmed hepatitis B |
| 371111005      | Serology confirmed Measles     |
| 371112003      | Serology confirmed Mumps       |
| 278968001      | Serology confirmed Rubella     |
| 371113008      | Serology confirmed Varicella   |
| 38907003       | History of Varicella infection |

## Disease Immunity Values and Reasons

## Disease Immunity Value - Code System 2.16.840.1.113883.3.795.12.100.8

| **Code Value**     | **Description**    |
|--------------------|--------------------|
| DISEASE_DOCUMENTED | Disease Documented |
| PROOF_OF_IMMUNITY  | Proof of Immunity  |

## Disease Immunity Reason - Code System 2.16.840.1.113883.3.795.12.100.9

| **Code Value** | **Description** |
|----------------|-----------------|
| IS_IMMUNE      | Is Immune       |
