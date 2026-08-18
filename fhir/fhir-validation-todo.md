# FHIR Validation TODO

Current validation target: FHIR R6 `6.0.0-ballot3` via HL7 validator `6.9.12`.

Validator command base:

```bash
java -jar /tmp/hln-ice-fhir-validation/validator_cli.jar <file>.json -version 6.0.0-ballot3
```

Validator package: `hl7.fhir.r6.core#6.0.0-ballot3`.

## Current Validation Results

### Request

Validated file:

```text
/tmp/hln-ice-fhir-validation/request-pasted.json
```

Latest fixed request file:

```text
/tmp/hln-ice-fhir-validation/request-pasted-fixed.json
```

Report:

```text
/tmp/hln-ice-fhir-validation/request-pasted-validation-ballot3.json
```

Latest fixed request report:

```text
/tmp/hln-ice-fhir-validation/request-pasted-fixed-validation-with-kb-ballot3.json
```

Counts:

- Error: 0
- Warning: 16
- Information: 2

Open items:

- [x] Fix CVX display for `http://hl7.org/fhir/sid/cvx#111`. Current display is `Influenza-LAIV3, IN, (2-49yrs)`. Validator expects
  `Influenza, live, trivalent, intranasal, PF`.
- [x] Keep the request `knowledgeBase` exception canonical as
  `https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0`.
- [ ] Make the request `knowledgeBase` canonical resolvable to the validator. Local artifact now exists at
  `fhir/PlanDefinition/ice-forecast.json`
  with canonical `https://terminology.cdsframework.org/PlanDefinition/ice-forecast`. Validator `-ig` loading still ignores local
  ballot3 resources:
  `Unsupported version 6.0.0-ballot3`. Hosted check on 2026-07-23:
  `https://terminology.cdsframework.org/PlanDefinition/ice-forecast.json`
  returns 200 with the correct canonical, but extensionless
  `https://terminology.cdsframework.org/PlanDefinition/ice-forecast`
  returns 404.
- [x] Ignore OID request CodeSystem warnings for now:
  `urn:oid:2.16.840.1.113883.6.103` and
  `urn:oid:2.16.840.1.113883.3.795.12.100.8`.

### Response

Validated file:

```text
/tmp/hln-ice-fhir-validation/response-new-normalized.json
```

This is a normalized copy of the pasted response with the same resource shapes, required fields, codes, systems, and ICE extensions.
It is not a byte-for-byte copy of the pasted response.

Report:

```text
/tmp/hln-ice-fhir-validation/response-new-normalized-validation-rerun.json
```

Latest ballot3 report:

```text
/tmp/hln-ice-fhir-validation/response-new-normalized-validation-ballot3.json
```

Counts:

- Error: 38
- Warning: 82
- Information: 11

Current response errors all come from unresolved ICE extensions:

- 15 occurrences: `https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context`
- 23 occurrences: `https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact`

Open items:

- [ ] Make ICE extension StructureDefinitions loadable as validator support artifacts.
- [ ] Re-run response validation after extension definitions load.
- [ ] Publish or load ICE CodeSystems used by the response:
  `evaluation-status`, `evaluation-reason`, `recommendation-status`,
  `recommendation-reason`, `series`, `series-display-selection-type`, and `seasons`.
- [ ] Generate and publish/load required ValueSets referenced by local StructureDefinitions:
  `ValueSet/ice-series`, `ValueSet/ice-vaccine-groups`,
  `ValueSet/ice-series-display-selection-type`, and `ValueSet/ice-seasons`.
- [ ] Review SNOMED CT code `397428000` because the terminology server reports it as inactive.
- [x] Confirm `GuidanceResponse.moduleCanonical` should use the exception canonical
  `https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0`
  everywhere.

## Local ICE Artifact Problems

The standalone validation of these two files produced no issues:

```text
fhir/ice/StructureDefinition/immunization-selection-context.json
fhir/ice/StructureDefinition/vaccine-group-rules-artifact.json
```

Report:

```text
/tmp/hln-ice-fhir-validation/ice-extension-sds-validation-rerun.json
```

Latest ballot3 report:

```text
/tmp/hln-ice-fhir-validation/ice-extension-sds-validation-ballot3.json
```

Standalone validation of the two extension StructureDefinitions passes with no reported issues. However, attempts to load them as
`-ig` support artifacts still fail.

Latest ballot3 response-with-extension-support report:

```text
/tmp/hln-ice-fhir-validation/response-new-normalized-with-extensions-validation-ballot3.json
```

Latest `-ig` load failure:

```text
ignored due to error: Unsupported version 6.0.0-ballot3
```

Earlier `-ig` load failure when files declared `fhirVersion: 6.0.0-ballot4`:

```text
ignored due to error: Unsupported version 6.0.0-ballot4
```

Open items:

- [x] Remove top-level OID `identifier` entries from generated ICE CodeSystem JSON under `fhir/ice`.
- [ ] Align local R6 artifacts to `fhirVersion: 6.0.0-ballot3`.
- [ ] Ensure all ICE `StructureDefinition` files use one consistent R6 `fhirVersion`.
- [ ] Determine why standalone validation accepts the extension StructureDefinitions but `-ig` loading ignores them. Hosted check on
  2026-07-23:
  `https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context`
  and
  `https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact`
  return 200, but both hosted files still declare
  `fhirVersion: 6.0.0-ballot4`, so the R6 ballot3 validator ignores them.
- [ ] Convert the loose `fhir/ice` directory into a validator-loadable IG/package.
- [ ] Add a repeatable validation command or script once the artifact package loads cleanly.

## Published Artifact Validation Findings

Additional findings reviewed from the `cds-project-iac` validation todo:

- [ ] Apply and verify nested extensionless URL rewrite for published artifacts. Root-level `/ice/<id>` URLs resolved, but nested
  canonicals returned 404 before the load balancer fix. Affected paths include:
  `ice/OperationDefinition/immds-forecast`,
  `ice/PlanDefinition/common-ice-forecast`,
  `PlanDefinition/ice-forecast`, and all `ice/StructureDefinition/...` artifacts.
- [ ] Verify the infrastructure rewrite uses a nested path matcher such as `/ice/{resource=**}` so extensionless nested canonical
  URLs resolve to matching `.json` objects.
- [ ] Re-run live smoke tests after deployment and mark complete only when extensionless URLs return 200 and match their `.json`
  bodies.
- [ ] Fix generated CodeSystem property codings that use non-absolute or missing `Coding.system`. Previously affected artifacts:
  `hl7.org/fhir/sid/cvx.json`, `ice/seasons.json`, and `ice/vaccine-group.json`.
- [ ] Review `ice/disease.json` validation errors and SNOMED mapping warnings.
- [ ] Fix malformed `ice/supplemental-recommendation-reason.json` concept properties where text fragments were emitted as property
  names.
- [ ] Fix extension StructureDefinition differential/snapshot shape for:
  `ice-dose-age-constraint`, `ice-dose-interval-constraint`, `ice-dose-vaccine`, and `ice-series-metadata`.
- [ ] Fix `ice-series-plan-definition` StructureDefinition differential/snapshot shape.
- [ ] Fix ballot version and snapshot issues in:
  `immunization-selection-context` and `vaccine-group-rules-artifact`.

## Publication URL Rules

Keep FHIR canonical URLs extensionless:

```text
https://terminology.cdsframework.org/ice/evaluation-status
```

Static JSON file URLs may include `.json`:

```text
https://terminology.cdsframework.org/ice/evaluation-status.json
```

Open items:

- [ ] Configure hosting so extensionless canonical URLs resolve, redirect, or content-negotiate to the JSON artifacts.
- [ ] Serve JSON with `application/fhir+json` where possible.
- [ ] Do not put `.json` into `CodeSystem.url`, `StructureDefinition.url`, `PlanDefinition.url`, or other canonical fields.

## Done

- [x] Downloaded and ran the HL7 validator.
- [x] Validated the corrected request body as R6 `Parameters`.
- [x] Validated a normalized copy of the new response as R6 `Parameters`.
- [x] Confirmed the request is structurally valid apart from terminology/canonical resolution issues.
- [x] Confirmed the response errors are dominated by unresolved ICE extension definitions.

## Historical Published-Artifact Validation Baseline

Started: 2026-07-21

Scope:

- Validate published ICE FHIR JSON artifacts under `https://terminology.cdsframework.org`.
- Keep canonical URLs extensionless.
- Track every validation or resolvability issue here until it is addressed.

Validation commands:

- Local structure validation:
  `java -jar /home/codex/Documents/Projects/ice-testing/tmp/fhir-validator/validator_cli.jar <artifact.json> -version 6.0.0-ballot3 -tx n/a`
- Live resolvability smoke test: compare extensionless and `.json` URLs for each published artifact.

Completed baseline work:

- [x] Run local FHIR validation for all published JSON artifacts.
    - Run output: `tmp/fhir-validation/20260721T232330Z/summary.txt`
    - Validator: `/home/codex/Documents/Projects/ice-testing/tmp/fhir-validator/validator_cli.jar`
    - Command shape: `java -jar validator_cli.jar <artifact.json> -version 6.0.0-ballot3 -tx n/a`
    - Result: 27 artifacts checked, 15 passed with 0 errors, 12 failed.

- [x] Run live extensionless URL smoke tests for all published `ice/**.json` artifacts.
    - Run output: `tmp/fhir-validation/20260721T232330Z/live-smoke.txt`
    - Result before follow-up load balancer fix: 24 artifacts checked, 13 extensionless URLs matched the `.json` object, 11 nested
      extensionless URLs returned 404.

Details that remain represented by the open items above:

- Nested extensionless URLs that need verification include `ice/OperationDefinition/immds-forecast`,
  `ice/PlanDefinition/common-ice-forecast`, `ice/PlanDefinition/ice-forecast`, and the affected
  `ice/StructureDefinition/...` artifacts. The proposed Terraform rewrite changes
  `/ice/{resource=*}` to `/ice/{resource=**}`. Local `terraform fmt -recursive` and
  `terraform validate` passed, but the HCP Terraform plan was blocked by unrelated remote-state attributes:
  `enforce_new_sql_network_architecture`, `is_cluster_ai_assistant_enabled`, and
  `workload_identity_config`.
- Earlier artifact-specific results: `hl7.org/fhir/sid/cvx.json` had 477 errors and 195 warnings;
  `ice/seasons.json` had 12 errors and 12 warnings; `ice/vaccine-group.json` had 53 errors and 25 warnings;
  `ice/disease.json` had 12 errors and 34 warnings; and `ice/supplemental-recommendation-reason.json` had 5 errors.
- The extension StructureDefinition error counts were 3 each for `ice-dose-age-constraint`,
  `ice-dose-interval-constraint`, and `ice-dose-vaccine`; 3 errors and 2 warnings for
  `ice-series-metadata`; 11 errors and 9 warnings for `ice-series-plan-definition`; 22 errors and 10 warnings for
  `immunization-selection-context`; and 5 errors and 1 warning for
  `vaccine-group-rules-artifact`.
