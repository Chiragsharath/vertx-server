package com.ka09solutions.session;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.ka09solutions.apiserver.AbstractRouteHandler;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sunil on 03-07-2018.
 */
public class SessionApiHandler extends AbstractRouteHandler {

    public SessionApiHandler(Vertx vertx) {
        super(vertx);
        this.route().handler(BodyHandler.create());
        this.post("/sessionLogin").handler(this::sessionLogin);
        this.post("/sessionLogout").handler(this::sessionLogout);
    }

    private void sessionLogin(RoutingContext context)
    {
        JsonObject params = context.getBodyAsJson();
        String idToken = params.getString("idToken"); //idToken from firebase authentication
        long expiresIn = TimeUnit.DAYS.toMillis(5); //5 days expiry for cookie
        SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();
        try {
            // Create the session cookie. This will also verify the ID token in the process.
            // The session cookie will have the same claims as the ID token.
            String sessionCookieValue = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
            context.addCookie(Cookie.cookie("session", sessionCookieValue).setPath("/"));
        } catch (FirebaseAuthException e) {
            context.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).end("Failed to create a session cookie");
        }
    }

    private void sessionLogout(RoutingContext context)
    {
        String sessionCookieValue = context.getCookie("session").getValue();
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookieValue);
            FirebaseAuth.getInstance().revokeRefreshTokens(decodedToken.getUid());
        } catch (FirebaseAuthException e) {
        }
        context.addCookie(Cookie.cookie("session", sessionCookieValue).setPath("/").setMaxAge(0));
        sendJsonResponse(context, "User logged out");
    }
}
