package com.ka09solutions.apiserver;

import com.ka09solutions.common.StringUtils;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class CacheHandler implements Handler<RoutingContext>
{
    private static final CacheHandler INSTANCE = new CacheHandler();
    private final static Logger LOG = LogManager.getLogger(CacheHandler.class);
    private final ConcurrentHashMap<RequestKey, ApiResponse> dataCache = new ConcurrentHashMap<>(256);

    private CacheHandler()
    {

    }

    public static CacheHandler getInstance()
    {
        return INSTANCE;
    }

    public void cache(RoutingContext context, String type, String text)
    {
        RequestKey key = new RequestKey(context.request().uri(), context.request().params());
        if (!this.dataCache.containsKey(key))
        {
            this.dataCache.put(key, new ApiResponse(type, text));
        }
    }

    @Override
    public void handle(RoutingContext context)
    {
        String uri = context.request().uri();
        if (StringUtils.isNonEmpty(uri))
        {
            if (uri.equals("/api/cache.clear"))
            {
                this.dataCache.clear();
                RouteUtil.getInstance().sendResponse(context, "Cache cleared", RouteUtil.TEXT_HTML_TYPE);
                return;
            }

            RequestKey key = new RequestKey(uri, context.request().params());
            ApiResponse response = this.dataCache.get(key);
            if (response != null)
            {
                RouteUtil.getInstance().sendResponse(context, response.getResponse(), response.getEncodingType());
                return;
            }
        }
        context.next();
    }
}
