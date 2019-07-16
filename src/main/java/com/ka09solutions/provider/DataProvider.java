package com.ka09solutions.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import us.monoid.json.JSONObject;

import java.util.HashMap;

public class DataProvider {

    private DataProviderMode dataProviderMode;

    private final ObjectMapper mapper;

    public DataProvider() {
        dataProviderMode = new RestDataProvider();
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    }

    public JSONObject postVideoToVideoCipher(String videoCipherUrl) {

        try {
            return dataProviderMode.postResourceWithFormData("get_opportunity_details.php", new HashMap<String, String>(){
                {
                    put("url",videoCipherUrl);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
