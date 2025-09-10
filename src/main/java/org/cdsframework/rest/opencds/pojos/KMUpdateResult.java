package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.impl.KMIdImpl;

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
public class KMUpdateResult
{
    private KMIdImpl kmId;
    private UpdateError error;

    public KMUpdateResult(final KMId kmId, final Integer status, final String message)
    {
        this.kmId = KMIdImpl.create(kmId);
        this.error = new UpdateError(status, message);
    }
}
