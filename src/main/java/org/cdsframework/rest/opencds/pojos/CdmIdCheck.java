package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.CDMId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CdmIdCheck
{
    private CDMId cdmId;
    private boolean exists;
}