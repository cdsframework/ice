/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Originally from org.drools.testcoverage.common.listener
 *
 * Modifications Copyright 2017-2020 OpenCDS.org
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

package org.opencds.service.drools.v7;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;

import java.util.Map;

/**
 * Logs rule firings in a session.
 *
 */
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Log log = LogFactory.getLog(TrackingAgendaEventListener.class);

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();

        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

        if (ruleMetaDataMap.size() > 0) {
            sb.append("\n  With [");
            sb.append(ruleMetaDataMap.size());
            sb.append("] meta-data:");
            for (String key : ruleMetaDataMap.keySet()) {
                sb.append("\n    key=");
                sb.append(key);
                sb.append(", value=");
                sb.append(ruleMetaDataMap.get(key));
            }
        }

        log.info(sb.toString());
    }

}
