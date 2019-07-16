package com.ka09solutions.config;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

public class ServicesConfig
{
    private JsonObject config;

    public ServicesConfig(JsonObject config)
    {
        this.config = config;
    }

    public List<ServiceDef> getParallel()
    {
        return this.getServices("parallel");
    }

    public Deque<ServiceDef> getSequential()
    {
        return this.getServicesAsQueue("sequential");
    }

    private List<ServiceDef> getServices(String tag)
    {
        if (!this.config.containsKey(tag)) return Collections.emptyList();

        JsonArray jsonList = this.config.getJsonArray(tag);
        List<ServiceDef> services = new ArrayList<>();
        for (Object json : jsonList)
        {
            services.add(new ServiceDef((JsonObject) json));
        }
        return services;
    }

    private Deque<ServiceDef> getServicesAsQueue(String tag)
    {
        if (!this.config.containsKey(tag)) return new ArrayDeque<>();

        JsonArray jsonList = this.config.getJsonArray(tag);
        int size = jsonList.size();
        Deque<ServiceDef> services = new ArrayDeque<>();
        for (int i = (size - 1); i>=0; i--)
        {
            services.add(new ServiceDef(jsonList.getJsonObject(i)));
        }
        return services;
    }
}
