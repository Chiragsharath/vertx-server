package com.ka09solutions.config;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceDef
{
    private static final Logger LOG = LogManager.getLogger(ServiceDef.class);

    private JsonObject config;

    public ServiceDef(JsonObject config)
    {
        this.config = config;
    }

    public boolean isWorker()
    {
        return this.config.getBoolean("worker", true);
    }

    public int numberOfInstances()
    {
        return this.config.getInteger("instances", 1);
    }

    public Class getType()
    {
        String name = this.config.getString("name");
        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            LOG.error("Class not found for : " + name, e);
            return null;
        }
    }

    public DeploymentOptions getDeploymentOptions()
    {
        DeploymentOptions options = new DeploymentOptions();
        options.setWorker(this.isWorker());
        options.setInstances(this.numberOfInstances());
        return options;
    }

    @Override
    public String toString()
    {
        return "ServiceDef - " + config.encodePrettily();
    }
}
