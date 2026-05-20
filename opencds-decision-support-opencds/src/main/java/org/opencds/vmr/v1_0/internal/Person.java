package org.opencds.vmr.v1_0.internal;

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Person extends EntityBase
{
    private List<EN> name;
    private List<AD> address;
    private List<TEL> telecom;
}
