# ICE Technical Documentation

Technical documentation for developers, implementers, and DevOps teams deploying, integrating, configuring, or maintaining ICE.

For clinical guidance, default immunization rules, news, and other user-facing ICE documentation, see
the [ICE Confluence wiki](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352468/Default+Immunization+Schedule).

ICE offers two forecast interfaces: the **ICE SOAP/vMR API**, which exchanges vMR messages over SOAP, and the **ICE FHIR API (FHIR
R6)**. The guides below document each interface and how to move between them.

## Getting Started

- [Installation](https://github.com/cdsframework/ice#installation)
- [Building ICE](https://github.com/cdsframework/ice#building-from-source)
- [Running ICE](https://github.com/cdsframework/ice#running-ice)

## Implementation

- [Implementation Overview](implementation-guides/_Implementation-overview.md)
- [ICE vMR Implementation Guide](implementation-guides/ICE-vMR-Implementation-Guide.md)
- [ICE FHIR R6 Implementation Guide](implementation-guides/ICE-FHIR-R6-Implementation-Guide.md)
- [Migrating from VMR to FHIR](implementation-guides/migrating-from-vmr-to-fhir.md)

## API Integration

- [ICE API Client Integration Guide](implementation-guides/ICE-API-Client-Integration-Guide.md)
- [API / Swagger Documentation](https://ice-service.cdsframework.org/opencds-decision-support-service/cds/ice-forecast/swagger-ui/index.html#/)

## Configuration

Technical guidance for configuring ICE service behavior and request-specific forecast options.

- [Configuration Overview](configuration/configuration-overview.md)
- [Runtime Configuration](configuration/runtime-configuration.md)
- [Schedule Flags](configuration/schedule-flags.md)
- [Series and Season Overrides](configuration/series-and-season-overrides.md)
- [Output Configuration](configuration/output-configuration.md)

## Reference

- [ICE Code Values index](reference/_ice-code-values.md)
    - [Administrative Gender Codes](reference/gender.md)
    - [Contextual Conditions](reference/contextual-conditions.md)
    - [Disease and Disease Immunity Codes](reference/disease-immunity.md)
    - [Evaluation and Recommendation Reason Codes](reference/evaluation-and-recommendation-reasons.md)
    - [Evaluation and Recommendation Status Codes](reference/evaluation-and-recommendation-statuses.md)
    - [Evaluation and Recommendation Supplemental Text](reference/evaluation-and-recommendation-supplemental-text.md)
    - [FHIR Target Diseases](reference/target-diseases.md)
    - [Optional Output Codes](reference/optional-output.md)
    - [Purpose Code](reference/purpose.md)
    - [Schedule Authority Codes](reference/schedule-authority.md)
    - [Season Codes](reference/season.md)
    - [Series and Display Codes](reference/series.md)
    - [Supported Vaccines](reference/supported-vaccines.md)
    - [Vaccine Group Codes](reference/vaccine-groups.md)

## Releases and Upgrades

- Release Notes
- Upgrade Information
- Action Required for Implementers

## Security and Support

- [Vulnerability Remediation SLA](policies/ICE_Vulnerability_Remediation_SLA.md)
