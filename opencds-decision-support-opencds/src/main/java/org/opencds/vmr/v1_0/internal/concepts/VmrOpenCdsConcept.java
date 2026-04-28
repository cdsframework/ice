package org.opencds.vmr.v1_0.internal.concepts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@ToString
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
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
