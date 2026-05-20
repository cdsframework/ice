package org.opencds.vmr.v1_0.mappings.utilities;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.utilities.MiscUtility;
import org.opencds.vmr.v1_0.internal.GoalValue;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.ADXP;
import org.opencds.vmr.v1_0.internal.datatypes.ANY;
import org.opencds.vmr.v1_0.internal.datatypes.AddressPartType;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.ENXP;
import org.opencds.vmr.v1_0.internal.datatypes.EntityNamePartQualifier;
import org.opencds.vmr.v1_0.internal.datatypes.EntityNamePartType;
import org.opencds.vmr.v1_0.internal.datatypes.EntityNameUse;
import org.opencds.vmr.v1_0.internal.datatypes.II;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.IVLINT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;
import org.opencds.vmr.v1_0.internal.datatypes.IVLREAL;
import org.opencds.vmr.v1_0.internal.datatypes.IVLRTO;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.PostalAddressUse;
import org.opencds.vmr.v1_0.internal.datatypes.REAL;
import org.opencds.vmr.v1_0.internal.datatypes.RTO;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;
import org.opencds.vmr.v1_0.internal.datatypes.TelecommunicationAddressUse;
import org.opencds.vmr.v1_0.internal.datatypes.TelecommunicationCapability;
import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class MappingUtility
{
    public static final CD OPENCDS_NO_INFORMATION = setNoInfo();
    private static final DateTimeFormatter DATE_TIME_FORMATTER_OUT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSZ");
    private static final DateTimeFormatter FLEXIBLE_PARSER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4)
            .optionalStart()
            .appendLiteral('-')
            .optionalEnd()
            .optionalStart()
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
            .optionalStart()
            .appendLiteral('-')
            .optionalEnd()
            .optionalStart()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .optionalStart()
            .optionalStart()
            .appendLiteral('T')
            .optionalEnd()
            .optionalStart()
            .appendLiteral(' ')
            .optionalEnd()
            .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
            .optionalStart()
            .appendLiteral(':')
            .optionalEnd()
            .optionalStart()
            .appendValue(ChronoField.MINUTE_OF_HOUR, 1, 2, SignStyle.NOT_NEGATIVE)
            .optionalStart()
            .appendLiteral(':')
            .optionalEnd()
            .optionalStart()
            .appendValue(ChronoField.SECOND_OF_MINUTE, 1, 2, SignStyle.NOT_NEGATIVE)
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
            .optionalEnd()
            .optionalEnd()
            .optionalEnd()
            .optionalEnd()
            .optionalEnd()
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HH:MM", "Z")
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HHMM", "Z")
            .optionalEnd()
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()
            .withResolverStyle(ResolverStyle.SMART);

    private static CD setNoInfo()
    {
        final CD noInfo = new CD();
        noInfo.setCodeSystem("");
        noInfo.setCode("");
        return noInfo;
    }

    public static org.opencds.vmr.v1_0.schema.ANY aNYInternal2ANY(final ANY pANY)
    {
        if (pANY == null)
            return null;

        return new org.opencds.vmr.v1_0.schema.ANY();
    }

    public static GoalValue targetGoalValue2TargetGoalValueInternal(final org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue pANY,
            final Map<String, LocalDate> parsedDatesCache)
    {
        if (pANY == null)
            return null;

        final GoalValue lANY = new GoalValue();

        if (pANY.getBoolean() != null)
            lANY.set_boolean(bL2BLInternal(pANY.getBoolean()));
        else
            if (pANY.getConcept() != null)
                lANY.setConcept(cD2CDInternal(pANY.getConcept()));
            else
                if (pANY.getDecimal() != null)
                    lANY.setDecimal(rEAL2REALInternal(pANY.getDecimal()));
                else
                    if (pANY.getDecimalRange() != null)
                        lANY.setDecimalRange(iVLREAL2IVLREALInternal(pANY.getDecimalRange()));
                    else
                        if (pANY.getInteger() != null)
                            lANY.setInteger(iNT2INTInternal(pANY.getInteger()));
                        else
                            if (pANY.getIntegerRange() != null)
                                lANY.setIntegerRange(iVLINT2IVLINTInternal(pANY.getIntegerRange()));
                            else
                                if (pANY.getPhysicalQuantity() != null)
                                    lANY.setPhysicalQuantity(pQ2PQInternal(pANY.getPhysicalQuantity()));
                                else
                                    if (pANY.getPhysicalQuantityRange() != null)
                                        lANY.setPhysicalQuantityRange(iVLPQ2IVLPQInternal(pANY.getPhysicalQuantityRange()));
                                    else
                                        if (pANY.getRatio() != null)
                                            lANY.setRatio(rTO2RTOInternal(pANY.getRatio()));
                                        else
                                            if (pANY.getRatioRange() != null)
                                                lANY.setRatioRange(iVLRTO2IVLRTOInternal(pANY.getRatioRange()));
                                            else
                                                if (pANY.getSimpleConcept() != null)
                                                    lANY.setSimpleConcept(cS2CSInternal(pANY.getSimpleConcept()));
                                                else
                                                    if (pANY.getText() != null)
                                                        lANY.setText(sT2STInternal(pANY.getText()));
                                                    else
                                                        if (pANY.getTime() != null)
                                                            lANY.setTime(tS2DateInternal(pANY.getTime(), parsedDatesCache));
                                                        else
                                                            if (pANY.getTimeRange() != null)
                                                                lANY.setTimeRange(iVLTS2IVLDateInternal(pANY.getTimeRange(),
                                                                        parsedDatesCache));

        return lANY;
    }

    public static org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue targetGoalValueInternal2targetGoalValue(final GoalValue pANY)
    {
        if (pANY == null)
            return null;

        final org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue lANY =
                new org.opencds.vmr.v1_0.schema.GoalBase.TargetGoalValue();

        if (pANY.get_boolean() != null)
            lANY.setBoolean(bLInternal2BL(pANY.get_boolean()));
        else
            if (pANY.getConcept() != null)
                lANY.setConcept(cDInternal2CD(pANY.getConcept()));
            else
                if (pANY.getDecimal() != null)
                    lANY.setDecimal(rEALInternal2REAL(pANY.getDecimal()));
                else
                    if (pANY.getDecimalRange() != null)
                        lANY.setDecimalRange(iVLREALInternal2IVLREAL(pANY.getDecimalRange()));
                    else
                        if (pANY.getInteger() != null)
                            lANY.setInteger(iNTInternal2INT(pANY.getInteger()));
                        else
                            if (pANY.getIntegerRange() != null)
                                lANY.setIntegerRange(iVLINTInternal2IVLINT(pANY.getIntegerRange()));
                            else
                                if (pANY.getPhysicalQuantity() != null)
                                    lANY.setPhysicalQuantity(pQInternal2PQ(pANY.getPhysicalQuantity()));
                                else
                                    if (pANY.getPhysicalQuantityRange() != null)
                                        lANY.setPhysicalQuantityRange(iVLPQInternal2IVLPQ(pANY.getPhysicalQuantityRange()));
                                    else
                                        if (pANY.getRatio() != null)
                                            lANY.setRatio(rTOInternal2RTO(pANY.getRatio()));
                                        else
                                            if (pANY.getRatioRange() != null)
                                                lANY.setRatioRange(iVLRTOInternal2IVLRTO(pANY.getRatioRange()));
                                            else
                                                if (pANY.getSimpleConcept() != null)
                                                    lANY.setSimpleConcept(cSInternal2CS(pANY.getSimpleConcept()));
                                                else
                                                    if (pANY.getText() != null)
                                                        lANY.setText(sTInternal2ST(pANY.getText()));
                                                    else
                                                        if (pANY.getTime() != null)
                                                            lANY.setTime(dateInternal2TS(pANY.getTime()));
                                                        else
                                                            if (pANY.getTimeRange() != null)
                                                                lANY.setTimeRange(iVLDateInternal2IVLTS(pANY.getTimeRange()));

        return lANY;
    }

    public static ObservationValue observationValue2ObservationValueInternal(
            final org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue pANY,
            final Map<String, LocalDate> parsedDatesCache) throws DataFormatException, InvalidDataException
    {
        if (pANY == null)
            return null;

        final ObservationValue lANY = new ObservationValue();

        if (pANY.getAddress() != null)
            lANY.setAddress(aD2ADInternal(((org.opencds.vmr.v1_0.schema.AD) pANY.getAddress())));
        else
            if (pANY.getBoolean() != null)
                lANY.set_boolean(bL2BLInternal(pANY.getBoolean()));
            else
                if (pANY.getConcept() != null)
                    lANY.setConcept(cD2CDInternal(pANY.getConcept()));
                else
                    if (pANY.getDecimal() != null)
                        lANY.setDecimal(rEAL2REALInternal(pANY.getDecimal()));
                    else
                        if (pANY.getDecimalRange() != null)
                            lANY.setDecimalRange(iVLREAL2IVLREALInternal(pANY.getDecimalRange()));
                        else
                            if (pANY.getIdentifier() != null)
                                lANY.setIdentifier(iI2FlatId(pANY.getIdentifier()));
                            else
                                if (pANY.getInteger() != null)
                                    lANY.setInteger(iNT2INTInternal(pANY.getInteger()));
                                else
                                    if (pANY.getIntegerRange() != null)
                                        lANY.setIntegerRange(iVLINT2IVLINTInternal(pANY.getIntegerRange()));
                                    else
                                        if (pANY.getName() != null)
                                            lANY.setName(eN2ENInternal(((org.opencds.vmr.v1_0.schema.EN) pANY.getName())));
                                        else
                                            if (pANY.getPhysicalQuantity() != null)
                                                lANY.setPhysicalQuantity(pQ2PQInternal(pANY.getPhysicalQuantity()));
                                            else
                                                if (pANY.getPhysicalQuantityRange() != null)
                                                    lANY.setPhysicalQuantityRange(
                                                            iVLPQ2IVLPQInternal(pANY.getPhysicalQuantityRange()));
                                                else
                                                    if (pANY.getRatio() != null)
                                                        lANY.setRatio(rTO2RTOInternal(pANY.getRatio()));
                                                    else
                                                        if (pANY.getRatioRange() != null)
                                                            lANY.setRatioRange(iVLRTO2IVLRTOInternal(pANY.getRatioRange()));
                                                        else
                                                            if (pANY.getSimpleConcept() != null)
                                                                lANY.setSimpleConcept(cS2CSInternal(pANY.getSimpleConcept()));
                                                            else
                                                                if (pANY.getTelecom() != null)
                                                                    lANY.setTelecom(tEL2TELInternal(
                                                                            ((org.opencds.vmr.v1_0.schema.TEL) pANY.getTelecom())));
                                                                else
                                                                    if (pANY.getText() != null)
                                                                        lANY.setText(sT2STInternal(pANY.getText()));
                                                                    else
                                                                        if (pANY.getTime() != null)
                                                                            lANY.setTime(tS2DateInternal(pANY.getTime(),
                                                                                    parsedDatesCache));
                                                                        else
                                                                            if (pANY.getTimeRange() != null)
                                                                                lANY.setTimeRange(
                                                                                        iVLTS2IVLDateInternal(pANY.getTimeRange(),
                                                                                                parsedDatesCache));

        return lANY;
    }

    public static org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue observationValueInternal2ObservationValue(
            final ObservationValue pANY) throws DataFormatException, InvalidDataException
    {
        if (pANY == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue lANY =
                new org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue();

        if (pANY.getAddress() != null)
            lANY.setAddress(aDInternal2AD(((AD) pANY.getAddress())));
        else
            if (pANY.get_boolean() != null)
                lANY.setBoolean(bLInternal2BL(pANY.get_boolean()));
            else
                if (pANY.getConcept() != null)
                    lANY.setConcept(cDInternal2CD(pANY.getConcept()));
                else
                    if (pANY.getDecimal() != null)
                        lANY.setDecimal(rEALInternal2REAL(pANY.getDecimal()));
                    else
                        if (pANY.getDecimalRange() != null)
                            lANY.setDecimalRange(iVLREALInternal2IVLREAL(pANY.getDecimalRange()));
                        else
                            if (pANY.getIdentifier() != null)
                                lANY.setIdentifier(iIFlat2II(pANY.getIdentifier()));
                            else
                                if (pANY.getInteger() != null)
                                    lANY.setInteger(iNTInternal2INT(pANY.getInteger()));
                                else
                                    if (pANY.getIntegerRange() != null)
                                        lANY.setIntegerRange(iVLINTInternal2IVLINT(pANY.getIntegerRange()));
                                    else
                                        if (pANY.getName() != null)
                                            lANY.setName(eNInternal2EN(((EN) pANY.getName())));
                                        else
                                            if (pANY.getPhysicalQuantity() != null)
                                                lANY.setPhysicalQuantity(pQInternal2PQ(pANY.getPhysicalQuantity()));
                                            else
                                                if (pANY.getPhysicalQuantityRange() != null)
                                                    lANY.setPhysicalQuantityRange(
                                                            iVLPQInternal2IVLPQ(pANY.getPhysicalQuantityRange()));
                                                else
                                                    if (pANY.getRatio() != null)
                                                        lANY.setRatio(rTOInternal2RTO(pANY.getRatio()));
                                                    else
                                                        if (pANY.getRatioRange() != null)
                                                            lANY.setRatioRange(iVLRTOInternal2IVLRTO(pANY.getRatioRange()));
                                                        else
                                                            if (pANY.getSimpleConcept() != null)
                                                                lANY.setSimpleConcept(cSInternal2CS(pANY.getSimpleConcept()));
                                                            else
                                                                if (pANY.getTelecom() != null)
                                                                    lANY.setTelecom(tELInternal2TEL(((TEL) pANY.getTelecom())));
                                                                else
                                                                    if (pANY.getText() != null)
                                                                        lANY.setText(sTInternal2ST(pANY.getText()));
                                                                    else
                                                                        if (pANY.getTime() != null)
                                                                            lANY.setTime(dateInternal2TS(pANY.getTime()));
                                                                        else
                                                                            if (pANY.getTimeRange() != null)
                                                                                lANY.setTimeRange(
                                                                                        iVLDateInternal2IVLTS(pANY.getTimeRange()));

        return lANY;
    }

    public static org.opencds.vmr.v1_0.schema.BL bLInternal2BL(final BL pBL)
    {
        if (pBL == null)
            return null;

        final org.opencds.vmr.v1_0.schema.BL lBL = new org.opencds.vmr.v1_0.schema.BL();
        lBL.setValue(pBL.isValue());
        return lBL;
    }

    public static BL bL2BLInternal(final org.opencds.vmr.v1_0.schema.BL pBL)
    {
        if (pBL == null)
            return null;

        final BL lBL = new BL();
        lBL.setValue(pBL.isValue());
        return lBL;
    }

    public static org.opencds.vmr.v1_0.schema.CD cDInternal2CD(final CD cdInternal)
    {
        if ((cdInternal == null) || (((cdInternal.getCode() == null) || (cdInternal.getCode().isEmpty())) && (
                (cdInternal.getOriginalText() == null) || (cdInternal.getOriginalText().isEmpty())) && (
                (cdInternal.getCodeSystem() == null) || (cdInternal.getCodeSystem().isEmpty()))))
        {
            return null;
        }

        final org.opencds.vmr.v1_0.schema.CD cd = new org.opencds.vmr.v1_0.schema.CD();
        try
        {
            if ((((cdInternal.getCode() == null) || (cdInternal.getCode().isEmpty())) && ((cdInternal.getOriginalText() == null)
                    || (cdInternal.getOriginalText().isEmpty()) || ((cdInternal.getCodeSystem() == null)
                    || (cdInternal.getCodeSystem().isEmpty())))))
            {
                throw new RuntimeException(
                        "CDInternal2CD(code=%s, codeSystem=%s, %s, %s, originalText=%s) must have both codeSystem and (code OR originalText).".formatted(
                                cdInternal.getCode(), cdInternal.getCodeSystem(), cdInternal.getDisplayName(),
                                cdInternal.getCodeSystemName(), cdInternal.getOriginalText()));
            }

            if (cdInternal.getCode() != null)
                cd.setCode(cdInternal.getCode());
            if (cdInternal.getCodeSystem() != null)
                cd.setCodeSystem(cdInternal.getCodeSystem());
            if (cdInternal.getCodeSystemName() != null)
                cd.setCodeSystemName(cdInternal.getCodeSystemName());
            if ((cdInternal.getDisplayName() != null) && (!cdInternal.getDisplayName().isEmpty()))
                cd.setDisplayName(cdInternal.getDisplayName());
            if ((cdInternal.getOriginalText() != null) && (!cdInternal.getOriginalText().isEmpty()))
                cd.setOriginalText(cdInternal.getOriginalText());
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("cDInternal2CD(%s) had errors: %s".formatted(cdInternal, e.getMessage()));
        }

        return cd;
    }

    public static CD cD2CDInternal(final org.opencds.vmr.v1_0.schema.CD cdSchema)
    {
        if ((cdSchema == null) || (((cdSchema.getCode() == null) || ("".equals(cdSchema.getCode()))) && (
                (cdSchema.getOriginalText() == null) || ("".equals(cdSchema.getOriginalText()))) && (
                (cdSchema.getCodeSystem() == null) || ("".equals(cdSchema.getCodeSystem())))))
        {
            return OPENCDS_NO_INFORMATION;
        }

        final CD cd = new CD();
        try
        {
            if ((((cdSchema.getCode() == null) || ("".equals(cdSchema.getCode()))) && ((cdSchema.getOriginalText() == null)
                    || ("".equals(cdSchema.getOriginalText())))) || ((cdSchema.getCodeSystem() == null) || ("".equals(
                    cdSchema.getCodeSystem()))))
            {
                throw new RuntimeException(
                        "cD2CDInternal(%s,%s,%s,%s,%s) both codeSystem and (code OR originalText) must have a value.".formatted(
                                cdSchema.getCode(), cdSchema.getCodeSystem(), cdSchema.getDisplayName(),
                                cdSchema.getCodeSystemName(), cdSchema.getOriginalText()));
            }

            if (cdSchema.getCode() != null)
                cd.setCode(cdSchema.getCode());
            if (cdSchema.getCodeSystem() != null)
                cd.setCodeSystem(cdSchema.getCodeSystem());
            if (cdSchema.getDisplayName() != null)
                cd.setDisplayName(cdSchema.getDisplayName());
            if (cdSchema.getCodeSystemName() != null)
                cd.setCodeSystemName(cdSchema.getCodeSystemName());
            if (cdSchema.getOriginalText() != null)
                cd.setOriginalText(cdSchema.getOriginalText());
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("cD2CDInternal(%s) had errors: %s".formatted(cdSchema, e.getMessage()));
        }

        return cd;
    }

    public static org.opencds.vmr.v1_0.schema.CS cSInternal2CS(final String code)
    {
        final org.opencds.vmr.v1_0.schema.CS cs = new org.opencds.vmr.v1_0.schema.CS();
        try
        {
            cs.setCode(code);
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("cSInternal2CS(%s) had errors: %s".formatted(code, e.getMessage()));
        }

        return cs;
    }

    public static String cS2CSInternal(final org.opencds.vmr.v1_0.schema.CS cs)
    {
        try
        {
            return cs.getCode();
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("cS2Code(%s) had errors: %s".formatted(cs.toString(), e.getMessage()));
        }
    }

    public static String iI2Root(final String context, final org.opencds.vmr.v1_0.schema.II ii)
    {
        try
        {
            if ((ii == null) || (ii.getRoot() == null))
            {
                throw new DataFormatException(
                        "%s context: iI2Root(%s) had errors: TemplateID must have GUID or OID as root".formatted(context, ii));
            }

            return ii.getRoot();
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("iI2Root(" + ii + ") had errors: " + e.getMessage());
        }
    }

    public static List<org.opencds.vmr.v1_0.schema.II> iIFlatList2IIList(final String[] rootCaratExtensionList)
    {
        return Arrays.stream(rootCaratExtensionList).map(MappingUtility::iIFlat2II).toList();
    }

    public static org.opencds.vmr.v1_0.schema.II iIFlat2II(final String rootCaratExtension)
    {
        if (ObjectUtils.isEmpty(rootCaratExtension))
            return null;

        final int positionCarat = rootCaratExtension.indexOf("^");
        final String root;
        String extension = null;
        if (positionCarat < 0)
            root = rootCaratExtension;
        else
        {
            root = rootCaratExtension.substring(0, positionCarat);
            extension = rootCaratExtension.substring(positionCarat + 1);
        }

        final org.opencds.vmr.v1_0.schema.II ii = new org.opencds.vmr.v1_0.schema.II();
        try
        {
            ii.setRoot(root);
            ii.setExtension(extension);
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("iIFlat2II(%s) had errors: %s".formatted(rootCaratExtension, e.getMessage()));
        }

        return ii;
    }

    public static String[] iIList2FlatIdList(final List<org.opencds.vmr.v1_0.schema.II> iiList)
    {
        if (iiList == null)
            return null;

        return iiList.stream().map(MappingUtility::iI2FlatId).toArray(String[]::new);
    }

    public static String iI2FlatId(final org.opencds.vmr.v1_0.schema.II ii)
    {
        String iD;
        try
        {
            iD = ii.getRoot();
            if ((ii.getExtension() != null) && (!"".equals(ii.getExtension())))
                iD += "^" + ii.getExtension();
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("iI2FlatId(%s) had errors: %s".formatted(ii.toString(), e.getMessage()));
        }

        return iD;
    }

    public static INT iNT2INTInternal(final org.opencds.vmr.v1_0.schema.INT pINT)
    {
        if (pINT == null)
            return null;

        final INT lINT = new INT();
        lINT.setValue(pINT.getValue());
        return lINT;
    }

    public static org.opencds.vmr.v1_0.schema.INT iNTInternal2INT(final INT pINT)
    {
        if (pINT == null)
            return null;

        final org.opencds.vmr.v1_0.schema.INT lINT = new org.opencds.vmr.v1_0.schema.INT();
        lINT.setValue(pINT.getValue());
        return lINT;
    }

    public static org.opencds.vmr.v1_0.schema.PQ pQInternal2PQ(final PQ pqInternal)
    {
        if (pqInternal == null)
            return null;

        final org.opencds.vmr.v1_0.schema.PQ pq = new org.opencds.vmr.v1_0.schema.PQ();
        try
        {
            if ((pqInternal.getUnit() == null) || (pqInternal.getUnit().isEmpty()))
                throw new RuntimeException("pQInternal2PQ( %s ) must have a valid unit.".formatted(pqInternal));

            pq.setValue(pqInternal.getValue());
            pq.setUnit(pqInternal.getUnit());

        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("pQInternal2PQ( %s ) had errors: %s".formatted(pqInternal, e.getMessage()));
        }

        return pq;
    }

    public static PQ pQ2PQInternal(final org.opencds.vmr.v1_0.schema.PQ pqSchema)
    {
        if (pqSchema == null)
            return null;

        final PQ pq = new PQ();
        try
        {
            if ((pqSchema.getUnit() == null) || ("".equals(pqSchema.getUnit())))
                throw new RuntimeException("pQ2PQInternal( %s ) must have a valid unit.".formatted(pqSchema));

            pq.setValue(pqSchema.getValue());
            pq.setUnit(pqSchema.getUnit());

        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException("pQ2PQInternal( %s ) had errors: %s".formatted(pqSchema, e.getMessage()));
        }

        return pq;
    }

    public static REAL rEAL2REALInternal(final org.opencds.vmr.v1_0.schema.REAL pREAL)
    {
        if (pREAL == null)
            return null;

        final REAL lREAL = new REAL();
        lREAL.setValue(pREAL.getValue());
        return lREAL;
    }

    public static org.opencds.vmr.v1_0.schema.REAL rEALInternal2REAL(final REAL pREAL)
    {
        if (pREAL == null)
            return null;

        final org.opencds.vmr.v1_0.schema.REAL lREAL = new org.opencds.vmr.v1_0.schema.REAL();
        lREAL.setValue(pREAL.getValue());
        return lREAL;
    }

    public static RTO rTO2RTOInternal(final org.opencds.vmr.v1_0.schema.RTO pRTO)
    {
        if (pRTO == null)
            return null;

        final RTO lRTO = new RTO();
        lRTO.setNumerator(pRTO.getNumerator());
        lRTO.setDenominator(pRTO.getDenominator());
        return lRTO;
    }

    public static org.opencds.vmr.v1_0.schema.RTO rTOInternal2RTO(final RTO pRTO)
    {
        if (pRTO == null)
            return null;

        final org.opencds.vmr.v1_0.schema.RTO lRTO = new org.opencds.vmr.v1_0.schema.RTO();
        lRTO.setNumerator(pRTO.getNumerator());
        lRTO.setDenominator(pRTO.getDenominator());
        return lRTO;
    }

    private static LocalDate getDateFromString(final String dateAsString)
    {
        try
        {
            return LocalDate.parse(dateAsString, DateTimeFormatter.BASIC_ISO_DATE);
        }
        catch (final DateTimeException ignored)
        {
        }

        try
        {
            return LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        catch (final DateTimeException ignored)
        {
        }

        try
        {
            return LocalDate.from(
                    FLEXIBLE_PARSER.parseBest(dateAsString, OffsetDateTime::from, LocalDateTime::from, LocalDate::from));
        }
        catch (final DateTimeException ignored)
        {
        }

        log.error("Could not parse date: {}", dateAsString);

        throw new RuntimeException("Could not parse date: " + dateAsString);
    }

    private static String getDateAsString(final LocalDate date)
    {
        try
        {
            return DATE_TIME_FORMATTER_OUT.format(date.atStartOfDay(ZoneId.systemDefault()));
        }
        catch (final DateTimeException e)
        {
            throw new RuntimeException("Could not format date: " + date, e);
        }
    }

    public static LocalDate tS2DateInternal(final org.opencds.vmr.v1_0.schema.TS pTS, final Map<String, LocalDate> parsedDatesCache)
    {
        final String _METHODNAME = "tS2TSInternal(): ";

        if ((pTS == null) || ("".equals(pTS.getValue())) || ("null".equalsIgnoreCase(pTS.getValue())))
            return null;

        final String hl7Time = pTS.getValue();
        if (hl7Time == null)
        {
            final String errStr = _METHODNAME + "TS.getValue() is null";
            log.error(errStr);
            throw new RuntimeException(errStr);
        }

        return parsedDatesCache.computeIfAbsent(hl7Time, MappingUtility::getDateFromString);
    }

    public static org.opencds.vmr.v1_0.schema.TS dateInternal2TS(final LocalDate pDate)
    {
        if (pDate == null)
            return null;

        final org.opencds.vmr.v1_0.schema.TS lTS = new org.opencds.vmr.v1_0.schema.TS();
        lTS.setValue(getDateAsString(pDate));
        return lTS;
    }

    public static org.opencds.vmr.v1_0.schema.ST sTInternal2ST(final String pST)
    {
        if (pST == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ST lST = new org.opencds.vmr.v1_0.schema.ST();
        lST.setValue(pST);
        return lST;
    }

    public static String sT2STInternal(final org.opencds.vmr.v1_0.schema.ST pST)
    {
        if (pST == null)
            return null;

        final String lST;
        lST = pST.getValue();
        return lST;
    }

    public static IVLINT iVLINT2IVLINTInternal(final org.opencds.vmr.v1_0.schema.IVLINT pIVLINT)
    {
        if (pIVLINT == null)
            return null;

        final IVLINT lIVLINT = new IVLINT();
        if (pIVLINT.isLowIsInclusive() != null)
            lIVLINT.setLowIsInclusive(pIVLINT.isLowIsInclusive());
        if (pIVLINT.isHighIsInclusive() != null)
            lIVLINT.setHighIsInclusive(pIVLINT.isHighIsInclusive());
        lIVLINT.setLow(pIVLINT.getLow());
        lIVLINT.setHigh(pIVLINT.getHigh());
        return lIVLINT;
    }

    public static org.opencds.vmr.v1_0.schema.IVLINT iVLINTInternal2IVLINT(final IVLINT pIVLINT)
    {
        if (pIVLINT == null)
            return null;

        final org.opencds.vmr.v1_0.schema.IVLINT lIVLINT = new org.opencds.vmr.v1_0.schema.IVLINT();
        if (pIVLINT.isLowIsInclusive() != null)
            lIVLINT.setLowIsInclusive(pIVLINT.isLowIsInclusive());
        if (pIVLINT.isHighIsInclusive() != null)
            lIVLINT.setHighIsInclusive(pIVLINT.isHighIsInclusive());
        lIVLINT.setLow(pIVLINT.getLow());
        lIVLINT.setHigh(pIVLINT.getHigh());
        return lIVLINT;
    }

    public static IVLPQ iVLPQ2IVLPQInternal(final org.opencds.vmr.v1_0.schema.IVLPQ pIVLPQ)
    {
        if (pIVLPQ == null)
            return null;

        final IVLPQ lIVLPQ = new IVLPQ();
        lIVLPQ.setLowUnit(pIVLPQ.getLowUnit());
        lIVLPQ.setLowValue(pIVLPQ.getLowValue());
        lIVLPQ.setHighUnit(pIVLPQ.getHighUnit());
        lIVLPQ.setHighValue(pIVLPQ.getHighValue());
        if (pIVLPQ.isLowIsInclusive() != null)
            lIVLPQ.setLowIsInclusive(pIVLPQ.isLowIsInclusive());
        if (pIVLPQ.isHighIsInclusive() != null)
            lIVLPQ.setHighIsInclusive(pIVLPQ.isHighIsInclusive());

        return lIVLPQ;
    }

    public static org.opencds.vmr.v1_0.schema.IVLPQ iVLPQInternal2IVLPQ(final IVLPQ pIVLPQ)
    {
        if (pIVLPQ == null)
            return null;

        final org.opencds.vmr.v1_0.schema.IVLPQ lIVLPQ = new org.opencds.vmr.v1_0.schema.IVLPQ();
        lIVLPQ.setLowUnit(pIVLPQ.getLowUnit());
        lIVLPQ.setLowValue(pIVLPQ.getLowValue());
        lIVLPQ.setHighUnit(pIVLPQ.getHighUnit());
        lIVLPQ.setHighValue(pIVLPQ.getHighValue());
        if (pIVLPQ.getLowIsInclusive() != null)
            lIVLPQ.setLowIsInclusive(pIVLPQ.getLowIsInclusive());
        if (pIVLPQ.getHighIsInclusive() != null)
            lIVLPQ.setHighIsInclusive(pIVLPQ.getHighIsInclusive());

        return lIVLPQ;
    }

    public static IVLDate iVLTS2IVLDateInternal(final org.opencds.vmr.v1_0.schema.IVLTS pIVLTS,
            final Map<String, LocalDate> parsedDatesCache)
    {
        if (pIVLTS == null)
            return null;

        final IVLDate lIVLDate = new IVLDate();

        if ((pIVLTS.getLow() != null) && (!"null".equals(pIVLTS.getLow())) && (!"".equals(pIVLTS.getLow())))
        {
            final org.opencds.vmr.v1_0.schema.TS low = new org.opencds.vmr.v1_0.schema.TS();
            low.setValue(pIVLTS.getLow());
            lIVLDate.setLow(tS2DateInternal(low, parsedDatesCache));
        }

        if ((pIVLTS.getHigh() != null) && (!"null".equals(pIVLTS.getHigh())) && (!"".equals(pIVLTS.getHigh())))
        {
            final org.opencds.vmr.v1_0.schema.TS high = new org.opencds.vmr.v1_0.schema.TS();
            high.setValue(pIVLTS.getHigh());
            lIVLDate.setHigh(tS2DateInternal(high, parsedDatesCache));
        }

        if (pIVLTS.isLowIsInclusive() != null)
            lIVLDate.setLowIsInclusive(pIVLTS.isLowIsInclusive());
        if (pIVLTS.isHighIsInclusive() != null)
            lIVLDate.setHighIsInclusive(pIVLTS.isHighIsInclusive());

        return lIVLDate;
    }

    public static org.opencds.vmr.v1_0.schema.IVLTS iVLDateInternal2IVLTS(final IVLDate pIVLDate)
    {
        if (pIVLDate == null)
            return null;

        final org.opencds.vmr.v1_0.schema.IVLTS lIVLTS = new org.opencds.vmr.v1_0.schema.IVLTS();

        if (pIVLDate.getLow() != null)
        {
            final LocalDate low = pIVLDate.getLow();
            final org.opencds.vmr.v1_0.schema.TS lowExternal = dateInternal2TS(low);
            lIVLTS.setLow(lowExternal.getValue());
        }

        if (pIVLDate.getHigh() != null)
        {
            final LocalDate high = pIVLDate.getHigh();
            final org.opencds.vmr.v1_0.schema.TS highExternal = dateInternal2TS(high);
            lIVLTS.setHigh(highExternal.getValue());
        }

        if (pIVLDate.isLowIsInclusive() != null)
            lIVLTS.setLowIsInclusive(pIVLDate.isLowIsInclusive());
        if (pIVLDate.isHighIsInclusive() != null)
            lIVLTS.setHighIsInclusive(pIVLDate.isHighIsInclusive());

        return lIVLTS;
    }

    public static IVLREAL iVLREAL2IVLREALInternal(final org.opencds.vmr.v1_0.schema.IVLREAL pIVLREAL)
    {
        if (pIVLREAL == null)
            return null;

        final IVLREAL lIVLREAL = new IVLREAL();
        if (pIVLREAL.isLowIsInclusive() != null)
            lIVLREAL.setLowIsInclusive(pIVLREAL.isLowIsInclusive());
        if (pIVLREAL.isHighIsInclusive() != null)
            lIVLREAL.setHighIsInclusive(pIVLREAL.isHighIsInclusive());
        lIVLREAL.setLow(pIVLREAL.getLow());
        lIVLREAL.setHigh(pIVLREAL.getHigh());
        return lIVLREAL;
    }

    public static org.opencds.vmr.v1_0.schema.IVLREAL iVLREALInternal2IVLREAL(final IVLREAL ivlreal)
    {
        if (ivlreal == null)
            return null;

        final org.opencds.vmr.v1_0.schema.IVLREAL lIVLREAL = new org.opencds.vmr.v1_0.schema.IVLREAL();
        if (ivlreal.isLowIsInclusive() != null)
            lIVLREAL.setLowIsInclusive(ivlreal.isLowIsInclusive());
        if (ivlreal.isHighIsInclusive() != null)
            lIVLREAL.setHighIsInclusive(ivlreal.isHighIsInclusive());
        lIVLREAL.setLow(ivlreal.getLow());
        lIVLREAL.setHigh(ivlreal.getHigh());
        return lIVLREAL;
    }

    public static IVLRTO iVLRTO2IVLRTOInternal(final org.opencds.vmr.v1_0.schema.IVLRTO pIVLRTO)
    {
        if (pIVLRTO == null)
            return null;

        final IVLRTO lIVLRTO = new IVLRTO();
        if (pIVLRTO.isLowIsInclusive() != null)
            lIVLRTO.setLowIsInclusive(pIVLRTO.isLowIsInclusive());
        if (pIVLRTO.isHighIsInclusive() != null)
            lIVLRTO.setHighIsInclusive(pIVLRTO.isHighIsInclusive());
        lIVLRTO.setLowDenominator(pIVLRTO.getLowDenominator());
        lIVLRTO.setLowNumerator(pIVLRTO.getLowNumerator());
        lIVLRTO.setHighDenominator(pIVLRTO.getHighDenominator());
        lIVLRTO.setHighNumerator(pIVLRTO.getHighNumerator());
        return lIVLRTO;
    }

    public static org.opencds.vmr.v1_0.schema.IVLRTO iVLRTOInternal2IVLRTO(final IVLRTO pIVLRTO)
    {
        if (pIVLRTO == null)
            return null;

        final org.opencds.vmr.v1_0.schema.IVLRTO lIVLRTO = new org.opencds.vmr.v1_0.schema.IVLRTO();
        if (pIVLRTO.getLowIsInclusive() != null)
            lIVLRTO.setLowIsInclusive(pIVLRTO.getLowIsInclusive());
        if (pIVLRTO.getHighIsInclusive() != null)
            lIVLRTO.setHighIsInclusive(pIVLRTO.getHighIsInclusive());
        lIVLRTO.setLowDenominator(pIVLRTO.getLowDenominator());
        lIVLRTO.setLowNumerator(pIVLRTO.getLowNumerator());
        lIVLRTO.setHighDenominator(pIVLRTO.getHighDenominator());
        lIVLRTO.setHighNumerator(pIVLRTO.getHighNumerator());
        return lIVLRTO;
    }

    public static org.opencds.vmr.v1_0.schema.EN eNInternal2EN(final EN pENInt) throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "eNInternal2EN(): ";

        if (pENInt == null)
            return null;

        final String errStr;
        final org.opencds.vmr.v1_0.schema.EN lENExt = new org.opencds.vmr.v1_0.schema.EN();

        final List<ENXP> lIntEntityPart = pENInt.getPart();
        if (lIntEntityPart == null || lIntEntityPart.isEmpty())
        {
            errStr = _METHODNAME + "List<ENXP> element of internal EN datatype not populated - required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new DataFormatException(errStr);
        }
        final Iterator<ENXP> lIntEntityPartIter = lIntEntityPart.iterator();
        int count = 0;
        while (lIntEntityPartIter.hasNext())
        {
            final ENXP lENXPInt = lIntEntityPartIter.next();
            final org.opencds.vmr.v1_0.schema.ENXP lENXPExp = eNXPInternal2ENXP(lENXPInt);
            lENExt.getPart().add(lENXPExp);
            count++;
        }
        if (count < 1)
        {
            errStr = _METHODNAME
                    + "No int->ext translations of List<ENXP> successful - at least one member of List<ENXP> required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new InvalidDataException(errStr);
        }

        final List<EntityNameUse> lEntityNameUseListInt = pENInt.getUse();
        if (!ObjectUtils.isEmpty(lEntityNameUseListInt))
            lENExt.getUse().add(eNNameUseInternal2ENNameUse(lEntityNameUseListInt.getFirst()));

        return lENExt;
    }

    private static org.opencds.vmr.v1_0.schema.EntityNameUse eNNameUseInternal2ENNameUse(final EntityNameUse pENU)
            throws InvalidDataException
    {
        final String _METHODNAME = "eNNameUseInternal2ENNameUse() :";

        if (pENU == null)
            return null;

        final String lEntityNameUseStrInt = pENU.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal EntityNameUse value: {}", lEntityNameUseStrInt);
        final org.opencds.vmr.v1_0.schema.EntityNameUse lEntityNameUseExt;
        try
        {
            lEntityNameUseExt = org.opencds.vmr.v1_0.schema.EntityNameUse.valueOf(lEntityNameUseStrInt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        return lEntityNameUseExt;
    }

    private static org.opencds.vmr.v1_0.schema.ENXP eNXPInternal2ENXP(final ENXP pENXP)
            throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "eNXPInternal2ENXP(): ";
        if (pENXP == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ENXP lENXPExt = new org.opencds.vmr.v1_0.schema.ENXP();

        lENXPExt.setValue(pENXP.getValue());

        final EntityNamePartType lEntityNamePartTypeInt = pENXP.getType();
        if (lEntityNamePartTypeInt == null)
            throw new DataFormatException(
                    _METHODNAME + "EntityPartType of external ENXP datatype not populated; required by vmr spec");

        final String lEntityNamePartTypeStrInt = lEntityNamePartTypeInt.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal EntityNamePartType value: {}", lEntityNamePartTypeStrInt);

        final org.opencds.vmr.v1_0.schema.EntityNamePartType lEntityNamePartTypeExt;
        try
        {
            lEntityNamePartTypeExt = org.opencds.vmr.v1_0.schema.EntityNamePartType.valueOf(lEntityNamePartTypeStrInt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External EntityNamePartType value: {}", lEntityNamePartTypeExt);

        lENXPExt.setType(lEntityNamePartTypeExt);

        final List<EntityNamePartQualifier> lPartQualifierListInt = pENXP.getQualifier();
        if (lPartQualifierListInt != null)
        {
            for (final EntityNamePartQualifier lPartQualifierInt : lPartQualifierListInt)
                lENXPExt.getQualifier().add(eNPartQualifierInternal2ENPartQualifier(lPartQualifierInt));
        }

        return lENXPExt;
    }

    private static org.opencds.vmr.v1_0.schema.EntityNamePartQualifier eNPartQualifierInternal2ENPartQualifier(
            final EntityNamePartQualifier pENPQInt) throws InvalidDataException
    {
        final String _METHODNAME = "eNPartQualifierInternal2ENPartQualifier() :";

        if (pENPQInt == null)
            return null;

        final String lPartQualifierStrInt = pENPQInt.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal EntityNamePartQualifier value: {}", lPartQualifierStrInt);

        final org.opencds.vmr.v1_0.schema.EntityNamePartQualifier lPartQualifierInt;
        try
        {
            lPartQualifierInt = org.opencds.vmr.v1_0.schema.EntityNamePartQualifier.valueOf(lPartQualifierStrInt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        return lPartQualifierInt;
    }

    public static EN eN2ENInternal(final org.opencds.vmr.v1_0.schema.EN pENExt) throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "eN2ENInternal(): ";

        if (pENExt == null)
            return null;

        final String errStr;
        final EN lENInt = new EN();

        final List<org.opencds.vmr.v1_0.schema.ENXP> lExtEntityPart = pENExt.getPart();
        if (lExtEntityPart == null || lExtEntityPart.isEmpty())
        {
            errStr = _METHODNAME + "List<ENXP element of EN datatype not populated - required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new DataFormatException(errStr);
        }
        final Iterator<org.opencds.vmr.v1_0.schema.ENXP> lExtEntityPartIter = lExtEntityPart.iterator();
        int count = 0;
        while (lExtEntityPartIter.hasNext())
        {
            final org.opencds.vmr.v1_0.schema.ENXP lENXPExt = lExtEntityPartIter.next();
            final ENXP lENXPInt = eNXP2ENXPInternal(lENXPExt);
            lENInt.getPart().add(lENXPInt);
            count++;
        }
        if (count < 1)
        {
            errStr = _METHODNAME
                    + "No ext->int translations of List<ENXP> successful - at least one member of List<ENXP> required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new InvalidDataException(errStr);
        }

        final List<org.opencds.vmr.v1_0.schema.EntityNameUse> lEntityNameUseListExt = pENExt.getUse();
        if (lEntityNameUseListExt != null && !lEntityNameUseListExt.isEmpty())
            lENInt.getUse().add(eNNameUse2eNNameUseInternal(lEntityNameUseListExt.getFirst()));

        return lENInt;
    }

    private static EntityNameUse eNNameUse2eNNameUseInternal(final org.opencds.vmr.v1_0.schema.EntityNameUse pENU)
            throws InvalidDataException
    {
        final String _METHODNAME = "eNNameUse2eNNameUseInternal() :";

        if (pENU == null)
            return null;

        final String lEntityNameUseStrExt = pENU.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External EntityNameUse value: {}", lEntityNameUseStrExt);

        final EntityNameUse lEntityNameUseInt;
        try
        {
            lEntityNameUseInt = EntityNameUse.valueOf(lEntityNameUseStrExt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
        }

        return lEntityNameUseInt;
    }

    private static ENXP eNXP2ENXPInternal(final org.opencds.vmr.v1_0.schema.ENXP pENXP) throws InvalidDataException
    {
        final String _METHODNAME = "eNXP2ENXPInternal(): ";
        if (pENXP == null)
            return null;

        final ENXP lENXPInt = new ENXP();

        lENXPInt.setValue(pENXP.getValue());

        final org.opencds.vmr.v1_0.schema.EntityNamePartType lEntityNamePartTypeExt = pENXP.getType();
        if (lEntityNamePartTypeExt == null)
            log.warn(_METHODNAME + "EntityPartType of external ENXP datatype not populated; required by vmr spec");
        else
        {
            final String lEntityNamePartTypeStrExt = lEntityNamePartTypeExt.toString();
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "External EntityNamePartType value: {}", lEntityNamePartTypeStrExt);

            final EntityNamePartType lEntityNamePartTypeInt;
            try
            {
                lEntityNamePartTypeInt = EntityNamePartType.valueOf(lEntityNamePartTypeStrExt);
            }
            catch (final IllegalArgumentException iae)
            {
                throw new InvalidDataException(
                        _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
            }

            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Internal EntityNamePartType value: {}", lEntityNamePartTypeInt);

            lENXPInt.setType(lEntityNamePartTypeInt);
        }

        final List<org.opencds.vmr.v1_0.schema.EntityNamePartQualifier> lPartQualifierListExt = pENXP.getQualifier();
        if (lPartQualifierListExt != null)
        {
            for (final org.opencds.vmr.v1_0.schema.EntityNamePartQualifier lPartQualifierExt : lPartQualifierListExt)
                lENXPInt.getQualifier().add(eNPartQualifier2eNPartQualifierInternal(lPartQualifierExt));
        }

        return lENXPInt;
    }

    private static EntityNamePartQualifier eNPartQualifier2eNPartQualifierInternal(
            final org.opencds.vmr.v1_0.schema.EntityNamePartQualifier pENPQExt) throws InvalidDataException
    {
        final String _METHODNAME = "eNXPPartQualifier2eNXPPartQualifierInternal() :";

        if (pENPQExt == null)
            return null;

        final String lPartQualifierStrInt = pENPQExt.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External EntityNamePartQualifier value: {}", lPartQualifierStrInt);

        final EntityNamePartQualifier lPartQualifierInt;
        try
        {
            lPartQualifierInt = EntityNamePartQualifier.valueOf(lPartQualifierStrInt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
        }

        return lPartQualifierInt;
    }

    public static TEL tEL2TELInternal(final org.opencds.vmr.v1_0.schema.TEL pTELExt) throws InvalidDataException
    {
        if (pTELExt == null)
            return null;

        final TEL lTELInt = new TEL();

        if (pTELExt.getUseablePeriodOriginalText() != null)
            lTELInt.setUseablePeriodOriginalText(pTELExt.getUseablePeriodOriginalText());
        if (pTELExt.getValue() != null)
            lTELInt.setValue(pTELExt.getValue());

        final List<org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse> lTelAddrListExt = pTELExt.getUse();
        if (lTelAddrListExt != null)
        {
            for (final org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse lTelAddrExt : lTelAddrListExt)
                lTELInt.getUse().add(tELTelecomAddrUse2TELTelecomAddrUseInternal(lTelAddrExt));
        }

        final List<org.opencds.vmr.v1_0.schema.TelecommunicationCapability> lTelecomCapaListExt = pTELExt.getCapabilities();
        if (lTelecomCapaListExt != null)
        {
            for (final org.opencds.vmr.v1_0.schema.TelecommunicationCapability lTelecomCapExt : lTelecomCapaListExt)
                lTELInt.getCapabilities().add(tELTelecomCapa2TELTelecomCapaInternal(lTelecomCapExt));
        }

        return lTELInt;
    }

    public static org.opencds.vmr.v1_0.schema.TEL tELInternal2TEL(final TEL pTELInt) throws InvalidDataException
    {
        if (pTELInt == null)
            return null;

        final org.opencds.vmr.v1_0.schema.TEL lTELExt = new org.opencds.vmr.v1_0.schema.TEL();

        lTELExt.setUseablePeriodOriginalText(pTELInt.getUseablePeriodOriginalText());
        if (pTELInt.getValue() != null)
            lTELExt.setValue(pTELInt.getValue());

        final List<TelecommunicationAddressUse> lTelAddrListInt = pTELInt.getUse();
        if (lTelAddrListInt != null)
        {
            for (final TelecommunicationAddressUse lTelAddrInt : lTelAddrListInt)
                lTELExt.getUse().add(tELTelecomAddrUseInternal2TELTelecomAddrUse(lTelAddrInt));
        }

        final List<TelecommunicationCapability> lTelecomCapaListInt = pTELInt.getCapabilities();
        if (lTelecomCapaListInt != null)
        {
            for (final TelecommunicationCapability lTelecomCapInt : lTelecomCapaListInt)
                lTELExt.getCapabilities().add(tELTelecomCapaInternal2TelecomCapa(lTelecomCapInt));
        }

        return lTELExt;
    }

    private static TelecommunicationAddressUse tELTelecomAddrUse2TELTelecomAddrUseInternal(
            final org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse pTC) throws InvalidDataException
    {
        final String _METHODNAME = "tELTelecomAddrUse2TELTelecomAddrUseInternal() :";

        if (pTC == null)
            return null;

        final String lTCStrExt = pTC.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External TelecommunicationAddressUse value: {}", lTCStrExt);

        final TelecommunicationAddressUse lTCInt;
        try
        {
            lTCInt = TelecommunicationAddressUse.valueOf(lTCStrExt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
        }

        return lTCInt;
    }

    private static org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse tELTelecomAddrUseInternal2TELTelecomAddrUse(
            final TelecommunicationAddressUse pTC) throws InvalidDataException
    {
        final String _METHODNAME = "tELTelecomAddrUseInternal2TELTelecomAddrUse(): ";

        if (pTC == null)
            return null;

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal TelecommunicationAddressUse value: {}", pTC);

        final org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse lTelecomCapaExt;
        try
        {
            lTelecomCapaExt = org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse.valueOf(pTC.toString());
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External TelecommunicationAddressUse value: {}", lTelecomCapaExt.value());

        return lTelecomCapaExt;
    }

    private static TelecommunicationCapability tELTelecomCapa2TELTelecomCapaInternal(
            final org.opencds.vmr.v1_0.schema.TelecommunicationCapability pTC) throws InvalidDataException
    {
        final String _METHODNAME = "tELTelecomCapa2TELTelecomCapaInternal() :";

        if (pTC == null)
            return null;

        final String lTCStrExt = pTC.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External TelecommunicationCapability value: {}", lTCStrExt);

        final TelecommunicationCapability lTCInt;
        try
        {
            lTCInt = TelecommunicationCapability.valueOf(lTCStrExt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
        }

        return lTCInt;
    }

    private static org.opencds.vmr.v1_0.schema.TelecommunicationCapability tELTelecomCapaInternal2TelecomCapa(
            final TelecommunicationCapability pTC) throws InvalidDataException
    {
        final String _METHODNAME = "tELTelecomInternal2TELTelecomCapa(): ";

        if (pTC == null)
            return null;

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal TelecommunicationCapability value: {}", pTC);

        final org.opencds.vmr.v1_0.schema.TelecommunicationCapability lTelecomCapaExt;
        try
        {
            lTelecomCapaExt = org.opencds.vmr.v1_0.schema.TelecommunicationCapability.valueOf(pTC.toString());
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External TelecommunicationCapability value: {}", lTelecomCapaExt.value());

        return lTelecomCapaExt;
    }

    public static AD aD2ADInternal(final org.opencds.vmr.v1_0.schema.AD pADExt) throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "aD2ADInternal(): ";

        if (pADExt == null)
            return null;

        final AD lADInt = new AD();

        final List<org.opencds.vmr.v1_0.schema.ADXP> lExtAddressPart = pADExt.getPart();
        if (Objects.isNull(lExtAddressPart))
        {
            final String errStr = _METHODNAME + "List<ADXP> element of external AD datatype not populated - required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new DataFormatException(errStr);
        }

        final Iterator<org.opencds.vmr.v1_0.schema.ADXP> lExtAddressPartIter = lExtAddressPart.iterator();
        int count = 0;
        while (lExtAddressPartIter.hasNext())
        {
            lADInt.getPart().add(aDXP2ADXPInternal(lExtAddressPartIter.next()));
            count++;
        }

        if (count < 1)
        {
            final String errStr = _METHODNAME
                    + "No ext->int translations of List<ADXP> successful - at least one member of List<ADXP> required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new DataFormatException(errStr);
        }

        final List<org.opencds.vmr.v1_0.schema.PostalAddressUse> lExtAddressUseList = pADExt.getUse();
        if (lExtAddressUseList != null)
        {
            for (final org.opencds.vmr.v1_0.schema.PostalAddressUse lExtAddressUse : lExtAddressUseList)
                lADInt.getUse().add(aDPostalAddressUse2ADPostalAddressUseInternal(lExtAddressUse));
        }

        return lADInt;
    }

    public static org.opencds.vmr.v1_0.schema.AD aDInternal2AD(final AD pADInt) throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "aDInternal2AD(): ";

        if (pADInt == null)
            return null;

        final org.opencds.vmr.v1_0.schema.AD lADExt = new org.opencds.vmr.v1_0.schema.AD();

        final List<ADXP> lIntAddressPart = pADInt.getPart();
        if (lIntAddressPart == null || lIntAddressPart.isEmpty())
        {
            final String errStr = _METHODNAME + "List<ADXP> element of internal AD datatype not populated - required by vmr spec";
            if (log.isDebugEnabled())
                log.debug(errStr);
            throw new DataFormatException(errStr);
        }

        final Iterator<ADXP> lIntAddressPartIter = lIntAddressPart.iterator();
        int count = 0;
        while (lIntAddressPartIter.hasNext())
        {
            lADExt.getPart().add(aDXPInternal2ADXP(lIntAddressPartIter.next()));
            count++;
        }

        if (count < 1)
            throw new InvalidDataException(_METHODNAME
                    + "No int->ext translations of List<ADXP> successful - at least one member of List<ADXP> required by vmr spec");

        final List<PostalAddressUse> lIntAddressUseList = pADInt.getUse();
        if (lIntAddressUseList != null)
        {
            for (final PostalAddressUse lIntAddressUse : lIntAddressUseList)
                lADExt.getUse().add(aDPostalAddressUseInternal2ADPostalAddressUse(lIntAddressUse));
        }

        return lADExt;
    }

    private static ADXP aDXP2ADXPInternal(final org.opencds.vmr.v1_0.schema.ADXP pADXP)
            throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "aDXP2ADXPInternal(): ";
        if (pADXP == null)
            return null;

        final ADXP lADXPInt = new ADXP();

        lADXPInt.setValue(pADXP.getValue());

        final org.opencds.vmr.v1_0.schema.AddressPartType lAddressPartTypeExt = pADXP.getType();
        if (lAddressPartTypeExt == null)
            throw new DataFormatException(
                    _METHODNAME + "AddressPartType of external ADXP datatype not populated; required by vmr spec");

        final String lAddrPartTypeStrExt = pADXP.getType().toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External AddressPartType value: {}", lAddrPartTypeStrExt);

        final AddressPartType lAddrPartTypeInt;
        try
        {
            lAddrPartTypeInt = AddressPartType.valueOf(lAddrPartTypeStrExt);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal AddressPartType value: {}", lAddrPartTypeInt);

        lADXPInt.setType(lAddrPartTypeInt);

        return lADXPInt;
    }

    private static org.opencds.vmr.v1_0.schema.ADXP aDXPInternal2ADXP(final ADXP pADXPInternal)
    {
        final String _METHODNAME = "aDXPInternal2ADXP(): ";
        if (pADXPInternal == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ADXP lADXPExt = new org.opencds.vmr.v1_0.schema.ADXP();

        lADXPExt.setValue(pADXPInternal.getValue());

        final String lIntAddrPartTypeStr = pADXPInternal.getType().toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal AddressPartType value: {}", lIntAddrPartTypeStr);

        final org.opencds.vmr.v1_0.schema.AddressPartType lAddrPartTypeExt;
        try
        {
            lAddrPartTypeExt = org.opencds.vmr.v1_0.schema.AddressPartType.valueOf(lIntAddrPartTypeStr);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new RuntimeException(_METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External AddressPartType value: {}", lAddrPartTypeExt);

        lADXPExt.setType(lAddrPartTypeExt);

        return lADXPExt;
    }

    private static org.opencds.vmr.v1_0.schema.PostalAddressUse aDPostalAddressUseInternal2ADPostalAddressUse(
            final PostalAddressUse pPAUInternal) throws InvalidDataException
    {
        final String _METHODNAME = "postalAddressUseInternal2PostalAddressUse(): ";

        if (pPAUInternal == null)
            return null;

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Internal PostalAddressUse value: {}", pPAUInternal);

        final org.opencds.vmr.v1_0.schema.PostalAddressUse lPostalAddressExt;
        try
        {
            lPostalAddressExt = org.opencds.vmr.v1_0.schema.PostalAddressUse.valueOf(pPAUInternal.toString());
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the internal to external enumeration");
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External PostAddressUse value: {}", lPostalAddressExt.value());

        return lPostalAddressExt;
    }

    private static PostalAddressUse aDPostalAddressUse2ADPostalAddressUseInternal(
            final org.opencds.vmr.v1_0.schema.PostalAddressUse pPAUExt) throws InvalidDataException
    {
        final String _METHODNAME = "postalAddressUse2PostalAddressUseInternal() :";

        if (pPAUExt == null)
            return null;

        final String lExtAddressUseStr = pPAUExt.toString();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "External PostalAddressUse value: {}", pPAUExt);

        final PostalAddressUse lPostalAddressInt;
        try
        {
            lPostalAddressInt = PostalAddressUse.valueOf(lExtAddressUseStr);
        }
        catch (final IllegalArgumentException iae)
        {
            throw new InvalidDataException(
                    _METHODNAME + "there was no direct value mapping from the external to internal enumeration");
        }

        return lPostalAddressInt;
    }

    public static II getUUIDAsII()
    {
        final II ii = new II();
        ii.setValue(MiscUtility.getIDAsString());
        return ii;
    }
}
