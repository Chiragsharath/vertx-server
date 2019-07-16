package com.ka09solutions.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.FormData;
import us.monoid.web.Resty;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static us.monoid.web.Resty.*;

/**
 * Created by User on 26-03-2018.
 */
public class RestDataProvider implements DataProviderMode {

    private static final Logger LOG = LogManager.getLogger(RestDataProvider.class);
    private final Resty resty = new Resty();

    @Override
    public JSONObject getResource(String urlFrag, Map<String, String> params) {
        try {
            System.out.println(urlFrag);
            return resty.json(urlFrag + "?" + queryParams(params)).object();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    @Override
    public JSONArray getResourceArray(String urlFrag, Map<String, String> params) {
        try {
            String string =  "/" + urlFrag + "?" + queryParams(params);
            LOG.debug("Hitting crm :" + string);
            return resty.json(string).array();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    @Override
    public JSONObject postResource(String urlFrag, String json) {

        try {
            return resty.json(getBaseURL() + "/" + urlFrag,
                    content(json))
                    .object();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    @Override
    public JSONObject postResourceWithFormData(String url, Map<String, String> keyValuePairs) {
        try {

                FormData[] values = new FormData[keyValuePairs.size()];
                int index = 0;
                for (String key : keyValuePairs.keySet()) {
                    values[index] = data(key, keyValuePairs.get(key));
                    index++;
                }
            String anUri =  "/" + url;


            return resty.json(anUri, form(values)).object();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + url, e);
        }

    }

    @Override
    public JSONArray postResourceWithFormData(String url, String parent_id) {

        try {
            return new Resty().json( "/" + url, form(data("opportunity_name", parent_id))).array();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  null;
    }

    @Override
    public JSONObject postResourceWithUrl(String url, String json) {
        try {
            return resty.json(url,
                    content(json))
                    .object();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + url, e);
        }
    }

    @Override
    public JSONObject putResourceWithUrl(String url, String json) {
        try {
            return resty.json(url, put(content(json))).object();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + url, e);
        }
    }

    @Override
    public JSONArray postResourceWithUrl(String url, Map<String, String> keyValuePairs) {

        try {
            FormData[] values = new FormData[keyValuePairs.size()];
            int index = 0;
            for (String key : keyValuePairs.keySet()) {
                values[index] = data(key, keyValuePairs.get(key));
                index++;
            }
            String anUri =   "/" + url;
            return resty.json(anUri, form(values)).array();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public JSONArray postResourceGetMultiple(String urlFrag, String jsonParams) {

        try {
            return resty.json(getBaseURL() + "/" + urlFrag,
                    content(jsonParams)).array();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }


    private String getBaseURL() {
        return "";
    }


    private String queryParams(Map<String, String> params) {
        return params.entrySet().stream().map(entry -> (entry.getKey() + "=" + entry.getValue())).collect(Collectors.joining("&"));
    }


}
