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

package org.cdsframework.cds;

import java.util.Objects;

import org.opencds.vmr.v1_0.internal.concepts.VmrOpenCdsConcept;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * A CDS concept that originates from CAT. It may or may not also be an instantiated OpenCDS concept during invocation of OpenCDS.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class CdsConcept extends VmrOpenCdsConcept
{
    public static CdsConcept constructDeepCopyOfCdsConceptObject(final CdsConcept pIC)
    {
        if (pIC == null)
            return null;

        final CdsConcept lIC = new CdsConcept();
        lIC.setConceptTargetId(pIC.getConceptTargetId());
        lIC.setDeterminationMethodCode(pIC.getDeterminationMethodCode());
        lIC.setOpenCdsConceptCode(pIC.getOpenCdsConceptCode());
        lIC.setDisplayName(pIC.getDisplayName());
        lIC.setIsOpenCdsSupportedConcept(pIC.isOpenCdsSupportedConcept);
        return lIC;
    }

    private String displayName;
    private boolean isOpenCdsSupportedConcept;

    /**
     * Instantiate an OpenCdsConceptCode object. It does not set the conceptTargetId, displayName or determinationMethodCode attributes of the VmrOpenCdsConcept
     *
     * @throws IllegalArgumentException if concept code is null
     */
    public CdsConcept(final String pOpenCdsConceptCode)
    {
        if (pOpenCdsConceptCode == null)
        {
            final String _METHODNAME = "OpenCdsICEConcept(): ";
            final String errStr = "concept code not supplied";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        setOpenCdsConceptCode(pOpenCdsConceptCode);
    }

    /**
     * Instantiate an CdsConcept object. It does not set the conceptTargetId or determinationMethodCode attributes of the VmrOpenCdsConcept
     *
     * @param pOpenCdsConceptCode Concept Code; mandatory
     * @param pDisplayName        Display Name
     * @throws IllegalArgumentException if concept code is null
     */
    public CdsConcept(final String pOpenCdsConceptCode, final String pDisplayName)
    {
        this(pOpenCdsConceptCode);
        setDisplayName(pDisplayName);
    }

    public void setIsOpenCdsSupportedConcept(final boolean pIsOpenCdsSupportedConcept)
    {
        this.isOpenCdsSupportedConcept = pIsOpenCdsSupportedConcept;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(getOpenCdsConceptCode());
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof final CdsConcept other))
            return false;

        return Objects.equals(this.getOpenCdsConceptCode(), other.getOpenCdsConceptCode());
    }

    @Override
    public String toString()
    {
        return "CdsConcept [displayName=%s, isOpenCdsSupportedConcept=%s, toString()=%s]".formatted(displayName,
                isOpenCdsSupportedConcept, super.toString());
    }
}
