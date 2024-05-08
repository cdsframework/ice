
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

package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class SecondaryCDMJe implements SecondaryCDM {

    private CDMIdJe cdmId;
    private SupportMethod supportMethod;

    private SecondaryCDMJe() {
    }

    public static SecondaryCDMJe create(CDMId cdmId, SupportMethod supportMethod) {
        SecondaryCDMJe scdmje = new SecondaryCDMJe();
        scdmje.cdmId = CDMIdJe.create(cdmId);
        scdmje.supportMethod = supportMethod;
        return scdmje;
    }
    
    public static SecondaryCDMJe create(SecondaryCDM scdm) {
        if (scdm == null) {
            return null;
        }
        if (scdm instanceof SecondaryCDMJe) {
            return SecondaryCDMJe.class.cast(scdm);
        }
        return create(scdm.getCDMId(), scdm.getSupportMethod());
    }

    public static List<SecondaryCDM> create(List<SecondaryCDM> secondaryCDMs) {
        if (secondaryCDMs == null) {
            return null;
        }
        List<SecondaryCDM> scdms = new ArrayList<>();
        for (SecondaryCDM scdm : secondaryCDMs) {
            scdms.add(create(scdm));
        }
        return scdms;
    }

    @Override
    public CDMId getCDMId() {
        return cdmId;
    }

    @Override
    public SupportMethod getSupportMethod() {
        return supportMethod;
    }

}
