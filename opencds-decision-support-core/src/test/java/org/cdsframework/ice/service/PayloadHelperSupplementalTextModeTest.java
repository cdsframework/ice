package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationReason;
import org.cdsframework.ice.supportingdata.ICESupportingDataConfiguration;
import org.cdsframework.ice.supportingdata.SupplementalReasonSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

class PayloadHelperSupplementalTextModeTest
{
    private record Scenario(SupplementalReasonSupport.SupplementalReasonType type,
                            String reasonCode,
                            String reasonConceptCode,
                            String newCodeSystem,
                            String baseLegacyReasonCode)
    {
    }

    private static Stream<Scenario> scenarios()
    {
        return Stream.of(new Scenario(SupplementalReasonSupport.SupplementalReasonType.EVALUATION,
                        "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT.COVID19_MIN_AGE", "COVID19_MIN_AGE", "2.16.840.1.113883.3.795.12.100.51",
                        BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName()),
                new Scenario(SupplementalReasonSupport.SupplementalReasonType.RECOMMENDATION,
                        "SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT.CUSTOM_REC", "CUSTOM_REC", "2.16.840.1.113883.3.795.12.100.50",
                        BaseDataRecommendationReason._SUPPLEMENTAL_TEXT.getCdsListItemName()));
    }

    @SuppressWarnings("unchecked")
    private static List<CD> invokeOutboundSupplementalTextReason(final Scenario scenario, final String supplementalMessage,
            final CD legacyBaseCD, final IceProperties.SupplementalTextMode mode) throws Exception
    {
        final Schedule schedule = mock(Schedule.class);
        final ICESupportingDataConfiguration supportingDataConfiguration = mock(ICESupportingDataConfiguration.class);
        final SupportedCdsLists supportedCdsLists = mock(SupportedCdsLists.class);
        when(schedule.getSupplementalTextMode()).thenReturn(mode);
        when(schedule.getICESupportingDataConfiguration()).thenReturn(supportingDataConfiguration);
        when(supportingDataConfiguration.getSupportedCdsLists()).thenReturn(supportedCdsLists);

        final LocallyCodedCdsListItem supplementalReason = mock(LocallyCodedCdsListItem.class);
        when(supplementalReason.getSupplementalReasonType()).thenReturn(scenario.type);
        when(supplementalReason.isSupplementalText()).thenReturn(false);
        when(supplementalReason.getCdsListItemValue()).thenReturn(supplementalMessage);

        final CD newModeCD = new CD();
        newModeCD.setCode(scenario.reasonConceptCode);
        newModeCD.setCodeSystem(scenario.newCodeSystem);
        newModeCD.setDisplayName(supplementalMessage);
        when(supplementalReason.getCdsListItemCD()).thenReturn(newModeCD);

        final LocallyCodedCdsListItem legacyBaseReason = mock(LocallyCodedCdsListItem.class);
        when(legacyBaseReason.getCdsListItemCD()).thenReturn(legacyBaseCD);

        when(supportedCdsLists.getCdsListItem(scenario.reasonCode)).thenReturn(supplementalReason);
        when(supportedCdsLists.getCdsListItem(scenario.baseLegacyReasonCode)).thenReturn(legacyBaseReason);

        final Method method =
                PayloadHelper.class.getDeclaredMethod("getOutboundCDForSupplementalTextReason", String.class, Schedule.class);
        method.setAccessible(true);
        return (List<CD>) method.invoke(null, scenario.reasonCode, schedule);
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void returnsLegacyModeSupplementalCodingFromBaseReason(final Scenario scenario) throws Exception
    {
        final String supplementalMessage = "Supplemental reason text";
        final CD legacyBaseCD = new CD();
        legacyBaseCD.setCode(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE);
        legacyBaseCD.setCodeSystem(scenario.type == SupplementalReasonSupport.SupplementalReasonType.EVALUATION
                                   ? "2.16.840.1.113883.3.795.12.100.3"
                                   : "2.16.840.1.113883.3.795.12.100.6");
        legacyBaseCD.setDisplayName(scenario.type == SupplementalReasonSupport.SupplementalReasonType.EVALUATION
                                    ? "Supplemental text is available for this immunization event."
                                    : "Supplemental text is available for this recommendations.");

        final List<CD> result = invokeOutboundSupplementalTextReason(scenario, supplementalMessage, legacyBaseCD,
                IceProperties.SupplementalTextMode.LEGACY);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE, result.getFirst().getCode());
        assertEquals(legacyBaseCD.getCodeSystem(), result.getFirst().getCodeSystem());
        assertEquals(legacyBaseCD.getDisplayName(), result.getFirst().getDisplayName());
        assertEquals(supplementalMessage, result.getFirst().getOriginalText());
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void returnsCodedModeSupplementalCodingFromSupplementalCodeSystem(final Scenario scenario) throws Exception
    {
        final CD legacyBaseCD = new CD();
        legacyBaseCD.setCode(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE);
        legacyBaseCD.setCodeSystem("legacy-system");
        legacyBaseCD.setDisplayName("legacy-display");

        final List<CD> result = invokeOutboundSupplementalTextReason(scenario, "Supplemental reason text", legacyBaseCD,
                IceProperties.SupplementalTextMode.CODED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(scenario.reasonConceptCode, result.getFirst().getCode());
        assertEquals(scenario.newCodeSystem, result.getFirst().getCodeSystem());
        assertNull(result.getFirst().getOriginalText());
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void returnsBothModeLegacyAndCodedSupplementalCoding(final Scenario scenario) throws Exception
    {
        final String supplementalMessage = "Supplemental reason text";
        final CD legacyBaseCD = new CD();
        legacyBaseCD.setCode(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE);
        legacyBaseCD.setCodeSystem(scenario.type == SupplementalReasonSupport.SupplementalReasonType.EVALUATION
                                   ? "2.16.840.1.113883.3.795.12.100.3"
                                   : "2.16.840.1.113883.3.795.12.100.6");
        legacyBaseCD.setDisplayName("legacy-display");

        final List<CD> result = invokeOutboundSupplementalTextReason(scenario, supplementalMessage, legacyBaseCD,
                IceProperties.SupplementalTextMode.BOTH);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE, result.getFirst().getCode());
        assertEquals(legacyBaseCD.getCodeSystem(), result.get(0).getCodeSystem());
        assertEquals(supplementalMessage, result.get(0).getOriginalText());
        assertEquals(scenario.reasonConceptCode, result.get(1).getCode());
        assertEquals(scenario.newCodeSystem, result.get(1).getCodeSystem());
        assertNull(result.get(1).getOriginalText());
    }
}
