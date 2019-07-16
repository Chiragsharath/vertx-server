package com.ka09solutions.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.Map;

public class QueryDef {
    public static final String SELECT_QUERY_TYPE = "select";

    public static final String INSERT_QUERY_TYPE = "insert";

    public static final String UPDATE_QUERY_TYPE = "update";

    public static final String DELETE_QUERY_TYPE = "delete";

    public String queryId;

    public String query;

    public String type;

    public boolean isUpdateQuery;

    public boolean isInsertQuery;

    public String tableForInsert;

    public String[] paramList;

    public String[] jsonFields;

    private boolean idFieldExists = false;
    private boolean substitutions = false;

    private Map<String, Object> defaultMap = Collections.emptyMap();

    public QueryDef(JsonObject queryJson) {
        this(queryJson.getString("query"), queryJson.getString("type"), queryJson.getString("table"));
        this.queryId = queryJson.getString("id");
        JsonArray paramsArray = queryJson.getJsonArray("params");
        if (paramsArray != null && !paramsArray.isEmpty()) {
            this.paramList = new String[paramsArray.size()];
            for (int i = 0; i < paramsArray.size(); i++) {
                this.paramList[i] = paramsArray.getString(i);
                if ("id".equals(this.paramList[i])) {
                    this.idFieldExists = true;
                }
            }
        }

        if (queryJson.containsKey("jsonfields")) {
            JsonArray jsonfieldsL = queryJson.getJsonArray("jsonfields");
            if (jsonfieldsL != null && !jsonfieldsL.isEmpty()) {
                this.jsonFields = new String[jsonfieldsL.size()];
                for (int i = 0; i < jsonfieldsL.size(); i++) {
                    this.jsonFields[i] = jsonfieldsL.getString(i);
                }
            }
        }

        this.substitutions = this.query.contains("${");

        if (queryJson.containsKey("defaults")) {
            this.defaultMap = queryJson.getJsonObject("defaults").getMap();
        }
    }

    public QueryDef(String query, String type, String tableForInsert) {
        this.query = query;
        this.type = type;
        this.isUpdateQuery = !this.type.equals(SELECT_QUERY_TYPE);
        this.isInsertQuery = this.type.equals(INSERT_QUERY_TYPE);
        this.tableForInsert = tableForInsert;
    }

    public boolean hasIdField() {
        return this.idFieldExists;
    }

    public boolean hasSubstitutions() {
        return substitutions;
    }

    public Map<String, Object> getDefaultMap() {
        return defaultMap;
    }
}
