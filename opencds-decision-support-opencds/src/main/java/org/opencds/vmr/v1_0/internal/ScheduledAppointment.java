package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class ScheduledAppointment extends EncounterBase
{
    private IVLDate appointmentTime;
}
