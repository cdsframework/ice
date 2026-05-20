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
    private String id;
    @EqualsAndHashCode.Include
    private String conceptTargetId;
    @EqualsAndHashCode.Include
    private String openCdsConceptCode;
    @EqualsAndHashCode.Include
    private String determinationMethodCode;
    private String displayName;
}
