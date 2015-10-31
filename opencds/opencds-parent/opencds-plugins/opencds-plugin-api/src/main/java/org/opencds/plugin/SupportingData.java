package org.opencds.plugin;


public final class SupportingData {
    private final String identifier;
    private final String kmId;
    private final String loadedByPluginId;
    private final String packageId;
    private final String packageType;
    private final byte[] data;

    private SupportingData(String identifier, String kmId, String loadedByPluginId, String packageId, String packageType, byte[] data) {
        this.identifier = identifier;
        this.kmId = kmId;
        this.loadedByPluginId = loadedByPluginId;
        this.packageId = packageId;
        this.packageType = packageType;
        this.data = data;
    }

    public static SupportingData create(String identifier, String kmId, String loadedByPluginId, String packageId, String packageType, byte[] data) {
        return new SupportingData(identifier, kmId, loadedByPluginId, packageId, packageType, data);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getKmId() {
        return kmId;
    }
    
    public String getLoadedByPluginId() {
        return loadedByPluginId;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getPackageType() {
        return packageType;
    }

    public byte[] getData() {
        return data;
    }

}
