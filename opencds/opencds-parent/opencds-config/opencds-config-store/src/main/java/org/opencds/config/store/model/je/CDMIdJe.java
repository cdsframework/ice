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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.CDMId;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class CDMIdJe implements CDMId {
    @KeyField(1)
    private String codeSystem;

    @KeyField(2)
    private String code;
    
    @KeyField(3)
    private String version;

    private CDMIdJe() {
    }

    public static CDMIdJe create(String codeSystem, String code, String version) {
        CDMIdJe cdmid = new CDMIdJe();
        cdmid.code = code;
        cdmid.codeSystem = codeSystem;
        cdmid.version = version;
        return cdmid;
    }

    public static CDMIdJe create(CDMId cdmid) {
        if (cdmid == null) {
            return null;
        }
        if (cdmid instanceof CDMIdJe) {
            return CDMIdJe.class.cast(cdmid);
        }
        return create(cdmid.getCodeSystem(), cdmid.getCode(), cdmid.getVersion());
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCodeSystem() {
        return codeSystem;
    }

    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return "CDMIdJe [codeSystem= " + codeSystem + ", code= " + code + ", version= " + version
                + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7351, 2731)
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
        CDMIdJe rhs = (CDMIdJe) obj;
        return new EqualsBuilder()
        .append(codeSystem, rhs.codeSystem)
        .append(code, rhs.code)
        .append(version, rhs.version)
        .isEquals();
    }

}
