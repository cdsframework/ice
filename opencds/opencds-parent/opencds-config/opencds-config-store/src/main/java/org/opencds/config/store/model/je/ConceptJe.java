package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Concept;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class ConceptJe implements Concept {
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;

    private ConceptJe() {
    }

    public static ConceptJe create(String code, String codeSystem, String codeSystemName, String displayName) {
        ConceptJe tc = new ConceptJe();
        tc.code = code;
        tc.codeSystem = codeSystem;
        tc.codeSystemName = codeSystemName;
        tc.displayName = displayName;
        return tc;
    }

    public static ConceptJe create(Concept c) {
        if (c == null) {
            return null;
        }
        if (c instanceof ConceptJe) {
            return ConceptJe.class.cast(c);
        }
        return create(c.getCode(), c.getCodeSystem(), c.getCodeSystemName(),
                c.getDisplayName());
    }

    public static List<Concept> create(List<Concept> concepts) {
        if (concepts == null) {
            return null;
        }
        List<Concept> fcs = new ArrayList<>();
        if (concepts != null) {
            for (Concept fc : concepts) {
                fcs.add(create(fc));
            }
        }
        return fcs;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCodeSystem() {
        return codeSystem;
    }

    @Override
    public String getCodeSystemName() {
        return codeSystemName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
