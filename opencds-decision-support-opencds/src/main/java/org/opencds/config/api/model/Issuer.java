package org.opencds.config.api.model;

public interface Issuer
{
    String iss();

    String jku();

    String jwk();

    AccessType accessType();
}
