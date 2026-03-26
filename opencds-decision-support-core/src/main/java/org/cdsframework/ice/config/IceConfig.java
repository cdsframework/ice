package org.cdsframework.ice.config;

import java.util.Optional;

import org.cdsframework.ice.service.VersionData;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.jackson.autoconfigure.XmlMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;

@Slf4j
@Configuration
public class IceConfig
{
    public IceConfig(final IceProperties iceProperties)
    {
        log.info("{}", iceProperties);
    }

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
