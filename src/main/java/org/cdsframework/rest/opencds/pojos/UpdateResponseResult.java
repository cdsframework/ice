package org.cdsframework.rest.opencds.pojos;

import java.util.ArrayList;
import java.util.List;

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
public class UpdateResponseResult
{
    private List<KMUpdateResult> kms = new ArrayList<>();
    private List<CDMUpdateResult> cdms = new ArrayList<>();
    private String environment;
    private String instanceId;

    public boolean hasErrors()
    {
        return hasKmErrors() || hasCdmErrors();
    }

    public boolean hasKmErrors()
    {
        return kms != null && !kms.isEmpty();
    }

    public boolean hasCdmErrors()
    {
        return cdms != null && !cdms.isEmpty();
    }
}
