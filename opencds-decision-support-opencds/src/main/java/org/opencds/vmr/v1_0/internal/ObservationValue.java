package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.IVLINT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;
import org.opencds.vmr.v1_0.internal.datatypes.IVLREAL;
import org.opencds.vmr.v1_0.internal.datatypes.IVLRTO;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.REAL;
import org.opencds.vmr.v1_0.internal.datatypes.RTO;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ObservationValue
{
    protected AD address;
    protected BL _boolean;
    protected CD concept;
    protected REAL decimal;
    protected IVLREAL decimalRange;
    protected String identifier;
    protected INT integer;
    protected IVLINT integerRange;
    protected EN name;
    protected PQ physicalQuantity;
    protected IVLPQ physicalQuantityRange;
    protected RTO ratio;
    protected IVLRTO ratioRange;
    protected String simpleConcept;
    protected TEL telecom;
    protected String text;
    protected java.util.Date time;
    protected IVLDate timeRange;
}
