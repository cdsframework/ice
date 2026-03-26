package org.cdsframework.ice.config.iceSupportingProperties;

public interface BaseConceptDescriptor
{
    String code();

    String codeSystem();

    String codeSystemName();

    String displayName();

    default String originalText()
    {
        return null;
    }
}
