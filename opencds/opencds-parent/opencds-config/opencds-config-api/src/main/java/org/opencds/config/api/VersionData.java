package org.opencds.config.api;

public class VersionData
{
    private final String iceVersion;
    private final String gitCommitSha;
    private final String buildDate;

    public VersionData(String iceVersion, String gitCommitSha, String buildDate)
    {
        this.iceVersion = iceVersion;
        this.gitCommitSha = gitCommitSha;
        this.buildDate = buildDate;
    }

    public String getIceVersion()
    {
        return iceVersion;
    }

    public String getGitCommitSha()
    {
        return gitCommitSha;
    }

    public String getBuildDate()
    {
        return buildDate;
    }
}
