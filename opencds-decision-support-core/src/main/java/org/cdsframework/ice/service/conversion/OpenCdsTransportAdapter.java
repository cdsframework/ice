package org.cdsframework.ice.service.conversion;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.InteractionIdentifier;
import org.omg.dss.ItemIdentifier;
import org.omg.dss.KMEvaluationRequest;
import org.omg.dss.SemanticPayload;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenCdsTransportAdapter
{
    private static final String CLIENT_LANGUAGE_CODE = "en";
    private static final String KM_DATA_SUFFIX = "Data";
    private static final String ENTITY_ID_SCOPING_ENTITY_VMR = "org.opencds.vmr";
    private static final String ENTITY_ID_BUSINESS_ID_VMR = "VMR";
    private static final String ENTITY_ID_VERSION_VMR = "1.0";
    private static final String ITEM_ID_CDS_PAYLOAD = "cdsPayload";
    private static final DatatypeFactory DATATYPE_FACTORY = DatatypeFactory.newDefaultInstance();

    public static EvaluateAtSpecifiedTime createEvaluateAtSpecifiedTime(final EntityIdentifier kmEntityIdentifier,
            final LocalDate assessmentDate, final String clientTimeZoneOffset, final byte[] payload)
    {
        return createEvaluateAtSpecifiedTime(
                createInteractionIdentifier(UUID.randomUUID().toString(), toXmlGregorianCalendar(OffsetDateTime.now()),
                        kmEntityIdentifier.getScopingEntityId()),
                toXmlGregorianCalendar(assessmentDate.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()),
                createEvaluationRequest(clientTimeZoneOffset, List.of(createKmEvaluationRequest(kmEntityIdentifier)),
                        List.of(createDataRequirementItemData(createItemIdentifier(
                                createEntityIdentifier(kmEntityIdentifier.getScopingEntityId(),
                                        "%s%s".formatted(kmEntityIdentifier.getBusinessId(), KM_DATA_SUFFIX),
                                        kmEntityIdentifier.getVersion())), createSemanticPayload(
                                createEntityIdentifier(ENTITY_ID_SCOPING_ENTITY_VMR, ENTITY_ID_BUSINESS_ID_VMR,
                                        ENTITY_ID_VERSION_VMR), List.of(payload))))));
    }

    private static EvaluateAtSpecifiedTime createEvaluateAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final EvaluationRequest evaluationRequest)
    {
        final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = new EvaluateAtSpecifiedTime();

        evaluateAtSpecifiedTime.setInteractionId(interactionId);
        evaluateAtSpecifiedTime.setSpecifiedTime(specifiedTime);
        evaluateAtSpecifiedTime.setEvaluationRequest(evaluationRequest);
        return evaluateAtSpecifiedTime;
    }

    private static EvaluationRequest createEvaluationRequest(final String clientTimeZoneOffset,
            final List<KMEvaluationRequest> kmEvaluationRequest, final List<DataRequirementItemData> dataRequirementItemData)
    {
        final EvaluationRequest request = new EvaluationRequest();
        request.setClientLanguage(CLIENT_LANGUAGE_CODE);
        request.setClientTimeZoneOffset(clientTimeZoneOffset);
        if (kmEvaluationRequest != null)
            request.getKmEvaluationRequest().addAll(kmEvaluationRequest);
        if (dataRequirementItemData != null)
            request.getDataRequirementItemData().addAll(dataRequirementItemData);
        return request;
    }

    private static DataRequirementItemData createDataRequirementItemData(final ItemIdentifier driId, final SemanticPayload data)
    {
        final DataRequirementItemData itemData = new DataRequirementItemData();
        itemData.setDriId(driId);
        itemData.setData(data);
        return itemData;
    }

    private static KMEvaluationRequest createKmEvaluationRequest(final EntityIdentifier kmEntityIdentifier)
    {
        final KMEvaluationRequest request = new KMEvaluationRequest();
        request.setKmId(kmEntityIdentifier);
        return request;
    }

    private static SemanticPayload createSemanticPayload(final EntityIdentifier informationModelSSId,
            final List<byte[]> base64EncodedPayload)
    {
        final SemanticPayload payload = new SemanticPayload();
        payload.setInformationModelSSId(informationModelSSId);
        if (base64EncodedPayload != null)
            payload.getBase64EncodedPayload().addAll(base64EncodedPayload);
        return payload;
    }

    private static ItemIdentifier createItemIdentifier(final EntityIdentifier containingEntityId)
    {
        final ItemIdentifier itemIdentifier = new ItemIdentifier();
        itemIdentifier.setContainingEntityId(containingEntityId);
        itemIdentifier.setItemId(ITEM_ID_CDS_PAYLOAD);
        return itemIdentifier;
    }

    private static EntityIdentifier createEntityIdentifier(final String scopingEntityId, final String businessId,
            final String version)
    {
        final EntityIdentifier entityIdentifier = new EntityIdentifier();
        entityIdentifier.setScopingEntityId(scopingEntityId);
        entityIdentifier.setBusinessId(businessId);
        entityIdentifier.setVersion(version);
        return entityIdentifier;
    }

    private static InteractionIdentifier createInteractionIdentifier(final String interactionId,
            final XMLGregorianCalendar submissionTime, final String scopingEntityId)
    {
        final InteractionIdentifier interactionIdentifier = new InteractionIdentifier();
        interactionIdentifier.setScopingEntityId(scopingEntityId);
        interactionIdentifier.setInteractionId(interactionId);
        interactionIdentifier.setSubmissionTime(submissionTime);
        return interactionIdentifier;
    }

    private static XMLGregorianCalendar toXmlGregorianCalendar(final OffsetDateTime dateTime)
    {
        return DATATYPE_FACTORY.newXMLGregorianCalendar(GregorianCalendar.from(dateTime.toZonedDateTime()));
    }
}
