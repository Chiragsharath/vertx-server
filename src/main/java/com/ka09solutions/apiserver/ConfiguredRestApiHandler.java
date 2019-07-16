package com.ka09solutions.apiserver;

import com.ka09solutions.config.ConfigHolder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredRestApiHandler
{
    private static final String SOURCE_FILE = "file";
    private static final String SOURCE_DB = "db";

    private final static Logger LOG = LogManager.getLogger(ConfiguredRestApiHandler.class);

    public void setup(Router router)
    {
        JsonArray restUrls = (JsonArray) ConfigHolder.getInstance().getConfigValue("resturls");
        if (restUrls.isEmpty()) return;

        for (Object restApiObject : restUrls)
        {
            JsonObject restApi = (JsonObject) restApiObject;
            LOG.info("Configuring url: " + restApi.encodePrettily());
            if (SOURCE_FILE.equals(restApi.getString("source")))
            {
                router.mountSubRouter(restApi.getString("uri"), new ResponseFromFileHandler(restApi.getString("file"),
                        restApi.getString("encoding"), restApi.getString("default")));
            }
            else if (SOURCE_DB.equals(restApi.getString("source")))
            {
                router.mountSubRouter(restApi.getString("uri"), new ResponseFromDBHandler(restApi.getString("query"),
                        restApi.getJsonArray("params"), restApi.getJsonArray("resultfields"), restApi.getString("default"), restApi.getBoolean("singleRecord", false)));
            }
            else
            {
                LOG.error("Api not configured properly. Api:" + restApi.encodePrettily());
            }
        }
        LOG.info("Urls configured.");
    }
}

