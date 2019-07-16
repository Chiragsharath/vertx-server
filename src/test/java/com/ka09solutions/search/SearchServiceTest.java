package com.ka09solutions.search;

import io.vertx.core.json.JsonObject;
import junit.framework.TestCase;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by test on 03-07-2018.
 */
public class SearchServiceTest extends TestCase
{
    public void testSearchQuery() throws Exception
    {
        String esConfig = new String(Files.readAllBytes(Paths.get(getClass().getResource("/apiserver/config/es.json").toURI())));
        JsonObject esConfigJson = new JsonObject(esConfig);
        String clusterName = esConfigJson.getJsonObject("elasticsearch").getString("cluster_name");
        System.out.println("cluster:" + clusterName);
        Node node = new NodeBuilder().clusterName(clusterName)
                .settings(ImmutableSettings.settingsBuilder().put("http.enabled", false))
                .client(true).node();
        Client client = node.client();
        SearchResponse response = client.prepareSearch("fill the index").setTypes("query type").setSource("query to search").execute().actionGet();
        // Check the response
    }
}

