package org.cdsframework.rest.opencds.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author sdn
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class HealthCheck
{
    private Integer status;
    private String message;
}
