package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.api.model.impl.PluginPackageImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.PPIdJe;
import org.opencds.config.store.model.je.PluginPackageJe;

public class PluginPackageJeDao extends GenericJeDao<PPIdJe, PluginPackageJe> implements PluginPackageDao {

    public PluginPackageJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(PPIdJe.class, PluginPackageJe.class));
    }

    @Override
    public PluginPackage find(PPId ppId) {
        return PluginPackageImpl.create(findCE(PPIdJe.create(ppId)));
    }

    @Override
    public List<PluginPackage> getAll() {
        List<PluginPackageJe> ppjs = getAllCE();
        List<PluginPackage> pps = new ArrayList<>();
        for (PluginPackageJe ppj : ppjs) {
            pps.add(PluginPackageImpl.create(ppj));
        }
        return pps;
    }

    @Override
    public void persist(PluginPackage pluginPackage) {
        persist(PluginPackageJe.create(pluginPackage));
    }

    @Override
    public void persist(List<PluginPackage> pluginPackages) {
        persistAllCE(PluginPackageJe.create(pluginPackages));
    }
    
    @Override
    public void delete(PluginPackage pluginPackage) {
        delete(PluginPackageJe.create(pluginPackage));
    }

}
