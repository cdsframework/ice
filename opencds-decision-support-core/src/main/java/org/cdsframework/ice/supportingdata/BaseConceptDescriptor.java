package org.cdsframework.ice.supportingdata;

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
