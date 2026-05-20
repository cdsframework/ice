package org.opencds.vmr.v1_0.internal;

import java.time.LocalDate;
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
    private LocalDate birthTime;
    private PQ age;
    private CD gender;
    private List<CD> race;
    private List<CD> ethnicity;
    private List<EN> name;
    private List<AD> address;
    private List<TEL> telecom;
    private BL isDeceased;
    private PQ ageAtDeath;
    private CD preferredLanguage;
}
