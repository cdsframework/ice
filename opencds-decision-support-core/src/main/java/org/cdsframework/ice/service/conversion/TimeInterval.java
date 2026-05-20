package org.cdsframework.ice.service.conversion;

import java.time.LocalDate;

record TimeInterval(LocalDate low,
                    LocalDate high)
{
}