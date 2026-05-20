package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CDSContext
{
    private CD cdsSystemUserType;
    private CD cdsSystemUserPreferredLanguage;
    private CD cdsInformationRecipientType;
    private CD cdsInformationRecipientPreferredLanguage;
    private CD cdsSystemUserTaskContext;
}
