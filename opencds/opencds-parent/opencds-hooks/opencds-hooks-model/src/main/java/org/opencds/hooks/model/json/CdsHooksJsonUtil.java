package org.opencds.hooks.model.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.hooks.lib.json.JsonUtil;
import org.opencds.hooks.model.Extension;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.context.ReadOnlyHookContext;
import org.opencds.hooks.model.context.WritableHookContext;
import org.opencds.hooks.model.context.support.HookContextJsonSupport;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.request.ReadOnlyCdsRequest;
import org.opencds.hooks.model.request.WritableCdsRequest;
import org.opencds.hooks.model.request.support.CdsRequestJsonSupport;
import org.opencds.hooks.model.response.ActionType;
import org.opencds.hooks.model.response.Indicator;
import org.opencds.hooks.model.response.support.ActionTypeJsonSupport;
import org.opencds.hooks.model.response.support.IndicatorJsonSupport;
import org.opencds.hooks.model.support.ExtensionJsonSupport;

public class CdsHooksJsonUtil implements JsonUtil {
    private final GsonBuilder gsonBuilder;
    private Gson gson;

    public CdsHooksJsonUtil() {
        this(new GsonBuilder());
        buildGson();
    }

    public CdsHooksJsonUtil(GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;

        // CdsRequest
        gsonBuilder.registerTypeAdapter(CdsRequest.class, new CdsRequestJsonSupport());
        gsonBuilder.registerTypeAdapter(ReadOnlyCdsRequest.class, new CdsRequestJsonSupport());
        gsonBuilder.registerTypeAdapter(WritableCdsRequest.class, new CdsRequestJsonSupport());
        gsonBuilder.registerTypeAdapter(HookContext.class, new HookContextJsonSupport());
        gsonBuilder.registerTypeAdapter(ReadOnlyHookContext.class, new HookContextJsonSupport());
        gsonBuilder.registerTypeAdapter(WritableHookContext.class, new HookContextJsonSupport());

        // CdsResponse
        gsonBuilder.registerTypeAdapter(ActionType.class, new ActionTypeJsonSupport());
        gsonBuilder.registerTypeAdapter(Indicator.class, new IndicatorJsonSupport());
        gsonBuilder.registerTypeAdapter(Extension.class, new ExtensionJsonSupport());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    protected GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }

    protected void buildGson() {
        gson = gsonBuilder.create();
    }

    @Override
    public Gson getGson() {
        if (gson == null) {
            throw new OpenCDSRuntimeException("gson not created");
        }
        return gson;
    }
}
