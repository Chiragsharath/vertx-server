package com.ka09solutions.db;

import com.ka09solutions.common.LocalCache;
import com.ka09solutions.config.ConfigHolder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService extends AbstractVerticle {

    private final static Logger LOG = LogManager.getLogger(DatabaseService.class);

    public static final String DB_QUERY = "db.query";

    public static final String MULTI_DB_QUERY = "multi.db.query";

    public static final String BATCH_DB_QUERY = "batch.db.query";

    private static Map<String, JDBCClient> clients;

    private static final String DEFAULT_POOL_NAME = "test";

    @Override
    public void start(Future<Void> startFuture) {
        try {
            this.initClient(startFuture);
        } catch (Exception e) {
            LOG.error("Error in starting database service.", e);
            startFuture.fail(e);
        }
    }

    private synchronized void initClient(Future<Void> startFuture) {
        if (clients != null) {
            startFuture.complete();
            LOG.info("Pool already created. Database service started {}", DEFAULT_POOL_NAME);
            return;
        }

        clients = new HashMap<>();

        JsonArray configArray = (JsonArray) ConfigHolder.getInstance().getConfigValue("jdbc");
        configArray.forEach(config -> {
            if (config == null) {
                throw new RuntimeException("Could not find config with key 'jdbc'");
            }
            String poolName = ((JsonObject) config).getString("poolName");
            clients.put(poolName, JDBCClient.createNonShared(vertx, (JsonObject) config));
        });
        clients.get(DEFAULT_POOL_NAME).getConnection(handler -> {
            if (handler.succeeded()) {
                LOG.info("Connection to DB established for {}", DEFAULT_POOL_NAME);
                handler.result().close();
                setupQueryMessageHandler();
                setupMultiQueryMessageHandler();
                setupBatchQueryMessageHandler();
                startFuture.complete();
                LOG.info("Pool created and Database service started {}", DEFAULT_POOL_NAME);
            } else {
                throw new RuntimeException(handler.cause());
            }
        });
    }

    private void setupBatchQueryMessageHandler() {
        EventBus eb = vertx.eventBus();
        eb.localConsumer(BATCH_DB_QUERY, (Message<Integer> message) -> {

            QueryData queryData = (QueryData) LocalCache.getInstance().remove(message.body());
            QueryPrepareService.getInstance().prepareQueryData(queryData);
            if (queryData.errorFlag) {
                message.reply(LocalCache.getInstance().store(queryData));
                return;
            }
            handleQuery(message, queryData);
        }).completionHandler(res -> LOG.info("Database Multi Query Service handler registered. {}", res.succeeded()));
    }

    private void setupMultiQueryMessageHandler() {
        EventBus eb = vertx.eventBus();
        eb.localConsumer(MULTI_DB_QUERY, (Message<Integer> message) -> {

            List<QueryData> qDataList = (List<QueryData>) LocalCache.getInstance().remove(message.body());
            for (QueryData qData : qDataList) {
                QueryPrepareService.getInstance().prepareQueryData(qData);
            }
            handleQueryInGroup(message, qDataList, 0);
        }).completionHandler(res -> LOG.info("Database Multi Query Service handler registered. {}", res.succeeded()));
    }

    private void setupQueryMessageHandler() {
        EventBus eb = vertx.eventBus();
        eb.localConsumer(DB_QUERY, (Message<Integer> message) -> {

            QueryData qData = (QueryData) LocalCache.getInstance().remove(message.body());
            QueryPrepareService.getInstance().prepareQueryData(qData);
            if (qData.errorFlag) {
                message.reply(LocalCache.getInstance().store(qData));
                return;
            }
            handleQuery(message, qData);
        }).completionHandler(res -> LOG.info("Database Query Service handler registered. {}", res.succeeded()));
    }

    private void handleQuery(Message message, QueryData qData) {
        qData.startQuery();

        String poolName = qData.poolName;
        if (poolName.equals("")) {
            poolName = DEFAULT_POOL_NAME;
        }

        clients.get(poolName).getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();

                if (qData.queryDef.isUpdateQuery) {
                    runUpdateQuery(message, qData, connection);
                } else {
                    runSelectQuery(message, qData, connection);
                }
            } else {
                LOG.info("Error in query", res.cause());
                qData.setError(res.cause());
                Integer id = LocalCache.getInstance().store(qData);
                message.reply(id);
            }
        });
    }

    private void runSelectQuery(Message message, QueryData qData, SQLConnection connection) {
        System.out.println("Query: {}, params = {}" + qData.getQuery() + " : " + qData.getParams());
        connection.queryWithParams(qData.getQuery(), qData.getParams(), res2 -> {
            if (res2.succeeded()) {
                List<JsonObject> rows = res2.result().getRows();
                System.out.println("Result rows : {}" + rows.size());
                qData.setResult(rows);
            } else {
                LOG.info("Error in executing query : ", qData.getQuery() + " : reason : " + res2.cause());
                qData.setError(res2.cause());
            }
            connection.close();
            message.reply(LocalCache.getInstance().store(qData));
        });
    }

    private void runUpdateQuery(Message message, QueryData qData, SQLConnection connection) {
        if (qData.isBatchMode()) {
            List<JsonArray> paramsForBatch = qData.getParamsForBatch();
            LOG.info("Query in batch mode : {}, params = {}", qData.getQuery(), paramsForBatch.size());
            connection.setAutoCommit(false, autoCommitResponse -> {
                if (autoCommitResponse.failed()) {
                    qData.setError(autoCommitResponse.cause());
                    LOG.info("Error in setting auto commit: {}", autoCommitResponse.cause());
                    message.reply(LocalCache.getInstance().store(qData));
                    return;
                }
                connection.batchWithParams(qData.getQuery(), paramsForBatch, res2 -> {
                    if (res2.failed()) {
                        qData.setError(res2.cause());
                        LOG.info("Error in batch execution:", res2.cause());
                    }
                    connection.commit(commitResult -> {
                        if (commitResult.failed()) {
                            qData.setError(res2.cause());
                            LOG.info("Error in commit: ", res2.cause());
                        }
                        connection.setAutoCommit(true, null);
                        connection.close();
                        message.reply(LocalCache.getInstance().store(qData));
                    });
                });
            });
        } else {
            LOG.info("Query: {}, Params = {}", qData.getQuery(), qData.getParams());
            connection.updateWithParams(qData.getQuery(), qData.getParams(), res2 -> {
                if (res2.succeeded()) {
                    qData.setResult(res2.result());
                } else {
                    qData.setError(res2.cause());
                    LOG.error("Error: {}", res2.cause());
                }
                connection.close();
                message.reply(LocalCache.getInstance().store(qData));
            });
        }
    }

    private void handleQueryInGroup(Message message, List<QueryData> qDataList, int index) {
        if (index >= qDataList.size()) {
            LOG.debug("Done with queries : {}, {}", index, qDataList.size());
            message.reply(LocalCache.getInstance().store(qDataList));
            return;
        }

        QueryData qData = qDataList.get(index);
        String poolName = qData.poolName;
        if (poolName.equals("") || poolName.length() == 0) {
            poolName = DEFAULT_POOL_NAME;
            qData.poolName = poolName;
        }

        if (qData.errorFlag) {
            LOG.info("Skipping query : {}, {}", qData.queryId, qData.errorMessage);

            handleQueryInGroup(message, qDataList, index + 1);
        }

        qData.startQuery();
        clients.get(poolName).getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();

                if (qData.queryDef.isUpdateQuery) {
                    runUpdateQueryInGroup(message, qData, connection, qDataList, index);
                } else {
                    runSelectQueryInGroup(message, qData, connection, qDataList, index);
                }
            } else {
                LOG.info("Error in query - {}", res.cause());
                qData.setError(res.cause());
                handleQueryInGroup(message, qDataList, index + 1);
            }
        });
    }

    private void runSelectQueryInGroup(Message message, QueryData qData, SQLConnection connection, List<QueryData> qDataList, int index) {
        connection.queryWithParams(qData.getQuery(), qData.getParams(), res2 -> {
            if (res2.succeeded()) {
                LOG.info("Query = {}, Params = {}", qData.getQuery(), qData.getParams());
                qData.setResult(res2.result().getRows());
            } else {
                qData.setError(res2.cause());
                LOG.info("Error:", res2.cause());

            }
            connection.close();
            handleQueryInGroup(message, qDataList, index + 1);
        });
    }

    private void runUpdateQueryInGroup(Message message, QueryData qData, SQLConnection connection, List<QueryData> qDataList, int index) {
        if (qData.isBatchMode()) {
            List<JsonArray> paramsForBatch = qData.getParamsForBatch();
            LOG.info("Query in batch mode: {}, Params = {}", qData.getQuery(), paramsForBatch.size());
            connection.setAutoCommit(false, autoCommitResponse -> {
                if (autoCommitResponse.failed()) {
                    qData.setError(autoCommitResponse.cause());
                    LOG.info("Error in setting auto commit: {}", autoCommitResponse.cause());
                    connection.setAutoCommit(true, null);
                    connection.close();
                    handleQueryInGroup(message, qDataList, index + 1);
                    return;
                }
                connection.batchWithParams(qData.getQuery(), paramsForBatch, res2 -> {
                    if (res2.failed()) {
                        qData.setError(res2.cause());
                        LOG.info("Error in batch execution: {}", res2.cause());
                        connection.setAutoCommit(true, null);
                        connection.close();
                        handleQueryInGroup(message, qDataList, index + 1);
                        return;
                    }
                    connection.commit(commitResult -> {
                        if (commitResult.failed()) {
                            qData.setError(res2.cause());
                            LOG.info("Error in commit: {}", res2.cause());
                        }
                        connection.setAutoCommit(true, null);
                        connection.close();
                        handleQueryInGroup(message, qDataList, index + 1);
                    });
                });
            });
        } else {
            LOG.info("Query: {}, Params = {}", qData.getQuery(), qData.getParams());
            connection.updateWithParams(qData.getQuery(), qData.getParams(), res2 -> {
                if (res2.succeeded()) {
                    qData.setResult(res2.result());
                } else {
                    qData.setError(res2.cause());
                    LOG.info("Error: {}", res2.cause());
                }
                connection.close();
                handleQueryInGroup(message, qDataList, index + 1);
            });
        }
    }
}