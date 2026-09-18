# Schedule Flags

Schedule flags alter a specific evaluation rule. A service can configure them
for every forecast, or a client can supply them for an individual forecast.
An unknown flag makes the request invalid.

This page describes the currently supported schedule flags and their
service-wide and request-level behavior.

## Currently defined flags

| Flag | Effect |
|---|---|
| `HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID` | Evaluate an invalid third Hepatitis B dose as an accepted extra dose. |
| `POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID` | Evaluate a fourth or fifth polio dose below the minimum age as an accepted extra dose. |

## Service-wide configuration

Set flags for every SOAP and FHIR forecast handled by the service instance
with an environment variable:

```bash
ICE_SCHEDULEFLAGS=HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID,POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
```

Equivalent YAML is:

```yaml
ice:
  schedule-flags:
    - HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
    - POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
```

Configured flags are added only when the incoming SOAP vMR payload does not
already contain that flag. In FHIR, configured flags are combined with request
flags and submitted once.

The service validates configured flags during service initialization. A
misspelled or unsupported value prevents startup. An unknown request flag makes
that request invalid.

## SOAP request-level usage

For SOAP, put the flag in the base64-encoded vMR `CDSInput` payload, under the
patient's `clinicalStatements/observationResults`. The SOAP envelope itself is
unchanged. The decoded payload must contain an `observationResult` like this:

```xml
<observationResult>
    <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>
    <id root="2f0d8eb9-66c5-458a-a70f-ff1d539b3cc4"/>
    <observationFocus
            code="HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"
            codeSystem="2.16.840.1.113883.3.795.12.100.502"
            displayName="Evaluate Invalid 3rd Hep B Dose as Accepted Extra Dose"/>
    <observationValue>
        <boolean value="true"/>
    </observationValue>
</observationResult>
```

The focus `codeSystem` may instead be
`http://terminology.cdsframework.org/ice/schedule-flags`. The boolean value must
be `true`. Use a unique `id.root` for each observation. Repeat the
`observationResult` element to send more than one flag, then base64-encode the
updated vMR XML in the ordinary SOAP `base64EncodedPayload` element.

## FHIR request-level usage

The FHIR forecast endpoint is `POST /cds/$immds-forecast` and must first be
enabled with:

```bash
CDS_ENGINE_EXPERIMENTALFEATURES_ENABLEFHIRR6=true
```

Add one repeatable `Parameters.parameter` for each flag:

```json
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "scheduleFlag",
      "valueCode": "HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"
    },
    {
      "name": "scheduleFlag",
      "valueCode": "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"
    }
  ]
}
```

Include the required request parameters in the same `Parameters` resource.
The request can use either `application/fhir+json` or `application/json`.

For the distinction between service-wide and request-level configuration, see
[Runtime Configuration](runtime-configuration.md).
