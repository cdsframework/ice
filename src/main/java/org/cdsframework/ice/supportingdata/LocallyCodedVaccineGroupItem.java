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
    /**
     * <ns2:iceVaccineGroupSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.ice.vaccinegroup">
     * <vaccineGroup code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1" codeSystemName="ICE Vaccine Group" displayName="HepB"/>
     * <cdsVersion>org.nyc.cir^ICE^1.0.0</cdsVersion>
     * <ns2:priority>10</ns2:priority>
     * <relatedVaccine code="104" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep A-Hep B"/>
     * <relatedVaccine code="43" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, adult"/>
     * <relatedVaccine code="42" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, adolescent/high risk infant"/>
     * <relatedVaccine code="44" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, dialysis"/>
     * <relatedVaccine code="102" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="DTP-Hib-Hep B"/>
     * <relatedVaccine code="45" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B NOS"/>
     * <relatedVaccine code="110" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="DTaP-Hep B-IPV"/>
     * <relatedVaccine code="08" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, adolescent or pediatric"/>
     * <relatedVaccine code="51" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hib-Hep B"/>
     * <diseaseImmunity code="070.30" codeSystem="2.16.840.1.113883.6.103" codeSystemName="ICE Disease" displayName="Hepatitis B"/>
     * <openCdsMembership code="ICE202" displayName="Immunization Evaluation (Hep B Vaccine Group)"/>
     * <primaryOpenCdsConcept code="ICE100" displayName="Immunization Evaluation (Hep B Vaccine Group)"/>
     * </ns2:iceVaccineGroupSpecificationFile>
     */

    private final Collection<String> relatedDiseasesCdsListItemNames;
    private int priority;

    protected LocallyCodedVaccineGroupItem(final String pVaccineGroupCdsListItemName, final CdsConcept pVaccineGroupCdsConcept,
            final Collection<String> pCdsVersions, final Collection<String> pRelatedDiseasesCdsListItemNames)
            throws IllegalArgumentException
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
        this.priority = 0;
    }

    protected LocallyCodedVaccineGroupItem(final String pVaccineGroupCdsListItemName, final CdsConcept pVaccineGroupCdsConcept,
            final Collection<String> pCdsVersions, final Collection<String> pRelatedDiseasesCdsListItemNames, final int pPriority)
            throws IllegalArgumentException
    {
        this(pVaccineGroupCdsListItemName, pVaccineGroupCdsConcept, pCdsVersions, pRelatedDiseasesCdsListItemNames);
        this.priority = pPriority;
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
                        + ", primaryOpenCdsConcept=" + getCdsConceptName());

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
