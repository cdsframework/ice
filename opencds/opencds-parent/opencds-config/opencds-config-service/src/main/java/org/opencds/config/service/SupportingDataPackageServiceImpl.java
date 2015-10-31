package org.opencds.config.service;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.StreamCacheElement;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.service.SupportingDataPackageService;

public class SupportingDataPackageServiceImpl implements SupportingDataPackageService {
    private static final Log log = LogFactory.getLog(SupportingDataPackageServiceImpl.class);

    private final FileDao fileDao;

    private CacheService cacheService;

    public SupportingDataPackageServiceImpl(FileDao fileDao, CacheService cacheService) {
        this.fileDao = fileDao;
        this.cacheService = cacheService;
    }

    @Override
    public boolean exists(SupportingData supportingData) {
        CacheElement cacheElement = fileDao.find(supportingData.getIdentifier());
        if (cacheElement == null) {
            return false;
        }
        return cacheElement.exists();
    }

    @Override
    public InputStream getPackageInputStream(SupportingData supportingData) {
        CacheElement cacheElement = fileDao.find(supportingData.getIdentifier());
        if (cacheElement == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = cacheElement.getInputStream();
        } catch (IOException e) {
            log.error("Cannot resolve package: " + supportingData.getPackageId());
        }
        return is;
    }

    @Override
    public byte[] getPackageBytes(SupportingData supportingData) {
        CacheElement cacheElement = fileDao.find(supportingData.getPackageId());
        byte[] bytes = cacheService.get(SDPackageCacheRegion.SD_PACKAGE_BYTES, cacheElement);
        if (bytes == null || bytes.length == 0) {
            try (InputStream is = cacheElement.getInputStream()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] input = new byte[1024];
                while (is.read(input) != -1) {
                    baos.write(input);
                }
                bytes = baos.toByteArray();
            } catch (IOException e) {
                log.error("Error reading CacheElement: " + cacheElement, e);
            }
        }
        return bytes;
    }

    @Override
    public void persistPackageInputStream(SupportingData sd, InputStream supportingDataPackage) {
        String packageId = sd.getPackageId();
        if (packageId == null) {
            packageId = UUID.randomUUID().toString(); // TODO: Is this what I
                                                      // want?
        }
        CacheElement cacheElement = StreamCacheElement.create(packageId, supportingDataPackage);
        fileDao.persist(cacheElement);
    }

    // TODO: Error handling/reporting?
    @Override
    public void deletePackage(SupportingData sd) {
        String packageId = sd.getPackageId();
        if (packageId != null) {
            CacheElement cacheElement = fileDao.find(packageId);
            if (cacheElement != null) {
                fileDao.delete(cacheElement);
                cacheService.evict(SDPackageCacheRegion.SD_PACKAGE_BYTES, cacheElement);
            }
        }
    }

    private static enum SDPackageCacheRegion implements CacheRegion {
        SD_PACKAGE_BYTES(byte[].class);

        private Class<?> type;

        SDPackageCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }

    }

}
