package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.XSDComputableDefinition;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class SemanticSignifierJe implements SemanticSignifier, ConfigEntity<SSIdJe> {
    @PrimaryKey
    private SSIdJe ssId;
    private String name;
    private String description;
    private List<XSDComputableDefinition> xsdComputableDefinitions;
    private String entryPoint;
    private String exitPoint;
    private String factListsBuilder;
    private String resultSetBuilder;
    private Date timestamp;
    private String userId;

    private SemanticSignifierJe() {
    }

    public static SemanticSignifierJe create(SSId ssid, String name, String description,
            List<XSDComputableDefinition> xsdComputableDefinitions, String entryPoint, String exitPoint,
            String factListsBuilder, String resultSetBuilder, Date timestamp, String userId) {
        SemanticSignifierJe ssje = new SemanticSignifierJe();
        ssje.ssId = SSIdJe.create(ssid);
        ssje.name = name;
        ssje.description = description;
        ssje.xsdComputableDefinitions = XSDComputableDefinitionJe.create(new ArrayList<>(xsdComputableDefinitions));
        ssje.entryPoint = entryPoint;
        ssje.exitPoint = exitPoint;
        ssje.factListsBuilder = factListsBuilder;
        ssje.resultSetBuilder = resultSetBuilder;
        ssje.timestamp = timestamp;
        ssje.userId = userId;
        return ssje;
    }

    public static SemanticSignifierJe create(SemanticSignifier ss) {
        if (ss == null) {
            return null;
        }
        if (ss instanceof SemanticSignifierJe) {
            return SemanticSignifierJe.class.cast(ss);
        }
        return create(ss.getSSId(), ss.getName(), ss.getDescription(), ss.getXSDComputableDefinitions(),
                ss.getEntryPoint(), ss.getExitPoint(), ss.getFactListsBuilder(), ss.getResultSetBuilder(),
                ss.getTimestamp(), ss.getUserId());
    }

    public static List<SemanticSignifierJe> create(List<SemanticSignifier> sses) {
        if (sses == null) {
            return null;
        }
        List<SemanticSignifierJe> ssjes = new ArrayList<>();
        for (SemanticSignifier ss : sses) {
            ssjes.add(create(ss));
        }
        return ssjes;
    }

    @Override
    public SSIdJe getPrimaryKey() {
        return getSSId();
    }

    public SSIdJe getSSId() {
        return ssId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<XSDComputableDefinition> getXSDComputableDefinitions() {
        return Collections.unmodifiableList(xsdComputableDefinitions);
    }

    @Override
    public String getEntryPoint() {
        return entryPoint;
    }

    @Override
    public String getExitPoint() {
        return exitPoint;
    }

    @Override
    public String getFactListsBuilder() {
        return factListsBuilder;
    }

    @Override
    public String getResultSetBuilder() {
        return resultSetBuilder;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

}
