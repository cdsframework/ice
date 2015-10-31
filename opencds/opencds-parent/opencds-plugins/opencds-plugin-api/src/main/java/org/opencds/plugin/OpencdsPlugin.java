package org.opencds.plugin;

public interface OpencdsPlugin<CTX extends PluginContext> {
    void execute(CTX context);
}
