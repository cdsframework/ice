package org.opencds.config.api.model;

public enum KMStatus {
    /**
     * The KM successfully passed the QA tests.
     * 
     */
    APPROVED,

    /**
     * Can be executed. Precondition: the KM is syntactically valid. When a
     * defined KM changes, a new version should be created.
     * 
     */
    DEFINED,

    /**
     * Draft KM.
     * 
     */
    DRAFT,

    /**
     * The KM can be used in the production environment.
     * 
     */
    PROMOTED,

    /**
     * The KM definition doesn't match the KM specification.
     * 
     */
    REJECTED,

    /**
     * The KM was historically promoted, but should not be used any more.
     * 
     */
    RETIRED;

}
