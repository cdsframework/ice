package org.opencds.vmr.v1_0.internal.datatypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CD extends ANY
{
    private String displayName;
    @EqualsAndHashCode.Include
    private String code;
    @EqualsAndHashCode.Include
    private String codeSystem;
    private String codeSystemName;
    private String originalText;
}
