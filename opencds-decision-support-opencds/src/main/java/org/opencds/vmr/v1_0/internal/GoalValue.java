package org.opencds.vmr.v1_0.internal;

import java.time.LocalDate;

import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.IVLINT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;
import org.opencds.vmr.v1_0.internal.datatypes.IVLREAL;
import org.opencds.vmr.v1_0.internal.datatypes.IVLRTO;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.REAL;
import org.opencds.vmr.v1_0.internal.datatypes.RTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GoalValue
{
    private BL _boolean;
    private CD concept;
    private REAL decimal;
    private IVLREAL decimalRange;
    private INT integer;
    private IVLINT integerRange;
    private PQ physicalQuantity;
    private IVLPQ physicalQuantityRange;
    private RTO ratio;
    private IVLRTO ratioRange;
    private String simpleConcept;
    private String text;
    private LocalDate time;
    private IVLDate timeRange;
}
