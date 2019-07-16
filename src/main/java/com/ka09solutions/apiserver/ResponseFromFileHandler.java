package com.ka09solutions.apiserver;

import com.ka09solutions.common.VertxInstance;
import io.vertx.ext.web.RoutingContext;

public class ResponseFromFileHandler extends AbstractRouteHandler
{
    private String filePath;
    private String encoding = RouteUtil.JSON_TYPE;
    private String defaultContent = "[]";

    public ResponseFromFileHandler(String filePath, String encoding, String defaultContent)
    {
        super(VertxInstance.get());
        this.filePath = filePath;
        this.encoding = encoding;
        this.defaultContent = defaultContent;
        this.get("/").handler(this::serveResponse);
    }

    private void serveResponse(RoutingContext context)
    {
        RouteUtil.getInstance().sendResponseFromFile(context, this.filePath, this.encoding, this.defaultContent);
    }
}
