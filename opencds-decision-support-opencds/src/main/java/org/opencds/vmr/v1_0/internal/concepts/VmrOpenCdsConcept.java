package org.opencds.vmr.v1_0.internal.concepts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@ToString
public abstract class VmrOpenCdsConcept
{
    @EqualsAndHashCode.Include
    protected String id;
    @EqualsAndHashCode.Include
    protected String conceptTargetId;
    @EqualsAndHashCode.Include
    protected String openCdsConceptCode;
    @EqualsAndHashCode.Include
    protected String determinationMethodCode;
    protected String displayName;
}
