package com.analysys.plugin.config;

import org.json.simple.JSONObject;

public class MethodTimerConfig {
    public boolean enable = true;
    public boolean all = false;
    public boolean printLog = false;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public boolean isPrintLog() {
        return printLog;
    }

    public void setPrintLog(boolean printLog) {
        this.printLog = printLog;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enable", enable);
            jsonObject.put("all", all);
            jsonObject.put("printLog", printLog);
        } catch (Throwable e) {
            //JSONException
        }
        return jsonObject;
    }
}
