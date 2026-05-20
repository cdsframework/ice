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
    private CD substanceAdministrationGeneralPurpose;
    private AdministrableSubstance substance;
    private CD deliveryMethod;
    private IVLPQ doseQuantity;
    private CD deliveryRoute;
    private BodySite approachBodySite;
    private BodySite targetBodySite;
    private IVLPQ dosingPeriod;
    private BL dosingPeriodIntervalIsImportant;
    private IVLPQ deliveryRate;
    private CD doseType;
}
