package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.impl.KMIdImpl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class KMUpdate
{
    private KMIdImpl kmId;
    private byte[] kmPackage;
    private String error;

    public boolean isValid()
    {
        return getKmId() != null && getKmId().getScopingEntityId() != null && getKmId().getBusinessId() != null
                && getKmId().getVersion() != null && getKmPackage() != null && getKmPackage().length > 0;
    }
}
