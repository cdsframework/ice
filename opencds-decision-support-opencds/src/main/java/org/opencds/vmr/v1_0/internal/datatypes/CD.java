package org.opencds.vmr.v1_0.internal.datatypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
public class CD extends ANY
{
    protected String displayName;
    @EqualsAndHashCode.Include
    protected String code;
    @EqualsAndHashCode.Include
    protected String codeSystem;
    protected String codeSystemName;
    protected String originalText;
}
