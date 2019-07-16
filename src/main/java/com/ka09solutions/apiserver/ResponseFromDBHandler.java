package com.ka09solutions.apiserver;

import com.ka09solutions.common.LocalCache;
import com.ka09solutions.common.VertxInstance;
import com.ka09solutions.db.DatabaseService;
import com.ka09solutions.db.QueryData;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResponseFromDBHandler extends AbstractRouteHandler
{
    private final static Logger LOG = LogManager.getLogger(ResponseFromDBHandler.class);

    private String query;
    private boolean singleRecord;
    private String[] params = null;
    private String[] resultFields = null;
    private String defaultContent;

    public ResponseFromDBHandler(String query, JsonArray params, JsonArray resultFields, String defaultContent, boolean singleRecord)
    {
        super(VertxInstance.get());
        this.route().handler(BodyHandler.create());
        this.query = query;
        this.singleRecord = singleRecord;
        this.defaultContent = defaultContent;
        this.params = this.getStringArray(params);
        this.resultFields = this.getStringArray(resultFields);
        this.get("/").handler(this::processGetRequest);
        this.post("/").handler(this::processPostRequest);
    }

    private String[] getStringArray(JsonArray jsonArray)
    {
        if (jsonArray.isEmpty()) return null;
        String[] values = new String[jsonArray.size()];
        for (int i=0; i < jsonArray.size(); i++)
        {
            values[i] = jsonArray.getString(i);
        }
        return values;
    }

    private void processGetRequest(RoutingContext context)
    {
        JsonObject paramsJson = this.getRequestParams(context);
        this.fetchDataAndSend(context, paramsJson);
    }

    private void processPostRequest(RoutingContext context)
    {
        JsonObject paramsJson = context.getBodyAsJson();
        this.fetchDataAndSend(context, paramsJson);
    }

    private JsonObject getRequestParams(RoutingContext context)
    {
        JsonObject paramsJson = new JsonObject();
        if (this.params != null)
        {
            for (int i=0; i < this.params.length; i++)
            {
                paramsJson.put(this.params[i], context.request().getParam(this.params[i]));
            }
        }
        return paramsJson;
    }

    private void fetchDataAndSend(RoutingContext context, JsonObject paramsData)
    {
        Integer id = LocalCache.getInstance().store(new QueryData(this.query, paramsData));
        LOG.info("Executing query:" + this.query + " | " + paramsData);
        VertxInstance.get().eventBus().send(DatabaseService.DB_QUERY, id,
                (AsyncResult<Message<Integer>> queryResult) -> {
                    QueryData queryData = (QueryData) LocalCache.getInstance().remove(queryResult.result().body());
                    if (queryData.errorFlag)
                    {
                        sendError(context, "Error in executing query " + queryData.queryId + "for params: " + paramsData.toString() + ". Error:" + queryData.errorMessage);
                        return;
                    }

                    if (queryData.queryDef.isUpdateQuery)
                    {
                        sendJsonResponse(context, paramsData.toString());
                        return;
                    }

                    if (queryData.rows == null || queryData.rows.isEmpty())
                    {
                        sendJsonResponse(context, this.defaultContent);
                    }
                    else
                    {
                        if (this.singleRecord)
                        {
                            if (this.resultFields == null)
                            {
                                sendJsonResponse(context, queryData.rows.get(0).toString());
                            }
                            else
                            {
                                sendJsonResponse(context, queryData.getJsonDataRow(this.resultFields[0]).toString());
                            }
                        }
                        else
                        {
                            if (this.resultFields == null)
                            {
                                sendJsonResponse(context, queryData.rows.toString());
                            }
                            else
                            {
                                sendJsonResponse(context, queryData.getJsonDataRows(this.resultFields[0]).toString());
                            }
                        }
                    }
                });

    }
}
