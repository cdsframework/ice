package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SupportingData;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class SupportingDataJe implements SupportingData, ConfigEntity<SDIdJe> {
    @PrimaryKey
    private SDIdJe sdId;
    private String packageType;
    private String packageId;
    private PluginIdJe loadedBy;
    private Date timestamp;
    private String userId;

    private SupportingDataJe() {
    }

    public static SupportingDataJe create(String identifier, KMId kmId, String packageType, String packageId,
            PluginId loadedBy, Date timestamp, String userId) {
        SupportingDataJe sdj = new SupportingDataJe();
        sdj.sdId = SDIdJe.create(KMIdJe.create(kmId), identifier);
        sdj.packageType = packageType;
        sdj.packageId = packageId;
        sdj.loadedBy = PluginIdJe.create(loadedBy);
        sdj.timestamp = timestamp;
        sdj.userId = userId;
        return sdj;
    }

    public static SupportingDataJe create(SupportingData sd) {
        if (sd == null) {
            return null;
        }
        if (sd instanceof SupportingDataJe) {
            return SupportingDataJe.class.cast(sd);
        }
        return create(sd.getIdentifier(), sd.getKMId(), sd.getPackageType(), sd.getPackageId(), sd.getLoadedBy(),
                sd.getTimestamp(), sd.getUserId());
    }

    public static List<SupportingDataJe> create(List<SupportingData> sds) {
        if (sds == null) {
            return null;
        }
        List<SupportingDataJe> sdjs = new ArrayList<>();
        for (SupportingData sd : sds) {
            sdjs.add(create(sd));
        }
        return sdjs;
    }

    @Override
    public SDIdJe getPrimaryKey() {
        return sdId;
    }

    @Override
    public String getIdentifier() {
        return sdId.getIdentifier();
    }

    @Override
    public KMId getKMId() {
        return sdId.getKMId();
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
