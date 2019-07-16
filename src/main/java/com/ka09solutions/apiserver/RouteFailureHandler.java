package com.ka09solutions.apiserver;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class RouteFailureHandler implements Handler<RoutingContext>
{

    @Override
    public void handle(RoutingContext context) {

        int statusCode = context.statusCode();
        HttpServerResponse response = context.response();
        String uri = context.request().uri();

        if (statusCode == 404 && !uri.contains(".")) {
            RouteUtil.getInstance().sendResponseFromFile(context, "webroot/index.html", RouteUtil.TEXT_HTML_TYPE);
        } else {
            if (statusCode <= 0) statusCode = HttpResponseStatus.NOT_FOUND.code();
            response.setStatusCode(statusCode).end("Sorry we have encountered an Error. Please refresh the page or go to <a href='#'>Home</a> to continue.");
        }

    }
}
