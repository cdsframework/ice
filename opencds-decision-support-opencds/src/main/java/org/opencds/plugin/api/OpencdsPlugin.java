package org.opencds.plugin.api;

public interface OpencdsPlugin<CTX extends PluginContext>
{
    void execute(CTX context);
}
