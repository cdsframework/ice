/**
 * Copyright 2011 - 2013 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.vmr.v1_0.mappings.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Collection of FactLists for input to Drools, moved to this class from CdsInputFactListsBuilder.java
 * 
 * @author David Shields
 *
 */
public class FactLists {
    private final ConcurrentMap<Class<?>, List<?>> factListMap = new ConcurrentHashMap<>();
   
    @SuppressWarnings("unchecked")
    public <F> void put(Class<F> clazz, List<F> list) {
        if (list != null) {
            for (F f : list) {
                ensureInitialized(clazz);
                ((List<F>)factListMap.get(clazz)).add(f);
            }
        }
    }

    private <F> void ensureInitialized(Class<F> clazz) {
        if (factListMap.get(clazz) == null) {
            factListMap.putIfAbsent(clazz, new ArrayList<F>());
        }
    }

    @SuppressWarnings("unchecked")
    public <F> void put(Class<F> clazz, F fact) {
        ensureInitialized(clazz);
        ((List<F>)factListMap.get(clazz)).add(fact);
    }

    @SuppressWarnings("unchecked")
    public <F> List<F> get(Class<F> clazz) {
        return (List<F>) factListMap.get(clazz);
    }
    
    public void populateAllFactLists(Map<Class<?>, List<?>> allFactLists) {
        for (Entry<Class<?>, List<?>> entry : factListMap.entrySet()) {
            allFactLists.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FactLists:\n");
        for (Entry<Class<?>, List<?>> entry : factListMap.entrySet()) {
            sb.append("\t" + entry.getKey().getName() + "\n");
            for (Object value : entry.getValue()) {
                sb.append("\t\t" + value + "\n");
            }
        }
        return sb.toString();
    }

}
