package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.impl.CDMIdImpl;

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
public class CDMUpdateResult
{
    private UpdateError error;
    private CDMIdImpl cdmId;

    public CDMUpdateResult(final CDMId cdmId, final Integer status, final String message)
    {
        this.cdmId = CDMIdImpl.create(cdmId);
        this.error = new UpdateError(status, message);
    }
}
