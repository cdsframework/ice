package org.opencds.config.api.service;

import java.io.File;
import java.io.InputStream;

import org.opencds.config.api.model.SupportingData;

public interface SupportingDataPackageService
{
    boolean exists(SupportingData supportingData);

    InputStream getPackageInputStream(SupportingData supportingData);

    byte[] getPackageBytes(SupportingData supportingData);

    void persistPackageInputStream(SupportingData sd, InputStream supportingDataPackage);

    void deletePackage(SupportingData sd);

    File getFile(SupportingData sd);
}
