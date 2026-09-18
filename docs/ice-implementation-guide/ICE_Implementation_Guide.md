# ICE Implementation Guide

**Immunization Calculation Engine (ICE)**  
Implementation Guide for Integrating with ICE

**ICE versions 2.58**  
**August 18th, 2026**

# ICE Documentation

The most complete source of information about the Immunization Calculation Engine (ICE) is the official ICE Wiki which is publicly
accessible at: [https://www.cdsframework.org](http://cdsframework.org/). Any future releases of this document will be posted on that
website.

In addition, HLN Consulting, LLC publishes some ICE information at: [https://www.hln.com/ice](https://www.hln.com/ice).

# Purpose of this Document

The purpose of this document is to describe what steps must be taken for a client application to invoke the ICE Web Service. This
document describes the format of the data that must be passed to the service, and the format of the data that is passed back from
the service. This document also provides guidance on how to interpret the information populated in the message structure.

Note that the code systems and code values specified in this document are specific to the default configuration of ICE (i.e. the
“out of the box” rules that are shipped with ICE). An ICE deployment that has been configured with different rules might use
different code systems and values, but the structure of the ICE messages will stay the same.

# ICE Overview

The **Immunization Calculation Engine**  (**ICE**) is a state-of-the-art open-source software system that provides clinical decision
support for immunizations (CDSi), commonly referred to as "immunization forecasting".

Organizations may freely adopt ICE due to its open source license and complete lack of dependence on any commercial software. The
ICE software system has been publicly released as an open-source software system, under the GNU Lesser General Public License v3
(LGPL v3). Through its standards-based Web Service interface, ICE easily integrates with third party clinical information systems
such as electronic health record systems (EHR-S), patient portals, immunization information systems (IIS), school health systems,
and health information exchanges (HIEs) - regardless of their software architecture (.NET, Java, or other). Because of ICE’s
Java-based implementation, it can be deployed in diverse technical environments.

The ICE software system has been developed and configured by a collaborative partnership of public health and information technology
experts from the New York City Department of Health and Mental Hygiene, Citywide Immunization Registry (CIR); HLN Consulting, LLC;
the Alabama Department of Public Health (ADPH); and the OpenCDS collaboration led by researchers at the University of Utah,
Department of Biomedical Informatics.

The ICE Web Service has been implemented as a clinical module within **OpenCDS**, an open-source software framework that provides
developers with a set of tools for implementing clinical decision support services. More information about OpenCDS can be found
at: [http://www.opencds.org](http://www.opencds.org).

ICE comes pre-configured with the childhood, adolescent, and adult immunization schedules for routinely administered vaccine groups.
The pre-configured ICE rules are thoroughly documented on the publicly accessible ICE Wiki
at: https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352468/Default+Immunization+Schedule. These rules are based on the
recommendations of the Advisory Committee on Immunization Practices (ACIP) as interpreted by a team of subject matter experts from
the CIR, ADPH, and HLN.

# Communicating with the ICE Service

Clinical information systems may utilize ICE by making SOAP Web Service calls to the ICE Web Service. The interface of the ICE Web
Service conforms to the Decision Support Service (DSS) standard which specifies the technical capabilities and interfaces of a
decision support service. The DSS standard has been specified by both HL7 International and the Object Management Group (OMG).

To communicate with the ICE Web Service, clinical information systems must send and receive XML-formatted data that conforms to the
Virtual Medical Record (vMR) standard. The vMR standard was developed by the HL7 Clinical Decision Support Workgroup and is a data
model and message specification format for representing clinical data relevant to a clinical decision support service. The workgroup
strived to develop as flexible of a format as possible by drawing upon the collective CDS expertise of its members, an examination
of the data requirements of 20 CDS systems across 4 nations, as well as applicable HL7 standards that already existed.

HLN chose the vMR specification for ICE’s inputs and outputs in order to support the project’s overarching goal of enabling
non-technical subject matter experts to create and maintain immunization evaluation and forecasting rules without the assistance of
a software developer. If new data elements are ever needed to support new types of rules, the vMR should be able to support this. In
addition, the vMR standard continues to be actively worked on and updated by the HL7 community, enabling new and better ways of
representing clinical information in a standardized format.

Below is a high level summary of the inputs and outputs to the ICE Web Service. The inputs and outputs are specified in much greater
detail throughout the remainder of this document.

**Inputs**

- Date of birth

- Gender

- Immunization history

- Disease indicators

- Identification of which ICE rule set to utilize (ICE comes pre-configured with one rule set)

- Date of evaluation

**Outputs**

- Evaluation of each dose in the immunization history

- Reason for evaluation

- Recommendation for each vaccine group

- Reason for recommendation

- If configured, number of doses remaining in the series

## Invoking ICE as a Decision Support Service

Client applications invoke the ICE service by way of SOAP method calls conforming to the Decision Support Service (DSS) standards.

Although OpenCDS itself implements several DSS operations, ICE currently only makes use of two operations within the Evaluate
Interface: evaluate and evaluateAtSpecifiedTime. Callers should use evaluate if they would like ICE to evaluate the immunizations
and make recommendations based on the current date, and use evaluateAtSpecifiedTime if they would like ICE to evaluate and recommend
with respect to a specified date. The ICE TestManager tool always utilizes the latter operation. In the case that forecasting should
occur with respect to today’s date, the TestManager simply specifies today’s date.

When constructing the SOAP invocation request using the evaluateAtSpecifiedTime operation, the following are the WSDL and SOAP
action parameters:

- Service is “DecisionSupportService”

- Port is “evaluate”

- Operation is “evaluateAtSpecifiedTime”

- URL is [location of the ICE3 service]. The exact URL will vary depending on your application server software and where you install
  ICE. As an example, if ICE is unpacked as opencds-decision-support-service in Tomcat’s webapp directory, the URL is simply
  “http://`<hostname>`/opencds-decision-support-service/evaluate”

- SOAP action is “http://www.omg.org/spec/CDSS/201105/dssWsdl:operation:evaluateAtSpecifiedTime”

In the DSS request, it is necessary to tell ICE which immunization schedule should be used. Since only one immunization schedule has
been configured at this point, specify the following attributes for the <kmEvaluationRequest><kmId> node:

- scopingEntityId=“org.nyc.cir”

- businessId=“ICE”

- version=“1.0.0”

In the DSS request, it is necessary to tell ICE which version of the VMR message format to use. Specify the below attribute values
for the <kmEvaluationRequest><dataRequirementItemData><data><informationModelSSId> node:

- scopingEntityId=“org.opencds.vmr”

- businessId=“VMR”

- version=“1.0”

In the DSS request, base64 encode the contents of the VMR message within
`<kmEvaluationRequest>``<dataRequirementItemData>``<data>``<base64EncodedPayload>`.

Following the above guidelines, a complete SOAP request will look like the following:

```xml
<?xml version='1.0' encoding='UTF-8'?>

<S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope">

    <S:Body>

        <ns2:evaluateAtSpecifiedTime xmlns:ns2="http://www.omg.org/spec/CDSS/201105/dss">

            <interactionId scopingEntityId="gov.nyc.health" interactionId="123456"/>

            <specifiedTime>2012-01-14T00:00:00.000-05:00</specifiedTime>

            <evaluationRequest clientLanguage="" clientTimeZoneOffset="">

                <kmEvaluationRequest>

                    <kmId scopingEntityId="org.nyc.cir" businessId="ICE" version="1.0.0"/>

                </kmEvaluationRequest>

                <dataRequirementItemData>

                    <driId itemId="cdsPayload">

                        <containingEntityId scopingEntityId="gov.nyc.health" businessId="ICEData" version="1.0.0.0"/>

                    </driId>

                    <data>

                        <informationModelSSId scopingEntityId="org.opencds.vmr" businessId="VMR" version="1.0"/>
                        <base64EncodedPayload>**BASE64_ENCODED_VMR_MESSAGE**</base64EncodedPayload>

                    </data>

                </dataRequirementItemData>

            </evaluationRequest>

        </ns2:evaluateAtSpecifiedTime>

    </S:Body>

</S:Envelope>
```

## Virtual Medical Record Format (VMR)

All messages to and from the ICE service conform to version 1.0 of the vMR. The vMR 1.0 XML Schema Definition files are required for
client application development. These XSD files can be downloaded from the ICE Wiki’s Technical Documentation page, which is at the
following
URL: [https://cdsframework.atlassian.net/wiki/display/CDSF/Technical+Documentation](https://cdsframework.atlassian.net/wiki/display/CDSF/Technical+Documentation).
In addition, there are links on this page to sample clients for interacting with the ICE Web Service. The sample clients are written
in Java and C# and are a good starting point for writing your own ICE client. (The source code projects are stored in a Bitbucket
repository and they also include the aforementioned XSD files.)

This document describes the aspects of the vMR that are relevant to ICE, including identifying essential ICE data elements and
vocabulary. It should be all that’s needed to successfully interface with the ICE Web Service. The implementer may also find it
useful to import the vMR XSD files into an XML editor to understand the general structure of the vMR input and output messages, or
to review the vMR Domain Analysis Model. The Domain Analysis Model is also available for download from the Technical Documentation
page of the ICE Wiki.

## ICE Input Message

The vMR input message must specify basic demographic information about the patient and his or her immunization history. The
demographic information consists of the patient’s birthdate and gender. The immunization history consists of the complete set of
shots administered to the patient during his/her lifetime, and a record (if any) of disease immunity for that patient.

The ICE-specific immunization input message conforms to the cdsInput.xsd, and the XML template on the next page.

- XML messages must follow the ordering and structure of this template. The order of elements should not deviate from the template.

- Wherever there is a `<root/>` element, **the ID supplied** **must be unique** and **cannot be repeated** for any other `<root/>`
  element in the message.

- Some elements may not be present in all messages or may repeat, as described in the comments of the template as well as in the
  XSD.

- Wherever a code system value, templateId value, or other value is specified in this template, that same value must be used at that
  location for *all* messages sent to the service.

- Values in set brackets (*i.e.* – “{..}”) must be supplied by the calling application.

- Refer to the [Input Node Elements and Attributes Section](#input-node-elements-and-attributes)

### Input Message Format

```xml
<!-- **Message Begins** -->
```

> `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>`
>
> `<!-- **CDSInput Section Begins (mandatory)** -->`
>
>
`<ns4:cdsInput xmlns:ns2="org.opencds" xmlns:ns3="org.opencds.vmr.v1_0.schema.vmr" xmlns:ns4="org.opencds.vmr.v1_0.schema.cdsinput" xmlns:ns5="org.opencds.vmr.v1_0.schema.cdsoutput">`
>
> `<templateId root="2.16.840.1.113883.3.795.11.1.1"/>`
>
> `<!-- **CDSContext Section Begins** **(mandatory)** -->`
>
> `<cdsContext>`
>
> `<!-- Specify user Preferred Language -->`
>
> `<cdsSystemUserPreferredLanguage code="en" codeSystem="2.16.840.1.113883.6.99" displayName="English"/>`

```xml
</cdsContext>
```

> `<!-- **CDSContext Section Ends** -->`
>
> `<!-- **vMR Input Section Begins** **(mandatory)** -->`
>
> `<vmrInput>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.1.1"/>`
>
> `<!-- **Patient Input Section Begins** **(mandatory)** -->`
>
> `<patient>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.2.1.1"/>`

```xml

<id root="{UNIQUE_ROOT_ID}"
    extension="{UNIQUE_ROOT_EXTENSION}"/> <!-- root & extension attributes appended together must be unique across all root & root/extension values for the entire message. The unique identifier cannot be repeated anywhere in the message. Suggestion: use the Globally Unique Identifer (GUID) algorithm to generate the root attribute value only and do not bother specifying the extension. Example GUID value: 0368a1b4-0f93-402e-841d-e0b02943300d -->
```

> `<!-- **Patient Birthdate and Gender Section** **Begins** **(mandatory)** -->`
>
> `<demographics>`
>
>` `<birthTime value="{YYYYMMDD}"/>` `<!-- e.g. February 29, 2012 would be specified by 20120229 -->`
>
> `<gender code="{GENDER_CODE}" codeSystem="2.16.840.1.113883.5.1" displayName="{Optional_Value}"/>`
>
> `</demographics>`
>
> `<!-- **Patient Birthdate and Gender Section** **Ends** -->`
>
> `<clinicalStatements>`

```xml
<!-- **Patient Disease Immunity Section Begins (optional)** -->
```

> `<observationResults>`
>
> `<observationResult>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>`
>
> `<id root="{UNIQUE_IDENTIFIER2}"/>` `<!-- Suggestion: Use Globally Unique Identifier algorithm (GUID) -->`

```xml

<observationFocus code="{DISEASE_IMMUNITY_FOCUS_CODE}" codeSystem="2.16.840.1.113883.6.103" displayName=".."
                  originalText=".."/> <!—codeSystem may be OID for ICD-9-CM, SNOMED-CT, or ICD-10. See Disease code tables -->
```

> `<!-- ObservationEventTime low and high attributes are dates in YYYYMMDD format, and they must be the same value -->`
>
> `<observationEventTime low="{YYYYMMDD}" high="{YYYYMMDD}"/>`
>
> `<observationValue>`
>
> `<concept code="{DISEASE_DOCUMENTATION_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.8" displayName=".." originalText=".."/>`
>
> `</observationValue>`
>
>
`<interpretation code="{DISEASE_IMMUNITY_INTERPRETATION_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.9" displayName=".." originalText=".."/>`
>
> `</observationResult>`
>
> `<observationResult>`
>
> [Record another disease immunity information here if necessary …]
>
> `</observationResult>`
>
> `<observationResult>`
>
> [Record another disease immunity information here if necessary …]
>
> `</observationResult>`
>
> `</observationResults>`
>
> `<!-- **Patient Disease Immunity Section Ends** -->`
>
> `<!-- **List of Vaccines Administered Begins (optional)** -->`
>
> `<substanceAdministrationEvents>`
>
> `<!-- Shot number \#1 Begin -->`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="{UNIQUE_IDENTIFIER3}"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="{UNIQUE_IDENTIFIER4}"/>`
>
> `<substanceCode code="{CVX_CODE}" codeSystem="2.16.840.1.113883.12.292" displayName=".." originalText=".."/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="{YYYYMMDD}" high="{YYYYMMDD}"/>`
>
> `</substanceAdministrationEvent>`
>
> `<!-- Shot number \#1 End -->`
>
> `<!-- Shot number \#2 Begin -->`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="{UNIQUE_IDENTIFIER5}"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="{UNIQUE_IDENTIFIER6}"/>`
>
> `<substanceCode code="{CVX_CODE}" codeSystem="2.16.840.1.113883.12.292" displayName=".." originalText=".."/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="{YYYYMMDD}" high="{YYYYMMDD}"/>`
>
> `</substanceAdministrationEvent>`
>
> `<!-- Shot number \#2 End -->`
>
> `<!-- Shot number \#3 Begin -->`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="{UNIQUE_IDENTIFIER7}"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="{UNIQUE_IDENTIFIER8}"/>`
>
> `<substanceCode code="{CVX_CODE}" codeSystem="2.16.840.1.113883.12.292" displayName=".." originalText=".."/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="{YYYYMMDD}" high="{YYYYMMDD}"/>`
>
> `</substanceAdministrationEvent>`
>
> `<!-- Shot number \#3 End -->`
>
> `<!-- Shot number 4 Begin -->`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="{UNIQUE_IDENTIFIER9}"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="{UNIQUE_IDENTIFIER10}"/>`
>
> `<substanceCode code="{CVX_CODE}" codeSystem="2.16.840.1.113883.12.292" displayName=".." originalText=".."/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="{YYYYMMDD}" high="{YYYYMMDD}"/>`
>
> `</substanceAdministrationEvent>`
>
> `<!-- Shot number 4 End -->`
>
> `</substanceAdministrationEvents>`
>
> `<!-- **List of Vaccines Administered Ends** -->`
>
> `</clinicalStatements>`
>
> `</patient>`
>
> `<!-- **Patient Input Section Ends** -->`
>
> `</vmrInput>`
>
> `<!-- **VMR Input Section Ends** -->`
>
> `</ns4:cdsInput>`
>
> `<!-- **CDSInput Section Ends** -->`
>
> `<!-- **Message Ends** -->`

### Sample Input Message

Below is a sample XML message with the values populated.

> `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>`
>
>
`<ns4:cdsInput xmlns:ns2="org.opencds" xmlns:ns3="org.opencds.vmr.v1_0.schema.vmr" xmlns:ns4="org.opencds.vmr.v1_0.schema.cdsinput" xmlns:ns5="org.opencds.vmr.v1_0.schema.cdsoutput">`
>
> `<templateId root="2.16.840.1.113883.3.795.11.1.1"/>`
>
> `<cdsContext>`
>
> `<cdsSystemUserPreferredLanguage code="en" codeSystem="2.16.840.1.113883.6.99" displayName="English"/>`
>
> `</cdsContext>`
>
> `<vmrInput>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.1.1"/>`
>
> `<patient>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.2.1.1"/>`
>
> `<id root="2.16.840.1.113883.3.795.12.100.11" extension="92"/>`
>
> `<demographics>`
>
> `<birthTime value="19900101"/>`
>
> `<gender code="M" codeSystem="2.16.840.1.113883.5.1" displayName="Male" originalText="M"/>`
>
> `</demographics>`
>
> `<clinicalStatements>`
>
> `<observationResults>`
>
> `<observationResult>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>`
>
> `<id root="617478b8-b6eb-4988-853a-b5f5c2441eb8"/>`
>
> `<observationFocus code="070.30" codeSystem="2.16.840.1.113883.6.103" displayName="Hepatitis B" originalText="070.30"/>`
>
> `<observationEventTime low="19960315" high="19960315"/>`
>
> `<observationValue>`
>
>
`<concept code="DISEASE_DOCUMENTED" codeSystem="2.16.840.1.113883.3.795.12.100.8" displayName="Disease Documented" originalText="DISEASE_DOCUMENTED"/>`
>
> `</observationValue>`
>
>
`<interpretation code="IS_IMMUNE" codeSystem="2.16.840.1.113883.3.795.12.100.9" displayName="Is Immune" originalText="IS_IMMUNE"/>`
>
> `</observationResult>`
>
> `</observationResults>`
>
> `<substanceAdministrationEvents>`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="2.16.840.1.113883.3.795.12.100.10" extension="230"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="6095733e-a576-44a2-b314-26a23e1ff6b6"/>`
>
> `<substanceCode code="45" codeSystem="2.16.840.1.113883.12.292" displayName="HepB NOS" originalText="45"/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="19900315" high="19900315"/>`
>
> `</substanceAdministrationEvent>`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="2.16.840.1.113883.3.795.12.100.10" extension="229"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="c4361cf7-4387-4072-a55e-5bac066813ad"/>`
>
> `<substanceCode code="45" codeSystem="2.16.840.1.113883.12.292" displayName="HepB NOS" originalText="45"/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="19900401" high="19900401"/>`
>
> `</substanceAdministrationEvent>`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="2.16.840.1.113883.3.795.12.100.10" extension="228"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="84e18c21-1a07-4347-b7fd-96f052a39ef6"/>`
>
> `<substanceCode code="08" codeSystem="2.16.840.1.113883.12.292" displayName="HepB peds &lt; 20yrs" originalText="08"/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="19960315" high="19960315"/>`
>
> `</substanceAdministrationEvent>`
>
> `<substanceAdministrationEvent>`
>
> `<templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>`
>
> `<id root="2.16.840.1.113883.3.795.12.100.10" extension="227"/>`
>
> `<substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>`
>
> `<substance>`
>
> `<id root="fca8d517-9541-4f80-adbd-1528b3963360"/>`
>
> `<substanceCode code="08" codeSystem="2.16.840.1.113883.12.292" displayName="HepB peds &lt; 20yrs" originalText="08"/>`
>
> `</substance>`
>
> `<administrationTimeInterval low="20100201" high="20100201"/>`
>
> `</substanceAdministrationEvent>`
>
> `</substanceAdministrationEvents>`
>
> `</clinicalStatements>`
>
> `</patient>`
>
> `</vmrInput>`
>
> `</ns4:cdsInput>`

### Input Node Elements and Attributes

The table below lists the XML nodes and attributes that may be utilized in the input message. Usage notes are provided. Some
attribute values are coded; the complete set of accepted code values is listed in the [Code Tables section](#code-tables) of this
document.

| Attribute                                                                                                                                                                                                                                                                                                                                                                           | Datatype | Required? | Usage                                                                                                                                                                                                                                                                                                         |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| &lt;cdsInput&gt; This section is always provided                                                                                                                                                                                                                                                                                                                                    |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y         | Set to "2.16.840.1.113883.3.795.11.1.1"                                                                                                                                                                                                                                                                       |
| &lt;cdsInput&gt;&lt;cdsContext&gt; This section is always provided                                                                                                                                                                                                                                                                                                                  |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;cdsSystemUserPreferredLanguage code&gt;                                                                                                                                                                                                                                                                                                                                         | String   | Y         | Set to “en”                                                                                                                                                                                                                                                                                                   |
| &lt;cdsSystemUserPreferredLanguage codeSystem                                                                                                                                                                                                                                                                                                                                       | UUID     | Y         | Set to “2.16.840.1.113883.6.99”                                                                                                                                                                                                                                                                               |
| &lt;cdsInput&gt;&lt;vmrInput&gt; This section is always provided                                                                                                                                                                                                                                                                                                                    |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y         | Set to “2.16.840.1.113883.3.795.11.1.1”                                                                                                                                                                                                                                                                       |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt; This section is always provided                                                                                                                                                                                                                                                                                                     |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y         | Set to “2.16.840.1.113883.3.795.11.2.1.1”                                                                                                                                                                                                                                                                     |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                     | UUID     | Y         | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                   |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                | String   | N         | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                           |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;demographics&gt; This section is always provided with the birthdate and gender of the patient                                                                                                                                                                                                                                    |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;birthTime value&gt;                                                                                                                                                                                                                                                                                                                                                             | TS       | Y         | Birthdate of the patient. Set to a timestamp value with the format YYYYMMDD.                                                                                                                                                                                                                                  |
| &lt;gender code&gt;                                                                                                                                                                                                                                                                                                                                                                 | String   | Y         | Gender of the patient. Set as “M” for Male or “F” for Female.                                                                                                                                                                                                                                                 |
| &lt;gender codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                           | UUID     | Y         | Codesystem used by ICE to interpret gender.code. Set to "2.16.840.1.113883.5.1".                                                                                                                                                                                                                              |
| &lt;gender displayName&gt;                                                                                                                                                                                                                                                                                                                                                          | String   | N         | Display name for Gender. Not used by ICE                                                                                                                                                                                                                                                                      |
| &lt;gender originalText&gt;                                                                                                                                                                                                                                                                                                                                                         | String   | N         | Original Text name for Gender. Not used by ICE                                                                                                                                                                                                                                                                |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt; This section is always provided.                                                                                                                                                                                                                                                                          |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt; This section is optionally provided to specify all instances of disease immunity for the patient. Each instance of disease immunity is specified by an &lt;observationResult&gt; section                                                                                        |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt; Repeated for each instance of disease immunity, if any.                                                                                                                                                                                                |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y         | Set to “2.16.840.1.113883.3.795.11.6.3.1”                                                                                                                                                                                                                                                                     |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                     | UUID     | Y         | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                   |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                | String   | N         | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                           |
| &lt;observationFocus code&gt;                                                                                                                                                                                                                                                                                                                                                       | String   | Y         | Code value specifying the focus of the observation. Refer to the code table for the below codeSystem for valid values. See Disease code tables for supported values.                                                                                                                                          |
| &lt;observationFocus codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y         | Code System used by ICE to interpret the above observationFocus code. Code System may be OID for ICD-9-CM, SNOMED-CT, or ICD-10.                                                                                                                                                                              |
| &lt;observationEventTime low&gt;                                                                                                                                                                                                                                                                                                                                                    | TS       | Y         | Time that the disease immunity was recorded. Set to a timestamp value with the format YYYYMMDD                                                                                                                                                                                                                |
| &lt;observationEventTime high&gt;                                                                                                                                                                                                                                                                                                                                                   | TS       | Y         | Time that the disease immunity was recorded. Set to the same timestamp value as observationEventTime.low (format YYYYMMDD)                                                                                                                                                                                    |
| &lt;interpretation code&gt;                                                                                                                                                                                                                                                                                                                                                         | String   | Y         | Interpretation element is repeatable. Code value specifying how ICE should interpret the nested &lt;observationValue&gt;. Refer to the code table for the below codeSystem for valid values                                                                                                                   |
| &lt;interpretation codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                   | UUID     | Y         | Interpretation element is repeatable. Code System used by ICE to interpret the above interpretation code. Set to “2.16.840.1.113883.3.795.12.100.9”                                                                                                                                                           |
| &lt;interpretation displayName&gt;                                                                                                                                                                                                                                                                                                                                                  | String   | N         | Interpretation element is repeatable. Display name corresponding with the above interpretation code. Not used by ICE.                                                                                                                                                                                         |
| &lt;interpretation originalText&gt;                                                                                                                                                                                                                                                                                                                                                 | String   | N         | Interpretation element is repeatable. Original text name corresponding with the above interpretation code. Not used by ICE.                                                                                                                                                                                   |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt;&lt;observationValue&gt; Required section if ancestor &lt;observationResult&gt; section is present.                                                                                                                                                     |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;concept code&gt;                                                                                                                                                                                                                                                                                                                                                                | String   | Y         | Code value specifying the value for the above &lt;observationFocus/&gt;. Since &lt;observationResults/&gt; are only specified in the input message for disease immunity, this code will always have something to do with disease immunity. Refer to the code table for the below codeSystem for valid values. |
| &lt;concept codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                          | UUID     | Y         | Code System used by ICE to interpret the above concept code. Set to “2.16.840.1.113883.3.795.12.100.8”                                                                                                                                                                                                        |
| &lt;concept displayName&gt;                                                                                                                                                                                                                                                                                                                                                         | String   | N         | Display name corresponding with the above observation value code. Not used by ICE.                                                                                                                                                                                                                            |
| &lt;concept originalText&gt;                                                                                                                                                                                                                                                                                                                                                        | String   | N         | Original text corresponding to the above observation value code. Not used by ICE.                                                                                                                                                                                                                             |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt; This section is optional. Specified when there are shots that have been administered. They are reported to ICE as a part of the patient’s immunization history. Each instance of an administered shot is specified by a &lt;substanceAdministrationEvent&gt; section |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt; Repeated for each instance of an administered shot.                                                                                                                                                                              |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y         | Set to “2.16.840.1.113883.3.795.11.9.1.1”                                                                                                                                                                                                                                                                     |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                     | UUID     | Y         | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                   |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                | String   | N         | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                           |
| &lt;substanceAdministrationGeneralPurpose code&gt;                                                                                                                                                                                                                                                                                                                                  | String   | Y         | Set to “384810002”                                                                                                                                                                                                                                                                                            |
| &lt;substanceAdministrationGeneralPurpose codeSystem&gt;                                                                                                                                                                                                                                                                                                                            | UUID     | Y         | Set to “2.16.840.1.113883.6.5”                                                                                                                                                                                                                                                                                |
| &lt;administrationTimeInterval low&gt;                                                                                                                                                                                                                                                                                                                                              | TS       | Y         | Date that a shot was administered. Set to a timestamp value with the format YYYYMMDD                                                                                                                                                                                                                          |
| &lt;administrationTimeInterval high&gt;                                                                                                                                                                                                                                                                                                                                             | TS       | Y         | Date that a shot was administered. Set to the same timestamp value as administrationTimeInterval.low (format YYYYMMDD)                                                                                                                                                                                        |
| &lt;isValid value&gt;                                                                                                                                                                                                                                                                                                                                                               | Boolean  | N         | If “enable_dose_override_feature” property is set in the ice.properties file, enables caller to override ICE’s evaluation for a shot. See https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/691470371/Dose+Override+Feature for details.                                                               |
| &lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;substance&gt; Required if ancestor &lt;substanceAdministrationEvent/&gt; section is present                                                                                                                                   |          |           |                                                                                                                                                                                                                                                                                                               |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                     | UUID     | Y         | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                   |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                | String   | N         | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                           |
| &lt;substanceCode code&gt;                                                                                                                                                                                                                                                                                                                                                          | String   | Y         | CVX code of the vaccine administered                                                                                                                                                                                                                                                                          |
| &lt;substanceCode codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                    | UUID     | Y         | Set to “2.16.840.1.113883.12.292”, the OID for CVX codes                                                                                                                                                                                                                                                      |
| &lt;substanceCode displayName&gt;                                                                                                                                                                                                                                                                                                                                                   | String   | N         | Display name corresponding with the above CVX code. Not used by ICE.                                                                                                                                                                                                                                          |
| &lt;substanceCode originalText&gt;                                                                                                                                                                                                                                                                                                                                                  | String   | N         | Original text corresponding with the above CVX code. Not used by ICE.                                                                                                                                                                                                                                         |

## ICE Output Message

When producing the output of evaluations and recommendations to the client, ICE will first mirror what was provided in the VMR input
message and then supplements the provided information with additional elements and attributes. In some cases, where additional
nested output is conveyed, ICE will do so by adding `<relatedClinicalStatement/>` nodes.

The ICE-specific immunization output message conforms to the cdsOutput.xsd, and the XML template on the next page.

- XML messages must follow the ordering and structure of this template. The order of elements do not deviate from the template.

- Some elements may not be present in all messages or may repeat, as described in the comments of the template as well as in the
  XSD.

- Refer to the [Output Node Elements and Attributes Section](#output-node-elements-and-attributes) for additional usage information.

### Output Message Format

```xml
<!-- **Message Begins** -->

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>

<!-- **CDSOutput Section Begins (always present)** -->

<ns5:cdsOutput xmlns:ns2="org.opencds" xmlns:ns3="org.opencds.vmr.v1_0.schema.vmr" xmlns:ns4="org.opencds.vmr.v1_0.schema.cdsinput"
               xmlns:ns5="org.opencds.vmr.v1_0.schema.cdsoutput">

    <!-- **VMR Output Section Begins (always present)** -->

    <vmrOutput>

        <templateId root="2.16.840.1.113883.3.795.11.1.1"/>

        <!-- **Patient Output Section Begins (always present)** -->

        <patient>

            <templateId root="2.16.840.1.113883.3.795.11.2.1.1"/>

            <id root="{UNIQUE_IDENTIFIER1}"/>

            <!-- **Patient Birthdate and Gender Section Begins (always present; no differences from input message)** -->

            <demographics>

                <birthTime value="{YYYYMMDD}"/>

                <gender code="{GENDER_CODE}" codeSystem="2.16.840.1.113883.5.1" displayName="…" originalText="…"/>

            </demographics>

            <!-- **Patient Birthdate and Gender Section Ends** -->

            <clinicalStatements>

                <!-- **Patient Disease Immunity Section Begins (only present if provided on input; no differences from input message)** -->

                <observationResults>

                    <observationResult>

                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                        <id root="{UNIQUE_IDENTIFIER2}"/>

                        <observationFocus code="{DISEASE_IMMUNITY_FOCUS_CODE" codeSystem="2.16.840.1.113883.6.103" displayName="…"
                                          originalText="…"/>

                        <observationEventTime low="{YYYYMMDD}" high="{YYYYMMDD}"/>

                        <observationValue>

                            <concept code="{DISEASE_DOCUMENTATION_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.8"
                                     displayName="…" originalText="…"/>

                        </observationValue>

                        <interpretation code="{DISEASE_IMMUNITY_INTERPRETATION_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.9"
                                        displayName="…" originalText="…"/>

                    </observationResult>
```

> `<observationResult>`
>
> [Output of another disease immunity record here if necessary …]
>
> `</observationResult>`
>
> `<observationResult>`
>
> [Output of another disease immunity record here if necessary …]
>
> `</observationResult>`

```xml
</observationResults>

        <!-- **Patient Disease Immunity Section Ends** -->

        <!-- **List of Vaccines Administered Section Begins. Note that each <SubstanceAdministrationEvent/> provided in the input message is also listed in this output** -->

<substanceAdministrationEvents>

    <!-- **SubstanceAdministrationEvent for administered vaccine \#1; evaluation information is added by ICE** -->

    <substanceAdministrationEvent>

        <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

        <id root="{UNIQUE_IDENTIFIER3}"/>

        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

        <substance>

            <id root="{UNIQUE_IDENTIFIER4}"/>

            <!-- **Vaccine code supplied by client application; note that this could be a composite vaccine** -->

            <substanceCode code="{CVX_CODE}" codeSystem="2.16.840.1.113883.12.292" displayName="…" originalText="…"/>

        </substance>

        <administrationTimeInterval low="{YYYYMMDD}" high="{YYYYMMDD}"/>

        <!-- **Evaluation Information Section Begins; this <relatedClinicalStatement/> is repeated for each component vaccine implemented in ICE** -->

        <relatedClinicalStatement>

            <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

            <substanceAdministrationEvent>

                <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                <id root="{UNIQUE_IDENTIFIER5}"/>

                <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                <substance>

                    <id root="{UNIQUE_IDENTIFIER6}"/>

                    <!-- **Component Vaccine in focus within this <relatedClinicalStatement/>; note that if the vaccine supplied by the client application is not a composite vaccine, this vaccine code will be the same as the above** -->

                    <substanceCode code="{CVX_CODE}" codeSystem="2.16.840.1.113883.12.292" displayName="…" originalText="…"/>

                </substance>

                <administrationTimeInterval low="{YYYYMMDD}" high="{YYYYMMDD}"/>

                <!-- **Validity of Component Vaccine; true if VALID, or false if ACCEPTED or INVALID. This summary value is supplied for convenience only; it is strongly recommended that the client application use the below nested <relatedClinicalStatement/> for validity information** -->

                <isValid value="{TRUE_OR_FALSE}"/>

                <relatedClinicalStatement>

                    <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                    <!-- **Component Vaccine Validity Information** -->

                    <observationResult>

                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                        <id root="{UNIQUE_IDENTIFIER7}"/>

                        <!-- **ObservationFocus to specify which component was evaluated** -->

                        <observationFocus code="{IMMUNIZATION_VALIDITY_FOCUS}" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                          displayName="…" originalText="…"/>

                        <!-- **ObservationValue to specify validity of component vaccine (*i.e.* VALID, ACCEPTED, or INVALID)** -->

                        <observationValue>

                            <concept code="{VALIDITY_VALUE}" codeSystem="2.16.840.1.113883.3.795.12.100.2" displayName="…"
                                     originalText="…"/>

                        </observationValue>

                        <!-- **Optional (repeatable) interpretation element specify why a vaccine is VALID, INVALID or ACCEPTED** -->

                        <interpretation code="{REASON_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.3" displayName="…"
                                        originalText="…"/>

                        <interpretation code="{REASON_CODE2}" codeSystem="2.16.840.1.113883.3.795.12.100.3" displayName="…"
                                        originalText="…"/>

                    </observationResult>

                </relatedClinicalStatement>

            </substanceAdministrationEvent>

        </relatedClinicalStatement>

        <!-- **Evaluation Information Section Ends for this component vaccine** -->

        <!-- **Evaluation Information Section Begins for next component vaccine (if any)** -->

        <relatedClinicalStatement>
```

...

```xml
</relatedClinicalStatement>

        <!-- **Evaluation Information Section Ends for this component vaccine** -->

        </substanceAdministrationEvent>

        <!-- **SubstanceAdministrationEvent Section Ends for this administered vaccine** -->

        <!-- **SubstanceAdministrationEvent Section Begins for next administered vaccine \#2, \#3, etc. (if any)** -->

<substanceAdministrationEvent>
```

...

```xml
</substanceAdministrationEvent>

        <!-- **SubstanceAdministrationEvent Section Ends for this administered vaccine** -->

        </substanceAdministrationEvents>

        <!-- **List of Vaccines Administered Section Ends** -->

        <!-- **ICE Recommendations Section Begins (always present). Note that each <SubstanceAdministrationProposal/> corresponds to a recommendation for 1 vaccine group** -->

<substanceAdministrationProposals>

    <!-- **SubstanceAdministrationProposal for vaccine group \#1** -->

    <substanceAdministrationProposal>

        <templateId root="2.16.840.1.113883.3.795.11.9.3.1"/>

        <id root="{UNIQUE_IDENTIFIER8}"/>

        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

        <substance>

            <id root="{UNIQUE_IDENTIFIER9}"/>

            <!— **substanceCode specifies the vaccine or vaccine group for this recommendation. If a specific vaccine is
            recommended, ICE will populate this with a CVX code. More commonly, this attribute will be populated with the vaccine
            group code using code system 2.16.840.1.113883.3.795.12.100.1 as in the example below In this example, substanceCode
            specifies the vaccine group for this recommendation** -->

            <substanceCode code="{VACCINE_GROUP_OR_VACCINE_SPECIFIC_CODE}"
                           codeSystem="<2.16.840.1.113883.12.292 if vaccine> or < 2.16.840.1.113883.3.795.12.100.1 if vaccine group>"
                           displayName="…" originalText="…"/>

        </substance>

        <!-- **<relatedClinicalStatement/>** **contains the recommendation forecast and associated reasons for the vaccine group specified by the below <observationFocus/> element** -->

        <relatedClinicalStatement>

            <targetRelationshipToSource code="RSON" codeSystem="2.16.840.1.113883.5.1002"/>

            <observationResult>

                <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                <id root="{UNIQUE_IDENTIFIER10}"/>

                <!-- **observationFocus specifies the vaccine group for this recommendation** -->

                <observationFocus code="{VACCINE_GROUP_RECOMMENDATION_FOCUS_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                  displayName="…" originalText="…"/>

                <!-- **observationValue specifies the recommendation; currently either RECOMMENDED, FUTURE_RECOMMENDED, CONDITIONALLY_RECOMMENDED or NOT_RECOMMENDED** -->

                <observationValue>

                    <concept code="{RECOMMENDATION_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.5" displayName="…"
                             originalText="…"/>

                </observationValue>

                <!-- **Optional (repeatable) interpretation element specify why the reason for the above recommendation value** -->

                <interpretation code="{RECOMMENDATION_REASON_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.6" displayName="…"
                                originalText="…"/>

            </observationResult>

        </relatedClinicalStatement>

    </substanceAdministrationProposal>

    <!-- **SubstanceAdministrationProposal Section Ends for vaccine group \#1** -->

    <!-- **SubstanceAdministrationProposal for vaccine group \#2** -->

    <substanceAdministrationProposal>

        <templateId root="2.16.840.1.113883.3.795.11.9.3.1"/>

        <id root="{UNIQUE_IDENTIFIER11}"/>

        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

        <substance>

            <id root="{UNIQUE_IDENTIFIER12}"/>

            <!— **substanceCode specifies the vaccine or vaccine group for this recommendation. If a specific vaccine is
            recommended, ICE will populate this with a CVX code. More commonly, this attribute will be populated with the vaccine
            group code using code system 2.16.840.1.113883.3.795.12.100.1 as in the example below In this example, substanceCode
            specifies the vaccine group for this recommendation** -->

            <substanceCode code="{VACCINE_GROUP_OR_VACCINE_SPECIFIC_CODE}"
                           codeSystem="<2.16.840.1.113883.12.292 if vaccine> or < 2.16.840.1.113883.3.795.12.100.1 if vaccine group>"
                           displayName="…" originalText="…"/>

        </substance>

        <!-- **<relatedClinicalStatement/>** **contains the recommendation forecast and associated reasons for the vaccine group specified by the below <observationFocus/> element** -->

        <relatedClinicalStatement>

            <targetRelationshipToSource code="RSON" codeSystem="2.16.840.1.113883.5.1002"/>

            <observationResult>

                <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                <id root="{UNIQUE_IDENTIFIER13}"/>

                <!-- **observationFocus specifies the vaccine group for this recommendation.** -->

                <observationFocus code="{VACCINE_RECOMMENDATION_FOCUS_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                  displayName="…" originalText="…"/>

                <!-- **observationValue specifies the recommendation; currently either RECOMMENDED, FUTURE_RECOMMENDED, CONDITIONALLY_RECOMMENDED or NOT_RECOMMENDED** -->

                <observationValue>

                    <concept code="{RECOMMENDATION_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.5" displayName="…"
                             originalText="…"/>

                </observationValue>

                <!-- **Optional (repeatable) interpretation element specify why the reason for the above recommendation value** -->

                <interpretation code="{RECOMMENDATION_REASON_CODE}" codeSystem="2.16.840.1.113883.3.795.12.100.6" displayName="…"
                                originalText="…"/>

            </observationResult>

        </relatedClinicalStatement>

    </substanceAdministrationProposal>

    <!-- **SubstanceAdministrationProposal Section Ends for vaccine group \#2** -->

    <!-- **SubstanceAdministrationProposal Section Repeated for remaining vaccine group**
```

> **or vaccine-specific recommendations** --\>

`<substanceAdministrationProposal>`

...

```xml
</substanceAdministrationProposal>

        <!-- **SubstanceAdministrationProposal Section Ends** -->

        </substanceAdministrationProposals>

        <!-- **ICE Recommendations Section Ends** -->

        </clinicalStatements>

        </patient>

        <!-- **Patient Output Section Ends** -->

        </vmrOutput>

        <!-- **VMR Output Section Ends** -->

        </ns5:cdsOutput>

        <!-- **CDSOutput Section Ends** -->

        <!-- **Message Ends** -->
```

### Sample Output Message

The following sample output shows the evaluations for 4 administered shots and one accompanying Hep B recommendation. The patient
was born on 1/1/1990. Disease immunity was documented on 3/15/1996 and the test was executed when the patient’s age was 21 years, 11
months and 11 days old (*i.e.* – 8015 days). (Recall that ICE will evaluate and forecast at a specified time via the DSS
evaluatedAtSpecifiedTime operation.)

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>

<ns5:cdsOutput xmlns:ns2="org.opencds" xmlns:ns3="org.opencds.vmr.v1_0.schema.vmr" xmlns:ns4="org.opencds.vmr.v1_0.schema.cdsinput"
               xmlns:ns5="org.opencds.vmr.v1_0.schema.cdsoutput">

    <vmrOutput>

        <templateId root="2.16.840.1.113883.3.795.11.1.1"/>

        <patient>

            <templateId root="2.16.840.1.113883.3.795.11.2.1.1"/>

            <id root="2.16.840.1.113883.3.795.12.100.11" extension="92"/>

            <demographics>

                <birthTime value="19900101"/>

                <gender code="M" codeSystem="2.16.840.1.113883.5.1" displayName="Male" originalText="M"/>

            </demographics>

            <clinicalStatements>

                <observationResults>

                    <observationResult>

                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                        <id root="617478b8-b6eb-4988-853a-b5f5c2441eb8"/>

                        <observationFocus code="070.30" codeSystem="2.16.840.1.113883.6.103" displayName="Hepatitis B"
                                          originalText="070.30"/>

                        <observationEventTime low="19960315" high="19960315"/>

                        <observationValue>

                            <concept code="DISEASE_DOCUMENTED" codeSystem="2.16.840.1.113883.3.795.12.100.8"
                                     displayName="Disease Documented" originalText="DISEASE_DOCUMENTED"/>

                        </observationValue>

                        <interpretation code="IS_IMMUNE" codeSystem="2.16.840.1.113883.3.795.12.100.9" displayName="Is Immune"
                                        originalText="IS_IMMUNE"/>

                    </observationResult>

                </observationResults>

                <substanceAdministrationEvents>

                    <substanceAdministrationEvent>

                        <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                        <id root="2.16.840.1.113883.3.795.12.100.10" extension="230"/>

                        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                        <substance>

                            <id root="1094b5c2-03f7-472d-bf62-989138841492"/>

                            <substanceCode code="45" codeSystem="2.16.840.1.113883.12.292" displayName="HepB NOS"
                                           originalText="45"/>

                        </substance>

                        <administrationTimeInterval low="19900315" high="19900315"/>

                        <relatedClinicalStatement>

                            <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                            <substanceAdministrationEvent>

                                <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                                <id root="7de6fc89-f6a7-4926-ad84-82708d87aaff"/>

                                <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                                <substance>

                                    <id root="4d0ea31e-04ac-4bd7-8fbf-3e1f7423b5e0"/>

                                    <substanceCode code="45" codeSystem="2.16.840.1.113883.12.292" displayName="HepB NOS"
                                                   originalText="45"/>

                                </substance>

                                <administrationTimeInterval low="19900315" high="19900315"/>

                                <isValid value="true"/>

                                <relatedClinicalStatement>

                                    <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                                    <observationResult>

                                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                                        <id root="6c360bc7-afb8-4585-823e-f3297db42048"/>

                                        <observationFocus code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                                          displayName="Immunization Validity (Hep B Component)" originalText="100"/>

                                        <observationValue>

                                            <concept code="VALID" codeSystem="2.16.840.1.113883.3.795.12.100.2"
                                                     displayName="Valid Immunization" originalText="VALID"/>

                                        </observationValue>

                                    </observationResult>

                                </relatedClinicalStatement>

                            </substanceAdministrationEvent>

                        </relatedClinicalStatement>

                    </substanceAdministrationEvent>

                    <substanceAdministrationEvent>

                        <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                        <id root="2.16.840.1.113883.3.795.12.100.10" extension="229"/>

                        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                        <substance>

                            <id root="7bc7d976-1d21-458f-b0ea-21262a1314db"/>

                            <substanceCode code="45" codeSystem="2.16.840.1.113883.12.292" displayName="HepB NOS"
                                           originalText="45"/>

                        </substance>

                        <administrationTimeInterval low="19900401" high="19900401"/>

                        <relatedClinicalStatement>

                            <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                            <substanceAdministrationEvent>

                                <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                                <id root="b84dd4d8-c942-443a-911e-424834327bca"/>

                                <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                                <substance>

                                    <id root="a8bca109-4b9f-4186-a4f5-8053d74c4a51"/>

                                    <substanceCode code="45" codeSystem="2.16.840.1.113883.12.292" displayName="HepB NOS"
                                                   originalText="45"/>

                                </substance>

                                <administrationTimeInterval low="19900401" high="19900401"/>

                                <isValid value="false"/>

                                <relatedClinicalStatement>

                                    <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                                    <observationResult>

                                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                                        <id root="b088d0e5-05e4-4aa5-8067-dba69a79e4f1"/>

                                        <observationFocus code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                                          displayName="Immunization Validity (Hep B Component)" originalText="100"/>

                                        <observationValue>

                                            <concept code="INVALID" codeSystem="2.16.840.1.113883.3.795.12.100.2"
                                                     displayName="Invalid Immunization" originalText="INVALID"/>

                                        </observationValue>

                                        <interpretation code="BELOW_MINIMUM_INTERVAL" codeSystem="2.16.840.1.113883.3.795.12.100.3"
                                                        displayName="Below Minimum Interval" originalText="BELOW_MINIMUM_INTERVAL"/>

                                    </observationResult>

                                </relatedClinicalStatement>

                            </substanceAdministrationEvent>

                        </relatedClinicalStatement>

                    </substanceAdministrationEvent>

                    <substanceAdministrationEvent>

                        <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                        <id root="2.16.840.1.113883.3.795.12.100.10" extension="228"/>

                        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                        <substance>

                            <id root="85818577-26e6-49a0-bd64-8062518b40da"/>

                            <substanceCode code="08" codeSystem="2.16.840.1.113883.12.292" displayName="HepB peds &lt; 20yrs"
                                           originalText="08"/>

                        </substance>

                        <administrationTimeInterval low="19960315" high="19960315"/>

                        <relatedClinicalStatement>

                            <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                            <substanceAdministrationEvent>

                                <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                                <id root="6d32bcec-4244-4e89-8d17-62097b775714"/>

                                <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                                <substance>

                                    <id root="7c8035f2-d860-4b80-8b9e-5673d3ba6c36"/>

                                    <substanceCode code="08" codeSystem="2.16.840.1.113883.12.292"
                                                   displayName="HepB peds &lt; 20yrs" originalText="08"/>

                                </substance>

                                <administrationTimeInterval low="19960315" high="19960315"/>

                                <isValid value="false"/>

                                <relatedClinicalStatement>

                                    <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                                    <observationResult>

                                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                                        <id root="d792be0e-1037-4d09-93e6-25ce729d93e0"/>

                                        <observationFocus code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                                          displayName="Immunization Validity (Hep B Component)" originalText="100"/>

                                        <observationValue>

                                            <concept code="ACCEPTED" codeSystem="2.16.840.1.113883.3.795.12.100.2"
                                                     displayName="Accepted Immunization" originalText="ACCEPTED"/>

                                        </observationValue>

                                        <interpretation code="PROOF_OF_IMMUNITY" codeSystem="2.16.840.1.113883.3.795.12.100.3"
                                                        displayName="Proof of Immunity" originalText="PROOF_OF_IMMUNITY"/>

                                    </observationResult>

                                </relatedClinicalStatement>

                            </substanceAdministrationEvent>

                        </relatedClinicalStatement>

                    </substanceAdministrationEvent>

                    <substanceAdministrationEvent>

                        <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                        <id root="2.16.840.1.113883.3.795.12.100.10" extension="227"/>

                        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                        <substance>

                            <id root="20ce1691-1e81-4af6-8c25-e40773159cd2"/>

                            <substanceCode code="08" codeSystem="2.16.840.1.113883.12.292" displayName="HepB peds &lt; 20yrs"
                                           originalText="08"/>

                        </substance>

                        <administrationTimeInterval low="20100201" high="20100201"/>

                        <relatedClinicalStatement>

                            <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                            <substanceAdministrationEvent>

                                <templateId root="2.16.840.1.113883.3.795.11.9.1.1"/>

                                <id root="efaf05b0-6664-47fa-9fad-0a7a68f48049"/>

                                <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                                <substance>

                                    <id root="6a2b2fb2-a55a-4919-9937-a5af77fa400a"/>

                                    <substanceCode code="08" codeSystem="2.16.840.1.113883.12.292"
                                                   displayName="HepB peds &lt; 20yrs" originalText="08"/>

                                </substance>

                                <administrationTimeInterval low="20100201" high="20100201"/>

                                <isValid value="false"/>

                                <relatedClinicalStatement>

                                    <targetRelationshipToSource code="PERT" codeSystem="2.16.840.1.113883.5.1002"/>

                                    <observationResult>

                                        <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                                        <id root="46ef3a8c-da1c-4c2f-943a-513552494a46"/>

                                        <observationFocus code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                                          displayName="Immunization Validity (Hep B Component)" originalText="100"/>

                                        <observationValue>

                                            <concept code="ACCEPTED" codeSystem="2.16.840.1.113883.3.795.12.100.2"
                                                     displayName="Accepted Immunization" originalText="ACCEPTED"/>

                                        </observationValue>

                                        <interpretation code="PROOF_OF_IMMUNITY" codeSystem="2.16.840.1.113883.3.795.12.100.3"
                                                        displayName="Proof of Immunity" originalText="PROOF_OF_IMMUNITY"/>

                                    </observationResult>

                                </relatedClinicalStatement>

                            </substanceAdministrationEvent>

                        </relatedClinicalStatement>

                    </substanceAdministrationEvent>

                </substanceAdministrationEvents>

                <substanceAdministrationProposals>

                    <substanceAdministrationProposal>

                        <templateId root="2.16.840.1.113883.3.795.11.9.3.1"/>

                        <id root="88758294-7f20-4491-aaae-6450fb1fb3fc"/>

                        <substanceAdministrationGeneralPurpose code="384810002" codeSystem="2.16.840.1.113883.6.5"/>

                        <substance>

                            <id root="2a048d0b-e15e-46f0-9008-397742e90afa"/>

                            <substanceCode code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                           displayName="Immunization Recommendation Focus (Hep B)" originalText="100"/>

                        </substance>

                        <relatedClinicalStatement>

                            <targetRelationshipToSource code="RSON" codeSystem="2.16.840.1.113883.5.1002"/>

                            <observationResult>

                                <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>

                                <id root="f8592ea2-22b4-4619-bb4b-8a4865753561"/>

                                <observationFocus code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1"
                                                  displayName="Immunization Recommendation Focus (Hep B)" originalText="100"/>

                                <observationValue>

                                    <concept code="NOT_RECOMMENDED" codeSystem="2.16.840.1.113883.3.795.12.100.5"
                                             displayName="Not Recommended" originalText="NOT_RECOMMENDED"/>

                                </observationValue>

                                <interpretation code="PROOF_OF_IMMUNITY" codeSystem="2.16.840.1.113883.3.795.12.100.6"
                                                displayName="Proof of Immunity" originalText="PROOF_OF_IMMUNITY"/>

                            </observationResult>

                        </relatedClinicalStatement>

                    </substanceAdministrationProposal>

                </substanceAdministrationProposals>

            </clinicalStatements>

        </patient>

    </vmrOutput>

</ns5:cdsOutput>
```

### Output Node Elements and Attributes

The table below lists the XML nodes and attributes that are utilized in the output message. Notice that the output message structure
contains much of what was provided on input, with additional elements and attributes encompassing evaluations and forecasts. Usage
notes are provided. Some attribute values are coded.

The complete set of accepted code values is listed in the [Code Tables section](#code-tables) of this document. Refer to
the [Output Message Format Section](#output-message-format) for a description on the structure of the output message.

| Attribute                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | Datatype | Always Present? | Value Same as in Input Message? | Usage                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|-----------------|---------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| &lt;cdsOutput&gt; This section is always provided                                                                                                                                                                                                                                                                                                                                                                                                                                               |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | Y                               | Set to "2.16.840.1.113883.3.795.11.1.1"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt; This section is always provided                                                                                                                                                                                                                                                                                                                                                                                                                              |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | Y                               | Set to “2.16.840.1.113883.3.795.11.1.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt; This section is always provided                                                                                                                                                                                                                                                                                                                                                                                                               |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | Y                               | Set to “2.16.840.1.113883.3.795.11.2.1.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | Y                               | UUID used to form a unique value across all id elements for the message. May be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | Y                               | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;demographics&gt; This section is always provided with the birthdate and gender of the patient                                                                                                                                                                                                                                                                                                                                              |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;birthTime value&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | TS       | Y               | Y                               | Birthdate of the patient. Set to a timestamp value with the format YYYYMMDD.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| &lt;gender code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | String   | Y               | Y                               | Gender of the patient. Set as “M” for Male or “F” for Female.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| &lt;gender codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | UUID     | Y               | Y                               | Codesystem used by ICE to interpret gender.code. Set to "2.16.840.1.113883.5.1".                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| &lt;gender displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | String   | N               | Y                               | Display name for Gender. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| &lt;gender originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | N               | Y                               | Original Text name for Gender. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt; This section is always provided.                                                                                                                                                                                                                                                                                                                                                                                    |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt; This section is optionally provided to specify all instances of disease immunity for the patient. Each instance of disease immunity is specified by an &lt;observationResult&gt; section                                                                                                                                                                                                  |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt; Repeated for each instance of disease immunity, if any.                                                                                                                                                                                                                                                                                                          |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | Y                               | Set to “2.16.840.1.113883.3.795.11.6.3.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | Y                               | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | Y                               | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;observationFocus code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | String   | Y               | Y                               | Code value specifying the focus of this observation related to disease immunity. Refer to the code table for the below codeSystem for valid values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;observationFocus codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y               | Y                               | Code System used by ICE to interpret the above observationFocus code. This should have been set to “2.16.840.1.113883.6.103” by the client application for disease immunity focus.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| &lt;observationEventTime low&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                | TS       | Y               | Y                               | Date that the disease immunity was recorded. Set to a timestamp value with the format YYYYMMDD                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| &lt;observationEventTime high&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | TS       | Y               | Y                               | Date that the disease immunity was recorded. Set to the same timestamp value as observationEventTime.low (format YYYYMMDD)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| &lt;interpretation code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | Y               | Y                               | Interpretation element is repeatable. Code value specifying how ICE interpreted the nested &lt;observationValue&gt; with respect to disease immunity. Refer to the code table for the below codeSystem for valid values                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| &lt;interpretation codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | UUID     | Y               | Y                               | Interpretation element is repeatable. Code System used by ICE to interpret the above interpretation code. This should have been set to “2.16.840.1.113883.3.795.12.100.9” by the client application for disease immunity interpretation.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &lt;interpretation displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | N               | Y                               | Interpretation element is repeatable. Display name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;interpretation originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                             | String   | N               | Y                               | Interpretation element is repeatable. Original text name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt;&lt;observationValue&gt; Required section if ancestor &lt;observationResult&gt; section is present.                                                                                                                                                                                                                                                               |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;concept code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | Y               | Y                               | Code value specifying the disease immunity value for the above &lt;observationFocus/&gt;. Refer to the code table for the below codeSystem for valid values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| &lt;concept codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | UUID     | Y               | Y                               | Code System used by ICE to interpret the above concept code. Set to “2.16.840.1.113883.3.795.12.100.8”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| &lt;concept displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | N               | Y                               | Display name corresponding with the above observation value code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| &lt;concept originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | String   | N               | Y                               | Original text corresponding to the above observation value code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt; This section is optional. Specified when there are shots that have been administered. They are reported to ICE as a part of the patient’s immunization history. Each instance of an administered shot is specified by a &lt;substanceAdministrationEvent&gt; section                                                                                                           |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt; Repeated for each instance of an administered shot.                                                                                                                                                                                                                                                                                        |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | Y                               | Set to “2.16.840.1.113883.3.795.11.9.1.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | Y                               | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | Y                               | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceAdministrationGeneralPurpose code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | Y               | Y                               | Set to “384810002”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| &lt;substanceAdministrationGeneralPurpose codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                        | UUID     | Y               | Y                               | Set to “2.16.840.1.113883.6.5”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| &lt;administrationTimeInterval low&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                          | TS       | Y               | Y                               | Date that the shot was administered. Set to a timestamp value with the format YYYYMMDD                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| &lt;administrationTimeInterval high&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                         | TS       | Y               | Y                               | Date that the shot was administered. Set to the same timestamp value as administrationTimeInterval.low (format YYYYMMDD)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;substance&gt; Required if ancestor &lt;substanceAdministrationEvent/&gt; section is present                                                                                                                                                                                                                                             |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | Y                               | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | Y                               | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceCode code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | String   | Y               | N                               | CVX code of the vaccine administered. In future versions of ICE, this value will be the same as that in the input message.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| &lt;substanceCode codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                | UUID     | Y               | N                               | ICE sets this to “2.16.840.1.113883.12.292”, the OID for CVX codes.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceCode displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | String   | N               | N                               | Display name corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &lt;substanceCode originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | N               | N                               | Original text corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt; This element was not provided on input but is present on output for its ancestor &lt;substanceAdministrationEvent/&gt;. It is included to list evaluation information for a component vaccine. Repeatable for each component vaccine of the administered shot.                                             |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;targetRelationshipToSource code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                         | String   | Y               | N/A                             | Set to “PERT”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| &lt;targetRelationshipToSource codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                   | UUID     | Y               | N/A                             | Set to “2.16.840.1.113883.5.1002”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;                                                                                                                                                                                                                                                                        |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | N/A                             | Set to “2.16.840.1.113883.3.795.11.9.1.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | N/A                             | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | N/A                             | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceAdministrationGeneralPurpose code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | Y               | N/A                             | ICE sets this to “384810002”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| &lt;substanceAdministrationGeneralPurpose codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                        | UUID     | Y               | N/A                             | ICE sets this to “2.16.840.1.113883.6.5”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &lt;administrationTimeInterval low&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                          | TS       | Y               | Y                               | Date that a shot was administered. ICE sets the date value in the format YYYYMMDD                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| &lt;administrationTimeInterval high&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                         | TS       | Y               | Y                               | Date that the shot was administered. Set to the same date value as administrationTimeInterval.low (format YYYYMMDD)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;isValid value&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                           | boolean  | N               | N/A                             | Set to true if component vaccine evaluated as VALID; false if anything other than VALID                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;substance&gt; This element was not provided on input but is present on output if ancestor &lt;substanceAdministrationEvent/&gt; is present. This section indicates the component vaccine in question and it is not repeatable.                                      |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | N/A                             | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | N/A                             | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceCode code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | String   | Y               | N                               | CVX code of the component vaccine.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| &lt;substanceCode codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                | UUID     | Y               | N                               | ICE sets this to “2.16.840.1.113883.12.292”, the OID for CVX codes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| &lt;substanceCode displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | String   | N               | N                               | Display name corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &lt;substanceCode originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | N               | N                               | Original text corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt; This element was not provided on input but is present on output for its ancestor &lt;substanceAdministrationEvent/&gt;. It is a continuation of evaluation information for the component vaccine and not repeated.                     |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;targetRelationshipToSource code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                         | String   | Y               | N/A                             | Set to “PERT”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt; Continuation of evaluation information for the component vaccine and not repeatable.                                                                                                                          |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | N/A                             | Set to “2.16.840.1.113883.3.795.11.6.3.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | N/A                             | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | N/A                             | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;observationFocus code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | String   | Y               | N/A                             | Code value specifying the focus of this observation related to evaluation of this component vaccine. Refer to the code table for the below codeSystem for valid values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| &lt;observationFocus codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y               | N/A                             | Code System used by ICE to interpret the above observationFocus code. Set to “2.16.840.1.113883.3.795.12.100.1” which indicates the vaccine group component that is being evaluated. (e.g. - “Immunization Validity (Hep B Component)”)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| &lt;interpretation code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | N               | N/A                             | Interpretation element is repeatable. The code value specifies how ICE should interpret the nested &lt;observationValue&gt;. Refer to the code table for the below codeSystem for valid values                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| &lt;interpretation codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | UUID     | N               | N/A                             | Interpretation element is repeatable. Set to “2.16.840.1.113883.3.795.12.100.3”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;interpretation displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | N               | N/A                             | Interpretation element is repeatable. Display name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;interpretation originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                             | String   | N               | N/A                             | Interpretation element is repeatable. Original text name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt;&lt;observationValue&gt; Continuation of evaluation information for the component vaccine and not repeatable.                                                                                                  |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;concept code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | Y               | N/A                             | Code value specifying the evaluation validity with respect to the above &lt;observationFocus/&gt;. That is, is the component vaccine VALID, INVALID, etc.? Refer to the code table for the below codeSystem for valid values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| &lt;concept codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | UUID     | Y               | N/A                             | Code System used by ICE to interpret the above concept code. ICE sets this to “2.16.840.1.113883.3.795.12.100.2”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| &lt;concept displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | N               | N/A                             | Display name corresponding with the above observation value code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| &lt;concept originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | String   | N               | N/A                             | Original text corresponding to the above observation value code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt; This section is optional. Specified when there are vaccine forecasts. Vaccine forecasts are broken up by vaccine group, and each vaccine group recommendation is specified by a &lt;substanceAdministrationProposal&gt; section                                                                                                                                             |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt; Repeated for each vaccine group recommendation.                                                                                                                                                                                                                                                                                      |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | N/A                             | ICE sets this to “2.16.840.1.113883.3.795.11.9.3.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | N/A                             | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | N/A                             | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceAdministrationGeneralPurpose code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | Y               | N/A                             | ICE sets this to “384810002”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| &lt;substanceAdministrationGeneralPurpose codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                        | UUID     | Y               | N/A                             | Set to “2.16.840.1.113883.6.5”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| &lt;proposedAdministrationTimeInterval low&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                  | TS       | N               | N/A                             | If a vaccine is due to be administered on a particular date, this element attribute is included in the output and represents the recommended date. ICE sets the date in the YYYYMMDD format. Implementations should always obtain the recommended date from this attribute, and not the “high” attribute (specified below).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;proposedAdministrationTimeInterval high&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                 | TS       | N               | N/A                             | If a vaccine is due to be administered on a particular date and the “output_earliest_and_overdue_dates” property is set to “Y” in the ice.properties file, this element attribute is included in the output and represents the past due date (a.k.a. - overdue date). The past due date is the date after which an immunization administered would be considered late. ICE sets the date in the YYYYMMDD format. If there is no past due date, this attribute is not included. Note: If the “output_earliest_and_overdue_dates” property is not set to “Y” in the ice.properties file, this attribute is set to the recommended date (i.e. – the same date as the “low” attribute specified above). This is done for backwards compatibility to previous implementations of ICE. However, moving forward, applications should be sure to obtain the earliest recommended date from the “low” attribute only. |
| &lt;validAdministrationTimeInterval low&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                     | TS       | N               | N/A                             | If a vaccine is due to be administered on a particular date and the “output_earliest_and_overdue_dates” property is set to “Y” in the ice.properties file, this element is included in the output and represents the earliest date. The earliest due date is the earliest date that the vaccine can be given and still be considered valid. ICE sets the date in the YYYYMMDD format.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;substance&gt; Continuation of the vaccine forecast within its ancestor &lt;substanceAdministrationProposal&gt;. This element is not repeatable.                                                                                                                                                                                   |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | N/A                             | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | N/A                             | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;substanceCode code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | String   | Y               | N/A                             | Vaccine or vaccine group code being recommended. If a specific vaccine is recommended, ICE will populate this with a CVX code. More commonly, this attribute will be populated with the vaccine group.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| &lt;substanceCode codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                | UUID     | Y               | N/A                             | The codeSystem representing the vaccine group for which this recommendation is being made. Currently, ICE sets this to “2.16.840.1.113883.3.795.12.100.1” if a vaccine group; “2.16.840.1.113883.12.292” if a vaccine. Note that the calling application must also look at the nested &lt;relatedClinicalStatement&gt; to get all of the parameters for the forecast.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| &lt;substanceCode displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | String   | N               | N/A                             | Display name corresponding with the above CVX or vaccine group code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;substanceCode originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | N               | N/A                             | Original text corresponding with the above CVX or vaccine group code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;relatedClinicalStatement&gt; This element is a continuation of the vaccine forecast within its ancestor &lt;substanceAdministrationProposal&gt; and contains the forecast                                                                                                                                                         |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;targetRelationshipToSource code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                         | String   | Y               | N/A                             | ICE always sets this to “RSON”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| &lt;targetRelationshipToSource codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                   | UUID     | Y               | N/A                             | ICE always sets this to “2.16.840.1.113883.5.1002”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt; This element is a continuation of the vaccine forecast within its ancestor &lt;relatedClinicalStatement&gt;. It indicates the vaccine group in question and also indicates the reasons for the recommendation in the nested &lt;observationValue&gt;. It is not repeatable. |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;templateId root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | UUID     | Y               | N/A                             | ICE always sets this to “2.16.840.1.113883.3.795.11.6.3.1”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| &lt;id root&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | UUID     | Y               | N/A                             | UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| &lt;id extension&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | N               | N/A                             | Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| &lt;observationFocus code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | String   | Y               | N/A                             | Vaccine group code for which this recommendation is being made.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;observationFocus codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                             | UUID     | Y               | N/A                             | This codeSystem attribute will be set to “2.16.840.1.113883.3.795.12.100.1”, which indicates the vaccine group for which the recommendation is being made. (e.g. - “Immunization Recommendation Focus (Hep B)”).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| &lt;interpretation code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | Y               | N/A                             | Interpretation element is repeatable. The code value specifies how ICE should interpret the nested &lt;observationValue&gt;. Refer to the code table for the below codeSystem for valid values                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| &lt;interpretation codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                               | UUID     | Y               | N/A                             | Interpretation element is repeatable. ICE always set this to “2.16.840.1.113883.3.795.12.100.6”. Note: Until Release 1.9.1, at most one Interpretation element was returned for each SubstanceAdministrationProposal. The Meningococcal B vaccine group, introduced in 1.9.1, may return multiple Interpretation elements for its SubstanceAdministrationProposal.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| &lt;interpretation displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                              | String   | N               | N/A                             | Interpretation element is repeatable. Display name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| &lt;interpretation originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                             | String   | N               | N/A                             | Interpretation element is repeatable. Original text name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| &lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt;&lt;observationValue&gt; This element is a continuation of the vaccine forecast within its ancestor &lt;observationResult&gt;. It specifies whether a shot is recommended, not recommended, conditionally recommended, etc. It is not repeatable.                            |          |                 |                                 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| &lt;concept code&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | String   | Y               | N/A                             | Code value specifying the recommendation with respect to the above &lt;observationFocus/&gt;. Refer to the code table for the below codeSystem for valid values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| &lt;concept codeSystem&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | UUID     | Y               | N/A                             | Code System used by ICE to interpret the above concept code. ICE sets this to “2.16.840.1.113883.3.795.12.100.5”                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| &lt;concept displayName&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | String   | N               | N/A                             | Display name corresponding with the above observation value code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| &lt;concept originalText&gt;                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | String   | N               | N/A                             | Original text corresponding to the above observation value code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |

# Code Tables

Below are the code systems and values that are used in ICE’s input and output messages. When constructing or processing messages,
client applications should use the code values in the tables below.

## Vaccines

### CVX - Code System 2.16.840.1.113883.12.292

Below is a list of the CVX codes accepted by this version of ICE. See following tables in this section for mapping of these vaccines
to those accepted by each vaccine group.

| Code Value | Description                                                                                                                                  |
|------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| 01         | DTP                                                                                                                                          |
| 02         | OPV                                                                                                                                          |
| 03         | MMR                                                                                                                                          |
| 04         | Measles/Rubella                                                                                                                              |
| 05         | Measles                                                                                                                                      |
| 06         | Rubella                                                                                                                                      |
| 07         | Mumps                                                                                                                                        |
| 08         | Hep B Peds &lt; 20 years                                                                                                                     |
| 09         | Td adult (absorbed)                                                                                                                          |
| 10         | IPV                                                                                                                                          |
| 15         | Influenza, Split                                                                                                                             |
| 16         | Influenza, Whole                                                                                                                             |
| 17         | Hib NOS                                                                                                                                      |
| 20         | DTaP                                                                                                                                         |
| 21         | Varicella                                                                                                                                    |
| 22         | DTP-Hib  (Tetramune; OmniHib-DTP)                                                                                                            |
| 25         | Typhoid, oral                                                                                                                                |
| 28         | DT (pediatric)                                                                                                                               |
| 31         | HepA Pediatric NOS                                                                                                                           |
| 32         | Meningococcal MPSV4 (Menomune)                                                                                                               |
| 33         | Pneumococcal Polysaccharide 23 valent                                                                                                        |
| 37         | Yellow fever live                                                                                                                            |
| 38         | Mumps/Rubella                                                                                                                                |
| 46         | Hib-PRP-D (ProHIBIT)                                                                                                                         |
| 47         | Hib-HbOC (HibTITER)                                                                                                                          |
| 48         | Hib-PRP-T (ActHIB, Hiberix)                                                                                                                  |
| 49         | Hib-PRP-OMP (PedvaxHIB)                                                                                                                      |
| 42         | Hep B High Risk Infant                                                                                                                       |
| 43         | Hep B Adult &gt;= 20 years                                                                                                                   |
| 44         | Hep B Dialysis                                                                                                                               |
| 45         | Hep B NOS                                                                                                                                    |
| 50         | DTaP-Hib (TriHiBit)                                                                                                                          |
| 51         | Hep B-Hib (PRP-OMP (ComVAX))                                                                                                                 |
| 52         | Hep A Adult                                                                                                                                  |
| 62         | HPV Quadrivalent (Gardasil)                                                                                                                  |
| 74         | Rotavirus                                                                                                                                    |
| 75         | vaccinia (smallpox) vaccine, diluted                                                                                                         |
| 83         | HepA ped/adol 2 dose                                                                                                                         |
| 84         | HepA pediatric/adolescent (3 dose)                                                                                                           |
| 85         | HepA NOS                                                                                                                                     |
| 88         | Influenza, unspecified formulation                                                                                                           |
| 89         | Polio, unspecified formulation                                                                                                               |
| 94         | MMR-Varicella                                                                                                                                |
| 100        | Pneumococcal Conjugate 7 valent (PCV 7)                                                                                                      |
| 101        | Typhoid, Vi capsular polysaccharide (ViCPS)                                                                                                  |
| 102        | DTP-Hib-HepB                                                                                                                                 |
| 104        | Twinrix                                                                                                                                      |
| 105        | vaccinia (smallpox) vaccine, diluted (Inactive; not preferred)                                                                               |
| 106        | DTaP, 5 pertussis antigens                                                                                                                   |
| 107        | DTaP, unspecified formulation                                                                                                                |
| 108        | Meningococcal, unspecified formulation                                                                                                       |
| 109        | Pneumococcal NOS                                                                                                                             |
| 110        | DTaP/HepB/IPV                                                                                                                                |
| 111        | influenza, live, intranasal                                                                                                                  |
| 113        | Td (adult) preservative free                                                                                                                 |
| 114        | Meningococcal MCV4P (Menactra)                                                                                                               |
| 115        | Tdap                                                                                                                                         |
| 116        | Rotavirus RV5 (RotaTeq, 3-dose)                                                                                                              |
| 118        | HPV Bivalent (Cervarix)                                                                                                                      |
| 119        | Rotavirus RV1 (Rotarix, 2-dose)                                                                                                              |
| 120        | DTaP-Hib (PRP-T)-IPV                                                                                                                         |
| 121        | Zoster vaccine, live                                                                                                                         |
| 122        | Rotavirus NOS                                                                                                                                |
| 125        | Novel Influenza-H1N1-09, nasal                                                                                                               |
| 126        | Novel influenza-H1N1-09, preservative-free                                                                                                   |
| 127        | Novel influenza-H1N1-09                                                                                                                      |
| 128        | Novel Influenza-H1N1-09, all formulations                                                                                                    |
| 130        | DTaP/IPV                                                                                                                                     |
| 132        | DTaP-IPV-Hib-HepB, Historical                                                                                                                |
| 133        | Pneumococcal Conjugate 13 (PCV 13)                                                                                                           |
| 134        | Japanese Encephalitis IM (Ixiaro)                                                                                                            |
| 135        | Influenza, high dose, seasonal                                                                                                               |
| 136        | Meningococcal MCV4O (Menveo)                                                                                                                 |
| 137        | HPV NOS                                                                                                                                      |
| 138        | Td (adult, not adsorbed)                                                                                                                     |
| 139        | Td, adult NOS                                                                                                                                |
| 140        | Influenza, seasonal, injectable, preservative free                                                                                           |
| 141        | Influenza, seasonal, injectable                                                                                                              |
| 144        | Influenza, seasonal, intradermal, preservative free                                                                                          |
| 146        | DTaP-IPV-Hib-HepB                                                                                                                            |
| 147        | Meningococcal MCV4, unspecified formulation                                                                                                  |
| 148        | Mening C&Y-Hib PRP-T (Menhibrix) (Only Hib component evaluated)                                                                              |
| 149        | Influenza, live, intranasal, quadrivalent                                                                                                    |
| 150        | Influenza, injectable, quadrivalent, preservative free                                                                                       |
| 151        | Influenza nasal, unspecified formulation                                                                                                     |
| 153        | Influenza, injectable, MDCK, preservative free                                                                                               |
| 155        | Influenza, injectable, MDCK, preservative free                                                                                               |
| 158        | Influenza-IIV4, IM (&gt;3yrs)                                                                                                                |
| 161        | Influenza, injectable, quadrivalent, preservative free, pediatric                                                                            |
| 162        | Meningococcal B FHbp, recombinant (Trumenba)                                                                                                 |
| 163        | Meningococcal B 4C, OMV (Bexsero)                                                                                                            |
| 164        | Meningococcal B, NOS                                                                                                                         |
| 165        | HPV9                                                                                                                                         |
| 166        | Influenza, intradermal, quadrivalent, preservative free, injectable                                                                          |
| 168        | Seasonal trivalent influenza vaccine, adjuvanted, preservative free                                                                          |
| 170        | DTaP-IPV-Hib                                                                                                                                 |
| 171        | Influenza, injectable, Madin Darby Canine Kidney, preservative free, quadrivalent                                                            |
| 174        | Cholera, live attenuated (Vaxchora)                                                                                                          |
| 177        | Pneumococcal Conjugate PCV10                                                                                                                 |
| 178        | OPV bivalent                                                                                                                                 |
| 179        | OPV ,monovalent, unspecified (NOS)                                                                                                           |
| 182        | OPV, Unspecified (NOS)                                                                                                                       |
| 183        | Yellow fever vaccine live - alt                                                                                                              |
| 184        | Yellow fever, unspecified formulation (NOS)                                                                                                  |
| 185        | Influenza, recombinant, quadrivalent, injectable, preservative free                                                                          |
| 186        | Influenza, injectable, MDCK, quadrivalent                                                                                                    |
| 187        | Zoster vaccine recombinant                                                                                                                   |
| 188        | Zoster vaccine, unspecified formulation (NOS)                                                                                                |
| 189        | Hep B, adjuvanted                                                                                                                            |
| 194        | Influenza, Southern Hemisphere, unspecified formulation                                                                                      |
| 197        | Influenza, high dose, quadrivalent                                                                                                           |
| 198        | DTP-Hep B-Hib Pentavalent Non-US                                                                                                             |
| 200        | Influenza, Southern Hemisphere, pediatric, preservative free                                                                                 |
| 201        | Influenza, Southern Hemisphere, preservative free                                                                                            |
| 202        | Influenza, Southern Hemisphere, quadrivalent, with preservative                                                                              |
| 203        | Meningococcal MenACWY-TT                                                                                                                     |
| 205        | Influenza, seasonal vaccine, quadrivalent, adjuvanted                                                                                        |
| 206        | Vaccinia, smallpox mpox vaccine live, PF, SQ or ID injection                                                                                 |
| 207        | COVID-19, mRNA, LNP-S, PF 100 mcg/0.5 mL (Moderna)                                                                                           |
| 208        | COVID-19, mRNA, LNP-S, PF, 30 mcg/0.3 mL dose (Pfizer)                                                                                       |
| 210        | COVID-19 vaccine, vector-nr, rS-ChAdOx1, PF, 0.5 mL (AstraZeneca)                                                                            |
| 211        | COVID-19 vaccine, Subunit, rS-nanoparticle+Matrix-M1 Adjuvant, PF, 0.5 mL                                                                    |
| 212        | COVID-19 vaccine, vector-nr, rS-Ad26, PF, 0.5 mL (Janssen)                                                                                   |
| 213        | COVID-19 vaccine, UNSPECIFIED                                                                                                                |
| 215        | Pneumococcal conjugate PCV15, polysaccharide CRM197 conjugate, adjuvant, PF                                                                  |
| 216        | Pneumococcal conjugate PCV20, polysaccharide CRM197 conjugate, adjuvant, PF                                                                  |
| 217        | COVID-19, mRNA, LNP-S, PF, 30 mcg/0.3 mL dose, tris-sucrose (Pfizer)                                                                         |
| 218        | COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL dose, tris-sucrose (Pfizer)                                                                         |
| 219        | Pfizer COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 5 years)                                                               |
| 220        | HepB recombinant, 3-antigen, Al(OH)3                                                                                                         |
| 221        | Moderna COVID-19 Vaccine (Preferable age ranges: &gt;= 6 years to &lt; 12 years OR &gt;= 18 years)                                           |
| 227        | Moderna COVID-19 Vaccine (Inactive)                                                                                                          |
| 228        | Moderna COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 6 years)                                                              |
| 229        | Moderna COVID-19 Vaccine, Bivalent Booster (Preferable Age Range: &gt; 6 years and &lt; 12 years (0.25mL dose); &gt;= 12 years (0.5mL dose)) |
| 231        | Influenza, Southern Hemisphere, high-dose, quadrivalent                                                                                      |
| 300        | Pfizer COVID-19 Vaccine, Bivalent Booster (Preferable Age Range: &gt;= 12 years)                                                             |
| 301        | Pfizer COVID-19 Vaccine, Bivalent Booster (Preferable Age Range: &gt; 5 years and &lt;= 12 years)                                            |
| 303        | RSV, recombinant (Adult)                                                                                                                     |
| 304        | Respiratory syncytial virus (RSV), unspecified                                                                                               |
| 305        | RSV, bivalent, PF (Adult)                                                                                                                    |
| 306        | RSV, mAb, 0.5 mL, age 0 - &lt; 8 mos.                                                                                                        |
| 307        | RSV, mAb, 1 mL, age 0 - 19 mos.                                                                                                              |
| 308        | COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 6 mos-4 yrs)                                                                     |
| 309        | COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 12+ yrs)                                                                         |
| 310        | COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 5-11 yrs)                                                                        |
| 311        | COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 6 mos-11 yrs)                                                                                  |
| 312        | COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 12+ yrs)                                                                                       |
| 313        | COVID-19, subunit, rS-nanoparticle, adjuvanted, PF, 12+ yrs, Novavax (12+ yrs)                                                               |
| 314        | Respiratory syncytial virus (RSV) vaccine, unspecified                                                                                       |
| 315        | Respiratory syncytial virus (RSV) MAB, unspecified                                                                                           |
| 316        | Meningococcal MenABCWY (Penbraya)                                                                                                            |
| 320        | Influenza, MDCK, trivalent, preservative                                                                                                     |
| 324        | Poliovirus, inactivated, fractional-dose (fIPV)                                                                                              |
| 325        | Vaccinia (smallpox, mpox), unspecified (NOS)                                                                                                 |
| 326        | RSV, mRNA, injectable, PF                                                                                                                    |
| 327        | Pneumococcal conjugate PCV21, polysaccharide CRM197 conjugate, PF                                                                            |
| 328        | Meningococcal MenABCWY (Penmenvy)                                                                                                            |
| 331        | **Influenza, Southern Hemisphere, trivalent, preservative free**                                                                             |
| 332        | **RSV, mAb, 0.7 mL, age 0 - &lt; 8 mos. (clesrovimab)**                                                                                      |
| 333        | Influenza, live, intranasal, self/caregiver admin                                                                                            |
| 334        | COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL, Moderna (mNEXSPIKE, 12+ yrs)                                                                       |
| 337        | Influenza, Southern Hemisphere, high-dose, trivalent, PF                                                                                     |
| 500        | COVID-19 Non-US Vaccine, Product Unknown                                                                                                     |
| 501        | COVID-19 IV Non-US Vaccine (QAZCOVID-IN)                                                                                                     |
| 502        | COVID-19 IV Non-US Vaccine (COVAXIN)                                                                                                         |
| 503        | COVID-19 LAV Non-US Vaccine (COVIVAC)                                                                                                        |
| 504        | COVID-19 VVnr Non-US Vaccine (Sputnik Light)                                                                                                 |
| 505        | COVID-19 VVnr Non-US Vaccine (Sputnik V)                                                                                                     |
| 506        | COVID-19 VVnr Non-US Vaccine (CanSino Biological Inc./Beijing Institute of Biotechnology)                                                    |
| 507        | COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom Biopharm + Inst of Micro, Chinese Acad of Sciences)                                         |
| 508        | COVID-19 PS Non-US Vaccine (Jiangsu Province Centers for Disease Control and Prevention)                                                     |
| 509        | COVID-19 PS Non-US Vaccine (EpiVacCorona)                                                                                                    |
| 510        | COVID-19 IV Non-US Vaccine (BIBP, Sinopharm)                                                                                                 |
| 511        | COVID-19 IV Non-US Vaccine (CoronaVac, Sinovac)                                                                                              |
| 512        | COVID-19 VLP Non-US Vaccine (Medicago, Covifenz)                                                                                             |
| 513        | COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom, Zifivax)                                                                                   |
| 514        | COVID-19 DNA Non-US Vaccine (Zydus Cadila, ZyCoV-D)                                                                                          |
| 515        | COVID-19 PS Non-US Vaccine (Medigen, MVC-COV1901)                                                                                            |
| 516        | COVID-19 Inactivated Non-US Vaccine (Minhai Biotechnology Co, KCONVAC)                                                                       |
| 517        | COVID-19 PS Non-US Vaccine (Biological E Limited, Corbevax)                                                                                  |
| 518        | COVID-19 Inactivated, Non-US Vaccine (VLA2001, Valneva)                                                                                      |
| 519        | COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine (Spikevax Bivalent), Moderna                                                  |
| 520        | COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine Product, Pfizer-BioNTech                                                      |
| 521        | COVID-19 SP, protein-based, adjuvanted (VidPrevtyn Beta), Sanofi-GSK                                                                         |

### Vaccines by Vaccine Group

#### Hep A

| CVX Code | Name                               |
|----------|------------------------------------|
| 83       | HepA ped/adol 2 dose               |
| 84       | HepA pediatric/adolescent (3 dose) |
| 31       | HepA pediatric NOS                 |
| 52       | HepA adult                         |
| 85       | HepA NOS                           |
| 104      | HepA-HepB (Twinrix)                |

#### Hep B

| **CVX Code** | **Name**                             |
|--------------|--------------------------------------|
| 08           | HepB peds `&lt;20yrs                 |
| 42           | HepB high risk infant                |
| 45           | HepB NOS                             |
| 43           | HepB adult =&gt;`20yrs               |
| 44           | HepB-dialysis                        |
| 51           | Hib/HepB (Comvax)                    |
| 102          | DTP-Hib-HepB                         |
| 110          | DTaP-HepB-IPV (Pediarix)             |
| 104          | HepA-HepB (Twinrix)                  |
| 132          | **DTaP-IPV-Hib-HepB, historical**    |
| 146          | **DTaP, IPV, Hib, Hep B**            |
| 189          | **Hep B, adjuvanted**                |
| 198          | **DTP-Hep B-Hib Pentavalent Non-US** |
| 220          | HepB recombinant, 3-antigen, Al(OH)3 |

#### MMR

| **CVX Code** | **Name**        |
|--------------|-----------------|
| 03           | MMR             |
| 05           | Measles         |
| 06           | Rubella         |
| 07           | Mumps           |
| 04           | Measles/Rubella |
| 38           | Mumps/Rubella   |
| 94           | MMR-Varicella   |

#### Varicella

| **CVX Code** | **Name**      |
|--------------|---------------|
| 21           | Varicella     |
| 94           | MMR-Varicella |

#### Rotavirus

| **CVX Code** | **Name**                        |
|--------------|---------------------------------|
| 116          | Rotavirus RV5 (RotaTeq, 3 dose) |
| 119          | Rotavirus RV1 (Rotarix, 2 dose) |
| 122          | Rotavirus NOS                   |
| 74           | Rotavirus                       |

#### Hib

| **CVX Code** | **Name**                                                            |
|--------------|---------------------------------------------------------------------|
| 46           | Hib-PRP-D (ProHIBIT)                                                |
| 47           | Hib-HbOC (HibTITER)                                                 |
| 48           | Hib-PRP-T (ActHIB, Hiberix)                                         |
| 49           | Hib-PRP-OMP (PedvaxHIB)                                             |
| 17           | Hib NOS                                                             |
| 50           | DTaP-Hib (TriHiBit)                                                 |
| 51           | Hep B-Hib (PRP-OMP (ComVAX)                                         |
| 120          | DTaP-Hib (PRP-T)-IPV                                                |
| 22           | DTP-Hib  (Tetramune; OmniHib-DTP)                                   |
| 102          | **DTP-Hib-HepB**                                                    |
| 132          | **DTaP-IPV-Hib-HepB, historical**                                   |
| 146          | **DTaP-IPV-Hib-HepB**                                               |
| 148          | **Mening C&Y-Hib PRP-T (Menhibrix) (Only Hib component evaluated)** |
| 170          | **DTaP-IPV-Hib**                                                    |
| 198          | **DTP-Hep B-Hib Pentavalent Non-US**                                |

#### HPV

| **CVX Code** | **Name**                    |
|--------------|-----------------------------|
| 62           | HPV Quadrivalent (Gardasil) |
| 118          | HPV Bivalent (Cervarix)     |
| 137          | HPV NOS                     |
| 165          | HPV9                        |

#### Pneumococcal

| **CVX Code** | **Name**                                                                        |
|--------------|---------------------------------------------------------------------------------|
| 100          | **Pneumococcal Conjugate 7 valent (PCV 7)**                                     |
| 133          | **Pneumococcal Conjugate 13 (PCV 13)**                                          |
| 109          | **Pneumococcal NOS**                                                            |
| 152          | **Pneumoccocal Conjugate NOS**                                                  |
| 177          | **Pneumococcal Conjugate PCV10**                                                |
| 33           | **Pneumococcal Polysaccharide 23 valent**                                       |
| 215          | **Pneumococcal conjugate PCV15, polysaccharide CRM197 conjugate, adjuvant, PF** |
| 216          | **Pneumococcal conjugate PCV20, polysaccharide CRM197 conjugate, adjuvant, PF** |
| 327          | **Pneumococcal conjugate PCV21, polysaccharide CRM197 conjugate, PF**           |

#### Influenza

| **CVX Code** | **Name**                                                                          |
|--------------|-----------------------------------------------------------------------------------|
| 15           | influenza, split                                                                  |
| 16           | influenza, whole                                                                  |
| 88           | influenza, unspecified formulation                                                |
| 111          | influenza, live, intranasal                                                       |
| 135          | influenza, high dose, seasonal                                                    |
| 140          | influenza, seasonal, injectable, preservative free                                |
| 141          | influenza, seasonal, injectable                                                   |
| 144          | influenza, seasonal, intradermal, preservative free                               |
| 149          | influenza, live, intranasal, quadrivalent                                         |
| 150          | influenza, injectable, quadrivalent, preservative free                            |
| 151          | influenza nasal, unspecified formulation                                          |
| 153          | influenza, injectable, MDCK, preservative free                                    |
| 155          | influenza, recombinant, injectable, preservative free                             |
| 158          | Influenza-IIV4, IM (\&gt;3yrs)                                                    |
| 161          | Influenza, injectable, quadrivalent, preservative free, pediatric                 |
| 166          | Influenza, intradermal, quadrivalent, preservative free, injectable               |
| 168          | Seasonal trivalent influenza vaccine, adjuvanted, preservative free               |
| 171          | Influenza, injectable, Madin Darby Canine Kidney, preservative free, quadrivalent |
| 185          | Influenza, recombinant, quadrivalent, injectable, preservative free               |
| 186          | Influenza, injectable, MDCK, quadrivalent                                         |
| 194          | Influenza, Southern Hemisphere, unspecified formulation                           |
| 197          | Influenza, high dose, quadrivalent                                                |
| 200          | Influenza, Southern Hemisphere, pediatric, preservative free                      |
| 201          | Influenza, Southern Hemisphere, preservative free                                 |
| 202          | Influenza, Southern Hemisphere, quadrivalent, with preservative                   |
| 205          | Influenza, seasonal vaccine, quadrivalent, adjuvanted                             |
| 231          | Influenza, Southern Hemisphere, high-dose, quadrivalent                           |
| 320          | Influenza, MDCK, trivalent, preservative                                          |
| 331          | **Influenza, Southern Hemisphere, trivalent, preservative free**                  |
| 333          | Influenza, live, intranasal, self/caregiver admin                                 |
| 337          | Influenza, Southern Hemisphere, high-dose, trivalent, PF                          |

#### H1N1

| **CVX Code** | **Name**                                   |
|--------------|--------------------------------------------|
| 125          | Novel Influenza-H1N1-09, nasal             |
| 126          | Novel influenza-H1N1-09, preservative-free |
| 127          | Novel influenza-H1N1-09                    |
| 128          | Novel Influenza-H1N1-09, all formulations  |

#### Meningococcal ACWY

| **CVX Code** | **Name**                                                        |
|--------------|-----------------------------------------------------------------|
| 114          | meningococcal MCV4P (Menactra)                                  |
| 136          | meningococcal MCV4O (Menveo)                                    |
| 32           | meningococcal MPSV4 (Menomune)                                  |
| 108          | meningococcal, unspecified formulation                          |
| 147          | meningococcal MCV4, unspecified formulation                     |
| 148          | Mening C&Y-Hib PRP-T (Menhibrix) (Only Hib component evaluated) |
| 203          | Meningococcal MenACW-TT (MenQuadfi)                             |
| 316          | Meningococcal MenABCWY (Penbraya)                               |
| 328          | Meningococcal MenABCWY (Penmenvy)                               |

#### Polio

| **CVX Code** | **Name**                                            |
|--------------|-----------------------------------------------------|
| 02           | OPV                                                 |
| 10           | IPV                                                 |
| 89           | polio, unspecified formulation                      |
| 110          | DTaP/HepB/IPV                                       |
| 120          | DTaP/IPV/Hib                                        |
| 130          | DTaP/IPV                                            |
| 132          | **DTaP-IPV-Hib-HepB, historical**                   |
| 146          | **DTaP-IPV-Hib-HepB**                               |
| 170          | **DTaP-IPV-Hib**                                    |
| 178          | **OPV bivalent**                                    |
| 179          | **OPV ,monovalent, unspecified (NOS)**              |
| 182          | **OPV, unspecified (NOS)**                          |
| 324          | **Poliovirus, inactivated, fractional-dose (fIPV)** |

#### DTP

| **CVX Code** | **Name**                             |
|--------------|--------------------------------------|
| 01           | DTP                                  |
| 09           | Td (adult), absorbed                 |
| 20           | DTaP                                 |
| 22           | **DTP-Hib (Tetramune; OmniHib-DTP)** |
| 28           | DT (pediatric)                       |
| 50           | **DTaP-Hib (TriHiBit)**              |
| 102          | **DTP-Hib-Hep B**                    |
| 106          | DTaP, 5 pertussis antigens           |
| 107          | DTaP, unspecified formulation        |
| 110          | **DTaP-Hep B-IPV (Pediarix)**        |
| 113          | Td (adult) preservative free         |
| 115          | Tdap                                 |
| 120          | **DTaP-Hib-IPV (Pentacel)**          |
| 130          | **DTaP-IPV**                         |
| 132          | **DTaP-IPV-Hib-HepB, historical**    |
| 138          | Td (adult, not adsorbed)             |
| 139          | Td (adult) NOS                       |
| 146          | **DTaP, IPV, Hib, Hep B**            |
| 170          | **DTaP-IPV-Hib**                     |
| 198          | **DTP-Hep B-Hib Pentavalent Non-US** |

#### Zoster

| **CVX Code** | **Name**                                      |
|--------------|-----------------------------------------------|
| 121          | Zoster vaccine, live                          |
| 187          | Zoster vaccine, recombinant                   |
| 188          | Zoster vaccine, unspecified formulation (NOS) |

#### Meningococcal B

| **CVX Code** | **Name**                                         |
|--------------|--------------------------------------------------|
| 162          | **Meningococcal B FHbp, recombinant (Trumenba)** |
| 163          | **Meningococcal B 4C, OMV (Bexsero)**            |
| 316          | Meningococcal MenABCWY (Penbraya)                |
| 328          | Meningococcal MenABCWY (Penmenvy)                |

#### COVID-19

| CVX Code | Name                                                                                                                                             |
|----------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| 207      | **COVID-19, mRNA, LNP-S, PF 100mcg/0.5 mL (Moderna)**                                                                                            |
| 208      | **COVID-19, mRNA, LNP-S, PF 30 mcg/0.3 mL (Pfizer)**                                                                                             |
| 210      | **COVID-19 vaccine, vector-nr, rS-ChAdOx1, PF, 0.5 mL (AstraZeneca)**                                                                            |
| 211      | **COVID-19 vaccine, Subunit, rS-nanoparticle+Matrix-M1 Adjuvant, PF, 0.5 mL (Novavax)**                                                          |
| 212      | **COVID-19 vaccine, vector-nr, rS-Ad26, PF, 0.5 mL (Janssen)**                                                                                   |
| 213      | **COVID-19 vaccine, UNSPECIFIED**                                                                                                                |
| 217      | **COVID-19, mRNA, LNP-S, PF, 30 mcg/0.3 mL dose, tris-sucrose (Pfizer)**                                                                         |
| 218      | **COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL dose, tris-sucrose (Pfizer)**                                                                         |
| 219      | **Pfizer COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 5 years)**                                                               |
| 221      | **Moderna COVID-19 Vaccine (Preferable age ranges: &gt;= 6 years to &lt; 12 years OR &gt;= 18 years)**                                           |
| 227      | **Moderna COVID-19 Vaccine (Inactive)**                                                                                                          |
| 228      | **Moderna COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 6 years)**                                                              |
| 229      | **Moderna COVID-19 Vaccine, Bivalent Booster (Preferable Age Range: &gt; 6 years and &lt; 12 years (0.25mL dose); &gt;= 12 years (0.5mL dose))** |
| 300      | **Pfizer COVID-19 Vaccine, Bivalent Booster  (Preferable Age Range: &gt;= 12 years)**                                                            |
| 301      | **Pfizer COVID-19 Vaccine, Bivalent Booster** **(Preferable Age Range: &gt; 5 years and &lt;= 12 years)**                                        |
| 308      | **COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 6 mos-4 yrs)**                                                                     |
| 309      | **COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 12+ yrs)**                                                                         |
| 310      | **COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 5-11 yrs)**                                                                        |
| 311      | **COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 6 mos-11 yrs)**                                                                                  |
| 312      | **COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 12+ yrs)**                                                                                       |
| 313      | **COVID-19, subunit, rS-nanoparticle, adjuvanted, PF, Novavax (12+ yrs)**                                                                        |
| 334      | **COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL, Moderna (mNEXSPIKE, 12+ yrs)**                                                                       |
| 500      | **COVID-19 Non-US Vaccine, Product Unknown**                                                                                                     |
| 501      | **COVID-19 IV Non-US Vaccine (QAZCOVID-IN)**                                                                                                     |
| 502      | **COVID-19 IV Non-US Vaccine (COVAXIN)**                                                                                                         |
| 503      | **COVID-19 LAV Non-US Vaccine (COVIVAC)**                                                                                                        |
| 504      | **COVID-19 VVnr Non-US Vaccine (Sputnik Light)**                                                                                                 |
| 505      | **COVID-19 VVnr Non-US Vaccine (Sputnik V)**                                                                                                     |
| 506      | **COVID-19 VVnr Non-US Vaccine (CanSino Biological Inc./Beijing Institute of Biotechnology)**                                                    |
| 507      | **COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom Biopharm + Inst of Micro, Chinese Acad of Sciences)**                                         |
| 508      | **COVID-19 PS Non-US Vaccine (Jiangsu Province Centers for Disease Control and Prevention)**                                                     |
| 509      | **COVID-19 PS Non-US Vaccine (EpiVacCorona)**                                                                                                    |
| 510      | **COVID-19 IV Non-US Vaccine (BIBP, Sinopharm)**                                                                                                 |
| 511      | **COVID-19 IV Non-US Vaccine (CoronaVac, Sinovac)**                                                                                              |
| 512      | **COVID-19 VLP Non-US Vaccine (Medicago, Covifenz)**                                                                                             |
| 513      | **COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom, Zifivax)**                                                                                   |
| 514      | **COVID-19 DNA Non-US Vaccine (Zydus Cadila, ZyCoV-D)**                                                                                          |
| 515      | **COVID-19 PS Non-US Vaccine (Medigen, MVC-COV1901)**                                                                                            |
| 516      | **COVID-19 Inactivated Non-US Vaccine (Minhai Biotechnology Co, KCONVAC)**                                                                       |
| 517      | **COVID-19 PS Non-US Vaccine (Biological E Limited, Corbevax)**                                                                                  |
| 518      | **COVID-19 Inactivated, Non-US Vaccine (VLA2001, Valneva)**                                                                                      |
| 519      | **COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine (Spikevax Bivalent), Moderna**                                                  |
| 520      | **COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine Product, Pfizer-BioNTech**                                                      |
| 521      | **COVID-19 SP, protein-based, adjuvanted (VidPrevtyn Beta), Sanofi-GSK**                                                                         |

#### Mpox

| **CVX Code** | **Name**                                                         |
|--------------|------------------------------------------------------------------|
| 75           | **vaccinia (smallpox, mpox), live**                              |
| 105          | **vaccinia (smallpox) vaccine, diluted**                         |
| 206          | **Vaccinia, smallpox mpox vaccine live, PF, SQ or ID injection** |
| 325          | **Vaccinia (smallpox, mpox), unspecified (NOS)**                 |

#### RSV

| **CVX Code** | **Name**                                                   |
|--------------|------------------------------------------------------------|
| 303          | **RSV, recombinant (Adult)**                               |
| 304          | **Respiratory syncytial virus (RSV), unspecified**         |
| 305          | **RSV, bivalent, PF (Adult)**                              |
| 306          | **RSV, mAb, 0.5 mL, age 0 - \&lt; 8 mos.**                 |
| 307          | **RSV, mAb, 1 mL, age 0 - 19 mos.**                        |
| 314          | **Respiratory syncytial virus (RSV) vaccine, unspecified** |
| 315          | **Respiratory syncytial virus (RSV) MAB, unspecified**     |
| 326          | **RSV, mRNA, injectable, PF**                              |
| 332          | **RSV, mAb, 0.7 mL, age 0 - \&lt; 8 mos. (clesrovimab)**   |

#### Cholera

| **CVX Code** | **Name**                                |
|--------------|-----------------------------------------|
| 174          | **Cholera, live attenuated (Vaxchora)** |

#### Japanese Encephalitis

| **CVX Code** | **Name**                              |
|--------------|---------------------------------------|
| 134          | **Japanese Encephalitis IM (Ixiaro)** |

#### Typhoid

| **CVX Code** | **Name**                                        |
|--------------|-------------------------------------------------|
| 25           | **Typhoid, oral**                               |
| 101          | **Typhoid, Vi capsular polysaccharide (ViCPS)** |

#### Yellow Fever

| **CVX Code** | **Name**                                        |
|--------------|-------------------------------------------------|
| 37           | **Yellow fever live**                           |
| 183          | **Yellow fever vaccine live - alt**             |
| 184          | **Yellow fever, unspecified formulation (NOS)** |
|              |                                                 |

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

## Disease Immunity Reason - Code System 2.16.840.1.113883.3.795.12.100.9

| **Code Value** | **Description** |
|----------------|-----------------|
| IS_IMMUNE      | Is Immune       |

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
| OUTSIDE_SEASON                                                                          | This immunization event occurred was administered outside of the vaccine season.                                                        |
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
| VACCINE_NOT_COUNTED_BASED_ON_ MOST_RECENT_VACCINE_GIVEN (note: remove extraneous space) | The vaccine will not be counted based on the most recent vaccine given. (Most recent vaccine given determines which series is applied.) |
| VACCINE_NOT_PART_OF_THIS_SERIES                                                         | The vaccine is not a part of this series, therefore it will not be counted towards completion of this series.                           |
| VACCINE_NOT_ALLOWED_IN_US                                                               | The vaccine is not allowed in the U.S., and therefore will be marked Invalid.                                                           |
| VACCINE_NOT_APPROVED_IN_US                                                              | The vaccine has not been approved in the U.S.                                                                                           |
| VACCINE_NOT_APPROVED_IN_US_OR_BY_WHO                                                    | The vaccine is not approved for use in the U.S. or by the WHO                                                                           |
| VACCINE_NOT_YET_AVAILABLE_ON_DATE_SPECIFIED                                             | The vaccine was not yet available on the date specified.                                                                                |

## Recommendation Value - Code System 2.16.840.1.113883.3.795.12.100.5

| **Code Value (Returned by ICE)** | **Description (Returned by ICE)**                                                    |
|----------------------------------|--------------------------------------------------------------------------------------|
| RECOMMENDED                      | Recommended                                                                          |
| CONDITIONAL                      | Conditionally Recommended                                                            |
| FUTURE_RECOMMENDED               | Recommended in the Future                                                            |
| NOT_RECOMMENDED                  | Not Recommended                                                                      |
| RECOMMENDATION_NOT_AVAILABLE     | Recommendation Not Available (*e.g.* - ICE did not forecast for unsupported vaccine) |

##        

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

**Revision History**

| **Document Revision**                             | Date                         | Author                                                                     | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|---------------------------------------------------|------------------------------|----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ICE 2.58                                          | 8/18/2026                    | ICE Team                                                                   | No Implementation Guide Changes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ICE 2.57                                          | 5/22/2026                    | ICE Team                                                                   | Added CVX 337 (Influenza, Southern Hemisphere, high-dose, trivalent, PF)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ICE 2.56                                          | 4/28/2026                    | ICE Team                                                                   | (1) Added CVX 324 (Poliovirus, inactivated, fractional-dose (fIPV))                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ICE 2.55                                          | 3/28/2026                    | ICE Team                                                                   | Added BOOSTER_DOSE evaluation reason code.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Release 4.24                                      | 2/4/2026                     | ICE Team                                                                   | (1) Logic fix for COVID-19 2025-2026 season (2) Added 4 new vaccine groups for (non-routine / travel): Cholera vaccine group, Japanese Encephalitis vaccine group, Typhoid vaccine group, Yellow Fever vaccine group                                                                                                                                                                                                                                                                                                                                                                 |
| Release 4.22                                      | 11/18/2025                   | ICE Team                                                                   | (1) Mpox: (i) Added vaccinia (smallpox, mpox), unspecified (NOS) (CVX 325); (ii) a couple of vaccine name changes (see vaccine table descriptions) (2) COVID-19: Added COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL, Moderna (mNEXSPIKE, 12+ yrs); (ii) various vaccine name changes (see vaccine table descriptions)                                                                                                                                                                                                                                                                    |
| Release 4.21                                      | 9/1/2025                     | ICE Team                                                                   | (1) Added Influenza (CVX 331, CVX 333) and RSV vaccines (CVX 332)    (2) Added NOT_SUPPORTED recommendation reason code                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| Release 4.20                                      | 6/30/2025                    | ICE Team                                                                   | (1) Added new vaccine codes: PCV10 (CVX 177); PCV21 (CVX 327) (2) Added new recommendation reason codes: ADMINISTER_PCV15_PCV20_OR_PCV21; ADMINISTER_PCV20_OR_PCV21 (3) New supplemental text for Pneumococcal. See https://docs.google.com/spreadsheets/d/1JB8QN2QeJACJmGdSkCaHr6LQ_8T942-J65Fjf_JDpic/edit?gid=1673907274#gid=1673907274 (4) Fixed incorrect information in sample payload that an ACCEPTED evaluation counts as a valid dose; ACCEPTED evaluations do not count as a valid dose. See News Entry and Release Notes on ICE Wiki for information about this release. |
| Release 4.19                                      | 4/16/2025                    | ICE Team                                                                   | Added CVX code 328 Meningococcal MenABCWY (Penmenvy)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Release 4.18                                      | 2/17/2025                    | ICE Team                                                                   | No Implementation Guide Changes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| Release 4.17                                      | 11/1/2024                    | ICE Team                                                                   | RSV Vaccine Group                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| Release 4.16                                      | 6/21/2024                    | ICE Team                                                                   | Added vaccines: Meningococcal MenABCWY (Penbraya) (CVX 316); Influenza, MDCK, trivalent, preservative (CVX 320)                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| Release 4.15                                      | 3/1/2024                     | ICE Team                                                                   | Updated COVID-19 Logic. See Release Notes on the ICE Wiki                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| Release 4.14                                      | 11/3/2023                    | Daryl Chertcoff / Erin Roche / Vikki Papadouka                             | Support for PCV20 for Child Series. See release notes for details. No changes to ICE Implementation Guide.                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Release 4.13                                      | 10/6/2023                    | Amy Moniz / Daryl Chertcoff / Erin Roche / Nette Arandez / Vikki Papadouka | Added ADMINISTER_TDAP_OR_TD recommendation reason code                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| Release 4.12                                      | 7/20/2023                    | Amy Moniz / Daryl Chertcoff / Erin Roche / Nette Arandez / Vikki Papadouka | (1) New vaccine (Influenza, southern hemisphere) - CVX 231  (2) New evaluation reason code: VACCINE_NOT_YET_AVAILABLE_ON_DATE_SPECIFIED (3) New recommendation reason code: ADMINISTER_COVID19_BIVALENT_VACCINE                                                                                                                                                                                                                                                                                                                                                                      |
| Release 4.10 / 4.11 (ICE versions 1.37 and 1.38)  | 12/2/2022                    | Amy Moniz / Erin Roche / Nette Arandez / Vikki Papadouka / Daryl Chertcoff | (1) New vaccines / CVX codes in COVID-19, DTP, Hib, Hep B and Polio vaccine groups: CVX 198,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Release 4.9 (ICE version 1.36)                    | 8/21/2022                    | Amy Moniz / Karrie Schwencer / Daryl Chertcoff                             | (1) New Vaccine Group – Orthopoxvirus vaccine group – May 2022 Emergency Use Authorization(EUA) for Monkeypox: 860 (2) New vaccines (Orthopoxvirus): CVX 75, 105, 206                                                                                                                                                                                                                                                                                                                                                                                                                |
| Release 4.8 (ICE version 1.35.1)                  | 7/19/2022                    | Amy Moniz / Karrie Schwencer / Daryl Chertcoff                             | (1) New vaccines / CVX codes in Pneumococcal, COVID-19 and Hep B vaccine groups: 215, 216, 219, 220, 221, 227, 228, 512, 513, 514, 515, 516, 517 (2) Added new Evaluation Reason Code: OUTSIDE_ROUTINE_SERIES (3) Added new Recommendation Reason Codes: (i) ADMINISTER_PCV15_OR_PCV20 (ii) ADMINISTER_mRNA_VACCINE                                                                                                                                                                                                                                                                  |
| Release 4.7 (ICE version 1.34)                    | 2/1/2022                     | Amy Moniz / Daryl Chertcoff                                                | No changes.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| Release 4.6 (ICE version 1.33)                    | 12/22/2021                   | Amy Moniz / Daryl Chertcoff                                                | (1) Added new COVID-19 CVX code: 217                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Release 4.5 (ICE version 1.32.1)                  | 11/24/2021                   | Amy Moniz / Daryl Chertcoff                                                | (1) Added new recommendation reason code: BOOSTER_DOSE (2) Added new COVID-19 CVX code: 218                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| Release 4.4 (ICE version 1.30.1)                  | 9/21/2021                    | Amy Moniz / Daryl Chertcoff                                                | (1) Added new evaluation reason code: VACCINE_NOT_APPROVED_IN_US_OR_BY_WHO (2) Added new COVID-19 CVX codes: 211, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511                                                                                                                                                                                                                                                                                                                                                                                                         |
| Release 4.3 (ICE version 1.29)                    | 5/13/2021                    | Amy Moniz / Daryl Chertcoff                                                | (1) Addition of COVID-19 evaluation reason code: VACCINE_NOT_APPROVED_IN_US                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| Release 4.2 (ICE version 1.28)                    | 3/3/2021                     | Amy Moniz / Daryl Chertcoff                                                | (1) Addition of COVID-19 Janssen CVX code (212) (2) Addition of COVID-19 AstraZeneca CVX code (210)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| Release 4.1 (ICE version 1.27.1)                  | 12/23/2020                   | Amy Moniz / Daryl Chertcoff                                                | (1) Addition of COVID-19 Unspecified vaccine code (CVX 213) (2) Addition of recommendation reason code: BASED_ON_VAC_AVAIL_AND_PRIORITY_RECS. This recommendation reason code may be used in the COVID-19 forecast.                                                                                                                                                                                                                                                                                                                                                                  |
| Release 4.0 (ICE version 1.26.1)                  | 12/14/2020                   | Amy Moniz / Daryl Chertcoff                                                | (1) Addition of COVID-19 Pfizer and Moderna vaccines (CVX codes 208, 207) (2) Addition of COVID-19 Vaccine Group to Evaluation Focus and Recommendation Focus tables                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Release 3.9 (ICE version 1.25.1)                  | 12/4/2020                    | Daryl Chertcoff                                                            | (1) Addition of new Meningococcal ACWY CVX code 203 – Meningococcal MenACWY-TT (2) ABOVE_REC_AGE no longer used                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| Release 3.8 (ICE release 1.24.1)                  | 11/6/2020                    | Daryl Chertcoff                                                            | No changes to this guide for this release.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Release 3.7 (ICE release 1.23.1)                  | 9/13/2020                    | Amy Moniz / Daryl Chertcoff                                                | (1) Addition of new Influenza CVX codes: CVX codes 194, 197, 200, 201, 202, 205 (2) Addition of new evaluation reason code: VACCINE_NOT_ALLOWED_IN_US (3) Evaluation reason coded value change: ABOVE_MAX_AGE_VACCINE changed to ABOVE_MAXIMUM_AGE_VACCINE/                                                                                                                                                                                                                                                                                                                          |
| Release 3.6 (ICE                                  | 2/7/2020                     | Amy Moniz / Daryl Chertcoff                                                | (1) Addition of Evaluation and Reason codes:   SUPPLEMENTAL_TEXT   OUTSIDE_SERIES   (2) Support for accepting &lt;isValid/&gt; in input message, if optional feature enabled. (3) Update evaluation reason and recommendation reason display text to match what is returned by ICE version 1.22.1.                                                                                                                                                                                                                                                                                   |
| Release 3.5 (ICE release 1.21.1)                  | 10/16/2019                   | Daryl Chertcoff                                                            | Addition of Recommendation Reason code – ABOVE_REC_AGE                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| Release 3.2 (ICE releases 1.16.1, 1.17.1, 1.20.1) | 3/7/2019 5/31/2019 8/28/2019 | Amy Moniz / Daryl Chertcoff                                                | ICE release 1.16.1: Added new recommendation reason code – TOO_OLD_TO_INITIATE. See p. 67 Bump to include ICE release 1.17.1 Bump to include ICE release 1.20.1                                                                                                                                                                                                                                                                                                                                                                                                                      |
| Release 3.1 (ICE release 1.15.1)                  | 2/1/2019                     | Amy Moniz / Daryl Chertcoff                                                | (1) New evaluation reason code – SELECT_ADJUVANT_PRODUCT_INTERVAL. See p. 65                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Release 2.20 (ICE release 1.12.1)                 | 4/12/2018                    | Daryl Chertcoff                                                            | (1) Addition of 3 vaccines/CVX codes for Zoster and Hep B. See pp. 52-61 (2) New evaluation reason code. See p. 64                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| Release 2.19 (ICE release 1.11.1)                 | 3/9/2018                     | Amy Moniz / Daryl Chertcoff / Maiko Minami                                 | Information on how to read the Earliest Date and Past Due Date (a.k.a. “overdue” date)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| Release 2.18 (ICE release 1.9.2 and 1.10.1)       | 11/20/2017                   | Daryl Chertcoff                                                            | (1) CVX 148 incorrectly listed twice under Hib vaccine group. (2) Fixed incorrect definition as previously defined in this document for the response payload’s “isValid” element. The prior definition and examples incorrectly stated that an ACCEPTED shot’s “isValid” element will be marked true. An ACCEPTED shot’s isValid element is false. The corrected definition states that isValid is only true if the shot is VALID.                                                                                                                                                   |
| Release 2.17 (ICE release 1.9.1)                  | 10/6/2017                    | Daryl Chertcoff                                                            | Finalized guide for 1.9.1 release. Changes since prior (2.16) guide: removed CVX 164                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Release 2.16 (ICE release 1.9)                    | 9/1/2017                     | Amy Moniz / Daryl Chertcoff                                                | Changes for new Meningococcal B vaccine group, ahead of ICE v. 1.9 release. This guide is a draft. It is possible (though unlikely) that additional changes may be made to this guide when ICE v. 1.9 is released                                                                                                                                                                                                                                                                                                                                                                    |
| Release 2.15 (ICE release 1.8.2)                  | 8/2/2017                     | Daryl Chertcoff                                                            | Add Influenza vaccine (CVX 186)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| Release 2.14 (ICE release 1.8.1)                  | 7/31/2017                    | Amy Moniz / Daryl Chertcoff / Maiko Minami                                 | (1) Edits to combine Pneumococcal “PCV” and “PPSV” vaccine groups into one “Pneumococcal” vaccine group: “PCV” and “PPSV” vaccine groups removed from “Evaluation Focus” and “Recommendation Focus” code systems; “Pneumococcal” added (2) Added additional vaccines (CVX codes) relevant to Influenza, DTP, and Polio to CVX code system (3) Added new evaluation reason codes to “Evaluation Reasons” code system (4) Renamed “Meningococcal” vaccine group “Meningococcal ACWY”                                                                                                   |
| Release 2.13                                      | 12/1/2016                    | Daryl Chertcoff                                                            | Clarified all supported disease immunity code systems (ICD-9-CM, ICD-10, and SNOMED-CT)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| Release 2.12                                      | 9/23/2016                    | Daryl Chertcoff, Maiko Minami                                              | Added Zoster vaccine group, vaccine; “Other” vaccine group; clarifications                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Release 2.11                                      | 8/16/2016                    | Daryl Chertcoff                                                            | Added influenza vaccine codes: CVX 168, CVX 171                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| Release 2.10                                      | 8/12/2016                    | Maiko Minami / Daryl Chertcoff                                             | Update to disease immunity code mappings (ICD-10 and SNOMED-CT added)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| Release 2.8                                       | 12/3/2015                    | Daryl Chertcoff                                                            | Prior documentation was missing CVX 148 and 166 in corresponding Vaccine Group section (Added 2.8). (For convenience, these changes are highlighted via Track Changes in two Tables in Section 5 of this document.)                                                                                                                                                                                                                                                                                                                                                                  |
| Release 2.7                                       | 9/22/2015                    | Daryl Chertcoff                                                            | Added CVX 166 code for Influenza                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| Release 2.6                                       | 7/3/2015                     | Daryl Chertcoff                                                            | Added codes for DTP; Added CVX code for HPV9                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Release 2.5                                       | 9/12/2014                    | Daryl Chertcoff                                                            | Added CVX code for Influenza 2014 – 2015 Influenza season                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| Release 2.4                                       | 08/29/2014                   | Michael Suralik                                                            | Changed document title and file name; Minor change to description of ICE in the ICE Overview.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

