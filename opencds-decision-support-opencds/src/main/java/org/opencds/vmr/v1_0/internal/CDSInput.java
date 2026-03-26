package org.opencds.vmr.v1_0.internal;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CDSInput
{
    protected List<String> templateId;
    protected List<CDSResource> cdsResource;

    protected String focalPersonId;
    protected CDSContext cdsContext;
}
