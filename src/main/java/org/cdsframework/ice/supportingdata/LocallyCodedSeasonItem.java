/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.service.Season;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class LocallyCodedSeasonItem extends LocallyCodedCdsItem
{
    /**
     * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <ns2:iceSeasonSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.ice.season">
     * <ns2:seasonId>ca689767c72a9c07c79514d7aef74624</ns2:seasonId>
     * <ns2:name>2013-2014 Influenza Season</ns2:name>
     * <ns2:code>20132014InfluenzaSeason</ns2:code>
     * <ns2:defaultSeason>false</ns2:defaultSeason>
     * <ns2:startDate>2013-08-01-04:00</ns2:startDate>
     * <ns2:endDate>2014-06-30-04:00</ns2:endDate>
     * <ns2:vaccineGroup code="800" codeSystem="2.16.840.1.113883.3.795.12.100.1" codeSystemName="ICE Vaccine Group" displayName="Influenza"/>
     * <ns2:cdsVersion>org.nyc.cir^ICE^1.0.0</ns2:cdsVersion>
     * </ns2:iceSeasonSpecificationFile>
     */
    private final Season season;

    protected LocallyCodedSeasonItem(final String pCdsSeasonName, final Collection<String> pCdsVersions, final Season pSeason)
            throws IllegalArgumentException
    {
        super(pCdsSeasonName, pCdsVersions);

        final String _METHODNAME = "LocallyCodedSeasonItem(): ";

        if (pSeason == null)
        {
            final String lErrStr = "Season parameter not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.season = pSeason;
    }

    @Override
    public String toString()
    {
        return "LocallyCodedSeasonItem [getCdsItemName()=%s; getSeason()=%s; getCdsVersions()=%s]".formatted(getCdsItemName(),
                this.season, getCdsVersions());
    }
}
