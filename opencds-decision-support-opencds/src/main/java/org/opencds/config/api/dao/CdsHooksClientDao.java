package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.CdsHooksClient;

public interface CdsHooksClientDao
{
    CdsHooksClient find(String clientId);

    List<CdsHooksClient> getAll();

    void persist(CdsHooksClient hooksClient);

    void persist(List<CdsHooksClient> hooksClients);

    void delete(CdsHooksClient hooksClient);
}
