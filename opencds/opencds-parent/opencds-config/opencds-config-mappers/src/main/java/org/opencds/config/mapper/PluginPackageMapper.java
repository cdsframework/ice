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

package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.api.model.impl.PPIdImpl;
import org.opencds.config.api.model.impl.PluginPackageImpl;
import org.opencds.config.schema.PluginLoadContext;
import org.opencds.config.schema.PluginPackageId;
import org.opencds.config.schema.PluginPackages;

public abstract class PluginPackageMapper {

    public static PluginPackage internal(org.opencds.config.schema.PluginPackage external) {
        if (external == null) {
            return null;
        }
        return PluginPackageImpl.create(PPIdImpl.create(external.getIdentifier().getScopingEntityId(), external
                .getIdentifier().getBusinessId(), external.getIdentifier().getVersion()), LoadContext.resolve(external
                .getLoadContext().name()), external.getResourceName(), PluginMapper.internal(external.getPlugins()),
                external.getTimestamp().toGregorianCalendar().getTime(), external.getUserId());
    }

    public static List<PluginPackage> internal(PluginPackages external) {
        if (external == null) {
            return null;
        }
        List<PluginPackage> pps = new ArrayList<>();
        for (org.opencds.config.schema.PluginPackage pp : external.getPluginPackage()) {
            pps.add(internal(pp));
        }
        return pps;
    }

    public static org.opencds.config.schema.PluginPackage external(PluginPackage internal) {
        if (internal == null) {
            return null;
        }

        org.opencds.config.schema.PluginPackage external = new org.opencds.config.schema.PluginPackage();
        PluginPackageId ppid = new PluginPackageId();
        ppid.setScopingEntityId(internal.getIdentifier().getScopingEntityId());
        ppid.setBusinessId(internal.getIdentifier().getBusinessId());
        ppid.setVersion(internal.getIdentifier().getVersion());
        external.setIdentifier(ppid);
        external.setLoadContext(PluginLoadContext.fromValue(internal.getLoadContext().name()));
        external.setResourceName(internal.getResourceName());
        external.setPlugins(PluginMapper.external(internal.getPlugins()));
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());
        return external;
    }

    public static PluginPackages external(List<PluginPackage> internal) {
        if (internal == null) {
            return null;
        }
        PluginPackages external = new PluginPackages();
        for (PluginPackage pp : internal) {
            external.getPluginPackage().add(external(pp));
        }
        return external;
    }
}
