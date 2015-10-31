package org.opencds.config.api.model.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.XSDComputableDefinition;

public class SemanticSignifierImpl implements SemanticSignifier {
    private SSId ssId;
    private String name;
    private String description;
    private List<XSDComputableDefinition> xsdComputableDefinitions;
    private String entryPoint;
    private String exitPoint;
    private String factListsBuilder;
    private String resultSetBuilder;
    private Date timestamp;
    private String userId;

    private SemanticSignifierImpl() {
    }

    public static SemanticSignifierImpl create(SSId ssId, String name, String description,
            List<XSDComputableDefinition> xsdComputableDefinitions, String entryPoint, String exitPoint,
            String factListsBuilder, String resultSetBuilder, Date timestamp, String userId) {
        SemanticSignifierImpl ssi = new SemanticSignifierImpl();
        ssi.ssId = SSIdImpl.create(ssId);
        ssi.name = name;
        ssi.description = description;
        ssi.xsdComputableDefinitions = XSDComputableDefinitionImpl.create(xsdComputableDefinitions);
        ssi.entryPoint = entryPoint;
        ssi.exitPoint = exitPoint;
        ssi.factListsBuilder = factListsBuilder;
        ssi.resultSetBuilder = resultSetBuilder;
        ssi.timestamp = timestamp;
        ssi.userId = userId;
        return ssi;
    }

    public static SemanticSignifierImpl create(SemanticSignifier ss) {
        if (ss == null) {
            return null;
        }
        if (ss instanceof SemanticSignifierImpl) {
            return SemanticSignifierImpl.class.cast(ss);
        }
        return create(ss.getSSId(), ss.getName(), ss.getDescription(), ss.getXSDComputableDefinitions(),
                ss.getEntryPoint(), ss.getExitPoint(), ss.getFactListsBuilder(), ss.getResultSetBuilder(),
                ss.getTimestamp(), ss.getUserId());
    }

    @Override
    public SSId getSSId() {
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(743, 1049).append(ssId).append(name).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SemanticSignifierImpl rhs = (SemanticSignifierImpl) obj;
        return new EqualsBuilder().append(ssId, rhs.ssId).append(name, rhs.name).isEquals();
    }

}
