package org.opencds.vmr.v1_0.internal.datatypes;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class AD extends ANY
{
    private List<ADXP> part = new ArrayList<>();
    private List<PostalAddressUse> use = new ArrayList<>();

    public List<ADXP> getPart()
    {
        if (part == null)
            part = new ArrayList<>();
        return this.part;
    }

    public List<PostalAddressUse> getUse()
    {
        if (use == null)
            use = new ArrayList<>();
        return this.use;
    }
}
