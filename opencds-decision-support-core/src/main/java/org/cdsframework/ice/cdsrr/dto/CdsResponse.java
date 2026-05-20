package org.cdsframework.ice.cdsrr.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record CdsResponse(Patient patient,
                          @Singular
                          List<ImmunizationEvaluation> immunizationEvaluations,
                          @Singular
                          List<ImmunizationRecommendation> immunizationRecommendations,
                          Module module,
                          LocalDate assessmentDate,
                          LocalDateTime responseDateTime,
                          Long responseDuration)
{
}