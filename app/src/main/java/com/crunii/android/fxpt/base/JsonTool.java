package com.crunii.android.fxpt.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 王春晖 on 2015/12/1.
 */
public class JsonTool {

    public static Map<String, Object> jsonStrToMap(String json) throws JSONException {
        JSONObject jsonResult = new JSONObject(json);
        Map<String, Object> result = jsonOjbToMap(jsonResult);
        return result;
    }

    public static List<Object> jsonArrToList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object val = jsonArray.get(i);
            if (!(val instanceof JSONObject) && !(val instanceof JSONArray)) {
                list.add(val);
                continue;
            }
            if (val instanceof JSONObject) {
                Map<String, Object> map = jsonOjbToMap((JSONObject) val);
                list.add(map);
                continue;
            }
            if (val instanceof JSONArray) {
                list.add(jsonArrToList((JSONArray) val));
                continue;
            }

        }
        return list;
    }

    public static Map<String, Object> jsonOjbToMap(JSONObject jsonResult) throws JSONException {
        Map<String, Object> result = new HashMap<String, Object>();
        Iterator<String> keyIt = jsonResult.keys();
        while (keyIt.hasNext()) {
            String key = keyIt.next();
            Object val = jsonResult.get(key);

            if ("null".equals(val)) {
                result.put(key, null);
                continue;
            }

            if (!(val instanceof JSONObject) && !(val instanceof JSONArray)) {
                result.put(key, val);
                continue;
            }
            if (val instanceof JSONObject) {
                Map<String, Object> valMap = jsonOjbToMap((JSONObject) val);
                result.put(key, valMap);
                continue;
            }
            if (val instanceof JSONArray) {
                JSONArray ja = (JSONArray) val;
                result.put(key, jsonArrToList(ja));
            }
        }
        return result;
    }
}

