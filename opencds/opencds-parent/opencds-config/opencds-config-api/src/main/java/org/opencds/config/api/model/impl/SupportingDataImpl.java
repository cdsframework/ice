package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SupportingData;

public class SupportingDataImpl implements SupportingData {
    private String identifier;
    private KMId kmId;
    private String packageType;
    private String packageId;
    private PluginId loadedBy;
    private Date timestamp;
    private String userId;

    private SupportingDataImpl() {
    }

    public static SupportingDataImpl create(String identifier, KMId kmId, String packageType, String packageId,
            PluginId loadedBy, Date timestamp, String userId) {
        SupportingDataImpl sdi = new SupportingDataImpl();
        sdi.identifier = identifier;
        sdi.kmId = KMIdImpl.create(kmId);
        sdi.packageType = packageType;
        sdi.packageId = packageId;
        sdi.loadedBy = loadedBy;
        sdi.timestamp = timestamp;
        sdi.userId = userId;
        return sdi;
    }

    public static SupportingDataImpl create(SupportingData sd) {
        if (sd == null) {
            return null;
        }
        if (sd instanceof SupportingDataImpl) {
            return SupportingDataImpl.class.cast(sd);
        }
        return create(sd.getIdentifier(), sd.getKMId(), sd.getPackageType(), sd.getPackageId(), sd.getLoadedBy(),
                sd.getTimestamp(), sd.getUserId());
    }
    
    public static List<SupportingDataImpl> create(List<SupportingData> sds) {
        if (sds == null) {
            return null;
        }
        List<SupportingDataImpl> sdis = new ArrayList<>();
        for (SupportingData sd : sds) {
            sdis.add(create(sd));
        }
        return sdis;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public KMId getKMId() {
        return kmId;
    }

    @Override
    public String getPackageType() {
        return packageType;
    }

    @Override
    public String getPackageId() {
        return packageId;
    }

    @Override
    public PluginId getLoadedBy() {
        return loadedBy;
    }
    
    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

}
