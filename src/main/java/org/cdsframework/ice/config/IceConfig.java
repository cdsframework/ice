package org.cdsframework.ice.config;

import java.util.Optional;

import org.springframework.boot.info.GitProperties;
import org.springframework.boot.jackson.autoconfigure.XmlMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;

@Configuration
public class IceConfig
{
    @Bean
    public VersionData versionData(final IceProperties iceProperties, final Optional<GitProperties> gitProperties)
    {
        final VersionData.VersionDataBuilder builder = VersionData.builder();
        builder.iceVersion(iceProperties.getIceVersion());

        gitProperties.ifPresent(gp -> builder.gitCommitSha(gp.get("commit.id.abbrev")).buildDate(gp.get("build.time")));

        return builder.build();
    }

    @Bean
    public JakartaXmlBindAnnotationModule jakartaXmlBindAnnotationModule()
    {
        return new JakartaXmlBindAnnotationModule();
    }

    @Bean
    public XmlMapperBuilderCustomizer xmlMapperBuilderCustomizer()
    {
        return xmlMapperBuilder -> xmlMapperBuilder.defaultUseWrapper(false).build();
    }
}
