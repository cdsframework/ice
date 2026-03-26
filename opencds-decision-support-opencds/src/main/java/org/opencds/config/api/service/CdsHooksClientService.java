package org.opencds.config.api.service;

import java.util.List;
import java.util.function.Predicate;

import org.opencds.config.api.model.CdsHooksClient;

public interface CdsHooksClientService
{
    CdsHooksClient find(String clientId);

    List<CdsHooksClient> getAll();

    List<CdsHooksClient> getAll(Predicate<CdsHooksClient> predicate);

    void persist(CdsHooksClient hooksClient);

    void persist(List<CdsHooksClient> hooksClients);

    void delete(String clientId);
}
