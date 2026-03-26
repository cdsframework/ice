package org.opencds.config.api.service;

import java.io.InputStream;
import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;

public interface SupportingDataService
{
    SupportingData find(String supportingDataId);

    SupportingData find(KMId kmId, String identifier);

    List<SupportingData> find(KMId kmid);

    List<SupportingData> getAll();

    void persist(SupportingData sd);

    @Deprecated
    void delete(KMId kmId, String identifier);

    void delete(String identifier);

    void deleteAll(KMId kmId);

    @Deprecated
    InputStream getSupportingDataPackage(KMId kmId, String supportingDataId);

    InputStream getSupportingDataPackage(String supportingDataId);

    @Deprecated
    boolean packageExists(KMId kmId, String supportingDataId);

    boolean packageExists(String supportingDataId);

    @Deprecated
    void persistSupportingDataPackage(KMId kmId, String identifier, InputStream supportingDataPackage);

    void persistSupportingDataPackage(String identifier, InputStream supportingDataPackage);

    @Deprecated
    void deleteSupportingDataPackage(KMId kmId, String identifier);

    void deleteSupportingDataPackage(String identifier);
}
