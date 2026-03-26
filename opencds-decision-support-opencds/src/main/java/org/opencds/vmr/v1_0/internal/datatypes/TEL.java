package org.opencds.vmr.v1_0.internal.datatypes;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class TEL extends ANY
{
    protected String useablePeriodOriginalText;
    protected String value;
    protected List<TelecommunicationAddressUse> use;
    protected List<TelecommunicationCapability> capabilities;
}
