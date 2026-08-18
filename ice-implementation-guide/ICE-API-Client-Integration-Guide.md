# ICE API Client Integration Guide

This guide describes how to consume the ICE Forecast API published at
`https://ice-service-development.cdsframework.org/cds/ice-forecast/api-docs` and build a usable client plus associated data objects
in:

- Java
- Spring Boot
- Python
- C#.NET

It is based on the live OpenAPI document and sampled live responses on May 11, 2026.

## 1. What the API Actually Exposes

Base server:

- `https://ice-service-development.cdsframework.org`

Primary forecast operation:

- `POST /cds/$immunization-forecast`

Supporting-data operations:

- `GET /cds/supporting-data/vaccines`
- `GET /cds/supporting-data/vaccine-groups`
- `GET /cds/supporting-data/series`
- `GET /cds/supporting-data/series-plan-definition`
- `GET /cds/supporting-data/seasons`
- `GET /cds/supporting-data/module-plan-definitions`
- `GET /cds/supporting-data/diseases`
- `GET /cds/supporting-data/code-system`

Observed contract characteristics:

- The forecast endpoint accepts and returns FHIR `Parameters`.
- Forecast content types are `application/json` and `application/fhir+json`.
- Supporting-data endpoints return normal JSON and are good candidates for OpenAPI-based DTO generation.
- The OpenAPI document does not define a security scheme.
- Forecast business errors are returned in the response body as `operationOutcome`; do not rely on HTTP status alone.
- `/cds/supporting-data/series-plan-definition` returns a JSON array of `PlanDefinition` resources.

## 2. Recommended Integration Strategy

Use two different modeling strategies:

1. For `POST /cds/$immunization-forecast`
    - Do **not** rely on generated classes alone.
    - The OpenAPI schema for `Parameters.parameter[]` is effectively untyped.
    - Use either:
        - a native FHIR SDK for your platform, or
        - a small handwritten DTO layer that models only the subset of FHIR resources you send and read.

2. For the supporting-data endpoints
    - Generate DTOs from OpenAPI or hand-model them directly.
    - These schemas are concrete and stable enough to generate:
        - `PlanDefinition`
        - `CodeSystem`

## 3. Shared Object Model

Regardless of language, the clean object split is:

### Forecast request/response objects

- `ForecastRequest`
    - wraps a FHIR `Parameters`
    - contains:
        - `patient`
        - `assessmentDate`
        - `module`
        - repeated `immunization`
        - repeated `observation`
- `ForecastResponse`
    - wraps the returned FHIR `Parameters`
    - extracts `output.part[]` into:
        - `durationMs`
        - `engineVersion`
        - `guidanceResponse`
        - `evaluations`
        - `recommendations`
        - `operationOutcome`

### Supporting-data objects

- `PlanDefinition`
- `CodeSystem`
- `Collection<PlanDefinition>` for `/cds/supporting-data/series-plan-definition` and `/cds/supporting-data/module-plan-definitions`

### FHIR subset to model if you do not use a FHIR SDK

Only model what ICE actually needs:

- `Parameters`
- `Parameter`
- `Patient`
- `Immunization`
- `Observation`
- `GuidanceResponse`
- `ImmunizationEvaluation`
- `ImmunizationRecommendation`
- `OperationOutcome`
- supporting primitives:
    - `Identifier`
    - `Coding`
    - `CodeableConcept`
    - `Reference`
    - `Extension`

## 4. Download the OpenAPI Document

```bash
curl -sSLo ice-forecast-api.json \
  https://ice-service-development.cdsframework.org/cds/ice-forecast/api-docs
```

Use that file for DTO generation and for contract tests.

## 5. Java

### Recommended approach

For plain Java, I would use:

- `java.net.http.HttpClient` for transport
- Jackson for JSON
- generated DTOs for supporting-data endpoints
- handwritten forecast request/response wrappers around FHIR JSON

This keeps the forecast path explicit and avoids fighting the weak `Parameters` schema in the OpenAPI document.

### Suggested Java classes

- `IceApiClient`
- `ForecastRequestBuilder`
- `ForecastResponseParser`
- `CodeSystem`
- `PlanDefinition`
- `List<PlanDefinition>` for module and series PlanDefinition responses

### Generate supporting-data DTOs

```bash
openapi-generator generate \
  -i ice-forecast-api.json \
  -g java \
  -o generated/ice-java \
  --global-property models,apis,supportingFiles \
  --additional-properties=library=native,hideGenerationTimestamp=true
```

After generation:

- keep the concrete supporting-data models
- do not use the generated `Parameters` model as your main forecast abstraction
- treat `series-plan-definition` as `List<PlanDefinition>` or another collection type for your client language

### Minimal client shape

```java
public final class IceApiClient
{
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI baseUri;

    public IceApiClient(final HttpClient httpClient, final ObjectMapper objectMapper, final URI baseUri)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUri = baseUri;
    }

    public ForecastResponse forecast(final Parameters request) throws IOException, InterruptedException
    {
        final var json = objectMapper.writeValueAsString(request);
        final var httpRequest = HttpRequest.newBuilder(baseUri.resolve("/cds/$immunization-forecast"))
                .header("Content-Type", "application/fhir+json")
                .header("Accept", "application/fhir+json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        final var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        final var parameters = objectMapper.readValue(httpResponse.body(), Parameters.class);
        return ForecastResponseParser.parse(parameters);
    }
}
```

### Forecast parsing rule

Your parser should:

- find top-level `parameter` where `name == "output"`
- iterate `part[]`
- dispatch by `part.name`
- treat `operationOutcome` as an application-level failure even when HTTP status is `200`

## 6. Spring Boot

### Recommended approach

For Spring Boot, I would use:

- `WebClient`
- `@ConfigurationProperties` for host/module defaults
- generated DTOs for supporting-data
- a dedicated forecast adapter that posts FHIR JSON and maps the response

### Suggested Spring components

- `IceProperties`
- `IceForecastClient`
- `IceSupportingDataClient`
- `ForecastRequestFactory`
- `ForecastResponseParser`

### Properties

```yaml
ice:
  base-url: https://ice-service-development.cdsframework.org
  module-canonical: https://terminology.cdsframework.org/PlanDefinition/ice-forecast
  module-version: 1.0.0
```

### Client bean

```java

@Configuration
public class IceClientConfiguration
{

    @Bean
    WebClient iceWebClient(final IceProperties properties)
    {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, "application/fhir+json, application/json")
                .build();
    }
}
```

### Forecast client

```java

@Service
public class IceForecastClient
{
    private final WebClient webClient;

    public IceForecastClient(final WebClient webClient)
    {
        this.webClient = webClient;
    }

    public Mono<ForecastResponse> forecast(final Parameters request)
    {
        return webClient.post()
                .uri("/cds/$immunization-forecast")
                .contentType(MediaType.parseMediaType("application/fhir+json"))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Parameters.class)
                .map(ForecastResponseParser::parse);
    }
}
```

### Supporting-data client

Use generated models for:

- `/cds/supporting-data/vaccines`
- `/cds/supporting-data/vaccine-groups`
- `/cds/supporting-data/series`
- `/cds/supporting-data/seasons`
- `/cds/supporting-data/module-plan-definitions`
- `/cds/supporting-data/diseases`
- `/cds/supporting-data/code-system`
- `/cds/supporting-data/series-plan-definition`

### Best Spring Boot boundary

Expose a business-facing interface like:

```java
public interface ImmunizationForecastGateway
{
    ForecastResult getForecast(Patient patient, LocalDate assessmentDate, List<Immunization> immunizations,
            List<Observation> observations);
}
```

Keep raw FHIR `Parameters` construction inside the gateway implementation.

## 7. Python

### Recommended approach

For Python, I would use:

- `httpx` for transport
- `pydantic` models for the FHIR subset and response extraction
- generated or handwritten models for supporting-data

### Suggested Python modules

- `ice_client.py`
- `forecast_models.py`
- `supporting_data_models.py`
- `forecast_parser.py`

### Generate supporting-data client

```bash
openapi-generator generate \
  -i ice-forecast-api.json \
  -g python \
  -o generated/ice_python_client
```

Use the generated package mainly for supporting-data DTOs and endpoint stubs. Keep the forecast request body in explicit local
models.

### Minimal Python client

```python
import httpx

class IceClient:
    def __init__(self, base_url: str = "https://ice-service-development.cdsframework.org") -> None:
        self._client = httpx.Client(base_url=base_url, timeout=30.0)

    def forecast(self, parameters: dict) -> dict:
        response = self._client.post(
            "/cds/$immunization-forecast",
            json=parameters,
            headers={
                "Content-Type": "application/fhir+json",
                "Accept": "application/fhir+json",
            },
        )
        response.raise_for_status()
        body = response.json()
        return parse_forecast_response(body)

    def get_vaccines(self, module_canonical: str, module_version: str) -> dict:
        response = self._client.get(
            "/cds/supporting-data/vaccines",
            params={
                "moduleCanonical": module_canonical,
                "moduleVersion": module_version,
            },
        )
        response.raise_for_status()
        return response.json()
```

### Pydantic model guidance

Model the forecast request as:

- `Parameters`
- `Parameter`
- `Patient`
- `Immunization`
- `Observation`

Model the forecast response extraction as:

- `ForecastResponse`
- `ForecastOutputPart`
- `OperationOutcomeIssue`

For supporting-data, generate or handwrite:

- `PlanDefinition`
- `CodeSystem`

### Important Python note

Treat `/cds/supporting-data/series-plan-definition` as:

```python
list[PlanDefinition]
```

If generated code models that response as a non-array type, patch it to a list of `PlanDefinition`.

## 8. C#.NET

### Recommended approach

For .NET, I would use:

- `IHttpClientFactory`
- `System.Text.Json`
- records for forecast DTOs or a platform FHIR SDK if your solution already depends on one
- generated supporting-data models from OpenAPI

### Suggested .NET types

- `IceClient`
- `ForecastRequestBuilder`
- `ForecastResponse`
- `CodeSystem`
- `PlanDefinition`
- `List<PlanDefinition>` for module and series PlanDefinition responses

### Generate supporting-data DTOs

```bash
openapi-generator generate \
  -i ice-forecast-api.json \
  -g csharp \
  -o generated/ice-csharp \
  --additional-properties=packageName=IceForecastClient,targetFramework=net8.0
```

You can also use NSwag for the same purpose. The same rule applies either way: keep the forecast body modeled locally, not only
through generated `Parameters` types.

### Minimal .NET client

```csharp
public sealed class IceClient
{
    private readonly HttpClient _httpClient;

    public IceClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<ForecastResponse> ForecastAsync(Parameters request, CancellationToken cancellationToken = default)
    {
        using var response = await _httpClient.PostAsJsonAsync(
            "/cds/$immunization-forecast",
            request,
            cancellationToken);

        response.EnsureSuccessStatusCode();

        var parameters = await response.Content.ReadFromJsonAsync<Parameters>(cancellationToken: cancellationToken);
        return ForecastResponseParser.Parse(parameters!);
    }
}
```

### Registration

```csharp
services.AddHttpClient<IceClient>(client =>
{
    client.BaseAddress = new Uri("https://ice-service-development.cdsframework.org");
    client.DefaultRequestHeaders.Accept.ParseAdd("application/fhir+json");
});
```

### Parsing rule

Exactly like Java and Python:

- locate `output`
- read `part[]`
- branch by `name`
- surface `operationOutcome` as a typed error/result object

## 9. Practical Data-Object Decisions

If you want the least integration friction, create these local forecast-side types in every language:

- `Parameters`
- `Parameter`
- `Patient`
- `Immunization`
- `Observation`
- `GuidanceResponse`
- `ImmunizationEvaluation`
- `ImmunizationRecommendation`
- `OperationOutcome`
- `ForecastResponse`

Generate or hand-model these supporting-data types:

- `PlanDefinition`
- `CodeSystem`

This split maps cleanly to the actual API shape.

## 10. Recommended Client Surface

In all four stacks, keep the public client API small:

```text
forecast(patient, assessmentDate, immunizations, observations, moduleCanonical, moduleVersion)
getVaccines(moduleCanonical, moduleVersion)
getVaccineGroups(moduleCanonical, moduleVersion)
getSeries(moduleCanonical, moduleVersion)
getSeriesPlanDefinitions(moduleCanonical, moduleVersion)
getSeasons(moduleCanonical, moduleVersion)
getModulePlanDefinitions()
getDiseases(moduleCanonical, moduleVersion)
getCodeSystem(moduleCanonical, moduleVersion, name)
```

Build the FHIR `Parameters` envelope internally. Do not force downstream callers to assemble raw `parameter[]` arrays themselves.

## 11. Notes for vMR Migration

### Vaccine Group Normalization

The prior vMR-based output model exposed vaccine-group-focused results. FHIR does not have an equivalent vaccine group result
concept in the forecast output. Under the FHIR contract, disease targeting is represented at the component level:

- `ImmunizationEvaluation.targetDisease`
- `ImmunizationRecommendation.recommendation.targetDisease`

Those `targetDisease` values are disease `CodeableConcept` values, not vaccine group values. When an implementation needs to group
FHIR evaluations or recommendations by the legacy ICE vaccine group, or compare FHIR output to vMR output during A/B testing, it
must normalize disease indicators back to vaccine groups.

Use the supporting-data endpoints to build the normalization map:

- `/cds/supporting-data/vaccine-groups`
    - Each vaccine group concept has an internal concept `code`.
    - Each vaccine group concept has a numeric vMR-facing value in the `outboundCode.code` concept property.
    - Each vaccine group concept has one or more `diseaseImmunity` concept property assignments.
    - Most vaccine groups map one-to-one to a disease. Multi-disease groups include:
        - `DTP` -> `DIPHTHERIA`, `PERTUSSIS`, `TETANUS`
        - `MMR` -> `MEASLES`, `MUMPS`, `RUBELLA`
- `/cds/supporting-data/diseases`
    - Disease concepts contain the SNOMED CT `outboundCode.code` used in FHIR `targetDisease` codings.
    - FHIR output uses this disease outbound code, not the vaccine-group numeric outbound code.
- `/cds/supporting-data/vaccines`
    - Vaccine concepts are CVX-based.
    - Vaccine concepts have one or more `diseaseImmunity` concept property assignments using supporting-data disease codes.
    - Use these assignments when deriving target diseases from administered vaccine CVX codes. For FHIR output grouping, prefer the
      emitted `targetDisease` values when present.

ICE may use vaccine group supporting-data concept `code` values internally, while FHIR output uses disease supporting-data
`outboundCode.code` values from SNOMED CT. Consumers should not compare vMR numeric vaccine group codes directly to FHIR
`targetDisease` codes.

Build these lookup tables from supporting data:

```text
vaccineGroupCode -> {
  vmrNumericCode,
  diseaseCodes
}

vmrNumericCode -> vaccineGroupCode
diseaseCode -> vaccineGroupCode(s)
diseaseSnomedCode -> diseaseCode
cvxCode -> diseaseCode(s)
```

Normalize vMR output:

1. Read the vMR vaccine group numeric indicator.
2. Resolve it through `vmrNumericCode -> vaccineGroupCode`.
3. Use `vaccineGroupCode` as the comparison/grouping key.

Normalize FHIR output:

1. For each `ImmunizationEvaluation.targetDisease` and each `ImmunizationRecommendation.recommendation.targetDisease`, read all
   codings.
2. Find SNOMED CT codings, normally using system `http://snomed.info/sct`, that match `/cds/supporting-data/diseases`
   `outboundCode.code` values.
3. Resolve each SNOMED CT code through `diseaseSnomedCode -> diseaseCode`.
4. Resolve each disease code through `diseaseCode -> vaccineGroupCode(s)`.
5. Use the resolved vaccine group code as the comparison/grouping key.

If a target disease is a component of a multi-disease group, group it under that vaccine group. For example, a FHIR target disease
of measles SNOMED CT `14189004` normalizes to disease `MEASLES`, which normalizes to vaccine group `MMR`. A diphtheria and tetanus
product normalizes to vaccine group `DTP` because both disease components are members of the `DTP` group.

When a FHIR item contains multiple target disease codings, normalize each disease coding independently and de-duplicate the
resulting vaccine group keys. If a future disease maps to more than one vaccine group, emit one normalized comparison record per
resolved group and keep the original disease code for traceability.

For vMR-to-FHIR A/B testing, compare normalized records rather than raw output codes:

```text
normalizedForecastItem {
  source: "vMR" | "FHIR"
  itemType: "evaluation" | "recommendation"
  vaccineGroupCode
  diseaseCode(s)
  targetDiseaseSnomedCode(s)
  status/reason fields relevant to the assertion
  dose/series fields relevant to the assertion
}
```

The stable comparison key should be the normalized `vaccineGroupCode` plus the relevant clinical dimensions for the assertion, such
as dose number, series, recommendation status, evaluation status, and reason codes. Preserve disease-level details in the normalized
FHIR record so component-level differences remain visible during debugging.

The following table is useful for migration tests and static fixtures. Runtime code should prefer deriving this from supporting
data.

```yaml
"ANTHRAX": [ "ANTHRAX", "991", "409498004" ]
"CHOLERA": [ "CHOLERA", "901", "63650001" ]
"COVID_19": [ "COVID_19", "850", "840539006" ]
"DTP": [ "DTP", "200", "DIPHTHERIA", "PERTUSSIS", "TETANUS", "397428000", "27836007", "76902006" ]
"HEP_A": [ "HEP_A", "810", "40468003" ]
"HEP_B": [ "HEP_B", "100", "66071002" ]
"HIB": [ "HIB", "300", "709410003" ]
"HPV": [ "HPV", "840", "240532009" ]
"INFLUENZA": [ "INFLUENZA", "800", "6142004" ]
"INFLUENZA_H1N1": [ "INFLUENZA_H1N1", "890", "442438000" ]
"JAPANESE_ENCEPHALITIS": [ "JAPANESE_ENCEPHALITIS", "902", "52947006" ]
"MENINGOCOCCAL_ACWY": [ "MENINGOCOCCAL_ACWY", "830", "23511006" ]
"MENINGOCOCCAL_B": [ "MENINGOCOCCAL_B", "835", "1354584007" ]
"MMR": [ "MMR", "500", "MEASLES", "MUMPS", "RUBELLA", "14189004", "36989005", "36653000" ]
"MOSQUITO_BORNE": [ "MOSQUITO_BORNE", "992", "4730003" ]
"MPOX": [ "MPOX", "860", "359814004" ]
"PNEUMOCOCCAL": [ "PNEUMOCOCCAL", "750", "PNEUMOCOCCAL_PNEUMONIA", "233604007" ]
"POLIO": [ "POLIO", "400", "398102009" ]
"ROTAVIRUS": [ "ROTAVIRUS", "820", "18624000" ]
"RSV": [ "RSV", "875", "55735004" ]
"TYPHOID": [ "TYPHOID", "904", "4834000" ]
"VARICELLA": [ "VARICELLA", "600", "38907003" ]
"YELLOW_FEVER": [ "YELLOW_FEVER", "905", "16541001" ]
"ZOSTER": [ "ZOSTER", "620", "4740000" ]
```

## 12. Integration Caveats to Capture in Code

- Accept both `application/json` and `application/fhir+json`.
- Always inspect the response body for `operationOutcome`.
- Keep `moduleCanonical` and `moduleVersion` configurable.
- Patch the generated type for `/cds/supporting-data/series-plan-definition` to a `PlanDefinition` collection if the generator still
  treats it as a non-array type.
- Add contract tests that deserialize:
    - one successful forecast response
    - one forecast response containing `operationOutcome`
    - one `/cds/supporting-data/module-plan-definitions` response
    - one `/cds/supporting-data/series-plan-definition` response

## 13. Bottom Line

The clean implementation is:

- generate or hand-model the supporting-data endpoints directly from OpenAPI
- model the forecast endpoint with a small explicit FHIR layer
- keep the public client surface business-oriented
- treat `series-plan-definition` as a `PlanDefinition` collection

That approach is robust in Java, Spring Boot, Python, and C#.NET and fits the API that ICE actually publishes today.
