package org.opencds.plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginContext {

    private PluginContext() {
    }

    public static PreProcessPluginContext createPreProcessPluginContext(Map<Class<?>, List<?>> allFactLists,
            Map<String, Object> namedObjects, Map<String, Object> globals, Map<String, SupportingData> supportingData,
            PluginDataCache cache) {
        return PreProcessPluginContext.create(allFactLists, namedObjects, globals, supportingData, cache);
    }

    public static PostProcessPluginContext createPostProcessPluginContext(Map<Class<?>, List<?>> allFactLists,
            Map<String, Object> namedObjects, Set<String> assertions, Map<String, List<?>> resultFactLists,
            Map<String, SupportingData> supportingData, PluginDataCache cache) {
        return PostProcessPluginContext.create(allFactLists, namedObjects, assertions, resultFactLists, supportingData,
                cache);
    }

    public static final class PreProcessPluginContext extends PluginContext {
        private final Map<Class<?>, List<?>> allFactLists;
        private final Map<String, Object> namedObjects;
        private final Map<String, Object> globals;
        private final Map<String, SupportingData> supportingData;
        private final PluginDataCache cache;

        private PreProcessPluginContext(Map<Class<?>, List<?>> allFactLists, Map<String, Object> namedObjects,
                Map<String, Object> globals, Map<String, SupportingData> supportingData, PluginDataCache cache) {
            this.allFactLists = allFactLists;
            this.namedObjects = namedObjects;
            this.globals = globals;
            this.supportingData = supportingData;
            this.cache = cache;
        }

        public static PreProcessPluginContext create(Map<Class<?>, List<?>> allFactLists,
                Map<String, Object> namedObjects, Map<String, Object> globals,
                Map<String, SupportingData> supportingData, PluginDataCache cache) {
            return new PreProcessPluginContext(allFactLists, namedObjects, globals, supportingData, cache);
        }

        public Map<Class<?>, List<?>> getAllFactLists() {
            return allFactLists;
        }

        public Map<String, Object> getNamedObjects() {
            return namedObjects;
        }

        public Map<String, Object> getGlobals() {
            return globals;
        }

        public Map<String, SupportingData> getSupportingData() {
            return supportingData;
        }

        public PluginDataCache getCache() {
            return cache;
        }
    }

    public static final class PostProcessPluginContext extends PluginContext {

        private final Set<String> assertions;
        private final Map<String, List<?>> resultFactLists;
        private final Map<String, SupportingData> supportingData;
        private final PluginDataCache cache;
        private Map<String, Object> namedObjects;
        private Map<Class<?>, List<?>> allFactLists;

        public PostProcessPluginContext(Map<Class<?>, List<?>> allFactLists, Set<String> assertions,
                Map<String, List<?>> resultFactLists, Map<String, Object> namedObjects,
                Map<String, SupportingData> supportingData, PluginDataCache cache) {
            this.allFactLists = allFactLists;
            this.assertions = assertions;
            this.resultFactLists = resultFactLists;
            this.namedObjects = namedObjects;
            this.supportingData = supportingData;
            this.cache = cache;
        }

        public static PostProcessPluginContext create(Map<Class<?>, List<?>> allFactLists, Map<String, Object> namedObjects,
                Set<String> assertions, Map<String, List<?>> resultFactLists,
                Map<String, SupportingData> supportingData, PluginDataCache cache) {
            return new PostProcessPluginContext(allFactLists, assertions, resultFactLists, namedObjects,
                    supportingData, cache);
        }

        public Map<Class<?>, List<?>> getAllFactLists() {
            return allFactLists;
        }

        public Set<String> getAssertions() {
            return assertions;
        }

        public Map<String, List<?>> getResultFactLists() {
            return resultFactLists;
        }

        public Map<String, Object> getNamedObjects() {
            return namedObjects;
        }

        public PluginDataCache getCache() {
            return cache;
        }

        public Map<String, SupportingData> getSupportingData() {
            return supportingData;
        }
    }

}
