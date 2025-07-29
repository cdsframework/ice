package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.impl.CDMIdImpl;

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
public class CDMUpdate
{
    private byte[] cdm;
    private CDMIdImpl cdmId;
    private String error;

    public boolean isValid()
    {
        return getCdmId() != null && getCdmId().getCode() != null && getCdmId().getCodeSystem() != null
                && getCdmId().getVersion() != null && getCdm() != null && getCdm().length > 0;
    }
}
