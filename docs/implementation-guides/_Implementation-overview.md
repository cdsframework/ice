# Implementation Guides

Use these guides to integrate a client application with ICE, understand its request and response formats, or move an existing
integration to another interface. ICE provides the ICE SOAP/vMR API and the ICE FHIR API (FHIR R6). Start with the implementation
guide for the interface your application uses, then use the migration or client integration guide as needed.

## ICE SOAP/vMR API

Use the [ICE vMR Implementation Guide](ICE-vMR-Implementation-Guide.md) to invoke ICE through SOAP, construct vMR requests, and
interpret evaluation and recommendation results. It describes the message structures and how clinical information is represented
in them.

## ICE FHIR API

Use the [ICE FHIR R6 Implementation Guide](ICE-FHIR-R6-Implementation-Guide.md) for the FHIR forecast operation, required and optional
inputs, response resources, and ICE-specific extensions. It also explains experimental feature availability and the changes from
the previous experimental FHIR contract.

## Migrating from vMR to FHIR

Use [Migrating from vMR to FHIR](migrating-from-vmr-to-fhir.md) when adapting an existing SOAP/vMR integration to the ICE FHIR API.
It explains the differences in request and response structure, clinical-data mappings, and configuration that need review during
migration. The linked implementation guides provide the detailed contracts.

## Building a FHIR Client

Use the [ICE API Client Integration Guide](ICE-API-Client-Integration-Guide.md) for practical guidance on building an ICE FHIR API
client in Java, Spring Boot, Python, or C#.NET. It covers client and data-object generation, supporting-data endpoints, and handling
forecast responses and errors.

## Related Guidance

For settings that control service behavior or returned information, see the
[Configuration Overview](../configuration/configuration-overview.md). For supported code values and terminology, see the
[ICE Code Values index](../reference/_ice-code-values.md).
