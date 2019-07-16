package com.ka09solutions.session;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.ka09solutions.apiserver.AbstractRouteHandler;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by test on 03-07-2018.
 */
public class SessionChecker extends AbstractRouteHandler {

    public SessionChecker(Vertx vertx) {
        super(vertx);
    }

    //todo: this is done only for APIs that need this check
    private void checkSession(RoutingContext context) {
        Cookie sessionCookie = context.getCookie("session");
        if (sessionCookie != null)
        {
            String sessionCookieValue = sessionCookie.getValue();
            try {
                // Verify the session cookie. In this case an additional check is added to detect
                // if the user's Firebase session was revoked, user deleted/disabled, etc.
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookieValue, true);
                setSessionUser(context, new SessionUser(decodedToken));
            } catch (FirebaseAuthException e) {
                context.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).end("Session cookie not available");
            }
        }
        else
        {
            context.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).end("Session cookie not available");
        }
    }


}
