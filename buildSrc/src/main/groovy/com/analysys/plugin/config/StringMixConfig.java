package com.analysys.plugin.config;

import org.json.simple.JSONObject;

public class StringMixConfig {
    public boolean enable;
    public String key;
    public String impl;

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JSONObject toJson () {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enable", enable);
            jsonObject.put("key", key);
            jsonObject.put("impl", impl);
        } catch (Throwable e) {
            //JSONException
        }
        return jsonObject;
    }
}
