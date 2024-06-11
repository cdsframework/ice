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

package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.CDMId;

public class CDMIdImpl implements CDMId {
    private String codeSystem;
    private String code;
    private String version;

    private CDMIdImpl() {
    }

    public static CDMIdImpl create(String codeSystem, String code, String version) {
        CDMIdImpl cdmid = new CDMIdImpl();
        cdmid.code = code;
        cdmid.codeSystem = codeSystem;
        cdmid.version = version;
        return cdmid;
    }

    public static CDMIdImpl create(CDMId primaryCDM) {
        if (primaryCDM == null) {
            return null;
        }
        if (primaryCDM instanceof CDMIdImpl) {
            return CDMIdImpl.class.cast(primaryCDM);
        }
        return create(primaryCDM.getCodeSystem(), primaryCDM.getCode(), primaryCDM.getVersion());
    }

    @Override
    public String getCodeSystem() {
        return codeSystem;
    }
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return "CDMIdImpl [codeSystem= " + codeSystem + ", code= " + code + ", version= " + version + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(2017, 2341)
        .append(codeSystem)
        .append(code)
        .append(version)
        .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
          return false;
        }
        CDMIdImpl rhs = (CDMIdImpl) obj;
        return new EqualsBuilder()
        .append(codeSystem, rhs.codeSystem)
        .append(code, rhs.code)
        .append(version, rhs.version)
        .isEquals();
    }
    

}
