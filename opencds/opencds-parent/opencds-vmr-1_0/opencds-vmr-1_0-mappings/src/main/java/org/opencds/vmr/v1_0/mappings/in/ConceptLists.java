/**
 * Copyright 2011, 2012 OpenCDS.org
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Collection of ConceptLists for input to Drools, moved to this class from CdsInputFactListsBuilder.java
 * 
 * @author David Shields
 *
 */
public class ConceptLists {
    private final ConcurrentMap<Class<?>, List<?>> conceptListMap = new ConcurrentHashMap<>();
    
    public <C> List<C> get(Class<C> clazz) {
        return (List<C>) conceptListMap.get(clazz);
    }
    
    @SuppressWarnings("unchecked")
    public <C> void put(Class<C> clazz, C c) {
        ensureInitialized(clazz);
        ((List<C>)conceptListMap.get(clazz)).add(c);
    }
    
	private <C> void ensureInitialized(Class<C> clazz) {
        conceptListMap.putIfAbsent(clazz, new ArrayList<C>());
    }
	
	public Iterable<Map.Entry<Class<?>, List<?>>> iterable() {
	    return conceptListMap.entrySet();
	}
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder("ConceptLists:\n");
	    for (Entry<Class<?>, List<?>> entry : conceptListMap.entrySet()) {
	        sb.append("\t" + entry.getKey().getName() + "\n");
	        for (Object value : entry.getValue()) {
	            sb.append("\t\t" + value + "\n");
	        }
	    }
	    return sb.toString();
	}

}
