package org.cdsframework.ice.service.conversion;

import java.time.LocalDate;
import java.util.List;

import org.cdsframework.fhir.Immunization;
import org.cdsframework.fhir.Observation;
import org.cdsframework.fhir.OperationOutcome;
import org.cdsframework.fhir.Patient;

record RequestContext(LocalDate assessmentDate,
                      String moduleCanonical,
                      Patient patient,
                      List<Immunization> immunizations,
                      List<Observation> observations,
                      List<OperationOutcome.Issue> validationIssues)
{
}