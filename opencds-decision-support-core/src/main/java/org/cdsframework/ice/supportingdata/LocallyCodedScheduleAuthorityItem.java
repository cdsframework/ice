/**
 * Copyright (C) 2026 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.supportingdata;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.service.ScheduleAuthority;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * LocallyCodedScheduleAuthorityItem
 */
@Slf4j
@Getter
public class LocallyCodedScheduleAuthorityItem extends LocallyCodedCdsItem
{
    private final ScheduleAuthority scheduleAuthority;

    protected LocallyCodedScheduleAuthorityItem(final String pCdsScheduleAuthorityName,
            final CdsConcept pScheduleAuthorityCdsConcept, final ScheduleAuthority pScheduleAuthority)
            throws IllegalArgumentException
    {
        super(pCdsScheduleAuthorityName, pScheduleAuthorityCdsConcept);

        final String _METHODNAME = "LocallyCodedScheduleAuthorityItem(): ";

        if (pScheduleAuthority == null)
        {
            final String lErrStr = "ScheduleAuthority parameter not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.scheduleAuthority = pScheduleAuthority;
    }

    @Override
    public String toString()
    {
        return "LocallyCodedScheduleAuthorityItem [getCdsItemName()=%s; getScheduleAuthority()=%s]".formatted(
                getCdsItemName(), this.scheduleAuthority);
    }
}
