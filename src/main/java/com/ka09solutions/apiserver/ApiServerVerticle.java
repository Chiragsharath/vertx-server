package com.ka09solutions.apiserver;


import com.ka09solutions.common.VertxInstance;
import com.ka09solutions.config.ConfigHolder;
import com.ka09solutions.session.SessionApiHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ApiServerVerticle extends AbstractVerticle
{
    private final static Logger LOG = LogManager.getLogger(ApiServerVerticle.class);

    public static void main(String[] args)
    {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ApiServerVerticle.class.getCanonicalName(), new DeploymentOptions().setInstances(4));
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception
    {
        this.setupHttpServer(); //todo: setup SSL server
        startFuture.complete();
    }

    private void setupHttpServer()
    {
        Router router = Router.router(VertxInstance.get());

        this.setupApiHandler(router);
        this.setupStaticHandler(router);

        int port = ConfigHolder.getInstance().getInteger("http_port", 8080);
        HttpServerOptions options = this.getHttpServerOptions();
        VertxInstance.get().createHttpServer(options).requestHandler(router::accept).listen(port);
        LOG.info("Starting http server on port : " + port);
    }

    private HttpServerOptions getHttpServerOptions()
    {
        return new HttpServerOptions()
                .setCompressionSupported(true)
                .setTcpKeepAlive(true);
    }

    private void setupStaticHandler(Router router)
    {
        router.route(HttpMethod.GET, "/*").handler(StaticHandler.create());
        router.route(HttpMethod.GET, "/*").failureHandler(new RouteFailureHandler());
    }

    private void setupApiHandler(Router router)
    {
        new ConfiguredRestApiHandler().setup(router);
        router.mountSubRouter("/v1/user/session", new SessionApiHandler(VertxInstance.get()));
    }

    private void logHeadersHandler(Router router)
    {
        router.route(HttpMethod.POST, "/api/:address/").handler(routingContext -> {
            LOG.info(routingContext.request().headers().get("content-type") + ":" + routingContext.request().headers().get("content-length"));
            LOG.info("Data In:" + routingContext.getBodyAsJson());
            routingContext.next();
        });
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
    }

}
