package org.cdsframework.cds.supportingdata;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.ICEConceptType;

import lombok.extern.slf4j.Slf4j;

/**
 * SupportedCdsConcepts is used to track the CAT-originated concepts defined in its supporting data files. All concepts, if defined in the supporting data, must be associated with
 * an ICEConceptType, or may not be tracked by this class. Furthermore, no non-OpenCDS CdsConcept defined in the supporting data can be associated with more than one CdsListItem
 * per ICEConceptType. This latter requirement is so that, on output for a request, the ICE response may be output using the locally coded values required by the client.
 * FUTURE enhancement: OpenCDS concepts may continue to be associated with multiple cdsListItems.
 */
@Slf4j
public class SupportedCdsConcepts
{
    private final Map<ICEConceptType, Map<CdsConcept, LocallyCodedCdsListItem>> iceConceptTypeToConceptCdsListItemMap =
            new EnumMap<>(ICEConceptType.class);
    // cdsListItem -> map of all OpenCDS and non-OpenCDS concepts
    // ICEConceptType -> (map of cds concepts -> cdsListItem)
    private final Map<LocallyCodedCdsListItem, Set<CdsConcept>> cdsListItemToConceptList = new HashMap<>();

    /**
     * Add the supported concept for the specified ICEConceptType and associate the specified LocallyCodedCdsListItem with the concept. If either ICEConceptType or CdsConcept
     * arguments are null, this method simply returns. If the LocallyCodedCdsListItem is null, the CdsConcept is simply added to the list of concepts being tracked without
     * a LocallyCodedCdsListItem
     *
     * @param pICT   The ICEConceptType this concept will be associated with
     * @param pIC    The concept to add
     * @param pLCCLI The LocallyCodedCdsListItem (a.k.a. locally coded CdsListItem) with which to associate the CdsConcept with.
     * @throws InconsistentConfigurationException if the supporting data supplied is inconsistent with prior supporting data that has already been provided. This will happen
     *                                            if the CdsConcept is already associated with a LocallyCodedCdsListItem for the specified IceConceptType. (So, although at the OpenCDS level, concepts may map to
     *                                            multiple codes and code sets, at the supporting data level, only one code may be mapped to an CdsConcept [which may or may not also be an OpenCDS concept]).
     */
    public void addSupportedCdsConceptWithCdsListItem(final ICEConceptType pICT, final CdsConcept pIC,
            final LocallyCodedCdsListItem pLCCLI) throws InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedCdsConceptWithCdsListItem(): ";

        if (pICT == null || pIC == null)
            return;

        final Map<CdsConcept, LocallyCodedCdsListItem> lIceConceptEntry =
                this.iceConceptTypeToConceptCdsListItemMap.getOrDefault(pICT, new HashMap<>());
        if (pLCCLI != null)
        {
            Set<CdsConcept> lICEConceptListAssocWCdsListItem = this.cdsListItemToConceptList.get(pLCCLI);
            final LocallyCodedCdsListItem priorAssociatedLCCLI = lIceConceptEntry.get(pIC);
            if (priorAssociatedLCCLI == null)
            {
                lIceConceptEntry.put(pIC, pLCCLI);
                // Record the Map<ICEConceptType, Map<ICEConcept, LocallyCodedCdsListItem> entry
                this.iceConceptTypeToConceptCdsListItemMap.put(pICT, lIceConceptEntry);
                // Record the Map<LocallyCodedCdsListItem, List<ICEConcept>> entry
                if (lICEConceptListAssocWCdsListItem == null || !lICEConceptListAssocWCdsListItem.contains(pIC))
                {
                    if (lICEConceptListAssocWCdsListItem == null)
                        lICEConceptListAssocWCdsListItem = new HashSet<>();
                    lICEConceptListAssocWCdsListItem.add(pIC);
                    this.cdsListItemToConceptList.put(pLCCLI, lICEConceptListAssocWCdsListItem);
                }

                if (log.isDebugEnabled())
                    log.debug(_METHODNAME
                                    + "SupportedCdsConcept with LocallyCodedCdsListItem ADDED. ICEConceptType: {}; ICEConcept: {}; LocallyCodedCdsListItem: {}",
                            pICT, pIC, pLCCLI);
            }
            else
            {
                if (!priorAssociatedLCCLI.equals(pLCCLI))
                {
                    final String lErrStr =
                            "Attempt to map different LocallyCodedCdsListItem with ICEConcept previously mapped: ICEConceptType: %s; ICEConcept: %s; LocallyCodedCdsListItem: %s".formatted(
                                    pICT, pIC, pLCCLI);
                    log.warn(_METHODNAME + "{}", lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (log.isDebugEnabled())
                    log.debug(_METHODNAME
                                    + "Encountered SupportedCdsConcept with _duplicate_ LocallyCodedCdsListItem (ignored): ICEConceptType: {}; ICEConcept: {}; LocallyCodedCdsListItem: {}",
                            pICT, pIC, pLCCLI);
            }
        }
        else
        {
            if (lIceConceptEntry.containsKey(pIC))
            {
                if (lIceConceptEntry.get(pIC) != null)
                {
                    final String lErrStr =
                            "Attempt to map different LocallyCodedCdsListItem with ICEConcept previously mapped: IceConceptType: %s; ICEConcept: %s".formatted(
                                    pICT, pIC);
                    log.warn(_METHODNAME + "{}", lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }
            }
            else
                this.iceConceptTypeToConceptCdsListItemMap.put(pICT, null);
        }
    }

    /**
     * Return map of supported concepts for the given (non-OpenCDS) ICEConceptType. Returns null if not found.
     */
    public Map<CdsConcept, LocallyCodedCdsListItem> getCdsConceptsAssociatedWithICEConceptType(final ICEConceptType pICT)
    {
        if (pICT == null)
            return null;

        return this.iceConceptTypeToConceptCdsListItemMap.get(pICT);
    }

    /**
     * Return LocallyCodedCdsListItem for the given IceConceptType and CdsConcept (i.e. - ICEConcept which may or may not also be an OpenCDS concept). Returns null if not found.
     */
    public LocallyCodedCdsListItem getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(final ICEConceptType pICT,
            final CdsConcept pIC)
    {
        if (pICT == null || pIC == null)
            return null;

        return Optional.ofNullable(iceConceptTypeToConceptCdsListItemMap.get(pICT)).map(m -> m.get(pIC)).orElse(null);
    }

    /**
     *
     */
    @Override
    public String toString()
    {
        final StringBuilder toStr = new StringBuilder("CdsConcept [ conceptTypeToConceptCdsListItemMap [[");

        {
            int i = 1;
            for (final Map.Entry<ICEConceptType, Map<CdsConcept, LocallyCodedCdsListItem>> entry : iceConceptTypeToConceptCdsListItemMap.entrySet())
            {
                toStr.append("\n\t{").append(i++).append("} ICEConceptType: ").append(entry.getKey());
                if (entry.getValue() != null)
                {
                    for (final Map.Entry<CdsConcept, LocallyCodedCdsListItem> entry2 : entry.getValue().entrySet())
                        toStr.append("\n\t\tConcept: ")
                                .append(entry2.getKey().getOpenCdsConceptCode())
                                .append(", isOpenCdsConcept? ")
                                .append(entry2.getKey().isOpenCdsSupportedConcept())
                                .append("; LocallyCodedCdsListItem cdsListItemName: ")
                                .append(entry2.getValue().getCdsListItemName());
                }
            }
        }

        toStr.append("\t]]\n\tcdsListItemToConceptList [[");

        int i = 1;
        for (final Map.Entry<LocallyCodedCdsListItem, Set<CdsConcept>> entry : cdsListItemToConceptList.entrySet())
        {
            toStr.append("\n\t{")
                    .append(i++)
                    .append("} LocallyCodedCdsListItem cdsListItemName: ")
                    .append(entry.getKey().getCdsListItemName());
            if (entry.getValue() != null)
            {
                for (final CdsConcept lcc : entry.getValue())
                    toStr.append("\n\t\tConcept: ")
                            .append(lcc.getOpenCdsConceptCode())
                            .append(", isOpenCdsConcept? ")
                            .append(lcc.isOpenCdsSupportedConcept());
            }
        }

        toStr.append("\t]]");

        return toStr.toString();
    }
}
