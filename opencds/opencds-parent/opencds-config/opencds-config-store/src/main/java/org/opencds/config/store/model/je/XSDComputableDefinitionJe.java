package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencds.config.api.model.XSDComputableDefinition;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class XSDComputableDefinitionJe implements XSDComputableDefinition {

    private String rootGlobalElementName;
    private String url;
    private List<String> storedSchematronUrls;
    private String narrativeModelRestrictionGuideUrl;

    private XSDComputableDefinitionJe() {
    }

    public static XSDComputableDefinitionJe create(String rootGlobalElementName, String url,
            List<String> schematronUrls, String narrativeModelRestrictionGuideUrl) {
        XSDComputableDefinitionJe cdj = new XSDComputableDefinitionJe();
        cdj.rootGlobalElementName = rootGlobalElementName;
        cdj.url = url;
        cdj.storedSchematronUrls = new ArrayList<>(schematronUrls);
        cdj.narrativeModelRestrictionGuideUrl = narrativeModelRestrictionGuideUrl;
        return cdj;
    }

    public static XSDComputableDefinitionJe create(XSDComputableDefinition cd) {
        if (cd == null) {
            return null;
        }
        if (cd instanceof XSDComputableDefinitionJe) {
            return XSDComputableDefinitionJe.class.cast(cd);
        }
        return create(cd.getRootGlobalElementName(), cd.getUrl(), cd.getSchematronUrls(),
                cd.getNarrativeModelRestrictionGuideUrl());
    }

    public static List<XSDComputableDefinition> create(ArrayList<XSDComputableDefinition> xsdComputableDefinitions) {
        if (xsdComputableDefinitions == null) {
            return null;
        }
        List<XSDComputableDefinition> cds = new ArrayList<>();
        for (XSDComputableDefinition cd : xsdComputableDefinitions) {
            cds.add(create(cd));
        }
        return cds;
    }

    @Override
    public String getRootGlobalElementName() {
        return rootGlobalElementName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    protected List<String> getStoredSchematronUrls() {
        return storedSchematronUrls;
    }

    @Override
    public List<String> getSchematronUrls() {
        return Collections.unmodifiableList(storedSchematronUrls);
    }

    @Override
    public String getNarrativeModelRestrictionGuideUrl() {
        return narrativeModelRestrictionGuideUrl;
    }

}
