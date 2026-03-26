package org.opencds.config.api;

public record ConfigData(String configType,
                         String configPath)
{
    public static ConfigData create(final String knowledgeRepoType, final String knowledgeRepoPath)
    {
        return new ConfigData(knowledgeRepoType, knowledgeRepoPath);
    }
}
