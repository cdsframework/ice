# Configuration

Use these pages to find the setting or request option that controls the ICE behavior you want to change. Service-wide configuration
applies to every forecast handled by an ICE instance. Series and season overrides change the effective schedule for that instance.
Schedule flags can also be supplied for an individual request; output settings control what additional information ICE returns.

## Runtime Configuration

Use [Runtime Configuration](runtime-configuration.md) for service-wide settings and how to supply them at startup. It covers general
feature controls and which vaccine groups the service forecasts.

## Schedule Flags

Use [Schedule Flags](schedule-flags.md) to change a specific evaluation rule. Flags can be configured for every forecast or supplied
by a client for one SOAP/vMR or FHIR request.

## Series and Season Overrides

Use [Series and Season Overrides](series-and-season-overrides.md) to customize the effective schedule for an ICE instance, such as
dose ages or intervals and season dates. These are service-wide settings.

## Output Configuration

Use [Output Configuration](output-configuration.md) to control optional or conditional information returned by ICE. Examples include
supplemental text, series and season details, vaccine-group rules artifact URLs, and vaccine-group schedule authorities. The linked
page explains the settings and their SOAP/vMR and FHIR response forms.
