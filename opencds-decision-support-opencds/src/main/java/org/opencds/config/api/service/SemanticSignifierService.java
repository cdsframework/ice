package org.opencds.config.api.service;

import java.util.List;

import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.ss.EntryPoint;
import org.opencds.config.api.ss.ExitPoint;

public interface SemanticSignifierService
{
    SemanticSignifier find(SSId ssId);

    List<SemanticSignifier> getAll();

    void persist(SemanticSignifier ss);

    void persist(List<SemanticSignifier> sses);

    void delete(SSId ssId);

    <T, EP extends EntryPoint<T>> EP getEntryPoint(SSId ssId);

    ExitPoint getExitPoint(SSId ssId);

    FactListsBuilder getFactListsBuilder(SSId ssId);

    <T, RSB extends ResultSetBuilder<T>> RSB getResultSetBuilder(SSId ssId);
}
