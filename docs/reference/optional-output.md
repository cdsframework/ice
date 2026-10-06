# Optional Output Codes

**Applicability:** ICE SOAP/vMR API output-focus codes. The ICE FHIR API (FHIR R6) represents these outputs through structured
fields and extensions described in [Output Configuration](../configuration/output-configuration.md).

The vMR optional-output focus code system is `2.16.840.1.113883.3.795.12.100.500`.

| Code                                     | Code system                          | Description                                                             |
|------------------------------------------|--------------------------------------|-------------------------------------------------------------------------|
| `SERIES_DISPLAY_OPTIONS`                 | `2.16.840.1.113883.3.795.12.100.500` | Output series selection(s) and corresponding number of doses remaining. |
| `VACCINE_GROUP_RULES_URL`                | `2.16.840.1.113883.3.795.12.100.500` | URL for the vaccine-group rules artifact, when available.               |
| `ICE_VACCINE_GROUP_SCHEDULE_AUTHORITIES` | `2.16.840.1.113883.3.795.12.100.500` | Schedule authority or authorities for the vaccine group.                |

`VACCINE_GROUP_RULES_URL` is returned only when
`output-vaccine-group-rules-artifact` is enabled and a URL is configured for
the vaccine group. FHIR R6 represents the same information with the
`vaccine-group-rules-artifact` extension.
See [Output Configuration](../configuration/output-configuration.md#vaccine-group-rules-artifact-and-schedule-authorities)
for instructions to enable the output.

For the vaccine-group code details and its output setting, see [Vaccine Group Codes](vaccine-groups.md)
and [Output Configuration](../configuration/output-configuration.md#series-information-and-doses-remaining).
