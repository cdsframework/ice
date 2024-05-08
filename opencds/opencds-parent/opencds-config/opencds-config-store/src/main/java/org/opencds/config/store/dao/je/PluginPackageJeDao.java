/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
