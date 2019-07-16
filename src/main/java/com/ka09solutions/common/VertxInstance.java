package com.ka09solutions.common;

import io.vertx.core.Vertx;

public class VertxInstance
{
    private static final Vertx vertx = Vertx.vertx();

    public static Vertx get()
    {
        return vertx;
    }
}
