package org.cdsframework.ice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.cdsframework.fhir.AdministrativeGender;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Immunization;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.fhir.Patient;
import org.springframework.core.io.ClassPathResource;

final class SeriesOutputTestFixture
{
    private static final String EMPTY_PACKAGE_TYPE = "<packageType/>";

    static Path configurationPath() throws IOException
    {
        final Path source = Path.of(new ClassPathResource("config").getURI());
        final Path fixture = Path.of("target", "series-output-test-config").toAbsolutePath();
        try (final Stream<Path> files = Files.walk(source))
        {
            for (final Path file : files.toList())
            {
                final Path destination = fixture.resolve(source.relativize(file));
                if (Files.isDirectory(file))
                    Files.createDirectories(destination);
                else
                    Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        // The shipped metadata leaves these unused fields empty; assertions require valid test values.
        for (final String metadata : List.of("supportingData/concepts-supportingData.xml", "knowledgeModules.xml"))
        {
            final Path file = fixture.resolve(metadata);
            final String content = Files.readString(file);
            if (!content.contains(EMPTY_PACKAGE_TYPE))
                throw new IllegalStateException("Expected %s in %s".formatted(EMPTY_PACKAGE_TYPE, file));
            Files.writeString(file,
                    content.replace(EMPTY_PACKAGE_TYPE, "<packageType>ICE</packageType><packageId>series-output-test</packageId>"));
        }
        return fixture;
    }

    static Parameters request()
    {
        return Parameters.builder()
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder()
                        .name("knowledgeBase")
                        .valueCanonical("https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .birthDate(LocalDate.parse("1950-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .identifier(Identifier.builder().system("urn:ice:test-patient").value("series-output").build())
                                .build())
                        .build())
                .parameter(immunization("zoster", "187", "2025-09-01"))
                .parameter(immunization("influenza", "140", "2025-10-01"))
                .parameter(immunization("pneumococcal", "133", "2015-01-01"))
                .parameter(immunization("hep-b-1", "08", "2000-01-01"))
                .parameter(immunization("hep-b-2", "08", "2000-02-01"))
                .parameter(immunization("hep-b-3", "08", "2000-07-01"))
                .build();
    }

    private static ParametersParameter immunization(final String id, final String cvx, final String date)
    {
        return ParametersParameter.builder()
                .name("immunization")
                .resource(Immunization.builder().id(id).status(Immunization.Status.COMPLETED).occurrenceDateTime(date)
                        .vaccineCode(CodeableConcept.builder()
                                .coding(Coding.builder().system("http://hl7.org/fhir/sid/cvx").code(cvx).build())
                                .build()).build())
                .build();
    }

    private SeriesOutputTestFixture()
    {
    }
}
