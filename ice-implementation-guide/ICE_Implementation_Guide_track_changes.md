# ICE Implementation Guide

**Immunization Calculation Engine (ICE)**  
Implementation Guide for Integrating with ICE

**ICE versions 2.57, 2.56, 2.55, 2.53**  
**Documentation Release 4.27**  
**May 22nd, 2026**

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

The  **Immunization Calculation Engine**  (**ICE**) is a state-of-the-art open-source software system that provides clinical
decision support for immunizations (CDSi), commonly referred to as "immunization forecasting".

Organizations may freely adopt ICE due to its open source license and complete lack of dependence on any commercial software. The
ICE software system has been publicly released as an open-source software system, under the GNU Lesser General Public License v3
(LGPL v3). Through its standards-based Web Service interface, ICE easily integrates with third party clinical information systems
such as electronic health record systems (EHR-S), patient portals, immunization information systems (IIS), school health systems,
and health information exchanges (HIEs) - regardless of their software architecture (.NET, Java, or other). Because of ICE’s
Java-based implementation, it can be deployed in diverse technical environments.

The ICE software system has been developed and configured by a collaborative partnership of public health and information technology
experts from the New York City Department of Health and Mental Hygiene, Citywide Immunization Registry (CIR); HLN Consulting, LLC;
the Alabama Department of Public Health (ADPH); and the OpenCDS collaboration led by researchers at the University of Utah,
Department of Biomedical Informatics.

The ICE Web Service has been implemented as a clinical module within  **OpenCDS**, an open-source software framework that provides
developers with a set of tools for implementing clinical decision support services. More information about OpenCDS can be found
at: [http://www.opencds.org](http://www.opencds.org).

ICE comes pre-configured with the childhood, adolescent, and adult immunization schedules for routinely administered vaccine groups.
The pre-configured ICE rules are thoroughly documented on the publicly accessible ICE
Wiki at: https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352468/Default+Immunization+Schedule. These rules are based on
the recommendations of the Advisory Committee on Immunization Practices (ACIP) as interpreted by a team of subject matter experts
from the CIR, ADPH, and HLN.

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

In the DSS request, base64 encode the contents of the VMR message
within <kmEvaluationRequest><dataRequirementItemData><data><base64EncodedPayload>.

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
>

```xml
>
<id root="{UNIQUE_ROOT_ID}"
    extension="{UNIQUE_ROOT_EXTENSION}"/> <!-- root & extension attributes appended together must be unique across all root & root/extension values for the entire message. The unique identifier cannot be repeated anywhere in the message. Suggestion: use the Globally Unique Identifer (GUID) algorithm to generate the root attribute value only and do not bother specifying the extension. Example GUID value: 0368a1b4-0f93-402e-841d-e0b02943300d -->
```

>
> `<!-- **Patient Birthdate and Gender Section** **Begins** **(mandatory)** -->`
>
> `<demographics>`
>

```xml
> <birthTime value="{YYYYMMDD}"/> <!-- e.g. February 29, 2012 would be specified by 20120229 -->
```

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

```xml
>
<id root="{UNIQUE_IDENTIFIER2}"/> <!-- Suggestion: Use Globally Unique Identifier algorithm (GUID) -->
```

>

```xml
>
<observationFocus code="{DISEASE_IMMUNITY_FOCUS_CODE}" codeSystem="2.16.840.1.113883.6.103" displayName=".."
                  originalText=".."/> <!—codeSystem may be OID for ICD-9-CM, SNOMED-CT, or ICD-10. See Disease code tables -->
```

>
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

<table>
<thead>
<tr class="header">
<th><strong>Attribute</strong></th>
<th><strong>Datatype</strong></th>
<th><strong>Required?</strong></th>
<th><strong>Usage</strong></th>
</tr>
<tr class="odd">
<th colspan="4"><p>&lt;cdsInput&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to "2.16.840.1.113883.3.795.11.1.1"</th>
</tr>
<tr class="odd">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;cdsContext&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="header">
<th>&lt;cdsSystemUserPreferredLanguage code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Set to “en”</th>
</tr>
<tr class="odd">
<th>&lt;cdsSystemUserPreferredLanguage codeSystem</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.6.99”</th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="odd">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.1.1”</th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="odd">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.2.1.1”</th>
</tr>
<tr class="header">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="odd">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;demographics&gt;</p>
<p>This section is <em>always</em> provided with the birthdate and gender of the patient</p></th>
</tr>
<tr class="odd">
<th>&lt;birthTime value&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Birthdate of the patient. Set to a timestamp value with the format YYYYMMDD.</th>
</tr>
<tr class="header">
<th>&lt;gender code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Gender of the patient. Set as “M” for Male or “F” for Female.</th>
</tr>
<tr class="odd">
<th>&lt;gender codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Codesystem used by ICE to interpret gender.code. Set to "2.16.840.1.113883.5.1".</th>
</tr>
<tr class="header">
<th>&lt;gender displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Display name for Gender. Not used by ICE</th>
</tr>
<tr class="odd">
<th>&lt;gender originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Original Text name for Gender. Not used by ICE</th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;</p>
<p>This section is <em>always</em> provided.</p></th>
</tr>
<tr class="odd">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;</p>
<p>This section is <em>optionally</em> provided to specify all instances of disease immunity for the patient. Each instance of disease immunity is specified by an &lt;observationResult&gt; section</p></th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt;</p>
<p>Repeated for each instance of disease immunity, if any.</p></th>
</tr>
<tr class="odd">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.6.3.1”</th>
</tr>
<tr class="header">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="odd">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="header">
<th>&lt;observationFocus code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Code value specifying the focus of the observation. Refer to the code table for the below codeSystem for valid values. See Disease code tables for supported values.</th>
</tr>
<tr class="odd">
<th>&lt;observationFocus codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Code System used by ICE to interpret the above observationFocus code. Code System may be OID for ICD-9-CM, SNOMED-CT, or ICD-10.</th>
</tr>
<tr class="header">
<th>&lt;observationEventTime low&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Time that the disease immunity was recorded. Set to a timestamp value with the format YYYYMMDD</th>
</tr>
<tr class="odd">
<th>&lt;observationEventTime high&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Time that the disease immunity was recorded. Set to the same timestamp value as observationEventTime.low (format YYYYMMDD)</th>
</tr>
<tr class="header">
<th>&lt;interpretation code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Interpretation element is repeatable. Code value specifying how ICE should interpret the nested &lt;observationValue&gt;. Refer to the code table for the below codeSystem for valid values</th>
</tr>
<tr class="odd">
<th>&lt;interpretation codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Interpretation element is repeatable. Code System used by ICE to interpret the above interpretation code. Set to “2.16.840.1.113883.3.795.12.100.9”</th>
</tr>
<tr class="header">
<th>&lt;interpretation displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Interpretation element is repeatable. Display name corresponding with the above interpretation code. Not used by ICE.</th>
</tr>
<tr class="odd">
<th>&lt;interpretation originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Interpretation element is repeatable. Original text name corresponding with the above interpretation code. Not used by ICE.</th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt;&lt;observationValue&gt;</p>
<p>Required section if ancestor &lt;observationResult&gt; section is present.</p></th>
</tr>
<tr class="odd">
<th>&lt;concept code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Code value specifying the value for the above &lt;observationFocus/&gt;. Since &lt;observationResults/&gt; are only specified in the input message for disease immunity, this code will always have something to do with disease immunity. Refer to the code table for the below codeSystem for valid values.</th>
</tr>
<tr class="header">
<th>&lt;concept codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Code System used by ICE to interpret the above concept code. Set to “2.16.840.1.113883.3.795.12.100.8”</th>
</tr>
<tr class="odd">
<th>&lt;concept displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Display name corresponding with the above observation value code. Not used by ICE.</th>
</tr>
<tr class="header">
<th>&lt;concept originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Original text corresponding to the above observation value code. Not used by ICE.</th>
</tr>
<tr class="odd">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;</p>
<p>This section is <em>optional</em>. Specified when there are shots that have been administered. They are reported to ICE as a part of the patient’s immunization history. Each instance of an administered shot is specified by a &lt;substanceAdministrationEvent&gt; section</p></th>
</tr>
<tr class="header">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;</p>
<p>Repeated for each instance of an administered shot.</p></th>
</tr>
<tr class="odd">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.9.1.1”</th>
</tr>
<tr class="header">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="odd">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="header">
<th>&lt;substanceAdministrationGeneralPurpose code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Set to “384810002”</th>
</tr>
<tr class="odd">
<th>&lt;substanceAdministrationGeneralPurpose codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.6.5”</th>
</tr>
<tr class="header">
<th>&lt;administrationTimeInterval low&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Date that a shot was administered. Set to a timestamp value with the format YYYYMMDD</th>
</tr>
<tr class="odd">
<th>&lt;administrationTimeInterval high&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Date that a shot was administered. Set to the same timestamp value as administrationTimeInterval.low (format YYYYMMDD)</th>
</tr>
<tr class="header">
<th>&lt;isValid value&gt;</th>
<th>Boolean</th>
<th>N</th>
<th>If “enable_dose_override_feature” property is set in the ice.properties file, enables caller to override ICE’s evaluation for a shot. See <a href="https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/691470371/Dose+Override+Feature">https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/691470371/Dose+Override+Feature</a> for details.</th>
</tr>
<tr class="odd">
<th colspan="4"><p>&lt;cdsInput&gt;&lt;vmrInput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;substance&gt;</p>
<p>Required if ancestor &lt;substanceAdministrationEvent/&gt; section is present</p></th>
</tr>
<tr class="header">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="odd">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode code&gt;</th>
<th>String</th>
<th>Y</th>
<th>CVX code of the vaccine administered</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.12.292”, the OID for CVX codes</th>
</tr>
<tr class="header">
<th>&lt;substanceCode displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Display name corresponding with the above CVX code. Not used by ICE.</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Original text corresponding with the above CVX code. Not used by ICE.</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

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

```xml
<substanceAdministrationProposal>
```

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

<table>
<thead>
<tr class="header">
<th><strong>Attribute</strong></th>
<th><strong>Datatype</strong></th>
<th><strong>Always Present?</strong></th>
<th><strong>Value Same as in Input Message?</strong></th>
<th><strong>Usage</strong></th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Set to "2.16.840.1.113883.3.795.11.1.1"</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.1.1”</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;</p>
<p>This section is <em>always</em> provided</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.2.1.1”</th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. May be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;demographics&gt;</p>
<p>This section is <em>always</em> provided with the birthdate and gender of the patient</p></th>
</tr>
<tr class="header">
<th>&lt;birthTime value&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Birthdate of the patient. Set to a timestamp value with the format YYYYMMDD.</th>
</tr>
<tr class="odd">
<th>&lt;gender code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Y</th>
<th>Gender of the patient. Set as “M” for Male or “F” for Female.</th>
</tr>
<tr class="header">
<th>&lt;gender codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Codesystem used by ICE to interpret gender.code. Set to "2.16.840.1.113883.5.1".</th>
</tr>
<tr class="odd">
<th>&lt;gender displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Display name for Gender. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th>&lt;gender originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Original Text name for Gender. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;</p>
<p>This section is <em>always</em> provided.</p></th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;</p>
<p>This section is <em>optionally</em> provided to specify all instances of disease immunity for the patient. Each instance of disease immunity is specified by an &lt;observationResult&gt; section</p></th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt;</p>
<p>Repeated for each instance of disease immunity, if any.</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.6.3.1”</th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;observationFocus code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Y</th>
<th>Code value specifying the focus of this observation related to disease immunity. Refer to the code table for the below codeSystem for valid values.</th>
</tr>
<tr class="header">
<th>&lt;observationFocus codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Code System used by ICE to interpret the above observationFocus code. This should have been set to “2.16.840.1.113883.6.103” by the client application for disease immunity focus.</th>
</tr>
<tr class="odd">
<th>&lt;observationEventTime low&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Date that the disease immunity was recorded. Set to a timestamp value with the format YYYYMMDD</th>
</tr>
<tr class="header">
<th>&lt;observationEventTime high&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Date that the disease immunity was recorded. Set to the same timestamp value as observationEventTime.low (format YYYYMMDD)</th>
</tr>
<tr class="odd">
<th>&lt;interpretation code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Y</th>
<th>Interpretation element is repeatable. Code value specifying how ICE interpreted the nested &lt;observationValue&gt; with respect to disease immunity. Refer to the code table for the below codeSystem for valid values</th>
</tr>
<tr class="header">
<th>&lt;interpretation codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Interpretation element is repeatable. Code System used by ICE to interpret the above interpretation code. This should have been set to “2.16.840.1.113883.3.795.12.100.9” by the client application for disease immunity interpretation.</th>
</tr>
<tr class="odd">
<th>&lt;interpretation displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Interpretation element is repeatable. Display name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th>&lt;interpretation originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Interpretation element is repeatable. Original text name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;observationResults&gt;&lt;observationResult&gt;&lt;observationValue&gt;</p>
<p>Required section if ancestor &lt;observationResult&gt; section is present.</p></th>
</tr>
<tr class="header">
<th>&lt;concept code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Y</th>
<th>Code value specifying the disease immunity value for the above &lt;observationFocus/&gt;. Refer to the code table for the below codeSystem for valid values.</th>
</tr>
<tr class="odd">
<th>&lt;concept codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Code System used by ICE to interpret the above concept code. Set to “2.16.840.1.113883.3.795.12.100.8”</th>
</tr>
<tr class="header">
<th>&lt;concept displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Display name corresponding with the above observation value code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="odd">
<th>&lt;concept originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Original text corresponding to the above observation value code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;</p>
<p>This section is <em>optional</em>. Specified when there are shots that have been administered. They are reported to ICE as a part of the patient’s immunization history. Each instance of an administered shot is specified by a &lt;substanceAdministrationEvent&gt; section</p></th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;</p>
<p>Repeated for each instance of an administered shot.</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.3.795.11.9.1.1”</th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;substanceAdministrationGeneralPurpose code&gt;</th>
<th>String</th>
<th>Y</th>
<th>Y</th>
<th>Set to “384810002”</th>
</tr>
<tr class="header">
<th>&lt;substanceAdministrationGeneralPurpose codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>Set to “2.16.840.1.113883.6.5”</th>
</tr>
<tr class="odd">
<th>&lt;administrationTimeInterval low&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Date that the shot was administered. Set to a timestamp value with the format YYYYMMDD</th>
</tr>
<tr class="header">
<th>&lt;administrationTimeInterval high&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Date that the shot was administered. Set to the same timestamp value as administrationTimeInterval.low (format YYYYMMDD)</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;substance&gt;</p>
<p>Required if ancestor &lt;substanceAdministrationEvent/&gt; section is present</p></th>
</tr>
<tr class="header">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>Y</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="odd">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>Y</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N</th>
<th>CVX code of the vaccine administered. In future versions of ICE, this value will be the same as that in the input message.</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N</th>
<th>ICE sets this to “2.16.840.1.113883.12.292”, the OID for CVX codes.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N</th>
<th>Display name corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N</th>
<th>Original text corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;</p>
<p>This element was not provided on input but is present on output for its ancestor &lt;substanceAdministrationEvent/&gt;. It is included to list evaluation information for a component vaccine. Repeatable for each component vaccine of the administered shot.</p></th>
</tr>
<tr class="odd">
<th>&lt;targetRelationshipToSource code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Set to “PERT”</th>
</tr>
<tr class="header">
<th>&lt;targetRelationshipToSource codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Set to “2.16.840.1.113883.5.1002”</th>
</tr>
<tr class="odd">
<th colspan="5">&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;</th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Set to “2.16.840.1.113883.3.795.11.9.1.1”</th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;substanceAdministrationGeneralPurpose code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>ICE sets this to “384810002”</th>
</tr>
<tr class="header">
<th>&lt;substanceAdministrationGeneralPurpose codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>ICE sets this to “2.16.840.1.113883.6.5”</th>
</tr>
<tr class="odd">
<th>&lt;administrationTimeInterval low&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Date that a shot was administered. ICE sets the date value in the format YYYYMMDD</th>
</tr>
<tr class="header">
<th>&lt;administrationTimeInterval high&gt;</th>
<th>TS</th>
<th>Y</th>
<th>Y</th>
<th>Date that the shot was administered. Set to the same date value as administrationTimeInterval.low (format YYYYMMDD)</th>
</tr>
<tr class="odd">
<th>&lt;isValid value&gt;</th>
<th>boolean</th>
<th>N</th>
<th>N/A</th>
<th>Set to true if component vaccine evaluated as VALID; false if anything other than VALID</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;substance&gt;</p>
<p>This element was not provided on input but is present on output if ancestor &lt;substanceAdministrationEvent/&gt; is present. This section indicates the component vaccine in question and it is not repeatable.</p></th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N</th>
<th>CVX code of the component vaccine.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N</th>
<th>ICE sets this to “2.16.840.1.113883.12.292”, the OID for CVX codes</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N</th>
<th>Display name corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N</th>
<th>Original text corresponding with the above CVX code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;</p>
<p>This element was not provided on input but is present on output for its ancestor &lt;substanceAdministrationEvent/&gt;. It is a continuation of evaluation information for the component vaccine and not repeated.</p></th>
</tr>
<tr class="header">
<th>&lt;targetRelationshipToSource code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Set to “PERT”</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt;</p>
<p>Continuation of evaluation information for the component vaccine and not repeatable.</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Set to “2.16.840.1.113883.3.795.11.6.3.1”</th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;observationFocus code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Code value specifying the focus of this observation related to evaluation of this component vaccine. Refer to the code table for the below codeSystem for valid values.</th>
</tr>
<tr class="header">
<th>&lt;observationFocus codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Code System used by ICE to interpret the above observationFocus code. Set to “2.16.840.1.113883.3.795.12.100.1” which indicates the vaccine group component that is being evaluated. (<em>e.g.</em> - “Immunization Validity (Hep B Component)”)</th>
</tr>
<tr class="odd">
<th>&lt;interpretation code&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Interpretation element is repeatable. The code value specifies how ICE should interpret the nested &lt;observationValue&gt;. Refer to the code table for the below codeSystem for valid values</th>
</tr>
<tr class="header">
<th>&lt;interpretation codeSystem&gt;</th>
<th>UUID</th>
<th>N</th>
<th>N/A</th>
<th>Interpretation element is repeatable. Set to “2.16.840.1.113883.3.795.12.100.3”</th>
</tr>
<tr class="odd">
<th>&lt;interpretation displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Interpretation element is repeatable. Display name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th>&lt;interpretation originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Interpretation element is repeatable. Original text name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationEvents&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;substanceAdministrationEvent&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt;&lt;observationValue&gt;</p>
<p>Continuation of evaluation information for the component vaccine and not repeatable.</p></th>
</tr>
<tr class="header">
<th>&lt;concept code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Code value specifying the evaluation validity with respect to the above &lt;observationFocus/&gt;. That is, is the component vaccine VALID, INVALID, etc.? Refer to the code table for the below codeSystem for valid values.</th>
</tr>
<tr class="odd">
<th>&lt;concept codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Code System used by ICE to interpret the above concept code. ICE sets this to “2.16.840.1.113883.3.795.12.100.2”</th>
</tr>
<tr class="header">
<th>&lt;concept displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Display name corresponding with the above observation value code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="odd">
<th>&lt;concept originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Original text corresponding to the above observation value code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;</p>
<p>This section is <em>optional</em>. Specified when there are vaccine forecasts. Vaccine forecasts are broken up by vaccine group, and each vaccine group recommendation is specified by a &lt;substanceAdministrationProposal&gt; section</p></th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;</p>
<p>Repeated for each vaccine group recommendation.</p></th>
</tr>
<tr class="header">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>ICE sets this to “2.16.840.1.113883.3.795.11.9.3.1”</th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;substanceAdministrationGeneralPurpose code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>ICE sets this to “384810002”</th>
</tr>
<tr class="header">
<th>&lt;substanceAdministrationGeneralPurpose codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Set to “2.16.840.1.113883.6.5”</th>
</tr>
<tr class="odd">
<th>&lt;proposedAdministrationTimeInterval low&gt;</th>
<th>TS</th>
<th>N</th>
<th>N/A</th>
<th>If a vaccine is due to be administered on a particular date, this element attribute is included in the output and represents the <em>recommended date</em>. ICE sets the date in the YYYYMMDD format. Implementations should always obtain the recommended date from this attribute, and not the “high” attribute (specified below).</th>
</tr>
<tr class="header">
<th>&lt;proposedAdministrationTimeInterval high&gt;</th>
<th>TS</th>
<th>N</th>
<th>N/A</th>
<th><p>If a vaccine is due to be administered on a particular date <em>and</em> the “output_earliest_and_overdue_dates” property is set to “Y” in the ice.properties file, this element attribute is included in the output and represents the <em>past due date (a.k.a. - overdue date)</em>. The past due date is the date after which an immunization administered would be considered late. ICE sets the date in the YYYYMMDD format. If there is no past due date, this attribute is not included.</p>
<p><em>Note:</em> If the “output_earliest_and_overdue_dates” property is <em>not</em> set to “Y” in the ice.properties file, this attribute is set to the recommended date (<em>i.e.</em> – the same date as the “low” attribute specified above). This is done for backwards compatibility to previous implementations of ICE. However, moving forward, applications should be sure to obtain the earliest recommended date from the “low” attribute only.</p></th>
</tr>
<tr class="odd">
<th>&lt;validAdministrationTimeInterval low&gt;</th>
<th>TS</th>
<th>N</th>
<th>N/A</th>
<th>If a vaccine is due to be administered on a particular date <em>and</em> the “output_earliest_and_overdue_dates” property is set to “Y” in the ice.properties file, this element is included in the output and represents the <em>earliest date</em>. The earliest due date is the earliest date that the vaccine can be given and still be considered valid. ICE sets the date in the YYYYMMDD format.</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;substance&gt;</p>
<p>Continuation of the vaccine forecast within its ancestor &lt;substanceAdministrationProposal&gt;. This element is not repeatable.</p></th>
</tr>
<tr class="odd">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="header">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Vaccine or vaccine group code being recommended. If a specific vaccine is recommended, ICE will populate this with a CVX code. More commonly, this attribute will be populated with the vaccine group.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>The codeSystem representing the vaccine group for which this recommendation is being made. Currently, ICE sets this to “2.16.840.1.113883.3.795.12.100.1” if a vaccine group; “2.16.840.1.113883.12.292” if a vaccine. Note that the calling application must also look at the nested &lt;relatedClinicalStatement&gt; to get all of the parameters for the forecast.</th>
</tr>
<tr class="odd">
<th>&lt;substanceCode displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Display name corresponding with the above CVX or vaccine group code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th>&lt;substanceCode originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Original text corresponding with the above CVX or vaccine group code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="odd">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;relatedClinicalStatement&gt;</p>
<p>This element is a continuation of the vaccine forecast within its ancestor &lt;substanceAdministrationProposal&gt; and contains the forecast</p></th>
</tr>
<tr class="header">
<th>&lt;targetRelationshipToSource code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>ICE always sets this to “RSON”</th>
</tr>
<tr class="odd">
<th>&lt;targetRelationshipToSource codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>ICE always sets this to “2.16.840.1.113883.5.1002”</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt;</p>
<p>This element is a continuation of the vaccine forecast within its ancestor &lt;relatedClinicalStatement&gt;. It indicates the vaccine group in question and also indicates the reasons for the recommendation in the nested &lt;observationValue&gt;. It is not repeatable.</p></th>
</tr>
<tr class="odd">
<th>&lt;templateId root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>ICE always sets this to “2.16.840.1.113883.3.795.11.6.3.1”</th>
</tr>
<tr class="header">
<th>&lt;id root&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>UUID used to form a unique value across all id elements for the message. Can be combined with id.extension attribute (below) to form uniqueness if desired.</th>
</tr>
<tr class="odd">
<th>&lt;id extension&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Character string that when appended to the id.root attribute forms a unique value for the message. If id.root is unique on its own, this attribute is not required.</th>
</tr>
<tr class="header">
<th>&lt;observationFocus code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Vaccine group code for which this recommendation is being made.</th>
</tr>
<tr class="odd">
<th>&lt;observationFocus codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>This codeSystem attribute will be set to “2.16.840.1.113883.3.795.12.100.1”, which indicates the vaccine group for which the recommendation is being made. (<em>e.g.</em> - “Immunization Recommendation Focus (Hep B)”).</th>
</tr>
<tr class="header">
<th>&lt;interpretation code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Interpretation element is repeatable. The code value specifies how ICE should interpret the nested &lt;observationValue&gt;. Refer to the code table for the below codeSystem for valid values</th>
</tr>
<tr class="odd">
<th>&lt;interpretation codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Interpretation element is repeatable. ICE always set this to “2.16.840.1.113883.3.795.12.100.6”. Note: Until Release 1.9.1, at most one Interpretation element was returned for each SubstanceAdministrationProposal. The Meningococcal B vaccine group, introduced in 1.9.1, may return multiple Interpretation elements for its SubstanceAdministrationProposal.</th>
</tr>
<tr class="header">
<th>&lt;interpretation displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Interpretation element is repeatable. Display name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="odd">
<th>&lt;interpretation originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Interpretation element is repeatable. Original text name corresponding with the above interpretation code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
<tr class="header">
<th colspan="5"><p>&lt;cdsOutput&gt;&lt;vmrOutput&gt;&lt;patient&gt;&lt;clinicalStatements&gt;&lt;substanceAdministrationProposals&gt;&lt;substanceAdministrationProposal&gt;&lt;relatedClinicalStatement&gt;&lt;observationResult&gt;&lt;observationValue&gt;</p>
<p>This element is a continuation of the vaccine forecast within its ancestor &lt;observationResult&gt;. It specifies whether a shot is recommended, not recommended, conditionally recommended, etc. It is not repeatable.</p></th>
</tr>
<tr class="odd">
<th>&lt;concept code&gt;</th>
<th>String</th>
<th>Y</th>
<th>N/A</th>
<th>Code value specifying the recommendation with respect to the above &lt;observationFocus/&gt;. Refer to the code table for the below codeSystem for valid values.</th>
</tr>
<tr class="header">
<th>&lt;concept codeSystem&gt;</th>
<th>UUID</th>
<th>Y</th>
<th>N/A</th>
<th>Code System used by ICE to interpret the above concept code. ICE sets this to “2.16.840.1.113883.3.795.12.100.5”</th>
</tr>
<tr class="odd">
<th>&lt;concept displayName&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Display name corresponding with the above observation value code. In the future, functionality may be added to ICE to support client-customizable display names for use by the client.</th>
</tr>
<tr class="header">
<th>&lt;concept originalText&gt;</th>
<th>String</th>
<th>N</th>
<th>N/A</th>
<th>Original text corresponding to the above observation value code. In the future, functionality may be added to ICE to support client-customizable original-text names for use by the client.</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

# Code Tables

Below are the code systems and values that are used in ICE’s input and output messages. When constructing or processing messages,
client applications should use the code values in the tables below.

## Vaccines

### CVX - Code System 2.16.840.1.113883.12.292

Below is a list of the CVX codes accepted by this version of ICE. See following tables in this section for mapping of these vaccines
to those accepted by each vaccine group.

<table>
<thead>
<tr class="header">
<th><strong>Code Value</strong></th>
<th><strong>Description</strong></th>
</tr>
<tr class="odd">
<th>01</th>
<th>DTP</th>
</tr>
<tr class="header">
<th>02</th>
<th>OPV</th>
</tr>
<tr class="odd">
<th>03</th>
<th>MMR</th>
</tr>
<tr class="header">
<th>04</th>
<th>Measles/Rubella</th>
</tr>
<tr class="odd">
<th>05</th>
<th>Measles</th>
</tr>
<tr class="header">
<th>06</th>
<th>Rubella</th>
</tr>
<tr class="odd">
<th>07</th>
<th>Mumps</th>
</tr>
<tr class="header">
<th>08</th>
<th>Hep B Peds &lt; 20 years</th>
</tr>
<tr class="odd">
<th>09</th>
<th>Td adult (absorbed)</th>
</tr>
<tr class="header">
<th>10</th>
<th>IPV</th>
</tr>
<tr class="odd">
<th>15</th>
<th>Influenza, Split</th>
</tr>
<tr class="header">
<th>16</th>
<th>Influenza, Whole</th>
</tr>
<tr class="odd">
<th>17</th>
<th>Hib NOS</th>
</tr>
<tr class="header">
<th>20</th>
<th>DTaP</th>
</tr>
<tr class="odd">
<th>21</th>
<th>Varicella</th>
</tr>
<tr class="header">
<th>22</th>
<th>DTP-Hib  (Tetramune; OmniHib-DTP)</th>
</tr>
<tr class="odd">
<th>25</th>
<th>Typhoid, oral</th>
</tr>
<tr class="header">
<th>28</th>
<th>DT (pediatric)</th>
</tr>
<tr class="odd">
<th>31</th>
<th>HepA Pediatric NOS</th>
</tr>
<tr class="header">
<th>32</th>
<th>Meningococcal MPSV4 (Menomune)</th>
</tr>
<tr class="odd">
<th>33</th>
<th>Pneumococcal Polysaccharide 23 valent</th>
</tr>
<tr class="header">
<th>37</th>
<th>Yellow fever live</th>
</tr>
<tr class="odd">
<th>38</th>
<th>Mumps/Rubella</th>
</tr>
<tr class="header">
<th>46</th>
<th>Hib-PRP-D (ProHIBIT) </th>
</tr>
<tr class="odd">
<th>47</th>
<th>Hib-HbOC (HibTITER)</th>
</tr>
<tr class="header">
<th>48</th>
<th>Hib-PRP-T (ActHIB, Hiberix)</th>
</tr>
<tr class="odd">
<th>49</th>
<th>Hib-PRP-OMP (PedvaxHIB)</th>
</tr>
<tr class="header">
<th>42</th>
<th>Hep B High Risk Infant</th>
</tr>
<tr class="odd">
<th>43</th>
<th>Hep B Adult &gt;= 20 years</th>
</tr>
<tr class="header">
<th>44</th>
<th>Hep B Dialysis</th>
</tr>
<tr class="odd">
<th>45</th>
<th>Hep B NOS</th>
</tr>
<tr class="header">
<th>50</th>
<th>DTaP-Hib (TriHiBit)</th>
</tr>
<tr class="odd">
<th>51</th>
<th>Hep B-Hib (PRP-OMP (ComVAX))</th>
</tr>
<tr class="header">
<th>52</th>
<th>Hep A Adult</th>
</tr>
<tr class="odd">
<th>62</th>
<th>HPV Quadrivalent (Gardasil)</th>
</tr>
<tr class="header">
<th>74</th>
<th>Rotavirus</th>
</tr>
<tr class="odd">
<th>75</th>
<th>vaccinia (smallpox) vaccine, diluted</th>
</tr>
<tr class="header">
<th>83</th>
<th>HepA ped/adol 2 dose</th>
</tr>
<tr class="odd">
<th>84</th>
<th>HepA pediatric/adolescent (3 dose)</th>
</tr>
<tr class="header">
<th>85</th>
<th>HepA NOS</th>
</tr>
<tr class="odd">
<th>88</th>
<th>Influenza, unspecified formulation</th>
</tr>
<tr class="header">
<th>89</th>
<th>Polio, unspecified formulation</th>
</tr>
<tr class="odd">
<th>94</th>
<th>MMR-Varicella</th>
</tr>
<tr class="header">
<th>100</th>
<th>Pneumococcal Conjugate 7 valent (PCV 7)</th>
</tr>
<tr class="odd">
<th>101</th>
<th>Typhoid, Vi capsular polysaccharide (ViCPS)</th>
</tr>
<tr class="header">
<th>102</th>
<th>DTP-Hib-HepB</th>
</tr>
<tr class="odd">
<th>104</th>
<th>Twinrix</th>
</tr>
<tr class="header">
<th>105</th>
<th>vaccinia (smallpox) vaccine, diluted (Inactive; not preferred)</th>
</tr>
<tr class="odd">
<th>106</th>
<th>DTaP, 5 pertussis antigens</th>
</tr>
<tr class="header">
<th>107</th>
<th>DTaP, unspecified formulation</th>
</tr>
<tr class="odd">
<th>108</th>
<th>Meningococcal, unspecified formulation</th>
</tr>
<tr class="header">
<th>109</th>
<th>Pneumococcal NOS</th>
</tr>
<tr class="odd">
<th>110</th>
<th>DTaP/HepB/IPV</th>
</tr>
<tr class="header">
<th>111</th>
<th>influenza, live, intranasal</th>
</tr>
<tr class="odd">
<th>113</th>
<th>Td (adult) preservative free</th>
</tr>
<tr class="header">
<th>114</th>
<th>Meningococcal MCV4P (Menactra)</th>
</tr>
<tr class="odd">
<th>115</th>
<th>Tdap</th>
</tr>
<tr class="header">
<th>116</th>
<th>Rotavirus RV5 (RotaTeq, 3-dose)</th>
</tr>
<tr class="odd">
<th>118</th>
<th>HPV Bivalent (Cervarix)</th>
</tr>
<tr class="header">
<th>119</th>
<th>Rotavirus RV1 (Rotarix, 2-dose)</th>
</tr>
<tr class="odd">
<th>120</th>
<th>DTaP-Hib (PRP-T)-IPV</th>
</tr>
<tr class="header">
<th>121</th>
<th>Zoster vaccine, live</th>
</tr>
<tr class="odd">
<th>122</th>
<th>Rotavirus NOS</th>
</tr>
<tr class="header">
<th>125</th>
<th>Novel Influenza-H1N1-09, nasal</th>
</tr>
<tr class="odd">
<th>126</th>
<th>Novel influenza-H1N1-09, preservative-free</th>
</tr>
<tr class="header">
<th>127</th>
<th>Novel influenza-H1N1-09</th>
</tr>
<tr class="odd">
<th>128</th>
<th>Novel Influenza-H1N1-09, all formulations</th>
</tr>
<tr class="header">
<th>130</th>
<th>DTaP/IPV</th>
</tr>
<tr class="odd">
<th>132</th>
<th>DTaP-IPV-Hib-HepB, Historical</th>
</tr>
<tr class="header">
<th>133</th>
<th>Pneumococcal Conjugate 13 (PCV 13)</th>
</tr>
<tr class="odd">
<th>134</th>
<th>Japanese Encephalitis IM (Ixiaro)</th>
</tr>
<tr class="header">
<th>135</th>
<th>Influenza, high dose, seasonal</th>
</tr>
<tr class="odd">
<th>136</th>
<th>Meningococcal MCV4O (Menveo)</th>
</tr>
<tr class="header">
<th>137</th>
<th>HPV NOS</th>
</tr>
<tr class="odd">
<th>138</th>
<th>Td (adult, not adsorbed)</th>
</tr>
<tr class="header">
<th>139</th>
<th>Td, adult NOS</th>
</tr>
<tr class="odd">
<th>140</th>
<th>Influenza, seasonal, injectable, preservative free</th>
</tr>
<tr class="header">
<th>141</th>
<th>Influenza, seasonal, injectable</th>
</tr>
<tr class="odd">
<th>144</th>
<th>Influenza, seasonal, intradermal, preservative free</th>
</tr>
<tr class="header">
<th>146</th>
<th>DTaP-IPV-Hib-HepB</th>
</tr>
<tr class="odd">
<th>147</th>
<th>Meningococcal MCV4, unspecified formulation</th>
</tr>
<tr class="header">
<th>148</th>
<th>Mening C&amp;Y-Hib PRP-T (Menhibrix) (Only Hib component evaluated)</th>
</tr>
<tr class="odd">
<th>149</th>
<th>Influenza, live, intranasal, quadrivalent</th>
</tr>
<tr class="header">
<th>150</th>
<th>Influenza, injectable, quadrivalent, preservative free</th>
</tr>
<tr class="odd">
<th>151</th>
<th>Influenza nasal, unspecified formulation</th>
</tr>
<tr class="header">
<th>153</th>
<th>Influenza, injectable, MDCK, preservative free</th>
</tr>
<tr class="odd">
<th>155</th>
<th>Influenza, injectable, MDCK, preservative free</th>
</tr>
<tr class="header">
<th>158</th>
<th>Influenza-IIV4, IM (&gt;3yrs)</th>
</tr>
<tr class="odd">
<th>161</th>
<th>Influenza, injectable, quadrivalent, preservative free, pediatric</th>
</tr>
<tr class="header">
<th>162</th>
<th>Meningococcal B FHbp, recombinant (Trumenba)</th>
</tr>
<tr class="odd">
<th>163</th>
<th>Meningococcal B 4C, OMV (Bexsero)</th>
</tr>
<tr class="header">
<th>164</th>
<th>Meningococcal B, NOS</th>
</tr>
<tr class="odd">
<th>165</th>
<th>HPV9</th>
</tr>
<tr class="header">
<th>166</th>
<th>Influenza, intradermal, quadrivalent, preservative free, injectable</th>
</tr>
<tr class="odd">
<th>168</th>
<th>Seasonal trivalent influenza vaccine, adjuvanted, preservative free</th>
</tr>
<tr class="header">
<th>170</th>
<th>DTaP-IPV-Hib</th>
</tr>
<tr class="odd">
<th>171</th>
<th>Influenza, injectable, Madin Darby Canine Kidney, preservative free, quadrivalent</th>
</tr>
<tr class="header">
<th>174</th>
<th>Cholera, live attenuated (Vaxchora)</th>
</tr>
<tr class="odd">
<th>177</th>
<th>Pneumococcal Conjugate PCV10</th>
</tr>
<tr class="header">
<th>178</th>
<th>OPV bivalent</th>
</tr>
<tr class="odd">
<th>179</th>
<th>OPV ,monovalent, unspecified (NOS)</th>
</tr>
<tr class="header">
<th>182</th>
<th>OPV, Unspecified (NOS)</th>
</tr>
<tr class="odd">
<th>183</th>
<th>Yellow fever vaccine live - alt</th>
</tr>
<tr class="header">
<th>184</th>
<th>Yellow fever, unspecified formulation (NOS)</th>
</tr>
<tr class="odd">
<th>185</th>
<th>Influenza, recombinant, quadrivalent, injectable, preservative free</th>
</tr>
<tr class="header">
<th>186</th>
<th>Influenza, injectable, MDCK, quadrivalent</th>
</tr>
<tr class="odd">
<th>187</th>
<th>Zoster vaccine recombinant</th>
</tr>
<tr class="header">
<th>188</th>
<th>Zoster vaccine, unspecified formulation (NOS)</th>
</tr>
<tr class="odd">
<th>189</th>
<th>Hep B, adjuvanted</th>
</tr>
<tr class="header">
<th>194</th>
<th>Influenza, Southern Hemisphere, unspecified formulation</th>
</tr>
<tr class="odd">
<th>197</th>
<th>Influenza, high dose, quadrivalent</th>
</tr>
<tr class="header">
<th>198</th>
<th>DTP-Hep B-Hib Pentavalent Non-US</th>
</tr>
<tr class="odd">
<th>200</th>
<th>Influenza, Southern Hemisphere, pediatric, preservative free</th>
</tr>
<tr class="header">
<th>201</th>
<th>Influenza, Southern Hemisphere, preservative free</th>
</tr>
<tr class="odd">
<th>202</th>
<th>Influenza, Southern Hemisphere, quadrivalent, with preservative</th>
</tr>
<tr class="header">
<th>203</th>
<th>Meningococcal MenACWY-TT</th>
</tr>
<tr class="odd">
<th>205</th>
<th>Influenza, seasonal vaccine, quadrivalent, adjuvanted</th>
</tr>
<tr class="header">
<th>206</th>
<th>Vaccinia, smallpox mpox vaccine live, PF, SQ or ID injection</th>
</tr>
<tr class="odd">
<th>207</th>
<th>COVID-19, mRNA, LNP-S, PF 100 mcg/0.5 mL (Moderna)</th>
</tr>
<tr class="header">
<th>208</th>
<th>COVID-19, mRNA, LNP-S, PF, 30 mcg/0.3 mL dose (Pfizer)</th>
</tr>
<tr class="odd">
<th>210</th>
<th>COVID-19 vaccine, vector-nr, rS-ChAdOx1, PF, 0.5 mL (AstraZeneca)</th>
</tr>
<tr class="header">
<th>211</th>
<th>COVID-19 vaccine, Subunit, rS-nanoparticle+Matrix-M1 Adjuvant, PF, 0.5 mL</th>
</tr>
<tr class="odd">
<th>212</th>
<th>COVID-19 vaccine, vector-nr, rS-Ad26, PF, 0.5 mL (Janssen)</th>
</tr>
<tr class="header">
<th>213</th>
<th>COVID-19 vaccine, UNSPECIFIED</th>
</tr>
<tr class="odd">
<th>215</th>
<th>Pneumococcal conjugate PCV15, polysaccharide CRM197 conjugate, adjuvant, PF</th>
</tr>
<tr class="header">
<th>216</th>
<th>Pneumococcal conjugate PCV20, polysaccharide CRM197 conjugate, adjuvant, PF</th>
</tr>
<tr class="odd">
<th>217</th>
<th>COVID-19, mRNA, LNP-S, PF, 30 mcg/0.3 mL dose, tris-sucrose (Pfizer)</th>
</tr>
<tr class="header">
<th>218</th>
<th>COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL dose, tris-sucrose (Pfizer)</th>
</tr>
<tr class="odd">
<th>219</th>
<th>Pfizer COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 5 years)</th>
</tr>
<tr class="header">
<th>220</th>
<th>HepB recombinant, 3-antigen, Al(OH)3</th>
</tr>
<tr class="odd">
<th>221</th>
<th>Moderna COVID-19 Vaccine (Preferable age ranges: &gt;= 6 years to &lt; 12 years OR &gt;= 18 years)</th>
</tr>
<tr class="header">
<th>227</th>
<th>Moderna COVID-19 Vaccine (Inactive)</th>
</tr>
<tr class="odd">
<th>228</th>
<th>Moderna COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 6 years)</th>
</tr>
<tr class="header">
<th>229</th>
<th><p>Moderna COVID-19 Vaccine, Bivalent Booster</p>
<p>(Preferable Age Range: &gt; 6 years and &lt; 12 years (0.25mL dose); &gt;= 12 years (0.5mL dose))</p></th>
</tr>
<tr class="odd">
<th>231</th>
<th>Influenza, Southern Hemisphere, high-dose, quadrivalent</th>
</tr>
<tr class="header">
<th>300</th>
<th>Pfizer COVID-19 Vaccine, Bivalent Booster (Preferable Age Range: &gt;= 12 years)</th>
</tr>
<tr class="odd">
<th>301</th>
<th><p>Pfizer COVID-19 Vaccine, Bivalent Booster</p>
<p>(Preferable Age Range: &gt; 5 years and &lt;= 12 years)</p></th>
</tr>
<tr class="header">
<th>303</th>
<th>RSV, recombinant (Adult)</th>
</tr>
<tr class="odd">
<th>304</th>
<th>Respiratory syncytial virus (RSV), unspecified</th>
</tr>
<tr class="header">
<th>305</th>
<th>RSV, bivalent, PF (Adult)</th>
</tr>
<tr class="odd">
<th>306</th>
<th>RSV, mAb, 0.5 mL, age 0 - &lt; 8 mos.</th>
</tr>
<tr class="header">
<th>307</th>
<th>RSV, mAb, 1 mL, age 0 - 19 mos.</th>
</tr>
<tr class="odd">
<th>308</th>
<th>COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 6 mos-4 yrs)</th>
</tr>
<tr class="header">
<th>309</th>
<th>COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 12+ yrs)</th>
</tr>
<tr class="odd">
<th>310</th>
<th>COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 5-11 yrs)</th>
</tr>
<tr class="header">
<th>311</th>
<th>COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 6 mos-11 yrs)</th>
</tr>
<tr class="odd">
<th>312</th>
<th>COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 12+ yrs)</th>
</tr>
<tr class="header">
<th>313</th>
<th>COVID-19, subunit, rS-nanoparticle, adjuvanted, PF, 12+ yrs, Novavax (12+ yrs)</th>
</tr>
<tr class="odd">
<th>314</th>
<th>Respiratory syncytial virus (RSV) vaccine, unspecified</th>
</tr>
<tr class="header">
<th>315</th>
<th>Respiratory syncytial virus (RSV) MAB, unspecified</th>
</tr>
<tr class="odd">
<th>316</th>
<th>Meningococcal MenABCWY (Penbraya)</th>
</tr>
<tr class="header">
<th>320</th>
<th>Influenza, MDCK, trivalent, preservative</th>
</tr>
<tr class="odd">
<th>324</th>
<th>Poliovirus, inactivated, fractional-dose (fIPV)</th>
</tr>
<tr class="header">
<th>325</th>
<th>Vaccinia (smallpox, mpox), unspecified (NOS)</th>
</tr>
<tr class="odd">
<th>326</th>
<th>RSV, mRNA, injectable, PF</th>
</tr>
<tr class="header">
<th>327</th>
<th>Pneumococcal conjugate PCV21, polysaccharide CRM197 conjugate, PF</th>
</tr>
<tr class="odd">
<th>328</th>
<th>Meningococcal MenABCWY (Penmenvy)</th>
</tr>
<tr class="header">
<th>331</th>
<th><mark>Influenza, Southern Hemisphere, trivalent, preservative free</mark></th>
</tr>
<tr class="odd">
<th>332</th>
<th><mark>RSV, mAb, 0.7 mL, age 0 - &lt; 8 mos. (clesrovimab)</mark></th>
</tr>
<tr class="header">
<th>333</th>
<th>Influenza, live, intranasal, self/caregiver admin</th>
</tr>
<tr class="odd">
<th>334</th>
<th>COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL, Moderna (mNEXSPIKE, 12+ yrs)</th>
</tr>
<tr class="header">
<th>337</th>
<th>Influenza, Southern Hemisphere, high-dose, trivalent, PF</th>
</tr>
<tr class="odd">
<th>500</th>
<th>COVID-19 Non-US Vaccine, Product Unknown</th>
</tr>
<tr class="header">
<th>501</th>
<th>COVID-19 IV Non-US Vaccine (QAZCOVID-IN)</th>
</tr>
<tr class="odd">
<th>502</th>
<th>COVID-19 IV Non-US Vaccine (COVAXIN)</th>
</tr>
<tr class="header">
<th>503</th>
<th>COVID-19 LAV Non-US Vaccine (COVIVAC)</th>
</tr>
<tr class="odd">
<th>504</th>
<th>COVID-19 VVnr Non-US Vaccine (Sputnik Light)</th>
</tr>
<tr class="header">
<th>505</th>
<th>COVID-19 VVnr Non-US Vaccine (Sputnik V)</th>
</tr>
<tr class="odd">
<th>506</th>
<th>COVID-19 VVnr Non-US Vaccine (CanSino Biological Inc./Beijing Institute of Biotechnology)</th>
</tr>
<tr class="header">
<th>507</th>
<th>COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom Biopharm + Inst of Micro, Chinese Acad of Sciences)</th>
</tr>
<tr class="odd">
<th>508</th>
<th>COVID-19 PS Non-US Vaccine (Jiangsu Province Centers for Disease Control and Prevention)</th>
</tr>
<tr class="header">
<th>509</th>
<th>COVID-19 PS Non-US Vaccine (EpiVacCorona)</th>
</tr>
<tr class="odd">
<th>510</th>
<th>COVID-19 IV Non-US Vaccine (BIBP, Sinopharm)</th>
</tr>
<tr class="header">
<th>511</th>
<th>COVID-19 IV Non-US Vaccine (CoronaVac, Sinovac)</th>
</tr>
<tr class="odd">
<th>512</th>
<th>COVID-19 VLP Non-US Vaccine (Medicago, Covifenz)</th>
</tr>
<tr class="header">
<th>513</th>
<th>COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom, Zifivax)</th>
</tr>
<tr class="odd">
<th>514</th>
<th>COVID-19 DNA Non-US Vaccine (Zydus Cadila, ZyCoV-D)</th>
</tr>
<tr class="header">
<th>515</th>
<th>COVID-19 PS Non-US Vaccine (Medigen, MVC-COV1901)</th>
</tr>
<tr class="odd">
<th>516</th>
<th>COVID-19 Inactivated Non-US Vaccine (Minhai Biotechnology Co, KCONVAC)</th>
</tr>
<tr class="header">
<th>517</th>
<th>COVID-19 PS Non-US Vaccine (Biological E Limited, Corbevax)</th>
</tr>
<tr class="odd">
<th>518</th>
<th>COVID-19 Inactivated, Non-US Vaccine (VLA2001, Valneva)</th>
</tr>
<tr class="header">
<th>519</th>
<th>COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine (Spikevax Bivalent), Moderna</th>
</tr>
<tr class="odd">
<th>520</th>
<th>COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine Product, Pfizer-BioNTech</th>
</tr>
<tr class="header">
<th>521</th>
<th>COVID-19 SP, protein-based, adjuvanted (VidPrevtyn Beta), Sanofi-GSK</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

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

| **CVX Code** | **Name**                                      |
|--------------|-----------------------------------------------|
| 08           | HepB peds `<20yrs                             |
| 42           | HepB high risk infant                         |
| 45           | HepB NOS                                      |
| 43           | HepB adult =>`20yrs                           |
| 44           | HepB-dialysis                                 |
| 51           | Hib/HepB (Comvax)                             |
| 102          | <mark>DTP-Hib-HepB</mark>                     |
| 110          | DTaP-HepB-IPV (Pediarix)                      |
| 104          | HepA-HepB (Twinrix)                           |
| 132          | <mark>DTaP-IPV-Hib-HepB, historical</mark>    |
| 146          | <mark>DTaP, IPV, Hib, Hep B</mark>            |
| 189          | <mark>Hep B, adjuvanted</mark>                |
| 198          | <mark>DTP-Hep B-Hib Pentavalent Non-US</mark> |
| 220          | HepB recombinant, 3-antigen, Al(OH)3          |

#### MMR

| **CVX Code** | **Name**        |
|--------------|-----------------|
| 03           | MMR             |
| 05           | Measles         |
| 06           | Rubella         |
| 07           | Mumps           |
| 04           | Measles/Rubella |
| 38           | Mumps/Rubella   |
| 94           | MMR-Varicella   |

#### Varicella

| **CVX Code** | **Name**      |
|--------------|---------------|
| 21           | Varicella     |
| 94           | MMR-Varicella |

#### Rotavirus

| **CVX Code** | **Name**                         |
|--------------|----------------------------------|
| 116          | Rotavirus RV5 (RotaTeq, 3 dose)  |
| 119          | Rotavirus RV1 (Rotarix, 2 dose)  |
| 122          | Rotavirus NOS                    |
| 74           | Rotavirus                        |

#### Hib

| **CVX Code** | **Name**                                                                     |
|--------------|------------------------------------------------------------------------------|
| 46           | Hib-PRP-D (ProHIBIT)                                                         |
| 47           | Hib-HbOC (HibTITER)                                                          |
| 48           | Hib-PRP-T (ActHIB, Hiberix)                                                  |
| 49           | Hib-PRP-OMP (PedvaxHIB)                                                      |
| 17           | Hib NOS                                                                      |
| 50           | <mark>DTaP-Hib (TriHiBit)</mark>                                             |
| 51           | Hep B-Hib (PRP-OMP (ComVAX)                                                  |
| 120          | DTaP-Hib (PRP-T)-IPV                                                         |
| 22           | DTP-Hib  (Tetramune; OmniHib-DTP)                                            |
| 102          | <mark>DTP-Hib-HepB</mark>                                                    |
| 132          | <mark>DTaP-IPV-Hib-HepB, historical</mark>                                   |
| 146          | <mark>DTaP-IPV-Hib-HepB</mark>                                               |
| 148          | <mark>Mening C&Y-Hib PRP-T (Menhibrix) (Only Hib component evaluated)</mark> |
| 170          | <mark>DTaP-IPV-Hib</mark>                                                    |
| 198          | <mark>DTP-Hep B-Hib Pentavalent Non-US</mark>                                |

#### HPV

| **CVX Code** | **Name**                    |
|--------------|-----------------------------|
| 62           | HPV Quadrivalent (Gardasil) |
| 118          | HPV Bivalent (Cervarix)     |
| 137          | HPV NOS                     |
| 165          | HPV9                        |

#### Pneumococcal

| **CVX Code** | **Name**                                                                                 |
|--------------|------------------------------------------------------------------------------------------|
| 100          | <mark>Pneumococcal Conjugate 7 valent (PCV 7)</mark>                                     |
| 133          | <mark>Pneumococcal Conjugate 13 (PCV 13)</mark>                                          |
| 109          | <mark>Pneumococcal NOS</mark>                                                            |
| 152          | <mark>Pneumoccocal Conjugate NOS</mark>                                                  |
| 177          | <mark>Pneumococcal Conjugate PCV10</mark>                                                |
| 33           | <mark>Pneumococcal Polysaccharide 23 valent</mark>                                       |
| 215          | <mark>Pneumococcal conjugate PCV15, polysaccharide CRM197 conjugate, adjuvant, PF</mark> |
| 216          | <mark>Pneumococcal conjugate PCV20, polysaccharide CRM197 conjugate, adjuvant, PF</mark> |
| 327          | <mark>Pneumococcal conjugate PCV21, polysaccharide CRM197 conjugate, PF</mark>           |

#### Influenza

| **CVX Code** | **Name**                                                                          |
|--------------|-----------------------------------------------------------------------------------|
| 15           | influenza, split                                                                  |
| 16           | influenza, whole                                                                  |
| 88           | influenza, unspecified formulation                                                |
| 111          | influenza, live, intranasal                                                       |
| 135          | influenza, high dose, seasonal                                                    |
| 140          | influenza, seasonal, injectable, preservative free                                |
| 141          | influenza, seasonal, injectable                                                   |
| 144          | influenza, seasonal, intradermal, preservative free                               |
| 149          | influenza, live, intranasal, quadrivalent                                         |
| 150          | influenza, injectable, quadrivalent, preservative free                            |
| 151          | influenza nasal, unspecified formulation                                          |
| 153          | influenza, injectable, MDCK, preservative free                                    |
| 155          | influenza, recombinant, injectable, preservative free                             |
| 158          | Influenza-IIV4, IM (\>3yrs)                                                       |
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
| 331          | <mark>Influenza, Southern Hemisphere, trivalent, preservative free</mark>         |
| 333          | Influenza, live, intranasal, self/caregiver admin                                 |
| 337          | Influenza, Southern Hemisphere, high-dose, trivalent, PF                          |

#### H1N1

| **CVX Code** | **Name**                                    |
|--------------|---------------------------------------------|
| 125          |  Novel Influenza-H1N1-09, nasal             |
| 126          |  Novel influenza-H1N1-09, preservative-free |
| 127          |  Novel influenza-H1N1-09                    |
| 128          |  Novel Influenza-H1N1-09, all formulations  |

#### Meningococcal ACWY

| **CVX Code** | **Name**                                                        |
|--------------|-----------------------------------------------------------------|
| 114          | meningococcal MCV4P (Menactra)                                  |
| 136          | meningococcal MCV4O (Menveo)                                    |
| 32           | meningococcal MPSV4 (Menomune)                                  |
| 108          | meningococcal, unspecified formulation                          |
| 147          | meningococcal MCV4, unspecified formulation                     |
| 148          | Mening C&Y-Hib PRP-T (Menhibrix) (Only Hib component evaluated) |
| 203          | Meningococcal MenACW-TT (MenQuadfi)                             |
| 316          | Meningococcal MenABCWY (Penbraya)                               |
| 328          | Meningococcal MenABCWY (Penmenvy)                               |

#### Polio

| **CVX Code** | **Name**                                                     |
|--------------|--------------------------------------------------------------|
| 02           | OPV                                                          |
| 10           | IPV                                                          |
| 89           | polio, unspecified formulation                               |
| 110          | DTaP/HepB/IPV                                                |
| 120          | DTaP/IPV/Hib                                                 |
| 130          | DTaP/IPV                                                     |
| 132          | <mark>DTaP-IPV-Hib-HepB, historical</mark>                   |
| 146          | <mark>DTaP-IPV-Hib-HepB</mark>                               |
| 170          | <mark>DTaP-IPV-Hib</mark>                                    |
| 178          | <mark>OPV bivalent</mark>                                    |
| 179          | <mark>OPV ,monovalent, unspecified (NOS)</mark>              |
| 182          | <mark>OPV, unspecified (NOS)</mark>                          |
| 324          | <mark>Poliovirus, inactivated, fractional-dose (fIPV)</mark> |

#### DTP

| **CVX Code** | **Name**                                      |
|--------------|-----------------------------------------------|
| 01           | DTP                                           |
| 09           | Td (adult), absorbed                          |
| 20           | DTaP                                          |
| 22           | <mark>DTP-Hib (Tetramune; OmniHib-DTP)</mark> |
| 28           | DT (pediatric)                                |
| 50           | <mark>DTaP-Hib (TriHiBit)</mark>              |
| 102          | <mark>DTP-Hib-Hep B</mark>                    |
| 106          | DTaP, 5 pertussis antigens                    |
| 107          | DTaP, unspecified formulation                 |
| 110          | <mark>DTaP-Hep B-IPV (Pediarix)</mark>        |
| 113          | Td (adult) preservative free                  |
| 115          | Tdap                                          |
| 120          | <mark>DTaP-Hib-IPV (Pentacel)</mark>          |
| 130          | <mark>DTaP-IPV</mark>                         |
| 132          | <mark>DTaP-IPV-Hib-HepB, historical</mark>    |
| 138          | Td (adult, not adsorbed)                      |
| 139          | Td (adult) NOS                                |
| 146          | <mark>DTaP, IPV, Hib, Hep B</mark>            |
| 170          | <mark>DTaP-IPV-Hib</mark>                     |
| 198          | <mark>DTP-Hep B-Hib Pentavalent Non-US</mark> |

#### Zoster

| **CVX Code** | **Name**                                      |
|--------------|-----------------------------------------------|
| 121          | Zoster vaccine, live                          |
| 187          | Zoster vaccine, recombinant                   |
| 188          | Zoster vaccine, unspecified formulation (NOS) |

#### Meningococcal B

| **CVX Code** | **Name**                                                  |
|--------------|-----------------------------------------------------------|
| 162          | <mark>Meningococcal B FHbp, recombinant (Trumenba)</mark> |
| 163          | <mark>Meningococcal B 4C, OMV (Bexsero)</mark>            |
| 316          | Meningococcal MenABCWY (Penbraya)                         |
| 328          | Meningococcal MenABCWY (Penmenvy)                         |

#### COVID-19

<table>
<thead>
<tr class="header">
<th><strong>CVX Code</strong></th>
<th><strong>Name</strong></th>
</tr>
<tr class="odd">
<th>207</th>
<th><mark>COVID-19, mRNA, LNP-S, PF 100mcg/0.5 mL (Moderna)</mark></th>
</tr>
<tr class="header">
<th>208</th>
<th><mark>COVID-19, mRNA, LNP-S, PF 30 mcg/0.3 mL (Pfizer)</mark></th>
</tr>
<tr class="odd">
<th>210</th>
<th><mark>COVID-19 vaccine, vector-nr, rS-ChAdOx1, PF, 0.5 mL (AstraZeneca)</mark></th>
</tr>
<tr class="header">
<th>211</th>
<th><mark>COVID-19 vaccine, Subunit, rS-nanoparticle+Matrix-M1 Adjuvant, PF, 0.5 mL (Novavax)</mark></th>
</tr>
<tr class="odd">
<th>212</th>
<th><mark>COVID-19 vaccine, vector-nr, rS-Ad26, PF, 0.5 mL (Janssen)</mark></th>
</tr>
<tr class="header">
<th>213</th>
<th><mark>COVID-19 vaccine, UNSPECIFIED</mark></th>
</tr>
<tr class="odd">
<th>217</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, 30 mcg/0.3 mL dose, tris-sucrose (Pfizer)</mark></th>
</tr>
<tr class="header">
<th>218</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL dose, tris-sucrose (Pfizer)</mark></th>
</tr>
<tr class="odd">
<th>219</th>
<th><mark>Pfizer COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 5 years)</mark></th>
</tr>
<tr class="header">
<th>221</th>
<th><mark>Moderna COVID-19 Vaccine (Preferable age ranges: &gt;= 6 years to &lt; 12 years OR &gt;= 18 years)</mark></th>
</tr>
<tr class="odd">
<th>227</th>
<th><mark>Moderna COVID-19 Vaccine (Inactive)</mark></th>
</tr>
<tr class="header">
<th>228</th>
<th><mark>Moderna COVID-19 Vaccine (Preferable age range: &gt;= 6 months to &lt; 6 years)</mark></th>
</tr>
<tr class="odd">
<th>229</th>
<th><mark>Moderna COVID-19 Vaccine, Bivalent Booster (Preferable Age Range: &gt; 6 years and &lt; 12 years (0.25mL dose); &gt;= 12 years (0.5mL dose))</mark></th>
</tr>
<tr class="header">
<th>300</th>
<th><mark>Pfizer COVID-19 Vaccine, Bivalent Booster<br />
(Preferable Age Range: &gt;= 12 years)</mark></th>
</tr>
<tr class="odd">
<th>301</th>
<th><p><mark>Pfizer COVID-19 Vaccine, Bivalent Booster</mark></p>
<p><mark>(Preferable Age Range: &gt; 5 years and &lt;= 12 years)</mark></p></th>
</tr>
<tr class="header">
<th>308</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 6 mos-4 yrs)</mark></th>
</tr>
<tr class="odd">
<th>309</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 12+ yrs)</mark></th>
</tr>
<tr class="header">
<th>310</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, tris-sucrose, Pfizer (Comirnaty, 5-11 yrs)</mark></th>
</tr>
<tr class="odd">
<th>311</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 6 mos-11 yrs)</mark></th>
</tr>
<tr class="header">
<th>312</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, Moderna (Spikevax, 12+ yrs)</mark></th>
</tr>
<tr class="odd">
<th>313</th>
<th><mark>COVID-19, subunit, rS-nanoparticle, adjuvanted, PF, Novavax (12+ yrs)</mark></th>
</tr>
<tr class="header">
<th>334</th>
<th><mark>COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL, Moderna (mNEXSPIKE, 12+ yrs)</mark></th>
</tr>
<tr class="odd">
<th>500</th>
<th><mark>COVID-19 Non-US Vaccine, Product Unknown</mark></th>
</tr>
<tr class="header">
<th>501</th>
<th><mark>COVID-19 IV Non-US Vaccine (QAZCOVID-IN)</mark></th>
</tr>
<tr class="odd">
<th>502</th>
<th><mark>COVID-19 IV Non-US Vaccine (COVAXIN)</mark></th>
</tr>
<tr class="header">
<th>503</th>
<th><mark>COVID-19 LAV Non-US Vaccine (COVIVAC)</mark></th>
</tr>
<tr class="odd">
<th>504</th>
<th><mark>COVID-19 VVnr Non-US Vaccine (Sputnik Light)</mark></th>
</tr>
<tr class="header">
<th>505</th>
<th><mark>COVID-19 VVnr Non-US Vaccine (Sputnik V)</mark></th>
</tr>
<tr class="odd">
<th>506</th>
<th><mark>COVID-19 VVnr Non-US Vaccine (CanSino Biological Inc./Beijing Institute of Biotechnology)</mark></th>
</tr>
<tr class="header">
<th>507</th>
<th><mark>COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom Biopharm + Inst of Micro, Chinese Acad of Sciences)</mark></th>
</tr>
<tr class="odd">
<th>508</th>
<th><mark>COVID-19 PS Non-US Vaccine (Jiangsu Province Centers for Disease Control and Prevention)</mark></th>
</tr>
<tr class="header">
<th>509</th>
<th><mark>COVID-19 PS Non-US Vaccine (EpiVacCorona)</mark></th>
</tr>
<tr class="odd">
<th>510</th>
<th><mark>COVID-19 IV Non-US Vaccine (BIBP, Sinopharm)</mark></th>
</tr>
<tr class="header">
<th>511</th>
<th><mark>COVID-19 IV Non-US Vaccine (CoronaVac, Sinovac)</mark></th>
</tr>
<tr class="odd">
<th>512</th>
<th><mark>COVID-19 VLP Non-US Vaccine (Medicago, Covifenz)</mark></th>
</tr>
<tr class="header">
<th>513</th>
<th><mark>COVID-19 PS Non-US Vaccine (Anhui Zhifei Longcom, Zifivax)</mark></th>
</tr>
<tr class="odd">
<th>514</th>
<th><mark>COVID-19 DNA Non-US Vaccine (Zydus Cadila, ZyCoV-D)</mark></th>
</tr>
<tr class="header">
<th>515</th>
<th><mark>COVID-19 PS Non-US Vaccine (Medigen, MVC-COV1901)</mark></th>
</tr>
<tr class="odd">
<th>516</th>
<th><mark>COVID-19 Inactivated Non-US Vaccine (Minhai Biotechnology Co, KCONVAC)</mark></th>
</tr>
<tr class="header">
<th>517</th>
<th><mark>COVID-19 PS Non-US Vaccine (Biological E Limited, Corbevax)</mark></th>
</tr>
<tr class="odd">
<th>518</th>
<th><mark>COVID-19 Inactivated, Non-US Vaccine (VLA2001, Valneva)</mark></th>
</tr>
<tr class="header">
<th>519</th>
<th><mark>COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine (Spikevax Bivalent), Moderna</mark></th>
</tr>
<tr class="odd">
<th>520</th>
<th><mark>COVID-19 mRNA, bivalent, original/Omicron BA.1, Non-US Vaccine Product, Pfizer-BioNTech</mark></th>
</tr>
<tr class="header">
<th>521</th>
<th><mark>COVID-19 SP, protein-based, adjuvanted (VidPrevtyn Beta), Sanofi-GSK</mark></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

#### Mpox

| **CVX Code** | **Name**                                                                  |
|--------------|---------------------------------------------------------------------------|
| 75           | <mark>vaccinia (smallpox, mpox), live</mark>                              |
| 105          | <mark>vaccinia (smallpox) vaccine, diluted</mark>                         |
| 206          | <mark>Vaccinia, smallpox mpox vaccine live, PF, SQ or ID injection</mark> |
| 325          | <mark>Vaccinia (smallpox, mpox), unspecified (NOS)</mark>                 |

#### RSV

| **CVX Code** | **Name**                                                            |
|--------------|---------------------------------------------------------------------|
| 303          | <mark>RSV, recombinant (Adult)</mark>                               |
| 304          | <mark>Respiratory syncytial virus (RSV), unspecified</mark>         |
| 305          | <mark>RSV, bivalent, PF (Adult)</mark>                              |
| 306          | <mark>RSV, mAb, 0.5 mL, age 0 - \< 8 mos.</mark>                    |
| 307          | <mark>RSV, mAb, 1 mL, age 0 - 19 mos.</mark>                        |
| 314          | <mark>Respiratory syncytial virus (RSV) vaccine, unspecified</mark> |
| 315          | <mark>Respiratory syncytial virus (RSV) MAB, unspecified</mark>     |
| 326          | <mark>RSV, mRNA, injectable, PF</mark>                              |
| 332          | <mark>RSV, mAb, 0.7 mL, age 0 - \< 8 mos. (clesrovimab)</mark>      |

#### Cholera

| **CVX Code** | **Name**                                         |
|--------------|--------------------------------------------------|
| 174          | <mark>Cholera, live attenuated (Vaxchora)</mark> |

#### Japanese Encephalitis

| **CVX Code** | **Name**                                       |
|--------------|------------------------------------------------|
| 134          | <mark>Japanese Encephalitis IM (Ixiaro)</mark> |

#### Typhoid

| **CVX Code** | **Name**                                                 |
|--------------|----------------------------------------------------------|
| 25           | <mark>Typhoid, oral</mark>                               |
| 101          | <mark>Typhoid, Vi capsular polysaccharide (ViCPS)</mark> |

#### Yellow Fever

| **CVX Code** | **Name**                                                 |
|--------------|----------------------------------------------------------|
| 37           | <mark>Yellow fever live</mark>                           |
| 183          | <mark>Yellow fever vaccine live - alt</mark>             |
| 184          | <mark>Yellow fever, unspecified formulation (NOS)</mark> |
|              |                                                          |

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

<table>
<thead>
<tr class="header">
<th><strong>Code Value (Returned by ICE)</strong></th>
<th><strong>Description (Returned by ICE)</strong></th>
</tr>
<tr class="odd">
<th>ABOVE_MAXIMUM_AGE_VACCINE</th>
<th>This immunization event occurred after the specified maximum age for this vaccine.</th>
</tr>
<tr class="header">
<th>ABOVE_REC_AGE_SERIES</th>
<th>The vaccine is administered above the recommended age for this series.</th>
</tr>
<tr class="odd">
<th>BELOW_MINIMUM_AGE_FINAL_DOSE</th>
<th>This patient was below the minimum age for the final dose.</th>
</tr>
<tr class="header">
<th>BELOW_MINIMUM_AGE_SERIES</th>
<th>This patient was below the minimum age for this dose.</th>
</tr>
<tr class="odd">
<th>BELOW_MINIMUM_AGE_VACCINE</th>
<th>This immunization event occurred prior to the specified minimum age for this vaccine.</th>
</tr>
<tr class="header">
<th>BELOW_MINIMUM_INTERVAL</th>
<th>This immunization event occurred prior to the specified minimum interval for this dose.</th>
</tr>
<tr class="odd">
<th>BELOW_MIN_INTERVAL_PCV_PPSV</th>
<th>This immunization event occurred prior to the specified minimum interval between PCV and PPSV doses.</th>
</tr>
<tr class="header">
<th>BELOW_REC_AGE_SERIES</th>
<th>The vaccine is administered below the recommended age for this series.</th>
</tr>
<tr class="odd">
<th>BOOSTER_DOSE</th>
<th>The vaccine administered is a booster dose.</th>
</tr>
<tr class="header">
<th>BOOSTER_ONLY</th>
<th>The vaccine administered is invalid as a primary shot; valid only as a booster dose.</th>
</tr>
<tr class="odd">
<th>D_AND_T_INVALID/P_VALID</th>
<th>The diphtheria and tetanus components are invalid due to minimum interval violation, pertussis component valid.</th>
</tr>
<tr class="header">
<th>DISEASE_DOCUMENTED</th>
<th>Disease Documented.</th>
</tr>
<tr class="odd">
<th>DUPLICATE_SAME_DAY</th>
<th>This immunization event is a duplicate.</th>
</tr>
<tr class="header">
<th>EXTRA_DOSE</th>
<th>The vaccine administered is an extra dose.</th>
</tr>
<tr class="odd">
<th>INSUFFICIENT_ANTIGEN</th>
<th>This vaccine contained insufficient antigen for the patient's age.</th>
</tr>
<tr class="header">
<th>INVALID_AGE</th>
<th>Invalid Age.</th>
</tr>
<tr class="odd">
<th>MISSING_ANTIGEN</th>
<th>The vaccine administered is missing an antigen.</th>
</tr>
<tr class="header">
<th>OUTSIDE_SEASON</th>
<th>This immunization event occurred was administered outside of the vaccine season.</th>
</tr>
<tr class="odd">
<th>OUTSIDE_FLU_VAC_SEASON</th>
<th>This immunization was administered outside of influenza vaccine season.</th>
</tr>
<tr class="header">
<th>OUTSIDE_SERIES</th>
<th>Shot Administered Outside of Defined Routine Series</th>
</tr>
<tr class="odd">
<th>OUTSIDE_ROUTINE_SERIES</th>
<th>Shot Administered Outside of Defined Routine Series</th>
</tr>
<tr class="header">
<th>PRIOR_TO_DOB</th>
<th>This immunization event was recorded prior to the date of birth.</th>
</tr>
<tr class="odd">
<th>PROOF_OF_IMMUNITY</th>
<th>Proof of Immunity.</th>
</tr>
<tr class="header">
<th>SELECT_ADJUVANT_PRODUCT_INTERVAL</th>
<th>This immunization event occurred prior to the specified minimum interval between adjuvant products.</th>
</tr>
<tr class="odd">
<th>SUPPLEMENTAL_TEXT</th>
<th>Supplemental text is available for this immunization event. (<em>Note: Supplemental text is populated in the “originalText” attribute</em>)</th>
</tr>
<tr class="header">
<th>TOO_EARLY_LIVE_VIRUS</th>
<th>This immunization event occurred prior to the specified minimum interval for a live vaccine dose.</th>
</tr>
<tr class="odd">
<th>VACCINE_NOT_MEMBER_OF_SERIES</th>
<th>The vaccine is not a part of this series, therefore it will not be counted towards completion of this series.</th>
</tr>
<tr class="header">
<th>WAITING_FOR_EVALUATION</th>
<th>Waiting for Evaluation</th>
</tr>
<tr class="odd">
<th>WRONG_GENDER</th>
<th>Wrong Gender</th>
</tr>
<tr class="header">
<th>VACCINE_NOT_SUPPORTED</th>
<th>The vaccine administered is not supported by the ICE service.</th>
</tr>
<tr class="odd">
<th>VACCINE_NOT_LICENSED_FOR_MALES</th>
<th>The vaccine administered is not licensed for males. </th>
</tr>
<tr class="header">
<th>VACCINE_NOT_ALLOWED</th>
<th>The vaccine administered is not allowed.</th>
</tr>
<tr class="odd">
<th>VACCINE_NOT_ALLOWED_FOR_THIS_DOSE</th>
<th>The vaccine administered is not allowed for this dose.</th>
</tr>
<tr class="header">
<th><p>VACCINE_NOT_COUNTED_BASED_ON_</p>
<p>MOST_RECENT_VACCINE_GIVEN</p>
<p>(note: remove extraneous space)</p></th>
<th>The vaccine will not be counted based on the most recent vaccine given. (Most recent vaccine given determines which series is applied.)</th>
</tr>
<tr class="odd">
<th>VACCINE_NOT_PART_OF_THIS_SERIES</th>
<th>The vaccine is not a part of this series, therefore it will not be counted towards completion of this series. </th>
</tr>
<tr class="header">
<th>VACCINE_NOT_ALLOWED_IN_US</th>
<th>The vaccine is not allowed in the U.S., and therefore will be marked Invalid.</th>
</tr>
<tr class="odd">
<th>VACCINE_NOT_APPROVED_IN_US</th>
<th>The vaccine has not been approved in the U.S.</th>
</tr>
<tr class="header">
<th>VACCINE_NOT_APPROVED_IN_US_OR_BY_WHO</th>
<th>The vaccine is not approved for use in the U.S. or by the WHO</th>
</tr>
<tr class="odd">
<th>VACCINE_NOT_YET_AVAILABLE_ON_DATE_SPECIFIED</th>
<th>The vaccine was not yet available on the date specified.</th>
</tr>
</thead>
<tbody>
</tbody>
</table>

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
| ABOVE_AGE_MAY_COMPLETE               | Above recommended age but may complete series.                                             |
| ADMINISTER_COVID19_BIVALENT_VACCINE  | Administer COVID-19 bivalent vaccine                                                       |
| ADMINISTER_PCV15_OR_PCV20            | Administer PCV15 or PCV20 vaccine                                                          |
| ADMINISTER_PCV15_OR_PCV20_OR_PCV21   | Administer PCV15, PCV20 or PCV21 vaccine                                                   |
| ADMINISTER_PCV20_OR_PCV21            | Administer PCV20 or PCV21 vaccine                                                          |
| ADMINISTER_mRNA_VACCINE              | Administer mRNA vaccine *(not used)*                                                       |
| ADMINISTER_TDAP_OR_TD                | Administer Tdap or Td                                                                      |
| BASED_ON_VAC_AVAIL_AND_PRIORITY_RECS | Based on vaccine availability or priority recommendations                                  |
| BELOW_MINIMUM_AGE_HIGH_RISK_SERIES   | Below minimum age for this high risk series.                                               |
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

<table>
<thead>
<tr class="header">
<th><strong>Document Revision</strong></th>
<th><strong>Date</strong></th>
<th><strong>Author</strong></th>
<th><strong>Description</strong></th>
</tr>
<tr class="odd">
<th><p>Release 4.27</p>
<p>ICE 2.57</p></th>
<th>5/22/2026</th>
<th>ICE Team</th>
<th>Added CVX 337 (Influenza, Southern Hemisphere, high-dose, trivalent, PF)</th>
</tr>
<tr class="header">
<th>Release 4.26<br />
<br />
ICE 2.56</th>
<th>4/28/2026</th>
<th>ICE Team</th>
<th>(1) Added CVX 324 (Poliovirus, inactivated, fractional-dose (fIPV))</th>
</tr>
<tr class="odd">
<th>Release 4.25<br />
<br />
ICE 2.55</th>
<th>3/28/2026</th>
<th>ICE Team</th>
<th>Added BOOSTER_DOSE evaluation reason code.</th>
</tr>
<tr class="header">
<th>Release 4.24</th>
<th>2/4/2026</th>
<th>ICE Team</th>
<th><p>(1) Logic fix for COVID-19 2025-2026 season</p>
<p>(2) Added 4 new vaccine groups for (non-routine / travel): Cholera vaccine group, Japanese Encephalitis vaccine group, Typhoid vaccine group, Yellow Fever vaccine group</p></th>
</tr>
<tr class="odd">
<th>Release 4.22</th>
<th>11/18/2025</th>
<th>ICE Team</th>
<th><p>(1) Mpox: (i) Added vaccinia (smallpox, mpox), unspecified (NOS) (CVX 325); (ii) a couple of vaccine name changes (see vaccine table descriptions)</p>
<p>(2) COVID-19: Added COVID-19, mRNA, LNP-S, PF, 10 mcg/0.2 mL, Moderna (mNEXSPIKE, 12+ yrs); (ii) various vaccine name changes (see vaccine table descriptions)</p></th>
</tr>
<tr class="header">
<th>Release 4.21</th>
<th>9/1/2025</th>
<th>ICE Team</th>
<th>(1) Added Influenza (CVX 331, CVX 333) and RSV vaccines (CVX 332)<br />
<br />
(2) Added NOT_SUPPORTED recommendation reason code</th>
</tr>
<tr class="odd">
<th>Release 4.20</th>
<th>6/30/2025</th>
<th>ICE Team</th>
<th><p>(1) Added new vaccine codes: PCV10 (CVX 177); PCV21 (CVX 327)</p>
<p>(2) Added new recommendation reason codes: ADMINISTER_PCV15_PCV20_OR_PCV21; ADMINISTER_PCV20_OR_PCV21</p>
<p>(3) New supplemental text for Pneumococcal. See <a href="https://docs.google.com/spreadsheets/d/1JB8QN2QeJACJmGdSkCaHr6LQ_8T942-J65Fjf_JDpic/edit?gid=1673907274#gid=1673907274">https://docs.google.com/spreadsheets/d/1JB8QN2QeJACJmGdSkCaHr6LQ_8T942-J65Fjf_JDpic/edit?gid=1673907274#gid=1673907274</a></p>
<p>(4) Fixed incorrect information in sample payload that an ACCEPTED evaluation counts as a valid dose; ACCEPTED evaluations do not count as a valid dose.</p>
<p>See News Entry and Release Notes on ICE Wiki for information about this release.</p></th>
</tr>
<tr class="header">
<th>Release 4.19</th>
<th>4/16/2025</th>
<th>ICE Team</th>
<th>Added CVX code 328 Meningococcal MenABCWY (Penmenvy)</th>
</tr>
<tr class="odd">
<th>Release 4.18</th>
<th>2/17/2025</th>
<th>ICE Team</th>
<th>No Implementation Guide Changes</th>
</tr>
<tr class="header">
<th>Release 4.17</th>
<th>11/1/2024</th>
<th>ICE Team</th>
<th>RSV Vaccine Group</th>
</tr>
<tr class="odd">
<th>Release 4.16</th>
<th>6/21/2024</th>
<th>ICE Team</th>
<th>Added vaccines: Meningococcal MenABCWY (Penbraya) (CVX 316); Influenza, MDCK, trivalent, preservative (CVX 320)</th>
</tr>
<tr class="header">
<th>Release 4.15</th>
<th>3/1/2024</th>
<th>ICE Team</th>
<th>Updated COVID-19 Logic. See Release Notes on the <a href="https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/23920670/News">ICE Wiki</a></th>
</tr>
<tr class="odd">
<th>Release 4.14</th>
<th>11/3/2023</th>
<th>Daryl Chertcoff / Erin Roche / Vikki Papadouka</th>
<th>Support for PCV20 for Child Series. See release notes for details. No changes to ICE Implementation Guide.</th>
</tr>
<tr class="header">
<th>Release 4.13</th>
<th>10/6/2023</th>
<th>Amy Moniz / Daryl Chertcoff / Erin Roche / Nette Arandez / Vikki Papadouka</th>
<th>Added ADMINISTER_TDAP_OR_TD recommendation reason code</th>
</tr>
<tr class="odd">
<th>Release 4.12</th>
<th>7/20/2023</th>
<th>Amy Moniz / Daryl Chertcoff / Erin Roche / Nette Arandez / Vikki Papadouka</th>
<th><p>(1) New vaccine (Influenza, southern hemisphere) - CVX 231<br />
(2) New evaluation reason code: VACCINE_NOT_YET_AVAILABLE_ON_DATE_SPECIFIED</p>
<p>(3) New recommendation reason code: ADMINISTER_COVID19_BIVALENT_VACCINE</p></th>
</tr>
<tr class="header">
<th>Release 4.10 / 4.11 (ICE versions 1.37 and 1.38)</th>
<th>12/2/2022</th>
<th><p>Amy Moniz / Erin Roche /</p>
<p>Nette Arandez / Vikki Papadouka / Daryl Chertcoff</p></th>
<th>(1) New vaccines / CVX codes in COVID-19, DTP, Hib, Hep B and Polio vaccine groups: CVX 198,</th>
</tr>
<tr class="odd">
<th>Release 4.9 (ICE version 1.36)</th>
<th>8/21/2022</th>
<th>Amy Moniz / Karrie Schwencer / Daryl Chertcoff</th>
<th><p>(1) New Vaccine Group – Orthopoxvirus vaccine group – May 2022 Emergency Use Authorization(EUA) for Monkeypox: 860</p>
<p>(2) New vaccines (Orthopoxvirus): CVX 75, 105, 206</p></th>
</tr>
<tr class="header">
<th>Release 4.8 (ICE version 1.35.1)</th>
<th>7/19/2022</th>
<th>Amy Moniz / Karrie Schwencer / Daryl Chertcoff</th>
<th><p>(1) New vaccines / CVX codes in Pneumococcal, COVID-19 and Hep B vaccine groups: 215, 216, 219, 220, 221, 227, 228, 512, 513, 514, 515, 516, 517</p>
<p>(2) Added new Evaluation Reason Code: OUTSIDE_ROUTINE_SERIES</p>
<p>(3) Added new Recommendation Reason Codes:</p>
<p>(i) ADMINISTER_PCV15_OR_PCV20</p>
<p>(ii) ADMINISTER_mRNA_VACCINE</p></th>
</tr>
<tr class="odd">
<th>Release 4.7 (ICE version 1.34)</th>
<th>2/1/2022</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th>No changes.</th>
</tr>
<tr class="header">
<th>Release 4.6 (ICE version 1.33)</th>
<th>12/22/2021</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th>(1) Added new COVID-19 CVX code: 217</th>
</tr>
<tr class="odd">
<th><p>Release 4.5</p>
<p>(ICE version 1.32.1)</p></th>
<th>11/24/2021</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Added new recommendation reason code: BOOSTER_DOSE</p>
<p>(2) Added new COVID-19 CVX code: 218</p></th>
</tr>
<tr class="header">
<th>Release 4.4 (ICE version 1.30.1)</th>
<th>9/21/2021</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Added new evaluation reason code: VACCINE_NOT_APPROVED_IN_US_OR_BY_WHO</p>
<p>(2) Added new COVID-19 CVX codes: 211, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511</p></th>
</tr>
<tr class="odd">
<th>Release 4.3 (ICE version 1.29)</th>
<th>5/13/2021</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th>(1) Addition of COVID-19 evaluation reason code: VACCINE_NOT_APPROVED_IN_US</th>
</tr>
<tr class="header">
<th>Release 4.2 (ICE version 1.28)</th>
<th>3/3/2021</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Addition of COVID-19 Janssen CVX code (212)</p>
<p>(2) Addition of COVID-19 AstraZeneca CVX code (210)</p></th>
</tr>
<tr class="odd">
<th>Release 4.1 (ICE version 1.27.1)</th>
<th>12/23/2020</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Addition of COVID-19 Unspecified vaccine code (CVX 213)</p>
<p>(2) Addition of recommendation reason code: BASED_ON_VAC_AVAIL_AND_PRIORITY_RECS. This recommendation reason code may be used in the COVID-19 forecast.</p></th>
</tr>
<tr class="header">
<th>Release 4.0 (ICE version 1.26.1)</th>
<th>12/14/2020</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Addition of COVID-19 Pfizer and Moderna vaccines (CVX codes 208, 207)</p>
<p>(2) Addition of COVID-19 Vaccine Group to Evaluation Focus and Recommendation Focus tables</p></th>
</tr>
<tr class="odd">
<th>Release 3.9 (ICE version 1.25.1)</th>
<th>12/4/2020</th>
<th>Daryl Chertcoff</th>
<th><p>(1) Addition of new Meningococcal ACWY CVX code 203 – Meningococcal MenACWY-TT</p>
<p>(2) ABOVE_REC_AGE no longer used</p></th>
</tr>
<tr class="header">
<th>Release 3.8 (ICE release 1.24.1)</th>
<th>11/6/2020</th>
<th>Daryl Chertcoff</th>
<th>No changes to this guide for this release.</th>
</tr>
<tr class="odd">
<th>Release 3.7 (ICE release 1.23.1)</th>
<th>9/13/2020</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Addition of new Influenza CVX codes: CVX codes 194, 197, 200, 201, 202, 205</p>
<p>(2) Addition of new evaluation reason code: VACCINE_NOT_ALLOWED_IN_US</p>
<p>(3) Evaluation reason coded value change: ABOVE_MAX_AGE_VACCINE changed to ABOVE_MAXIMUM_AGE_VACCINE/</p></th>
</tr>
<tr class="header">
<th>Release 3.6 (ICE</th>
<th>2/7/2020</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>(1) Addition of Evaluation and Reason codes:</p>
<ul>
<li><blockquote>
<p>SUPPLEMENTAL_TEXT</p>
</blockquote></li>
<li><blockquote>
<p>OUTSIDE_SERIES</p>
</blockquote></li>
</ul>
<p>(2) Support for accepting &lt;isValid/&gt; in input message, if optional feature enabled.</p>
<p>(3) Update evaluation reason and recommendation reason display text to match what is returned by ICE version 1.22.1.</p></th>
</tr>
<tr class="odd">
<th>Release 3.5 (ICE release 1.21.1)</th>
<th>10/16/2019</th>
<th>Daryl Chertcoff</th>
<th>Addition of Recommendation Reason code – ABOVE_REC_AGE</th>
</tr>
<tr class="header">
<th>Release 3.2 (ICE releases 1.16.1, 1.17.1, 1.20.1)</th>
<th><p>3/7/2019</p>
<p>5/31/2019</p>
<p>8/28/2019</p></th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th><p>ICE release 1.16.1: Added new recommendation reason code – TOO_OLD_TO_INITIATE. See p. 67</p>
<p>Bump to include ICE release 1.17.1</p>
<p>Bump to include ICE release 1.20.1</p></th>
</tr>
<tr class="odd">
<th>Release 3.1 (ICE release 1.15.1)</th>
<th>2/1/2019</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th>(1) New evaluation reason code – SELECT_ADJUVANT_PRODUCT_INTERVAL. See p. 65</th>
</tr>
<tr class="header">
<th>Release 2.20 (ICE release 1.12.1)</th>
<th>4/12/2018</th>
<th>Daryl Chertcoff</th>
<th><p>(1) Addition of 3 vaccines/CVX codes for Zoster and Hep B. See pp. 52-61</p>
<p>(2) New evaluation reason code. See p. 64</p></th>
</tr>
<tr class="odd">
<th>Release 2.19 (ICE release 1.11.1)</th>
<th>3/9/2018</th>
<th>Amy Moniz / Daryl Chertcoff / Maiko Minami</th>
<th>Information on how to read the Earliest Date and Past Due Date (<em>a.k.a.</em> “overdue” date)</th>
</tr>
<tr class="header">
<th>Release 2.18 (ICE release 1.9.2 and 1.10.1)</th>
<th>11/20/2017</th>
<th>Daryl Chertcoff</th>
<th><p>(1) CVX 148 incorrectly listed twice under Hib vaccine group.</p>
<p>(2) Fixed incorrect definition as previously defined in this document for the response payload’s “isValid” element. The prior definition and examples incorrectly stated that an ACCEPTED shot’s “isValid” element will be marked true. An ACCEPTED shot’s isValid element is false. The corrected definition states that isValid is only true if the shot is VALID.</p></th>
</tr>
<tr class="odd">
<th>Release 2.17 (ICE release 1.9.1)</th>
<th>10/6/2017</th>
<th>Daryl Chertcoff</th>
<th>Finalized guide for 1.9.1 release. Changes since prior (2.16) guide: removed CVX 164</th>
</tr>
<tr class="header">
<th>Release 2.16 (ICE release 1.9)</th>
<th>9/1/2017</th>
<th>Amy Moniz / Daryl Chertcoff</th>
<th>Changes for new Meningococcal B vaccine group, ahead of ICE v. 1.9 release. This guide is a draft. It is possible (though unlikely) that additional changes may be made to this guide when ICE v. 1.9 is released</th>
</tr>
<tr class="odd">
<th>Release 2.15 (ICE release 1.8.2)</th>
<th>8/2/2017</th>
<th>Daryl Chertcoff</th>
<th>Add Influenza vaccine (CVX 186)</th>
</tr>
<tr class="header">
<th><p>Release 2.14</p>
<p>(ICE release 1.8.1)</p></th>
<th>7/31/2017</th>
<th>Amy Moniz / Daryl Chertcoff / Maiko Minami</th>
<th><p>(1) Edits to combine Pneumococcal “PCV” and “PPSV” vaccine groups into one “Pneumococcal” vaccine group: “PCV” and “PPSV” vaccine groups removed from “Evaluation Focus” and “Recommendation Focus” code systems; “Pneumococcal” added</p>
<p>(2) Added additional vaccines (CVX codes) relevant to Influenza, DTP, and Polio to CVX code system</p>
<p>(3) Added new evaluation reason codes to “Evaluation Reasons” code system</p>
<p>(4) Renamed “Meningococcal” vaccine group “Meningococcal ACWY”</p></th>
</tr>
<tr class="odd">
<th>Release 2.13</th>
<th>12/1/2016</th>
<th>Daryl Chertcoff</th>
<th>Clarified all supported disease immunity code systems (ICD-9-CM, ICD-10, and SNOMED-CT)</th>
</tr>
<tr class="header">
<th>Release 2.12</th>
<th>9/23/2016</th>
<th>Daryl Chertcoff, Maiko Minami</th>
<th>Added Zoster vaccine group, vaccine; “Other” vaccine group; clarifications</th>
</tr>
<tr class="odd">
<th>Release 2.11</th>
<th>8/16/2016</th>
<th>Daryl Chertcoff</th>
<th>Added influenza vaccine codes: CVX 168, CVX 171</th>
</tr>
<tr class="header">
<th>Release 2.10</th>
<th>8/12/2016</th>
<th>Maiko Minami / Daryl Chertcoff</th>
<th>Update to disease immunity code mappings (ICD-10 and SNOMED-CT added)</th>
</tr>
<tr class="odd">
<th>Release 2.8</th>
<th>12/3/2015</th>
<th>Daryl Chertcoff</th>
<th>Prior documentation was missing CVX 148 and 166 in corresponding Vaccine Group section (Added 2.8). (For convenience, these changes are highlighted via Track Changes in two Tables in Section 5 of this document.)</th>
</tr>
<tr class="header">
<th>Release 2.7</th>
<th>9/22/2015</th>
<th>Daryl Chertcoff</th>
<th>Added CVX 166 code for Influenza</th>
</tr>
<tr class="odd">
<th>Release 2.6</th>
<th>7/3/2015</th>
<th>Daryl Chertcoff</th>
<th>Added codes for DTP; Added CVX code for HPV9</th>
</tr>
<tr class="header">
<th>Release 2.5</th>
<th>9/12/2014</th>
<th>Daryl Chertcoff</th>
<th>Added CVX code for Influenza 2014 – 2015 Influenza season</th>
</tr>
<tr class="odd">
<th>Release 2.4</th>
<th>08/29/2014</th>
<th>Michael Suralik</th>
<th>Changed document title and file name; Minor change to description of ICE in the ICE Overview.</th>
</tr>
<tbody>
</tbody>
</table>
