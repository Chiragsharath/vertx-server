package com.ka09solutions.apiserver;

import com.ka09solutions.session.SessionUser;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.impl.RouterImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractRouteHandler extends RouterImpl
{
    private final static Logger LOG = LogManager.getLogger(AbstractRouteHandler.class);

    private RouteUtil routeUtil = RouteUtil.getInstance();
    protected Vertx vertx;

    public AbstractRouteHandler(Vertx vertx)
    {
        super(vertx);
        this.vertx = vertx;
        this.route().handler(CorsHandler.create(".+")
                .maxAgeSeconds(600)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type")
                .allowedHeader("Accept")
                .allowedHeader("Accept-Language")
                .allowedHeader("Authorization"));
    }

    protected void setSessionUser(RoutingContext context, SessionUser user)
    {
        context.put("session.user", user);
    }

    protected SessionUser getSessionUser(RoutingContext context)
    {
        return (SessionUser) context.get("session.user");
    }

    protected void sendError(HttpServerResponse response, String message)
    {
        routeUtil.sendError(response, message);
    }

    protected void sendError(RoutingContext context, String message)
    {
        routeUtil.sendError(context, message);
    }

    protected void sendJsonResponseFromFile(RoutingContext context, String filePath)
    {
        routeUtil.sendJsonResponseFromFile(context, filePath);
    }

    protected void sendJsonResponseFromFile(RoutingContext context, String filePath, String defaultContent)
    {
        routeUtil.sendJsonResponseFromFile(context, filePath, defaultContent);
    }

    protected void sendJsonResponse(RoutingContext context, String json)
    {
        routeUtil.sendJsonResponse(context, json);
    }

}
