package org.cdsframework.ice.cdsrr.dto;

import java.util.List;

// TODO: look at PlanDefinition to replace Module

public record Module(String id,
                     String name,
                     String description,
                     List<String> versions)
{
}