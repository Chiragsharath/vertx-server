package com.ka09solutions.db;

import com.ka09solutions.common.StringUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryData {
    public String queryId;

    public QueryDef queryDef;

    public JsonObject paramsObject;

    public List<JsonObject> paramsList;

    public List<JsonObject> rows;

    public UpdateResult updateResult;

    public long responseTimeInMillis;

    public boolean errorFlag;

    public Throwable error;

    public String errorMessage;

    private long startTime;

    public String poolName = "";

    public QueryData(String queryId) {
        this.queryId = queryId;
    }

    public QueryData(String queryId, JsonObject paramsObject) {
        this.queryId = queryId;
        this.paramsObject = paramsObject;
    }

    public QueryData(String queryId, JsonObject paramsObject, String poolName) {
        this.queryId = queryId;
        this.paramsObject = paramsObject;
        this.poolName = poolName;

    }

    public QueryData(String queryId, List<JsonObject> paramsList) {
        this.queryId = queryId;
        this.paramsList = paramsList;
    }

    public QueryData(String queryId, List<JsonObject> paramsList, String poolName) {
        this.queryId = queryId;
        this.paramsList = paramsList;
        this.poolName = poolName;
    }

    public boolean isBatchMode() {
        return this.paramsList != null && !paramsList.isEmpty();
    }

    public List<JsonArray> getParamsForBatch() {
        List<JsonArray> paramsForBatch = new ArrayList<>(this.paramsList.size());
        for (JsonObject jsonObject : this.paramsList) {
            paramsForBatch.add(this.prepareParams(jsonObject));
        }
        return paramsForBatch;
    }

    public JsonArray getParams() {
        if (this.paramsObject == null || this.queryDef == null || this.queryDef.paramList == null) {
            return null;
        }
        return prepareParams(this.paramsObject);
    }

    private JsonArray prepareParams(JsonObject paramsObject) {
        JsonArray params = new JsonArray();
        for (String paramKey : this.queryDef.paramList) {
            Object value = paramsObject.getValue(paramKey);
            if (value == null) {
                params.add("");
            } else if (value instanceof JsonObject) {
                params.add(value.toString());
            } else if (value instanceof JsonArray) {
                params.add(value.toString());
            } else {
                params.add(value);
            }
        }
        return params;
    }

    public QueryData setError(String error) {
        this.errorFlag = true;
        this.errorMessage = error;
        this.endQuery();
        return this;
    }

    public QueryData setError(Throwable cause) {
        this.error = cause;
        return this.setError(cause.getMessage());
    }

    public QueryData startQuery() {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public QueryData endQuery() {
        this.responseTimeInMillis = System.currentTimeMillis() - this.startTime;
        this.startTime = 0;
        return this;
    }

    public JsonArray getJsonDataRows(String jsonField) {
        JsonArray jsonRows = new JsonArray();
        for (JsonObject record : this.rows) {
            jsonRows.add(new JsonObject(record.getString(jsonField)));
        }
        return jsonRows;
    }

    public JsonObject getJsonDataRow(String jsonField) {
        return new JsonObject(this.rows.get(0).getString(jsonField));
    }

    public QueryData setResult(List<JsonObject> rows) {
        this.rows = rows;
        if (this.queryDef.jsonFields != null) {
            for (JsonObject row : this.rows) {
                for (String jsonfield : this.queryDef.jsonFields) {
                    String data = row.getString(jsonfield);
                    if (StringUtils.isNonEmpty(data)) {
                        if (data.charAt(0) == '[') {
                            row.put(jsonfield, new JsonArray(data));
                        } else {
                            row.put(jsonfield, new JsonObject(data));
                        }

                    }
                }
            }
        }
        return this.endQuery();
    }

    public QueryData setResult(UpdateResult updateResult) {
        this.updateResult = updateResult;
        if (this.queryDef.isInsertQuery && this.updateResult.getKeys() != null && !this.updateResult.getKeys().isEmpty()) {
            this.paramsObject.put("id", this.updateResult.getKeys().getValue(0));
        }
        return this.endQuery();
    }

    public String getQuery() {
        if (queryDef.hasSubstitutions()) {
            Map<String, Object> params = paramsObject.getMap();
            queryDef.getDefaultMap().entrySet().stream().forEach(entry -> params.putIfAbsent(entry.getKey(), entry.getValue()));
            StrSubstitutor substitutor = new StrSubstitutor(params);
            return substitutor.replace(queryDef.query);
        } else {
            return queryDef.query;
        }
    }
}