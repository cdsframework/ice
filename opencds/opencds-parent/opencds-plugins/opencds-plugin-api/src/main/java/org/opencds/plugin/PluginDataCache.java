package org.opencds.plugin;

public interface PluginDataCache {

    <K, V> V get(K key);

    <K, V> void put(K key, V value);

}
