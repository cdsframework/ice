package org.opencds.vmr.v1_0.internal;

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
    protected BL _boolean;
    protected CD concept;
    protected REAL decimal;
    protected IVLREAL decimalRange;
    protected INT integer;
    protected IVLINT integerRange;
    protected PQ physicalQuantity;
    protected IVLPQ physicalQuantityRange;
    protected RTO ratio;
    protected IVLRTO ratioRange;
    protected String simpleConcept;
    protected String text;
    protected java.util.Date time;
    protected IVLDate timeRange;
}
