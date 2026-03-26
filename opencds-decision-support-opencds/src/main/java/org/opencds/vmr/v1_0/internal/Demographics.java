package org.opencds.vmr.v1_0.internal;

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Demographics
{
    protected java.util.Date birthTime;
    protected PQ age;
    protected CD gender;
    protected List<CD> race;
    protected List<CD> ethnicity;
    protected List<EN> name;
    protected List<AD> address;
    protected List<TEL> telecom;
    protected BL isDeceased;
    protected PQ ageAtDeath;
    protected CD preferredLanguage;
}
