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
    private String useablePeriodOriginalText;
    private String value;
    private List<TelecommunicationAddressUse> use;
    private List<TelecommunicationCapability> capabilities;
}
