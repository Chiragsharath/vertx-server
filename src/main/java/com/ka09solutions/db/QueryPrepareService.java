package com.ka09solutions.db;

import com.ka09solutions.common.MultiFileReader;
import com.ka09solutions.config.ConfigHolder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryPrepareService extends AbstractVerticle implements MultiFileReader.ResultHandler
{
	private final static Logger LOG = LogManager.getLogger(QueryPrepareService.class);
	private Map<String, QueryDef> queryMap;
	
	private static QueryPrepareService INSTANCE;
	
	public static QueryPrepareService getInstance()
	{
		return INSTANCE;
	}

	private Future<Void> startFuture;

	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		this.startFuture = startFuture;
		this.setupQueryMap();
		INSTANCE = this;
	}

	private void setupQueryMap()
	{
		JsonArray queryFiles = (JsonArray) ConfigHolder.getInstance().getConfigValue("queryfiles");
        if (queryFiles == null || queryFiles.isEmpty())
        {
			LOG.warn("No SQL queries found to setup.");
			this.startFuture.complete();
			return;
        }
		new MultiFileReader(queryFiles.getList(), this).read();
	}
	
	public QueryData prepareQueryData(QueryData qData)
	{
		if (qData.queryDef == null)
		{
			if (!this.queryMap.containsKey(qData.queryId))
			{
				qData.setError("Query not configured:" + qData.queryId);
				return qData;
			}

            qData.queryDef = this.queryMap.get(qData.queryId);
		}
		
		return qData;
	}
	
	@Override
	public void onSuccess(List<String> files, List<String> filesData)
	{
		this.queryMap = new HashMap<>();
		for (String fileData : filesData)
		{
			JsonObject json = new JsonObject(fileData);
			for (Object queryObject : json.getJsonArray("queries"))
			{
				QueryDef queryDef = new QueryDef((JsonObject) queryObject);
				this.queryMap.put(queryDef.queryId, queryDef);
			}
		}
		LOG.info("Query map created.");
		this.startFuture.complete();
	}

	@Override
	public void onError(String filename, Throwable cause)
	{
		LOG.error("Could not create query map due to an error with file: " + filename, cause);
		this.startFuture.fail(cause);
	}
}
