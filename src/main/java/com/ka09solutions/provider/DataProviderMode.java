package com.ka09solutions.provider;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

import java.util.Map;

/**
 * Created by User on 26-03-2018.
 */
public interface DataProviderMode {

    JSONObject getResource(String urlFrag, Map<String, String> params);

    JSONArray getResourceArray(String urlFrag, Map<String, String> params);

    JSONObject postResource(String urlFrag, String json);

    JSONObject postResourceWithFormData(String url, Map<String, String> keyValuePairs);

    JSONArray postResourceWithFormData(String url, String opportunity_name);

    JSONObject postResourceWithUrl(String urlFrag, String json);

    JSONObject putResourceWithUrl(String urlFrag, String json);

    JSONArray postResourceWithUrl(String url, Map<String, String> keyValuePairs);

    JSONArray postResourceGetMultiple(String urlFrag, String jsonParams);
}
