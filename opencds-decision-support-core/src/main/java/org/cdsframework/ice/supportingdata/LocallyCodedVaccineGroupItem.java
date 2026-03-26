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

import java.util.ArrayList;
import java.util.Collection;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class LocallyCodedVaccineGroupItem extends LocallyCodedCdsItem
{
    private final Collection<String> relatedDiseasesCdsListItemNames;
    private final int priority;
    private final boolean routine;

    protected LocallyCodedVaccineGroupItem(final String pVaccineGroupCdsListItemName, final CdsConcept pVaccineGroupCdsConcept,
            final Collection<String> pCdsVersions, final Collection<String> pRelatedDiseasesCdsListItemNames, final int pPriority,
            final boolean routine) throws IllegalArgumentException
    {
        super(pVaccineGroupCdsListItemName, pVaccineGroupCdsConcept, pCdsVersions);

        final String _METHODNAME = "VaccineGroupItem(): ";

        // Check to make sure related vaccines list is specified
        if (pRelatedDiseasesCdsListItemNames == null)
        {
            final String lErrStr = "Related vaccines not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.relatedDiseasesCdsListItemNames = pRelatedDiseasesCdsListItemNames;
        this.priority = pPriority;
        this.routine = routine;
    }

    protected Collection<String> getRelatedDiseasesCdsListItemNames()
    {
        return relatedDiseasesCdsListItemNames;
    }

    public Collection<String> getCopyOfRelatedDiseasesCdsListItemNames()
    {
        if (this.relatedDiseasesCdsListItemNames == null)
            return null;

        return new ArrayList<>(this.relatedDiseasesCdsListItemNames);
    }

    @Override
    public String toString()
    {
        final StringBuilder lStr = new StringBuilder(
                "LocallyCodedVaccineGroupItem [vaccineGroupCdsListItemName=" + getCdsItemName() + ", priority=" + priority
                        + ", primaryOpenCdsConcept=" + getCdsConceptName() + ", routine=" + routine);

        lStr.append("\nrelatedDiseases= [");
        for (final String lDisease : getRelatedDiseasesCdsListItemNames())
            lStr.append("\tRelatedDiseaseCdsListItemName=").append(lDisease).append("\n");
        lStr.append("\t]\n");
        lStr.append("]");

        lStr.append("\ncdsVersions= [");
        for (final String lVersionStr : getCdsVersions())
            lStr.append("\tCdsVersion=").append(lVersionStr).append("\n");
        lStr.append("\t]\n");
        lStr.append("]");

        return lStr.toString();
    }
}
