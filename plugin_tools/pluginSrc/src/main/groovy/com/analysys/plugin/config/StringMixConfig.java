package com.analysys.plugin.config;

import org.json.simple.JSONObject;

public class StringMixConfig {
    public boolean enable;
    public String key;
    public String impl;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JSONObject toJson() {
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
