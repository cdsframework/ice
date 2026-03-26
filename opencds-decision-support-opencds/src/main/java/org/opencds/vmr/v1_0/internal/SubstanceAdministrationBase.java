package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLPQ;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public abstract class SubstanceAdministrationBase extends ClinicalStatement
{
    protected CD substanceAdministrationGeneralPurpose;
    protected AdministrableSubstance substance;
    protected CD deliveryMethod;
    protected IVLPQ doseQuantity;
    protected CD deliveryRoute;
    protected BodySite approachBodySite;
    protected BodySite targetBodySite;
    protected IVLPQ dosingPeriod;
    protected BL dosingPeriodIntervalIsImportant;
    protected IVLPQ deliveryRate;
    protected CD doseType;
}
