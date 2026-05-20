package org.opencds.config.mapper;

import java.util.List;

import org.opencds.config.api.model.CdsHooksClient;
import org.opencds.config.schema.CDSHooksClient;
import org.opencds.config.schema.CdsHooksClients;

public class CdsHooksClientMapper
{
    public static List<CdsHooksClient> internal(final CdsHooksClients clients)
    {
        if (clients == null)
            return null;

        return clients.getCdsHooksClient().stream().map(CdsHooksClientMapper::internal).toList();
    }

    public static CdsHooksClient internal(final CDSHooksClient hooksClient)
    {
        if (hooksClient == null)
            return null;

        return new CdsHooksClient(hooksClient.getId(), hooksClient.getDescription(),
                IssuersMapper.internal(hooksClient.getIssuers()), hooksClient.getTenant());
    }

    public static CDSHooksClient external(final CdsHooksClient hooksClient)
    {
        if (hooksClient == null)
            return null;

        final var cdsHooksClient = new CDSHooksClient();
        cdsHooksClient.setId(hooksClient.id());
        cdsHooksClient.setDescription(hooksClient.description());
        cdsHooksClient.setIssuers(IssuersMapper.external(hooksClient.issuers()));
        cdsHooksClient.setTenant(hooksClient.tenant());
        return cdsHooksClient;
    }

    public static CdsHooksClients external(final List<CdsHooksClient> hooksClients)
    {
        if (hooksClients == null)
            return null;

        final var clients = new CdsHooksClients();
        hooksClients.stream().map(CdsHooksClientMapper::external).forEach(e -> clients.getCdsHooksClient().add(e));
        return clients;
    }
}
