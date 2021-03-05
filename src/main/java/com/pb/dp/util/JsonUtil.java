package com.pb.dp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    public static Map<String, Object> getMapFromJsonString(String jsonString) {
        Map<String, Object> retMap = new Gson().fromJson(
                jsonString, new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );
        return retMap;
    }

    public static String getJsonStringFromObject(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return json;
    }

    public static String getJsonWithNullsFromPojo(Object obj) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.setPrettyPrinting().create();

        String json = gson.toJson(obj);
        return json;
    }
}