package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.KMId;

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
public class KmIdCheck
{
    private KMId kmId;
    private boolean exists;
}